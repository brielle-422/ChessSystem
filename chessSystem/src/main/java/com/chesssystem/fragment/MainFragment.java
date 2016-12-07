package com.chesssystem.fragment;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
import android.util.Log;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.Glide;
import com.chesssystem.MainActivity;
import com.chesssystem.NimApplication;
import com.chesssystem.R;
import com.chesssystem.adapter.MainAdapter;
import com.chesssystem.adapter.PopularAdapter;
import com.chesssystem.alipay.PayDemoActivity;
import com.chesssystem.banner.BannerItem;
import com.chesssystem.banner.ImageCycleView;
import com.chesssystem.banner.ImageCycleView.ImageCycleViewListener;
import com.chesssystem.item.MainItem;
import com.chesssystem.item.OrderItem;
import com.chesssystem.map.CityChoiceActivity;
import com.chesssystem.map.MapActivity.MyLocationListenner;
import com.chesssystem.ui.BusinessActivity;
import com.chesssystem.ui.H5MerchantsJoinActivity;
import com.chesssystem.ui.MerchantsActivity;
import com.chesssystem.ui.SearchActivity;
import com.chesssystem.ui.dazi.DaziActivity;
import com.chesssystem.ui.dazi.MyInviteActivity;
import com.chesssystem.ui.setting.LoginActivity;
import com.chesssystem.util.DateUtil;
import com.chesssystem.util.HttpUtl;
import com.chesssystem.util.ServerUrl;
import com.chesssystem.util.HttpUtl.HttpCallbackListener;
import com.chesssystem.widget.MyDialog;
import com.chesssystem.widget.SetListHeight;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.CursorUtils.FindCacheSequence;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 首页
 * @author lyg
 * @time 2016-7-4上午9:49:17
 */
public class MainFragment extends Fragment implements OnClickListener {
	private static MainFragment mainFragmentInstance;
	private ArrayList<BannerItem> bannerItems = new ArrayList<BannerItem>();
	private ImageCycleView icvBanner;
	private PullToRefreshListView lvMall;
	private LinearLayout llDazi;
	private LinearLayout llBusiness;
	private LinearLayout llShop;
	private ProgressDialog progressDialog;
	private List<MainItem> mainItems = new ArrayList<MainItem>();
	private ArrayAdapter<MainItem> arrayAdapter;
	private LinearLayout recommendOld;
	private LinearLayout nearNew;
	private LinearLayout nearOld;
	private int msgPagerNumber = 1;
	private ListView refreshableView;
	private MyDialog myDialog;
	private TextView tvPopContent;
	private TextView tvCity;
	private LinearLayout llCity;
	private boolean isRecommend;
	private LinearLayout llSearch;
	private ListView lvPopular;
	private List<MainItem> popularItems = new ArrayList<MainItem>();
	private ArrayAdapter<MainItem> popularAdapter;
	private LinearLayout headList;
	/*
	 * 定位相关
	 */
	private LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			NimApplication.city = location.getCity().replace("市", "");
			NimApplication.address = location.getAddrStr();
			NimApplication.lat = location.getLatitude();
			NimApplication.lng = location.getLongitude();
			tvCity.setText(NimApplication.city);
			uploadHotStore();
			uploadMsg(1);//获取商家数据
			// 退出时销毁定位
			mLocClient.stop();
		}
		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	public static MainFragment getInstance() {
		mainFragmentInstance = new MainFragment();
		return mainFragmentInstance;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		initView(view);
		initData();
		return view;
	}

	private void initLocation() {
		mLocClient = new LocationClient(getActivity());
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		option.setAddrType("all");
		mLocClient.setLocOption(option);
		mLocClient.start();
		
	}

	private void initView(View view) {
		lvMall = (PullToRefreshListView) view.findViewById(R.id.lv_mall);
		lvMall.setMode(Mode.BOTH);
		refreshableView = lvMall.getRefreshableView();
	    headList = (LinearLayout) LayoutInflater.from(
				getActivity()).inflate(R.layout.head_main, null);
//		refreshableView.addHeaderView(headList, null, false);
		
		recommendOld = (LinearLayout) view.findViewById(R.id.ll_recommend);
		nearNew = (LinearLayout) view.findViewById(R.id.ll_nearby_new);
		nearOld = (LinearLayout) headList.findViewById(R.id.ll_nearby);//head的附近商家栏
		lvPopular=(ListView) headList.findViewById(R.id.lv_popular);
		popularAdapter = new PopularAdapter(getActivity(), popularItems);
		lvPopular.setAdapter(popularAdapter);
		lvPopular.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent intent = new Intent(getActivity(),
						MerchantsActivity.class);
				intent.putExtra("storeId", popularItems.get(position)
						.getStoreId());
				startActivity(intent);
			}
		});
		
		llDazi = (LinearLayout) view.findViewById(R.id.ll_dazi);
		llDazi.setOnClickListener(this);
		llBusiness = (LinearLayout) view.findViewById(R.id.ll_business);
		llBusiness.setOnClickListener(this);
		llShop = (LinearLayout) view.findViewById(R.id.ll_shop);
		llShop.setOnClickListener(this);
		tvCity=(TextView) view.findViewById(R.id.tv_city);
		llCity=(LinearLayout) view.findViewById(R.id.ll_city);
		llCity.setOnClickListener(this);
        llSearch=(LinearLayout) view.findViewById(R.id.ll_search);
        llSearch.setOnClickListener(this);
		arrayAdapter = new MainAdapter(getActivity(), mainItems);
		lvMall.setAdapter(arrayAdapter);
		lvMall.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Intent intent = new Intent(getActivity(),
						MerchantsActivity.class);
				if(popularItems.size()>0){
					intent.putExtra("storeId", mainItems.get(position - 2)
							.getStoreId());
				}else{
					intent.putExtra("storeId", mainItems.get(position - 1)
							.getStoreId());
				}
				startActivity(intent);
			}
		});
		lvMall.getLoadingLayoutProxy(false, true).setPullLabel(
				getString(R.string.upFresh));
		lvMall.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (refreshView.isShownHeader()) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							msgPagerNumber = 1;
							bannerItems.clear();
							uploadBanner();
							uploadHotStore();
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
		icvBanner = (ImageCycleView) view.findViewById(R.id.icv_main_banner);
		refreshableView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
			}

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				int[] location = new int[2];
				recommendOld.getLocationInWindow(location);
				int y = location[1];

				int[] location1 = new int[2];
				nearOld.getLocationInWindow(location1);
				int y1 = location1[1];
				if (y < y1) {// 如果bar滑动到隐藏的bar之上，则让隐藏的bar显示，反之隐藏
					nearNew.setVisibility(View.GONE);
					recommendOld.setVisibility(View.VISIBLE);
				} else {
					nearNew.setVisibility(View.VISIBLE);
					recommendOld.setVisibility(View.GONE);

				}
			}
		});
	}

	/**
	 * 获取数据
	 */
	private void initData() {
		initLocation();
		uploadBanner();
	}

	/**
	 * 广告栏
	 */
	public ImageCycleViewListener mAdCycleViewListener = new ImageCycleViewListener() {
		@Override
		public void onImageClick(BannerItem info, int postion, View imageView) {
			 	Intent intentWeb=new Intent(getActivity(),H5MerchantsJoinActivity.class);
				intentWeb.putExtra("url",info.getContent());
				startActivity(intentWeb);
		}

		@Override
		public void displayImage(String imageURL, ImageView imageView) {
			Glide.with(mainFragmentInstance)
			.load(imageURL)
					.placeholder(R.color.gray2)
					.error(R.color.gray2)
					.skipMemoryCache( true )
					.centerCrop()
					.into(imageView);
		}
	};

	/**
	 * 获取广告数据
	 */
	private void uploadBanner() {
		HttpUtl.getHttp(ServerUrl.getBannerMsgUrl + "?adType=1" + "&status=1",
				getActivity(), new HttpCallbackListener() {
					@Override
					public void onFinish(JSONObject response) {
						JSONArray jsonArray;
						try {
							jsonArray = response.getJSONArray("data");
							if (jsonArray.length() > 0) {
								for (int i = 0; i < jsonArray.length(); i++) {
									JSONObject object = (JSONObject) jsonArray
											.get(i);
									BannerItem info = new BannerItem();
									info.setUrl(ServerUrl.getPicUrl
											+ object.getString("adPic"));
									info.setContent(object.getString("adUrl"));
									bannerItems.add(info);
									
								}
								icvBanner.setImageResources(bannerItems,
										mAdCycleViewListener);
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
	 * 人气推荐
	 * 
	 * @param numberPager
	 */
	public void uploadHotStore() {
		HttpUtl.getHttp(ServerUrl.getStoreListUrl+"&city="+DateUtil.setUTF8(NimApplication.city)+"&pageSize=1"+"&orderByOrders=1", getActivity(),
				new HttpCallbackListener() {
					@Override
					public void onFinish(JSONObject response) {
						JSONArray jsonArray;
						try {
							popularItems.clear();
							jsonArray = response.getJSONArray("data");
							if (jsonArray.length() > 0) {
								for (int i = 0; i < jsonArray.length(); i++) {
									JSONObject object = (JSONObject) jsonArray
											.get(i);
									//订单数大于0才加入
									if(object.getInt("orders")>0){
									MainItem mainItem = new MainItem(
											object.getString("storeId"),
											object.getString("storeName"),
											ServerUrl.getPicUrl+ object.getString("storePic"),
											object.getDouble("distance"),
											(int)object.getDouble("rateStar"),
											object.getDouble("perPrice"));
									popularItems.add(mainItem);
									}
								}
								//判断人气推荐列表是否有数据
								if(popularItems.size()>0){
								refreshableView.removeHeaderView(headList);
								refreshableView.addHeaderView(headList, null, false);
								}else{
									refreshableView.removeHeaderView(headList);
								}
							}else{
								refreshableView.removeHeaderView(headList);
							}
							popularAdapter.notifyDataSetChanged();
							SetListHeight.setListViewHeightBasedOnChildren(lvPopular);//解决listview添加head为listview的时候，head只显示一个item的问题
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
	 * 获取商家列表信息
	 * 
	 * @param numberPager
	 */
	public void uploadMsg(final int numberPager) {
		HttpUtl.getHttp(ServerUrl.getStoreListUrl+"&city="+DateUtil.setUTF8(NimApplication.city)+"&currentPage=" + numberPager+"&pageSize=15"+"&orderByDistance=1", getActivity(),
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
							
							DispearBg();
							lvMall.onRefreshComplete();// 停止刷新
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onError(VolleyError e) {
						lvMall.onRefreshComplete();// 停止刷新
						DispearBg();
					}

					@Override
					public void onFinishError(JSONObject response) {
						lvMall.onRefreshComplete();// 停止刷新
						DispearBg();
					}
				});
	}

	public void DispearBg() {
		MainActivity.isMsgFinish = true;
		MainActivity.DispearBg();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ll_dazi:
			if (NimApplication.LOGINED) {
				startActivity(new Intent(getActivity(), DaziActivity.class));
			} else {
				popDialog("您还没有登录，请登录后再查看搭子");
			}
			break;
		case R.id.ll_business:
			Intent intentBusiness=new Intent(getActivity(), BusinessActivity.class);
			startActivityForResult(intentBusiness, 0);

			break;
		case R.id.ll_city:
			Intent intentCity=new Intent(getActivity(), CityChoiceActivity.class);
			startActivityForResult(intentCity, 0);
			break;
		case R.id.ll_shop:
			Intent intentWeb=new Intent(getActivity(),H5MerchantsJoinActivity.class);
			intentWeb.putExtra("url","http://www.sansancloud.com/chainalliance/majiang/custom_page_index.html?index=1#eyJwYXJhbXMiOnt9LCJoYXNoIjoiY3VzdG9tX3BhZ2VfaW5kZXgiLCJ1cmwiOiJjdXN0b21fcGFnZV9pbmRleC5odG1sIn0=");
			startActivity(intentWeb);
			break;
		case R.id.ll_search:
			startActivity(new Intent(getActivity(),SearchActivity.class));
			break;
		default:
			break;
		}
	}
	@Override
	public void onResume() {
		super.onResume();
		icvBanner.startImageCycle();
	};

	@Override
	public void onPause() {
		super.onPause();
		icvBanner.pushImageCycle();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		icvBanner.pushImageCycle();
	}

	public void popDialog(String content) {
		myDialog = new MyDialog(getActivity(), R.style.inputDialog);
		tvPopContent = (TextView) myDialog.getContent();
		tvPopContent.setText(content);
		myDialog.setOnPositiveListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), LoginActivity.class));
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
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 0:
			if (resultCode == 0) {
				tvCity.setText(NimApplication.city);
				msgPagerNumber=1;
				uploadHotStore();
				uploadMsg(msgPagerNumber);//获取商家数据
			}
			break;

		default:
			break;
		}
	}

}
