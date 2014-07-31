package com.mzp.chat.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.widget.TextView;
import com.mzp.chat.adapter.UserExpandListAdapter;
import com.mzp.chat.bean.ChatMessage;
import com.mzp.chat.bean.UserInfo;
import com.mzp.chat.global.ChatApplication;
import com.mzp.chat.global.IpMessageConst;
import com.mzp.chat.util.ToolUtils;
import com.mzp.chat.widget.PullToRefreshExpandableListView;
import com.mzp.chat.widget.PullToRefreshExpandableListView.OnRefreshListener;

/**
 * 局域网好友列表activity
 *	2014-7-23
 */
public class MainActivity extends BaseActivity{
	private PullToRefreshExpandableListView userList;
	private UserExpandListAdapter userAdapter;
	private List<String> strGroups; //所有一级菜单名称集合
	private List<List<UserInfo>> children;
	private TextView ipTextView;
	private String hostIp;
	private ChatApplication chatApp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if(!ToolUtils.isWifiActive(this)){
			ToolUtils.commonToast(this, "没有wifi连接，请检查wifi设置后再启动本程序。");
		}
		findViews();
        netThreadHelper.connectSocket();	//开始监听数据
        netThreadHelper.noticeOnline();	//广播上线
        refreshViews();
	}

	/**
	 *初始化组件
	 */
	private void findViews() {
		chatApp = ChatApplication.getInstance();
		userList = (PullToRefreshExpandableListView) findViewById(R.id.userlist);
		ipTextView = (TextView) findViewById(R.id.mymood);
		hostIp = chatApp.getLocalIp();
		ipTextView.setText(hostIp);	
		strGroups = new ArrayList<String>(); 
		children = new ArrayList<List<UserInfo>>();
		userAdapter = new UserExpandListAdapter(this, strGroups, children);
		userList.setAdapter(userAdapter);
		userList.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				new AsyncTask<Void, Void, Void>() {
					protected Void doInBackground(Void... params) {
						try {
							Thread.sleep(300);
						} catch (Exception e) {
							e.printStackTrace();
						}
						return null;
					}
					@Override
					protected void onPostExecute(Void result) {
						userList.onRefreshComplete();
					}

				}.execute();
				netThreadHelper.refreshUsers();
				refreshViews();
			}
		});
	}
	

	@Override
	public void finish() {
		super.finish();
		netThreadHelper.noticeOffline();	//通知下线
		netThreadHelper.disconnectSocket(); //停止监听
		
	}
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void processMessage(Message msg) {
		switch(msg.what){
		case IpMessageConst.IPMSG_BR_ENTRY:
		case IpMessageConst.IPMSG_BR_EXIT:
		case IpMessageConst.IPMSG_ANSENTRY:
		case IpMessageConst.IPMSG_SENDMSG:
			refreshViews();	
			break;
		}
	}

	//更新数据和UI显示
		private void refreshViews(){	
			//清空数据
			strGroups.clear();
			children.clear();
			
			Map<String,UserInfo> currentUsers = new HashMap<String, UserInfo>();
			currentUsers.putAll(netThreadHelper.getUsers());
			Queue<ChatMessage> msgQueue = netThreadHelper.getReceiveMsgQueue();
			Map<String, Integer> ip2Msg = new HashMap<String, Integer>();	//IP地址与未收消息个数的map
			//遍历消息队列，填充ip2Msg
			Iterator<ChatMessage> it = msgQueue.iterator();
			while(it.hasNext()){
				ChatMessage chatMsg = it.next();
				String ip = chatMsg.getSenderIp();	//得到消息发送者IP
				Integer tempInt = ip2Msg.get(ip);
				if(tempInt == null){	///若map中没有IP对应的消息个数,则把IP添加进去,值为1
					ip2Msg.put(ip, 1);
				}else{	//若已经有对应ip，则将其值加一
					ip2Msg.put(ip, ip2Msg.get(ip)+1);
				}
			}
			
			//遍历currentUsers,更新strGroups和children
			Iterator<String> iterator = currentUsers.keySet().iterator();
			while (iterator.hasNext()) {
				UserInfo user = currentUsers.get(iterator.next());	
				//设置每个在线用户对应的未收消息个数
				if(ip2Msg.get(user.getIp()) == null){
					user.setMsgCount(0);
				}else{
					user.setMsgCount(ip2Msg.get(user.getIp()));
				}
				
				String groupName = user.getGroupName();
				int index = strGroups.indexOf(groupName);
				if(index == -1){ //没有相应分组，则添加分组，并添加对应child
					strGroups.add(groupName);
					
					List<UserInfo> childData = new ArrayList<UserInfo>();
					childData.add(user);
					children.add(childData);
				}else{	//已存在分组，则将对应child添加到相对应分组中
					children.get(index).add(user);
				}
			}
			userAdapter.notifyDataSetChanged();	//更新ExpandableListView
			//String countStr = "当前在线" + currentUsers.size() +"个用户";
	        //totalUser.setText(countStr);	//更新TextView
			
		}

}
