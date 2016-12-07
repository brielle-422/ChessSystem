package com.chesssystem.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.chesssystem.BaseActivity;
import com.chesssystem.NimApplication;
import com.chesssystem.R;
import com.chesssystem.map.SideBar.OnTouchingLetterChangedListener;
import com.chesssystem.util.CityFileUtils;
import com.chesssystem.widget.ClearEditText;

public class CityChoiceActivity extends BaseActivity {
	private LinearLayout llBack;
	private TextView tvTitle;
	private ListView lvCity;
	private ClearEditText edtInput;
	private SideBar sidebar;
	private TextView dialog;
	private SortAdapter sortadapter;
	private List<CityItem> data=new ArrayList<CityItem>();
	private List<CityItem> dataFinal=new ArrayList<CityItem>();
	View view;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view=getLayoutInflater().inflate(R.layout.activity_city,null);
		setContentView(view);
		initView();
		initDate();
	}
	private void initView() {
		llBack = (LinearLayout) findViewById(R.id.ll_back);
		llBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText("选择城市");
		lvCity=(ListView) findViewById(R.id.lv_city);
		sidebar = (SideBar) view.findViewById(R.id.sidrbar);
		dialog = (TextView) view.findViewById(R.id.dialog);
		sidebar.setTextView(dialog);
		// 设置字母导航触摸监听
		sidebar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = sortadapter.getPositionForSelection(s.charAt(0));

				if (position != -1) {
					lvCity.setSelection(position);
				}
			}
		});
		lvCity.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				//强制收起输入法
				imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				NimApplication.city= data.get(position).getCityName();
				Intent intent = new Intent();
				intent.putExtra("city", data.get(position).getCityName());
				setResult(0,intent);
				finish();
			}
		});
		edtInput=(ClearEditText) findViewById(R.id.et_input);
		edtInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}
			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});
		
	}
	private void initDate() {
		List<String> list = CityFileUtils.getCityFile(this);
		List<CityItem>cityItems=new ArrayList<CityItem>();
		for(String a :list){
			CityItem arg0=new CityItem(a);
			cityItems.add(arg0);
		}
		data = getData(cityItems);
		dataFinal = getData(cityItems);
		Collections.sort(data, new PinyinComparator());
		Collections.sort(dataFinal, new PinyinComparator());
		sortadapter = new SortAdapter(this, data);
		lvCity.setAdapter(sortadapter);
	}
	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<CityItem> filterDateList = new ArrayList<CityItem>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = dataFinal;
		} else {
			filterDateList.clear();
			for (CityItem personMsg : dataFinal) {
				String name = personMsg.getCityName();
				String pingyin=personMsg.getPinyin();
				if (name.indexOf(filterStr.toString()) != -1 ||pingyin.indexOf(filterStr.toString())!=-1)
				 {
					filterDateList.add(personMsg);
				}
			}
		}
		// 根据a-z进行排序
		Collections.sort(filterDateList, new PinyinComparator());
		data.clear();
			for(CityItem arg0:filterDateList){
				data.add(arg0);
		}
			sortadapter.notifyDataSetChanged();
	}
	private List<CityItem> getData(List<CityItem> data){
		List<CityItem> listarray = new ArrayList<CityItem>();
		for (int i = 0; i < data.size(); i++) {
			String pinyin = PinyinUtils.getPingYin(data.get(i).getCityName());
			String Fpinyin = pinyin.substring(0, 1).toUpperCase();

			CityItem person = new CityItem();
			person.setCityName(data.get(i).getCityName());
			person.setPinyin(pinyin);
			// 正则表达式，判断首字母是否是英文字母
			if (Fpinyin.matches("[A-Z]")) {
				person.setFpinyin(Fpinyin);
			} else {
				person.setFpinyin("#");
			}

			listarray.add(person);
		}
		return listarray;

	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ll_back:
			finish();
			//强制收起输入法
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
			break;

		default:
			break;
		}
	}
}
