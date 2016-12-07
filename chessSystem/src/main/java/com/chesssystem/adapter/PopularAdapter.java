package com.chesssystem.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.chesssystem.R;
import com.chesssystem.item.MainItem;
import com.chesssystem.util.DoubleToInt;
import com.nostra13.universalimageloader.core.ImageLoader;
/**
 * 人气推荐适配器
 * @author lyg
 * @time 2016-7-4上午9:48:13
 */
public class PopularAdapter extends ArrayAdapter<MainItem>{
	private List<MainItem> mainItems;
	private Context mContext;
	private LayoutInflater lInflater;
	private ViewHolder holder;
	public PopularAdapter(Context context,List<MainItem>datas){
		super(context,-1, datas);
		mContext=context;
		mainItems=datas;
		lInflater=LayoutInflater.from(context);
	}
	@Override
	public View getView(final int position, View convertView,  ViewGroup parent) {
		holder=null;
		if (convertView==null) {
			convertView=lInflater.inflate(R.layout.item_popular, parent, false);
			holder=new ViewHolder();
			holder.name=(TextView) convertView.findViewById(R.id.tv_popular_name);
			holder.image=(ImageView) convertView.findViewById(R.id.image_popular);
			holder.price=(TextView) convertView.findViewById(R.id.tv_popular_price);
			holder.distance=(TextView) convertView.findViewById(R.id.tv_popular_distance);
			holder.starGroup=(ViewGroup) convertView.findViewById(R.id.ll_popular_star);
		    convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		holder.name.setText(getItem(position).getName());
		holder.price.setText("人均消费"+DoubleToInt.DoubleToInt(getItem(position).getPerPrice()));
		holder.distance.setText("距离"+DoubleToInt.DoubleToDistance(getItem(position).getDistance()));
		final String tag = (String) holder.image.getTag(R.id.imageloader_uri);
		if(!getItem(position).getImagepath().equals(tag)){
			Glide.with(mContext)
			.load(getItem(position).getImagepath())
			.placeholder(R.color.gray2)
			.error(R.color.gray2)
			.skipMemoryCache( true )
			.thumbnail(0.1f)//缩略图
			.centerCrop()
			.into(holder.image);
			holder.image.setTag(R.id.imageloader_uri,getItem(position).getImagepath());
		}
		/*
		 * 设置星级
		 */
		holder.starGroup.removeAllViews();
		ImageView[] tips = new ImageView[5];
		for (int i = 0; i < getItem(position).getStar(); i++) {
			ImageView imageView = new ImageView(getContext());
			tips[i] = imageView;
//			if (i<getItem(position).getStar()) {
				tips[i].setImageResource(R.drawable.favorate_black);
//			} 
//			else {
//				tips[i].setImageResource(R.drawable.favorate_default);
//			}
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
						new ViewGroup.LayoutParams(40,
							40));
			holder.starGroup.addView(imageView, layoutParams);
		}
		
		return convertView;
	}
	private class ViewHolder{
		private TextView name;
		private TextView price;
		private TextView distance;
		private ImageView image;
		private ViewGroup starGroup;
	}

}
