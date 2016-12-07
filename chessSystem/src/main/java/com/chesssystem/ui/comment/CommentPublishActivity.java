package com.chesssystem.ui.comment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baidu.mapapi.map.Text;
import com.chesssystem.BaseActivity;
import com.chesssystem.NimApplication;
import com.chesssystem.R;
import com.chesssystem.adapter.CommentPublishAdapter;
import com.chesssystem.adapter.OrderGoodsAdapter;
import com.chesssystem.adapter.CommentMyAdapter.GridAdapter.ViewHolder;
import com.chesssystem.album.GirdItemAdapter;
import com.chesssystem.album.SelectPhotoActivity;
import com.chesssystem.album.ImageLoader.Type;
import com.chesssystem.item.CommentPublishItem;
import com.chesssystem.item.GoodsItem;
import com.chesssystem.util.DoubleToInt;
import com.chesssystem.util.HttpUtl;
import com.chesssystem.util.HttpUtl.HttpCallbackListener;
import com.chesssystem.util.ImageUtil;
import com.chesssystem.util.PhotoFileUtils;
import com.chesssystem.util.ServerUrl;
import com.chesssystem.widget.BaseListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.core.ImageLoader;
/**
 * 订单评价页面
 * @author lyg
 * @time 2016-7-4上午9:51:18
 */
public class CommentPublishActivity extends BaseActivity {
	private LinearLayout llBack;
	private TextView tvTitle;
	private TextView tvFinish;
	private BaseListView lvGoods;
	private String storeId;
	private PopupWindow pop = null;
	private LinearLayout ll_popup;
	private View viewParent;
	private RelativeLayout RlParent;
	private Button btCamera;
	private Button btPhoto;
	private Button btCancel;
	private static final int TAKE_PICTURE = 0x000001;
	private static final int SELECT_PHOTO = 0x000002;
	private int Star;
	private RelativeLayout rlRoom;//棋牌室栏目
	private TextView tvRoomName;//棋牌室名称
	private TextView tvRoomPrice;//棋牌室总价
	private ImageView ivRoomImage;//棋牌室图片
	
	private LinearLayout llGoods;//商品栏目
	private TextView tvGoodsPrice;//商品总价
	private List<GoodsItem>goodsItems=new ArrayList<GoodsItem>();
	private ArrayAdapter<GoodsItem> arrayAdapter;
	private RatingBar rbStar;//星级
	private TextView tvComment;//满意程度
	private EditText etcomment;//评语输入框
	private ImageView ivCheckBox;//匿名按钮
	private boolean isAnonymous=false;//是否匿名
	private GridView gvCommentPic;
	private TextView tvDafen;
	public static List<CommentPublishItem>commentPublishItems=new ArrayList<CommentPublishItem>();
	public static CommentPublishAdapter imageAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		viewParent=getLayoutInflater().inflate(R.layout.activity_comment_publish,null);
		setContentView(viewParent);
		initView();
		initPop();
		uploadMsg();
	}
	/*
	 * popupwindow
	 */
	private void initPop() {
		pop = new PopupWindow(this);
		View popView =getLayoutInflater().inflate(R.layout.pop_photo, null);
		ll_popup = (LinearLayout) popView.findViewById(R.id.ll_popup);
		pop.setWidth(LayoutParams.MATCH_PARENT);
		pop.setHeight(LayoutParams.WRAP_CONTENT);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setContentView(popView);
		RlParent = (RelativeLayout) popView.findViewById(R.id.parent);
		RlParent.setOnClickListener(this);
		btCamera = (Button) popView.findViewById(R.id.item_popupwindows_camera);
		btCamera.setOnClickListener(this);
		btPhoto = (Button) popView.findViewById(R.id.item_popupwindows_Photo);
		btPhoto.setOnClickListener(this);
		btCancel = (Button) popView.findViewById(R.id.item_popupwindows_cancel);
		btCancel.setOnClickListener(this);		
	}
	private void initView() {
		llBack = (LinearLayout) findViewById(R.id.ll_back);
		llBack.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText(R.string.evaluate);
		tvFinish=(TextView) findViewById(R.id.tv_finish);
		tvFinish.setOnClickListener(this);
		tvFinish.setText(R.string.release);
		ivCheckBox=(ImageView) findViewById(R.id.iv_check);
		ivCheckBox.setOnClickListener(this);
		tvDafen=(TextView) findViewById(R.id.tv_dafen);
		lvGoods=(BaseListView) findViewById(R.id.lv_order_goods);
		tvGoodsPrice=(TextView) findViewById(R.id.tv_goods_money);
		rlRoom=(RelativeLayout) findViewById(R.id.rl_order_room);
		arrayAdapter=new OrderGoodsAdapter(this, goodsItems);
		lvGoods.setAdapter(arrayAdapter);
		gvCommentPic=(GridView) findViewById(R.id.gv_comment_pic);
		imageAdapter=new CommentPublishAdapter(this, commentPublishItems);
		gvCommentPic.setAdapter(imageAdapter);
		gvCommentPic.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if (position ==commentPublishItems.size()) {
					ll_popup.startAnimation(AnimationUtils.loadAnimation(CommentPublishActivity.this,R.anim.activity_translate_in));
					pop.showAtLocation(viewParent, Gravity.BOTTOM, 0, 0);
				} else {
				}
			}
		});
		llGoods=(LinearLayout) findViewById(R.id.ll_goods);
		tvRoomName=(TextView) findViewById(R.id.tv_order_roomname);
		tvRoomPrice=(TextView) findViewById(R.id.tv_order_price);
		ivRoomImage=(ImageView) findViewById(R.id.iv_order_image);
		tvComment=(TextView) findViewById(R.id.tv_comment);
		etcomment=(EditText) findViewById(R.id.et_comment_input);
		rbStar=(RatingBar) findViewById(R.id.rb_comment_star);
		rbStar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			@Override
			public void onRatingChanged(RatingBar arg0, float arg1, boolean arg2) {
				Star=(int)arg1;
				switch ((int)arg1) {
				case 1:
					tvComment.setText("非常不满意");
					break;
				case 2:
					tvComment.setText("很不满意");
					break;
				case 3:
					tvComment.setText("一般");
					break;
				case 4:
					tvComment.setText("很满意");
					break;
				case 5:
					tvComment.setText("非常满意");
					break;

				default:
					break;
				}
			}
		});
	}
	/**
	 * 获取订单详情
	 */
	private void uploadMsg() {
		HttpUtl.getHttp(ServerUrl.getOrderDetailUrl + "?orderId=" + getIntent().getStringExtra("orderId"),
				this, new HttpCallbackListener() {
					@Override
					public void onFinish(JSONObject response) {
						 JSONObject jsonObject;
						try {
							jsonObject = response.getJSONObject("data");
							storeId=jsonObject.getString("storeId");
							JSONArray jsonArray=jsonObject.getJSONArray("orderDetailBeans");
							/*
							 * 该订单为商品
							 */
							if(jsonObject.getInt("goodsType")==1){
								rlRoom.setVisibility(View.GONE);	
								llGoods.setVisibility(View.VISIBLE);
								tvGoodsPrice.setText(DoubleToInt.DoubleToInt(jsonObject.getDouble("orderPrice")));
								for (int i = 0; i < jsonArray.length(); i++) {
									JSONObject object = (JSONObject) jsonArray
											.get(i);
									GoodsItem goodsItem=new GoodsItem("",
											object.getString("goodsName"),"",0, 
											object.getDouble("price"),"", 
											object.getInt("buyCount"));
									goodsItems.add(goodsItem);
								}
								arrayAdapter.notifyDataSetChanged();
							}
							/*
							 * 该订单为棋牌室
							 */
							else{
								rlRoom.setVisibility(View.VISIBLE);	
								llGoods.setVisibility(View.GONE);
								JSONObject object = (JSONObject) jsonArray.get(0);
								tvRoomName.setText(object.getString("goodsName"));
								tvRoomPrice.setText(DoubleToInt.DoubleToInt(jsonObject.getDouble("orderPrice")));
								ImageLoader.getInstance().displayImage(ServerUrl.getPicUrl+object.getString("roomPic"), ivRoomImage);
								
							}
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
				})
				;
	}
	/**
	 * 发布评论接口
	 */
	private void postComment(final String imageStr) {
		StringRequest stringRequest = new StringRequest(Method.POST,
				ServerUrl.addOrderCommentUrl, new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							JSONObject jsonObject = new JSONObject(response);
							if (jsonObject.getInt("retCode") == 0) {
								if (progressDialog != null) {
									progressDialog.dismiss();
								}
								finishToNewPage();
								Toast.makeText(CommentPublishActivity.this, "发表成功",
										Toast.LENGTH_SHORT).show();
							} else {
								if (progressDialog != null) {
									progressDialog.dismiss();
								}
								Toast.makeText(CommentPublishActivity.this,
										jsonObject.getString("retMsg"),
										Toast.LENGTH_SHORT).show();
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
						Toast.makeText(CommentPublishActivity.this, error + "",
								Toast.LENGTH_SHORT).show();
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId", sharedPreferences.getString("userId", ""));
				map.put("rateContent",etcomment.getText().toString());
				map.put("rateStar",String.valueOf(Star));
				map.put("orderId", getIntent().getStringExtra("orderId"));
				map.put("storeId",storeId);
				if(imageStr.length()>0)
				map.put("imageIdStr",imageStr);
				if(isAnonymous){
					map.put("anonymous","1");
				}else{
					map.put("anonymous","2");	
				}
				return map;
			}
		};
		NimApplication.getHttpQueues().add(stringRequest);
	}
	/**
	 * 保存评论图片
	 */
	public void savaRatePic(){
		HttpUtils http = new HttpUtils();
		RequestParams params = new RequestParams();
		for(int i=0;i<commentPublishItems.size();i++){
			File file=commentPublishItems.get(i).getImageFile();
			params.addBodyParameter("image"+(i+1), file);
		}
		http.send(HttpMethod.POST,ServerUrl.postRatePicUrl, params,
				new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						if (progressDialog != null) {
							progressDialog.dismiss();
						}
						Toast.makeText(CommentPublishActivity.this,"发表失败", Toast.LENGTH_SHORT).show();
					}
					@Override
					public void onLoading(long total, long current,
							boolean isUploading) {
						super.onLoading(total, current, isUploading);
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						try {
							JSONObject jsonObject = new JSONObject(arg0.result);
							if(jsonObject.getInt("retCode")==0){
								String imageStr=jsonObject.getString("data").replace("[", "");
								imageStr=imageStr.replace("]", "");
								/*
								 * 保存图片成功，下一步发布评论
								 */
								postComment(imageStr);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});
	}
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ll_back:
			finish();
			break;
			//点击发布
		case R.id.tv_finish:
			if(etcomment.getText().toString().equals("")){
				Toast.makeText(CommentPublishActivity.this,"请输入评论内容",
						Toast.LENGTH_SHORT).show();
			}else if(Star==0){
				Toast.makeText(CommentPublishActivity.this,"请进行星级评价",
						Toast.LENGTH_SHORT).show();
			}else if(commentPublishItems.size()>0){
				if (progressDialog == null) {
					progressDialog = new ProgressDialog(this);
					progressDialog.setCanceledOnTouchOutside(false);
				}
				progressDialog.show();
				savaRatePic();
			}else{
				if (progressDialog == null) {
					progressDialog = new ProgressDialog(this);
					progressDialog.setCanceledOnTouchOutside(false);
				}
				progressDialog.show();
				postComment("");
			}
			break;
			//点击匿名框
		case R.id.iv_check:
			if(isAnonymous){
				isAnonymous=false;
				ivCheckBox.setImageResource(R.drawable.checkbox_normal);
			}else{
				isAnonymous=true;
				ivCheckBox.setImageResource(R.drawable.checkbox_press);
			}
			break;
			//点击相机
		case R.id.item_popupwindows_camera:
			pop.dismiss();
			ll_popup.clearAnimation();
			Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File out = new File(getPhotopath());  
            Uri uri = Uri.fromFile(out);  
            // 获取拍照后未压缩的原图片，并保存在uri路径中  
            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri); 
			startActivityForResult(openCameraIntent, TAKE_PICTURE);
			break;
			//点击相册
		case R.id.item_popupwindows_Photo:
			pop.dismiss();
			ll_popup.clearAnimation();
			Intent selectPhotoIntent=new Intent(this, SelectPhotoActivity.class);
			selectPhotoIntent.putExtra("picNumber", 3-commentPublishItems.size());
			startActivityForResult(selectPhotoIntent,SELECT_PHOTO);
			break;
			//点击取消
		case R.id.item_popupwindows_cancel:
			pop.dismiss();
			ll_popup.clearAnimation();
			break;	
			//点击其他位置
		case R.id.parent:
			pop.dismiss();
			ll_popup.clearAnimation();
			break;
		default:
			break;
		}
	}
	/** 
     * 获取原图片存储路径 
     * @return 
     */  
	private String photoUrl;
    private String getPhotopath() {  
        // 照片全路径  
        String fileName = "";  
        // 文件夹路径  
        String pathUrl = Environment.getExternalStorageDirectory()+"/chess_photos";  
        // 生成文件名
        SimpleDateFormat t = new SimpleDateFormat("yyyyMMddssSSS");
        String filename = "MT" + (t.format(new Date())) + ".jpg";
        File file = new File(pathUrl);  
        file.mkdirs();// 创建文件夹  
        fileName = pathUrl + filename;  
        photoUrl=fileName;
        return fileName;  
    } 
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PICTURE:
			if (resultCode == RESULT_OK) {
				Bitmap loacalBitmap = ImageUtil.getLoacalBitmap(photoUrl);
				File file = ImageUtil.BitmapToFile(loacalBitmap);
			    CommentPublishItem item=new CommentPublishItem(photoUrl, file,
			    		loacalBitmap);
				commentPublishItems.add(item);
				imageAdapter.notifyDataSetChanged();
			}
			break;
		case SELECT_PHOTO:
			if (resultCode == 0) {
				if (data != null){
					ArrayList<String> stringArrayListExtra = data.getStringArrayListExtra("photo");
					for(String picUrl:stringArrayListExtra){
//						/*
//						 * 对图片进行压缩处理
//						 */
						Bitmap loacalBitmap = ImageUtil.getLoacalBitmap(picUrl);
//						File file = ImageUtil.BitmapToFile(loacalBitmap);
						File file=new File(picUrl);
						CommentPublishItem item=new CommentPublishItem(picUrl, file,
								loacalBitmap);
						commentPublishItems.add(item);
					}
					imageAdapter.notifyDataSetChanged();
				}
			}
			break;
		}
		
	}
	/**
	 * 发布完成后新的界面
	 */
	private LinearLayout llBgNew;
	private LinearLayout llBgOld;
	private TextView tvCommentNew;
	private GridView gvImageNew;
	private GridAdapter gridAdapter;
	public void finishToNewPage(){
		tvDafen.setVisibility(View.GONE);	
		tvFinish.setVisibility(View.GONE);
		tvComment.setVisibility(View.GONE);
		llBgOld=(LinearLayout) findViewById(R.id.ll_bg_old);
		llBgOld.setVisibility(View.GONE);
		llBgNew=(LinearLayout) findViewById(R.id.ll_comment_new);
		llBgNew.setVisibility(View.VISIBLE);
		tvCommentNew=(TextView) findViewById(R.id.tv_comment_text);
		tvCommentNew.setText(etcomment.getText().toString());
		gvImageNew=(GridView) findViewById(R.id.gv_comment_pic_new);
		gridAdapter=new GridAdapter(this);
		gvImageNew.setAdapter(gridAdapter);
	}
	/*
	  * 发布完的GridView的适配器
	  */
		public  class GridAdapter extends BaseAdapter {
			private LayoutInflater inflater;
			public GridAdapter(Context context) {
				inflater = LayoutInflater.from(context);
			}
			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				final ViewHolder holder;
				if (convertView == null) {
					convertView = inflater.inflate(R.layout.item_image,parent, false);
					holder = new ViewHolder();
					holder.image =(ImageView) convertView.findViewById(R.id.item_image);
					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				holder.image.setImageBitmap(commentPublishItems.get(position).getImageBitmap());
				return convertView;
			}

			public class ViewHolder {
				public ImageView image;
			}
			@Override
			public int getCount() {
				//如果返回的值为空，则无法执行getview
				return commentPublishItems.size();
			}
			@Override
			public Object getItem(int arg0) {
				return arg0;
			}
			@Override
			public long getItemId(int arg0) {
				return arg0;
			}
		}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		commentPublishItems.clear();
	}
}
