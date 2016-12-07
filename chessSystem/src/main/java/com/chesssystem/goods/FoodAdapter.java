package com.chesssystem.goods;

import java.util.Arrays;
import java.util.List;

import com.bumptech.glide.Glide;
import com.chesssystem.MainActivity;
import com.chesssystem.R;
import com.chesssystem.adapter.DaziAdapter.DaziClickListener;
import com.chesssystem.goods.PinnedHeaderListView.PinnedHeaderAdapter;
import com.chesssystem.item.GoodsItem;
import com.chesssystem.shoppingcar.CicleAddAndSubView;
import com.chesssystem.shoppingcar.CicleAddAndSubView.OnNumChangeListener;
import com.chesssystem.util.DoubleToInt;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author lyg
 * @time 2016-7-4上午9:49:47
 */
public class FoodAdapter extends BaseAdapter implements SectionIndexer, PinnedHeaderAdapter, OnScrollListener {  
	private static final String TAG = "FoodAdapter";
	
	private OnPinneChangeListener changeListener;
	private OnChoiceNumChangeListener onChoiceNumChangeListener;
	public  boolean isChangeable;
	private Context context;
	private List<GoodsItem> foodList;//商品集合
	private List<FoodTypeModel> foodTypeList;//商品类型集合
	private List<Integer> foodTpyePositionList;//商品类型位置集合
	
	private int mLocationPosition = -1;
	private LayoutInflater inflater;
	
	public void setChangeable(boolean isChangeable) {
		this.isChangeable = isChangeable;
	}
	public FoodAdapter(Context context, List<GoodsItem> foodList, List<FoodTypeModel> foodTypeList, List<Integer> foodTpyePositionList) {
		super();
		this.context = context;
		this.foodList = foodList;
		this.foodTypeList = foodTypeList;
		this.foodTpyePositionList = foodTpyePositionList;
		
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() { 
		return foodList.size();
	}

	@Override
	public GoodsItem getItem(int position) { 
		return foodList.get(position);
	}

	@Override
	public long getItemId(int position) { 
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		int section = getSectionForPosition(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_doublelistview_right, null);
		}
		LinearLayout mHeaderParent = (LinearLayout) convertView.findViewById(R.id.friends_item_header_parent);
		TextView mHeaderText = (TextView) convertView.findViewById(R.id.tv_food_content_title);
		if (getPositionForSection(section) == position) {
			mHeaderParent.setVisibility(View.VISIBLE);
			mHeaderText.setText(foodTypeList.get(section).getFoodTypeName());
		} else {
			mHeaderParent.setVisibility(View.GONE);
		}
		final GoodsItem goodsItem=foodList.get(position);
		TextView goodsName = (TextView) convertView.findViewById(R.id.tv_doubleright_name);
		TextView goodsNum= (TextView) convertView.findViewById(R.id.tv_doubleright_sales);
		TextView goodsPrice= (TextView) convertView.findViewById(R.id.tv_doubleright_price);
		final CicleAddAndSubView addAndSubView=(CicleAddAndSubView) convertView.findViewById(R.id.bt_add_reduce);
		ImageView goodsImage=(ImageView) convertView.findViewById(R.id.iv_doubleright_image);
		/*
		 * data
		 */
		String tag = (String) goodsImage.getTag(R.id.imageloader_uri);
		if(!goodsItem.getImageUrl().equals(tag)){
			Glide.with(context)
			.load(goodsItem.getImageUrl())
			.placeholder(R.color.gray2)
			.error(R.color.gray2)
			.skipMemoryCache( true )
			.centerCrop()
			.into(goodsImage);
			goodsImage.setTag(R.id.imageloader_uri,goodsItem.getImageUrl());
		}
		goodsName.setText(goodsItem.getGoodsName()); 
		goodsNum.setText("月售"+goodsItem.getGoodsSales()+"份"); 
		goodsPrice.setText(DoubleToInt.DoubleToInt(goodsItem.getGoodsPrice())); 
		addAndSubView.setAutoChangeNumber(true);//设置是否自增长
		addAndSubView.setNum(goodsItem.getChoiceNumber());//设置默认值
		addAndSubView.setOnNumChangeListener(new OnNumChangeListener() {
			@Override
			public void onNumChange(View view, int stype, int num) {
				foodList.get(position).setChoiceNumber(num);
				onChangeChoiceNum();
				int m=0;
				for(int i=0;i<foodList.size();i++){
					m+=foodList.get(i).getChoiceNumber();
				}
			}
		});
		return convertView;
	}

	
	
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) { 
		if(changeListener!=null){
			changeListener.onMyScrollStateChanged(view, scrollState);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) { 
		if (view instanceof PinnedHeaderListView) {
			((PinnedHeaderListView) view).configureHeaderView(firstVisibleItem);
		}
	}

	@Override
	public int getPinnedHeaderState(int position) {
		int realPosition = position;
		if (realPosition < 0 || (mLocationPosition != -1 && mLocationPosition == realPosition)) {
			return PINNED_HEADER_GONE;
		}
		mLocationPosition = -1;
		int section = getSectionForPosition(realPosition);
		int nextSectionPosition = getPositionForSection(section + 1);
		if (nextSectionPosition != -1 && realPosition == nextSectionPosition - 1) {
			return PINNED_HEADER_PUSHED_UP;
		}
		return PINNED_HEADER_VISIBLE;
	}

	@Override
	public void configurePinnedHeader(View header, int position, int alpha) { 
		int realPosition = position;
		int section = getSectionForPosition(realPosition);
		if (isChangeable) {// 只有在Activity中添加了此监听，并且下一个标题到达了顶端的时候，才触发监听
			onChange(foodTypeList.get(section));
		}
		 
		
		FoodTypeModel model=(FoodTypeModel) getSections()[section];
		 
		((TextView) header.findViewById(R.id.tv_food_title)).setText(model.getFoodTypeName());
		
	}

	@Override
	public Object[] getSections() { 
		return foodTypeList.toArray();
	}

	@Override
	public int getPositionForSection(int section) {
		if (section < 0 || section >= foodTypeList.size()) {
			return -1;
		} 
		return foodTpyePositionList.get(section);
	}

	@Override
	public int getSectionForPosition(int position) {
		if (position < 0 || position >= getCount()) {
			return -1;
		}
		 //注意这个方法的返回值，它就是index<0时，返回-index-2的原因
        //解释Arrays.binarySearch，如果搜索结果在数组中，刚返回它在数组中的索引，如果不在，刚返回第一个比它大的索引的负数-1
		int index = Arrays.binarySearch(foodTpyePositionList.toArray(), position);
		return index >= 0 ? index : -index - 2;
	}
	
	// ------------------------------------------
	public interface OnPinneChangeListener {
		abstract void onChange(FoodTypeModel foodTypeModel);
		abstract void onMyScrollStateChanged(AbsListView view, int scrollState);
		
	}

	public void setOnPinneChangeListener(OnPinneChangeListener listener) {
		changeListener = listener;
		isChangeable = true;
	}

	private void onChange(FoodTypeModel foodTypeModel) {
		if (changeListener != null) {
			changeListener.onChange(foodTypeModel);
		}
	}
	
	// ------------------------------------------
	public interface OnChoiceNumChangeListener{
		abstract void onChangeChoiceNum();
	}
	public void setOnChoiceNumChangeListener(OnChoiceNumChangeListener listener) {
		onChoiceNumChangeListener = listener;
	}
	private void onChangeChoiceNum(){
		if (onChoiceNumChangeListener != null) {
			onChoiceNumChangeListener.onChangeChoiceNum();
		}
		
	}

}
