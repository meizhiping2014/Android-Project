package com.mzp.chat.bean;

public class UserInfo {
	private String userName;	// 用户名
	private String alias;		//别名（若为pc，则是登录名）
	private String groupName;	//组名
	private String ip;			//ip地址
	private String hostName;	//主机名
	private String mac;			//MAC地址
	private int msgCount;		//未接收消息数
	
	public UserInfo(){
		msgCount = 0;	//初始化为零
	}
	
	@Override
	public String toString() {
			return "UserInfo [userName=" + userName + ", alias=" + alias
					 + ", groupName=" + groupName
					 + ", ip=" + ip
					 + ", hostName=" + hostName
					 + ", mac=" + mac
					 + ", msgCount=" + msgCount
					+"]";
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public int getMsgCount() {
		return msgCount;
	}
	public void setMsgCount(int msgCount) {
		this.msgCount = msgCount;
	}
	
	
}
