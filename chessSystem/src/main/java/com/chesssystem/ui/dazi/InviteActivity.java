package com.chesssystem.ui.dazi;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.chesssystem.util.DateUtil;
import com.chesssystem.util.ServerUrl;
/**
 * 
 * @author lyg
 * @time 2016-7-4上午9:51:37
 */
public class InviteActivity extends BaseActivity {
	private LinearLayout llBack;
	private TextView tvTitle;
	private Button btInvite;
	private TextView tvPickTime;
	private EditText edtLocal;
	private EditText edtNumber;
	private EditText edtContent;
	private EditText edtContact;
	private ProgressDialog progressDialog;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private WheelMain wheelMain;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite);
		initView();
	}

	private void initView() {
		llBack = (LinearLayout) findViewById(R.id.ll_back);
		llBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.invite);
		btInvite = (Button) findViewById(R.id.bt_invite);
		btInvite.setOnClickListener(this);
		tvPickTime = (TextView) findViewById(R.id.tv_invite_picktime);
		tvPickTime.setOnClickListener(this);
		edtLocal=(EditText) findViewById(R.id.edt_invite_local);
		edtNumber=(EditText) findViewById(R.id.edt_invite_number);
		edtContent=(EditText) findViewById(R.id.edt_invite_content);
		edtContact=(EditText) findViewById(R.id.edt_invite_contact);
//		Calendar calendar = Calendar.getInstance();
//		tvPickTime.setText(calendar.get(Calendar.YEAR) + "-"
//				+ (calendar.get(Calendar.MONTH) + 1) + "-"
//				+ calendar.get(Calendar.DAY_OF_MONTH) + "");
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ll_back:
			finish();
			break;
			//选择时间
		case R.id.tv_invite_picktime:
			pickBeginTime();
			break;
			//点击发布
		case R.id.bt_invite:
			
			invite();
			break;

		default:
			break;
		}
	}
	/**
	 * 发布
	 */
	private void invite() {
		if(tvPickTime.getText().toString().equals("")){
			Toast.makeText(this, "开始时间不能为空!", Toast.LENGTH_SHORT).show();
		}else if(edtLocal.getText().toString().equals("")){
			Toast.makeText(this, "开始时间不能早于当前时间!", Toast.LENGTH_SHORT).show();
		}else if(edtLocal.getText().toString().equals("")){
			Toast.makeText(this, "地点不能为空!", Toast.LENGTH_SHORT).show();
		}else if(edtNumber.getText().toString().equals("")){
			Toast.makeText(this, "人数不能为空!", Toast.LENGTH_SHORT).show();
		}else if(edtContent.getText().toString().equals("")){
			Toast.makeText(this, "内容不能为空!", Toast.LENGTH_SHORT).show();
		}else if(edtContact.getText().toString().equals("")){
			Toast.makeText(this, "联系方式不能为空!", Toast.LENGTH_SHORT).show();
		}else{
			if (progressDialog == null) {
				progressDialog = new ProgressDialog(this);
				progressDialog.setMessage("发布中...");
				progressDialog.setCanceledOnTouchOutside(false);
			}
			progressDialog.show();
			uploadDazi();
		}
	}
	/**
	 * 创建搭子
	 */
	private void uploadDazi() {
		StringRequest stringRequest = new StringRequest(Method.POST,ServerUrl.postCreateDaziUrl,  
                new Response.Listener<String>() {  
                    @Override  
                    public void onResponse(String response) { 
                    	Log.d("sucess",response);  
                    	try {
							JSONObject jsonObject = new JSONObject(response);
							if (progressDialog != null) {
								progressDialog.dismiss();
							}
							if(jsonObject.getInt("retCode")==0){
								Toast.makeText(InviteActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
								finish();
							}else {
								Toast.makeText(InviteActivity.this,jsonObject.getString("retMsg"), Toast.LENGTH_SHORT).show();
							} 
							
                    	} catch (JSONException e) {
							e.printStackTrace();
						}
                    }  
                }, new Response.ErrorListener() {  
                    @Override  
                    public void onErrorResponse(VolleyError error) {  
                        Log.d("false", error.getMessage(), error); 
                        if (progressDialog != null) {
							progressDialog.dismiss();
						}
						Toast.makeText(InviteActivity.this, error+"", Toast.LENGTH_SHORT).show();
                    }  
                }){
			
				@Override
				protected Map<String, String> getParams() throws AuthFailureError {
					Map<String, String> map = new HashMap<String, String>();
					map.put("userId", NimApplication.sharedPreferences.getString("userId",""));
					map.put("lugContent",  edtContent.getText().toString());
					map.put("orderTime",  tvPickTime.getText().toString().replace("-", "/"));
					map.put("memberCount",  edtNumber.getText().toString());
					map.put("lugAddress",  edtLocal.getText().toString());
					map.put("telephone",  edtContact.getText().toString());
					return map;
				}
		}; 
		NimApplication.getHttpQueues().add(stringRequest);
	}

	/**
	 * 选择时间
	 */
	private void pickBeginTime() {
		LayoutInflater inflater = LayoutInflater.from(InviteActivity.this);
		final View timepickerview = inflater.inflate(R.layout.pop_picktime,
				null);
		ScreenInfo screenInfo = new ScreenInfo(InviteActivity.this);
		wheelMain = new WheelMain(timepickerview, true);
		wheelMain.screenheight = screenInfo.getHeight();
		String time = tvPickTime.getText().toString();
		Calendar calendar = Calendar.getInstance();
		if (JudgeDate.isDate(time, "yyyy-MM-dd HH:mm:ss")) {
			try {
				calendar.setTime(dateFormat.parse(time));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int h = calendar.get(Calendar.HOUR_OF_DAY);
		int m = calendar.get(Calendar.MINUTE);
		wheelMain.initDateTimePicker(year, month, day, h, m);
		new AlertDialog.Builder(InviteActivity.this)
				.setTitle("选择时间")
				.setView(timepickerview)
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if(DateUtil.checkTimeAndNow(wheelMain.getTime())){
									tvPickTime.setText(wheelMain.getTime());
								}else{
									Toast.makeText(InviteActivity.this,"开始时间不能早于当前时间!", Toast.LENGTH_SHORT).show();
								}
							}
						})
				.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						}).show();		
	}
}
