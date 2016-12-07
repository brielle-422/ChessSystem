package com.chesssystem.picktime;

import java.text.SimpleDateFormat;
/**
 * 
 * @author lyg
 * @time 2016-7-4上午9:50:50
 */
public class JudgeDate {

	public static  boolean isDate(String str_input,String rDateFormat){
		if (!isNull(str_input)) {
	         SimpleDateFormat formatter = new SimpleDateFormat(rDateFormat);
	         formatter.setLenient(false);
	         try {
	             formatter.format(formatter.parse(str_input));
	         } catch (Exception e) {
	             return false;
	         }
	         return true;
	     }
		return false;
	}
	public static boolean isNull(String str){
		if(str==null)
			return true;
		else
			return false;
	}
}