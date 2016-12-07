package com.chesssystem.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 数字处理
 * @author Administrator
 *
 */
public class DateUtil {
	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 将yyyy-MM-dd HH:mm:ss格式的日期转换成xx年xx月xx日上午xx:xx格式
	 * @param datetime
	 * @return
	 */
	public static String TimeToChange(String datetime){
		String date;
		String time ;
		String minute;
		Calendar calendar = Calendar.getInstance();
		Calendar now = Calendar.getInstance();
		try {
			calendar.setTime(dateFormat.parse(datetime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int h = calendar.get(Calendar.HOUR_OF_DAY);
		int m = calendar.get(Calendar.MINUTE);
		int s = calendar.get(Calendar.SECOND);
		if(m<10){
			minute="0"+m;
		}else{
			minute=m+"";
		}
		if(h==12){
			time="中午"+h+":"+minute;
		}else if(h>12){
			time="下午"+(h-12)+":"+minute;
		}else{
			time="上午"+h+":"+minute;
		}
		if(year==now.get(Calendar.YEAR)){
			date=month+"月"+day+"日"+time;
		}else{
			date=year+"年"+month+"月"+day+"日"+time;
		}
		return date;
	}
	/**
	 * 将yyyy-MM-dd HH:mm:ss格式的日期转换成xx年xx月xx日格式
	 * @param datetime
	 * @return
	 */
	public static String TimeOfDate(String datetime){
		String date;
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(dateFormat.parse(datetime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		date=year+"年"+month+"月"+day+"日";
		return date;
	}
	/**
	 * 将yyyy-MM-dd HH:mm:ss格式的日期转换成yyyy-MM-dd格式
	 * @param datetime
	 * @return
	 */
	public static String deleteHours(String datetime){
		String date;
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(dateFormat.parse(datetime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		date=year+"-"+month+"-"+day;
		return date;
	}
	public static String getAgeOld(String birthday){
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(dateFormat.parse(birthday));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int year = calendar.get(Calendar.YEAR);
		String date=year+"";
		return date.substring(2,3)+"0后";
	}
    /**
     * 验证手机号码格式
     * @param mobiles
     * @return
     */
    public static boolean checkMobileNumber(String mobileNumber){
        boolean flag = false;
        try{
                Pattern regex = Pattern.compile("^(((13[0-9])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$");
                Matcher matcher = regex.matcher(mobileNumber);
                flag = matcher.matches();
            }catch(Exception e){
                flag = false;
            }
        return flag;
    }
    /**
     * 判断时间是否在当前时间之后
     * @param datetime
     * @return
     */
    public static boolean checkTimeAndNow(String datetime){
    	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    	boolean flag = false;
    	Calendar nowcalendar = Calendar.getInstance();
    	int nowYear = nowcalendar.get(Calendar.YEAR);
		int nowMonth = nowcalendar.get(Calendar.MONTH)+1;
		int nowDay = nowcalendar.get(Calendar.DATE);
		int nowH = nowcalendar.get(Calendar.HOUR_OF_DAY);
		int nowM = nowcalendar.get(Calendar.MINUTE);
		
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(dateFormat.parse(datetime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int h = calendar.get(Calendar.HOUR_OF_DAY);
		int m = calendar.get(Calendar.MINUTE);
		
		
		if(year<nowcalendar.get(Calendar.YEAR)){
			flag=false;
		}
		else if(year==nowYear){
			if(month<nowMonth){
				flag=false;
			}else if(month==nowMonth){
				if(day<nowDay){
					flag=false;
				}else if(day==nowDay){
					if(h<nowH){
						flag=false;
					}else if(h==nowH){
						if(m<nowM){
							flag=false;
						}else{
							flag=true;
						}
					}else{
						flag=true;
					}
				}else{
					flag=true;
				}
			}else{
				flag=true;
			}
		}
    else{
			flag=true;
		}
		return flag;
    }
    /**
     * 判断时间是否在当前时间之后(无时分)
     * @param datetime
     * @return
     */
    public static boolean checkTimeAndNow_noHour(String datetime){
    	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    	boolean flag = false;
    	Calendar nowcalendar = Calendar.getInstance();
    	int nowYear = nowcalendar.get(Calendar.YEAR);
		int nowMonth = nowcalendar.get(Calendar.MONTH)+1;
		int nowDay = nowcalendar.get(Calendar.DATE);
		
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(dateFormat.parse(datetime));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		
		if(year<nowcalendar.get(Calendar.YEAR)){
			flag=false;
		}
		else if(year==nowYear){
			if(month<nowMonth){
				flag=false;
			}else if(month==nowMonth){
				if(day<=nowDay){
					flag=false;
				}else{
					flag=true;
				}
			}else{
				flag=true;
			}
		}
    else{
			flag=true;
		}
		return flag;
    }
    /**
     * 将中文统一改成utf-8格式
     */
    public static String setUTF8(String msg){
    	try {
    		msg=URLEncoder.encode(msg, "utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
    	return msg;
    }
}
