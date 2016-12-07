package com.chesssystem.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.chesssystem.R;
import com.chesssystem.item.GoodsItem;
import com.chesssystem.util.DoubleToInt;
/**
 * 订单页中的商品记录适配器
 * @author lyg
 * @time 2016-7-1上午10:27:43
 */
public class OrderGoodsAdapter extends ArrayAdapter<GoodsItem>{
	private List<GoodsItem> goodsItems;
	private Context mContext;
	private LayoutInflater lInflater;
	private ViewHolder holder;
	public OrderGoodsAdapter(Context context,List<GoodsItem>datas){
		super(context,-1, datas);
		mContext=context;
		goodsItems=datas;
		lInflater=LayoutInflater.from(context);
	}
	@Override
	public View getView(final int position, View convertView,  ViewGroup parent) {
		holder=null;
		if (convertView==null) {
			convertView=lInflater.inflate(R.layout.item_order_submit, parent, false);
			holder=new ViewHolder();
			holder.tvName=(TextView) convertView.findViewById(R.id.goods_name);
			holder.tvNumber=(TextView) convertView.findViewById(R.id.goods_number);
			holder.tvPrice=(TextView) convertView.findViewById(R.id.goods_price);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		holder.tvName.setText(getItem(position).getGoodsName());
		holder.tvNumber.setText(getItem(position).getChoiceNumber()+"");
		holder.tvPrice.setText(DoubleToInt.DoubleToInt(getItem(position).getGoodsPrice()*getItem(position).getChoiceNumber()));
		return convertView;
	}
	private class ViewHolder{
		private TextView tvName;
		private TextView tvNumber;
		private TextView tvPrice;
	}

}
