package com.chesssystem.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.chesssystem.BaseActivity;
import com.chesssystem.NimApplication;
import com.chesssystem.R;
import com.chesssystem.adapter.MainAdapter;
import com.chesssystem.item.MainItem;
import com.chesssystem.util.DateUtil;
import com.chesssystem.util.HttpUtl;
import com.chesssystem.util.ServerUrl;
import com.chesssystem.util.HttpUtl.HttpCallbackListener;
import com.chesssystem.widget.ClearEditText;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

public class SearchActivity extends BaseActivity {
	private LinearLayout llBack;
	private TextView tvTitle;
	private PullToRefreshListView lvMall;
	private List<MainItem> mainItems = new ArrayList<MainItem>();
	private ArrayAdapter<MainItem> arrayAdapter;
	private ClearEditText edtInput;
	private TextView tvConfirm;
	private int msgPagerNumber = 1;
	private String searchName=""; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		initView();
	}
	private void initView() {
		llBack = (LinearLayout) findViewById(R.id.ll_back);
		llBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText("搜索");	
		tvConfirm=(TextView) findViewById(R.id.tv_confirm);
		tvConfirm.setOnClickListener(this);
		edtInput=(ClearEditText) findViewById(R.id.et_input);
		edtInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
			}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}
			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});
		lvMall = (PullToRefreshListView)findViewById(R.id.lv_search);
		lvMall.setMode(Mode.BOTH);
		arrayAdapter = new MainAdapter(SearchActivity.this, mainItems);
		lvMall.setAdapter(arrayAdapter);
		lvMall.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (refreshView.isShownHeader()) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							msgPagerNumber = 1;
							uploadMsg(searchName,msgPagerNumber);
						}
					}, 1000);
				}
				if (refreshView.isShownFooter()) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							uploadMsg(searchName,msgPagerNumber);
						}
					}, 1000);
				}
			}
		});
		lvMall.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent intent = new Intent(SearchActivity.this,
						MerchantsActivity.class);
				intent.putExtra("storeId", mainItems.get(position - 1)
						.getStoreId());							
				startActivity(intent);
			}
		});
	}
	/**
	 * 获取商家列表信息
	 * 
	 * @param numberPager
	 */
	public void uploadMsg(final String storeName,final int numberPager) {
		HttpUtl.getHttp(ServerUrl.getStoreListUrl+"&storeName="+DateUtil.setUTF8(storeName)+"&currentPage=" + numberPager+"&pageSize=15",this,
				new HttpCallbackListener() {
					@Override
					public void onFinish(JSONObject response) {
						JSONArray jsonArray;
						try {
							jsonArray = response.getJSONArray("data");
							/*
							 * 如果有下拉刷新时，将清空数据的操作放在这里，防止数据越界
							 */
							if (numberPager == 1) {
								mainItems.clear();
							}	
							if (jsonArray.length() > 0) {
								for (int i = 0; i < jsonArray.length(); i++) {
									JSONObject object = (JSONObject) jsonArray
											.get(i);
									MainItem mainItem = new MainItem(
											object.getString("storeId"),
											object.getString("storeName"),
											object.getString("storeDes"),
											ServerUrl.getPicUrl+ object.getString("storePic"),
											object.getDouble("distance"),
											object.getString("storeAdd"));
									mainItems.add(mainItem);
								}
								msgPagerNumber++;
							}
							arrayAdapter.notifyDataSetChanged();
							lvMall.onRefreshComplete();// 停止刷新
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onError(VolleyError e) {
						lvMall.onRefreshComplete();// 停止刷新
					}

					@Override
					public void onFinishError(JSONObject response) {
						lvMall.onRefreshComplete();// 停止刷新
					}
				});
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ll_back:
			finish();
			//强制收起输入法
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
			break;
		case R.id.tv_confirm:
			msgPagerNumber=1;
			searchName=edtInput.getText().toString();
			if(!searchName.equals("")){
			uploadMsg(searchName,msgPagerNumber);//获取商家数据
			}
			break;
		default:
			break;
		}
	}

}
