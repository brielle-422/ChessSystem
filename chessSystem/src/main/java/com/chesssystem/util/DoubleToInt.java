package com.chesssystem.util;

import java.text.DecimalFormat;

/**
 * 计量单位
 * @author lyg
 * @time 2016-7-4上午9:52:24
 */
public class DoubleToInt {
	/*
	 * 去掉金额小数点，并保存为string
	 */
	public static String DoubleToInt(double number) {
		if (number % 1.0 == 0) {
			return (int) number + "";
		}
		return number + "";
	}

	/*
	 * 将double距离转为m,km等单位计量
	 */
	public static String DoubleToDistance(double distance) {
		if (distance > 10000) {
			return ">10km";
		} else if (distance > 1000 && distance < 10000) {
			DecimalFormat df = new DecimalFormat("######0.0");
			return df.format(distance / 1000) + "km";

		} else {
			return (int) distance + "m";
		}
	}
}
