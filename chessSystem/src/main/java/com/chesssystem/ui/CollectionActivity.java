package com.chesssystem.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chesssystem.BaseActivity;
import com.chesssystem.NimApplication;
import com.chesssystem.R;
import com.chesssystem.adapter.CollectionAdapter;
import com.chesssystem.item.MainItem;
import com.chesssystem.swipemenu.SwipeMenu;
import com.chesssystem.swipemenu.SwipeMenuCreator;
import com.chesssystem.swipemenu.SwipeMenuItem;
import com.chesssystem.swipemenu.SwipeMenuListView;
import com.chesssystem.swipemenu.SwipeMenuListView.OnMenuItemClickListener;
import com.chesssystem.util.HttpUtl;
import com.chesssystem.util.ServerUrl;
import com.chesssystem.util.HttpUtl.HttpCallbackListener;
/**
 * 收藏页面
 * @author lyg
 * @time 2016-7-4上午9:51:11
 */
public class CollectionActivity extends BaseActivity {
	private LinearLayout llBack;
	private TextView tvTitle;
	private List<MainItem> collectionItems = new ArrayList<MainItem>();
	private ArrayAdapter<MainItem> arrayAdapter;
	private SwipeMenuListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collection);
		initView();
	}

	private void initView() {
		llBack = (LinearLayout) findViewById(R.id.ll_back);
		llBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.myCollection);
		mListView = (SwipeMenuListView) findViewById(R.id.lv_collection);
		arrayAdapter = new CollectionAdapter(this, collectionItems);
		mListView.setAdapter(arrayAdapter);

		/**
		 * 创建滑动菜单
		 */
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// 创建删除按钮
				SwipeMenuItem deleteItem = new SwipeMenuItem(
						getApplicationContext());
				// 按钮背景颜色
				deleteItem.setBackground(new ColorDrawable(Color.rgb(255,
						255, 255)));
				// 按钮宽度
				deleteItem.setWidth(dp2px(100));
				// 文字、颜色、大小
				deleteItem.setTitle("取消收藏");
				deleteItem.setTitleSize(14);
				deleteItem.setTitleColor(Color.WHITE);
				// 加入到菜单中
				menu.addMenuItem(deleteItem);
			}
		};
		/**
		 * 将菜单加入到list列表中
		 */
		mListView.setMenuCreator(creator);
		/**
		 * 设置List的菜单点击事件
		 */
		mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				switch (index) {
				case 0:
					if (progressDialog == null) {
						progressDialog = new ProgressDialog(CollectionActivity.this);
						progressDialog.setCanceledOnTouchOutside(false);
					}
					progressDialog.show();
					deleteCollection(collectionItems.get(position).getStoreId());
					break;
				}
			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Intent intent = new Intent(CollectionActivity.this,
						MerchantsActivity.class);
				intent.putExtra("storeId", collectionItems.get(position)
						.getStoreId());
				startActivity(intent);

			}
		});
	}

	/**
	 * 取消收藏
	 */
	public void deleteCollection(String storeId) {
		StringRequest stringRequest = new StringRequest(Method.POST,
				ServerUrl.postDeleteCollectionUrl
						+ "?userId="
						+ NimApplication.sharedPreferences.getString("userId",
								"") + "&storeId=" + storeId,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							JSONObject jsonObject = new JSONObject(response);
							if (jsonObject.getInt("retCode") == 1) {
								Toast.makeText(CollectionActivity.this,
										jsonObject.getString("retMsg"),
										Toast.LENGTH_SHORT).show();
							} else if (jsonObject.getInt("retCode") == 0) {
								Toast.makeText(CollectionActivity.this,
										"取消收藏成功", Toast.LENGTH_SHORT).show();
								if (progressDialog != null) {
									progressDialog.dismiss();
								}
								collectionItems.clear();
								uploadCollction();
								
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (progressDialog != null) {
							progressDialog.dismiss();
						}
						Toast.makeText(CollectionActivity.this, "取消收藏失败",
								Toast.LENGTH_SHORT).show();
					}
				}) {
		};
		NimApplication.getHttpQueues().add(stringRequest);
	}

	/**
	 * 获取商家收藏列表
	 * 
	 * @param numberPager
	 */
	public void uploadCollction() {
		HttpUtl.getHttp(ServerUrl.getStoreCollectionListUrl + "&userId="
				+ NimApplication.sharedPreferences.getString("userId", ""),
				this, new HttpCallbackListener() {
					@Override
					public void onFinish(JSONObject response) {
						try {
							JSONArray jsonArray = response.getJSONArray("data");
							if (jsonArray.length() > 0) {
								for (int i = 0; i < jsonArray.length(); i++) {
									JSONObject object = (JSONObject) jsonArray
											.get(i);
									MainItem mainItem = new MainItem(
											object.getString("storeId"),
											object.getString("storeName"),
											object.getString("storeDes"),
											ServerUrl.getPicUrl
													+ object.getString("storePic"),
													object.getDouble("distance"), object.getString("storeAdd"));
									collectionItems.add(mainItem);
								}
							}
							arrayAdapter.notifyDataSetChanged();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onError(VolleyError e) {
					}

					@Override
					public void onFinishError(JSONObject response) {
						// TODO Auto-generated method stub
						
					}
				});
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ll_back:
			finish();
			break;

		default:
			break;
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		collectionItems.clear();
		uploadCollction();// 获取商家收藏列表
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getResources().getDisplayMetrics());
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
	}

}
