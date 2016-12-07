package com.chesssystem.widget;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * 动态设置Listview高度
 * @author Administrator
 *
 */
public class SetListHeight {
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		
		ListAdapter adapter = listView.getAdapter();
		
		if(adapter != null) {
		
		int totalHeight = 0;
		
		for(int i=0; i<adapter.getCount(); i++) {
		
		View listItem = adapter.getView(i, null, listView);
		
		listItem.measure(0, 0);
		
		totalHeight += listItem.getMeasuredHeight();
		
		}
		
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		
		params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
		
		((MarginLayoutParams) params).setMargins(0, 0, 0, 0);
		
		listView.setLayoutParams(params);
		
		System.out.println(params.height + "===" + adapter.getCount());
		
		}
		
		}
}
