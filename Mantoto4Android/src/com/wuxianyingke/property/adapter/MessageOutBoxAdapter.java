package com.wuxianyingke.property.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantoto.property.R;
import com.wuxianyingke.property.activities.Radio3Activity;
import com.wuxianyingke.property.remote.RemoteApi.MessageInfo;


public class MessageOutBoxAdapter extends BaseAdapter {
	private List<MessageInfo> mList;
	private Context mContext;
	private boolean mStoped;
	private int mCount;
	
	public MessageOutBoxAdapter(Context ctx, List<MessageInfo> list) 
	{
		this.mContext = ctx;
		this.mList = list;
		this.mStoped = false;
		this.mCount = mList.size();
	}
	
	public void appandAdapter(List<MessageInfo> list) 
	{
		for(int i=0; i<list.size(); i++)
		{
			mList.add(list.get(i));
			mCount++;
		}
	}
	

	@Override
	public int getCount() {
		return mCount;
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		if(mStoped)
			return convertView;
		ActivityItem activityItem;
		final MessageInfo activity = mList.get(position);
		if (convertView == null) 
		{
			View v = LayoutInflater.from(mContext).inflate(R.layout.main_radio3_list_out_box_item, null);
			activityItem = new ActivityItem();
			activityItem.mProductMessageTitleTextView = (TextView) v.findViewById(R.id.ProductMessageTitleTextView);
			activityItem.mProductMessageTimeTextView = (TextView) v.findViewById(R.id.ProductMessageTimeTextView);
			activityItem.mMainRadio4ListItemLinearLayout = (LinearLayout) v.findViewById(R.id.MainRadio4ListItemLinearLayout);
			v.setTag(activityItem);
			convertView = v;
		} 
		else 
		{
			activityItem = (ActivityItem)convertView.getTag();
		}
		activityItem.mProductMessageTitleTextView.setText(activity.header);
		activityItem.mProductMessageTimeTextView.setText(activity.cTime);
		activityItem.mMainRadio4ListItemLinearLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Radio3Activity.outBoxMessageContants(mContext,activity);
				/*Intent intent=new Intent();
				intent.setClass(mContext, Radio3Activity.class);
				intent.putExtra("messageStatus", Constants.MESSAGE_OUT_BOX_STATUS);
				intent.putExtra("messageHeader", activity.header);
				intent.putExtra("messageTime", activity.cTime);
				intent.putExtra("messageBody", activity.body);
				intent.putExtra("messageTypeID", activity.type.messageTypeID);
				intent.putExtra("messageTypeName", activity.type.messageTypeName);
				mContext.startActivity(intent);*/
			}
		});
		
		return convertView;
	}

	class ActivityItem 
	{
		TextView mProductMessageTitleTextView;
		TextView mProductMessageTimeTextView;
		LinearLayout mMainRadio4ListItemLinearLayout;
	}
}
