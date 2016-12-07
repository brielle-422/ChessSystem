package com.chesssystem.ui.order;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.chesssystem.BaseActivity;
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

public class RefundDetailActivity extends BaseActivity {
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
	private TextView tvName;
	private TextView tvTelephone;
	private TextView tvPay;
	private TextView tvOrderNumber;
	private TextView tvOrderTime;
	private RelativeLayout rlStore;
	private String storeId;
	private ImageView ivRefundStatus;//退款状态图片
	private TextView tvRefundCancel;//退款取消
	private TextView tvRefunding;//退款处理中
	private TextView tvRefundSuccess;//退款成功
	private int refundStatus=0;//退款状态，3退款中 4退款成功 5退款失败
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_refund_detail);
		initView();
		uploadMsg();
	}
	private void initView() {
		llBack = (LinearLayout) findViewById(R.id.ll_back);
		llBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.refundDetail);
		ivRefundStatus=(ImageView) findViewById(R.id.iv_order_status);
		tvRefundCancel=(TextView) findViewById(R.id.tv_order_cancel);
		tvRefundSuccess=(TextView) findViewById(R.id.tv_order_success);
		tvRefunding=(TextView) findViewById(R.id.tv_order_deal);
		tvStoreName=(TextView) findViewById(R.id.tv_order_name);
		ivRoomPic=(ImageView) findViewById(R.id.iv_order_image);
		tvRoomName=(TextView) findViewById(R.id.tv_order_roomname);
		tvAllPrice=(TextView) findViewById(R.id.tv_order_money);
		lvGoods=(BaseListView) findViewById(R.id.lv_order_goods);
		rlRoom=(RelativeLayout) findViewById(R.id.rl_order_room);
		tvName=(TextView) findViewById(R.id.tv_order_username);
		tvTelephone=(TextView) findViewById(R.id.tv_order_telephone);
		tvPay=(TextView) findViewById(R.id.tv_order_payType);
		tvOrderNumber=(TextView) findViewById(R.id.tv_order_number);
		tvOrderTime=(TextView) findViewById(R.id.tv_order_creatTime);
		rlStore=(RelativeLayout) findViewById(R.id.rl_shop);
		arrayAdapter=new OrderGoodsAdapter(this, goodsItems);
		lvGoods.setAdapter(arrayAdapter);
	}
	private void uploadMsg() {
		HttpUtl.getHttp(ServerUrl.getOrderDetailUrl + "?orderId=" + getIntent().getStringExtra("orderId"),
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
							tvOrderTime.setText(DateUtil.deleteHours(jsonObject.getString("createTime")));
							tvOrderNumber.setText(jsonObject.getString("orderNumber"));
							storeId=jsonObject.getString("storeId");
							refundStatus=jsonObject.getInt("payStatus");//支付/退款状态
							if(refundStatus==3){
								ivRefundStatus.setImageResource(R.drawable.refund_icon_center);
								tvRefunding.setTextColor(getResources()
										.getColor(R.color.text_color_huangse_ff8800));
							}else if(refundStatus==4){
								ivRefundStatus.setImageResource(R.drawable.refund_icon_right);
								tvRefundSuccess.setTextColor(getResources()
										.getColor(R.color.text_color_huangse_ff8800));
							}else if(refundStatus==5){
								ivRefundStatus.setImageResource(R.drawable.refund_icon_left);
								tvRefundCancel.setTextColor(getResources()
										.getColor(R.color.text_color_huangse_ff8800));
							}
							rlStore.setOnClickListener(RefundDetailActivity.this);
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
								JSONObject object = (JSONObject) jsonArray
										.get(0);
								tvRoomName.setText(object.getString("goodsName"));
								Glide.with(RefundDetailActivity.this)
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
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ll_back:
			finish();
			break;
		case R.id.rl_shop:
			Intent intent = new Intent(RefundDetailActivity.this,
					MerchantsActivity.class);
			intent.putExtra("storeId",storeId);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
}
