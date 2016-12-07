package com.chesssystem.ui.dazi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.chesssystem.BaseActivity;
import com.chesssystem.NimApplication;
import com.chesssystem.R;
import com.chesssystem.adapter.DaziUserAdapter;
import com.chesssystem.adapter.RecordAdapter;
import com.chesssystem.adapter.DaziUserAdapter.DaziUserClickListener;
import com.chesssystem.adapter.RecordAdapter.RecordClickListener;
import com.chesssystem.item.DaziItem;
import com.chesssystem.item.DaziUserItem;
import com.chesssystem.ui.setting.SettingActivity;
import com.chesssystem.util.HttpUtl;
import com.chesssystem.util.ServerUrl;
import com.chesssystem.util.HttpUtl.HttpCallbackListener;
import com.chesssystem.widget.RateDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
/**
 * 
 * @author lyg
 * @time 2016-7-4上午9:51:50
 */
public class RecordActivity extends BaseActivity {
	private LinearLayout llBack;
	private TextView tvTitle;
	private TextView tvPublishInvite;
	private TextView tvAccountRecord;
	private PullToRefreshListView lvRecord;
	private List<DaziUserItem> recordItems = new ArrayList<DaziUserItem>();
	private ArrayAdapter<DaziUserItem> arrayAdapter;
	private RateDialog rateDialog;
	private TextView goodRage;
    private TextView midRate;
    private TextView lowRate;
    private int msgPagerNumber = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);
		initView();
		initDate();
	}

	private void initDate() {
		uploadMsg(1);		
	}

	private void initView() {
		llBack=(LinearLayout) findViewById(R.id.ll_back);
		llBack.setOnClickListener(this);
		tvTitle=(TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.dazi_record);
		tvPublishInvite=(TextView) findViewById(R.id.tv_finish);
		tvPublishInvite.setText(R.string.invite);
		tvPublishInvite.setOnClickListener(this);
		tvAccountRecord=(TextView) findViewById(R.id.tv_record_account);
		tvAccountRecord.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
		
		lvRecord = (PullToRefreshListView) findViewById(R.id.lv_record);
		lvRecord.setMode(Mode.BOTH);
		arrayAdapter = new RecordAdapter(this, recordItems,mListener);
		lvRecord.setAdapter(arrayAdapter);
		lvRecord.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent intent=new Intent(RecordActivity.this,DaziDetailActivity.class);
				intent.putExtra("lugId", recordItems.get(position-1).getLugId());
				startActivity(intent);
			}
		});
		/**
		 * 上拉、下拉刷新
		 */
		lvRecord.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (refreshView.isShownHeader()) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							msgPagerNumber = 1;
							uploadMsg(msgPagerNumber);
						}
					}, 1000);
				}
				if (refreshView.isShownFooter()) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							uploadMsg(msgPagerNumber);
						}
					}, 1000);
				}
			}
		});
	}
	private void uploadMsg(final int numberPager) {
		HttpUtl.getHttp(ServerUrl.getRecordListUrl+"?userId="
				+ sharedPreferences.getString("userId", "")+"&currentPage="
						+ numberPager+"&pageSize=15", this, new HttpCallbackListener() {
			@Override
			public void onFinish(JSONObject response) {
				try {
					JSONObject jsonObject = new JSONObject(response
							.getString("data"));
					tvAccountRecord.setText(jsonObject.getString("count"));
					JSONArray jsonArray = jsonObject
							.getJSONArray("lugBeans");
					/*
					 * 如果有下拉刷新时，将清空数据的操作放在这里，防止数据越界
					 */
					if(numberPager==1){
						recordItems.clear();
					}
					if (jsonArray.length() > 0) {
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject object = (JSONObject) jsonArray
									.get(i);
							DaziUserItem daziUserItem=new DaziUserItem(
									object.getString("userId"),
									object.getString("lugId"),
									ServerUrl.getPicUrl+object.getString("userPic"),
									object.getString("nick"),
									object.getInt("sex"),
									object.getString("birthday"),
									object.getString("telephone"),
									object.getInt("goodRate"),
									object.getInt("midRate"),
									object.getInt("badRate"),
									object.getInt("isRate"));
							recordItems.add(daziUserItem);
						}
						msgPagerNumber++;
					}
					arrayAdapter.notifyDataSetChanged();
					lvRecord.onRefreshComplete();// 停止刷新
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(VolleyError e) {
				lvRecord.onRefreshComplete();// 停止刷新
			}

			@Override
			public void onFinishError(JSONObject response) {
				lvRecord.onRefreshComplete();// 停止刷新
				
			}
		});

}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ll_back:
			finish();
			break;
			//点击发邀约
		case R.id.tv_finish:
			startActivity(new Intent(this,InviteActivity.class));
			break;
		default:
			break;
		}
	}
	/**
	 * 弹出评价选项框
	 */
	private  RecordClickListener mListener = new RecordClickListener() {
		@Override
		public void myOnClick(final int position, View v) {
			rateDialog = new RateDialog(RecordActivity.this,
					R.style.inputDialog);
			goodRage=(TextView) rateDialog.getGoodRate();
			midRate=(TextView) rateDialog.getMidRate();
			lowRate=(TextView) rateDialog.getLowRate();
			goodRage.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					rateUser(recordItems.get(position).getUserId(),
							recordItems.get(position).getLugId(),
							"1","0","0");
					rateDialog.dismiss();
				}
			});
			midRate.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					rateUser(recordItems.get(position).getUserId(),
							recordItems.get(position).getLugId(),
							"0","1","0");
				}
			});
			lowRate.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					rateUser(recordItems.get(position).getUserId(),
							recordItems.get(position).getLugId(),
							"0","0","1");
					
				}
			});
			
			rateDialog.show();
		}
	};
	/*
	 * 评价搭主接口
	 */
	private void rateUser(final String userId,final String lugId,  final String goodRate,final String midRate,final String badRate) {
		StringRequest stringRequest = new StringRequest(Method.POST,
				ServerUrl.postRateDaziUserUrl, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							JSONObject jsonObject = new JSONObject(response);
							if (jsonObject.getInt("retCode") == 0) {
								Toast.makeText(RecordActivity.this, "保存成功",
										Toast.LENGTH_SHORT).show();
								rateDialog.dismiss();
								uploadMsg(1);
							} else {
								Toast.makeText(RecordActivity.this,
										jsonObject.getString("retMsg"),
										Toast.LENGTH_SHORT).show();
								rateDialog.dismiss();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("false", error.getMessage(), error);
						rateDialog.dismiss();
						Toast.makeText(RecordActivity.this, error + "",
								Toast.LENGTH_SHORT).show();
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("raterId", sharedPreferences.getString("userId", ""));
				map.put("lugId", lugId);
				map.put("userId", userId);
				map.put("goodRate", goodRate);
				map.put("midRate", midRate);
				map.put("badRate", badRate);
				return map;
			}
		};
		NimApplication.getHttpQueues().add(stringRequest);
	}
}
