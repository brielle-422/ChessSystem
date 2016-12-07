package com.chesssystem.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;
/**
 * 自定义gridview
 * @author lyg
 * @time 2016-6-30上午10:07:17
 */
public class GoodsGridView extends GridView{
	public GoodsGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public GoodsGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GoodsGridView(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int heightSpec = MeasureSpec.makeMeasureSpec(
					Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);

		super.onMeasure(widthMeasureSpec, heightSpec);
	}
}
