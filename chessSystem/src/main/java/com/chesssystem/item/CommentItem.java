package com.chesssystem.item;

import java.util.List;
/**
 * 
 * @author lyg
 * @time 2016-7-4上午9:50:06
 */
public class CommentItem {
	private String storeName;
	private String storeId;
	private String name;
	private String content;
	private int star;
	private String face;
	private String time;
	private String reply;
	private List<String> images;
	/*
	 * 店铺里的评论信息
	 */
	public CommentItem(String name,String content,int star,String face,String time,String reply,List<String> images){
		this.name=name;
		this.content=content;
		this.star=star;
		this.face=face;
		this.time=time;
		this.reply=reply;
		this.images=images;
	}
	/*
	 * 我的评论列表信息
	 */
	public CommentItem(String storeId,String storeName,String content,int star,String time,String reply,List<String> images){
		this.storeId=storeId;
		this.storeName=storeName;
		this.content=content;
		this.star=star;
		this.time=time;
		this.reply=reply;
		this.images=images;
	}
	
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getStar() {
		return star;
	}
	public void setStar(int star) {
		this.star = star;
	}
	public String getFace() {
		return face;
	}
	public void setFace(String face) {
		this.face = face;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getReply() {
		return reply;
	}
	public void setReply(String reply) {
		this.reply = reply;
	}
	
	
}
