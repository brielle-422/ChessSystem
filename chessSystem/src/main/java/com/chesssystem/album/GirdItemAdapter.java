package com.chesssystem.album;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;




import com.chesssystem.R;
import com.chesssystem.album.ImageLoader.Type;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
/**
 * 
 * @author lyg
 * @time 2016-7-4上午9:48:30
 */
public class GirdItemAdapter extends BaseAdapter{
	final int VIEW_TYPE = 2;
	final int TYPE_1 = 0;
	final int TYPE_2 = 1;
	/**
	 * 用户选择的图片，存储为图片的完整路径
	 */
	public static List<String> mSelectedImage = new LinkedList<String>();
	/**
	 * 文件夹路径
	 */
	private String mDirPath;

	private Context context;

	private List<String> mDatas=new ArrayList<String>();//所有的图片


	public GirdItemAdapter(Context context, List<String> mDatas,String dirPath) {
		super();
		this.context = context;
		this.mDatas = mDatas;
		this.mDirPath=dirPath;
	}

	public void changeData(List<String> mDatas,String dirPath){
		this.mDatas = mDatas;
		this.mDirPath=dirPath;
		notifyDataSetChanged();

	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public String getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public int getViewTypeCount() {
		return VIEW_TYPE;
	}
	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView==null){
				convertView=LayoutInflater.from(context).inflate(R.layout.item_grid, null);
				holder=new ViewHolder();
				holder.id_item_image=(ImageView)convertView.findViewById(R.id.id_item_image);
				holder.id_item_select=(ImageButton)convertView.findViewById(R.id.id_item_select);
				convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
			holder.id_item_select.setImageResource(R.drawable.picture_unselected);
			holder.id_item_image.setImageResource(R.drawable.pictures_no);
			ImageLoader.getInstance(3,Type.LIFO).loadImage(mDirPath + "/" + mDatas.get(position), holder.id_item_image);
			holder.id_item_image.setColorFilter(null);
			holder.id_item_image.setOnClickListener(new MyOnClickListener(holder, position));
			if (mSelectedImage.contains(mDirPath + "/" + mDatas.get(position))){
				holder.id_item_select.setImageResource(R.drawable.pictures_selected);
				holder.id_item_image.setColorFilter(Color.parseColor("#77000000"));
			}else{
				holder.id_item_select.setImageResource(R.drawable.picture_unselected);
				holder.id_item_image.setColorFilter(Color.parseColor("#00000000"));
			}
		return convertView;
	}
	class MyOnClickListener implements OnClickListener{
		ViewHolder holder;
		int position;
		MyOnClickListener(ViewHolder holder,int  position){
			this.holder=holder;
			this.position=position;
		}
		@Override
		public void onClick(View v) {
			// 已经选择过该图片
			if (mSelectedImage.contains(mDirPath + "/" + mDatas.get(position))){
				mSelectedImage.remove(mDirPath + "/" + mDatas.get(position));
				holder.id_item_select.setImageResource(R.drawable.picture_unselected);
				holder.id_item_image.setColorFilter(null);
			} else{// 未选择该图片
				if(mSelectedImage.size()>=SelectPhotoActivity.maxSize){
//					Toast.makeText(context, "最多只能选择9张图片", Toast.LENGTH_SHORT).show();
					return ;
				}
				mSelectedImage.add(mDirPath + "/" + mDatas.get(position));
				holder.id_item_select.setImageResource(R.drawable.pictures_selected);
				holder.id_item_image.setColorFilter(Color.parseColor("#77000000"));
			}
			onPhotoSelectedListener.photoClick(mSelectedImage);
		}
		
	}
	class ViewHolder{
		ImageView id_item_image;
		ImageButton id_item_select;
	}
	class ViewHolder2{
		LinearLayout id_item_image2;
	}
	public OnPhotoSelectedListener onPhotoSelectedListener;
	public void setOnPhotoSelectedListener(OnPhotoSelectedListener onPhotoSelectedListener){
		this.onPhotoSelectedListener=onPhotoSelectedListener;
	}
	public  interface OnPhotoSelectedListener{
		public void photoClick(List<String> number);
//		public void takePhoto();
	}

}
