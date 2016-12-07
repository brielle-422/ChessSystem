package com.chesssystem.map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.chesssystem.BaseActivity;
import com.chesssystem.R;
/**
 * 
 * @author lyg
 * @time 2016-7-4上午9:50:38
 */
public class MapActivity extends BaseActivity {
	    // 定位相关
	    LocationClient mLocClient;
	    public MyLocationListenner myListener = new MyLocationListenner();
	    private LocationMode mCurrentMode;
	    BitmapDescriptor mCurrentMarker;
	    private static final int accuracyCircleFillColor = 0xAAFFFF88;
	    private static final int accuracyCircleStrokeColor = 0xAA00FF00;
	    MapView mMapView;
	    BaiduMap mBaiduMap;
	    private String lat;
	    private String lng;
	    private String storeName;
	    private String storeAdd;
	    private LinearLayout llBack;
	    private TextView tvStoreName;
	    private TextView tvStoreAdd;

	    // UI相关
	    boolean isFirstLoc = true; // 是否首次定位

	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_map);
	        initView();
	    }

	    private void initView() {
	    	Intent intent=getIntent();
	        storeName=intent.getStringExtra("storeName");
	        storeAdd=intent.getStringExtra("storeAdd");
	        lat=intent.getStringExtra("lat");
	        lng=intent.getStringExtra("lng");
	        llBack=(LinearLayout) findViewById(R.id.ll_map_back);
	        llBack.setOnClickListener(this);
	        tvStoreName=(TextView) findViewById(R.id.tv_map_name);
	        tvStoreName.setText(storeName);
	        tvStoreAdd=(TextView) findViewById(R.id.tv_map_add);
	        tvStoreAdd.setText(storeAdd);
	        mCurrentMode = LocationMode.NORMAL;
	        // 地图初始化
	        mMapView=(MapView) findViewById(R.id.bmapview);
			mBaiduMap=mMapView.getMap();
	        // 开启定位图层
	        mBaiduMap.setMyLocationEnabled(true);
	        // 定位初始化
	        mLocClient = new LocationClient(this);
	        mLocClient.registerLocationListener(myListener);
	        LocationClientOption option = new LocationClientOption();
	        option.setOpenGps(true); // 打开gps
	        option.setCoorType("bd09ll"); // 设置坐标类型
	        option.setScanSpan(1000);
	        mLocClient.setLocOption(option);
	        mLocClient.start();
		}

		/**
	     * 定位SDK监听函数
	     */
	    public class MyLocationListenner implements BDLocationListener {

	        @Override
	        public void onReceiveLocation(BDLocation location) {
	            // map view 销毁后不在处理新接收的位置
	            if (location == null || mMapView == null) {
	                return;
	            }
	            MyLocationData locData = new MyLocationData.Builder()
	                    .accuracy(location.getRadius())
	                            // 此处设置开发者获取到的方向信息，顺时针0-360
	                    .direction(100).latitude(Double.valueOf(lat))
	                    .longitude(Double.valueOf(lng)).build();
	            mBaiduMap.setMyLocationData(locData);
	            if (isFirstLoc) {
	                isFirstLoc = false;
	                LatLng ll = new LatLng(Double.valueOf(lat),
	                Double.valueOf(lng));
	                MapStatus.Builder builder = new MapStatus.Builder();
	                builder.target(ll).zoom(18.0f);
	                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
	            }
	        }

	        public void onReceivePoi(BDLocation poiLocation) {
	        }
	    }

	    @Override
	    protected void onPause() {
	        mMapView.onPause();
	        super.onPause();
	    }

	    @Override
	    protected void onResume() {
	        mMapView.onResume();
	        super.onResume();
	    }

	    @Override
	    protected void onDestroy() {
	        // 退出时销毁定位
	        mLocClient.stop();
	        // 关闭定位图层
	        mBaiduMap.setMyLocationEnabled(false);
	        mMapView.onDestroy();
	        mMapView = null;
	        super.onDestroy();
	    }

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.ll_map_back:
				finish();
				break;

			default:
				break;
			}
		}

	}
