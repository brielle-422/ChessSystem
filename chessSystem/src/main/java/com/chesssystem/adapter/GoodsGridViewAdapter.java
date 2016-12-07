package com.chesssystem.adapter;

import java.util.List;

import com.bumptech.glide.Glide;
import com.chesssystem.R;
import com.chesssystem.item.DaziItem;
import com.chesssystem.item.GoodsItem;
import com.chesssystem.widget.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 
 * @author lyg
 * @time 2016-7-4上午9:48:08
 */
public class GoodsGridViewAdapter extends ArrayAdapter<GoodsItem>{
	private List<GoodsItem> goodsItems;
	private Context mContext;
	private LayoutInflater lInflater;
	private ViewHolder holder;
	public GoodsGridViewAdapter(Context context,List<GoodsItem>datas){
		super(context,-1, datas);
		mContext=context;
		goodsItems=datas;
		lInflater=LayoutInflater.from(context);
	}
	@Override
	public View getView(final int position, View convertView,  ViewGroup parent) {
		holder=null;
		if (convertView==null) {
			convertView=lInflater.inflate(R.layout.item_goods, parent, false);
			holder=new ViewHolder();
			holder.image=(ImageView) convertView.findViewById(R.id.iv_goods_image);
			holder.name=(TextView) convertView.findViewById(R.id.tv_goods_name);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		Glide.with(mContext)
		.load(getItem(position).getImageUrl())
				.placeholder(R.color.gray2)
				.error(R.color.gray2)
				.skipMemoryCache( true )
				.centerCrop()
				.into(holder.image);
		holder.name.setText(getItem(position).getGoodsName());
		return convertView;
	}
	private class ViewHolder{
		private ImageView image;
		private TextView name;
	}

}