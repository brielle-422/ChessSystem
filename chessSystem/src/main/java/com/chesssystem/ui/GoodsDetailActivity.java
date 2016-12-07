package com.chesssystem.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.chesssystem.BaseActivity;
import com.chesssystem.NimApplication;
import com.chesssystem.R;
import com.chesssystem.banner.BannerItem;
import com.chesssystem.goods.FoodActivity;
import com.chesssystem.goods.FoodAdapter.OnChoiceNumChangeListener;
import com.chesssystem.item.GoodsItem;
import com.chesssystem.shoppingcar.CicleAddAndSubView;
import com.chesssystem.shoppingcar.ShoppingCarAdapter;
import com.chesssystem.shoppingcar.CicleAddAndSubView.OnNumChangeListener;
import com.chesssystem.shoppingcar.ShoppingCarAdapter.OnShoppingcarChangeListener;
import com.chesssystem.ui.order.SubmitOrderActivity;
import com.chesssystem.ui.setting.LoginActivity;
import com.chesssystem.util.DoubleToInt;
import com.chesssystem.util.HttpUtl;
import com.chesssystem.util.ServerUrl;
import com.chesssystem.util.HttpUtl.HttpCallbackListener;
import com.chesssystem.widget.MyDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author lyg
 * @time 2016-7-4上午9:51:21
 */
public class GoodsDetailActivity extends BaseActivity {
	private LinearLayout llBack;
	private TextView TvTitle;
	private ImageView ivPic;
	private TextView tvName;
	private TextView tvSales;
	private TextView tvPrice;
	private String goodsId;
	private String goodsName;
	private String goodsSales;
	private String goodsPrice;
	private String goodsImage;
	private int number;
	private MyDialog myDialog;
	private TextView tvPopContent;
	/**
	 * 购物车
	 */
	private CicleAddAndSubView addAndSubView;
	private ShoppingCarAdapter shoppingcarAdapter;
	private ListView lvShoppingcar;
	private LinearLayout llShoppingcarBg;
	private LinearLayout llShoppingCarBottom;
	private Button btPay;
	private TextView tvShoppingcarPrice;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goods_detail);
		initView();
		uploadGoodsDetail();
	}

	private void initView() {
		Intent intent = getIntent();
		goodsId = intent.getStringExtra("goodsId");
		number = intent.getIntExtra("number", 0);
		llBack = (LinearLayout) findViewById(R.id.ll_back);
		llBack.setOnClickListener(this);
		TvTitle = (TextView) findViewById(R.id.tv_title);
		TvTitle.setText(R.string.goods);
		ivPic = (ImageView) findViewById(R.id.iv_goodsdetail_image);
		tvName = (TextView) findViewById(R.id.tv_goodsdetail_name);
		tvPrice = (TextView) findViewById(R.id.tv_goodsdetail_price);
		tvSales = (TextView) findViewById(R.id.tv_goodsdetail_sales);

		/**
		 * 加减按钮
		 */
		addAndSubView = (CicleAddAndSubView) findViewById(R.id.bt_add_reduce);
		addAndSubView.setAutoChangeNumber(true);// 设置是否自增长
		addAndSubView.setNum(FoodActivity.foodList.get(number)
				.getChoiceNumber());// 设置默认值
		addAndSubView.setOnNumChangeListener(new OnNumChangeListener() {
			@Override
			public void onNumChange(View view, int stype, int num) {
				FoodActivity.foodList.get(number).setChoiceNumber(num);
				shoppingCarMsg();
			}
		});
		/**
		 * 购物车清单
		 */
		shoppingcarAdapter = new ShoppingCarAdapter(this,
				FoodActivity.shoppingcarList);
		lvShoppingcar = (ListView) findViewById(R.id.lv_shopppingcar);
		lvShoppingcar.setAdapter(shoppingcarAdapter);
		llShoppingCarBottom = (LinearLayout) findViewById(R.id.ll_shoppingcar_bottom);
		llShoppingCarBottom.setOnClickListener(this);
		btPay = (Button) findViewById(R.id.bt_goodsdetail_pay);
		btPay.setOnClickListener(this);
		llShoppingcarBg = (LinearLayout) findViewById(R.id.ll_shoppingcar_bg);
		llShoppingcarBg.setOnClickListener(this);
		tvShoppingcarPrice = (TextView) findViewById(R.id.tv_shoppingcar_price);
		shoppingCarMsg();
		shoppingcarAdapter
				.setOnShoppingcarChangeListener(new OnShoppingcarChangeListener() {
					@Override
					public void onShoppingcarNum() {
						shoppingCarMsg();
					}
				});

	}

	private void uploadGoodsDetail() {
		HttpUtl.getHttp(ServerUrl.getGoodsDetailUrl + "?goodsId=" + goodsId,
				this, new HttpCallbackListener() {
					@Override
					public void onFinish(JSONObject response) {
						try {
							JSONObject data = new JSONObject(response
									.getString("data"));
							goodsName = data.getString("goodsName");
							goodsPrice = data.getString("goodsPrice");
							goodsSales = data.getString("sale");
							goodsImage = ServerUrl.getPicUrl
									+ data.getString("goodsPic");
							Glide.with(getApplicationContext())
							.load(goodsImage.replace("getPic.do", "getPicBig"))
									.placeholder(R.color.gray2)
									.error(R.color.gray2)
									.skipMemoryCache( true )
									.centerCrop()
									.into(ivPic);
							tvName.setText(goodsName);
							tvPrice.setText(goodsPrice);
							tvSales.setText(goodsSales);
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
			Intent intent = new Intent();
			setResult(0, intent);
			finish();
			break;
		// 点击底部购物车栏
		case R.id.ll_shoppingcar_bottom:
			if (llShoppingcarBg.getVisibility() == View.GONE
					&& FoodActivity.shoppingcarList.size() > 0) {
				shoppingcarAdapter.notifyDataSetChanged();
				llShoppingcarBg.setVisibility(View.VISIBLE);
			} else {
				llShoppingcarBg.setVisibility(View.GONE);
			}
			break;
		case R.id.ll_shoppingcar_bg:
			llShoppingcarBg.setVisibility(View.GONE);
			break;
		// 点击结算
		case R.id.bt_goodsdetail_pay:
			if (!NimApplication.LOGINED) {
				popDialog("您还没有登录，请登录后再进行结算");
			} else {
				Intent intentPay = new Intent(GoodsDetailActivity.this,
						SubmitOrderActivity.class);
				intentPay.putExtra("from", "shop");
				intentPay.putExtra("orderType", "goods");
				intentPay.putExtra("storeId", FoodActivity.storeId);
				intentPay.putExtra("storeName", FoodActivity.storeName);
				intentPay.putExtra("goodsList",(Serializable)FoodActivity.shoppingcarList);
				startActivity(intentPay);
			}
			break;

		default:
			break;
		}
	}

	/**
	 * 获取购物车清单数据
	 */
	public void shoppingCarMsg() {
		addAndSubView.setNum(FoodActivity.foodList.get(number)
				.getChoiceNumber());// 设置默认值
		FoodActivity.shoppingcarList.clear();
		for (GoodsItem item : FoodActivity.foodList) {
			if (item.getChoiceNumber() > 0
					&& !FoodActivity.shoppingcarList.contains(item)) {
				FoodActivity.shoppingcarList.add(item);
			}

		}
		if (FoodActivity.shoppingcarList.size() > 0) {
			btPay.setVisibility(View.VISIBLE);
		} else {
			btPay.setVisibility(View.GONE);
			llShoppingcarBg.setVisibility(View.GONE);
		}
		double price = 0;
		for (GoodsItem goodsItem : FoodActivity.shoppingcarList) {
			price += goodsItem.getGoodsPrice() * goodsItem.getChoiceNumber();
		}
		Log.e("price", String.valueOf(price));
		tvShoppingcarPrice.setText(DoubleToInt.DoubleToInt(price));
	}
	public void popDialog(String content){
		myDialog = new MyDialog(this,
				R.style.inputDialog);
		tvPopContent=(TextView) myDialog.getContent();
		tvPopContent.setText(content);
		myDialog.setOnPositiveListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(GoodsDetailActivity.this, LoginActivity.class));
				myDialog.dismiss();
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
