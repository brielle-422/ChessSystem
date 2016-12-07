package com.chesssystem.ui;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chesssystem.BaseActivity;
import com.chesssystem.MainActivity;
import com.chesssystem.NimApplication;
import com.chesssystem.R;
import com.chesssystem.R.id;
import com.chesssystem.adapter.MainAdapter;
import com.chesssystem.album.ImageFloder;
import com.chesssystem.album.ImageFloderAdapter;
import com.chesssystem.album.SelectPhotoActivity;
import com.chesssystem.expandtabview.PopBusinessAdapter;
import com.chesssystem.expandtabview.PopBusinessItem;
import com.chesssystem.item.MainItem;
import com.chesssystem.map.CityChoiceActivity;
import com.chesssystem.util.DateUtil;
import com.chesssystem.util.HttpUtl;
import com.chesssystem.util.ServerUrl;
import com.chesssystem.util.HttpUtl.HttpCallbackListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 
 * @author lyg
 * @time 2016-7-4上午9:51:08
 */
public class BusinessActivity extends BaseActivity {
	private LinearLayout llBack;
	private TextView tvTitle;
	private PullToRefreshListView lvMall;
	private List<MainItem> mainItems = new ArrayList<MainItem>();
	private ArrayAdapter<MainItem> arrayAdapter;
	private int msgPagerNumber = 1;
	private ListView refreshableView;
	private LinearLayout llAll;// 全部按钮
	private TextView tvAll;
	private ImageView ivall;
	private LinearLayout llNear;// 附近按钮
	private TextView tvNear;
	private ImageView ivNear;
	private LinearLayout llsort;// 筛选按钮
	private TextView tvSort;
	private ImageView ivSort;
	private LinearLayout llScreen;// 排序按钮
	private TextView tvScreen;
	private ImageView ivScreen;
	private Button btWifi;
	private Button btPark;
	private Button btBill;
	private Button btConfirm;
	private LinearLayout llTab;
	private View dirview;
	private PopupWindow popupWindow;
	private ListView dirListView;
	private RelativeLayout rlScreen;
	private int mScreenHeight;
	private int mScreenWidth;
	private LinearLayout llCity;
	private TextView tvCity;// 城市
	private TextView tvAddress;// 详细地址
	private List<PopBusinessItem> popDistanceItems = new ArrayList<PopBusinessItem>();
	private List<PopBusinessItem> popSortItems = new ArrayList<PopBusinessItem>();
	private List<PopBusinessItem> popScreenItems = new ArrayList<PopBusinessItem>();
	private PopBusinessAdapter floderAdapter;
	private LinearLayout llBottom;
	private LinearLayout llSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_business);
		initTab();
		initView();
	}

	private void initTab() {
		llTab = (LinearLayout) findViewById(R.id.ll_business_tab);
		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		mScreenHeight = outMetrics.heightPixels;
		mScreenWidth = outMetrics.widthPixels;
		llAll = (LinearLayout) findViewById(R.id.ll_business_all);
		llNear = (LinearLayout) findViewById(R.id.ll_business_near);
		llsort = (LinearLayout) findViewById(R.id.ll_business_sort);
		llScreen = (LinearLayout) findViewById(R.id.ll_business_screen);
		llAll.setOnClickListener(this);
		llNear.setOnClickListener(this);
		llsort.setOnClickListener(this);
		llScreen.setOnClickListener(this);
		tvAll = (TextView) findViewById(R.id.tv_business_all);
		ivall = (ImageView) findViewById(R.id.iv_business_all);
		tvNear = (TextView) findViewById(R.id.tv_business_near);
		ivNear = (ImageView) findViewById(R.id.iv_business_near);
		tvSort = (TextView) findViewById(R.id.tv_business_sort);
		ivSort = (ImageView) findViewById(R.id.iv_business_sort);
		tvScreen = (TextView) findViewById(R.id.tv_business_screen);
		ivScreen = (ImageView) findViewById(R.id.iv_business_screen);
		initTabMsg();

	}

	/**
	 * tab数据
	 */
	public void initTabMsg() {
		popDistanceItems.clear();
		popSortItems.clear();
		popScreenItems.clear();
		PopBusinessItem businessItem = new PopBusinessItem("1km", false);
		PopBusinessItem businessItem2 = new PopBusinessItem("2km", false);
		PopBusinessItem businessItem3 = new PopBusinessItem("3km", false);
		popDistanceItems.add(businessItem);
		popDistanceItems.add(businessItem2);
		popDistanceItems.add(businessItem3);
		PopBusinessItem businessItem4 = new PopBusinessItem("距离最近", false);
		PopBusinessItem businessItem5 = new PopBusinessItem("评分最高", false);
		PopBusinessItem businessItem6 = new PopBusinessItem("人气最高", false);
		popSortItems.add(businessItem4);
		popSortItems.add(businessItem5);
		popSortItems.add(businessItem6);
		PopBusinessItem businessItem7 = new PopBusinessItem("无线wifi上网", false);
		PopBusinessItem businessItem8 = new PopBusinessItem("提供停车位", false);
		PopBusinessItem businessItem9 = new PopBusinessItem("提供小吃", false);
		popScreenItems.add(businessItem7);
		popScreenItems.add(businessItem8);
		popScreenItems.add(businessItem9);
	}

	/**
	 * 初始化附近popupWindw
	 */
	private void initPopupWindw(final List<PopBusinessItem> items, int type) {
		if (popupWindow == null) {
			dirview = LayoutInflater.from(BusinessActivity.this).inflate(
					R.layout.pop_business_tab, null);
			dirListView = (ListView) dirview.findViewById(R.id.id_list_dirs);
			rlScreen = (RelativeLayout) dirview.findViewById(R.id.rl_pop_top);
			popupWindow = new PopupWindow(dirview, LayoutParams.MATCH_PARENT,
					mScreenHeight);
			// floderAdapter=new PopBusinessAdapter(BusinessActivity.this,
			// items);
			// dirListView.setAdapter(floderAdapter);
			btWifi = (Button) dirview.findViewById(R.id.bt_pop_wifi);
			btPark = (Button) dirview.findViewById(R.id.bt_pop_park);
			btBill = (Button) dirview.findViewById(R.id.bt_pop_bill);
			btConfirm = (Button) dirview.findViewById(R.id.bt_pop_confirm);
			btWifi.setOnClickListener(this);
			btPark.setOnClickListener(this);
			btBill.setOnClickListener(this);
			btConfirm.setOnClickListener(this);
			llBottom = (LinearLayout) dirview.findViewById(R.id.ll_pop_other);
		}
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		// 为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		// 显示的位置为:屏幕的宽度的一半-PopupWindow的高度的一半
		// titleIcon.setBackgroundResource(R.drawable.navigationbar_arrow_up);
		popupWindow.showAsDropDown(llTab, 0, 0);
		popupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				initColor();
			}
		});
		llBottom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				popupWindow.dismiss();
			}
		});
		if (type == 2) {
			rlScreen.setVisibility(View.VISIBLE);
			dirListView.setVisibility(View.GONE);
		} else {
			dirListView.setVisibility(View.VISIBLE);
			rlScreen.setVisibility(View.GONE);
			floderAdapter = new PopBusinessAdapter(BusinessActivity.this, items);
			dirListView.setAdapter(floderAdapter);
			dirListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					if (items.get(position).isSelected()) {
						items.get(position).setSelected(false);
					} else {
						items.get(position).setSelected(true);
					}
					for (int i = 0; i < items.size(); i++) {
						if (position != i) {
							items.get(i).setSelected(false);
						}
					}
					floderAdapter.changeData(items);
					popupWindow.dismiss();
					msgPagerNumber = 1;
					uploadMsg(getScreenUrl(), msgPagerNumber);

				}
			});
		}

	}

	private void initView() {
		llBack = (LinearLayout) findViewById(R.id.ll_back);
		llBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.business);
		llSearch=(LinearLayout) findViewById(R.id.ll_search);
		llSearch.setOnClickListener(this);
		// listview
		LinearLayout headList = (LinearLayout) LayoutInflater.from(this)
				.inflate(R.layout.head_business, null);
		tvAddress = (TextView) headList.findViewById(R.id.tv_address);
		tvAddress.setText(NimApplication.address);
		tvCity = (TextView) findViewById(R.id.tv_city);
		llCity = (LinearLayout) findViewById(R.id.ll_city);
		llCity.setOnClickListener(this);
		tvCity.setText(NimApplication.city);
		lvMall = (PullToRefreshListView) findViewById(R.id.lv_business_merchants);
		refreshableView = lvMall.getRefreshableView();
		refreshableView.addHeaderView(headList, null, false);
		arrayAdapter = new MainAdapter(this, mainItems);
		lvMall.setAdapter(arrayAdapter);
		lvMall.setMode(Mode.BOTH);
		lvMall.getLoadingLayoutProxy(false, true).setPullLabel(
				getString(R.string.upFresh));
		/**
		 * 上拉、下拉刷新
		 */
		lvMall.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (refreshView.isShownHeader()) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							msgPagerNumber = 1;
							uploadMsg(getScreenUrl(), msgPagerNumber);
						}
					}, 1000);
				}
				if (refreshView.isShownFooter()) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							uploadMsg(getScreenUrl(), msgPagerNumber);
						}
					}, 1000);
				}
			}
		});
		/**
		 * 点击List Item
		 */
		lvMall.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Intent intent = new Intent(BusinessActivity.this,
						MerchantsActivity.class);
				intent.putExtra("storeId", mainItems.get(position - 2)
						.getStoreId());							
				startActivity(intent);
			}
		});
		uploadMsg(ServerUrl.getStoreListUrl + "&city=" + NimApplication.city,
				msgPagerNumber);
	}

	private void uploadMsg(final String url, final int numberPager) {
		HttpUtl.getHttp(url + "&currentPage=" + numberPager, this,
				new HttpCallbackListener() {
					@Override
					public void onFinish(JSONObject response) {
						try {
							JSONArray jsonArray = response.getJSONArray("data");
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
											ServerUrl.getPicUrl
													+ object.getString("storePic"),
											object.getDouble("distance"),
											object.getString("storeAdd"));
									mainItems.add(mainItem);
								}
								msgPagerNumber++;
							}
							arrayAdapter.notifyDataSetChanged();
							lvMall.onRefreshComplete();// 停止刷新
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onError(VolleyError e) {
						lvMall.onRefreshComplete();// 停止刷新
					}

					@Override
					public void onFinishError(JSONObject response) {
						lvMall.onRefreshComplete();// 停止刷新
					}
				});
	}

	public void initColor() {
		tvAll.setTextColor(getResources().getColor(
				R.color.text_color_heise_504f4f));
		ivall.setImageResource(R.drawable.triangle);
		tvNear.setTextColor(getResources().getColor(
				R.color.text_color_heise_504f4f));
		ivNear.setImageResource(R.drawable.triangle);
		tvSort.setTextColor(getResources().getColor(
				R.color.text_color_heise_504f4f));
		ivSort.setImageResource(R.drawable.triangle);
		tvScreen.setTextColor(getResources().getColor(
				R.color.text_color_heise_504f4f));
		ivScreen.setImageResource(R.drawable.triangle);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ll_back:
			finish();
			break;
		// 点击弹出框的全部按钮
		case R.id.ll_business_all:
			initTabMsg();
			msgPagerNumber = 1;
			uploadMsg(getScreenUrl(), msgPagerNumber);
			break;
		// 点击弹出框的附近按钮
		case R.id.ll_business_near:
			tvNear.setTextColor(getResources().getColor(R.color.main_green));
			ivNear.setImageResource(R.drawable.triangle_top);
			initPopupWindw(popDistanceItems, 0);
			break;
		// 点击弹出框的智能排序按钮
		case R.id.ll_business_sort:
			tvSort.setTextColor(getResources().getColor(R.color.main_green));
			ivSort.setImageResource(R.drawable.triangle_top);
			initPopupWindw(popSortItems, 1);
			break;
		// 点击弹出框的筛选按钮
		case R.id.ll_business_screen:
			tvScreen.setTextColor(getResources().getColor(R.color.main_green));
			ivScreen.setImageResource(R.drawable.triangle_top);
			initPopupWindw(popScreenItems, 2);
			break;
		// 点击弹出框的无线wifi上网按钮
		case R.id.bt_pop_wifi:
			if (!popScreenItems.get(0).isSelected()) {
				btWifi.setBackgroundResource(R.drawable.circle_transparent_green);
				btWifi.setTextColor(getResources().getColor(R.color.main_green));
				popScreenItems.get(0).setSelected(true);
			} else {
				btWifi.setBackgroundResource(R.drawable.circle_transparent_black);
				btWifi.setTextColor(getResources().getColor(
						R.color.text_color_shenhui_677582));
				popScreenItems.get(0).setSelected(false);
			}
			break;
		// 点击弹出框的提供停车位按钮
		case R.id.bt_pop_park:
			if (!popScreenItems.get(1).isSelected()) {
				btPark.setBackgroundResource(R.drawable.circle_transparent_green);
				btPark.setTextColor(getResources().getColor(R.color.main_green));
				popScreenItems.get(1).setSelected(true);
			} else {
				btPark.setBackgroundResource(R.drawable.circle_transparent_black);
				btPark.setTextColor(getResources().getColor(
						R.color.text_color_shenhui_677582));
				popScreenItems.get(1).setSelected(false);
			}
			break;
		// 点击弹出框的支持开发票按钮
		case R.id.bt_pop_bill:
			if (!popScreenItems.get(2).isSelected()) {
				btBill.setBackgroundResource(R.drawable.circle_transparent_green);
				btBill.setTextColor(getResources().getColor(R.color.main_green));
				popScreenItems.get(2).setSelected(true);
			} else {
				btBill.setBackgroundResource(R.drawable.circle_transparent_black);
				btBill.setTextColor(getResources().getColor(
						R.color.text_color_shenhui_677582));
				popScreenItems.get(2).setSelected(false);
			}
			break;
		// 点击弹出框的确定按钮
		case R.id.bt_pop_confirm:
			popupWindow.dismiss();
			msgPagerNumber = 1;
			uploadMsg(getScreenUrl(), msgPagerNumber);
			break;
		// 点击城市
		case R.id.ll_city:
			Intent intentCity = new Intent(BusinessActivity.this,
					CityChoiceActivity.class);
			startActivityForResult(intentCity, 1);
			break;
		case R.id.ll_search:
			startActivity(new Intent(BusinessActivity.this,SearchActivity.class));
			break;

		default:
			break;
		}
	}

	public String getScreenUrl() {
		String url = ServerUrl.getStoreListUrl + "&city=" + DateUtil.setUTF8(NimApplication.city)+"&pageSize=15";
		/*
		 * 附近
		 */
		for (int i = 0; i < popDistanceItems.size(); i++) {
			if (popDistanceItems.get(i).isSelected())
				url += "&distanceForme=" + (i + 1) + "000";
		}
		/*
		 * 排序
		 */
		if (popSortItems.size() > 0) {
			if (popSortItems.get(0).isSelected()) {
				url += "&orderByDistance=1";
			}
			if (popSortItems.get(1).isSelected()) {
				url += "&orderByRates=1";
			}
			if (popSortItems.get(2).isSelected()) {
				url += "&orderByOrders=1";
			}
		}
		/*
		 * 筛选
		 */
		if (popScreenItems.size() > 0) {
			if (popScreenItems.get(0).isSelected()) {
				url += "&attr_wifi=1";
			}
			if (popScreenItems.get(1).isSelected()) {
				url += "&attr_parking=1";
			}
			if (popScreenItems.get(2).isSelected()) {
				url += "&attr_eating=1";
			}
		}
		return url;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			if (resultCode == 0 && data != null) {
				tvCity.setText(NimApplication.city);
				initTabMsg();
				popupWindow = null;
				msgPagerNumber = 1;
				uploadMsg(getScreenUrl(), msgPagerNumber);// 获取商家数据
			}
			break;

		default:
			break;
		}
	}
}
