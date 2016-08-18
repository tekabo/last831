package com.mantoto.property.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.wuxianyingke.property.activities.PayFailActivity;
import com.wuxianyingke.property.activities.PaySuccessActivity;
import com.wuxianyingke.property.common.Constants;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
	
	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
	
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_pay_success);
		Log.i("MyLog","wxpay======");
    	api = WXAPIFactory.createWXAPI(this, Constants.WEICHAT_APPID);
        api.handleIntent(getIntent(), this);

    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {

	}


	@Override
	public void onResp(BaseResp resp) {
//		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
		Log.i("MyLog","wxpay======");
//		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle("提示");
//			builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
//			builder.show();
//		}

		if (resp.errCode==0){
			Intent intent = new Intent();
			intent.setClass(WXPayEntryActivity.this, PaySuccessActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			finish();
		}else if(resp.errCode==-2){
			Toast.makeText(WXPayEntryActivity.this, "支付失败，您取消了支付",
					Toast.LENGTH_SHORT).show();
			Intent intent = new Intent();
			intent.setClass(WXPayEntryActivity.this, PayFailActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			finish();
		}else {
			Toast.makeText(WXPayEntryActivity.this, "支付失败"+resp.errCode+ resp.errStr+resp.openId,
					Toast.LENGTH_SHORT).show();
			Intent intent = new Intent();
			intent.setClass(WXPayEntryActivity.this, PayFailActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			finish();
		}
	}
}