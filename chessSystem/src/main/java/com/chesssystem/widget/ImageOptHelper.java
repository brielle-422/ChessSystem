package com.chesssystem.widget;

import android.graphics.Bitmap;

import com.chesssystem.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
/**
 * 
 * @author lyg
 * @time 2016-7-4上午9:52:47
 */
public class ImageOptHelper {
	//加载图片
	public static DisplayImageOptions imgOptions() {
		DisplayImageOptions imgOptions = new DisplayImageOptions.Builder()
			.cacheOnDisc(true)//缓存到磁盘
			.cacheInMemory(false)//缓存到内存
			.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.showStubImage(R.color.gray2)
			.showImageForEmptyUri(R.color.gray2)
			.showImageOnFail(R.color.gray2)
			.build();
		return imgOptions;
	}
}
