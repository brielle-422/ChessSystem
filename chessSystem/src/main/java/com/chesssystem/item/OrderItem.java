package com.chesssystem.item;
/**
 * 
 * @author lyg
 * @time 2016-7-4上午9:50:23
 */
public class OrderItem {
	private String orderId;
	private String orderNumber;
	private String storeId;
	private String storeName;
	private String createTime;
	private double orderPrice;
	private int payStatus;//付款状态
	private int status;//订单状态
	private int isRate;//是否评论
	private String orderImage;
	public OrderItem(String orderId,String orderNumber,
			String storeId,String storeName,String time,double money,
			int payStatus,String image,int status,int isRate){
		this.orderId=orderId;
		this.orderNumber=orderNumber;
		this.storeId=storeId;
		this.storeName=storeName;
		this.createTime=time;
		this.orderPrice=money;
		this.payStatus=payStatus;
		this.orderImage=image;
		this.status=status;
		this.isRate=isRate;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int isRate() {
		return isRate;
	}

	public void setRate(int isRate) {
		this.isRate = isRate;
	}

	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public double getOrderPrice() {
		return orderPrice;
	}
	public void setOrderPrice(double orderPrice) {
		this.orderPrice = orderPrice;
	}
	public int getPayStatus() {
		return payStatus;
	}
	public void setPayStatus(int payStatus) {
		this.payStatus = payStatus;
	}
	public String getOrderImage() {
		return orderImage;
	}
	public void setOrderImage(String orderImage) {
		this.orderImage = orderImage;
	}


}
