package com.chesssystem.map;

import java.util.Comparator;

/*
 * 根据字母进行排序
 */
public class PinyinComparator implements Comparator<CityItem> {

	@Override
	public int compare(CityItem lhs, CityItem rhs) {
		return sort(lhs, rhs);
	}

	private int sort(CityItem lhs, CityItem rhs) {
		// 获取ascii值
		int lhs_ascii = lhs.getFpinyin().toUpperCase().charAt(0);
		int rhs_ascii = rhs.getFpinyin().toUpperCase().charAt(0);
		// 判断若不是字母，则排在字母之后
		if (lhs_ascii < 65 || lhs_ascii > 90)
			return 1;
		else if (rhs_ascii < 65 || rhs_ascii > 90)
			return -1;
		else
			//比较字母先后排序
			return lhs.getPinyin().compareTo(rhs.getPinyin());
	}

}
