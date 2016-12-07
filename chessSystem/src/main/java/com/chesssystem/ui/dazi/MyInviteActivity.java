package com.chesssystem.ui.dazi;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.volley.VolleyError;
import com.chesssystem.BaseActivity;
import com.chesssystem.NimApplication;
import com.chesssystem.R;
import com.chesssystem.adapter.InviteAdapter;
import com.chesssystem.adapter.RecordAdapter;
import com.chesssystem.item.CommentItem;
import com.chesssystem.item.DaziItem;
import com.chesssystem.item.DaziUserItem;
import com.chesssystem.util.HttpUtl;
import com.chesssystem.util.ServerUrl;
import com.chesssystem.util.HttpUtl.HttpCallbackListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
/**
 * 
 * @author lyg
 * @time 2016-7-4上午9:51:41
 */
public class MyInviteActivity extends BaseActivity {
	private LinearLayout llBack;
	private TextView tvTitle;
	private TextView tvPublishInvite;
	private TextView tvAccountInvites;
	private PullToRefreshListView lvInvite;
	private List<DaziItem> daziItems = new ArrayList<DaziItem>();
	private ArrayAdapter<DaziItem> arrayAdapter;
	private String url;
	private int msgPagerNumber = 1;
	private TextView tvText;//个邀约  文本
	private int type;//0代表我的邀约页面；1代表我的应约页面
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myinvite);
		initView();
		initDate();
	}
	private void initDate() {
		Intent intent=getIntent();
		type=intent.getIntExtra("stype", 0);
		if(type==0){
			tvTitle.setText(R.string.myinvite);
			tvText.setText("个邀约");
			url=ServerUrl.getInvitesListUrl+"?type=1&pageSize=15&userId="+sharedPreferences.getString("userId", "");
		}else{
			tvTitle.setText(R.string.myyingyue);
			tvText.setText("个应约");
			url=ServerUrl.getInvitesListUrl+"?type=2&pageSize=15&userId="+sharedPreferences.getString("userId", "");
		}
		uploadMsg(1);
	}
	private void uploadMsg(final int numberPager) {
			HttpUtl.getHttp(url+"&currentPage="
					+ numberPager, this, new HttpCallbackListener() {
				@Override
				public void onFinish(JSONObject response) {
					try {
						JSONObject jsonObject = new JSONObject(response
								.getString("data"));
						tvAccountInvites.setText(jsonObject.getString("count"));
						JSONArray jsonArray = jsonObject
								.getJSONArray("lugBeans");
						/*
						 * 如果有下拉刷新时，将清空数据的操作放在这里，防止数据越界
						 */
						if(numberPager==1){
							daziItems.clear();
						}
						if (jsonArray.length() > 0) {
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject object = (JSONObject) jsonArray
										.get(i);
								DaziItem daziItem=new DaziItem(object.getString("lugId"),
										object.getString("orderTime"), 
										object.getString("lugAddress"),
										object.getInt("memberCount"),
										object.getString("lugContent"),
										object.getString("fromToday"));
								daziItems.add(daziItem);
							}
							msgPagerNumber++;
						}
						arrayAdapter.notifyDataSetChanged();
						lvInvite.onRefreshComplete();// 停止刷新
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onError(VolleyError e) {
					lvInvite.onRefreshComplete();// 停止刷新
				}

				@Override
				public void onFinishError(JSONObject response) {
					lvInvite.onRefreshComplete();// 停止刷新
					
				}
			});

	}
	private void initView() {
		llBack=(LinearLayout) findViewById(R.id.ll_back);
		llBack.setOnClickListener(this);
		tvTitle=(TextView) findViewById(R.id.tv_title);
		tvPublishInvite=(TextView) findViewById(R.id.tv_finish);
		tvPublishInvite.setText(R.string.invite);
		tvPublishInvite.setOnClickListener(this);
		tvAccountInvites=(TextView) findViewById(R.id.tv_myinvite_account);
		tvAccountInvites.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
		tvText=(TextView) findViewById(R.id.tv_myinvite_text);
		lvInvite = (PullToRefreshListView) findViewById(R.id.lv_myinvite);
		lvInvite.setMode(Mode.BOTH);
		arrayAdapter = new InviteAdapter(this, daziItems);
		lvInvite.setAdapter(arrayAdapter);
		lvInvite.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent intent=new Intent(MyInviteActivity.this,DaziDetailActivity.class);
				intent.putExtra("lugId", daziItems.get(position-1).getId());
				startActivity(intent);
			}
		});
		/**
		 * 上拉、下拉刷新
		 */
		lvInvite.setOnRefreshListener(new OnRefreshListener<ListView>() {
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
}
