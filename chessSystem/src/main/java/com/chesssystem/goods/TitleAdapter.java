package com.chesssystem.goods;

import java.util.List;

import com.chesssystem.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

/**
 * 
 * @author lyg
 * @time 2016-7-4上午9:50:01
 */
public class TitleAdapter extends BaseAdapter {
	private static final String TAG = "TitleAdapter";

	private Context context;
	private List<FoodTypeModel> foodTypeList;
	private PinnedHeaderListView lv_Content;
	private int pos;

	public TitleAdapter(Context context, List<FoodTypeModel> foodTypeList,PinnedHeaderListView lv_Content) {
		super();
		this.context = context;
		this.foodTypeList = foodTypeList;
		this.lv_Content=lv_Content;
	}
	public void setPos(int pos) {
		this.pos = pos;
	}

	@Override
	public int getCount() {
		return foodTypeList.size();
	}

	@Override
	public FoodTypeModel getItem(int position) {
		return foodTypeList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.item_doublelistview_left, null);
			vh.title = (TextView) convertView.findViewById(R.id.left_list_item);
			vh.btNumber=(TextView) convertView.findViewById(R.id.bt_type_number);
			vh.viewLine=convertView.findViewById(R.id.view_line);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		FoodTypeModel model = getItem(position); 
		vh.btNumber.setText((position+1)+"F");
		if (pos==position) {
			vh.title.setTextColor(context.getResources().getColor(R.color.text_color_huangse_ff8800));
			vh.btNumber.setTextColor(context.getResources().getColor(R.color.text_color_huangse_ff8800));
			vh.btNumber.setBackgroundResource(R.drawable.circle_button_yellow);
		} else {
			vh.title.setTextColor(context.getResources().getColor(R.color.text_color_shenhui_677582));
			vh.btNumber.setTextColor(context.getResources().getColor(R.color.text_color_shenhui_677582));
			vh.btNumber.setBackgroundResource(R.drawable.circle_button_gray);
		}
		final String foodTypeName=model.getFoodTypeName();
		final int itemPos=model.getItemPosition();
		vh.title.setText(foodTypeName); 
		if(foodTypeList.size()==(position+1)){
			vh.viewLine.setVisibility(View.GONE);
		}else{
			vh.viewLine.setVisibility(View.VISIBLE);
		}
//		vh.title.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) { 
//				changeTextColor(foodTypeName); 
//				
//				lv_Content.setSelection(itemPos);
//			}
//		});
		return convertView;
	}

	class ViewHolder {
		public TextView title;
		public TextView btNumber;
		public View viewLine;
	}

}
