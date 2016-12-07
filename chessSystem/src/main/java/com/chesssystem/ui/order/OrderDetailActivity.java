package com.chesssystem.ui.order;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.baidu.mapapi.map.Text;
import com.bumptech.glide.Glide;
import com.chesssystem.BaseActivity;
import com.chesssystem.MainActivity;
import com.chesssystem.R;
import com.chesssystem.adapter.OrderGoodsAdapter;
import com.chesssystem.item.GoodsItem;
import com.chesssystem.ui.MerchantsActivity;
import com.chesssystem.util.DateUtil;
import com.chesssystem.util.DoubleToInt;
import com.chesssystem.util.HttpUtl;
import com.chesssystem.util.ServerUrl;
import com.chesssystem.util.HttpUtl.HttpCallbackListener;
import com.chesssystem.widget.BaseListView;
import com.chesssystem.widget.MyDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

public class OrderDetailActivity extends BaseActivity {
	private LinearLayout llBack;
	private TextView tvTitle;
	private TextView tvStoreName;
	private ImageView ivRoomPic;
	private TextView tvRoomName;
	private TextView tvAllPrice;
	private BaseListView lvGoods;
	private ArrayAdapter<GoodsItem> arrayAdapter;
	private List<GoodsItem>goodsItems=new ArrayList<GoodsItem>();
	private RelativeLayout rlRoom;
	private RelativeLayout rlStore;
	private TextView tvName;
	private TextView tvTelephone;
	private TextView tvRoomTitle;
	private TextView tvRoomNumber;
	private TextView tvPay;
	private TextView tvOrderStutus;
	private Button btConfirm;
	private TextView tvRefund;
	private String storeId;
	private String orderId;
	private int payStatus;//支付状态
	private int status;//订单状态
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_detail);
		initView();
		uploadMsg();
	}

	private void uploadMsg() {
		HttpUtl.getHttp(ServerUrl.getOrderDetailUrl + "?orderId=" +orderId,
				this, new HttpCallbackListener() {
					@Override
					public void onFinish(JSONObject response) {
						 JSONObject jsonObject;
						try {
							jsonObject = response.getJSONObject("data");
							JSONArray jsonArray=jsonObject.getJSONArray("orderDetailBeans");
							tvStoreName.setText(jsonObject.getString("storeName"));
							tvName.setText(jsonObject.getString("username"));
							tvTelephone.setText(jsonObject.getString("telePhone"));
							storeId=jsonObject.getString("storeId");
							payStatus=jsonObject.getInt("payStatus");
							status=jsonObject.getInt("status");
							btConfirm.setVisibility(View.VISIBLE);
							btConfirm.setOnClickListener(OrderDetailActivity.this);
//							tvRefund.setOnClickListener(OrderDetailActivity.this);
							tvRefund.setVisibility(View.GONE);
							rlStore.setOnClickListener(OrderDetailActivity.this);
							switch (payStatus) {
							case 1:
								btConfirm.setText("取消订单");
								tvOrderStutus.setText("未支付");
								break;
							case 2:
								btConfirm.setText("再来一单");
								if(status==1){
									tvRefund.setVisibility(View.VISIBLE);
									tvOrderStutus.setText("未接单");
								}else if(status==2){
									tvRefund.setVisibility(View.VISIBLE);
									tvOrderStutus.setText("已接单");
								}else if(status==3){
									tvOrderStutus.setText("退款中");
								}else if(status==4){
									tvOrderStutus.setText("订单完成");
								}
								break;
							case 3:
								tvOrderStutus.setText("退款中");
								btConfirm.setText("再来一单");
								break;
							case 4:
								tvOrderStutus.setText("退款成功");
								btConfirm.setText("再来一单");
								break;
							case 5:
								tvOrderStutus.setText("退款失败");
								btConfirm.setText("再来一单");
								break;

							default:
								break;
							}
							if(jsonObject.getInt("payType")==1){
								tvPay.setText("在线支付(支付宝)");
							}else{
								tvPay.setText("在线支付(微信)");
							}
							tvAllPrice.setText(DoubleToInt.DoubleToInt(jsonObject.getDouble("orderPrice")));
							/*
							 * 该订单为商品
							 */
							if(jsonObject.getInt("goodsType")==1){
								rlRoom.setVisibility(View.GONE);	
								lvGoods.setVisibility(View.VISIBLE);
								tvRoomNumber.setText(jsonObject.getString("roomNumber"));
								goodsItems.clear();
								for (int i = 0; i < jsonArray.length(); i++) {
									JSONObject object = (JSONObject) jsonArray
											.get(i);
									GoodsItem goodsItem=new GoodsItem(
											"",
											object.getString("goodsName"),
											"",
											0, 
											object.getDouble("price"),
											"", 
											object.getInt("buyCount"));
									goodsItems.add(goodsItem);
								}
								arrayAdapter.notifyDataSetChanged();
							}
							/*
							 * 该订单为棋牌室
							 */
							else{
								rlRoom.setVisibility(View.VISIBLE);	
								lvGoods.setVisibility(View.GONE);
								tvRoomTitle.setText("到店时间:");
								tvRoomNumber.setText(DateUtil.TimeToChange(jsonObject.getString("orderDate")));
								JSONObject object = (JSONObject) jsonArray
										.get(0);
								tvRoomName.setText(object.getString("goodsName"));
								Glide.with(OrderDetailActivity.this)
								.load(ServerUrl.getPicUrl+object.getString("roomPic"))
								.placeholder(R.color.gray2)
								.error(R.color.gray2)
								.skipMemoryCache( true )
								.centerCrop()
								.into(ivRoomPic);
							}
						
						
						
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onError(VolleyError e) {
					}

					@Override
					public void onFinishError(JSONObject response) {
					}
				});
	}

	private void initView() {
		orderId=getIntent().getStringExtra("orderId");
		llBack=(LinearLayout) findViewById(R.id.ll_back);
		llBack.setOnClickListener(this);
		tvTitle=(TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.orderdetail);
		tvRefund=(TextView) findViewById(R.id.tv_finish);
		tvRefund.setText(R.string.refund);
		tvRefund.setVisibility(View.GONE);
		tvRefund.setOnClickListener(this);
		tvOrderStutus=(TextView) findViewById(R.id.tv_order_status);
		tvStoreName=(TextView) findViewById(R.id.tv_order_name);
		ivRoomPic=(ImageView) findViewById(R.id.iv_order_image);
		tvRoomName=(TextView) findViewById(R.id.tv_order_roomname);
		tvAllPrice=(TextView) findViewById(R.id.tv_order_money);
		lvGoods=(BaseListView) findViewById(R.id.lv_order_goods);
		rlRoom=(RelativeLayout) findViewById(R.id.rl_order_room);
		tvName=(TextView) findViewById(R.id.tv_order_username);
		rlStore=(RelativeLayout) findViewById(R.id.rl_store);
		tvTelephone=(TextView) findViewById(R.id.tv_order_telephone);
		tvRoomTitle=(TextView) findViewById(R.id.tv_order_roomnumber_title);
		tvRoomNumber=(TextView) findViewById(R.id.tv_order_roomnumber);
		btConfirm=(Button) findViewById(R.id.bt_confirm);
		tvPay=(TextView) findViewById(R.id.tv_order_pay);
		arrayAdapter=new OrderGoodsAdapter(this, goodsItems);
		lvGoods.setAdapter(arrayAdapter);
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			//返回按钮
		case R.id.ll_back:
			finish();
			break;
		case R.id.bt_confirm:
			if(payStatus==1){
				popDialog("确定要取消该订单吗?",0);
			}else{
				/*
				 * 再来一单
				 */
				Intent intent=new Intent(OrderDetailActivity.this,SubmitOrderActivity.class);
				intent.putExtra("from", "order");
				intent.putExtra("orderId",orderId);
				startActivity(intent);
			}
			break;
		case R.id.tv_finish:
			popDialog("确定要申请退款?",1);
			break;
		case R.id.rl_store:
			Intent intent = new Intent(OrderDetailActivity.this,
					MerchantsActivity.class);
			intent.putExtra("storeId",storeId);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
	/**
	 * 退款申请
	 */
	public void refund(String orderId){
		HttpUtl.getHttp(ServerUrl.refundUrl+ "?orderId="+ orderId,
				OrderDetailActivity.this,new HttpCallbackListener() {
											@Override
											public void onFinish(JSONObject response) {
												try {
													if (progressDialog != null) {
														progressDialog.dismiss();
													}
													uploadMsg();
													Toast.makeText(OrderDetailActivity.this, response.getString("retMsg"), Toast.LENGTH_SHORT).show();
												} catch (JSONException e) {
													e.printStackTrace();
												}
											}
											
											@Override
											public void onError(VolleyError e) {
												if (progressDialog != null) {
													progressDialog.dismiss();
												}
												Toast.makeText(OrderDetailActivity.this, "退款申请失败", Toast.LENGTH_SHORT).show();
											}

											@Override
											public void onFinishError(
													JSONObject response) {
												if (progressDialog != null) {
													progressDialog.dismiss();
												}
//												Toast.makeText(OrderDetailActivity.this, "退款申请失败", Toast.LENGTH_SHORT).show();
											}
										});
	}
	/**
	 * 删除订单
	 */
	public void deleteOrder(String orderId){
		HttpUtl.getHttp(ServerUrl.deleteOrderUrl+ "?orderId="+ orderId,
				MainActivity.instance,new HttpCallbackListener() {
											@Override
											public void onFinish(JSONObject response) {
												try {
													if (progressDialog != null) {
														progressDialog.dismiss();
													}
													Toast.makeText(OrderDetailActivity.this, response.getString("retMsg"), Toast.LENGTH_SHORT).show();
													finish();
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
	/**
	 * 取消订单提示框
	 */
	private MyDialog myDialog;
	private TextView tvPopContent;
	public void popDialog(String content,final int x){
		myDialog = new MyDialog(OrderDetailActivity.this,R.style.inputDialog);
		tvPopContent=(TextView) myDialog.getContent();
		tvPopContent.setText(content);
		myDialog.setOnPositiveListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				myDialog.dismiss();
				if (progressDialog == null) {
					progressDialog = new ProgressDialog(OrderDetailActivity.this);
					progressDialog.setCanceledOnTouchOutside(false);
				}
				progressDialog.show();
				if(x==0){
				deleteOrder(orderId);
				}else{
					refund(orderId);
				}
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
