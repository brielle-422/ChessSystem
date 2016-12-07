package com.chesssystem.album;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chesssystem.R;
import com.chesssystem.widget.MyImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

public class previewActivity extends Activity {
	private ViewPager mViewPager;
	private ImageLoader imageLoader;
	private ImageView[] mImageViews;
	private String imageURl;
	private String[] imageUrls;
	private String[] filePaths;
	private ImageView[] tips;
	private int position;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview);
		imageLoader = ImageLoader.getInstance();
		initImage();
		initView();
	}
	private void initImage() {
		Intent intent = getIntent();
		imageURl = intent.getStringExtra("image");
//		position = intent.getIntExtra("position", position);
//		imageUrls = new String[20];
//		imageUrls = CheckFormat.splitImagePath(imageURl);
		filePaths = new String[ 1];
//		for (int i = 1; i < imageUrls.length; i++) {
//			filePaths[i - 1] = imageUrls[i];
//		}
		filePaths[0]=imageURl;
		mImageViews = new ImageView[filePaths.length];
	}

	private void initView() {
		/*
		 *  底部圆点
		 */
		ViewGroup group = (ViewGroup) findViewById(R.id.viewGroup);
		tips = new ImageView[mImageViews.length];
		for (int i = 0; i < tips.length; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setLayoutParams(new LayoutParams(10, 10));
			tips[i] = imageView;
			if (i == position) {
				tips[i].setImageResource(R.drawable.page_indicator_focused);
			} else {
				tips[i].setImageResource(R.drawable.page_indicator_unfocused);
			}

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
			layoutParams.leftMargin = 5;
			layoutParams.rightMargin = 5;
			group.addView(imageView, layoutParams);
		}
		/*
		 * viewPager
		 */
		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		mViewPager.setAdapter(new PagerAdapter() {
			@Override
			public Object instantiateItem(final ViewGroup container, final int position) {
				final MyImageView imageView = new MyImageView(getApplicationContext());
				Glide.with(previewActivity.this)
				.load(filePaths[position].replace("getPic.do", "getPicBig"))
						.placeholder(R.color.gray2)
						.error(R.color.gray2)
						.skipMemoryCache( true )
						.centerCrop()
						.into(imageView);
				container.addView(imageView);
				mImageViews[position] = imageView;
//				imageLoader.displayImage(filePaths[position], imageView,new ImageLoadingListener() {
//					@Override
//					public void onLoadingStarted(String arg0, View arg1) {
//						//开始加载
//					}
//					@Override
//					public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
//						//加载失败,重新加载
////						container.addView(imageView);
////						mImageViews[position] = imageView;
//						imageLoader.displayImage(filePaths[position], imageView);
//					}
//					@Override
//					public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
//						//加载成功
//						container.addView(imageView);
//						mImageViews[position] = imageView;
//					}
//					
//					@Override
//					public void onLoadingCancelled(String arg0, View arg1) {
//						//加载取消
//					}
//				});// 加个回调，判断图片加载完成在将图片存入数组
				
				return imageView;
			}
			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				container.removeView(mImageViews[position]);
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return filePaths.length;
			}
		});
		mViewPager.setCurrentItem(position);//默认显示页
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				for(int i=0; i<tips.length; i++){  
		            if(i == arg0){  
		                tips[i].setImageResource(R.drawable.page_indicator_focused);  
		            }else{  
		                tips[i].setImageResource(R.drawable.page_indicator_unfocused);  
		            }  
		        }
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
	
	}

}
