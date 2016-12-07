package com.chesssystem.map;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chesssystem.R;

public class SortAdapter extends BaseAdapter {
	private Context context;
	private List<CityItem> persons;
	private LayoutInflater inflater;

	public SortAdapter(Context context, List<CityItem> persons) {
		this.context = context;
		this.persons = persons;
		this.inflater = LayoutInflater.from(context);

	}
	public void updateListView(List<CityItem> list){
		this.persons = list;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		return persons.size();
	}

	@Override
	public Object getItem(int position) {
		return persons.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewholder = null;
		CityItem person = persons.get(position);
		if (convertView == null) {
			viewholder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_city, null);
			viewholder.tv_tag = (TextView) convertView.findViewById(R.id.catalog);
			viewholder.tv_name = (TextView) convertView.findViewById(R.id.friends_item_title);
			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}
		// 获取首字母的assii值
		int selection = person.getFpinyin().charAt(0);
		// 通过首字母的assii值来判断是否显示字母
		int positionForSelection = getPositionForSelection(selection);
		if (position == positionForSelection) {// 相等说明需要显示字母
			viewholder.tv_tag.setVisibility(View.VISIBLE);
			viewholder.tv_tag.setText(person.getFpinyin());
		} else {
			viewholder.tv_tag.setVisibility(View.GONE);

		}
		viewholder.tv_name.setText(person.getCityName());
		return convertView;
	}

	//判断是否为该字母下的第一个数据
	public int getPositionForSelection(int selection) {
		for (int i = 0; i < persons.size(); i++) {
			String Fpinyin = persons.get(i).getFpinyin();
			char first = Fpinyin.toUpperCase().charAt(0);
			if (first == selection) {
				return i;
			}
		}
		return -1;

	}

	class ViewHolder {
		TextView tv_tag;
		TextView tv_name;
	}

}
