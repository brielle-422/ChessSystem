package com.chesssystem.util;


import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chesssystem.NimApplication;
/**
 * 访问网络接口
 * @author lyg
 * @time 2016-6-30下午2:37:21
 */
public class HttpUtl {
	private static Toast toast = null;
	/**
	 * 判断网络状态--------service
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {   
        ConnectivityManager cm = (ConnectivityManager) context   
                .getSystemService(Context.CONNECTIVITY_SERVICE);   
        if (cm == null) {   
        } else {
            NetworkInfo[] info = cm.getAllNetworkInfo();   
            if (info != null) {   
                for (int i = 0; i < info.length; i++) {   
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {   
                        return true;   
                    }   
                }   
            }   
        }   
        return false;   
    } 
	/**
	 * GET请求
	 * @param url
	 * @param context
	 * @param listener
	 */
	public static void getHttp(final String url,final Context context,final HttpCallbackListener listener){
		/*
		 * 有网络才访问接口
		 */
		if(!isNetworkAvailable(context)){
			if (toast == null) { 
				toast = Toast.makeText(context, "网络不可用!", Toast.LENGTH_SHORT); 
				} else { 
				toast.setText("网络不可用!"); 
				} 
				toast.show(); 
		}
		StringRequest stringRequest = new StringRequest(Method.GET,
				url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
//						Log.d("sucess-----"+context, response);
						try {
							JSONObject jsonObject = new JSONObject(response);
							 if (jsonObject.getInt("retCode") == 0) {
								listener.onFinish(jsonObject);
							}else {
								listener.onFinishError(jsonObject);
								Toast.makeText(context,
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
						listener.onError(error);
//						Toast.makeText(context,
//								error.getMessage(),
//								Toast.LENGTH_SHORT).show();
//						Log.e("Volley-ERROR", error.getMessage(), error);
					}
				}) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Charset", "utf-8");
                headers.put("Content-Type", "application/x-javascript");
                headers.put("Accept-Encoding", "gzip,deflate");
                return headers;
            }
		};
		NimApplication.getHttpQueues().add(stringRequest);
	
	}
	public interface HttpCallbackListener {
		void onFinish(JSONObject response);
		void onFinishError(JSONObject response);
		void onError(VolleyError e);
}

}
