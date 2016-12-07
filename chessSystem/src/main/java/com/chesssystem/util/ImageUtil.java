package com.chesssystem.util;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
/**
 * 图片处理
 * @author Administrator
 *
 */
public class ImageUtil {
	/**
	 * url转Bitmap
	 * @param url
	 * @return
	 */
    public static Bitmap getLoacalBitmap(String url) {
		 BitmapFactory.Options options=new BitmapFactory.Options();
		 options.inJustDecodeBounds=false;
		 options.inSampleSize=4;
		 Bitmap bmp=BitmapFactory.decodeFile(url,options);
		 return bmp;
   }
    public static File  BitmapToFile(Bitmap bitmap){
	    FileOutputStream fileOutputStream = null;
	    try {
	        // 获取 SD 卡根目录
	        String saveDir = Environment.getExternalStorageDirectory() + "/chess_photos";
	        // 新建目录
	        File dir = new File(saveDir);
	        if (! dir.exists()) dir.mkdir();
	        // 生成文件名
	        SimpleDateFormat t = new SimpleDateFormat("yyyyMMddssSSS");
	        String filename = "MT" + (t.format(new Date())) + ".jpg";
	        // 新建文件
	        File file = new File(saveDir, filename);
	        // 打开文件输出流
	        fileOutputStream = new FileOutputStream(file);
	        // 生成图片文件
	        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
	        return file;
	        // 相片的完整路径
//	        String picPath = file.getPath();
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        if (fileOutputStream != null) {
	            try {
	                fileOutputStream.close();
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	    }
		return null;
    }
}
