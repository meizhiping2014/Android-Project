package com.mzp.chat.activity;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.mzp.chat.global.ChatApplication;
import com.mzp.chat.global.IpMessageConst;
import com.mzp.chat.global.UsedConst;
import com.mzp.chat.handle.ChatThreadHandle;
import com.mzp.chat.handle.NetTcpFileReceiveThread;
import com.mzp.chat.util.IpMessageProtocol;

public abstract class BaseActivity extends Activity{
	protected static ChatThreadHandle netThreadHelper;
	protected static LinkedList<BaseActivity> queue = new LinkedList<BaseActivity>();
	private static SoundPool soundPool;
	private NotificationManager mNotManager;
	private Notification mNotification;
	private static int notification_id = 9786970;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		netThreadHelper = ChatThreadHandle.newInstance();
		if(!queue.contains(this))
			queue.add(this);
		if(soundPool == null){
			//创建声音播放
			soundPool= new SoundPool(5,AudioManager.STREAM_RING,5);//第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
			soundPool.load(this,R.raw.msg,1);//把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
		}
		//建立notification
		mNotManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNotification = new Notification(android.R.drawable.stat_sys_download, "飞鸽接收文件", System.currentTimeMillis());
		mNotification.contentView = new RemoteViews(getPackageName(), R.layout.file_download_notification);
		mNotification.contentView.setProgressBar(R.id.pd_download, 100, 0, false);
		Intent notificationIntent = new Intent(this,BaseActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		mNotification.contentIntent = contentIntent;
		ChatApplication.getInstance().setLocalIp(getLocalIpAddress());
	}
	
	/**
	 * 获取本机机IP地址
	 */
	private String getLocalIpAddress(){
		WifiManager wifimanger = (WifiManager) getSystemService(WIFI_SERVICE);
		WifiInfo wifiinfo = wifimanger.getConnectionInfo();
		String ip = intToIp(wifiinfo.getIpAddress());  //注：getIpAddress获取的为int型需要用intToIp方法
		return ip;
	}
	
	private String intToIp(int i)  {
		 return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
		} 
	
	public abstract void processMessage(Message msg);
	
	@Override
	public void finish() {
		super.finish();
		queue.removeLast();
	}

	public static void sendMessage(int cmd, String text) {
		Message msg = new Message();
		msg.obj = text;
		msg.what = cmd;
		sendMessage(msg);
	}

	public static void sendMessage(Message msg) {
		handler.sendMessage(msg);
	}

	public static void sendEmptyMessage(int what) {
		handler.sendEmptyMessage(what);
	}

	private static Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case IpMessageConst.IPMSG_SENDMSG | IpMessageConst.IPMSG_FILEATTACHOPT:{
				//收到发送文件请求
				
				final String[] extraMsg = (String[]) msg.obj;	//得到附加文件信息,字符串数组，分别放了  IP，附加文件信息,发送者名称，包ID
				Log.d("receive file....", "receive file from :" + extraMsg[2] + "(" + extraMsg[0] +")");
				Log.d("receive file....", "receive file info:" + extraMsg[1]);
				byte[] bt = {0x07};		//用于分隔多个发送文件的字符
				String splitStr = new String(bt);
				final String[] fileInfos = extraMsg[1].split(splitStr);	//使用分隔字符进行分割
				
				Log.d("feige", "收到文件传输请求,共有" + fileInfos.length + "个文件");
				
				String infoStr = "发送者IP:\t" + extraMsg[0] + "\n" + 
								 "发送者名称:\t" + extraMsg[2] + "\n" +
								 "文件总数:\t" + fileInfos.length +"个";
				
				new AlertDialog.Builder(queue.getLast())
					.setIcon(android.R.drawable.ic_dialog_info)
					.setTitle("收到文件传输请求")
					.setMessage(infoStr)
					.setPositiveButton("接收", 
							new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Thread fileReceiveThread = new Thread(new NetTcpFileReceiveThread(extraMsg[3], extraMsg[0],fileInfos));	//新建一个接受文件线程
									fileReceiveThread.start();	//启动线程
									
									Toast.makeText(queue.getLast(), "开始接收文件", Toast.LENGTH_SHORT).show();
									
									queue.getLast().showNotification();	//显示notification
								}
							})
					 .setNegativeButton("取消", 
							 new DialogInterface.OnClickListener() {
						 		public void onClick(DialogInterface dialog, int which) {
						 			//发送拒绝报文
						 			//构造拒绝报文
									IpMessageProtocol ipmsgSend = new IpMessageProtocol();
									ipmsgSend.setVersion("" +IpMessageConst.VERSION);	//拒绝命令字
									ipmsgSend.setCommandNo(IpMessageConst.IPMSG_RELEASEFILES);
									ipmsgSend.setSenderName("android飞鸽");
									ipmsgSend.setSenderHost("android");
									ipmsgSend.setAdditionalSection(extraMsg[3] + "\0");	//附加信息里是确认收到的包的编号
						 			
									InetAddress sendAddress = null;
									try {
										sendAddress = InetAddress.getByName(extraMsg[0]);
									} catch (UnknownHostException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
									netThreadHelper.sendUdpData(ipmsgSend.getProtocolString(), sendAddress, IpMessageConst.PORT);
									
						 		}
					 }).show();
					 
			}
				break;
				
			case UsedConst.FILERECEIVEINFO:{	//更新接收文件进度条
				int[] sendedPer = (int[]) msg.obj;	//得到信息
				queue.getLast().mNotification.contentView.setProgressBar(R.id.pd_download, 100, sendedPer[1], false);
				queue.getLast().mNotification.contentView.setTextViewText(R.id.fileRec_info, "文件"+ (sendedPer[0] + 1) +"接收中:" + sendedPer[1] + "%");
				queue.getLast().showNotification();	//显示notification
			}
				break;
				
			case UsedConst.FILERECEIVESUCCESS:{	//文件接收成功
				
				int[] successNum = (int[]) msg.obj;
				
				queue.getLast().mNotification.contentView.setTextViewText(R.id.fileRec_info, "第"+ successNum[0] +"个文件接收成功");
				queue.getLast().makeTextShort("第"+ successNum[0] +"个文件接收成功");
				if(successNum[0] == successNum[1]){
					queue.getLast().mNotification.contentView.setTextViewText(R.id.fileRec_info, "所有文件接收成功");
//					queue.getLast().mNotManager.cancel(notification_id);
					
					queue.getLast().makeTextShort("所有文件接收成功");
				}
				queue.getLast().showNotification();
			}
				break;
				
			default:
				if( 0 != queue.size()){
					queue.getLast().processMessage(msg);
				}
				break;
			}
		}

	};
	protected void showNotification(){
		mNotManager.notify(notification_id, mNotification);
	}
	
	/**
	 * 播放声音
	 */
	public static void playMsg(){
		soundPool.play(1,1, 1, 0, 0, 1);
	}
	
	public void makeTextShort(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}
