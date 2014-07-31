package com.mzp.chat.adapter;

import java.util.ArrayList;
import java.util.List;
import com.mzp.chat.activity.ChatActivity;
import com.mzp.chat.activity.R;
import com.mzp.chat.bean.UserInfo;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 *好友列表适配器
 *	2014-7-17
 */
public class UserExpandListAdapter extends BaseExpandableListAdapter{
	private List<String> groups = new ArrayList<String>();
	private List<List<UserInfo>> children = new ArrayList<List<UserInfo>>();
	private LayoutInflater myInflater;
	private Context context;
	
	public UserExpandListAdapter(Context context,List<String> groups,List<List<UserInfo>> children){
		this.context = context;
		myInflater = LayoutInflater.from(context);
		this.groups = groups;
		this.children = children;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return children.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		 ViewChildHolder child_holder = null;
		if(null == convertView){
			convertView = myInflater.inflate(R.layout.user_expand_child_view, null);
			child_holder = new ViewChildHolder();
			child_holder.child_name = (TextView) convertView.findViewById(R.id.child_name);
			child_holder.child_ip = (TextView) convertView.findViewById(R.id.child_ip);
			child_holder.child_infos = (TextView) convertView.findViewById(R.id.child_infos);
			child_holder.user_img = (ImageView) convertView.findViewById(R.id.user_img);
			convertView.setTag(child_holder);
		}else{
			child_holder = (ViewChildHolder) convertView.getTag();
		}
	
		final UserInfo user_info = children.get(groupPosition).get(childPosition);
		child_holder.child_name.setText(user_info.getUserName());
		child_holder.child_ip.setText(user_info.getIp());
		child_holder.user_img.setImageResource(R.drawable.default_character_img);
		if( 0 == user_info.getMsgCount()){
			child_holder.child_infos.setVisibility(View.GONE);
		}else{
			child_holder.child_infos.setVisibility(View.VISIBLE);
			child_holder.child_infos.setText(user_info.getMsgCount()+"");
		}
		convertView.setOnClickListener(new View.OnClickListener(){	

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(context, ChatActivity.class);
				intent.putExtra("receiverName", user_info.getUserName());
				intent.putExtra("receiverIp", user_info.getIp());
				intent.putExtra("receiverGroup", user_info.getGroupName());
				
				user_info.setMsgCount(0);
				notifyDataSetChanged();
				context.startActivity(intent);
				
			}
			
		});
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return children.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groups.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		ViewGroupHolder group_holder = null;
		if(null == convertView){
			convertView = myInflater.inflate(R.layout.user_expand_group_view, null);
			group_holder = new ViewGroupHolder();
			group_holder.group_img = (ImageView) convertView.findViewById(R.id.group_img);
			group_holder.group_text = (TextView) convertView.findViewById(R.id.group);
			group_holder.group_count = (TextView) convertView.findViewById(R.id.group_onlinenum);
			convertView.setTag(group_holder);
		}else{
			group_holder = (ViewGroupHolder) convertView.getTag();
		}
		if(isExpanded){
			group_holder.group_img.setBackgroundResource(R.drawable.group_exp);
		}else{
			group_holder.group_img.setBackgroundResource(R.drawable.group_notexp);
		}
		group_holder.group_text.setText(groups.get(groupPosition));
		group_holder.group_count.setText("[" + getChildrenCount(groupPosition) + "]");
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}
	
	/**
	 * Child HolderView类
	 * */
	private class ViewChildHolder {
		TextView child_name;
		TextView child_ip;
		TextView child_infos;
		ImageView user_img;
	}
	
	/**
	 *  Group HolderView类
	 *	2014-7-18
	 */
	private class ViewGroupHolder{
		ImageView group_img;
		TextView group_text;
		TextView group_count;
	}

}
