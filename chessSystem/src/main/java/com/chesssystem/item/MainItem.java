package com.chesssystem.item;
/**
 * 
 * @author lyg
 * @time 2016-7-4上午9:50:27
 */
public class MainItem {
	private String storeId;
	private String imagepath;
	private String name;
	private String detail;
	private double distance;
	private String local;
	private String consumption;
	private int star;
	private double perPrice;
	/*
	 * 商家列表数据
	 */
	public MainItem(String storeId,String name,String detail,String imagePath,double distance,String local){
		this.storeId=storeId;
		this.name=name;
		this.detail=detail;
		this.imagepath=imagePath;
		this.distance=distance;
		this.local=local;
	}
	/*
	 * 人气推荐数据
	 */
	public MainItem(String storeId,String name,String imagePath,double distance,int star,double perPrice){
		this.storeId=storeId;
		this.name=name;
		this.imagepath=imagePath;
		this.distance=distance;
		this.star=star;
		this.perPrice=perPrice;
	}
	
	public double getPerPrice() {
		return perPrice;
	}
	public void setPerPrice(double perPrice) {
		this.perPrice = perPrice;
	}
	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String getImagepath() {
		return imagepath;
	}
	public void setImagepath(String imagepath) {
		this.imagepath = imagepath;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public String getConsumption() {
		return consumption;
	}
	public void setConsumption(String consumption) {
		this.consumption = consumption;
	}
	public int getStar() {
		return star;
	}
	public void setStar(int star) {
		this.star = star;
	}
	
}
