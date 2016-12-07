package com.chesssystem.ui.setting;

import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chesssystem.BaseActivity;
import com.chesssystem.NimApplication;
import com.chesssystem.R;
import com.chesssystem.R.id;
import com.chesssystem.R.layout;
import com.chesssystem.R.string;
import com.chesssystem.fragment.MyFragment;
import com.chesssystem.fragment.OrderFragment;
import com.chesssystem.util.ServerUrl;
/**
 * 登录页面
 * @author lyg
 * @time 2016-7-4上午9:52:04
 */
public class LoginActivity extends BaseActivity {
	private TextView tvRegister;
	private LinearLayout llBack;
	private TextView tvTitle;
	private Button tvLogin;
	private EditText etAccount;
	private EditText etPassword;
	private ProgressDialog progressDialog;
	private TextView tvForget;
	View view;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view=getLayoutInflater().inflate(R.layout.activity_login,null);
		setContentView(view);
		initView();
	}

	private void initView() {
		tvRegister=(TextView) findViewById(R.id.tv_finish);
		tvRegister.setText(R.string.register);
		tvRegister.setOnClickListener(this);
		tvTitle=(TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.login);
		tvLogin=(Button) findViewById(R.id.bt_login);
		tvLogin.setOnClickListener(this);
		etAccount=(EditText) findViewById(R.id.et_account);
		etPassword=(EditText) findViewById(R.id.et_password);
		llBack=(LinearLayout) findViewById(R.id.ll_back);
		llBack.setOnClickListener(this);
		tvForget=(TextView) findViewById(R.id.tv_forget_password);
		tvForget.setOnClickListener(this);
    }

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.tv_finish:
			startActivity(new Intent(this,RegisterActivity.class));
			break;
		case R.id.bt_login:
			login();
			break;
		case R.id.ll_back:
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
			finish();
			break;
		case R.id.tv_forget_password:
			startActivity(new Intent(this,ReviseActivity.class));
			break;
		default:
			break;
		}
	}

	private void login() {
		if(etAccount.getText().toString().equals("")||etPassword.getText().toString().equals("")){
			Toast.makeText(LoginActivity.this, "账号密码不能为空!", Toast.LENGTH_SHORT).show();
		}else{
			if (progressDialog == null) {
				progressDialog = new ProgressDialog(this);
				progressDialog.setMessage("登录中...");
				progressDialog.setCanceledOnTouchOutside(false);
			}
			progressDialog.show();
//			RequestQueue mQueue = Volley.newRequestQueue(this);
			StringRequest stringRequest = new StringRequest(Method.POST,ServerUrl.loginUrl,  
                    new Response.Listener<String>() {  
                        @Override  
                        public void onResponse(String response) { 
                        	Log.d("sucess",response);  
                        	try {
								JSONObject jsonObject = new JSONObject(response);
								if (progressDialog != null) {
									progressDialog.dismiss();
								}
//								imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
								if(jsonObject.getInt("retCode")==0){
									//强制收起输入法
									imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
									JSONObject jsonObject1 = new JSONObject(jsonObject.getString("data"));
									Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
									NimApplication.LOGINED=true;
									SharedPreferences.Editor editor=sharedPreferences.edit();
									editor.putString("account", jsonObject1.getString("username"));
									editor.putString("password", jsonObject1.getString("password"));
									editor.putString("userId", jsonObject1.getString("userId"));
									editor.putString("nick",jsonObject1.getString("nick"));
									editor.putInt("sex",jsonObject1.getInt("sex"));
									editor.putString("birthday",jsonObject1.getString("birthday"));
									editor.putString("userPic",ServerUrl.getPicUrl
											+ jsonObject1.getString("userPic"));
									editor.commit();
//									if(MyFragment.myFragmentInstance.isAdded())
//										MyFragment.initInfor();
//									if(OrderFragment.orderFragmentInstance.isAdded())
//										OrderFragment.initListOrder();
								finish();
									
								}else {
									Toast.makeText(LoginActivity.this,jsonObject.getString("retMsg"), Toast.LENGTH_SHORT).show();
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
							Toast.makeText(LoginActivity.this, error+"", Toast.LENGTH_SHORT).show();
                        }  
                    }){
				
					@Override
					protected Map<String, String> getParams() throws AuthFailureError {
						Map<String, String> map = new HashMap<String, String>();
						map.put("username",  etAccount.getText().toString());
						map.put("password",  etPassword.getText().toString());
						return map;
					}
			}; 
			NimApplication.getHttpQueues().add(stringRequest);
//			mQueue.add(stringRequest);
		}
	}
}
