package com.chesssystem.ui.dazi;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputBinding;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.baidu.mapapi.map.Text;
import com.bumptech.glide.Glide;
import com.chesssystem.BaseActivity;
import com.chesssystem.NimApplication;
import com.chesssystem.R;
import com.chesssystem.adapter.DaziAdapter;
import com.chesssystem.adapter.DaziAdapter.DaziClickListener;
import com.chesssystem.adapter.RoomAdapter.MyClickListener;
import com.chesssystem.banner.BannerItem;
import com.chesssystem.item.DaziItem;
import com.chesssystem.item.GoodsItem;
import com.chesssystem.item.MainItem;
import com.chesssystem.util.DateUtil;
import com.chesssystem.util.HttpUtl;
import com.chesssystem.util.ServerUrl;
import com.chesssystem.util.HttpUtl.HttpCallbackListener;
import com.chesssystem.widget.CircleImageView;
import com.chesssystem.widget.MyDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.internal.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
/**
 * 
 * @author lyg
 * @time 2016-7-4上午9:51:31
 */
public class DaziActivity extends BaseActivity {
	private LinearLayout llBack;
	private TextView tvTitle;
	private TextView tvPublishInvite;
	private PullToRefreshListView lvDazi;
	private List<DaziItem> daziItems = new ArrayList<DaziItem>();
	private ArrayAdapter<DaziItem> arrayAdapter;
	private View parentView;
	private EditText edtPopContact;
	private TextView tvPopContent;
	private CircleImageView civImage;
	private TextView tvName;
	private TextView tvAge;
	private ImageView ivSex;
	private TextView tvInvites;
	private TextView tvYingyue;
	private TextView tvRecord;
	private TextView tvHaoping;
	private TextView tvZhongping;
	private TextView tvChaping;
	private TextView tvAccount;
	private LinearLayout llInvites;
	private LinearLayout llYingyue;
	private LinearLayout llRecord;
	private MyDialog myDialog;
	private int msgPagerNumber = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parentView = getLayoutInflater().inflate(R.layout.activity_dazi, null);
		setContentView(parentView);
		initView();
		initData();
	}

	private void initView() {
		llBack = (LinearLayout) findViewById(R.id.ll_back);
		llBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.dazi);
		tvPublishInvite = (TextView) findViewById(R.id.tv_finish);
		tvPublishInvite.setText(R.string.invite);
		tvPublishInvite.setOnClickListener(this);
		/*
		 * 好评、中评、差评、应约总数
		 */
		tvAccount = (TextView) findViewById(R.id.tv_dazi_account);
		tvHaoping = (TextView) findViewById(R.id.tv_dazi_haoping);
		tvZhongping = (TextView) findViewById(R.id.tv_dazi_zhongping);
		tvChaping = (TextView) findViewById(R.id.tv_dazi_chaping);
		tvAccount.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
		tvHaoping.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvZhongping.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvChaping.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		/*
		 * 我的邀约、应约、搭子记录数
		 */
		tvInvites = (TextView) findViewById(R.id.tv_dazi_invites);
		tvYingyue = (TextView) findViewById(R.id.tv_dazi_yingyue);
		tvRecord = (TextView) findViewById(R.id.tv_dazi_record);
		llInvites = (LinearLayout) findViewById(R.id.ll_dazi_invites);
		llInvites.setOnClickListener(this);
		llYingyue = (LinearLayout) findViewById(R.id.ll_dazi_yingyue);
		llYingyue.setOnClickListener(this);
		llRecord = (LinearLayout) findViewById(R.id.ll_dazi_record);
		llRecord.setOnClickListener(this);
		/*
		 * 用户信息
		 */
		civImage = (CircleImageView) findViewById(R.id.civ_dazi_face);
		tvName = (TextView) findViewById(R.id.tv_dazi_name);
		ivSex = (ImageView) findViewById(R.id.iv_dazi_sex);
		tvAge = (TextView) findViewById(R.id.tv_dazi_age);

		lvDazi = (PullToRefreshListView) findViewById(R.id.lv_dazi);
		arrayAdapter = new DaziAdapter(this, daziItems, mListener);
		lvDazi.setAdapter(arrayAdapter);
		lvDazi.setMode(Mode.BOTH);
		/**
		 * 上拉、下拉刷新
		 */
		lvDazi.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (refreshView.isShownHeader()) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							initData();
						}
					}, 1000);
				}
				if (refreshView.isShownFooter()) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							uploadDazi(msgPagerNumber);
						}
					}, 1000);
				}
			}
		});
		lvDazi.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
					Intent intent=new Intent(DaziActivity.this,DaziDetailActivity.class);
					intent.putExtra("lugId", daziItems.get(position-1).getId());
					startActivity(intent);
			}
		});
	}

	/**
	 * 获取数据
	 */
	private void initData() {
		uploadInfor();// 获取用户信息
		msgPagerNumber=1;
		uploadDazi(msgPagerNumber);// 获取搭子列表信息
	}

	/**
	 * 获取搭子列表信息
	 */
	private void uploadDazi(final int numberPager) {
		HttpUtl.getHttp(ServerUrl.getDaziListUrl + "?pageSize=15" + "&currentPage="
				+ numberPager,
				this, new HttpCallbackListener() {
					@Override
					public void onFinish(JSONObject response) {
						try {
							JSONObject jsonObject1 = new JSONObject(response
									.getString("data"));
							tvAccount.setText(jsonObject1.getString("count"));
							JSONArray jsonArray2 = jsonObject1
									.getJSONArray("lugBeans");
							/*
							 * 如果有下拉刷新时，将清空数据的操作放在这里，防止数据越界
							 */
							if(numberPager==1){
								daziItems.clear();
							}
							if (jsonArray2.length() > 0) {
								for (int i = 0; i < jsonArray2.length(); i++) {
									JSONObject object = (JSONObject) jsonArray2
											.get(i);
									DaziItem mainItem = new DaziItem(
											object.getString("lugId"),
											ServerUrl.getPicUrl
											+object.getString("userPic"),
											object.getString("nick"), 
											object.getString("birthday"), 
											object.getInt("sex"),
											object.getString("orderTime"), 
											object.getString("lugAddress"),
											object.getInt("memberCount"),
											object.getString("lugContent"),
											object.getString("telephone"));
									daziItems.add(mainItem);
								}
								msgPagerNumber++;
							}
							arrayAdapter.notifyDataSetChanged();
							lvDazi.onRefreshComplete();// 停止刷新
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onError(VolleyError e) {
						lvDazi.onRefreshComplete();// 停止刷新
					}

					@Override
					public void onFinishError(JSONObject response) {
						lvDazi.onRefreshComplete();// 停止刷新
					}
				});

	}

	/**
	 * 获取用户信息
	 */
	private void uploadInfor() {
		HttpUtl.getHttp(ServerUrl.getUserDaziInforUrl + "?userId="
				+ NimApplication.sharedPreferences.getString("userId", ""),
				this, new HttpCallbackListener() {
					@Override
					public void onFinish(JSONObject response) {
						try {
							JSONObject jsonObject1 = new JSONObject(response
									.getString("data"));
							JSONObject jsonObject2 = new JSONObject(jsonObject1
									.getString("userInfo"));
							tvName.setText(jsonObject2.getString("nick"));
							//头像
//							if(!(ServerUrl.getPicUrl
//											+ jsonObject2.getString("userPic")).equals(civImage.getTag())){
//								ImageLoader.getInstance().displayImage(
//										ServerUrl.getPicUrl
//										+ jsonObject2.getString("userPic"),
//										civImage);
//								civImage.setTag(ServerUrl.getPicUrl
//										+ jsonObject2.getString("userPic"));
//							}
							String tag = (String) civImage.getTag(R.id.imageloader_uri);
							if(!(ServerUrl.getPicUrl+ jsonObject2.getString("userPic")).equals(tag)){
								Glide.with(DaziActivity.this)
								.load(ServerUrl.getPicUrl+ jsonObject2.getString("userPic"))
								.placeholder(R.drawable.avatar_default)
								.error(R.drawable.avatar_default)
								.skipMemoryCache( true )
								.centerCrop()
								.dontAnimate()
								.into(civImage);
								civImage.setTag(R.id.imageloader_uri,ServerUrl.getPicUrl+ jsonObject2.getString("userPic"));
							}
							//评价
							tvHaoping.setText(jsonObject1.getString("goodRate"));
							tvZhongping.setText(jsonObject1
									.getString("midRate"));
							tvChaping.setText(jsonObject1.getString("badRate"));

							tvInvites.setText(jsonObject1.getString("lugCount"));
							tvYingyue.setText(jsonObject1
									.getString("applyCount"));
							tvRecord.setText(jsonObject1
									.getString("successCount"));
							// 设置性别
							if (jsonObject2.getInt("sex") == 0) {
								ivSex.setImageResource(R.drawable.girl);
							} else if (jsonObject2.getInt("sex") == 1) {
								ivSex.setImageResource(R.drawable.boy);
							}
							//设置年龄
							tvAge.setText(DateUtil.getAgeOld(jsonObject2.getString("birthday")));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onError(VolleyError e) {
					}

					@Override
					public void onFinishError(JSONObject response) {
						
					}
				});

	}
	/**
	 * 应约搭子
	 */
	private void applyDazi(String lugId,String telephone) {
		HttpUtl.getHttp(ServerUrl.postApplyUrl + "?userId="
				+ NimApplication.sharedPreferences.getString("userId", "")+"&lugId="+lugId
				+"&telephone="+telephone,
				this, new HttpCallbackListener() {
					@Override
					public void onFinish(JSONObject response) {
							Toast.makeText(DaziActivity.this,
									"应约成功!",
									Toast.LENGTH_SHORT).show();
							myDialog.dismiss();
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
		// 点击返回
		case R.id.ll_back:
			finish();
			break;
		// 点击发邀约
		case R.id.tv_finish:
			startActivity(new Intent(this, InviteActivity.class));
			break;
		// 点击我的邀约
		case R.id.ll_dazi_invites:
			Intent intentInvites=new Intent(this, MyInviteActivity.class);
			intentInvites.putExtra("stype", 0);
			startActivity(intentInvites);
			break;
		// 点击我的应约
		case R.id.ll_dazi_yingyue:
			Intent intentYingyue=new Intent(this, MyInviteActivity.class);
			intentYingyue.putExtra("stype", 1);
			startActivity(intentYingyue);
			break;
		// 点击记录
		case R.id.ll_dazi_record:
			startActivity(new Intent(this, RecordActivity.class));
			break;

		default:
			break;
		}
	}

	/**
	 * 适配器点击应约的监听
	 */
	private DaziClickListener mListener = new DaziClickListener() {
		@Override
		public void myOnClick(final int position, View v) {
			myDialog = new MyDialog(DaziActivity.this,
					R.style.inputDialog);
			edtPopContact = (EditText) myDialog.getEditText();// 方法在InputDialog中实现
			edtPopContact.setVisibility(View.VISIBLE);
			tvPopContent=(TextView) myDialog.getContent();
			tvPopContent.setText(R.string.pop_join_prompt);
			myDialog.setOnPositiveListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(!edtPopContact.getText().toString().equals("")){
						applyDazi(daziItems.get(position).getId(),edtPopContact.getText().toString());
						// 强制隐藏输入法
						imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
					}else{
						Toast.makeText(DaziActivity.this,"请填写联系方式!",
								Toast.LENGTH_SHORT).show();
					}
				}
			});
			myDialog.setOnNegativeListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					myDialog.dismiss();
				}
			});
			myDialog.show();
		}
	};
}
