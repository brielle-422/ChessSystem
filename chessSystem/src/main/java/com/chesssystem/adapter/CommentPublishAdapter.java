package com.chesssystem.adapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.chesssystem.R;
import com.chesssystem.album.ImageLoader;
import com.chesssystem.album.ImageLoader.Type;
import com.chesssystem.item.CommentPublishItem;
import com.chesssystem.ui.comment.CommentPublishActivity;
import com.lidroid.xutils.db.sqlite.CursorUtils.FindCacheSequence;
import com.nostra13.universalimageloader.core.download.ImageDownloader.Scheme;
/**
 * 
 * @author lyg
 * @time 2016-7-4上午9:48:08
 */
public class CommentPublishAdapter extends BaseAdapter{
	private List<CommentPublishItem> imageItems;
	private Context mContext;
	private LayoutInflater lInflater;
	
	public CommentPublishAdapter(Context context,List<CommentPublishItem>datas){
		mContext=context;
		imageItems=datas;
		lInflater=LayoutInflater.from(context);
	}
	@Override
	public View getView( final int position, View convertView,  ViewGroup parent) {
		 ViewHolder holder=null;
		if (convertView==null) {
			convertView=lInflater.inflate(R.layout.item_comment_image, parent, false);
			holder=new ViewHolder();
			holder.image=(ImageView) convertView.findViewById(R.id.item_pickphto_iv);
			holder.delete=(ImageView) convertView.findViewById(R.id.item_pickphto_ib);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		if(position==imageItems.size()){
			holder.delete.setVisibility(View.GONE);
			holder.image.setImageResource(R.drawable.image_add);
			if (position == 3) {
				holder.image.setVisibility(View.GONE);
			}
		}else{
			holder.delete.setVisibility(View.VISIBLE);
			holder.image.setImageBitmap(imageItems.get(position).getImageBitmap());
		}
		holder.delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				CommentPublishActivity.commentPublishItems.remove(position);
				CommentPublishActivity.imageAdapter.notifyDataSetChanged();
			}
		});
		return convertView;
	}
	private class ViewHolder{
		private ImageView image;
		private ImageView delete;
	}
	@Override
	public int getCount() {
		if(imageItems.size()==3){
			return 3;
		}
		return (imageItems.size()+1);
	}
	@Override
	public Object getItem(int arg0) {
		return null;
	}
	@Override
	public long getItemId(int arg0) {
		return 0;
	}

}