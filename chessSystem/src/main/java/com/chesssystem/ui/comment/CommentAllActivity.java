package com.chesssystem.ui.comment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.chesssystem.BaseActivity;
import com.chesssystem.NimApplication;
import com.chesssystem.R;
import com.chesssystem.adapter.CommentAdapter;
import com.chesssystem.item.CommentItem;
import com.chesssystem.item.MainItem;
import com.chesssystem.util.HttpUtl;
import com.chesssystem.util.ServerUrl;
import com.chesssystem.util.HttpUtl.HttpCallbackListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author lyg
 * @time 2016-7-4上午9:51:15
 */
public class CommentAllActivity extends BaseActivity {

	private LinearLayout llBack;
	private TextView tvTitle;
	private PullToRefreshListView listView;
	private Button btAll;
	private Button btImage;
	private Button btSatisfied;
	private Button btLow;
	private PullToRefreshListView lvComment;
	private ArrayAdapter<CommentItem> commAdapter;
	private List<CommentItem> commentItems = new ArrayList<CommentItem>();
	private int msgPagerNumber = 1;
	private String storeId;
	private String commentUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment_all);
		initView();
	}

	private void initView() {
		Intent intent = getIntent();
		storeId = intent.getStringExtra("storeId");
		commentUrl=ServerUrl.getCommentAllUrl
				+ "?storeId=" + storeId + "&pageSize=15&currentPage=";
		llBack = (LinearLayout) findViewById(R.id.ll_back);
		llBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText("全部评价");
		btAll = (Button) findViewById(R.id.bt_comment_all);
		btAll.setOnClickListener(this);
		btImage = (Button) findViewById(R.id.bt_comment_image);
		btImage.setOnClickListener(this);
		btSatisfied = (Button) findViewById(R.id.bt_comment_satisfied);
		btSatisfied.setOnClickListener(this);
		btLow = (Button) findViewById(R.id.bt_comment_low);
		btLow.setOnClickListener(this);
		lvComment = (PullToRefreshListView) findViewById(R.id.lv_comment);
		lvComment.setMode(Mode.BOTH);
		commAdapter = new CommentAdapter(getApplicationContext(), commentItems);
		lvComment.setAdapter(commAdapter);
		lvComment.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (refreshView.isShownHeader()) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							msgPagerNumber = 1;
							downloadComment(commentUrl+ msgPagerNumber);
						}
					}, 1000);
				}
				if (refreshView.isShownFooter()) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							downloadComment(commentUrl+ msgPagerNumber);
						}
					}, 1000);
				}
			}
		});
		downloadComment(commentUrl+ msgPagerNumber);// 加载评论
	}

	private void downloadComment(String url) {
		HttpUtl.getHttp(url, this, new HttpCallbackListener() {
			@Override
			public void onFinish(JSONObject response) {
				try {
					JSONObject jsonObject = response.getJSONObject("data");
					/*
					 * 评论数
					 */
					btAll.setText("全部("+jsonObject.getString("allCount")+")");
					btImage.setText("有图("+jsonObject.getString("hasPicCount")+")");
					btSatisfied.setText("满意("+jsonObject.getString("goodsCount")+")");
					btLow.setText("低分("+jsonObject.getString("badCount")+")");
					tvTitle.setText("全部评价("+jsonObject.getString("allCount")+")");
					/*
					 * 评价内容
					 */
					JSONArray jsonArray = jsonObject
							.getJSONArray("rateBeans");
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
									.getJSONArray("ratePics");
							List<String> images = new ArrayList<String>();
							for (int j = 0; j < jsonArray2.length(); j++) {
								images.add(ServerUrl.getPicUrl
										+ jsonArray2.getString(j));
							}
							/*
							 * 商家回复
							 */
							String reply="";
							JSONArray replys = object
									.getJSONArray("responseBeans");
							if(replys.length()>0){
								JSONObject replay = (JSONObject) replys.get(0);
								reply=replay.getString("rateContent");
							}
							CommentItem commentItem = new CommentItem(
									object.getString("username"),
									object.getString("rateContent"),
									(int) object.getDouble("rateStar"),
									ServerUrl.getPicUrl+ object.getString("userpic"),
									object.getString("createTime"),
									reply,
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
				lvComment.onRefreshComplete();// 停止刷新
			}
		});

	}

	public void initColor() {
		btAll.setBackgroundResource(R.drawable.circle_transparent_black);
		btImage.setBackgroundResource(R.drawable.circle_transparent_black);
		btSatisfied.setBackgroundResource(R.drawable.circle_transparent_black);
		btLow.setBackgroundResource(R.drawable.circle_transparent_black);
		btAll.setTextColor(this.getResources().getColor(
				R.color.text_color_shenhui_677582));
		btImage.setTextColor(this.getResources().getColor(
				R.color.text_color_shenhui_677582));
		btSatisfied.setTextColor(this.getResources().getColor(
				R.color.text_color_shenhui_677582));
		btLow.setTextColor(this.getResources().getColor(
				R.color.text_color_shenhui_677582));
	}

	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()) {
		case R.id.ll_back:
			finish();
			break;
		// 点击全部
		case R.id.bt_comment_all:
			initColor();
			btAll.setBackgroundResource(R.drawable.circle_transparent_yellow);
			btAll.setTextColor(this.getResources().getColor(
					R.color.text_color_huangse_ff8800));
			commentUrl=ServerUrl.getCommentAllUrl
					+ "?storeId=" + storeId + "&pageSize=15&currentPage=";
			msgPagerNumber=1;
			downloadComment(commentUrl+ msgPagerNumber);
			break;
		// 点击有图
		case R.id.bt_comment_image:
			initColor();
			btImage.setBackgroundResource(R.drawable.circle_transparent_yellow);
			btImage.setTextColor(this.getResources().getColor(
					R.color.text_color_huangse_ff8800));
			commentUrl=ServerUrl.getCommentAllUrl
					+ "?storeId=" + storeId + "&pageSize=15&hasPic=1&currentPage=";
			msgPagerNumber=1;
			downloadComment(commentUrl+ msgPagerNumber);
			break;
		// 点击满意
		case R.id.bt_comment_satisfied:
			initColor();
			btSatisfied
					.setBackgroundResource(R.drawable.circle_transparent_yellow);
			btSatisfied.setTextColor(this.getResources().getColor(
					R.color.text_color_huangse_ff8800));
			commentUrl=ServerUrl.getCommentAllUrl
					+ "?storeId=" + storeId + "&pageSize=15&rateStar=4&currentPage=";
			msgPagerNumber=1;
			downloadComment(commentUrl+ msgPagerNumber);
			break;
		// 点击低分
		case R.id.bt_comment_low:
			initColor();
			btLow.setBackgroundResource(R.drawable.circle_transparent_yellow);
			btLow.setTextColor(this.getResources().getColor(
					R.color.text_color_huangse_ff8800));
			commentUrl=ServerUrl.getCommentAllUrl
					+ "?storeId=" + storeId + "&pageSize=15&rateStar=1&currentPage=";
			msgPagerNumber=1;
			downloadComment(commentUrl+ msgPagerNumber);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		NimApplication.getHttpQueues().cancelAll("commentall_request");
	}
}
