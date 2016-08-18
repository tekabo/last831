package com.wuxianyingke.property.activities;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LogUtil;

public class SettingActivity extends BaseActivity {
	private CheckBox mPushMessageCheckBox;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		setContentView(R.layout.setting);
		setImmerseLayout(findViewById(R.id.common_back));
		LogUtil.d("MyTag","PushMessage="+LocalStore.getPushMessge(SettingActivity.this));
		mPushMessageCheckBox=(CheckBox)findViewById(R.id.PushMessageCheckBox);
		mPushMessageCheckBox.setChecked(LocalStore.getPushMessge(SettingActivity.this));
		mPushMessageCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				LogUtil.d("MyTag","PushMessage="+isChecked);
				LocalStore.setPushMessage(SettingActivity.this,isChecked);
			    Toast.makeText(SettingActivity.this, "设置成功！", Toast.LENGTH_SHORT).show();
			}
		});
	}
}
