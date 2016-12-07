package com.chesssystem.adapter;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chesssystem.R;
import com.chesssystem.item.CommentItem;
import com.chesssystem.util.DateUtil;
import com.chesssystem.util.ServerUrl;
import com.chesssystem.widget.CircleImageView;
import com.chesssystem.widget.GoodsGridView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
/**
 * 商家评价列表适配器
 * @author lyg
 * @time 2016-7-4上午9:47:53
 */
public class CommentAdapter extends ArrayAdapter<CommentItem>{
	private List<CommentItem> commentItems;
	private static Context mContext;
	private LayoutInflater lInflater;
	private ViewHolder holder;
	private GridAdapter gridAdapter;
	public CommentAdapter(Context context,List<CommentItem>datas){
		super(context,-1, datas);
		mContext=context;
		commentItems=datas;
		lInflater=LayoutInflater.from(context);
	}
	@Override
	public View getView(final int position, View convertView,  ViewGroup parent) {
		holder=null;
		if (convertView==null) {
			convertView=lInflater.inflate(R.layout.item_comment, parent, false);
			holder=new ViewHolder();
			holder.TvName=(TextView) convertView.findViewById(R.id.tv_comment_name);
			holder.tvContent=(TextView) convertView.findViewById(R.id.tv_comment_content);
			holder.tvTime=(TextView) convertView.findViewById(R.id.tv_commment_time);
		    holder.IvFace=(CircleImageView) convertView.findViewById(R.id.civ_comment_face);
			holder.llReply=(LinearLayout) convertView.findViewById(R.id.ll_comment_reply);
		    holder.tvreply=(TextView) convertView.findViewById(R.id.tv_comment_reply);
			holder.starGroup=(ViewGroup) convertView.findViewById(R.id.ll_comment_star);
			holder.gvImage=(GridView) convertView.findViewById(R.id.gv_comment_image);
				
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		holder.TvName.setText(getItem(position).getName());
		holder.tvContent.setText(getItem(position).getContent());
		holder.tvTime.setText(DateUtil.TimeOfDate(getItem(position).getTime()));
		String tag = (String) holder.IvFace.getTag(R.id.imageloader_uri);
		if(!getItem(position).getFace().equals(tag)){	
		Glide.with(mContext)
			.load(getItem(position).getFace())
					.placeholder(R.drawable.avatar_default)
					.error(R.drawable.avatar_default)
					.skipMemoryCache( true )
					.dontAnimate()
					.centerCrop()
					.into(holder.IvFace);
		holder.IvFace.setTag(R.id.imageloader_uri,getItem(position).getFace());
		}
		String tagImage = (String) holder.gvImage.getTag(R.id.imageloader_uri);
		if(!getItem(position).getImages().toString().equals(tagImage)){	
			gridAdapter=new GridAdapter(mContext,getItem(position).getImages());
			holder.gvImage.setAdapter(gridAdapter);
		holder.gvImage.setTag(R.id.imageloader_uri,getItem(position).getImages().toString());
		}
//		gridAdapter=new GridAdapter(mContext,getItem(position).getImages());
//		holder.gvImage.setAdapter(gridAdapter);
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
						new ViewGroup.LayoutParams(50,
							50));
			holder.starGroup.addView(imageView, layoutParams);
		}
		
		return convertView;
	}
	private class ViewHolder{
		private TextView TvName;
		private TextView tvContent;
		private TextView tvTime;
		private LinearLayout llReply;
		private TextView tvreply;
		private CircleImageView IvFace;
		private ViewGroup starGroup;
		private GridView gvImage;
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
//							.thumbnail(0.1f)//缩略图
							.skipMemoryCache( true )
							.centerCrop()
							.into(holder.image);
					holder.image.setTag(R.id.imageloader_uri,imageurls.get(position));
					}
				return convertView;
			}

			public class ViewHolder {
				public ImageView image;
			}
			@Override
			public int getCount() {
				//如果返回的值为空，则无法执行getview
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

}