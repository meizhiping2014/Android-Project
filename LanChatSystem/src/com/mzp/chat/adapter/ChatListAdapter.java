package com.mzp.chat.adapter;

import java.util.List;

import com.mzp.chat.activity.R;
import com.mzp.chat.bean.ChatMessage;
import com.mzp.chat.util.ToolUtils;

import android.content.Context;
import android.content.res.Resources;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 *聊天窗口适配器
 * 
 * 
 */
public class ChatListAdapter extends BaseAdapter {
	protected LayoutInflater mInflater;
	protected List<ChatMessage> msgList;
	protected Resources res;
	private static final String zhengze = "f0[0-9]{2}|f10[0-7]";	//正则表达式，用来判断消息内是否有表情
	private Context context;

	public ChatListAdapter(Context c, List<ChatMessage> list) {
		super();
		this.mInflater = LayoutInflater.from(c);
		this.msgList = list;
		context = c;
		res = c.getResources();
	}
	
	  
	
	
	//ListView视图的内容由IMsgViewType决定  
    public static interface IMsgViewType  
    {  
        //对方发来的信息  
        int IMVT_COM_MSG = 0;  
        //自己发出的信息  
        int IMVT_TO_MSG = 1;  
    }
   
    
  //获取项的类型
    @Override
    public int getItemViewType(int position) {
    	ChatMessage entity = msgList.get(position);  
        
        if (entity.isSelfMsg())  
        {  
            return IMsgViewType.IMVT_COM_MSG;  
        }else{  
            return IMsgViewType.IMVT_TO_MSG;  
        }  
    }
    
    @Override
    public int getViewTypeCount() {
    	return 2;
    }
    
	@Override
	public int getCount() {
		return msgList.size();
	}

	@Override
	public Object getItem(int position) {
		return msgList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		ChatMessage msg = msgList.get(position);
		boolean isSelfMsg = msg.isSelfMsg();
		if (convertView == null) {
			viewHolder = new ViewHolder();
			if (isSelfMsg) {//如果是自己发出的消息，则显示的是左气泡 
				convertView = mInflater.inflate(R.layout.chat_item_msg_left,null);
			} else {//如果是对方发来的消息，则显示的是右气泡  
				convertView = mInflater.inflate(R.layout.chat_item_msg_right,null);
			}
			viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
			//viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_username);
			viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
			//viewHolder.isComMsg = isSelfMsg;
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tvSendTime.setText(msg.getTimeStr());
		//viewHolder.tvUserName.setText(msg.getSenderName());
		
		SpannableString spannableString = ToolUtils.getExpressionString(context, msg.getMsg(), zhengze);
		viewHolder.tvContent.setText(spannableString);

		return convertView;
	}

	/**
	 * ViewHolder类
	 * 
	 */
	private class ViewHolder {
		public TextView tvSendTime;
		//public TextView tvUserName;
		public TextView tvContent;
		//public boolean isComMsg = false;
	}

}
