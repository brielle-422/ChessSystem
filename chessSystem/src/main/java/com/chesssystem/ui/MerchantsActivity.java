package com.chesssystem.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.chesssystem.BaseActivity;
import com.chesssystem.NimApplication;
import com.chesssystem.R;
import com.chesssystem.adapter.CommentAdapter;
import com.chesssystem.adapter.GoodsGridViewAdapter;
import com.chesssystem.adapter.RoomAdapter;
import com.chesssystem.adapter.RoomAdapter.MyClickListener;
import com.chesssystem.banner.BannerItem;
import com.chesssystem.banner.ImageCycleView;
import com.chesssystem.banner.ImageCycleView.ImageCycleViewListener;
import com.chesssystem.goods.FoodActivity;
import com.chesssystem.item.CommentItem;
import com.chesssystem.item.GoodsItem;
import com.chesssystem.item.RoomItem;
import com.chesssystem.map.MapActivity;
import com.chesssystem.ui.comment.CommentAllActivity;
import com.chesssystem.ui.order.SubmitOrderActivity;
import com.chesssystem.ui.setting.LoginActivity;
import com.chesssystem.util.DoubleToInt;
import com.chesssystem.util.HttpUtl;
import com.chesssystem.util.HttpUtl.HttpCallbackListener;
import com.chesssystem.util.ServerUrl;
import com.chesssystem.widget.GoodsGridView;
import com.chesssystem.widget.MyDialog;
import com.chesssystem.widget.SetListHeight;
import com.chesssystem.widget.TagLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

/**
 * 店铺详情页面
 * 
 * @author lyg
 * @time 2016-7-4上午9:51:25
 */
public class MerchantsActivity extends BaseActivity {
	// 商家信息
	private String storeId;
	private String storeName;
	private String storeAdd;
	private String lat;
	private String lng;
	private MyDialog myDialog;
	private TextView tvPopContent;
	private LinearLayout llBack;
	private RelativeLayout rlGoods;
	private RelativeLayout rlComment;
	private LinearLayout llWifi;
	private LinearLayout llPark;
	private LinearLayout llEat;
	private ImageView ivHeart;
	private LinearLayout llMap;
	private TextView TvTitle;
	private TextView TvDistance;
	private TextView TvLocal;
	private ArrayList<BannerItem> bannerItems = new ArrayList<BannerItem>();
	private GoodsGridView goodsGridView;
	private ImageView icvBanner;
	private ListView lvRoom;
	private PullToRefreshListView lvComment;
	private List<GoodsItem> goodsItems = new ArrayList<GoodsItem>();
	private List<RoomItem> roomItems = new ArrayList<RoomItem>();
	private List<CommentItem> commentItems = new ArrayList<CommentItem>();
	private ArrayAdapter<GoodsItem> goodsAdapter;
	private ArrayAdapter<RoomItem> roomAdapter;
	private ArrayAdapter<CommentItem> commAdapter;
	private int msgPagerNumber = 1;
	private int collected;
	private TextView tvRatesNumber;// 总评论数(底部)
	private TextView tvRatesNumberTop;// 总评论数(顶部)
	private ViewGroup starGroup;// 平均星级(底部)
	private ViewGroup starGroupTop;// 平均星级(顶部)
	private RelativeLayout titleOffer;
	private RelativeLayout titleGoods;
	private RelativeLayout titleComment;
	private RelativeLayout scrollGoods;
	private RelativeLayout scrollComment;
	private LinearLayout headList;
	private ListView refreshableView;
	private String telephone="";
	private TagLayout mFlowLayout;
	private ImageView ivTelephone;//电话图标

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_metchants);
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
		initView();
		initData();
	}

	private void initView() {
		/*
		 * 评论List+head
		 */
		lvComment = (PullToRefreshListView) findViewById(R.id.lv_metchants_comment);
		lvComment.setMode(Mode.PULL_UP_TO_REFRESH);
		refreshableView = lvComment.getRefreshableView();
		headList = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.head_metchants, null);
		refreshableView.addHeaderView(headList, null, false);
		commAdapter = new CommentAdapter(getApplicationContext(), commentItems);
		lvComment.setAdapter(commAdapter);
		lvComment.getLoadingLayoutProxy(false, true).setPullLabel(
				getString(R.string.upFresh));
		/*
		 * head内容
		 */
		Intent intent = getIntent();
		storeId = intent.getStringExtra("storeId");
		llBack = (LinearLayout) findViewById(R.id.ll_back);
		llBack.setOnClickListener(this);
		llWifi = (LinearLayout) findViewById(R.id.ll_wifi);// wifi
		llPark = (LinearLayout) findViewById(R.id.ll_park);// 停车
		llEat = (LinearLayout) findViewById(R.id.ll_eat);// 小吃
		ivTelephone=(ImageView) findViewById(R.id.iv_telephone);//电话
		ivTelephone.setOnClickListener(this);
		TvTitle = (TextView) findViewById(R.id.tv_title);// 标题
		ivHeart = (ImageView) findViewById(R.id.iv_title_heart);// 收藏
		ivHeart.setVisibility(View.VISIBLE);
		ivHeart.setOnClickListener(this);
		TvDistance = (TextView) findViewById(R.id.tv_metchants_distance);// 距离
		TvLocal = (TextView) findViewById(R.id.tv_metchants_local);// 地址
		rlGoods = (RelativeLayout) findViewById(R.id.rl_metchants_goods);// 商品栏
		rlGoods.setOnClickListener(this);
		rlComment = (RelativeLayout) findViewById(R.id.rl_metchants_comment);// 评论栏
		rlComment.setOnClickListener(this);
		tvRatesNumber = (TextView) headList.findViewById(R.id.tv_rates_number);// 评论数（底部）
		starGroup = (ViewGroup) headList.findViewById(R.id.ll_star);// 平均星级(底部)
		llMap = (LinearLayout) findViewById(R.id.ll_metchants_map);
		llMap.setOnClickListener(this);
		icvBanner = (ImageView) findViewById(R.id.icv_metchants_banner);
		goodsGridView = (GoodsGridView) findViewById(R.id.gv_metchants_goods);
		goodsAdapter = new GoodsGridViewAdapter(getApplicationContext(),
				goodsItems);
		goodsGridView.setAdapter(goodsAdapter);
		lvRoom = (ListView) findViewById(R.id.lv_metchants_room);
		roomAdapter = new RoomAdapter(this, roomItems, mListener);
		lvRoom.setAdapter(roomAdapter);
		mFlowLayout = (TagLayout) findViewById(R.id.tg_hotword);
		goodsGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Intent intentGoods = new Intent(MerchantsActivity.this,
						FoodActivity.class);
				intentGoods.putExtra("storeId", storeId);
				intentGoods.putExtra("storeName", storeName);
				startActivity(intentGoods);
			}
		});
		lvComment.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (refreshView.isShownHeader()) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							msgPagerNumber = 1;
							downloadComment(msgPagerNumber);
						}
					}, 1000);
				}
				if (refreshView.isShownFooter()) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							 downloadComment(msgPagerNumber);
//							initData();
						}
					}, 1000);
				}
			}
		});
		titleOffer = (RelativeLayout) findViewById(R.id.title_offer);// 固定顶部的信息栏
		titleGoods = (RelativeLayout) findViewById(R.id.title_goods);// 固定顶部的商品栏
		titleGoods.setOnClickListener(this);
		titleComment = (RelativeLayout) findViewById(R.id.title_comment);// 固定顶部的评论栏
		titleComment.setOnClickListener(this);
		tvRatesNumberTop = (TextView) titleComment
				.findViewById(R.id.tv_rates_number);// 评论数(顶部)
		starGroupTop = (ViewGroup) titleComment.findViewById(R.id.ll_star);// 平均星级(顶部)
		refreshableView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				int[] location = new int[2];
				titleOffer.getLocationInWindow(location);
				int y = location[1];

				int[] location1 = new int[2];
				rlGoods.getLocationInWindow(location1);
				int y1 = location1[1];

				int[] location2 = new int[2];
				rlComment.getLocationInWindow(location2);
				int y2 = location2[1];
				if (y <= y1 && y <= y2 && y1 <= y2) {
					titleOffer.setVisibility(View.VISIBLE);
					titleGoods.setVisibility(View.GONE);
					titleComment.setVisibility(View.GONE);
				} else if (y > y1 && y < y2) {
					titleOffer.setVisibility(View.GONE);
					titleComment.setVisibility(View.GONE);
					titleGoods.setVisibility(View.VISIBLE);
				} else if (y > y2 && y2 > y1) {
					titleOffer.setVisibility(View.GONE);
					titleComment.setVisibility(View.VISIBLE);
					titleGoods.setVisibility(View.GONE);
				}

			}
		});
	}

	private void initData() {
		String inforUrl;
		if (NimApplication.LOGINED) {
			inforUrl = ServerUrl.getStoreDetailUrl + "?storeId=" + storeId
					+ "&userId="
					+ NimApplication.sharedPreferences.getString("userId", "")
					+ "&lat=" + NimApplication.lat + "&lng="
					+ NimApplication.lng;
		} else {
			inforUrl = ServerUrl.getStoreDetailUrl + "?storeId=" + storeId
					+ "&lat=" + NimApplication.lat + "&lng="
					+ NimApplication.lng;
		}
		downloadInfor(inforUrl);// 加载商家信息
		uploadBanner();// 加载广告栏数据
		downloadRooms();// 加载棋牌室
		downloadGoods();// 加载商品
		downloadComment(msgPagerNumber);// 加载评论
	}

	/**
	 * 加载商家信息
	 */
	private void downloadInfor(String url) {
		HttpUtl.getHttp(url, this, new HttpCallbackListener() {
			@Override
			public void onFinish(JSONObject response) {
				try {
					JSONObject jsonArray = response.getJSONObject("data");
					storeName = jsonArray.getString("storeName");// 店铺名称
					storeAdd = jsonArray.getString("storeAdd");// 店铺地址
					lat = jsonArray.getString("lat");// 店铺坐标X
					lng = jsonArray.getString("lng");// 店铺坐标y
					telephone= jsonArray.getString("workTel");//商家电话联系方式
					TvTitle.setText(storeName);
					TvLocal.setText(storeAdd);
					collected = jsonArray.getInt("collected");
					TvDistance.setText(DoubleToInt.DoubleToDistance(jsonArray
							.getDouble("distance")));
					if (collected == 0) {
						ivHeart.setImageResource(R.drawable.heart);
					} else {
						ivHeart.setImageResource(R.drawable.yellow_heart);
					}
					if (jsonArray.getInt("attr_wifi") == 1)
						llWifi.setVisibility(View.VISIBLE);
					if (jsonArray.getInt("attr_parking") == 1)
						llPark.setVisibility(View.VISIBLE);
					if (jsonArray.getInt("attr_eating") == 1)
						llEat.setVisibility(View.VISIBLE);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(VolleyError e) {
			}

			@Override
			public void onFinishError(JSONObject response) {
				// TODO Auto-generated method stub

			}
		});

	}

	// public ImageCycleViewListener mAdCycleViewListener = new
	// ImageCycleViewListener() {
	// @Override
	// public void onImageClick(BannerItem info, int postion, View imageView) {
	// // Toast.makeText(MerchantsActivity.this,
	// // "content->" + info.getContent(), Toast.LENGTH_SHORT).show();
	// }
	//
	// @Override
	// public void displayImage(String imageURL, ImageView imageView) {
	// Glide.with(MerchantsActivity.this)
	// .load(imageURL)
	// .placeholder(R.color.gray2)
	// .error(R.color.gray2)
	// .skipMemoryCache( true )
	// .centerCrop()
	// .into(imageView);
	// }
	// };

	/**
	 * 获取广告数据
	 */
	private void uploadBanner() {
		HttpUtl.getHttp(ServerUrl.getBannerMsgUrl + "?adType=2" + "&status=1"
				+ "&storeId=" + storeId, this, new HttpCallbackListener() {
			@Override
			public void onFinish(JSONObject response) {
				try {
					JSONArray jsonArray = response.getJSONArray("data");
					if (jsonArray.length() > 0) {
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject object = (JSONObject) jsonArray.get(i);
							BannerItem info = new BannerItem();
							info.setUrl(ServerUrl.getPicUrl
									+ object.getString("adPic"));
							info.setContent("top-->" + i);
							bannerItems.add(info);
						}
						Glide.with(MerchantsActivity.this)
								.load(bannerItems.get(0).getUrl())
								.placeholder(R.color.gray2)
								.error(R.color.gray2).skipMemoryCache(true)
								.centerCrop().into(icvBanner);
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

	/**
	 * 加载评论
	 * 
	 * @param numberPager
	 */
	private void downloadComment(final int numberPager) {
		HttpUtl.getHttp(ServerUrl.SERVERURL + "/rate/getRateList" + "?storeId="
				+ storeId + "&pageSize=15&currentPage=" + numberPager, this,
				new HttpCallbackListener() {
					@Override
					public void onFinish(JSONObject response) {
						try {
							JSONObject jsonObject = response
									.getJSONObject("data");
							tvRatesNumber.setText("评价("
									+ jsonObject.getString("allCount") + ")");
							tvRatesNumberTop.setText("评价("
									+ jsonObject.getString("allCount") + ")");
							/*
							 * 设置平均星级
							 */
							setStar((int) jsonObject.getDouble("avgRate"),
									starGroup);
							setStar((int) jsonObject.getDouble("avgRate"),
									starGroupTop);
							/*
							 * 热词
							 */
							JSONArray hotWords = jsonObject
									.getJSONArray("hotWord");
							if (hotWords.length() > 0) {
								mFlowLayout.setVisibility(View.VISIBLE);
								for (int i = 0; i < hotWords.length(); i++) {
									TextView tv = new TextView(
											MerchantsActivity.this);
									tv.setText(hotWords.get(i).toString());
									LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
											LinearLayout.LayoutParams.WRAP_CONTENT,
											LinearLayout.LayoutParams.WRAP_CONTENT);
									params.setMargins(0, 0, 20, 30);
									tv.setLayoutParams(params);
									tv.setPadding(8, 8, 8, 8);
									tv.setBackgroundResource(R.drawable.circle_transparent_yellow);
									mFlowLayout.addView(tv);
								}
							}
							/*
							 * 评论内容
							 */
							JSONArray jsonArray = jsonObject.getJSONArray("rateBeans");
							/*
							 * 如果有下拉刷新时，将清空数据的操作放在这里，防止数据越界
							 */
							if (numberPager == 1) {
								commentItems.clear();
								//当首次获取数据并且数据为空时关闭上下拉刷新
								if(jsonArray.length()==0){
									lvComment.setMode(Mode.DISABLED);
								}
							}
							if (jsonArray.length() > 0) {
								lvComment.setMode(Mode.PULL_UP_TO_REFRESH);
								for (int i = 0; i < jsonArray.length(); i++) {
									JSONObject object = (JSONObject) jsonArray
											.get(i);
									/*
									 * 获取图片数组
									 */
									JSONArray jsonArray2 = object
											.getJSONArray("ratePics");
									List<String> images = new ArrayList<String>();
									for (int j = 0; j < jsonArray2.length(); j++) {
										images.add(ServerUrl.getPicUrl
												+ jsonArray2.getString(j));
									}
									/*
									 * 商家回复
									 */
									String reply = "";
									JSONArray replys = object
											.getJSONArray("responseBeans");
									if (replys.length() > 0) {
										JSONObject replay = (JSONObject) replys
												.get(0);
										reply = replay.getString("rateContent");
									}
									CommentItem commentItem = new CommentItem(
											object.getString("username"),
											object.getString("rateContent"),
											(int) object.getDouble("rateStar"),
											ServerUrl.getPicUrl
													+ object.getString("userpic"),
											object.getString("createTime"),
											reply, images);
									commentItems.add(commentItem);
								}
								commAdapter.notifyDataSetChanged();
								msgPagerNumber++;
							}
//							else {
//								lvComment.setMode(Mode.DISABLED);
//							}
							if (progressDialog != null) {
								progressDialog.dismiss();
							}
							lvComment.onRefreshComplete();// 停止刷新
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					private void setStar(int starNumber, ViewGroup viewGroup) {
						viewGroup.removeAllViews();
						ImageView[] tips = new ImageView[5];
						for (int i = 0; i < 5; i++) {
							ImageView imageView = new ImageView(
									MerchantsActivity.this);
							tips[i] = imageView;
							if (i < starNumber) {
								tips[i].setImageResource(R.drawable.favorate_press);
							} else {
								tips[i].setImageResource(R.drawable.favorate_default);
							}
							LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
									new ViewGroup.LayoutParams(55, 55));
							viewGroup.addView(imageView, layoutParams);
						}
					}

					@Override
					public void onError(VolleyError e) {
						if (progressDialog != null) {
							progressDialog.dismiss();
						}
						lvComment.onRefreshComplete();// 停止刷新
					}
					@Override
					public void onFinishError(JSONObject response) {
						lvComment.onRefreshComplete();// 停止刷新

					}
				});

	}

	/**
	 * 加载棋牌室
	 */
	private void downloadRooms() {
		HttpUtl.getHttp(ServerUrl.getRoomListUrl + "?storeId=" + storeId
				+ "&currentPage=1&pageSize=0", this,
				new HttpCallbackListener() {
					@Override
					public void onFinish(JSONObject response) {
						try {
							JSONArray jsonArray = response.getJSONArray("data");
							roomItems.clear();
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject object = (JSONObject) jsonArray
										.get(i);
								RoomItem roomItem = new RoomItem(object
										.getString("roomId"), object
										.getString("roomName"), object
										.getDouble("roomPrice"), object
										.getDouble("roomHour"), storeId, object
										.getInt("status"), ServerUrl.getPicUrl
										+ object.getString("roomPic"));
								roomItems.add(roomItem);
							}
							SetListHeight
									.setListViewHeightBasedOnChildren(lvRoom);
							roomAdapter.notifyDataSetChanged();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onError(VolleyError e) {
						lvComment.onRefreshComplete();// 停止刷新
					}

					@Override
					public void onFinishError(JSONObject response) {
					}
				});

	}

	/**
	 * 加载商品
	 */
	private void downloadGoods() {
		HttpUtl.getHttp(ServerUrl.getGoodsListUrl + "?storeId=" + storeId
				+ "&currentPage=1&pageSize=4", this,
				new HttpCallbackListener() {
					@Override
					public void onFinish(JSONObject response) {
						try {
							JSONArray jsonArray = response.getJSONArray("data");
							goodsItems.clear();
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject object = (JSONObject) jsonArray
										.get(i);
								GoodsItem goodsItem = new GoodsItem(object
										.getString("goodsId"), object
										.getString("goodsName"),
										ServerUrl.getPicUrl
												+ object.getString("goodsPic"),
										object.getInt("sale"), object
												.getDouble("goodsPrice"),
										object.getString("goodsType"), 0);
								goodsItems.add(goodsItem);
							}
							goodsAdapter.notifyDataSetChanged();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onError(VolleyError e) {
						lvComment.onRefreshComplete();// 停止刷新
					}

					@Override
					public void onFinishError(JSONObject response) {
						// TODO Auto-generated method stub

					}
				});
	}

	/**
	 * 收藏、取消
	 */
	public void collection(String url, final String msg) {
		StringRequest stringRequest = new StringRequest(Method.POST, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							JSONObject jsonObject = new JSONObject(response);
							if (jsonObject.getInt("retCode") == 1) {
								Toast.makeText(MerchantsActivity.this,
										jsonObject.getString("retMsg"),
										Toast.LENGTH_SHORT).show();
							} else if (jsonObject.getInt("retCode") == 0) {
								Toast.makeText(MerchantsActivity.this,
										msg + "成功", Toast.LENGTH_SHORT).show();
								if (collected == 0) {
									collected = 1;
									ivHeart.setImageResource(R.drawable.yellow_heart);
								} else {
									collected = 0;
									ivHeart.setImageResource(R.drawable.heart);
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(MerchantsActivity.this, msg + "失败",
								Toast.LENGTH_SHORT).show();
					}
				}) {
		};
		NimApplication.getHttpQueues().add(stringRequest);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ll_back:
			finish();
			break;
		// 点击商品栏
		case R.id.title_goods:
		case R.id.rl_metchants_goods:
			Intent intentGoods = new Intent(this, FoodActivity.class);
			intentGoods.putExtra("storeId", storeId);
			intentGoods.putExtra("storeName", storeName);
			startActivity(intentGoods);
			break;
		// 点击评论栏
		case R.id.title_comment:
		case R.id.rl_metchants_comment:
			Intent intentComment = new Intent(this, CommentAllActivity.class);
			intentComment.putExtra("storeId", storeId);
			startActivity(intentComment);
			break;
		// 点击地图
		case R.id.ll_metchants_map:
			Intent intentMap = new Intent(this, MapActivity.class);
			intentMap.putExtra("lat", lat);
			intentMap.putExtra("lng", lng);
			intentMap.putExtra("storeName", storeName);
			intentMap.putExtra("storeAdd", storeAdd);
			startActivity(intentMap);
			break;
		//点击电话
		case R.id.iv_telephone:
			if(!telephone.equals("")){
				Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+telephone));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	            startActivity(intent); 
			}else{
				popDialog("商家尚未填写电话号码~",1);
//				Toast.makeText(MerchantsActivity.this, "商家尚未填写电话号码~", Toast.LENGTH_SHORT).show();
			}
			break;
		// 点击收藏
		case R.id.iv_title_heart:
			if (!NimApplication.LOGINED) {
				Toast.makeText(MerchantsActivity.this, "请先登录后再进行收藏",
						Toast.LENGTH_SHORT).show();
			} else if (NimApplication.LOGINED && collected == 0) {
				collection(
						ServerUrl.postCollectedUrl
								+ "?userId="
								+ NimApplication.sharedPreferences.getString(
										"userId", "") + "&storeId=" + storeId,
						"收藏");
			} else {
				collection(
						ServerUrl.postDeleteCollectionUrl
								+ "?userId="
								+ NimApplication.sharedPreferences.getString(
										"userId", "") + "&storeId=" + storeId,
						"取消收藏");
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		NimApplication.getHttpQueues().cancelAll("merchants_request");
	}

	/**
	 * 点击棋牌室预订，跳转到提交订单页面
	 */
	private MyClickListener mListener = new MyClickListener() {
		@Override
		public void myOnClick(int position, View v) {
			if (!NimApplication.LOGINED) {
				popDialog("您还没有登录，请登录后再预定棋牌室",0);
			} else {
				Intent intent = new Intent(MerchantsActivity.this,
						SubmitOrderActivity.class);
				intent.putExtra("from", "shop");
				intent.putExtra("orderType", "room");
				intent.putExtra("storeId", storeId);
				intent.putExtra("storeName", storeName);
				intent.putExtra("roomId", roomItems.get(position).getRoomId());
				intent.putExtra("roomPic", roomItems.get(position).getRoomPic());
				intent.putExtra("roomName", roomItems.get(position)
						.getRoomName());
				intent.putExtra("roomPrice", roomItems.get(position).getPrice());
				startActivity(intent);
			}
		}
	};

	public void popDialog(String content,final int type) {
		myDialog = new MyDialog(this, R.style.inputDialog);
		tvPopContent = (TextView) myDialog.getContent();
		tvPopContent.setText(content);
		if(type==1){
			myDialog.getCancel().setVisibility(View.GONE);
		}
		myDialog.setOnPositiveListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(type==0){
				startActivity(new Intent(MerchantsActivity.this,
						LoginActivity.class));
				}
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