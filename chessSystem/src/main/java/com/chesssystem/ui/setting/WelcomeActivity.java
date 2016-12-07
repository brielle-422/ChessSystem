package com.chesssystem.ui.setting;


import com.chesssystem.BaseActivity;
import com.chesssystem.MainActivity;
import com.chesssystem.R;
import com.chesssystem.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
/**
 * 
 * @author lyg
 * @time 2016-7-4上午9:52:18
 */
public class WelcomeActivity extends BaseActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//		  WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
		setContentView(R.layout.activity_welcome);
		initView();
	}

	private void initView() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
				finish();
			}
		},1000);		
	}
}
