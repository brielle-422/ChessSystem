package com.chesssystem.expandtabview;
/**
 * 
 * @author lyg
 * @time 2016-7-4上午9:49:12
 */
public class PopBusinessItem {

	private String name;
	private boolean isSelected;
	public PopBusinessItem(String name,boolean isSelected){
		this.name=name;
		this.isSelected=isSelected;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
}
