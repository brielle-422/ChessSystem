package com.chesssystem.item;
/**
 * 搭子应约用户信息
 */
public class DaziUserItem {
	private String userId;//用户id
	private String memberId;//用户所在搭子的id
	private String imageFace;//头像
	private String name;//名称
	private int sex;//性别
	private String old;//年龄
	private String contact;//内容
	private String joinTime;//申请、加入时间
	private int goodRate;//好评
	private int midRate;//中评
	private int lowRate;//差评
	private int joinState;//加入状态，表示成功、失败、待审核
	private int rateState;//评价状态，表示好评、中评、差评、未评价
	private String lugId;//搭子id
	/*
	 * 搭子应约人员
	 */
	public DaziUserItem(String userId,String memberId,String imageFace,String name,int sex,String old,
			String contact,String joinTime,int goodRate,int midRate,int lowRate,int joinState){
		this.userId=userId;
		this.memberId=memberId;
		this.imageFace=imageFace;
		this.name=name;
		this.sex=sex;
		this.old=old;
		this.contact=contact;
		this.joinTime=joinTime;
		this.goodRate=goodRate;
		this.midRate=midRate;
		this.lowRate=lowRate;
		this.joinState=joinState;
	}
	/*
	 * 搭子记录评价
	 */
	public DaziUserItem(String userId,String lugId,String imageFace,String name,int sex,String old,
			String contact,int goodRate,int midRate,int lowRate,int rateState){
		this.userId=userId;
		this.lugId=lugId;
		this.imageFace=imageFace;
		this.name=name;
		this.sex=sex;
		this.old=old;
		this.contact=contact;
		this.goodRate=goodRate;
		this.midRate=midRate;
		this.lowRate=lowRate;
		this.rateState=rateState;
	}
	
	public String getMemverId() {
		return memberId;
	}
	public void setMemverId(String memverId) {
		this.memberId = memverId;
	}
	public String getUserId() {
		return userId;
	}
	public String getMemberId() {
		return memberId;
	}
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	public String getLugId() {
		return lugId;
	}
	public void setLugId(String lugId) {
		this.lugId = lugId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public int getRateState() {
		return rateState;
	}
	public void setRateState(int rateState) {
		this.rateState = rateState;
	}
	public String getImageFace() {
		return imageFace;
	}
	public void setImageFace(String imageFace) {
		this.imageFace = imageFace;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public String getOld() {
		return old;
	}
	public void setOld(String old) {
		this.old = old;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getJoinTime() {
		return joinTime;
	}
	public void setJoinTime(String joinTime) {
		this.joinTime = joinTime;
	}
	public int getGoodRate() {
		return goodRate;
	}
	public void setGoodRate(int goodRate) {
		this.goodRate = goodRate;
	}
	public int getMidRate() {
		return midRate;
	}
	public void setMidRate(int midRate) {
		this.midRate = midRate;
	}
	public int getLowRate() {
		return lowRate;
	}
	public void setLowRate(int lowRate) {
		this.lowRate = lowRate;
	}
	public int getJoinState() {
		return joinState;
	}
	public void setJoinState(int joinState) {
		this.joinState = joinState;
	}
	
	
}
