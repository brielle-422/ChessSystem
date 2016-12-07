package com.chesssystem.ui.dazi;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.chesssystem.BaseActivity;
import com.chesssystem.MainActivity;
import com.chesssystem.NimApplication;
import com.chesssystem.R;
import com.chesssystem.adapter.DaziUserAdapter;
import com.chesssystem.adapter.DaziUserAdapter.DaziUserClickListener;
import com.chesssystem.item.DaziItem;
import com.chesssystem.item.DaziUserItem;
import com.chesssystem.util.DateUtil;
import com.chesssystem.util.HttpUtl;
import com.chesssystem.util.ServerUrl;
import com.chesssystem.util.HttpUtl.HttpCallbackListener;
import com.chesssystem.widget.CircleImageView;
import com.chesssystem.widget.MyDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
/**
 * 搭子详情页面
 * @author lyg
 * @time 2016-7-4上午9:51:34
 */
public class DaziDetailActivity extends BaseActivity {
	private LinearLayout llBack;
	private TextView tvTitle;
	private PullToRefreshListView lvUser;
	private MyDialog myDialog;
	private EditText edtPopContact;
	private TextView tvPopContent;
	private TextView tvPopCancel;
	private ImageView ivYingyue;//应约按钮
	private String lugId;//搭子id
	private String masterId;//搭子主人id
	private TextView tvBeginTime;//开始时间
	private TextView tvCreateTime;//发布时间
	private TextView tvLocal;//搭子地址
	private TextView tvAcccountYingyue;//应约人数--中间
	private TextView tvYingyueNumber;//应约人数--顶部
	private TextView tvNumber;//搭子人数
	private TextView tvContent;//搭子内容
	private TextView tvContact;//联系方式
	private TextView tvSee;//看过人数
	private CircleImageView masterImage;//搭主头像
	private TextView tvName;//搭主名称
	private TextView tvOld;//搭主年龄
	private ImageView ivSex;//搭主性别
	private TextView tvGoodRate;//搭主好评数
	private TextView tvMidRate;//搭主中评数
	private TextView tvBadRate;//搭主差评数
	public static int userType;//用户在该搭子的身份
	private List<DaziUserItem> daziUserItems = new ArrayList<DaziUserItem>();
	private ArrayAdapter<DaziUserItem> arrayAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dazidetail);
		initView();
		uploadDazi();
	}
	private void initView() {
		Intent intent=getIntent();
		lugId=intent.getStringExtra("lugId");
		ivYingyue=(ImageView) findViewById(R.id.iv_yingyue);
		ivYingyue.setOnClickListener(this);
		tvAcccountYingyue=(TextView) findViewById(R.id.tv_yingyue_account);
		tvAcccountYingyue.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
		llBack=(LinearLayout) findViewById(R.id.ll_back);
		llBack.setOnClickListener(this);
		tvTitle=(TextView) findViewById(R.id.tv_title);
		tvBeginTime=(TextView) findViewById(R.id.tv_dazi_begintime);
		tvCreateTime=(TextView) findViewById(R.id.tv_dazi_createtime);
		tvSee=(TextView) findViewById(R.id.tv_dazi_view);
		tvYingyueNumber=(TextView) findViewById(R.id.tv_dazi_yingyue_number);
		tvLocal=(TextView) findViewById(R.id.tv_dazi_local);
		tvNumber=(TextView) findViewById(R.id.tv_dazi_number);
		tvContent=(TextView) findViewById(R.id.tv_dazi_content);
		tvContact=(TextView) findViewById(R.id.tv_dazi_contact);
		masterImage=(CircleImageView) findViewById(R.id.civ_dazi_face);
		tvName=(TextView) findViewById(R.id.tv_dazi_name);
		ivSex=(ImageView) findViewById(R.id.iv_dazi_sex);
		tvOld=(TextView) findViewById(R.id.tv_dazi_old);
		tvGoodRate=(TextView) findViewById(R.id.tv_dazi_goodrate);
		tvMidRate=(TextView) findViewById(R.id.tv_dazi_midrate);
		tvBadRate=(TextView) findViewById(R.id.tv_dazi_badrate);
		
		lvUser = (PullToRefreshListView) findViewById(R.id.lv_dazidetail);
		lvUser.setMode(Mode.PULL_DOWN_TO_REFRESH);
		arrayAdapter = new DaziUserAdapter(this, daziUserItems,mListener);
		lvUser.setAdapter(arrayAdapter);
		lvUser.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
			}
		});
		/**
		 * 上拉、下拉刷新
		 */
		lvUser.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (refreshView.isShownHeader()) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							uploadDazi();
						}
					}, 1000);
				}
				if (refreshView.isShownFooter()) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
						}
					}, 1000);
				}
			}
		});
	}
	/**
	 * 获取搭子详情信息
	 */
	private void uploadDazi() {
		HttpUtl.getHttp(ServerUrl.getDaziDetailUrl + "?lugId=" +lugId+ "&userId="
				+ NimApplication.sharedPreferences.getString("userId", ""),
				this, new HttpCallbackListener() {
					@Override
					public void onFinish(JSONObject response) {
						try {
							JSONObject jsonObject = new JSONObject(response
									.getString("data"));
							/*
							 * 顶部搭子信息
							 */
							masterId=jsonObject.getString("userId");
							tvCreateTime.setText(jsonObject.getString("fromToday")+"发布");
							tvBeginTime.setText(DateUtil.TimeToChange(jsonObject.getString("orderTime")));
							tvLocal.setText(jsonObject.getString("lugAddress"));
							tvNumber.setText(jsonObject.getString("memberCount"));
							tvContent.setText(jsonObject.getString("lugContent"));
							tvSee.setText(jsonObject.getInt("seenCount")+"人看过");
							tvName.setText(jsonObject.getString("nick"));
							tvOld.setText(DateUtil.getAgeOld(jsonObject.getString("birthday")));
							if(jsonObject.getInt("sex")==0){
								ivSex.setImageResource(R.drawable.girl);
							}else{
								ivSex.setImageResource(R.drawable.boy);
							}
							tvGoodRate.setText(jsonObject.getInt("goodRate")+"");
							tvMidRate.setText(jsonObject.getInt("midRate")+"");
							tvBadRate.setText(jsonObject.getInt("badRate")+"");
//							ImageLoader.getInstance().displayImage(
//									ServerUrl.getPicUrl+ jsonObject.getString("userPic"),
//									masterImage);
							String tag = (String) masterImage.getTag(R.id.imageloader_uri);
							if(!(ServerUrl.getPicUrl+ jsonObject.getString("userPic")).equals(tag)){
								Glide.with(DaziDetailActivity.this)
								.load(ServerUrl.getPicUrl+ jsonObject.getString("userPic"))
								.placeholder(R.drawable.avatar_default)
								.error(R.drawable.avatar_default)
								.skipMemoryCache( true )
								.centerCrop()
								.dontAnimate()
								.into(masterImage);
								masterImage.setTag(R.id.imageloader_uri,ServerUrl.getPicUrl+ jsonObject.getString("userPic"));
							}
							//用户状态
							userType=jsonObject.getInt("userType");
							switch (userType) {
							case 0:
								ivYingyue.setVisibility(View.VISIBLE);
								tvTitle.setText(R.string.dazi_detail);
								tvContact.setText("应约成功后可查看");
								break;
							case 1:
								ivYingyue.setVisibility(View.GONE);
								tvTitle.setText(R.string.myyingyue);
								tvContact.setText("应约成功后可查看");
								break;
							case 2:
								ivYingyue.setVisibility(View.GONE);
								tvTitle.setText(R.string.myyingyue);
								tvContact.setText(jsonObject.getString("telephone"));
								break;
							case 3:
								ivYingyue.setVisibility(View.GONE);
								tvTitle.setText(R.string.myyingyue);
								tvContact.setText("应约成功后可查看");
								break;
							case 4:
								ivYingyue.setVisibility(View.GONE);
								tvTitle.setText(R.string.myinvite);
								tvContact.setText(jsonObject.getString("telephone"));
								break;
							default:
								break;
							}
							/*
							 * 成员信息
							 */
							JSONArray jsonArray= jsonObject.getJSONArray("lugMemberBeans");
							if (jsonArray.length() > 0) {
								daziUserItems.clear();
								tvAcccountYingyue.setText(jsonArray.length()+"");
								tvYingyueNumber.setText(jsonArray.length()+"人应约");
								for (int i = 0; i < jsonArray.length(); i++) {
									JSONObject object = (JSONObject) jsonArray
											.get(i);
									DaziUserItem daziUserItem=new DaziUserItem(
											object.getString("userId"),
											object.getString("lugMemberId"),
											ServerUrl.getPicUrl +object.getString("userPic"),
											object.getString("nick"),
											object.getInt("sex"),
											object.getString("birthday"),
											object.getString("telephone"),
											object.getString("fromToday"),
											object.getInt("goodRate"),
											object.getInt("midRate"),
											object.getInt("badRate"),
											object.getInt("status"));
									daziUserItems.add(daziUserItem);
								}
								arrayAdapter.notifyDataSetChanged();
							}
							lvUser.onRefreshComplete();// 停止刷新
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onError(VolleyError e) {
						lvUser.onRefreshComplete();// 停止刷新
					}

					@Override
					public void onFinishError(JSONObject response) {
						lvUser.onRefreshComplete();// 停止刷新
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
							Toast.makeText(DaziDetailActivity.this,
									"应约成功!",
									Toast.LENGTH_SHORT).show();
							uploadDazi();//重新刷新数据
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
	/**
	 * 审核搭子成员
	 */
	private void checkMember(String memberId,int status) {
		HttpUtl.getHttp(ServerUrl.getCheckMember + "?lugMemberId="
				+ memberId
				+"&status="+status,
				this, new HttpCallbackListener() {
					@Override
					public void onFinish(JSONObject response) {
							Toast.makeText(DaziDetailActivity.this,
									"审核成功!",
									Toast.LENGTH_SHORT).show();
							uploadDazi();//重新刷新数据
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
		case R.id.ll_back:
			finish();
			break;
		case R.id.iv_yingyue:
			myDialog = new MyDialog(DaziDetailActivity.this,
					R.style.inputDialog);
			edtPopContact = (EditText) myDialog.getEditText();// 方法在InputDialog中实现
			edtPopContact.setVisibility(View.VISIBLE);
			tvPopContent=(TextView) myDialog.getContent();
			tvPopContent.setText(R.string.pop_join_prompt);
			myDialog.setOnPositiveListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(!edtPopContact.getText().toString().equals("")){
						applyDazi(lugId,edtPopContact.getText().toString());
						// 强制隐藏输入法
						imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
					}else{
						Toast.makeText(DaziDetailActivity.this,"请填写联系方式!",
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
			break;
		default:
			break;
		}
		
	}
	private  DaziUserClickListener mListener = new DaziUserClickListener() {
		@Override
		public void myOnClick(final int position, View v) {
			myDialog = new MyDialog(DaziDetailActivity.this,
					R.style.inputDialog);
			tvPopContent=(TextView) myDialog.getContent();
			tvPopContent.setText(R.string.pop_join_prompt);
			String text = "确定约"+daziUserItems.get(position).getName()+",你将可以看到对方的联系方式；同时对方也可以看到您的联系方式。"; 
			SpannableStringBuilder style = new SpannableStringBuilder(text);
			style.setSpan(new ForegroundColorSpan(getResources().getColor(
					R.color.text_color_huangse_ff8800)), 3, 5, Spannable.SPAN_EXCLUSIVE_INCLUSIVE); //设置指定位置文字的颜色
			tvPopContent.setText(style);
			tvPopCancel=(TextView) myDialog.getCancel();
			tvPopCancel.setText(R.string.refuse);
			myDialog.setOnPositiveListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					checkMember(daziUserItems.get(position).getMemverId(),2);
//					myDialog.dismiss();
				}
			});
			myDialog.setOnNegativeListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					checkMember(daziUserItems.get(position).getMemverId(),3);
//					myDialog.dismiss();
				}
			});
			myDialog.show();
			
		}
	};
	
}
