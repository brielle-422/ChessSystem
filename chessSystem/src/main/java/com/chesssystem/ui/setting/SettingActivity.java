package com.chesssystem.ui.setting;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Instrumentation.ActivityResult;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.chesssystem.BaseActivity;
import com.chesssystem.NimApplication;
import com.chesssystem.R;
import com.chesssystem.fragment.MyFragment;
import com.chesssystem.fragment.OrderFragment;
import com.chesssystem.picktime.JudgeDate;
import com.chesssystem.picktime.ScreenInfo;
import com.chesssystem.picktime.WheelMain;
import com.chesssystem.ui.dazi.InviteActivity;
import com.chesssystem.util.DateUtil;
import com.chesssystem.util.HttpUtl;
import com.chesssystem.util.ServerUrl;
import com.chesssystem.util.HttpUtl.HttpCallbackListener;
import com.chesssystem.widget.MyDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 设置界面
 * 
 * @author Administrator
 * 
 */
public class SettingActivity extends BaseActivity {
	private LinearLayout llBack;
	private TextView tvTitle;
	private RelativeLayout rlName;
	private RelativeLayout rlSex;
	private RelativeLayout rlBirthday;
	private MyDialog myDialog;
	private TextView tvPopContent;
	private Button btOut;
	private PopupWindow pop = null;
	private LinearLayout ll_popup;
	private Button btGril;
	private Button btBoy;
	private Button btConfirm;
	private View viewGirl;
	private View viewBoy;
	private View parent;
	private RelativeLayout RlParent;
	private WheelMain wheelMain;
	private TextView tvBirthday;
	private TextView tvName;
	private TextView tvSex;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private int sex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parent = getLayoutInflater().inflate(R.layout.activity_setting, null);
		setContentView(parent);
		initView();
		initDate();
		initPop();
	}

	private void initPop() {
		/*
		 * popupwindow
		 */
		pop = new PopupWindow(this);
		View popView = getLayoutInflater().inflate(R.layout.pop_sex, null);
		ll_popup = (LinearLayout) popView.findViewById(R.id.ll_popup);
		pop.setWidth(LayoutParams.MATCH_PARENT);
		pop.setHeight(LayoutParams.WRAP_CONTENT);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setContentView(popView);
		btGril = (Button) popView.findViewById(R.id.item_popupwindows_girl);
		btGril.setOnClickListener(this);
		btBoy = (Button) popView.findViewById(R.id.item_popupwindows_boy);
		btBoy.setOnClickListener(this);
		btConfirm = (Button) popView
				.findViewById(R.id.item_popupwindows_confirm);
		btConfirm.setOnClickListener(this);
		viewGirl = (View) popView.findViewById(R.id.view_girl);
		viewBoy = (View) popView.findViewById(R.id.view_boy);
		if (sex == 0) {
			viewGirl.setVisibility(View.VISIBLE);
		} else {
			viewBoy.setVisibility(View.VISIBLE);
		}
		RlParent = (RelativeLayout) popView.findViewById(R.id.parent);
		RlParent.setOnClickListener(this);
	}

	private void initView() {
		llBack = (LinearLayout) findViewById(R.id.ll_back);
		llBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.setting);
		rlName = (RelativeLayout) findViewById(R.id.rl_setting_name);
		rlName.setOnClickListener(this);
		rlSex = (RelativeLayout) findViewById(R.id.rl_setting_sex);
		rlSex.setOnClickListener(this);
		rlBirthday = (RelativeLayout) findViewById(R.id.rl_setting_birthday);
		rlBirthday.setOnClickListener(this);
		btOut = (Button) findViewById(R.id.bt_login_out);
		btOut.setOnClickListener(this);
		tvBirthday = (TextView) findViewById(R.id.tv_setting_birthday);
		tvName = (TextView) findViewById(R.id.tv_setting_name);
		tvSex = (TextView) findViewById(R.id.tv_setting_sex);
	}

	private void initDate() {
		tvName.setText(sharedPreferences.getString("nick", ""));
		if (sharedPreferences.getInt("sex", 0) == 0) {
			tvSex.setText("女");
			sex = 0;
		} else {
			tvSex.setText("男");
			sex = 1;
		}
		tvBirthday.setText(DateUtil
				.deleteHours(sharedPreferences.getString(
						"birthday", "")));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 0:
			if (resultCode == 0) {
				if (data != null)
					tvName.setText(data.getStringExtra("nick"));
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ll_back:
			finish();
			break;
		// 点击姓名
		case R.id.rl_setting_name:
			Intent intent = new Intent(this, NameSettingActivity.class);
			startActivityForResult(intent, 0);
			break;
		// 点击性别
		case R.id.rl_setting_sex:
			ll_popup.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.activity_translate_in));
			pop.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			break;
		// 点击生日
		case R.id.rl_setting_birthday:
			pickBeginTime();
			break;
		// 点击弹出框的其他位置
		case R.id.parent:
			pop.dismiss();
			ll_popup.clearAnimation();
			break;
		// 点击弹出框的女选项
		case R.id.item_popupwindows_girl:
			sex = 0;
			viewGirl.setVisibility(View.VISIBLE);
			viewBoy.setVisibility(View.GONE);
			break;
		// 点击弹出框的男选项
		case R.id.item_popupwindows_boy:
			sex = 1;
			viewGirl.setVisibility(View.GONE);
			viewBoy.setVisibility(View.VISIBLE);
			break;
		// 点击性别选择框的确定按钮
		case R.id.item_popupwindows_confirm:
			modifyInfor("sex", sex + "");
			break;
		// 点击退出
		case R.id.bt_login_out:
			popDialog("退出后您将不能查看订单，确定退出吗?");
			break;
		default:
			break;
		}
	}

	/*
	 * 修改信息接口
	 */
	private void modifyInfor(final String key, final String values) {
		StringRequest stringRequest = new StringRequest(Method.POST,
				ServerUrl.postModifyInforUrl, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							JSONObject jsonObject = new JSONObject(response);
							if (jsonObject.getInt("retCode") == 0) {
								if (key.equals("sex")) {
									if (sex == 0) {
										tvSex.setText("女");
									} else {
										tvSex.setText("男");
									}
									pop.dismiss();
									ll_popup.clearAnimation();
									Editor editor = sharedPreferences.edit();
									editor.putInt("sex",
											Integer.parseInt(values)).commit();// 修改数据库信息
								} else if(key.equals("birthday")){
									Editor editor = sharedPreferences.edit();
									editor.remove("birthday");
									editor.putString("birthday",
											values+" 00:00:00").commit();// 修改数据库信息
								}
								Toast.makeText(SettingActivity.this, "保存成功",
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(SettingActivity.this,
										jsonObject.getString("retMsg"),
										Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("false", error.getMessage(), error);
						Toast.makeText(SettingActivity.this, error + "",
								Toast.LENGTH_SHORT).show();
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				// map.put("enctype","multipart/form-data");
				map.put("userId", sharedPreferences.getString("userId", ""));
				map.put(key, values);
				return map;
			}
		};
		NimApplication.getHttpQueues().add(stringRequest);
	}

	/**
	 * 退出提示框
	 * 
	 * @param content
	 */
	public void popDialog(String content) {
		myDialog = new MyDialog(this, R.style.inputDialog);
		tvPopContent = (TextView) myDialog.getContent();
		tvPopContent.setText(content);
		myDialog.setOnPositiveListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				/*
				 * 注销
				 */
				myDialog.dismiss();
				NimApplication.LOGINED = false;
				// if (MyFragment.myFragmentInstance.isAdded())
				// MyFragment.initInfor();
				// if (OrderFragment.orderFragmentInstance.isAdded())
				// OrderFragment.initListOrder();
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.clear().commit();
				finish();
			}
		});
		myDialog.setOnNegativeListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				myDialog.dismiss();
			}
		});
		myDialog.show();
	}

	/**
	 * 选择生日
	 */
	private void pickBeginTime() {
		LayoutInflater inflater = LayoutInflater.from(SettingActivity.this);
		final View timepickerview = inflater.inflate(R.layout.pop_picktime,
				null);
		ScreenInfo screenInfo = new ScreenInfo(SettingActivity.this);
		wheelMain = new WheelMain(timepickerview, true);
		wheelMain.screenheight = screenInfo.getHeight();
		String time = tvBirthday.getText().toString();
		Calendar calendar = Calendar.getInstance();
		if (JudgeDate.isDate(time, "yyyy-MM-dd")) {
			try {
				calendar.setTime(dateFormat.parse(time));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		wheelMain.initDateTimePicker(year, month, day);
		new AlertDialog.Builder(SettingActivity.this).setTitle("选择时间")
				.setView(timepickerview)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(!DateUtil.checkTimeAndNow_noHour(wheelMain.getTime_nohours())){
						modifyInfor("birthday", wheelMain.getTime_nohours());
						tvBirthday.setText(wheelMain.getTime_nohours());
						}else{
							Toast.makeText(SettingActivity.this, "出生日期不能晚于今天~", Toast.LENGTH_SHORT).show();
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
	}
}
