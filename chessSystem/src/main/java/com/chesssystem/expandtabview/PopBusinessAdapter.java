package com.chesssystem.expandtabview;

import java.util.List;


import com.chesssystem.R;
import com.chesssystem.album.ImageLoader.Type;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 
 * @author lyg
 * @time 2016-7-4上午9:49:08
 */
public class PopBusinessAdapter extends BaseAdapter{
	private Context context;
	private List<PopBusinessItem> list;
	
	public PopBusinessAdapter(Context context, List<PopBusinessItem> list) {
		super();
		this.context = context;
		this.list = list;
	}

	public void changeData(List<PopBusinessItem> list){
		this.list=list;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public PopBusinessItem getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		if(convertView==null){
			convertView=LayoutInflater.from(context).inflate(R.layout.item_pop_business, null);
			holder=new ViewHolder();
			holder.dirItemIcon=(ImageView)convertView.findViewById(R.id.iv_pop_choose);
			holder.dirItemName=(TextView)convertView.findViewById(R.id.tv_pop_name);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		holder.dirItemName.setText(list.get(position).getName());
		if(list.get(position).isSelected()){
			holder.dirItemName.setTextColor(context.getResources().getColor(R.color.main_green));
			holder.dirItemIcon.setVisibility(View.VISIBLE);
		}else{
			holder.dirItemIcon.setVisibility(View.INVISIBLE);
			holder.dirItemName.setTextColor(context.getResources().getColor(R.color.text_color_shenhui_677582));
		}
		return convertView;
	}

	class ViewHolder{
		TextView dirItemName;
		ImageView dirItemIcon;
	}
	


}
