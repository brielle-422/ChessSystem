package com.chesssystem.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chesssystem.R;
import com.chesssystem.adapter.RoomAdapter.MyClickListener;
import com.chesssystem.item.DaziItem;
import com.chesssystem.item.MainItem;
import com.chesssystem.util.DateUtil;
import com.chesssystem.widget.CircleImageView;
/**
 * 我的邀约、应约列表适配器
 * @author Administrator
 *
 */
public class InviteAdapter extends ArrayAdapter<DaziItem>{
	private List<DaziItem> daziItems;
	private Context mContext;
	private LayoutInflater lInflater;
	private ViewHolder holder;
	public InviteAdapter(Context context,List<DaziItem>datas){
		super(context,-1, datas);
		mContext=context;
		daziItems=datas;
		lInflater=LayoutInflater.from(context);
	}
	@Override
	public View getView(final int position, View convertView,  ViewGroup parent) {
		holder=null;
		if (convertView==null) {
			convertView=lInflater.inflate(R.layout.item_invite, parent, false);
			holder=new ViewHolder();
			holder.time=(TextView) convertView.findViewById(R.id.tv_dazi_time);
			holder.local=(TextView) convertView.findViewById(R.id.tv_dazi_local);
		    holder.number=(TextView) convertView.findViewById(R.id.tv_dazi_number);
		    holder.content=(TextView) convertView.findViewById(R.id.tv_dazi_content);
		    holder.createTime=(TextView)convertView.findViewById(R.id.tv_invite_createtime);
		    convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		holder.time.setText(DateUtil.TimeToChange(getItem(position).getTime()));
		holder.local.setText(getItem(position).getLocal());
		holder.number.setText(getItem(position).getNumber()+"");
		holder.content.setText(getItem(position).getContent());
		holder.createTime.setText(getItem(position).getCreateTime());
		return convertView;
	}
	private class ViewHolder{
		private TextView time;
		private TextView local;
		private TextView number;
		private TextView content;
		private TextView createTime;
	}

}
