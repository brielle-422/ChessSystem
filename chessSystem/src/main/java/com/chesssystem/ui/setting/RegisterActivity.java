package com.chesssystem.ui.setting;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.chesssystem.BaseActivity;
import com.chesssystem.NimApplication;
import com.chesssystem.R;
import com.chesssystem.R.id;
import com.chesssystem.R.layout;
import com.chesssystem.R.string;
import com.chesssystem.util.CountDownTimerUtils;
import com.chesssystem.util.DateUtil;
import com.chesssystem.util.HttpUtl;
import com.chesssystem.util.HttpUtl.HttpCallbackListener;
import com.chesssystem.util.ServerUrl;

import android.app.Activity;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 注册页面
 * @author lyg
 * @time 2016-7-4上午9:52:10
 */
public class RegisterActivity extends BaseActivity{
	private LinearLayout llBack;
	private TextView tvTitle;
	private TextView tvLogin;
	private Button btGetCode;
	private EditText etUserName;
	private EditText etCode;
	private EditText etPassword;
	private EditText etPasswordAgain;
	private Button btRegister;
	private String cookie=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initView();
	}

	private void initView() {
		llBack=(LinearLayout) findViewById(R.id.ll_back);
		llBack.setOnClickListener(this);
		tvTitle=(TextView) findViewById(R.id.tv_title);
		tvLogin=(TextView) findViewById(R.id.tv_finish);
		tvLogin.setOnClickListener(this);
		tvTitle.setText(R.string.register);
		tvLogin.setText(R.string.login);
		btGetCode=(Button) findViewById(R.id.bt_getCode);
		btGetCode.setOnClickListener(this);
		etUserName=(EditText) findViewById(R.id.et_account);
		etCode=(EditText) findViewById(R.id.et_code);
		etPassword=(EditText) findViewById(R.id.et_password);
		etPasswordAgain=(EditText) findViewById(R.id.et_password_again);
		btRegister=(Button) findViewById(R.id.bt_register);
		btRegister.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.ll_back:
			finish();
			break;
		case R.id.tv_finish:
			finish();
			break;
		case R.id.bt_getCode:
			if(!DateUtil.checkMobileNumber(etUserName.getText().toString())||etUserName.getText().toString().equals("")){
				Toast.makeText(this, "请输入正确手机号", Toast.LENGTH_SHORT).show();
			}else{
				getMessageCode(etUserName.getText().toString(),1);
			}
			break;
		case R.id.bt_register:
			if(!DateUtil.checkMobileNumber(etUserName.getText().toString())||etUserName.getText().toString().equals("")){
				Toast.makeText(this, "请输入正确手机号", Toast.LENGTH_SHORT).show();
			}else if(etCode.getText().toString().equals("")){
				Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
			}else if(etPassword.getText().toString().equals("")||etPasswordAgain.getText().toString().equals("")){
				Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
			}else if(!etPassword.getText().toString().equals(etPasswordAgain.getText().toString())){
				Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
			}else if(etPassword.getText().toString().length()<6){
				Toast.makeText(this, "请输入不少于6位的密码", Toast.LENGTH_SHORT).show();
			}else{
				register(etUserName.getText().toString(),etPassword.getText().toString(),etCode.getText().toString());
			}
			break;
		default:
			break;
		}
	}
	/**
	 * 获取短信验证码
	 * @param telephone
	 * @param type
	 */
	private void getMessageCode(final String telephone,final int type) {
		StringRequest stringRequest = new StringRequest(Method.POST,
				ServerUrl.getMessageCodeUrl, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							JSONObject jsonObject = new JSONObject(response);
							if (jsonObject.getInt("retCode") == 0) {
								CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(btGetCode, 60000, 1000);
								mCountDownTimerUtils.start();
							} else {
								Toast.makeText(RegisterActivity.this,
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
						Toast.makeText(RegisterActivity.this, error + "",
								Toast.LENGTH_SHORT).show();
					}
				}) {
					@Override
					protected Response<String> parseNetworkResponse(
							NetworkResponse response) {
				 try {
			          Map<String, String> responseHeaders = response.headers;
			          String rawCookies = responseHeaders.get("Set-Cookie");//cookie值
			          String[] cookievalues=rawCookies.split(";");
			          cookie=cookievalues[0];
			          String dataString = new String(response.data,"UTF-8");//返回值
			          return Response.success(dataString,HttpHeaderParser.parseCacheHeaders(response));
			      } catch (UnsupportedEncodingException e) {
			          return Response.error(new ParseError(e));
			      }
					}
			
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("username", telephone);
				map.put("actionType", String.valueOf(type));
				return map;
			}
		};
		NimApplication.getHttpQueues().add(stringRequest);
	}
	/**
	 * 注册
	 * @param telephone
	 * @param password
	 * @param code
	 */
	private void register(final String telephone,final String password,final String code) {
		StringRequest stringRequest = new StringRequest(Method.POST,
				ServerUrl.registerUrl, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							JSONObject jsonObject = new JSONObject(response);
							if (jsonObject.getInt("retCode") == 0) {
								Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
								new Handler().postDelayed(new Runnable() {
									@Override
									public void run() {
										finish();
									}
								},1000);
							} else {
								Toast.makeText(RegisterActivity.this,
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
						Toast.makeText(RegisterActivity.this, error + "",
								Toast.LENGTH_SHORT).show();
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("username", telephone);
				map.put("password", password);
				map.put("code", code);
				return map;
			}
			@Override
			public Map<String, String> getHeaders()
					throws AuthFailureError {
		 if(cookie!= null && cookie.length() > 0){  
               HashMap<String,String> headers = new HashMap<String, String>();  
               headers.put("Cookie",cookie);  
               return headers;  
     } 
				return super.getHeaders();
			}
		};
		NimApplication.getHttpQueues().add(stringRequest);
	}
	
	public static boolean  judgePassword(String str){   
        boolean isDigit = false;//定义一个boolean值，用来表示是否包含数字
        boolean isLetter = false;//定义一个boolean值，用来表示是否包含字母
        boolean isLength=false;
        for(int i=0 ; i<str.length() ; i++){ //循环遍历字符串   
            if(Character.isDigit(str.charAt(i))){     //用char包装类中的判断数字的方法判断每一个字符
                isDigit = true;   
            }
            if(Character.isLetter(str.charAt(i))){   //用char包装类中的判断字母的方法判断每一个字符
                isLetter = true;
            }
        }
        if(str.length()>=6){
        	isLength=true;
        }
        String regex = "^[a-zA-Z0-9]+$";
        return isLength&&isDigit&&isLetter&&str.matches(regex);
	}
}
