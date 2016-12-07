package com.chesssystem.fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.chesssystem.MainActivity;
import com.chesssystem.NimApplication;
import com.chesssystem.R;
import com.chesssystem.album.SelectPhotoActivity;
import com.chesssystem.item.GoodsItem;
import com.chesssystem.ui.CollectionActivity;
import com.chesssystem.ui.H5MerchantsJoinActivity;
import com.chesssystem.ui.comment.CommentMyActivity;
import com.chesssystem.ui.comment.CommentPublishActivity;
import com.chesssystem.ui.dazi.DaziActivity;
import com.chesssystem.ui.dazi.MyInviteActivity;
import com.chesssystem.ui.order.OrderActivity;
import com.chesssystem.ui.order.RefundActivity;
import com.chesssystem.ui.order.RefundDetailActivity;
import com.chesssystem.ui.setting.LoginActivity;
import com.chesssystem.ui.setting.NameSettingActivity;
import com.chesssystem.ui.setting.SettingActivity;
import com.chesssystem.util.DateUtil;
import com.chesssystem.util.DoubleToInt;
import com.chesssystem.util.HttpUtl;
import com.chesssystem.util.ImageUtil;
import com.chesssystem.util.PhotoFileUtils;
import com.chesssystem.util.ServerUrl;
import com.chesssystem.util.HttpUtl.HttpCallbackListener;
import com.chesssystem.widget.CircleImageView;
import com.chesssystem.widget.MyDialog;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nostra13.universalimageloader.core.ImageLoader;
/**
 * 
 * @author lyg
 * @time 2016-7-4上午9:49:20
 */
public class MyFragment extends Fragment implements OnClickListener {
	public static MyFragment myFragmentInstance;
	private static LinearLayout llUserInfor;
	private static Button btLogin;
	private static CircleImageView myImage;
	private static TextView tvName;
	private PopupWindow pop = null;
	private LinearLayout ll_popup;
	private RelativeLayout RlParent;
	private Button btCamera;
	private Button btPhoto;
	private Button btCancel;
	private View viewParent;
	private TextView tvPhone;
	private static ImageView ivSex;
	private LinearLayout llMyOrder;
	private LinearLayout llMyRefund;
	private LinearLayout llMyCollection;
	private LinearLayout llMyComment;
	private LinearLayout llMyDazi;
	private LinearLayout llMyRepair;
	private LinearLayout llMySetting;
	private LinearLayout llMyAbout;
	private MyDialog myDialog;
	private String telephone="";
	private TextView tvPopContent;
	private static final int TAKE_PICTURE = 0x000001;
	private static final int SELECT_PHOTO = 0x000002;
	public static MyFragment getInstance() {
		myFragmentInstance = new MyFragment();
		return myFragmentInstance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		viewParent = inflater.inflate(R.layout.fragment_my, container, false);
		initView(viewParent);
		getServiceTel();
		return viewParent;
	}

	private void initView(View view) {
		llUserInfor = (LinearLayout) view.findViewById(R.id.ll_userinfor);
		btLogin = (Button) view.findViewById(R.id.bt_login);
		btLogin.setOnClickListener(this);
		myImage = (CircleImageView) view.findViewById(R.id.my_faces);
		myImage.setOnClickListener(this);
		ivSex=(ImageView) view.findViewById(R.id.iv_my_sex);
		tvName = (TextView) view.findViewById(R.id.tv_my_name);
		tvPhone=(TextView) view.findViewById(R.id.tv_my_phone);
		tvPhone.setOnClickListener(this);
		llMyOrder=(LinearLayout) view.findViewById(R.id.ll_my_order);
		llMyRefund=(LinearLayout) view.findViewById(R.id.ll_my_refund);
		llMyCollection=(LinearLayout) view.findViewById(R.id.ll_my_collection);
		llMyComment=(LinearLayout) view.findViewById(R.id.ll_my_comment);
		llMyDazi=(LinearLayout) view.findViewById(R.id.ll_my_dazi);
		llMyRepair=(LinearLayout) view.findViewById(R.id.ll_my_repair);
		llMySetting=(LinearLayout) view.findViewById(R.id.ll_my_setting);
		llMyAbout=(LinearLayout) view.findViewById(R.id.ll_my_about);
		llMyOrder.setOnClickListener(this);
		llMyRefund.setOnClickListener(this);
		llMyCollection.setOnClickListener(this);
		llMyComment.setOnClickListener(this);
		llMyDazi.setOnClickListener(this);
		llMyRepair.setOnClickListener(this);
		llMySetting.setOnClickListener(this);
		llMyAbout.setOnClickListener(this);
		/*
		 * popupwindow
		 */
		pop = new PopupWindow(getActivity());
		View popView = getActivity().getLayoutInflater().inflate(
				R.layout.pop_photo, null);
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

	public static void initInfor() {
		if (NimApplication.LOGINED) {
			llUserInfor.setVisibility(View.VISIBLE);
			btLogin.setVisibility(View.GONE);
			/*
			 *  头像
			 */
			String tag = (String) myImage.getTag(R.id.imageloader_uri);
			if(!NimApplication.sharedPreferences.getString("userPic", "").equals(tag)){
				Glide.with(myFragmentInstance)
				.load(NimApplication.sharedPreferences.getString("userPic", ""))
				.placeholder(R.drawable.avatar_default)
				.error(R.drawable.avatar_default)
				.skipMemoryCache( true )
				.dontAnimate()
				.centerCrop()
				.into(myImage);
				myImage.setTag(R.id.imageloader_uri,NimApplication.sharedPreferences.getString("userPic", ""));
			}
			tvName.setText(NimApplication.sharedPreferences.getString("nick",
					""));
			if(NimApplication.sharedPreferences.getInt("sex",0)==0){
				ivSex.setImageResource(R.drawable.girl);
			}else{
				ivSex.setImageResource(R.drawable.boy);
			}
		} else {
			llUserInfor.setVisibility(View.GONE);
			btLogin.setVisibility(View.VISIBLE);
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
	public void onClick(View view) {
		switch (view.getId()) {
			//点击登录
		case R.id.bt_login:
			startActivity(new Intent(getActivity(), LoginActivity.class));
			break;
			//点击头像
		case R.id.my_faces:
			ll_popup.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.activity_translate_in));
			pop.showAtLocation(viewParent, Gravity.BOTTOM, 0, 0);
			break;
			//点击其他位置
		case R.id.parent:
			pop.dismiss();
			ll_popup.clearAnimation();
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
			Intent selectPhotoIntent=new Intent(getActivity(), SelectPhotoActivity.class);
			selectPhotoIntent.putExtra("picNumber", 1);
			startActivityForResult(selectPhotoIntent,SELECT_PHOTO);
			break;
			//点击取消
		case R.id.item_popupwindows_cancel:
			pop.dismiss();
			ll_popup.clearAnimation();
			break;
			//点击客服
		case R.id.tv_my_phone:
			if(!telephone.equals("")){
			Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+telephone));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent); 
			}else{
				popDialog("暂无客服联系电话~",1);
			}
			break;
			//点击我的订单
		case R.id.ll_my_order:
			if(NimApplication.LOGINED){
			startActivity(new Intent(getActivity(),OrderActivity.class));
			}else{
				popDialog("您还没有登录，请登录后再查看我的订单",0);
			}
			break;
			//点击我的退款
		case R.id.ll_my_refund:
			if(NimApplication.LOGINED){
				startActivity(new Intent(getActivity(),RefundActivity.class));
				}else{
					popDialog("您还没有登录，请登录后再查看我的退款",0);
				}
			break;
			//点击我的收藏
		case R.id.ll_my_collection:
			if(NimApplication.LOGINED){
				startActivity(new Intent(getActivity(),CollectionActivity.class));
				}else{
					popDialog("您还没有登录，请登录后再查看我的收藏",0);
				}
			break;
			//点击我的评价
		case R.id.ll_my_comment:
			if(NimApplication.LOGINED){
				startActivity(new Intent(getActivity(),CommentMyActivity.class));
				}else{
					popDialog("您还没有登录，请登录后再查看我的评价",0);
				}
			break;
			//点击我的搭子
		case R.id.ll_my_dazi:
			if(NimApplication.LOGINED){
				Intent intentInvites=new Intent(MainActivity.instance, MyInviteActivity.class);
				intentInvites.putExtra("stype", 0);
				startActivity(intentInvites);
				}else{
					popDialog("您还没有登录，请登录后再查看我的搭子",0);
				}
			break;
			//点击维修
		case R.id.ll_my_repair:
			break;
			//点击设置
		case R.id.ll_my_setting:
			if(NimApplication.LOGINED){
				startActivity(new Intent(getActivity(),SettingActivity.class));
				}else{
					popDialog("您还没有登录，请登录后再查看设置",0);
				}
			
			break;
			//点击商户加盟
		case R.id.ll_my_about:
			Intent intentWeb=new Intent(getActivity(),H5MerchantsJoinActivity.class);
			intentWeb.putExtra("url", "http://114.55.118.199/auth/login");
			startActivity(intentWeb);
			break;
		default:
			break;
		}
	}
	/*
	 * 从不可见变为可见状态，更新数据
	 */
	@Override
	public void onResume() {
		super.onResume();
		initInfor();
	}
	private ProgressDialog progressDialog;
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PICTURE:
			if (resultCode == getActivity().RESULT_OK) {
				if (progressDialog == null) {
					progressDialog = new ProgressDialog(getActivity());
					progressDialog.setCanceledOnTouchOutside(false);
				}
				progressDialog.show();
				Bitmap loacalBitmap = ImageUtil.getLoacalBitmap(photoUrl);
				File file = ImageUtil.BitmapToFile(loacalBitmap);
			    savaPic(file);
			}
			break;
		case SELECT_PHOTO:
			if (resultCode == 0) {
				if (data != null){
//					/*
//					 * 对相册图片进行压缩后存为file文件上传
//					 */
//					Bitmap loacalBitmap = ImageUtil.getLoacalBitmap(data.getStringArrayListExtra("photo").get(0));
//					File file = ImageUtil.BitmapToFile(loacalBitmap);
					File file = new File(data.getStringArrayListExtra("photo").get(0));
					savaPic(file);
				}
			}
			break;
		}
	}
	public void savaPic(File file){
		if (file.exists()) {
			if (progressDialog == null) {
				progressDialog = new ProgressDialog(getActivity());
				progressDialog.setCanceledOnTouchOutside(false);
			}
			progressDialog.show();
			HttpUtils http = new HttpUtils();
			RequestParams params = new RequestParams();
			params.addBodyParameter("image", file);
			http.send(HttpMethod.POST,ServerUrl.postPicUrl, params,
					new RequestCallBack<String>() {
						@Override
						public void onFailure(HttpException arg0, String arg1) {
							if (progressDialog != null) {
	     						progressDialog.dismiss();
	     					}
							Toast.makeText(MainActivity.instance,"保存失败", Toast.LENGTH_SHORT).show();
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
									modifyPic(jsonObject.getString("data"));
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					});
			

		} 
	}
	/**
	 * 获取客服电话
	 */
	private void getServiceTel(){
		HttpUtl.getHttp(ServerUrl.telephoneUrl ,
				getActivity(), new HttpCallbackListener() {
					@Override
					public void onFinish(JSONObject response) {
						try {
							telephone=response.getString("data");
							tvPhone.setText("客服 : "+telephone);
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
	 * 修改个人信息
	 */
	private void modifyPic(final String userpic) {
	StringRequest stringRequest = new StringRequest(Method.POST,ServerUrl.postModifyInforUrl,  
            new Response.Listener<String>() {  
                @Override  
                public void onResponse(String response) { 
                	try {
                		if (progressDialog != null) {
     						progressDialog.dismiss();
     					}
						JSONObject jsonObject = new JSONObject(response);
						if(jsonObject.getInt("retCode")==0){
							Editor editor = NimApplication.sharedPreferences.edit();
							editor.putString("userPic",ServerUrl.getPicUrl
									+ userpic).commit();
							Toast.makeText(MainActivity.instance, "保存成功", Toast.LENGTH_SHORT).show();
							initInfor();
						}else {
							Toast.makeText(MainActivity.instance,jsonObject.getString("retMsg"), Toast.LENGTH_SHORT).show();
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
					Toast.makeText(MainActivity.instance, error+"", Toast.LENGTH_SHORT).show();
                }  
            }){
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userId",NimApplication.sharedPreferences.getString("userId", ""));
				map.put("userpic", userpic);
				return map;
			}
	}; 
	NimApplication.getHttpQueues().add(stringRequest);
	}
	public void popDialog(String content,final int type){
		myDialog = new MyDialog(getActivity(),
				R.style.inputDialog);
		tvPopContent=(TextView) myDialog.getContent();
		tvPopContent.setText(content);
		if(type==1){
			myDialog.getCancel().setVisibility(View.GONE);
		}
		myDialog.setOnPositiveListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(type==0){
					startActivity(new Intent(getActivity(), LoginActivity.class));
				}
				myDialog.dismiss();
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
}
