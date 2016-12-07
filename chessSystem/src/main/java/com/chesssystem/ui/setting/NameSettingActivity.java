package com.chesssystem.ui.setting;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.chesssystem.item.DaziItem;
import com.chesssystem.util.HttpUtl;
import com.chesssystem.util.ServerUrl;
import com.chesssystem.util.HttpUtl.HttpCallbackListener;
/**
 * 修改名称
 * @author lyg
 * @time 2016-7-4上午9:52:07
 */
public class NameSettingActivity extends BaseActivity {
	private LinearLayout llBack;
	private TextView tvTitle;
	private Button btFinish;
	private EditText edtName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_name);
		initView();
	}
	private void initView() {
		tvTitle=(TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.nicheng_setting);
		llBack=(LinearLayout) findViewById(R.id.ll_back);
		llBack.setOnClickListener(this);
		btFinish=(Button) findViewById(R.id.bt_confirm);
		btFinish.setOnClickListener(this);
		edtName=(EditText) findViewById(R.id.edt_setting_name);
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ll_back:
			finish();
			break;
		case R.id.bt_confirm:
			if(edtName.getText().toString().equals("")){
				Toast.makeText(NameSettingActivity.this, "请输入昵称", Toast.LENGTH_SHORT).show();
			}else{
				if (progressDialog == null) {
					progressDialog = new ProgressDialog(this);
					progressDialog.setCanceledOnTouchOutside(false);
				}
				progressDialog.show();
				modifyName(edtName.getText().toString());
			}
			
			break;
		default:
			break;
		}
	}
	private void modifyName(final String userName) {
		StringRequest stringRequest = new StringRequest(Method.POST,ServerUrl.postModifyInforUrl,  
                new Response.Listener<String>() {  
                    @Override  
                    public void onResponse(String response) { 
                    	try {
							JSONObject jsonObject = new JSONObject(response);
							if (progressDialog != null) {
								progressDialog.dismiss();
							}
							if(jsonObject.getInt("retCode")==0){
								Toast.makeText(NameSettingActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
								Editor editor = sharedPreferences.edit();
								editor.putString("nick",userName).commit();//修改数据库信息
								Intent intent = new Intent();
				                intent.putExtra("nick", userName);
								setResult(0,intent);
								finish();
							}else {
								Toast.makeText(NameSettingActivity.this,jsonObject.getString("retMsg"), Toast.LENGTH_SHORT).show();
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
						Toast.makeText(NameSettingActivity.this, error+"", Toast.LENGTH_SHORT).show();
                    }  
                }){
				@Override
				protected Map<String, String> getParams() throws AuthFailureError {
					Map<String, String> map = new HashMap<String, String>();
					map.put("userId",sharedPreferences.getString("userId", ""));
					map.put("nick", userName);
					return map;
				}
		}; 
		NimApplication.getHttpQueues().add(stringRequest);
	}

}
