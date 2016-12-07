package com.chesssystem.ui.order;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.chesssystem.BaseActivity;
import com.chesssystem.MainActivity;
import com.chesssystem.NimApplication;
import com.chesssystem.R;
import com.chesssystem.adapter.OrderAdapter;
import com.chesssystem.adapter.RefundAdapter;
import com.chesssystem.item.OrderItem;
import com.chesssystem.util.HttpUtl;
import com.chesssystem.util.ServerUrl;
import com.chesssystem.util.HttpUtl.HttpCallbackListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
/**
 * 我的退款页面
 * @author Administrator
 *
 */
public class RefundActivity extends BaseActivity {
	private LinearLayout llBack;
	private TextView tvTitle;
	private  PullToRefreshListView lvRefund;
	private  List<OrderItem> refundItems = new ArrayList<OrderItem>();
	private  ArrayAdapter<OrderItem> arrayAdapter;
	private int msgPagerNumber=1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_refund);
		initView();
	}

	private void initView() {
		llBack = (LinearLayout) findViewById(R.id.ll_back);
		llBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.myRefund);
		lvRefund = (PullToRefreshListView)findViewById(R.id.lv_refund);
		lvRefund.setMode(Mode.BOTH);
		lvRefund.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载");
		arrayAdapter =new RefundAdapter(this, refundItems);
		lvRefund.setAdapter(arrayAdapter);
		lvRefund.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (refreshView.isShownHeader()) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							msgPagerNumber = 1;
							uploadMsg(msgPagerNumber);
							lvRefund.onRefreshComplete();// 停止刷新
						}
					}, 1000);
				}
				if (refreshView.isShownFooter()) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							uploadMsg(msgPagerNumber);
							lvRefund.onRefreshComplete();// 停止刷新
						}
					}, 1000);
				}
			}
		});
		lvRefund.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent intent=new Intent(RefundActivity.this,RefundDetailActivity.class);
				intent.putExtra("orderId", refundItems.get(position-1).getOrderId());
				startActivity(intent);
			}
		});
		uploadMsg(1);
	}
	/**
	 * 获取我的退款列表信息
	 * 
	 * @param numberPager
	 */
	public  void uploadMsg(final int numberPager) {
		HttpUtl.getHttp(ServerUrl.getOrderListUrl+ "?pageSize=15"+ "&userId="
				+ NimApplication.sharedPreferences.getString("userId","") + "&currentPage=" + numberPager
				+"&refound=1",
				MainActivity.instance,new HttpCallbackListener() {
											@Override
											public void onFinish(JSONObject response) {
												JSONArray jsonArray;
												try {
													jsonArray = response.getJSONArray("data");
													/*
													 * 如果有下拉刷新时，将清空数据的操作放在这里，防止数据越界
													 */
													if(numberPager==1){
														refundItems.clear();
													}
												if (jsonArray.length() > 0) {
													for (int i = 0; i < jsonArray.length(); i++) {
														JSONObject object = (JSONObject) jsonArray
																.get(i);
														OrderItem orderItem = new OrderItem(
																object.getString("orderId"),
																object.getString("orderNumber"),
																object.getString("storeId"),
																object.getString("storeName"),
																object.getString("createTime"),
																object.getDouble("orderPrice"),
																object.getInt("payStatus"),
																ServerUrl.getPicUrl+ object.getString("storePic"),
																object.getInt("status"), 
																object.getInt("isRate"));
														refundItems.add(orderItem);
													}
													msgPagerNumber++;
												}
												arrayAdapter.notifyDataSetChanged();
												lvRefund.onRefreshComplete();// 停止刷新
												} catch (JSONException e) {
													e.printStackTrace();
												}
											}
											
											@Override
											public void onError(VolleyError e) {
												lvRefund.onRefreshComplete();// 停止刷新
											}

											@Override
											public void onFinishError(
													JSONObject response) {
												lvRefund.onRefreshComplete();// 停止刷新
												
											}
										});
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ll_back:
			finish();
			break;

		default:
			break;
		}
	}

}
