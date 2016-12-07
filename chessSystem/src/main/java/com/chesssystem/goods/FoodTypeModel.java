package com.chesssystem.goods;

/**
 * 菜品类型
 * 
 * @author zkw
 * 
 */
public class FoodTypeModel {
	private String foodTypeName;
	private boolean isChangeColor;
	private int itemPosition;// 菜品类型在菜品列表中对应的位置
	private int typePosition;// 记录一下当前菜品类型在菜品类型列表中的position

	public FoodTypeModel(String foodTypeName, boolean isChangeColor, int itemPosition, int typePosition) {
		super();
		this.foodTypeName = foodTypeName;
		this.isChangeColor = isChangeColor;
		this.itemPosition = itemPosition;
		this.typePosition = typePosition;
	}

	public String getFoodTypeName() {
		return foodTypeName;
	}

	public void setFoodTypeName(String foodTypeName) {
		this.foodTypeName = foodTypeName;
	}

	public boolean isChangeColor() {
		return isChangeColor;
	}

	public void setChangeColor(boolean isChangeColor) {
		this.isChangeColor = isChangeColor;
	}

	public int getItemPosition() {
		return itemPosition;
	}

	public void setItemPosition(int itemPosition) {
		this.itemPosition = itemPosition;
	}

	public int getTypePosition() {
		return typePosition;
	}

	public void setTypePosition(int typePosition) {
		this.typePosition = typePosition;
	}

}
