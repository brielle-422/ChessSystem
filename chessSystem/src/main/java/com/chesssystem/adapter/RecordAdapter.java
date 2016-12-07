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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chesssystem.R;
import com.chesssystem.adapter.RoomAdapter.MyClickListener;
import com.chesssystem.item.DaziItem;
import com.chesssystem.item.DaziUserItem;
import com.chesssystem.item.MainItem;
import com.chesssystem.util.DateUtil;
import com.chesssystem.widget.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
/**
 * 搭子记录适配器
 * @author lyg
 * @time 2016-7-1上午10:27:31
 */
public class RecordAdapter extends ArrayAdapter<DaziUserItem>{
	private List<DaziUserItem> daziUserItems;
	private Context mContext;
	private LayoutInflater lInflater;
	private ViewHolder holder;
	private RecordClickListener mListener;
	public RecordAdapter(Context context,List<DaziUserItem>datas,RecordClickListener listener){
		super(context,-1, datas);
		mContext=context;
		daziUserItems=datas;
		lInflater=LayoutInflater.from(context);
		mListener = listener;
	}
	@Override
	public View getView(final int position, View convertView,  ViewGroup parent) {
		holder=null;
		if (convertView==null) {
			convertView=lInflater.inflate(R.layout.item_record, parent, false);
			holder=new ViewHolder();
			holder.image=(CircleImageView) convertView.findViewById(R.id.civ_user_face);
			holder.name=(TextView) convertView.findViewById(R.id.tv_user_name);
			holder.sex=(ImageView) convertView.findViewById(R.id.iv_user_sex);
		    holder.old=(TextView) convertView.findViewById(R.id.tv_user_old);
		    holder.goodRates=(TextView) convertView.findViewById(R.id.tv_user_goodrates);
		    holder.midRates=(TextView) convertView.findViewById(R.id.tv_user_midrates);
		    holder.lowRates=(TextView) convertView.findViewById(R.id.tv_user_lowrates);
			holder.choice=(Button) convertView.findViewById(R.id.bt_user_choice);
			holder.state=(TextView) convertView.findViewById(R.id.tv_user_state);
		    convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		holder.name.setText(getItem(position).getName());
		holder.sex.setImageResource(R.drawable.girl);
		holder.old.setText(DateUtil.getAgeOld(getItem(position).getOld()));
		holder.goodRates.setText(getItem(position).getGoodRate()+"");
		holder.midRates.setText(getItem(position).getMidRate()+"");
		holder.lowRates.setText(getItem(position).getLowRate()+"");
		String tag = (String) holder.image.getTag(R.id.imageloader_uri);
		if(!getItem(position).getImageFace().equals(tag)){
			Glide.with(mContext)
			.load(getItem(position).getImageFace())
			.placeholder(R.drawable.avatar_default)
			.error(R.drawable.avatar_default)
			.skipMemoryCache( true )
			.centerCrop()
			.dontAnimate()
			.into(holder.image);
			holder.image.setTag(R.id.imageloader_uri,getItem(position).getImageFace());
		}
		if(getItem(position).getRateState()==0){
			holder.choice.setVisibility(View.VISIBLE);
			holder.state.setVisibility(View.GONE);
		}else if(getItem(position).getRateState()==1){
			holder.choice.setVisibility(View.GONE);
			holder.state.setVisibility(View.VISIBLE);
			holder.state.setText("好评");
			holder.state.setTextColor(mContext.getResources().getColor(
					R.color.text_color_hongse_ff4318));
		}else if(getItem(position).getRateState()==2){
			holder.choice.setVisibility(View.GONE);
			holder.state.setVisibility(View.VISIBLE);
			holder.state.setText("中评");
			holder.state.setTextColor(mContext.getResources().getColor(
					R.color.text_color_huangse_ff8800));
		}else if(getItem(position).getRateState()==3){
			holder.choice.setVisibility(View.GONE);
			holder.state.setVisibility(View.VISIBLE);
			holder.state.setText("差评");
			holder.state.setTextColor(mContext.getResources().getColor(
					R.color.blue));
		}
		holder.choice.setOnClickListener( mListener);
		holder.choice.setTag(position);
		return convertView;
	}
	private class ViewHolder{
		private CircleImageView image;
		private TextView name;
		private ImageView sex;
		private TextView old;
		private TextView goodRates;
		private TextView midRates;
		private TextView lowRates;
		private Button choice;
		private TextView state;
	}
	 public static abstract class RecordClickListener implements OnClickListener {
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
