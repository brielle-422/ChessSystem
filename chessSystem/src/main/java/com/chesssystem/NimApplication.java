package com.chesssystem;


import java.util.ArrayList;
import java.util.List;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.SDKInitializer;
import com.chesssystem.item.GoodsItem;
import com.chesssystem.widget.ImageOptHelper;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
/**
 * 
 * @author lyg
 * @time 2016-7-4上午9:47:39
 */
public class NimApplication extends Application {
	public static SharedPreferences sharedPreferences;
	public  static boolean LOGINED;
	private String account;
	private String password;
	public static List<GoodsItem>shoppingItems=new ArrayList<GoodsItem>();
	public static RequestQueue queues;
	public static String city;//城市
	public static String address;//地址
	public static double lat;//x轴坐标
	public static double lng;//y轴坐标
	public void onCreate(){
		SDKInitializer.initialize(this);
		queues=Volley.newRequestQueue(getApplicationContext());
		 initDate();
	     initImageLoader(getApplicationContext());
	 }
	private void initDate() {
		 sharedPreferences=getSharedPreferences("userInfor",MODE_PRIVATE);
		 account=sharedPreferences.getString("account", "");
		 password=sharedPreferences.getString("password", "");
		if(account.isEmpty()||password.isEmpty()){
			LOGINED=false;
		}else{
			LOGINED=true;
		}
	}
	public static RequestQueue getHttpQueues(){
		return queues;
	}
	// 初始化图片处理
	private void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPoolSize(5)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCache(new WeakMemoryCache())
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.defaultDisplayImageOptions(ImageOptHelper.imgOptions())
				.memoryCacheSize(2 * 1024 * 1024)    
				.discCacheSize(50 * 1024 * 1024)  
				.build();
		ImageLoader.getInstance().init(config);
	}
	
		
}
