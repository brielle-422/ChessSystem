package com.chesssystem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
/**
 * 
 * @author lyg
 * @time 2016-7-1上午9:47:15
 */
public class BaseActivity extends Activity implements OnClickListener{
	public InputMethodManager imm;
	public ProgressDialog progressDialog;
	public SharedPreferences sharedPreferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//保持的竖屏
		// 输入法弹出框
		imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		sharedPreferences=getSharedPreferences("userInfor",MODE_PRIVATE);
	}
	@Override
	public void onClick(View view) {
	}

}
