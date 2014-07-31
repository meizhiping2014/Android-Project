package com.mzp.chat.impl;

import com.mzp.chat.bean.ChatMessage;


/**
 *接收消息监听的listener接口
 *
 */
public interface ReceiveMsgListener {
	public boolean receive(ChatMessage msg);

}
