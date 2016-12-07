package com.chesssystem.goods;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.chesssystem.BaseActivity;
import com.chesssystem.NimApplication;
import com.chesssystem.R;
import com.chesssystem.goods.FoodAdapter.OnChoiceNumChangeListener;
import com.chesssystem.goods.FoodAdapter.OnPinneChangeListener;
import com.chesssystem.item.GoodsItem;
import com.chesssystem.shoppingcar.ShoppingCarAdapter;
import com.chesssystem.shoppingcar.ShoppingCarAdapter.OnShoppingcarChangeListener;
import com.chesssystem.ui.GoodsDetailActivity;
import com.chesssystem.ui.MerchantsActivity;
import com.chesssystem.ui.order.SubmitOrderActivity;
import com.chesssystem.ui.setting.LoginActivity;
import com.chesssystem.util.DoubleToInt;
import com.chesssystem.util.HttpUtl;
import com.chesssystem.util.HttpUtl.HttpCallbackListener;
import com.chesssystem.util.ServerUrl;
import com.chesssystem.widget.MyDialog;

/**
 * 商品列表页面
 * @author lyg
 * @time 2016-7-4上午9:49:43
 */
public class FoodActivity extends BaseActivity {
	private static final String TAG = "MainActivity";
	private boolean isChange;
	private LinearLayout llBack;
	private TextView tvTitle;
	private List<String> types = new ArrayList<String>();
	private List<GoodsItem> goodsItems = new ArrayList<GoodsItem>();
	private MyDialog myDialog;
	private TextView tvPopContent;
	/**
	 * 购物车
	 */
	private ShoppingCarAdapter shoppingcarAdapter;
	private ListView lvShoppingcar;
	private LinearLayout llShoppingcarBg;
	private LinearLayout llShoppingCarBottom;
	private Button btPay;
	public static List<GoodsItem> shoppingcarList;
	private TextView tvPrice;
	/**
	 * 菜品数据集
	 */
	public static List<GoodsItem> foodList;
	/**
	 * 菜品类型数据集
	 */
	public List<FoodTypeModel> foodTypeList;
	/**
	 * 菜品类型位置集
	 */
	private List<Integer> foodTpyePositionList;
	private ListView lv_listTitle;
	private PinnedHeaderListView lv_Content;
	public static FoodAdapter foodAdapter;
	private TitleAdapter titleAdapter;
	public static String storeId;
	public static String storeName;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				FoodTypeModel foodTypeModel = (FoodTypeModel) msg.obj;
				int typePosition = foodTypeModel.getTypePosition();
				titleAdapter.setPos(typePosition);
				titleAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_goods);
		initView();
		uploadGoods();
	}

	private void initView() {
		Intent intent = getIntent();
		storeId = intent.getStringExtra("storeId");
		storeName=intent.getStringExtra("storeName");
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(storeName);// 标题
		llBack = (LinearLayout) findViewById(R.id.ll_back);
		llBack.setOnClickListener(this);
	}

	public void initFoodData() {
		/*
		 * 数据
		 */
		foodList = new ArrayList<GoodsItem>();
		foodTypeList = new ArrayList<FoodTypeModel>();
		foodTpyePositionList = new ArrayList<Integer>();
		int itemPosition = 0;// 每个item在list中的位置
		for (int i = 0; i < types.size(); i++) {
			foodTypeList.add(new FoodTypeModel(types.get(i), false,
					itemPosition, i));
			foodTpyePositionList.add(itemPosition);
			for (GoodsItem goodsItem : goodsItems) {
				String[] goodsType = splitIs(goodsItem.getGoodsType());
				for (int j = 0; j < goodsType.length; j++) {
					if (goodsType[j].equals(types.get(i))) {
						itemPosition++;
						foodList.add(goodsItem);
					}
				}
			}
		}
		/*
		 * UI
		 */
		lv_listTitle = (ListView) findViewById(R.id.lv_business_shop_food_orderfoods_foodTypes);
		lv_Content = (PinnedHeaderListView) findViewById(R.id.lv_business_shop_food_orderfoods_foods);
		foodAdapter = new FoodAdapter(getApplicationContext(), foodList, foodTypeList,
				foodTpyePositionList);
		lv_Content.setAdapter(foodAdapter);
		lv_Content.setOnScrollListener(foodAdapter);
		lv_Content.setPinnedHeaderView(LayoutInflater.from(this).inflate(
				R.layout.header_doublelistview, lv_Content, false));
		titleAdapter = new TitleAdapter(this, foodTypeList, lv_Content);
		lv_listTitle.setAdapter(titleAdapter);
		foodAdapter.setOnPinneChangeListener(new OnPinneChangeListener() {
			@Override
			public void onChange(FoodTypeModel foodTypeModel) {
				// 这个方法会一直走 所以用一个boolean值限制一下
				if (isChange) {
					Message message = handler.obtainMessage(1, foodTypeModel);
					handler.sendMessage(message);
				}
			}

			@Override
			public void onMyScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_FLING:
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					isChange = true;

					break;

				case OnScrollListener.SCROLL_STATE_IDLE:
					isChange = false;
					break;
				}

			}
		});
		/*
		 * 点击商品
		 */
		lv_Content.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(FoodActivity.this,
						GoodsDetailActivity.class);
				intent.putExtra("goodsId", foodList.get(arg2).getGoodsId());
				intent.putExtra("number",arg2);
				startActivityForResult(intent, 0);
			}
		});
		/*
		 * 点击分类
		 */
		lv_listTitle.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				FoodTypeModel model = titleAdapter.getItem(arg2);
				Integer itemPos = model.getItemPosition();
				lv_Content.setSelection(itemPos);
				titleAdapter.setPos(arg2);
				titleAdapter.notifyDataSetChanged();

			}

		});
		
		/**
		 * 购物车清单
		 */
		shoppingcarList=new ArrayList<GoodsItem>();
		shoppingcarAdapter=new ShoppingCarAdapter(this, shoppingcarList);
		lvShoppingcar=(ListView) findViewById(R.id.lv_shopppingcar);
		lvShoppingcar.setAdapter(shoppingcarAdapter);
		llShoppingCarBottom=(LinearLayout) findViewById(R.id.ll_shoppingcar_bottom);
		llShoppingCarBottom.setOnClickListener(this);
		btPay=(Button) findViewById(R.id.bt_goodsdetail_pay);
		btPay.setOnClickListener(this);
		llShoppingcarBg=(LinearLayout) findViewById(R.id.ll_shoppingcar_bg);
		llShoppingcarBg.setOnClickListener(this);
		tvPrice=(TextView) findViewById(R.id.tv_shoppingcar_price);
		foodAdapter.setOnChoiceNumChangeListener(new OnChoiceNumChangeListener() {
			@Override
			public void onChangeChoiceNum() {
				shoppingCarMsg();
				
			}
		});
		shoppingcarAdapter.setOnShoppingcarChangeListener(new OnShoppingcarChangeListener() {
			
			@Override
			public void onShoppingcarNum() {
				shoppingCarMsg();
			}
		});
	}

	public String[] splitIs(String goodsTypes) {
		String[] goodsType = goodsTypes.split(",");
		return goodsType;
	}

	private void uploadGoods() {
		HttpUtl.getHttp(ServerUrl.getGoodsListUrl + "?storeId=" + storeId,
				this, new HttpCallbackListener() {
					@Override
					public void onFinish(JSONObject response) {
						JSONArray jsonArray;
						try {
							jsonArray = response.getJSONArray("data");
							if (jsonArray.length() > 0) {
								for (int i = 0; i < jsonArray.length(); i++) {
									JSONObject object = (JSONObject) jsonArray
											.get(i);
									String[] type = splitIs(object.getString("goodsType"));
									GoodsItem goodsItem = new GoodsItem(
											object.getString("goodsId"),
											object.getString("goodsName"),
											ServerUrl.getPicUrl+ object.getString("goodsPic"),
											object.getInt("sale"), 		
											object.getDouble("goodsPrice"),
											object.getString("goodsType"), 
											0);
									goodsItems.add(goodsItem);
									for (int j = 0; j < type.length; j++) {
										if (!types.contains(type[j])) {
											types.add(type[j]);
										}
									}
								}
								initFoodData();
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
			//点击底部购物车栏
		case R.id.ll_shoppingcar_bottom:
			if(llShoppingcarBg.getVisibility()==View.GONE&&shoppingcarList.size()>0){
				shoppingcarAdapter.notifyDataSetChanged();
				llShoppingcarBg.setVisibility(View.VISIBLE);
			}else{
				llShoppingcarBg.setVisibility(View.GONE);
			}
			break;
			//点击结算
		case R.id.bt_goodsdetail_pay:
			if(!NimApplication.LOGINED){
				popDialog("您还没有登录，请登录后再进行结算");
			}else{
				Intent intent=new Intent(FoodActivity.this,SubmitOrderActivity.class);
				intent.putExtra("from", "shop");
				intent.putExtra("orderType", "goods");
				intent.putExtra("storeId", storeId);
				intent.putExtra("storeName", storeName);
				intent.putExtra("goodsList",(Serializable)shoppingcarList);
				startActivity(intent);
			}
			break;
		case R.id.ll_shoppingcar_bg:
			llShoppingcarBg.setVisibility(View.GONE);
			break;

		default:
			break;
		}
	}
	/**
	 * 获取购物车清单数据
	 */
	public void shoppingCarMsg(){
		shoppingcarList.clear();
		for(GoodsItem item:foodList){
			if(item.getChoiceNumber()>0&&!shoppingcarList.contains(item)){
				shoppingcarList.add(item);
			}
			
		}
		if(shoppingcarList.size()>0){
			btPay.setVisibility(View.VISIBLE);
		}else{
			btPay.setVisibility(View.GONE);
			llShoppingcarBg.setVisibility(View.GONE);
		}
		double price=0;
		for(GoodsItem goodsItem:shoppingcarList){
			price+=goodsItem.getGoodsPrice()*goodsItem.getChoiceNumber();
		}
		tvPrice.setText(DoubleToInt.DoubleToInt(price));
		foodAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 0:
			if (resultCode == 0) {
				shoppingCarMsg();
			}
			break;

		default:
			break;
		}
	}
	public void popDialog(String content){
		myDialog = new MyDialog(this,
				R.style.inputDialog);
		tvPopContent=(TextView) myDialog.getContent();
		tvPopContent.setText(content);
		myDialog.setOnPositiveListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(FoodActivity.this, LoginActivity.class));
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
