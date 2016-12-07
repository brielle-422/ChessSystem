package com.chesssystem.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;
/**
 * 解决scrollView嵌套listView只显示一行问题
 * @author Administrator
 *
 */
public class BaseListView extends ListView {

	public BaseListView(Context context) {
		super(context);
	}
	   public BaseListView(Context context, AttributeSet attrs) {  
	        // TODO Auto-generated method stub  
	        super(context, attrs);  
	    }  
	  
	    public BaseListView(Context context, AttributeSet attrs, int defStyle) {  
	        // TODO Auto-generated method stub  
	        super(context, attrs, defStyle);  
	    } 
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, mExpandSpec);
	}

}
