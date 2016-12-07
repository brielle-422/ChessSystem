package com.chesssystem.item;
/**
 * 搭子内容信息
 * @author lyg
 * @time 2016-7-1下午4:08:27
 */
public class DaziItem {
	private String id;//搭子Id
	private String image;//头像
	private String name;//名称
	private String time;//开始时间
	private String local;//地点
	private int number;//人数
	private String content;//内容
	private String telphone;//联系方式
	private String createTime;//创建时间
	private int sex;//0表示女;1表示男
	private String birthday;//生日
	/**
	 * 搭子首页列表数据
	 * @param image
	 * @param name
	 * @param time
	 * @param local
	 * @param number
	 * @param content
	 * @param telphone
	 */
	public DaziItem(String id,String image,String name,String birthday,int sex,String time,String local,int number,
			String content,String telphone){
		this.id=id;
		this.image=image;
		this.name=name;
		this.birthday=birthday;
		this.sex=sex;
		this.time=time;
		this.local=local;
		this.number=number;
		this.content=content;
		this.telphone=telphone;
	}
	/**
	 * 我的邀约、应约列表数据
	 * @param time
	 * @param local
	 * @param number
	 * @param content
	 * @param createTime
	 */
	public DaziItem(String id,String time,String local,int number,
			String content,String createTime){
		this.id=id;
		this.time=time;
		this.local=local;
		this.number=number;
		this.content=content;
		this.createTime=createTime;
	}
	
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getLocal() {
		return local;
	}
	public void setLocal(String local) {
		this.local = local;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTelphone() {
		return telphone;
	}
	public void setTelphone(String telphone) {
		this.telphone = telphone;
	}
	
}
