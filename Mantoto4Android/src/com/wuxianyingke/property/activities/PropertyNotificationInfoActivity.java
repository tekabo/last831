package com.wuxianyingke.property.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.mantoto.property.R;
import com.umeng.message.PushAgent;

/**
 * 物业通知查看
 */
public class PropertyNotificationInfoActivity extends BaseActivity {
    private TextView topbar_txt;
    private Button topbar_left;
    private TextView mProductMessageInfoTimeTextView,
            mProductMessageInfoContentTextView, mNoticeTimeTextView,
            mNoticeSignatureTextView, mNoticeSignatureTimeTextView;
    private String mProductMessageInfoTitle, mProductMessageInfoTime,
            mProductMessageInfoContent, mProductMessageInfoSignature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        PushAgent.getInstance(getApplicationContext()).onAppStart();
        setContentView(R.layout.property_notification_info);
        setImmerseLayout(findViewById(R.id.common_back));
        Bundle bundle = getIntent().getExtras();
        mProductMessageInfoTitle = bundle.getString("productMessageInfoTitle");
        mProductMessageInfoTime = bundle.getString("productMessageInfoTime");
        mProductMessageInfoContent = bundle
                .getString("productMessageInfoContent");
        mProductMessageInfoSignature = bundle
                .getString("productMessageInfoSignature");

        initWidgets();
    }

    private void initWidgets() {
        mProductMessageInfoTimeTextView = (TextView) findViewById(R.id.ProductMessageInfoTimeTextView);
        mProductMessageInfoContentTextView = (TextView) findViewById(R.id.ProductMessageInfoContentTextView);
        mNoticeTimeTextView = (TextView) findViewById(R.id.NoticeTimeTextView);
        mNoticeSignatureTextView = (TextView) findViewById(R.id.NoticeSignatureTextView);
        mNoticeSignatureTimeTextView = (TextView) findViewById(R.id.NoticeSignatureTimeTextView);
        topbar_txt = (TextView) findViewById(R.id.topbar_txt);

        topbar_left = (Button) findViewById(R.id.topbar_left);
        topbar_txt.setText("通知");
        topbar_left.setVisibility(View.VISIBLE);
        topbar_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        mProductMessageInfoTimeTextView.setText(mProductMessageInfoTitle);
        mProductMessageInfoContentTextView.setText("       "
                + mProductMessageInfoContent);
        mNoticeTimeTextView.setText(mProductMessageInfoTime);
        mNoticeSignatureTextView.setText(mProductMessageInfoSignature);
        mNoticeSignatureTimeTextView.setText(mProductMessageInfoTime);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}