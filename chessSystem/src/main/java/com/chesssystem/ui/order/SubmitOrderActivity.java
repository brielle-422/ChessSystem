package com.chesssystem.ui.order;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.sdk.app.PayTask;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.chesssystem.BaseActivity;
import com.chesssystem.NimApplication;
import com.chesssystem.R;
import com.chesssystem.adapter.OrderGoodsAdapter;
import com.chesssystem.alipay.AliPayUtils;
import com.chesssystem.alipay.PayDemoActivity;
import com.chesssystem.alipay.PayResult;
import com.chesssystem.alipay.SignUtils;
import com.chesssystem.goods.FoodActivity;
import com.chesssystem.item.GoodsItem;
import com.chesssystem.item.MainItem;
import com.chesssystem.picktime.JudgeDate;
import com.chesssystem.picktime.ScreenInfo;
import com.chesssystem.picktime.WheelMain;
import com.chesssystem.ui.MerchantsActivity;
import com.chesssystem.ui.dazi.InviteActivity;
import com.chesssystem.ui.setting.NameSettingActivity;
import com.chesssystem.util.DateUtil;
import com.chesssystem.util.DoubleToInt;
import com.chesssystem.util.HttpUtl;
import com.chesssystem.util.ServerUrl;
import com.chesssystem.util.HttpUtl.HttpCallbackListener;
import com.chesssystem.widget.BaseListView;
import com.chesssystem.widget.MyDialog;
import com.chesssystem.wxapi.Util;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 提交订单页面
 * @author lyg
 * @time 2016-7-4上午9:51:59
 */
public class SubmitOrderActivity extends BaseActivity {
	private LinearLayout llBack;
	private TextView tvTitle;
	private ImageView ivZfb;	
	private ImageView ivWx;
	private RelativeLayout rlZfb;
	private RelativeLayout rlWx;
	private Button btInvite;
	private ArrayAdapter<GoodsItem> arrayAdapter;
	private List<GoodsItem>goodsItems=new ArrayList<GoodsItem>();
	private Intent intent;
	private RelativeLayout rlRoom;
	private RelativeLayout rlStore;
	private TextView tvStoreName;
	private ImageView ivRoomPic;
	private TextView tvRoomName;
	private TextView tvAllPrice;
	private LinearLayout llRoomNumber;
	private LinearLayout llArriverTime;
	private WheelMain wheelMain;
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private int choiceZfb=1;//支付方式。1支付宝 2微信
	private BaseListView lvGoods;//商品列表
	private EditText etInputName;//联系人
	private EditText etInputTelephone;//电话号码
	private EditText etInputRoomNumber;//房间号
	private TextView tvInputArriveTime;//到店时间
	private String orderType;//订单类型,商品或是棋牌室
	private String storeId;//店铺id
	private String roomId;//棋牌室id
	private String roomPic;//棋牌室图片
	private String roomName;//棋牌室名称
	private double allPrice=0;//总价
	private String orderNumber=null;//生成订单后的订单号
	private static String orderId;//订单id
	private boolean isAddOrder=false;//是否已经生成订单
	public static SubmitOrderActivity instance;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_submit);
		instance=this;
		initView();
		intent=getIntent();
		if(intent.getStringExtra("from").equals("order")){
			orderId=intent.getStringExtra("orderId");
			initDataFromOrder();
		}else{
			initDataFromShop();
		}
	}
	private void initDataFromOrder() {
		HttpUtl.getHttp(ServerUrl.getOrderDetailUrl + "?orderId=" +intent.getStringExtra("orderId"),
				this, new HttpCallbackListener() {
					@Override
					public void onFinish(JSONObject response) {
						 JSONObject jsonObject;
						try {
							jsonObject = response.getJSONObject("data");
							JSONArray jsonArray=jsonObject.getJSONArray("orderDetailBeans");
							tvStoreName.setText(jsonObject.getString("storeName"));
							tvAllPrice.setText(DoubleToInt.DoubleToInt(jsonObject.getDouble("orderPrice")));
							allPrice=jsonObject.getDouble("orderPrice");
							storeId=jsonObject.getString("storeId");
							/*
							 * 判断该订单是未支付还是已支付
							 */
							if(jsonObject.getInt("payStatus")==1){//未支付
								orderNumber=jsonObject.getString("orderNumber");
							}
							/*
							 * 该订单为商品
							 */
							if(jsonObject.getInt("goodsType")==1){
								orderType="goods";
								btInvite.setText("确认下单");
								rlRoom.setVisibility(View.GONE);
								lvGoods.setVisibility(View.VISIBLE);
								llArriverTime.setVisibility(View.GONE);
								llRoomNumber.setVisibility(View.VISIBLE);
								for (int i = 0; i < jsonArray.length(); i++) {
									JSONObject object = (JSONObject) jsonArray
											.get(i);
									GoodsItem goodsItem=new GoodsItem(
											object.getString("goodsId"),
											object.getString("goodsName"),"",0, 
											object.getDouble("price"),"", 
											object.getInt("buyCount"));
									goodsItems.add(goodsItem);
								}
								arrayAdapter=new OrderGoodsAdapter(SubmitOrderActivity.this, goodsItems);
								lvGoods.setAdapter(arrayAdapter);
							}
							/*
							 * 该订单为棋牌室
							 */
							else{
								orderType="room";
								rlRoom.setVisibility(View.VISIBLE);
								lvGoods.setVisibility(View.GONE);
								llArriverTime.setVisibility(View.VISIBLE);
								llRoomNumber.setVisibility(View.GONE);
								JSONObject object = (JSONObject) jsonArray.get(0);
								roomId=object.getString("goodsId");
								tvRoomName.setText(object.getString("goodsName"));
								Glide.with(SubmitOrderActivity.this)
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
	private void initDataFromShop() {
		orderType=intent.getStringExtra("orderType");
	    tvStoreName.setText(intent.getStringExtra("storeName"));//设置店铺名称
	    storeId=intent.getStringExtra("storeId");
		/**
		 * 棋牌室订单
		 */
		if(orderType.equals("room")){
			rlRoom.setVisibility(View.VISIBLE);
			lvGoods.setVisibility(View.GONE);
			llArriverTime.setVisibility(View.VISIBLE);
			llRoomNumber.setVisibility(View.GONE);
			roomId=intent.getStringExtra("roomId");
			roomName=intent.getStringExtra("roomName");
			roomPic=intent.getStringExtra("roomPic");
			allPrice=intent.getDoubleExtra("roomPrice",0);
			rlRoom.setVisibility(View.VISIBLE);
			lvGoods.setVisibility(View.GONE);
			Glide.with(this)
			.load(roomPic)
			.placeholder(R.color.gray2)
			.error(R.color.gray2)
			.skipMemoryCache( true )
			.centerCrop()
			.into(ivRoomPic);
			tvRoomName.setText(roomName);
			tvAllPrice.setText(DoubleToInt.DoubleToInt(allPrice));
		}
		/**
		 * 商品订单
		 */
		else if(orderType.equals("goods")){
		rlRoom.setVisibility(View.GONE);
		lvGoods.setVisibility(View.VISIBLE);
		llArriverTime.setVisibility(View.GONE);
		llRoomNumber.setVisibility(View.VISIBLE);
		btInvite.setText("确认下单");
		List<GoodsItem> goodsList=(List<GoodsItem>) intent.getSerializableExtra("goodsList"); 
		for(GoodsItem goodsItem:goodsList){
			goodsItems.add(goodsItem);
			allPrice+=goodsItem.getGoodsPrice()*goodsItem.getChoiceNumber();
		}
		arrayAdapter=new OrderGoodsAdapter(this, goodsItems);
		lvGoods.setAdapter(arrayAdapter);
		tvAllPrice.setText(DoubleToInt.DoubleToInt(allPrice));
		}
	}

	private void initView() {
		llBack=(LinearLayout) findViewById(R.id.ll_back);
		llBack.setOnClickListener(this);
		tvTitle=(TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.submitorder);
		ivZfb=(ImageView) findViewById(R.id.iv_submit_zhifubao);
		ivWx=(ImageView) findViewById(R.id.iv_submit_weixin);
		rlZfb=(RelativeLayout) findViewById(R.id.rl_submit_zhifubao);
		rlZfb.setOnClickListener(this);
		rlWx=(RelativeLayout) findViewById(R.id.rl_submit_weixin);
		rlWx.setOnClickListener(this);
		btInvite=(Button) findViewById(R.id.bt_invite);
		btInvite.setOnClickListener(this);
		rlStore=(RelativeLayout) findViewById(R.id.rl_store);
		rlStore.setOnClickListener(this);
		lvGoods=(BaseListView) findViewById(R.id.lv_ordersubmit_goods);
		rlRoom=(RelativeLayout) findViewById(R.id.rl_ordersubmit_room);
		tvStoreName=(TextView) findViewById(R.id.tv_order_name);
		ivRoomPic=(ImageView) findViewById(R.id.iv_order_submit_image);
		tvRoomName=(TextView) findViewById(R.id.tv_order_roomname);
		tvAllPrice=(TextView) findViewById(R.id.tv_order_submit_money);
		etInputName=(EditText) findViewById(R.id.et_input_name);
		etInputTelephone=(EditText) findViewById(R.id.et_input_telephone);
		etInputTelephone.setText(sharedPreferences.getString("account", ""));//设置默认显示用户手机账号
		etInputRoomNumber=(EditText) findViewById(R.id.et_input_roomnumber);
		llRoomNumber=(LinearLayout) findViewById(R.id.ll_roomnumber);
		llArriverTime=(LinearLayout) findViewById(R.id.ll_time_arrive);
		tvInputArriveTime=(TextView) findViewById(R.id.tv_time_arrive);
		tvInputArriveTime.setOnClickListener(this);
	}
	/**
	 * 创建订单
	 */
	public void addOrder(){
		StringRequest stringRequest = new StringRequest(Method.POST,ServerUrl.postAddOrderUrl,  
                new Response.Listener<String>() {  
                    @Override  
                    public void onResponse(String response) {
                    	try {
							JSONObject jsonObject = new JSONObject(response);
							if(jsonObject.getInt("retCode")==0){
								JSONObject jsonObject1 = new JSONObject(jsonObject.getString("data"));
								orderNumber= jsonObject1.getString("orderNumber");
								orderId= jsonObject1.getString("orderId");
								isAddOrder=true;//已经生成订单
								if(choiceZfb==1){
								Alipay(orderNumber);
								}else{
									WeixinPay(orderNumber);	
								}
							}else {
								if (progressDialog != null) {
									progressDialog.dismiss();
								}
								Toast.makeText(SubmitOrderActivity.this,jsonObject.getString("retMsg"), Toast.LENGTH_SHORT).show();
							} 
                    	} catch (JSONException e) {
							e.printStackTrace();
						}
                    }  
                }, new Response.ErrorListener() {  
                    @Override  
                    public void onErrorResponse(VolleyError error) {  
                    	if (progressDialog != null) {
							progressDialog.dismiss();
						}
                        Log.e("false", error.getMessage(),error); 
						Toast.makeText(SubmitOrderActivity.this, error+"", Toast.LENGTH_SHORT).show();
                    }  
                }){
				@Override
				protected Map<String, String> getParams() throws AuthFailureError {
					Map<String, String> map = new HashMap<String, String>();
					if(orderNumber!=null){
					map.put("orderId",orderId);
					}
					map.put("userId",sharedPreferences.getString("userId", ""));
					map.put("username", etInputName.getText().toString());
					map.put("storeId", storeId);
					map.put("telePhone", etInputTelephone.getText().toString());
					map.put("payType", String.valueOf(choiceZfb));
					/*
					 * 棋牌室订单
					 */
					if(orderType.equals("room")){
						map.put("goodsType", "2");
						map.put("orderStr", roomId+":1");
						map.put("orderDate", tvInputArriveTime.getText().toString().replace("-", "/"));
					}
					/*
					 * 商品订单
					 */
					else if(orderType.equals("goods")){
						map.put("goodsType", "1");
						String orderStr="";
						for(GoodsItem goodsItem:goodsItems){
							orderStr+=","+goodsItem.getGoodsId()+":"+goodsItem.getChoiceNumber();
						}
						orderStr=orderStr.replaceFirst("\\,", "");
						map.put("orderStr",orderStr);
						map.put("roomNumber", etInputRoomNumber.getText().toString());
					}
					return map;
				}
		}; 
		NimApplication.getHttpQueues().add(stringRequest);
	}
	/**
	 * 支付宝支付
	 * @param orderNumber
	 */
	public void Alipay(String orderNumber){
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
		String orderInfo = AliPayUtils.getOrderInfo("订单-"+orderNumber,tvStoreName.getText().toString(), String.valueOf(allPrice),orderNumber);
		String sign = SignUtils.sign(orderInfo, AliPayUtils.RSA_PRIVATE);
		try {
			/**
			 * 仅需对sign 做URL编码
			 */
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		/**
		 * 完整的符合支付宝参数规范的订单信息
		 */
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + "sign_type=\"RSA\"";
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(SubmitOrderActivity.this);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo, true);
				Message msg = new Message();
				msg.what = AliPayUtils.SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};
		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unused")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AliPayUtils.SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);
				String resultInfo = payResult.getResult();// 同步返回需要验证的信息
				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					Toast.makeText(SubmitOrderActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
					finished();
//					Intent intent=new Intent(SubmitOrderActivity.this,OrderDetailActivity.class);
//					intent.putExtra("orderId",orderId);
//					startActivity(intent);
//					finish();
				} else {
					// 判断resultStatus 为非"9000"则代表可能支付失败
					// "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(SubmitOrderActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();
					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(SubmitOrderActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
					}
				}
				break;
			}
			default:
				break;
			}
		};
	};
	public static void finished(){
		Intent intent=new Intent(SubmitOrderActivity.instance,OrderDetailActivity.class);
		intent.putExtra("orderId",orderId);
		SubmitOrderActivity.instance.startActivity(intent);
		SubmitOrderActivity.instance.finish();
	}
	/**
	 * 微信支付
	 */
	private IWXAPI api;
	public void WeixinPay(String orderNumber){
		api = WXAPIFactory.createWXAPI(this, "wx71c535e1837c6bdf");
		api.registerApp("wx71c535e1837c6bdf");
		HttpUtl.getHttp(ServerUrl.weixinPayUrl+"?orderNumber="+orderNumber+"&orderPrice="+(int)(allPrice*100), this,
				new HttpCallbackListener() {
					@Override
					public void onFinish(JSONObject response) {
						try {
							JSONObject json = new JSONObject(response.getString("data"));
							PayReq req = new PayReq();
							req.appId			= json.getString("appid");
							req.nonceStr		= json.getString("noncestr");
							req.packageValue	= "Sign=WXPay";
							req.partnerId		= json.getString("partnerid");
							req.prepayId		= json.getString("prepayid");
							req.sign			= json.getString("sign");
							req.timeStamp		= json.getString("timestamp");
//							Toast.makeText(SubmitOrderActivity.this, "正常调起支付", Toast.LENGTH_SHORT).show();
							api.sendReq(req);
						} catch (JSONException e) {
							e.printStackTrace();
							Toast.makeText(SubmitOrderActivity.this, "异常："+e.getMessage(), Toast.LENGTH_SHORT).show();
						}
						if (progressDialog != null) {
							progressDialog.dismiss();
						}
					}
					@Override
					public void onError(VolleyError e) {
						if (progressDialog != null) {
							progressDialog.dismiss();
						}
					}

					@Override
					public void onFinishError(JSONObject response) {
						if (progressDialog != null) {
							progressDialog.dismiss();
						}
					}
				});
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			//返回按钮
		case R.id.ll_back:
			finish();
			break;
			//支付宝支付选项
		case R.id.rl_submit_zhifubao:
			if(choiceZfb==2){
				ivZfb.setImageResource(R.drawable.rediobt_press);
				ivWx.setImageResource(R.drawable.rediobt_normal);
				choiceZfb=1;
			}
			break;
			//微信支付选项
		case R.id.rl_submit_weixin:
			if(choiceZfb==1){
				ivZfb.setImageResource(R.drawable.rediobt_normal);
				ivWx.setImageResource(R.drawable.rediobt_press);
				choiceZfb=2;
			}
			break;
			//确认预定
		case R.id.bt_invite:
				if(etInputName.getText().toString().equals("")){
					Toast.makeText(this, "请输入联系人姓名", Toast.LENGTH_SHORT).show();
				}else if(etInputTelephone.getText().toString().equals("")){
					Toast.makeText(this, "请输入电话号码", Toast.LENGTH_SHORT).show();
				}else if(orderType.equals("room")&&tvInputArriveTime.getText().toString().equals("")){
					Toast.makeText(this, "请输入到店时间", Toast.LENGTH_SHORT).show();
				}else if(orderType.equals("goods")&&etInputRoomNumber.getText().toString().equals("")){
					Toast.makeText(this, "请输入包厢号", Toast.LENGTH_SHORT).show();
				}else{
//					if (progressDialog == null) {
//						progressDialog = new ProgressDialog(this);
//						progressDialog.setCanceledOnTouchOutside(false);
//					}
//					progressDialog.show();
//					if(isAddOrder){
//						if(choiceZfb==1){
//							Alipay(orderNumber);
//						}else{
//							WeixinPay(orderNumber);	
//						}
//					}else{
//						addOrder();
//					}
//					addOrder();
					if(orderType.equals("room")){
					popDialog("亲，距离预约时间到期30分钟内将不能退款哦!");
					}else{
						addOrder();
					}
				}
			break;
			//选择到店时间
		case R.id.tv_time_arrive:
			pickBeginTime();
			break;
		case R.id.rl_store:
			Intent intent = new Intent(SubmitOrderActivity.this,
					MerchantsActivity.class);
			intent.putExtra("storeId",storeId);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
	/**
	 * 选择时间
	 */
	private void pickBeginTime() {
		LayoutInflater inflater = LayoutInflater.from(SubmitOrderActivity.this);
		final View timepickerview = inflater.inflate(R.layout.pop_picktime,
				null);
		ScreenInfo screenInfo = new ScreenInfo(SubmitOrderActivity.this);
		wheelMain = new WheelMain(timepickerview, true);
		wheelMain.screenheight = screenInfo.getHeight();
		String time = tvInputArriveTime.getText().toString();
		Calendar calendar = Calendar.getInstance();
		if (JudgeDate.isDate(time, "yyyy-MM-dd HH:mm:ss")) {
			try {
				calendar.setTime(dateFormat.parse(time));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int h = calendar.get(Calendar.HOUR_OF_DAY);
		int m = calendar.get(Calendar.MINUTE);
		wheelMain.initDateTimePicker(year, month, day, h, m);
		new AlertDialog.Builder(SubmitOrderActivity.this)
				.setTitle("选择时间")
				.setView(timepickerview)
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if(DateUtil.checkTimeAndNow(wheelMain.getTime())){
									tvInputArriveTime.setText(wheelMain.getTime());
								}else{
									Toast.makeText(SubmitOrderActivity.this,"到店时间不能早于当前时间!", Toast.LENGTH_SHORT).show();
								}
							}
						})
				.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						}).show();		
	}
	/**
	 * 提示框
	 */
	private MyDialog myDialog;
	private TextView tvPopContent;
	public void popDialog(String content){
		myDialog = new MyDialog(SubmitOrderActivity.this,R.style.inputDialog);
		tvPopContent=(TextView) myDialog.getContent();
		tvPopContent.setText(content);
		myDialog.setOnPositiveListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				myDialog.dismiss();
				if (progressDialog == null) {
					progressDialog = new ProgressDialog(SubmitOrderActivity.this);
					progressDialog.setCanceledOnTouchOutside(false);
				}
				progressDialog.show();
				addOrder();
			}
		});
		myDialog.setOnNegativeListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				myDialog.dismiss();
				Toast.makeText(SubmitOrderActivity.this,"取消预订", Toast.LENGTH_SHORT).show();
			}
		});
		myDialog.show();
	}
}
