package com.chesssystem.adapter;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chesssystem.NimApplication;
import com.chesssystem.R;
import com.chesssystem.adapter.CommentAdapter.GridAdapter;
import com.chesssystem.adapter.OrderAdapter.OrderClickListener;
import com.chesssystem.album.previewActivity;
import com.chesssystem.item.CommentItem;
import com.chesssystem.util.DateUtil;
import com.chesssystem.widget.CircleImageView;
import com.chesssystem.widget.GoodsGridView;
import com.nostra13.universalimageloader.core.ImageLoader;
/**
 * 我的评价列表适配器
 * @author lyg
 * @time 2016-7-4上午9:47:53
 */
public class CommentMyAdapter extends ArrayAdapter<CommentItem>{
	private List<CommentItem> commentItems;
	private static Context mContext;
	private LayoutInflater lInflater;
	private ViewHolder holder;
	private GridAdapter gridAdapter;
	private MyClickListener myClickListener;
	public CommentMyAdapter(Context context,List<CommentItem>datas,MyClickListener clickListener){
		super(context,-1, datas);
		mContext=context;
		commentItems=datas;
		lInflater=LayoutInflater.from(context);
		myClickListener=clickListener;
	}
	@Override
	public View getView(final int position, View convertView,  ViewGroup parent) {
		holder=null;
		if (convertView==null) {
			convertView=lInflater.inflate(R.layout.item_comment_my, parent, false);
			holder=new ViewHolder();
			holder.tvStoreName=(TextView) convertView.findViewById(R.id.tv_order_name);
			holder.tvContent=(TextView) convertView.findViewById(R.id.tv_comment_content);
			holder.tvTime=(TextView) convertView.findViewById(R.id.tv_comment_time);
			holder.llReply=(LinearLayout) convertView.findViewById(R.id.ll_comment_reply);
		    holder.tvreply=(TextView) convertView.findViewById(R.id.tv_comment_reply);
			holder.starGroup=(ViewGroup) convertView.findViewById(R.id.ll_comment_star);
			holder.gvImage=(GridView) convertView.findViewById(R.id.gv_comment_image);
			holder.rlStoreName=(RelativeLayout) convertView.findViewById(R.id.rl_storeName);	
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		holder.tvStoreName.setText(getItem(position).getStoreName());
		holder.tvContent.setText(getItem(position).getContent());
		holder.tvTime.setText(DateUtil.deleteHours(getItem(position).getTime()));
		String tagImage = (String) holder.gvImage.getTag(R.id.imageloader_uri);
		if(!getItem(position).getImages().toString().equals(tagImage)){	
			gridAdapter=new GridAdapter(mContext,getItem(position).getImages());
			holder.gvImage.setAdapter(gridAdapter);
		holder.gvImage.setTag(R.id.imageloader_uri,getItem(position).getImages().toString());
		}
//		gridAdapter=new GridAdapter(mContext,getItem(position).getImages());
//		holder.gvImage.setAdapter(gridAdapter);
		holder.rlStoreName.setOnClickListener(myClickListener);
		holder.rlStoreName.setTag(position);
		if(getItem(position).getReply().length()>0){
			holder.llReply.setVisibility(View.VISIBLE);
			holder.tvreply.setText(getItem(position).getReply());
		}else{
			holder.llReply.setVisibility(View.GONE);
		}
		/*
		 * 设置星级
		 */
		holder.starGroup.removeAllViews();
		ImageView[] tips = new ImageView[5];
		for (int i = 0; i < 5; i++) {
			ImageView imageView = new ImageView(getContext());
			tips[i] = imageView;
			if (i<getItem(position).getStar()) {
				tips[i].setImageResource(R.drawable.favorate_press);
			} else {
				tips[i].setImageResource(R.drawable.favorate_default);
			}
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
						new ViewGroup.LayoutParams(60,
							60));
			holder.starGroup.addView(imageView, layoutParams);
		}
		
		return convertView;
	}
	private class ViewHolder{
		private TextView tvStoreName;
		private TextView tvContent;
		private TextView tvTime;
		private LinearLayout llReply;
		private TextView tvreply;
		private ViewGroup starGroup;
		private GridView gvImage;
		private RelativeLayout rlStoreName;
	}
	/*
	  * GridView的适配器
	  */
		public static class GridAdapter extends BaseAdapter {
			private LayoutInflater inflater;
			private List<String> imageurls=new ArrayList<String>();
			public GridAdapter(Context context,List<String> imageURl) {
				inflater = LayoutInflater.from(context);
				this.imageurls=imageURl;
			}
			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				final ViewHolder holder;
				if (convertView == null) {
					convertView = inflater.inflate(R.layout.item_image,parent, false);
					holder = new ViewHolder();
					holder.image =(ImageView) convertView.findViewById(R.id.item_image);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				String tag = (String) holder.image.getTag(R.id.imageloader_uri);
				if(!imageurls.get(position).equals(tag)){
				Glide.with(mContext)
				.load(imageurls.get(position))
						.placeholder(R.color.gray2)
						.error(R.color.gray2)
						.skipMemoryCache( true )
						.centerCrop()
						.into(holder.image);
				holder.image.setTag(R.id.imageloader_uri,imageurls.get(position));
//				holder.image.setOnClickListener(new OnClickListener() {
//					
//					@Override
//					public void onClick(View arg0) {
//						Intent intent=new Intent(mContext,previewActivity.class);
//						intent.putExtra("image", imageurls.get(position));
//						mContext.startActivity(intent);
//					}
//				});
				}
				return convertView;
			}

			public class ViewHolder {
				public ImageView image;
			}
			@Override
			public int getCount() {
				return imageurls.size();
			}
			@Override
			public Object getItem(int arg0) {
				return arg0;
			}
			@Override
			public long getItemId(int arg0) {
				return arg0;
			}
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