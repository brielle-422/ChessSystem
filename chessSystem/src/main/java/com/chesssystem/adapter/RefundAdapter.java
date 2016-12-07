package com.chesssystem.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chesssystem.R;
import com.chesssystem.item.OrderItem;
import com.chesssystem.util.DateUtil;
import com.chesssystem.util.DoubleToInt;
import com.nostra13.universalimageloader.core.ImageLoader;
/**
 * 我的退款列表适配器
 * @author lyg
 * @time 2016-7-4上午9:47:47
 */
public class RefundAdapter extends ArrayAdapter<OrderItem>{
	private List<OrderItem> refundItems;
	private Context mContext;
	private LayoutInflater lInflater;
	private ViewHolder holder;
	public RefundAdapter(Context context,List<OrderItem>datas){
		super(context,-1, datas);
		mContext=context;
		refundItems=datas;
		lInflater=LayoutInflater.from(context);
	}
	@Override
	public View getView(final int position, View convertView,  ViewGroup parent) {
		holder=null;
		if (convertView==null) {
			convertView=lInflater.inflate(R.layout.item_refund, parent, false);
			holder=new ViewHolder();
			holder.storeName=(TextView) convertView.findViewById(R.id.tv_refund_name);
			holder.status=(TextView) convertView.findViewById(R.id.iv_refund_status);
			holder.creatTime=(TextView) convertView.findViewById(R.id.tv_refund_time);
			holder.orderId=(TextView) convertView.findViewById(R.id.tv_refund_id);
			holder.price=(TextView) convertView.findViewById(R.id.tv_refund_money);
			holder.orderPic=(ImageView) convertView.findViewById(R.id.iv_refund_image);
		    convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		holder.storeName.setText(getItem(position).getStoreName());
		if(getItem(position).getPayStatus()==3){
			holder.status.setText("处理中");
		}else if(getItem(position).getPayStatus()==4){
			holder.status.setText("已退款");
		}else if(getItem(position).getPayStatus()==5){
			holder.status.setText("退款失败");
		}
		holder.creatTime.setText(DateUtil.deleteHours(getItem(position).getCreateTime()));
		holder.orderId.setText(getItem(position).getOrderNumber());
		holder.price.setText(DoubleToInt.DoubleToInt(getItem(position).getOrderPrice()));
		String tag = (String) holder.orderPic.getTag(R.id.imageloader_uri);
		if(!getItem(position).getOrderImage().equals(tag)){
			Glide.with(mContext)
			.load(getItem(position).getOrderImage())
			.placeholder(R.color.gray2)
			.error(R.color.gray2)
			.skipMemoryCache( true )
			.centerCrop()
			.into(holder.orderPic);
			holder.orderPic.setTag(R.id.imageloader_uri,getItem(position).getOrderImage());
		}
		return convertView;
	}
	private class ViewHolder{
		private TextView storeName;
		private TextView status;
		private TextView creatTime;
		private TextView orderId;
		private TextView price;
		private ImageView orderPic;
	}

}
