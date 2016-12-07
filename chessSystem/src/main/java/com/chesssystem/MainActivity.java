package com.chesssystem;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chesssystem.fragment.MainFragment;
import com.chesssystem.fragment.MyFragment;
import com.chesssystem.fragment.OrderFragment;
import com.chesssystem.ui.setting.WelcomeActivity;
/**
 * 
 * @author lyg
 * @time 2016-6-4上午9:47:30
 */
public class MainActivity extends FragmentActivity implements OnClickListener {
	FragmentManager fragmentManager = getSupportFragmentManager();
	MainFragment mainFragment = (MainFragment) MainFragment.getInstance();
	OrderFragment orderFragment = (OrderFragment) OrderFragment.getInstance();
    MyFragment myFragment = (MyFragment) MyFragment.getInstance();
	private Fragment mContent;
	private ImageView ivMain;
	private ImageView ivOrder; 
	private ImageView ivMy;
	private LinearLayout llMain;
	private LinearLayout llOrder; 
	private LinearLayout llMy;
	private TextView tvMain;
	private TextView tvOrder;
	private TextView tvMy;
	public static LinearLayout llBgWelcome;
	public static LinearLayout llMainParent;
	public static MainActivity instance;
	public static boolean isBgFinish=false;
	public static boolean isMsgFinish=false;
	public static boolean keydown=false;
	static TranslateAnimation mShowAction;
	static TranslateAnimation mHiddenAction;
	public static void DispearBg(){
		if(isBgFinish&&isMsgFinish){
			llMainParent.setVisibility(View.VISIBLE);
			llBgWelcome.setVisibility(View.GONE);
			keydown=true;
		}
	}
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//保持的竖屏
		instance=this;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				isBgFinish=true;
				DispearBg();
			}
		},2000);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if(isMsgFinish==false)
					isMsgFinish=true;
				DispearBg();
			}
		},3000);
		initAnimation();
		initView();
		initFragment();
	}
	
	private void initAnimation() {
		mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,     
	            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,     
	            -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);     
		mShowAction.setDuration(500);  
		mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,     
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,     
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,     
                -1.0f);    
		mHiddenAction.setDuration(500); 		
	}

	private void initView() {
		llMain = (LinearLayout) findViewById(R.id.ll_main);
		llMain.setOnClickListener(this);
		llOrder = (LinearLayout) findViewById(R.id.ll_order);
		llOrder.setOnClickListener(this);
		llMy = (LinearLayout) findViewById(R.id.ll_my);
		llMy.setOnClickListener(this);
		ivMain = (ImageView) findViewById(R.id.iv_main);
		ivOrder = (ImageView) findViewById(R.id.iv_order);
		ivMy = (ImageView) findViewById(R.id.iv_my);
		tvMain = (TextView) findViewById(R.id.tv_main);
		tvOrder = (TextView) findViewById(R.id.tv_order);
		tvMy = (TextView) findViewById(R.id.tv_my);
		llBgWelcome=(LinearLayout) findViewById(R.id.ll_bg_welcome);
		llMainParent=(LinearLayout) findViewById(R.id.ll_main_parent);
		llMainParent.setVisibility(View.GONE);
	}

	private void initFragment() {
		switchFragment(R.id.fragment_container_MainActivity, mainFragment);
		ivMain.setImageResource(R.drawable.main_press1);
		tvMain.setTextColor(this.getResources().getColor(R.color.main_green));
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
	}
	private void switchFragment(int id, Fragment fragment) {
		initImage();
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		if (mContent != fragment) {
			if (!fragment.isAdded()) {// 先判断是否被add过
				if (mContent == null) {
					transaction.add(id, fragment).commit(); 
				} else {
					transaction.hide(mContent).add(id, fragment).commit();// 隐藏当前的fragment，add下一个到Activity中
				}
			}else{
				transaction.hide(mContent).show(fragment).commit(); // 隐藏当前的fragment，显示下一个
			}
			mContent = fragment;
		}
	}
	public static void restartFragment(){
	}

	public void initImage() {
		ivMain.setImageResource(R.drawable.main_default1);
		ivOrder.setImageResource(R.drawable.order_default1);
		ivMy.setImageResource(R.drawable.my_default1);
		tvMain.setTextColor(this.getResources().getColor(
				R.color.text_color_shenhui_677582));
		tvOrder.setTextColor(this.getResources().getColor(
				R.color.text_color_shenhui_677582));
		tvMy.setTextColor(this.getResources().getColor(
				R.color.text_color_shenhui_677582));
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ll_main:
			switchFragment(R.id.fragment_container_MainActivity, mainFragment);
			ivMain.setImageResource(R.drawable.main_press1);
			tvMain.setTextColor(this.getResources()
					.getColor(R.color.main_green));
			break;
		case R.id.ll_order:
			switchFragment(R.id.fragment_container_MainActivity, orderFragment);
			ivOrder.setImageResource(R.drawable.order_press1);
			tvOrder.setTextColor(this.getResources().getColor(
					R.color.main_green));
			break;
		case R.id.ll_my:
			switchFragment(R.id.fragment_container_MainActivity, myFragment);
			ivMy.setImageResource(R.drawable.my_press1);
			tvMy.setTextColor(this.getResources().getColor(R.color.main_green));
			break;

		default:
			break;
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK&&keydown){
			exitBy2Click(); //调用双击退出函数}
		}
		return false;
	}
	/*
	 * 双击返回键退出 
	 */
	private static Boolean isExit = false;
	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true;// 准备退出
			Toast.makeText(this, "再按一次退出~", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override  
				public void run() {
					isExit = false;// 取消退出
					}
				}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
			} else { 
				finish();
				System.exit(0);
				}
		}

}
