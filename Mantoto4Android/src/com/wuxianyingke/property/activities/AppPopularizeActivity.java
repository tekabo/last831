package com.wuxianyingke.property.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.adapter.AppPopularizeAdapter;
import com.wuxianyingke.property.common.Constants;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.remote.RemoteApi.AppPopularize;
import com.wuxianyingke.property.threads.GetAppPopularizeThread;

public class AppPopularizeActivity extends BaseActivityWithRadioGroup {

	private enum ProductListTab {
		LIST, GRID
	}

	private enum DataSource {
		CATE_SOURCE,
	}

	private ListView mLogsListView;
	private ArrayList<AppPopularize> mDataList = new ArrayList<AppPopularize>();
	private static AppPopularizeAdapter mListAdapter; // TODO 写重复了一个adapter
	private TextView topbar_txt,topbar_right;
	private Button topbar_left;
	private GetAppPopularizeThread mThread = null;

	private ProgressDialog mProgressDialog;
	private int propertyid;

	private ProductListTab mTab = ProductListTab.LIST;

	private DataSource mDataSource = DataSource.CATE_SOURCE;

	// 品牌商品列表

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.MSG_GET_TUIJIANYINGYONG_FINISH:
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
				if(mThread!=null)
					showLogsListView(mThread.getProductList());
				break;
			case Constants.MSG_GET_PRODUCT_LIST_EMPTY:
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
				((TextView) findViewById(R.id.empty_tv))
						.setVisibility(View.VISIBLE);
				break;
			case Constants.MSG_NETWORK_ERROR:
				if (mProgressDialog != null && mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
				if(mPageNum == 1)
				{
					View view = findViewById(R.id.view_network_error);
					view.setVisibility(View.VISIBLE);
				}
				else
				{
					LogUtil.d("MyTag","MSG_NETWORK_ERROR");
					mPageNum--;
					mAllowGetLogAgain = false;
					searchLayout.setVisibility(View.GONE);
				}
				break;
			case Constants.MSG_GET_SEARCH_ICON_FINISH:
			case Constants.MSG_GET_PRODUCT_DETAIL_IMG_FINISH:
				if (mListAdapter != null)
					mListAdapter.notifyDataSetChanged();
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		setContentView(R.layout.tuijianyingyong);
//		SharedPreferences saving = this.getSharedPreferences(
//				LocalStore.USER_INFO, 0);
//		propertyid = (int) saving.getLong(LocalStore.PROPERTY_ID, 0);
		propertyid=LocalStore.getUserInfo().PropertyID;
		topbar_txt= (TextView) findViewById(R.id.topbar_txt);
		topbar_txt.setText(R.string.gridview_tuijianyingyong);
		topbar_left = (Button) findViewById(R.id.topbar_left);
		topbar_left.setVisibility(View.VISIBLE);
		topbar_right = (TextView) findViewById(R.id.topbar_right);
		topbar_right.setVisibility(View.GONE);
		topbar_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			    finish();   
			}
		});

		mLogsListView = (ListView) findViewById(R.id.tuijianyingyong_list_view);
		mLogsListView.setVerticalScrollBarEnabled(false);
		mLogsListView.setDivider(getResources().getDrawable(R.drawable.list_line));
//		mLogsListView.addFooterView(showLayout());
		mLogsListView.setOnScrollListener(mScrollListner);
		showDialog();
		mThread = new GetAppPopularizeThread(AppPopularizeActivity.this,
				mHandler, propertyid, mPageNum);
		mThread.start();
	}
	protected void onRestart()
	{
		if(mLogsListView!=null)
			mLogsListView.invalidateViews();
		super.onRestart();
	}
	@Override
	protected void onNewIntent(Intent intent)
	{
		Boolean needInit = intent.getBooleanExtra("FromGroup", false);
		if(needInit)
		{
			//重新获取资源
			showDialog();
			endChildrenThreads();
			mAllowGetLogAgain = false;
			searchLayout.setVisibility(View.VISIBLE);
			mThread = new GetAppPopularizeThread(AppPopularizeActivity.this,
					mHandler, propertyid, mPageNum);
			mThread.start();
			LogUtil.d("MyTag", "Radio2Activity.this onNewIntent");
		}
		super.onNewIntent(intent);
	}
	
	@Override
	protected void onDestroy()
	{
		endChildrenThreads();
		super.onDestroy();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		//startThread();
		super.onResume();
	}

	private void showDialog() {
		View view = findViewById(R.id.view_network_error);
		view.setVisibility(View.GONE);
		mLogsListView.setVisibility(View.GONE);
		if (mProgressDialog != null)
			mProgressDialog.dismiss();
		mProgressDialog = new ProgressDialog(AppPopularizeActivity.this);
		mProgressDialog.setMessage("加载中，请稍候...");
		mProgressDialog.setCancelable(true);
		mProgressDialog.show();
	}


	@Override
	public void freeResource() {
		int count = mDataList.size();
		/*for (int i = 0; i < count; ++i) {
			BitmapDrawable bd = (BitmapDrawable) mDataList.get(i).frontCover.imgDw;
			if (bd != null && !bd.getBitmap().isRecycled()) {
				bd.getBitmap().recycle();
			}
		}*/
		mThread = null ;
		showDialog();
		endChildrenThreads();
	}

	public void showLogsListView(ArrayList<AppPopularize> arrayList) 
	{
		if (arrayList == null)
		{
			if (mProgressDialog != null)
			{
				mProgressDialog.dismiss();
				mProgressDialog = null;
				
			}
			searchLayout.setVisibility(View.GONE);
			return;
		}
		mLogsListView.setVisibility(View.VISIBLE);
		if(mPageNum == 1)
		{
			 
			mListAdapter = new AppPopularizeAdapter(this, arrayList,mHandler);
			LogUtil.d("MyTag","mItemSum mPageNum == 1  mLogAdapter.getCount()="+mListAdapter.getCount());
			if (mProgressDialog != null)
			{
				mProgressDialog.dismiss();
				mProgressDialog = null;
			}
			Collections.sort(arrayList, new Comparator<AppPopularize>() {  
				            /** 
				             *  
				             * @param lhs 
				             * @param rhs 
				             * @return an integer < 0 if lhs is less than rhs, 0 if they are 
				             *         equal, and > 0 if lhs is greater than rhs,比较数据大小时,这里比的是时间 
				             */  
				            @Override  
				            public int compare(AppPopularize lhs, AppPopularize rhs) {  
				                int priority1 = lhs.priority;  
				                int priority2 = rhs.priority;  
				                if (priority1>priority2) {  
				                    return 1;  
				                }  
				                return -1;  
				            }
				        });  
			mLogsListView.setAdapter(mListAdapter);

		}
		else
		{
			mListAdapter.appandAdapter(arrayList);
			mListAdapter.notifyDataSetChanged();
			LogUtil.d("MyTag","mItemSum = mLogAdapter.getCount()="+mListAdapter.getCount());
			int test12 = mItemSum % 10;
			if(test12 == 0)
				mLogsListView.setSelection(mItemSum - 10);
			else
				mLogsListView.setSelection(mItemSum - test12);
		}
		//setAllowGetPageAgain();
	}
	
	// 以下为分页显示代码
	private static LinearLayout searchLayout = null;
	public LinearLayout showLayout()
	{
		searchLayout = new LinearLayout(this);
		searchLayout.setOrientation(LinearLayout.HORIZONTAL);

		ProgressBar progressBar = new ProgressBar(this);
		progressBar.setPadding(0, 0, 15, 0);
		progressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_medium));
		searchLayout.addView(progressBar, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

		TextView textView = new TextView(this);
		textView.setText("加载中...");
		textView.setGravity(Gravity.CENTER_VERTICAL);
		searchLayout.addView(textView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
		searchLayout.setGravity(Gravity.CENTER);

		LinearLayout loadingLayout = new LinearLayout(this);
		loadingLayout.addView(searchLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		loadingLayout.setGravity(Gravity.CENTER);
		return loadingLayout;
	}
	
	private static boolean mAllowGetLogAgain = false;
	private static int mItemSum = 0;
	private int mPageNum = 1;
	private final int mPagecount = 10;
	public static void setAllowGetPageAgain()
	{
		LogUtil.d("MyTag", "mItemSummItemSum="+mItemSum);
		if(mListAdapter!=null)
			mItemSum = mListAdapter.getCount();
		int test12 = mItemSum % 10;
		if((test12 != 0) || (mItemSum >= 96))
		{
			LogUtil.d("MyTag", "mItemSummItemSum"+test12);
			mAllowGetLogAgain = false;
			searchLayout.setVisibility(View.GONE);
		}
		else
		{
			mAllowGetLogAgain = true;
			LogUtil.d("MyTag", "mItemSummItemSummAllowGetLogAgain = true"+test12);
		}
	}
	
	private OnScrollListener mScrollListner = new OnScrollListener()
	{
		private int lastItem = 0;
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
		{
			lastItem = firstVisibleItem + visibleItemCount - 1;
			
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState)
		{
			if (mListAdapter != null && lastItem >= (mListAdapter.getCount()-2))
			{
				LogUtil.d("MyTag", "Radio2Activity.this mAllowGetLogAgain="+mAllowGetLogAgain);
				if (!mAllowGetLogAgain)
					return;
				mAllowGetLogAgain = false;
				mPageNum++;
				LogUtil.d("MyTag", "Radio2Activity.this onScrollStateChanged");
				mThread = new GetAppPopularizeThread(AppPopularizeActivity.this,
						mHandler, propertyid, mPageNum);
				mThread.start();
			}
		}
	};

	
	private void endChildrenThreads()
	{

		if (mThread != null)
		{
			mThread.stopRun();
			mThread = null;
		}

		mLogsListView.setAdapter(null);
		
		mAllowGetLogAgain = false;
		mPageNum = 1;
		mItemSum = 0;
	}
	void initResource() {
		// TODO Auto-generated method stub
		LogUtil.d("MyTag", "Radio2Activity.this initResource");
		freeResource() ;
		if (mThread == null) 
		showDialog();
		mThread = new GetAppPopularizeThread(AppPopularizeActivity.this,
				mHandler, propertyid, mPageNum);
		mThread.start();
	}
}
