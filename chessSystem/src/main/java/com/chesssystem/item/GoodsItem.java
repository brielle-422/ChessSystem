package com.chesssystem.item;

import java.io.Serializable;

/**
 * 
 * @author lyg
 * @time 2016-7-4上午9:50:19
 */
public class GoodsItem implements Serializable{

	private String goodsId;
	private String imageUrl;
	private String goodsName;
	private int goodsSales;//月销量
	private double goodsPrice;
	private String goodsType;
	private int choiceNumber;//订单中选择的个数
	/*
	 * 商品展示
	 */
	public GoodsItem(String goodsId,String goodsName,String imageUrl,int goodsSales,double goodsPrice
			,String goodsType,int choiceNumber){
		this.goodsId=goodsId;
		this.goodsName=goodsName;
		this.imageUrl=imageUrl;
		this.goodsSales=goodsSales;
		this.goodsPrice=goodsPrice;
		this.goodsType=goodsType;
		this.choiceNumber=choiceNumber;
	}
	public int getChoiceNumber() {
		return choiceNumber;
	}
	public void setChoiceNumber(int choiceNumber) {
		this.choiceNumber = choiceNumber;
	}
	public String getGoodsType() {
		return goodsType;
	}

	public void setGoodsType(String goodsType) {
		this.goodsType = goodsType;
	}

	public int getGoodsSales() {
		return goodsSales;
	}
	public void setGoodsSales(int goodsSales) {
		this.goodsSales = goodsSales;
	}
	public double getGoodsPrice() {
		return goodsPrice;
	}
	public void setGoodsPrice(double goodsPrice) {
		this.goodsPrice = goodsPrice;
	}
	public String getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	
}

