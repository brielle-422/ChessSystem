package com.chesssystem.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chesssystem.R;
import com.chesssystem.adapter.RoomAdapter.MyClickListener;
import com.chesssystem.item.MainItem;
import com.chesssystem.item.OrderItem;
import com.chesssystem.util.DateUtil;
import com.chesssystem.util.DoubleToInt;
import com.handmark.pulltorefresh.library.internal.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
/**
 * 
 * @author lyg
 * @time 2016-7-4上午9:48:19
 */
public class OrderAdapter extends ArrayAdapter<OrderItem>{
	private List<OrderItem> orderItems;
	private Context mContext;
	private LayoutInflater lInflater;
	private ViewHolder holder;
	private OrderClickListener orderClickListener;
	public OrderAdapter(Context context,List<OrderItem>datas,OrderClickListener listener){
		super(context,-1, datas);
		mContext=context;
		orderItems=datas;
		lInflater=LayoutInflater.from(context);
		orderClickListener = listener;
	}
	@Override
	public View getView(final int position, View convertView,  ViewGroup parent) {
		holder=null;
		if (convertView==null) {
			convertView=lInflater.inflate(R.layout.item_order, parent, false);
			holder=new ViewHolder();
			holder.storeName=(TextView) convertView.findViewById(R.id.tv_order_name);
			holder.orderNumber=(TextView) convertView.findViewById(R.id.tv_order_id);
			holder.time=(TextView) convertView.findViewById(R.id.tv_order_time);
			holder.money=(TextView) convertView.findViewById(R.id.tv_order_money);
			holder.image=(ImageView) convertView.findViewById(R.id.iv_order_image);
			holder.right=(TextView) convertView.findViewById(R.id.tv_order_right);
			holder.left=(TextView) convertView.findViewById(R.id.tv_order_left);
			holder.rlStoreName=(RelativeLayout) convertView.findViewById(R.id.ll_storeName);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		holder.storeName.setText(getItem(position).getStoreName());
		holder.orderNumber.setText(getItem(position).getOrderNumber());
//		holder.time.setText(DateUtil.deleteHours(getItem(position).getCreateTime()));
		holder.time.setText(getItem(position).getCreateTime());
		holder.money.setText(DoubleToInt.DoubleToInt(getItem(position).getOrderPrice()));
		String tag = (String) holder.image.getTag(R.id.imageloader_uri);
		if(!getItem(position).getOrderImage().equals(tag)){
			Glide.with(mContext)
			.load(getItem(position).getOrderImage())
			.placeholder(R.color.gray2)
			.error(R.color.gray2)
			.skipMemoryCache( true )
			.centerCrop()
			.into(holder.image);
			holder.image.setTag(R.id.imageloader_uri,getItem(position).getOrderImage());
		}
		if(getItem(position).getPayStatus()==1){
			holder.left.setText("取消订单");
			holder.right.setText("去支付");
		}else{
			holder.left.setText("再来一单");
			if(getItem(position).getPayStatus()==3){
				
			}
		}
		switch (getItem(position).getPayStatus()) {
		case 1:
			holder.left.setText("取消订单");
			holder.right.setText("去支付");
			break;
		case 2:
			holder.left.setText("再来一单");
			if(getItem(position).getStatus()==1||getItem(position).getStatus()==2){
				holder.right.setText("查看订单");
			}else if(getItem(position).getStatus()==3){
				holder.right.setText("查看退款");
			}else if(getItem(position).getStatus()==4){
				if(getItem(position).isRate()==0){
					holder.right.setText("去评价");
				}else if(getItem(position).isRate()==1){
					holder.right.setText("查看评价");
				}
			}
			break;
		case 3:
		case 4:
		case 5:
			holder.left.setText("再来一单");
			holder.right.setText("查看退款");
			break;
		default:
			break;
		}
		holder.right.setOnClickListener(orderClickListener);
		holder.right.setTag(position);
		holder.left.setOnClickListener(orderClickListener);
		holder.left.setTag(position);
		holder.rlStoreName.setOnClickListener(orderClickListener);
		holder.rlStoreName.setTag(position);
		return convertView;
	}
	private class ViewHolder{
		private TextView storeName;
		private TextView orderNumber;
		private TextView time;
		private TextView money;
		private ImageView image;
		private TextView right;
		private TextView left;
		private RelativeLayout rlStoreName;
	}
	 public static abstract class OrderClickListener implements OnClickListener {
         /**
          * 基类的onClick方法
          */
         @Override
         public void onClick(View v) {
            orderOnClick((Integer) v.getTag(), v);
         }
         public abstract void orderOnClick(int position, View v);
     }

}
