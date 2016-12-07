package com.chesssystem.ui.comment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.chesssystem.BaseActivity;
import com.chesssystem.MainActivity;
import com.chesssystem.R;
import com.chesssystem.adapter.CommentAdapter;
import com.chesssystem.adapter.CommentMyAdapter;
import com.chesssystem.adapter.CommentMyAdapter.MyClickListener;
import com.chesssystem.adapter.OrderAdapter.OrderClickListener;
import com.chesssystem.item.CommentItem;
import com.chesssystem.ui.MerchantsActivity;
import com.chesssystem.ui.order.SubmitOrderActivity;
import com.chesssystem.util.HttpUtl;
import com.chesssystem.util.ServerUrl;
import com.chesssystem.util.HttpUtl.HttpCallbackListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

public class CommentMyActivity extends BaseActivity {
	private LinearLayout llBack;
	private TextView tvTitle;
	private ArrayAdapter<CommentItem> commAdapter;
	private List<CommentItem> commentItems = new ArrayList<CommentItem>();
	private PullToRefreshListView lvComment;
	private int msgPagerNumber = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment_my);
		initView();
	}

	private void initView() {
		llBack = (LinearLayout) findViewById(R.id.ll_back);
		llBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.myEvaluate);
		lvComment = (PullToRefreshListView) findViewById(R.id.lv_comment_my);
		lvComment.setMode(Mode.BOTH);
		commAdapter = new CommentMyAdapter(this, commentItems,mListener);
		lvComment.setAdapter(commAdapter);
		lvComment.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (refreshView.isShownHeader()) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							msgPagerNumber = 1;
							downloadComment(msgPagerNumber);
						}
					}, 1000);
				}
				if (refreshView.isShownFooter()) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							downloadComment(msgPagerNumber);
						}
					}, 1000);
				}
			}
		});
		downloadComment(1);// 加载评论
		
	}
	private void downloadComment(int number) {
		HttpUtl.getHttp(ServerUrl.getMyCommentListUrl+"?userId="+sharedPreferences.getString("userId", "")
				+"&pageSize=15"+ "&currentPage=" + number, this, new HttpCallbackListener() {
			@Override
			public void onFinish(JSONObject response) {
				try {
					JSONArray jsonArray = response.getJSONArray("data");
					/*
					 * 如果有下拉刷新时，将清空数据的操作放在这里，防止数据越界
					 */
					if(msgPagerNumber==1){
						commentItems.clear();
					}
					if (jsonArray.length() > 0) {
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject object = (JSONObject) jsonArray
									.get(i);
							/*
							 * 获取图片数组
							 */
							JSONArray jsonArray2 = object
									.getJSONArray("ratePic");
							List<String> images = new ArrayList<String>();
							for (int j = 0; j < jsonArray2.length(); j++) {
								images.add(ServerUrl.getPicUrl
										+ jsonArray2.getString(j));
							}
							CommentItem commentItem =new CommentItem(
									object.getString("storeId"),
									object.getString("storeName"),
									object.getString("rateContent"),
									(int) object.getDouble("rateStar"),
									object.getString("createTime"), 
									object.getString("responseContent"),
									images);
							commentItems.add(commentItem);
						}
						msgPagerNumber++;
					}
					commAdapter.notifyDataSetChanged();
					lvComment.onRefreshComplete();// 停止刷新
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(VolleyError e) {
				lvComment.onRefreshComplete();// 停止刷新
			}
			@Override
			public void onFinishError(JSONObject response) {
			}
		});

	}
	private  MyClickListener mListener = new MyClickListener() {
		@Override
		public void myOnClick(int position, View v) {
			switch (v.getId()) {
			case R.id.rl_storeName:
				Intent intent = new Intent(CommentMyActivity.this,
						MerchantsActivity.class);
				intent.putExtra("storeId",commentItems.get(position).getStoreId());
				startActivity(intent);
				break;

			default:
				break;
			}
		}
	};
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
}
