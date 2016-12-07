package com.chesssystem.map;

public class CityItem {

	private String cityName;
	private String pinyin;
	private String fpinyin;

	public CityItem(String cityName){
		this.cityName=cityName;
	}
	public CityItem(){
		super();
	}
	
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	public String getPinyin() {
		return pinyin;
	}

	public void setFpinyin(String fpinyin) {
		this.fpinyin = fpinyin;
	}

	public String getFpinyin() {
		return fpinyin;
	}

	public String toString() {
		return "城市：" + getCityName() + "   拼音：" + getPinyin() + "    首字母："
				+ getFpinyin();

	}
}
