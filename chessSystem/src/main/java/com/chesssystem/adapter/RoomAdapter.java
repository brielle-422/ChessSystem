package com.chesssystem.adapter;


import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chesssystem.R;
import com.chesssystem.item.RoomItem;
import com.chesssystem.ui.MerchantsActivity;
import com.chesssystem.util.DoubleToInt;
import com.nostra13.universalimageloader.core.ImageLoader;
/**
 * 
 * @author lyg
 * @time 2016-7-4上午9:48:25
 */
public class RoomAdapter extends ArrayAdapter<RoomItem>{
	private List<RoomItem> roomItems;
	private Context mContext;
	private LayoutInflater lInflater;
	private ViewHolder holder;
	private MyClickListener mListener;
	public RoomAdapter(Context context,List<RoomItem>datas,MyClickListener listener){
		super(context,-1, datas);
		mContext=context;
		roomItems=datas;
		lInflater=LayoutInflater.from(context);
		 mListener = listener;
	}
	@Override
	public View getView(final int position, View convertView,  ViewGroup parent) {
		holder=null;
		if (convertView==null) {
			convertView=lInflater.inflate(R.layout.item_room, parent, false);
			holder=new ViewHolder();
			holder.TvName=(TextView) convertView.findViewById(R.id.tv_room_name);
			holder.TvPrice=(TextView) convertView.findViewById(R.id.tv_room_price);
			holder.IvPic=(ImageView) convertView.findViewById(R.id.iv_room_image);
		    holder.BtReserve=(Button) convertView.findViewById(R.id.bt_room_reserve);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		holder.TvName.setText(getItem(position).getRoomName());
		holder.TvPrice.setText(DoubleToInt.DoubleToInt(getItem(position).getPrice())+"元/"+DoubleToInt.DoubleToInt(getItem(position).getTime())+"小时");
		Glide.with(mContext)
		.load(getItem(position).getRoomPic())
				.placeholder(R.color.gray2)
				.error(R.color.gray2)
				.skipMemoryCache( true )
				.centerCrop()
				.into(holder.IvPic);
		holder.BtReserve.setOnClickListener( mListener);
		holder.BtReserve.setTag(position);
		return convertView;
	}
	private class ViewHolder{
		private TextView TvName;
		private TextView TvPrice;
		private ImageView IvPic;
		private Button BtReserve;
	}
	 public static abstract class MyClickListener implements OnClickListener {
		          /**
		           * 基类的onClick方法
		           */
		          @Override
		          public void onClick(View v) {
		             myOnClick((Integer) v.getTag(), v);
		          }
		          public abstract void myOnClick(int position, View v);
		      }

}