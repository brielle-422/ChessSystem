package com.chesssystem.item;

import java.io.File;

import android.graphics.Bitmap;
/**
 * 发布评论页面的图片
 * @author Administrator
 *
 */
public class CommentPublishItem {
	private String imageUrl;
	private File imageFile;
	private Bitmap imageBitmap;
	public CommentPublishItem(String imageUrl,File imageFile,Bitmap imageBitmap){
		this.imageFile=imageFile;
		this.imageUrl=imageUrl;
		this.imageBitmap=imageBitmap;
	}
	
	public Bitmap getImageBitmap() {
		return imageBitmap;
	}

	public void setImageBitmap(Bitmap imageBitmap) {
		this.imageBitmap = imageBitmap;
	}

	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public File getImageFile() {
		return imageFile;
	}
	public void setImageFile(File imageFile) {
		this.imageFile = imageFile;
	}
	
}
