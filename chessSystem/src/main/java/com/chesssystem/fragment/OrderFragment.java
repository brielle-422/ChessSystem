package com.chesssystem.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.chesssystem.MainActivity;
import com.chesssystem.NimApplication;
import com.chesssystem.R;
import com.chesssystem.adapter.OrderAdapter;
import com.chesssystem.adapter.OrderAdapter.OrderClickListener;
import com.chesssystem.item.OrderItem;
import com.chesssystem.ui.MerchantsActivity;
import com.chesssystem.ui.comment.CommentMyActivity;
import com.chesssystem.ui.comment.CommentPublishActivity;
import com.chesssystem.ui.order.OrderDetailActivity;
import com.chesssystem.ui.order.RefundActivity;
import com.chesssystem.ui.order.RefundDetailActivity;
import com.chesssystem.ui.order.SubmitOrderActivity;
import com.chesssystem.ui.setting.LoginActivity;
import com.chesssystem.util.HttpUtl;
import com.chesssystem.util.HttpUtl.HttpCallbackListener;
import com.chesssystem.util.ServerUrl;
import com.chesssystem.widget.MyDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
/**
 * 订单列表页面
 * @author lyg
 * @time 2016-7-4上午9:49:31
 */
public class OrderFragment extends Fragment implements OnClickListener {
	public static OrderFragment orderFragmentInstance;
	private static LinearLayout llLogin;
	private static PullToRefreshListView lvOrder;
	private static List<OrderItem> orderItems = new ArrayList<OrderItem>();
	private static ArrayAdapter<OrderItem> arrayAdapter;
	private static int msgPagerNumber = 1;
	private Button btLogin;
	private ProgressDialog progressDialog;

	public static OrderFragment getInstance() {
		orderFragmentInstance = new OrderFragment();
		return orderFragmentInstance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_order, container, false);
		initView(view);
		return view;
	}

	private void initView(View view) {
		llLogin = (LinearLayout) view.findViewById(R.id.ll_login);
		btLogin=(Button) view.findViewById(R.id.bt_order_login);
		btLogin.setOnClickListener(this);
		lvOrder = (PullToRefreshListView) view.findViewById(R.id.lv_order);
		lvOrder.setMode(Mode.BOTH);
		lvOrder.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载");
		arrayAdapter = new OrderAdapter(getActivity(), orderItems,mListener);
		lvOrder.setAdapter(arrayAdapter);
		lvOrder.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (refreshView.isShownHeader()) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							msgPagerNumber = 1;
							uploadMsg(msgPagerNumber);
							lvOrder.onRefreshComplete();// 停止刷新
						}
					}, 1000);
				}
				if (refreshView.isShownFooter()) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							uploadMsg(msgPagerNumber);
							lvOrder.onRefreshComplete();// 停止刷新
						}
					}, 1000);
				}
			}
		});
		lvOrder.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent intent=new Intent(getActivity(),OrderDetailActivity.class);
				intent.putExtra("orderId", orderItems.get(position-1).getOrderId());
				startActivity(intent);
			}
		});
	}

	public static void initListOrder() {
		if (NimApplication.LOGINED) {
			lvOrder.setVisibility(View.VISIBLE);
			llLogin.setVisibility(View.GONE);
			uploadMsg(msgPagerNumber);
		} else {
			lvOrder.setVisibility(View.GONE);
			llLogin.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 获取订单列表信息
	 * 
	 * @param numberPager
	 */
	public static void uploadMsg(final int numberPager) {
		HttpUtl.getHttp(ServerUrl.getOrderListUrl+ "?pageSize=15"+ "&userId="
				+ NimApplication.sharedPreferences.getString("userId","") + "&currentPage=" + numberPager,
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
														orderItems.clear();
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
														orderItems.add(orderItem);
													}
													msgPagerNumber++;
												}
												arrayAdapter.notifyDataSetChanged();
												lvOrder.onRefreshComplete();// 停止刷新
												} catch (JSONException e) {
													e.printStackTrace();
												}
											}
											
											@Override
											public void onError(VolleyError e) {
												lvOrder.onRefreshComplete();// 停止刷新
											}

											@Override
											public void onFinishError(
													JSONObject response) {
												lvOrder.onRefreshComplete();// 停止刷新
												
											}
										});
	}
	private  OrderClickListener mListener = new OrderClickListener() {
		@Override
		public void orderOnClick(int position, View v) {
			switch (v.getId()) {
				/**
				 * 订单item左边按钮
				 */
			case R.id.tv_order_left:
				//取消订单
				if(orderItems.get(position).getPayStatus()==1){
					popDialog("确定要取消该订单吗?",position);
				}
				//再来一单
				else{
					Intent submitOrderintent=new Intent(MainActivity.instance,SubmitOrderActivity.class);
					submitOrderintent.putExtra("from", "order");
					submitOrderintent.putExtra("orderId", orderItems.get(position).getOrderId());
					startActivity(submitOrderintent);
				}
				break;
				/**
				 * 订单item右边按钮
				 */
			case R.id.tv_order_right:
				//去支付
				if(orderItems.get(position).getPayStatus()==1){
					Intent submitOrderintent=new Intent(MainActivity.instance,SubmitOrderActivity.class);
					submitOrderintent.putExtra("from", "order");
					submitOrderintent.putExtra("orderId", orderItems.get(position).getOrderId());
					startActivity(submitOrderintent);
				}
				//查看退款
				else if(orderItems.get(position).getPayStatus()==3||
						orderItems.get(position).getPayStatus()==4||
						orderItems.get(position).getPayStatus()==5||
						orderItems.get(position).getStatus()==3){
					Intent intent=new Intent(getActivity(),RefundDetailActivity.class);
					intent.putExtra("orderId", orderItems.get(position).getOrderId());
					startActivity(intent);
				}else{
					//查看订单
					if(orderItems.get(position).getStatus()==1||
							orderItems.get(position).getStatus()==2){
						Intent intent=new Intent(getActivity(),OrderDetailActivity.class);
						intent.putExtra("orderId", orderItems.get(position).getOrderId());
						startActivity(intent);
					}
					//去评价
					else if(orderItems.get(position).getStatus()==4&&orderItems.get(position).isRate()==0){
						Intent intentComment=new Intent(getActivity(),CommentPublishActivity.class);
						intentComment.putExtra("orderId", orderItems.get(position).getOrderId());
						startActivity(intentComment);
					}
					//查看评价
					else if(orderItems.get(position).getStatus()==4&&orderItems.get(position).isRate()==1){
						startActivity(new Intent(getActivity(),CommentMyActivity.class));
					}
				}
				break;
				/**
				 * 进入店铺
				 */
			case R.id.ll_storeName:
				Intent intent = new Intent(getActivity(),
						MerchantsActivity.class);
				intent.putExtra("storeId",orderItems.get(position).getStoreId());
				startActivity(intent);
				break;

			default:
				break;
			}
		}
	};
	/**
	 * 删除订单
	 */
	public void deleteOrder(String orderId,final int position){
		HttpUtl.getHttp(ServerUrl.deleteOrderUrl+ "?orderId="+ orderId,
				MainActivity.instance,new HttpCallbackListener() {
											@Override
											public void onFinish(JSONObject response) {
												try {
													if (progressDialog != null) {
														progressDialog.dismiss();
													}
													orderItems.remove(position);
													arrayAdapter.notifyDataSetChanged();
													Toast.makeText(getActivity(), response.getString("retMsg"), Toast.LENGTH_SHORT).show();
												} catch (JSONException e) {
													e.printStackTrace();
												}
											}
											
											@Override
											public void onError(VolleyError e) {
												if (progressDialog != null) {
													progressDialog.dismiss();
												}
											}

											@Override
											public void onFinishError(
													JSONObject response) {
												if (progressDialog != null) {
													progressDialog.dismiss();
												}
												
											}
										});
	}
	/*
	 * 从不可见变为可见状态，更新数据
	 */
	@Override
	public void onResume() {
		super.onResume();
		if(msgPagerNumber>1){
			msgPagerNumber--;
		}
		initListOrder();
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.bt_order_login:
			startActivity(new Intent(getActivity(), LoginActivity.class));
			break;

		default:
			break;
		}
	}
	/**
	 * 取消订单提示框
	 */
	private MyDialog myDialog;
	private TextView tvPopContent;
	public void popDialog(String content,final int position){
		myDialog = new MyDialog(getActivity(),R.style.inputDialog);
		tvPopContent=(TextView) myDialog.getContent();
		tvPopContent.setText(content);
		myDialog.setOnPositiveListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				myDialog.dismiss();
				if (progressDialog == null) {
					progressDialog = new ProgressDialog(getActivity());
					progressDialog.setCanceledOnTouchOutside(false);
				}
				progressDialog.show();
				deleteOrder(orderItems.get(position).getOrderId(),position);
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
}
