package com.chesssystem.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

/**
 * @文件描述	: 城市名文件工具类
 */
public class CityFileUtils {
	/**
	 * 读取城市配置文件
	 * 
	 * @param context
	 * @return
	 */
	public static List<String> getCityFile(Context context) {
		try {
			List<String> list = new ArrayList<String>();
			InputStream in = context.getResources().getAssets().open("city");
			BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
			String str = null;
			while ((str = br.readLine()) != null) {
				list.add(str);
			}

			return list;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
