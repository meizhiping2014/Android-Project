package com.mzp.chat.global;



import android.app.Application;

/**
 * 全局类
 *	2014-7-17
 */
public class ChatApplication extends Application{
	//private  final static String TAG = "ChatApplication";
	private String localIpAddress;
	private static ChatApplication instance = null;
	
	/**
	 * 静态工厂方法
	 * @return
	 */
	public static synchronized ChatApplication getInstance(){
		if(null == instance){
			instance = new ChatApplication();
		}
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	public String getLocalIp() {
		return localIpAddress;
	} 

	public void setLocalIp(String localIpAddress) {
		this.localIpAddress = localIpAddress;
	}
	

	
}
