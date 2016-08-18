package com.wuxianyingke.property.activities;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.mantoto.property.R;
import com.umeng.message.PushAgent;
import com.wuxianyingke.property.camera.CameraManager;
import com.wuxianyingke.property.common.LocalStore;
import com.wuxianyingke.property.common.LogUtil;
import com.wuxianyingke.property.decoding.CaptureActivityHandler;
import com.wuxianyingke.property.decoding.InactivityTimer;
import com.wuxianyingke.property.decoding.RGBLuminanceSource;
import com.wuxianyingke.property.remote.RemoteApi;
import com.wuxianyingke.property.remote.RemoteApiImpl;
import com.wuxianyingke.property.views.ViewfinderView;

public class MipcaActivityCapture extends BaseActivity implements Callback, View.OnClickListener{

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	
	
	private static final int REQUEST_CODE = 100;
	private static final int PARSE_BARCODE_SUC = 300;
	private static final int PARSE_BARCODE_FAIL = 303;
	private ProgressDialog mProgress;
	private String photo_path;
	private Bitmap scanBitmap;
    Dialog dialog = null;

	private String mErrorInfo = "";
	private String desc = "";
	private String resultString;
	private SharedPreferences saving;
	private int propertyid;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		setContentView(R.layout.activity_capture);
		setImmerseLayout(findViewById(R.id.common_back));
		//ViewUtil.addTopView(getApplicationContext(), this, R.string.scan_card);
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		
		Button mButtonBack = (Button) findViewById(R.id.topbar_left);
        mButtonBack.setVisibility(View.VISIBLE);
		mButtonBack.setOnClickListener(this);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);

		saving = this.getSharedPreferences(LocalStore.USER_INFO, 0);
//		propertyid = (int) saving.getLong(LocalStore.PROPERTY_ID, 0);
		propertyid=LocalStore.getUserInfo().PropertyID;

		ImageButton mImageButton = (ImageButton) findViewById(R.id.button_function);
		mImageButton.setOnClickListener(this);
	}
	
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.topbar_left:
			this.finish();
			break;
		case R.id.button_function:
            //打开手机中的相册
			Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT); //"android.intent.action.GET_CONTENT"
	        innerIntent.setType("image/*");
			Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
	        this.startActivityForResult(wrapperIntent, REQUEST_CODE);
			break;
		}
	}
	
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			mProgress.dismiss();
			switch (msg.what) {
				// 登录失败
				case 0:
					Toast.makeText(MipcaActivityCapture.this, mErrorInfo,
							Toast.LENGTH_SHORT).show();
					break;

				// 登陆成功
				case 1:
					Toast.makeText(MipcaActivityCapture.this, "签码成功",
							Toast.LENGTH_SHORT).show();
					dialog = new AlertDialog.Builder(MipcaActivityCapture.this).create();
					LayoutInflater inflater = LayoutInflater.from(MipcaActivityCapture.this);
					View backupExpandHeader = inflater.inflate(R.layout.mipca_dialog, null);

					LinearLayout.LayoutParams params_rb = new LinearLayout.LayoutParams(   LinearLayout.LayoutParams.WRAP_CONTENT,
							LinearLayout.LayoutParams.WRAP_CONTENT );

					backupExpandHeader.setLayoutParams(params_rb);
					TextView popTitle = (TextView) backupExpandHeader.findViewById(R.id.normal_dialog_title);
					popTitle.setText("签码");
					TextView popInfo = (TextView) backupExpandHeader.findViewById(R.id.normal_dialog_content);
					popInfo.setText("签码成功,感谢使用!");

					dialog.show();
					dialog.setContentView(backupExpandHeader);
					
					Timer timer = new Timer();  
				    timer.schedule(new TimerTask()  
					     {  
					         public void run()  
					         {  
					             finish();
					         }  
					     },  
			     	2000);  

					break;
				// 通讯错误
				case 4:
					Toast.makeText(MipcaActivityCapture.this, "通讯错误，请检查网络或稍后再试。",
							Toast.LENGTH_SHORT).show();
					break;
				case 8:
					Toast.makeText(MipcaActivityCapture.this, desc,
							Toast.LENGTH_SHORT).show();
					break;
				case 9:
					Toast.makeText(MipcaActivityCapture.this, "网络超时，请重新获取",
							Toast.LENGTH_SHORT).show();
					break;
				case 201:
					Toast.makeText(MipcaActivityCapture.this, "网络超时，请重新获取",
							Toast.LENGTH_SHORT).show();
					break;
			case PARSE_BARCODE_SUC:
				onResultHandler((String)msg.obj, scanBitmap);
				break;
			case PARSE_BARCODE_FAIL:
				Toast.makeText(MipcaActivityCapture.this, (String) msg.obj, Toast.LENGTH_LONG).show();
				break;
			}
		}
		
	};
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == RESULT_OK){
			switch(requestCode){
			case REQUEST_CODE:
                //获取选中图片的路径
				Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
				if (cursor.moveToFirst()) {
					photo_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
				}
				cursor.close();
				
				mProgress = new ProgressDialog(MipcaActivityCapture.this);
				mProgress.setMessage("正在扫描...");
				mProgress.setCancelable(false);
				mProgress.show();
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						Result result = scanningImage(photo_path);
						if (result != null) {
							Message m = mHandler.obtainMessage();
							m.what = PARSE_BARCODE_SUC;
							m.obj = result.getText();
							mHandler.sendMessage(m);
						} else {
							Message m = mHandler.obtainMessage();
							m.what = PARSE_BARCODE_FAIL;
							m.obj = "Scan failed!";
							mHandler.sendMessage(m);
						}
					}
				}).start();
				
				break;
			
			}
		}
	}

    /**
     * 扫描二维码图片的方法
      */
	public Result scanningImage(String path) {
		if(TextUtils.isEmpty(path)){
			return null;
		}
		Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); //设置二维码内容的编码

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
		int sampleSize = (int) (options.outHeight / (float) 200);
		if (sampleSize <= 0)
			sampleSize = 1;
		options.inSampleSize = sampleSize;
		scanBitmap = BitmapFactory.decodeFile(path, options);
		RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
		BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
		QRCodeReader reader = new QRCodeReader();
		try {
			return reader.decode(bitmap1, hints);

		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (ChecksumException e) {
			e.printStackTrace();
		} catch (FormatException e) {
			e.printStackTrace();
		}
		return null;
	}


	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			LogUtil.d("man","initCamera");
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

    /**
     * 处理扫描结果
     */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		resultString = result.getText();
//		onResultHandler(resultString, barcode);

		mProgress = new ProgressDialog(MipcaActivityCapture.this);
		mProgress.setMessage("正在签码...");
		mProgress.setCancelable(false);
		mProgress.show();

		Thread loginThread = new Thread() {
			public void run() {
				RemoteApiImpl remote = new RemoteApiImpl();
				RemoteApi.NetInfo netInfo = null;
				netInfo = remote.repairSolved(propertyid,saving.getLong(LocalStore.USER_ID, 0),1,resultString.trim());
				Message msg = new Message();
				if (netInfo == null) {
					msg.what = 4;
				} else if (200 == netInfo.code) {
					msg.what = 1;
				} else {
					msg.what = 0;
					mErrorInfo = netInfo.desc;
				}

				mHandler.sendMessage(msg);
				
				if(msg.what != 1){
					refresh();
				}
			}
		};
		loginThread.start();
	}

    /** 
     * 刷新 
     */  
    private void refresh() {  
        finish();  
        Intent intent = new Intent(MipcaActivityCapture.this, MipcaActivityCapture.class);  
        startActivity(intent);  
    }  
	
    /**
     * 跳转到上一个页面
     */
	private void onResultHandler(String resultString, Bitmap bitmap){
		if(TextUtils.isEmpty(resultString)){
			Toast.makeText(MipcaActivityCapture.this, "Scan failed!", Toast.LENGTH_SHORT).show();
			return;
		}
        LogUtil.d("resultString=",resultString);
//		Intent resultIntent = new Intent();
//		Bundle bundle = new Bundle();
//		bundle.putString("result", resultString);
//		bundle.putParcelable("bitmap", bitmap);
//		resultIntent.putExtras(bundle);
//		this.setResult(RESULT_OK, resultIntent);
//		MipcaActivityCapture.this.finish();
	}
	
	private void initCamera(SurfaceHolder surfaceHolder) {
		try {

            LogUtil.d("line 282", "initCamera");
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
            ioe.printStackTrace();
			return;
		} catch (RuntimeException e) {
            e.printStackTrace();
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};



}