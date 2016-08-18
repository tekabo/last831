package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.adapter.AddressAdapter;

public class AopyOfAddressActivity extends Activity {

	// 顶部导航
	private TextView topbar_txt, topbar_right;
	private Button topbar_left;
	// private Button topbar_right;
//	private Context ctx;

	private int favorite_flag;

//	private ListView mLogsListView;
//	private ArrayList<AddressItem> mDataList = new ArrayList<AddressItem>();

	private ListView listView;

	private static AddressAdapter mListAdapter;

//	private ProgressDialog mProgressDialog;
//
//	private AddressAllThread mThread = null;
//
//	private long uid = 0;
	// 品牌商品列表

//	private Handler mHandler = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case Constants.MSG_GET_CANYIN_LIST_FINISH:
//				if (mProgressDialog != null && mProgressDialog.isShowing()) {
//					mProgressDialog.dismiss();
//				}
//				if (mThread != null)
//					showLogsListView(mThread.getAddressList());
//				break;
//			case Constants.MSG_GET_CANYIN_LIST_EMPTY:
//				if (mProgressDialog != null && mProgressDialog.isShowing()) {
//					mProgressDialog.dismiss();
//				}
//				((TextView) findViewById(R.id.empty_tv))
//						.setVisibility(View.VISIBLE);
//				break;
//			case Constants.MSG_NETWORK_ERROR:
//				if (mProgressDialog != null && mProgressDialog.isShowing()) {
//					mProgressDialog.dismiss();
//				}
//				// if(mPageNum == 1)
//				// {
//				// View view = findViewById(R.id.view_network_error);
//				// view.setVisibility(View.VISIBLE);
//				// }
//				// else
//				// {
//				// LogUtil.d("MyTag","MSG_NETWORK_ERROR");
//				// mPageNum--;
//				// mAllowGetLogAgain = false;
//				// searchLayout.setVisibility(View.GONE);
//				// }
//				break;
//			case Constants.MSG_GET_SEARCH_ICON_FINISH:
//			case Constants.MSG_GET_CANYIN_DETAIL_IMG_FINISH:
//				if (mListAdapter != null)
//					mListAdapter.notifyDataSetChanged();
//				break;
//			}
//			super.handleMessage(msg);
//		}
//
//	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		setContentView(R.layout.user_address);

		// 初始化控件
		initView();
//		init();
		// 初始化事件监听器
		initListener();

	}

//	private void init() {
//		if (new AddressManage().getaddresss(getApplicationContext(), null)
//				.size() > 0) {
//			
////			mListAdapter = new AddressAdapter(getApplicationContext(),
////					new AddressManage().getaddresss(getApplicationContext(),
////							null));
//			listView.setAdapter(mListAdapter);
//		}
//
//	}

//	protected void onRestart() {
//		if (mLogsListView != null)
//			mLogsListView.invalidateViews();
//		super.onRestart();
//	}

//	@Override
//	protected void onNewIntent(Intent intent) {
//		Boolean needInit = intent.getBooleanExtra("FromGroup", false);
//		if (needInit) {
//			// 重新获取资源
//			showDialog();
//			endChildrenThreads();
//			// mAllowGetLogAgain = false;
//			// searchLayout.setVisibility(View.VISIBLE);
//			mThread = new AddressAllThread(ctx, mHandler, uid);
//			mThread.start();
//			LogUtil.d("MyTag", "Radio2Activity.this onNewIntent");
//		}
//		super.onNewIntent(intent);
//	}

//	@Override
//	protected void onDestroy() {
//		endChildrenThreads();
//		super.onDestroy();
//	}

//	protected void onResume() {
//		// TODO Auto-generated method stub
//		// startThread();
//		init();
//		super.onResume();
//	}

//	private void showDialog() {
//		View view = findViewById(R.id.view_network_error);
//		view.setVisibility(View.GONE);
//		mLogsListView.setVisibility(View.GONE);
//		if (mProgressDialog != null)
//			mProgressDialog.dismiss();
//		mProgressDialog = new ProgressDialog(AddressActivity.this);
//		mProgressDialog.setMessage("加载中，请稍候...");
//		mProgressDialog.setCancelable(true);
//		mProgressDialog.show();
//	}


	private void initListener() {
		// 左侧返回菜单处理事件
		topbar_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				if (0 != favorite_flag) {
					Intent intent = new Intent();
					intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
							| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.setClass(getApplicationContext(),
							CommitOrderActivity.class);
					startActivity(intent);
				} else {
					finish();
				}

			}
		});

		// 右侧按钮处理事件 进入地址编辑界面

		topbar_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AopyOfAddressActivity.this,
						AddressEditActivity.class);
				startActivity(intent);
			}
		});

	}


	private void initView() {

		// 顶部导航
		topbar_txt = (TextView) findViewById(R.id.topbar_txt);
		topbar_txt.setText("地址管理");
		topbar_left = (Button) findViewById(R.id.topbar_left);
		topbar_left.setVisibility(View.VISIBLE);
		topbar_right = (TextView) findViewById(R.id.topbar_right);
		topbar_right.setVisibility(View.VISIBLE);
		topbar_right.setText("新建");
		topbar_right.setTextSize(22);
		
		listView = (ListView) findViewById(R.id.lv_addressId);

	}

	// public void freeResource() {
	// int count = mDataList.size();
	// for (int i = 0; i < count; ++i) {
	// BitmapDrawable bd = (BitmapDrawable) mDataList.get(i).frontCover.imgDw;
	// if (bd != null && !bd.getBitmap().isRecycled()) {
	// bd.getBitmap().recycle();
	// }
	// }
	// mThread = null ;
	// showDialog();
	// endChildrenThreads();
	// }

//	public void showLogsListView(List<AddressItem> list) {
//		if (list == null) {
//			if (mProgressDialog != null) {
//				mProgressDialog.dismiss();
//				mProgressDialog = null;
//
//			}
//			// searchLayout.setVisibility(View.GONE);
//			return;
//		}
//		// mLogsListView.setVisibility(View.VISIBLE);
//		// if(mPageNum == 1)
//		// {
//		//
//		// mListAdapter = new CanYinListAdapter(this,
//		// list,mHandler,shoucang_flag,0);
//		// LogUtil.d("MyTag","CanYinListActivity253-mItemSum mPageNum == 1  mLogAdapter.getCount()="+mListAdapter.getCount());
//		// if (mProgressDialog != null)
//		// {
//		// mProgressDialog.dismiss();
//		// mProgressDialog = null;
//		// }
//		// mLogsListView.setAdapter(mListAdapter);
//		// }
//		// else
//		// {
//		// mListAdapter.appandAdapter(list);
//		// mListAdapter.notifyDataSetChanged();
//		// LogUtil.d("MyTag","CanYinListActivity265- mItemSum = mLogAdapter.getCount()="+mListAdapter.getCount());
//		// int test12 = mItemSum % 10;
//		// if(test12 == 0)
//		// mLogsListView.setSelection(mItemSum - 10);
//		// else
//		// mLogsListView.setSelection(mItemSum - test12);
//		// }
//		// setAllowGetPageAgain();
//	}

	// 以下为分页显示代码
	// private static LinearLayout searchLayout = null;
	// public LinearLayout showLayout()
	// {
	// searchLayout = new LinearLayout(this);
	// searchLayout.setOrientation(LinearLayout.HORIZONTAL);
	//
	// ProgressBar progressBar = new ProgressBar(this);
	// progressBar.setPadding(0, 0, 15, 0);
	// progressBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_medium));
	// searchLayout.addView(progressBar, new
	// LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
	// LinearLayout.LayoutParams.WRAP_CONTENT));
	//
	// TextView textView = new TextView(this);
	// textView.setText("加载中...");
	// textView.setGravity(Gravity.CENTER_VERTICAL);
	// searchLayout.addView(textView, new
	// LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
	// LinearLayout.LayoutParams.FILL_PARENT));
	// searchLayout.setGravity(Gravity.CENTER);
	//
	// LinearLayout loadingLayout = new LinearLayout(this);
	// loadingLayout.addView(searchLayout, new
	// LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
	// LinearLayout.LayoutParams.WRAP_CONTENT));
	// loadingLayout.setGravity(Gravity.CENTER);
	// return loadingLayout;
	// }
	//
	// private static boolean mAllowGetLogAgain = false;
	// private static int mItemSum = 0;
	// private int mPageNum = 1;
	// private final int mPagecount = 10;
	// public static void setAllowGetPageAgain()
	// {
	// LogUtil.d("MyTag", "mItemSummItemSum="+mItemSum);
	// if(mListAdapter!=null)
	// mItemSum = mListAdapter.getCount();
	// int test12 = mItemSum % 10;
	// if((test12 != 0) || (mItemSum >= 96))
	// {
	// LogUtil.d("MyTag", "mItemSummItemSum"+test12);
	// mAllowGetLogAgain = false;
	// searchLayout.setVisibility(View.GONE);
	// }
	// else
	// {
	// mAllowGetLogAgain = true;
	// LogUtil.d("MyTag", "mItemSummItemSummAllowGetLogAgain = true"+test12);
	// }
	// }
	//
	// private OnScrollListener mScrollListner = new OnScrollListener()
	// {
	// private int lastItem = 0;
	//
	// @Override
	// public void onScroll(AbsListView view, int firstVisibleItem, int
	// visibleItemCount, int totalItemCount)
	// {
	// lastItem = firstVisibleItem + visibleItemCount - 1;
	//
	// }
	//
	// @Override
	// public void onScrollStateChanged(AbsListView view, int scrollState)
	// {
	// if (mListAdapter != null && lastItem >= (mListAdapter.getCount()-2))
	// {
	// LogUtil.d("MyTag",
	// "Radio2Activity.this mAllowGetLogAgain="+mAllowGetLogAgain);
	// if (!mAllowGetLogAgain)
	// return;
	// mAllowGetLogAgain = false;
	// mPageNum++;
	// LogUtil.d("MyTag", "Radio2Activity.this onScrollStateChanged");
	// mThread = new AddressAllThread(AddressAllThread.this,
	// mHandler, uid);
	// mThread.start();
	// }
	// }
	// };

//	private void endChildrenThreads() {
//
//		if (mThread != null) {
//			mThread.stopRun();
//			mThread = null;
//		}
//
//		mLogsListView.setAdapter(null);
//
//		// mAllowGetLogAgain = true;
//		// mPageNum = 1;
//		// mItemSum = 0;
//	}

//	void initResource() {
//		// TODO Auto-generated method stub
//		LogUtil.d("MyTag", "Radio2Activity.this initResource");
//		// freeResource() ;
//		if (mThread == null)
//			showDialog();
//		mThread = new AddressAllThread(ctx, mHandler, uid);
//		mThread.start();
//	}

}
