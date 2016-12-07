package com.chesssystem.shoppingcar;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chesssystem.R;
import com.chesssystem.goods.FoodActivity;
import com.chesssystem.goods.FoodAdapter.OnChoiceNumChangeListener;
import com.chesssystem.item.GoodsItem;
import com.chesssystem.shoppingcar.CicleAddAndSubView.OnNumChangeListener;
import com.chesssystem.util.DoubleToInt;

public class ShoppingCarAdapter extends BaseAdapter {
	private List<GoodsItem> goodsItems;
	private Context mContext;
	private LayoutInflater lInflater;
	private ViewHolder holder;
	private OnShoppingcarChangeListener onShoppingcarChangeListener;
	public ShoppingCarAdapter(Context context,List<GoodsItem>datas){
		super();
		mContext=context;
		goodsItems=datas;
		lInflater=LayoutInflater.from(context);
	}
	public View getView(final int position, View convertView,  ViewGroup parent) {
		holder=null;
		if (convertView==null) {
			convertView=lInflater.inflate(R.layout.item_shoppingcar, parent, false);
			holder=new ViewHolder();
			holder.name=(TextView) convertView.findViewById(R.id.shoppingcar_name);
			holder.price=(TextView) convertView.findViewById(R.id.tv_shoppingcar_price);
			holder.addAndSubView=(CicleAddAndSubView) convertView.findViewById(R.id.bt_add_reduce_shoppingcar);
		    convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		holder.name.setText(goodsItems.get(position).getGoodsName());
		holder.price.setText(DoubleToInt.DoubleToInt(goodsItems.get(position).getGoodsPrice()));
		holder.addAndSubView.setAutoChangeNumber(true);//设置是否自增长
		holder.addAndSubView.setNum(goodsItems.get(position).getChoiceNumber());//设置默认值
		holder.addAndSubView.setOnNumChangeListener(new OnNumChangeListener() {
			@Override
			public void onNumChange(View view, int stype, int num) {
				goodsItems.get(position).setChoiceNumber(num);
				if(goodsItems.get(position).getChoiceNumber()==0){
					goodsItems.remove(position);
				}
				notifyDataSetChanged();
//				FoodActivity.foodAdapter.notifyDataSetChanged();
				onShoppingcarNum();
			}
		});
		return convertView;
	}
	private class ViewHolder{
		private TextView name;
		private TextView price;
		private CicleAddAndSubView addAndSubView;
	}
	public interface OnShoppingcarChangeListener{
		abstract void onShoppingcarNum();
	}
	public void setOnShoppingcarChangeListener(OnShoppingcarChangeListener listener) {
		onShoppingcarChangeListener = listener;
	}
	private void onShoppingcarNum(){
		if (onShoppingcarChangeListener != null) {
			onShoppingcarChangeListener.onShoppingcarNum();
		}
		
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return goodsItems.size();
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return goodsItems.get(position);
	}
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
}
