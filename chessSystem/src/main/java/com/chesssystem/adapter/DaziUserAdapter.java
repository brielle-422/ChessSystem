package com.chesssystem.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chesssystem.NimApplication;
import com.chesssystem.R;
import com.chesssystem.adapter.RoomAdapter.MyClickListener;
import com.chesssystem.item.DaziItem;
import com.chesssystem.item.DaziUserItem;
import com.chesssystem.item.MainItem;
import com.chesssystem.ui.dazi.DaziDetailActivity;
import com.chesssystem.util.DateUtil;
import com.chesssystem.util.ServerUrl;
import com.chesssystem.widget.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 
 * @author lyg
 * @time 2016-7-4上午9:48:01
 */
public class DaziUserAdapter extends ArrayAdapter<DaziUserItem> {
	private List<DaziUserItem> daziUserItems;
	private Context mContext;
	private LayoutInflater lInflater;
	private ViewHolder holder;
	private DaziUserClickListener mListener;

	public DaziUserAdapter(Context context, List<DaziUserItem> datas,
			DaziUserClickListener listener) {
		super(context, -1, datas);
		mContext = context;
		daziUserItems = datas;
		lInflater = LayoutInflater.from(context);
		mListener = listener;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		holder = null;
		if (convertView == null) {
			convertView = lInflater.inflate(R.layout.item_dazi_user, parent,
					false);
			holder = new ViewHolder();
			holder.image = (CircleImageView) convertView
					.findViewById(R.id.civ_user_face);
			holder.name = (TextView) convertView
					.findViewById(R.id.tv_user_name);
			holder.sex = (ImageView) convertView.findViewById(R.id.iv_user_sex);
			holder.old = (TextView) convertView.findViewById(R.id.tv_user_old);
			holder.goodRates = (TextView) convertView
					.findViewById(R.id.tv_user_goodrates);
			holder.midRates = (TextView) convertView
					.findViewById(R.id.tv_user_midrates);
			holder.lowRates = (TextView) convertView
					.findViewById(R.id.tv_user_lowrates);
			holder.choice = (Button) convertView
					.findViewById(R.id.bt_user_choice);
			holder.state = (TextView) convertView
					.findViewById(R.id.tv_user_state);
			holder.time = (TextView) convertView
					.findViewById(R.id.tv_user_date);
			holder.btContact = (Button) convertView
					.findViewById(R.id.bt_user_contact);
			holder.tvContact = (TextView) convertView
					.findViewById(R.id.bt_user_contact_telephone);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.name.setText(getItem(position).getName());
		holder.time.setText(getItem(position).getJoinTime());
		holder.old.setText(DateUtil.getAgeOld(getItem(position).getOld()));
		holder.goodRates.setText(getItem(position).getGoodRate() + "");
		holder.midRates.setText(getItem(position).getMidRate() + "");
		holder.lowRates.setText(getItem(position).getLowRate() + "");
		if (getItem(position).getSex() == 0) {
			holder.sex.setImageResource(R.drawable.girl);
		} else {
			holder.sex.setImageResource(R.drawable.boy);
		}
		holder.tvContact.setText(getItem(position).getContact());
		holder.tvContact.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
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
		holder.choice.setOnClickListener(mListener);
		holder.choice.setTag(position);
		switch (getItem(position).getJoinState()) {
		/*
		 *  待审核人员
		 */
		case 1:
			if(getItem(position).getUserId().equals(NimApplication.sharedPreferences.getString("userId", ""))){
				holder.btContact.setVisibility(View.GONE);
				holder.tvContact.setVisibility(View.VISIBLE);
			}else{
				holder.btContact.setVisibility(View.VISIBLE);
				holder.tvContact.setVisibility(View.GONE);
			}
			if (DaziDetailActivity.userType == 4) {
				holder.state.setVisibility(View.GONE);
				holder.choice.setVisibility(View.VISIBLE);
			} else if (DaziDetailActivity.userType == 0) {
				holder.state.setVisibility(View.GONE);
				holder.choice.setVisibility(View.GONE);
			} else {	
				holder.state.setVisibility(View.VISIBLE);
				holder.choice.setVisibility(View.GONE);
				holder.state.setText("待审核");
				holder.state.setTextColor(mContext.getResources().getColor(
						R.color.main_green));
			}
			break;
		/*
		 *  成功人员
		 */
		case 2:
			if (DaziDetailActivity.userType == 2|| DaziDetailActivity.userType == 4) {
				holder.btContact.setVisibility(View.GONE);
				holder.tvContact.setVisibility(View.VISIBLE);
			}else{
				holder.btContact.setVisibility(View.VISIBLE);
				holder.tvContact.setVisibility(View.GONE);
			}
			if (DaziDetailActivity.userType != 0) {
				holder.state.setVisibility(View.VISIBLE);
				holder.choice.setVisibility(View.GONE);
				holder.state.setText("成功");
				holder.state.setTextColor(mContext.getResources().getColor(
						R.color.text_color_hongse_ff4318));
				
			}else{
				holder.state.setVisibility(View.GONE);
				holder.choice.setVisibility(View.GONE);
			}
			break;
		/*
		 *  失败人员
		 */
		case 3:
			if(getItem(position).getUserId().equals(NimApplication.sharedPreferences.getString("userId", ""))){
				holder.btContact.setVisibility(View.GONE);
				holder.tvContact.setVisibility(View.VISIBLE);
			}else{
				holder.btContact.setVisibility(View.VISIBLE);
				holder.tvContact.setVisibility(View.GONE);
			}
			if (DaziDetailActivity.userType != 0) {
				holder.state.setVisibility(View.VISIBLE);
				holder.choice.setVisibility(View.GONE);
				holder.state.setText("失败");
				holder.state.setTextColor(mContext.getResources().getColor(
						R.color.text_color_heise_504f4f));
			}else{
				holder.state.setVisibility(View.GONE);
				holder.choice.setVisibility(View.GONE);
			}
			break;
		default:
			break;
		}
		return convertView;
	}

	private class ViewHolder {
		private CircleImageView image;
		private TextView name;
		private ImageView sex;
		private TextView old;
		private TextView goodRates;
		private TextView midRates;
		private TextView lowRates;
		private Button choice;
		private TextView state;
		private TextView time;
		private Button btContact;
		private TextView tvContact;
	}

	public static abstract class DaziUserClickListener implements
			OnClickListener {
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
