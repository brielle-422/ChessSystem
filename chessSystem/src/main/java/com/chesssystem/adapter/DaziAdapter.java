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

import com.bumptech.glide.Glide;
import com.chesssystem.R;
import com.chesssystem.adapter.RoomAdapter.MyClickListener;
import com.chesssystem.item.DaziItem;
import com.chesssystem.item.MainItem;
import com.chesssystem.util.DateUtil;
import com.chesssystem.widget.CircleImageView;
import com.lidroid.xutils.db.sqlite.CursorUtils.FindCacheSequence;
import com.nostra13.universalimageloader.core.ImageLoader;
/**
 * 
 * @author lyg
 * @time 2016-7-4上午9:47:57
 */
public class DaziAdapter extends ArrayAdapter<DaziItem>{
	private List<DaziItem> daziItems;
	private Context mContext;
	private LayoutInflater lInflater;
	private ViewHolder holder;
	private DaziClickListener mListener;
	public DaziAdapter(Context context,List<DaziItem>datas,DaziClickListener listener){
		super(context,-1, datas);
		mContext=context;
		daziItems=datas;
		lInflater=LayoutInflater.from(context);
		mListener = listener;
	}
	@Override
	public View getView(final int position, View convertView,  ViewGroup parent) {
		holder=null;
		if (convertView==null) {
			convertView=lInflater.inflate(R.layout.item_dazi, parent, false);
			holder=new ViewHolder();
			holder.image=(CircleImageView) convertView.findViewById(R.id.civ_face);
			holder.name=(TextView) convertView.findViewById(R.id.tv_dazi_name);
			holder.time=(TextView) convertView.findViewById(R.id.tv_dazi_time);
			holder.local=(TextView) convertView.findViewById(R.id.tv_dazi_local);
		    holder.number=(TextView) convertView.findViewById(R.id.tv_dazi_number);
		    holder.content=(TextView) convertView.findViewById(R.id.tv_dazi_content);
		    holder.telphone=(TextView) convertView.findViewById(R.id.tv_dazi_telphone);
			holder.yingyue=(ImageView) convertView.findViewById(R.id.iv_dazi_yingyue);
		    holder.sex=(ImageView) convertView.findViewById(R.id.iv_dazi_sex);
		    holder.birthday=(TextView) convertView.findViewById(R.id.tv_dazi_old);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		holder.name.setText(getItem(position).getName());
		if(getItem(position).getSex()==0){
			holder.sex.setImageResource(R.drawable.girl);
		}else{
			holder.sex.setImageResource(R.drawable.boy);
		}
		String tag = (String) holder.image.getTag(R.id.imageloader_uri);
		if(!getItem(position).getImage().equals(tag)){
			Glide.with(mContext)
			.load(getItem(position).getImage())
			.placeholder(R.drawable.avatar_default)
			.error(R.drawable.avatar_default)
			.skipMemoryCache( true )
			.centerCrop()
			.dontAnimate()
			.into(holder.image);
			holder.image.setTag(R.id.imageloader_uri,getItem(position).getImage());
		}
		holder.birthday.setText(DateUtil.getAgeOld(getItem(position).getBirthday()));
		holder.time.setText(DateUtil.TimeToChange(getItem(position).getTime()));
		holder.local.setText(getItem(position).getLocal());
		holder.number.setText(getItem(position).getNumber()+"");
		holder.content.setText(getItem(position).getContent());
		holder.telphone.setText("应约成功后可查看");
		holder.yingyue.setOnClickListener( mListener);
		holder.yingyue.setTag(position);
		return convertView;
	}
	private class ViewHolder{
		private CircleImageView image;
		private TextView name;
		private TextView time;
		private TextView local;
		private TextView number;
		private TextView content;
		private TextView telphone;
		private ImageView yingyue;
		private ImageView sex;
		private TextView birthday;
	}
	 public static abstract class DaziClickListener implements OnClickListener {
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
