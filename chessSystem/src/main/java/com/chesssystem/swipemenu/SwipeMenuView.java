package com.chesssystem.swipemenu;

import java.util.List;

import com.chesssystem.R;

import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SwipeMenuView extends LinearLayout implements OnClickListener {

	private SwipeMenuListView mListView;
	private SwipeMenuLayout mLayout;
	private SwipeMenu mMenu;
	private OnSwipeItemClickListener onItemClickListener;
	private int position;

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public SwipeMenuView(SwipeMenu menu, SwipeMenuListView listView) {
		super(menu.getContext());
		mListView = listView;
		mMenu = menu;
		List<SwipeMenuItem> items = menu.getMenuItems();
		int id = 0;
		for (SwipeMenuItem item : items) {
			addItem(item, id++);
		}
	}
	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
	private void addItem(SwipeMenuItem item, int id) {
	LayoutParams params = new LayoutParams(item.getWidth(),
			LayoutParams.MATCH_PARENT);
	LinearLayout parent = new LinearLayout(getContext());
	parent.setId(id);
	parent.setGravity(Gravity.CENTER);
	parent.setPadding(dp2px(10),dp2px(10),dp2px(10),dp2px(10));
	parent.setOrientation(LinearLayout.VERTICAL);
	parent.setLayoutParams(params);
	parent.setBackgroundDrawable(item.getBackground());
	parent.setOnClickListener(this);
	addView(parent);

	if (item.getIcon() != null) {
		parent.addView(createIcon(item));
	}
	if (!TextUtils.isEmpty(item.getTitle())) {
		parent.addView(createTitle(item));
//		parent.addView(createButton(item));
	}

}

	private ImageView createIcon(SwipeMenuItem item) {
		ImageView iv = new ImageView(getContext());
		iv.setImageDrawable(item.getIcon());
		return iv;
	}

	private TextView createTitle(SwipeMenuItem item) {
		TextView tv = new TextView(getContext());
		tv.setText(item.getTitle());
		tv.setGravity(Gravity.CENTER);
		tv.setTextSize(item.getTitleSize());
		tv.setBackgroundResource(R.drawable.icon_swipemenu);
		tv.setHeight(dp2px(40));
		tv.setTextColor(item.getTitleColor());
		return tv;
	}
	private Button createButton(SwipeMenuItem item) {
		Button button=new Button(getContext());
		button.setText(item.getTitle());
		button.setTextSize(item.getTitleSize());
		button.setTextColor(item.getTitleColor());
		button.setBackgroundColor(Color.rgb(0xF9,
				0x3F, 0x25));
		button.setMaxWidth(30);
		return button;
	}
	@Override
	public void onClick(View v) {
		if (onItemClickListener != null && mLayout.isOpen()) {
			onItemClickListener.onItemClick(this, mMenu, v.getId());
		}
	}

	public OnSwipeItemClickListener getOnSwipeItemClickListener() {
		return onItemClickListener;
	}

	public void setOnSwipeItemClickListener(OnSwipeItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	public void setLayout(SwipeMenuLayout mLayout) {
		this.mLayout = mLayout;
	}

	public static interface OnSwipeItemClickListener {
		void onItemClick(SwipeMenuView view, SwipeMenu menu, int index);
	}
}
