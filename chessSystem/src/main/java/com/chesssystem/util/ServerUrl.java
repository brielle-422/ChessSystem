package com.chesssystem.util;

import com.chesssystem.NimApplication;

/**
 * 接口地址
 * @author lyg
 * @time 2016-7-4上午9:52:32
 */
public class ServerUrl {
	/*
	 * 服务器地址
	 */
	public final static String SERVERURL="http://114.55.118.199";
	/*
	 * 获取客服电话
	 */
	public final static String telephoneUrl=SERVERURL+"/store/getAppTel";
	/*
	 * 登录接口
	 */
	public final static String loginUrl=SERVERURL+"/app/login";
	/*
	 * 获取商店列表
	 */
	public final static String getStoreListUrl=SERVERURL+"/store/getStoreList?lat="+NimApplication.lat+
			"&lng="+NimApplication.lng;
	/*
	 * 获取商店详情
	 */
	public final static String getStoreDetailUrl=SERVERURL+"/store/getStore";
	/*
	 * 获取店铺收藏列表
	 */
	public final static String getStoreCollectionListUrl=SERVERURL+"/collect/getMyCollect?lat="+NimApplication.lat+
			"&lng="+NimApplication.lng;
	/*
	 * 获取店铺所有评论列表
	 */
	public final static String getCommentAllUrl=SERVERURL+"/rate/getRateList";
	/*
	 * 收藏店铺
	 */
	public final static String postCollectedUrl=SERVERURL+"/collect/collectStore";
	/*
	 * 取消收藏店铺
	 */
	public final static String postDeleteCollectionUrl=SERVERURL+"/collect/deleteCollect";
	/*
	 * 获取图片
	 */
	public final static String getPicUrl=SERVERURL+"/picture/getPic.do?picId=";
	/*
	 * 上传图片
	 */
	public final static String postPicUrl=SERVERURL+"/picture/savePic";
	/*
	 * 上传评论图片(多图)
	 */
	public final static String postRatePicUrl=SERVERURL+"/rate/addRatePic";
	/*
	 * 获取商品列表
	 */
	public final static String getGoodsListUrl=SERVERURL+"/goods/appmgr";
	/*
	 * 获取商品详情
	 */
	public final static String getGoodsDetailUrl=SERVERURL+"/goods/getGoodsDetail";
	/*
	 * 获取棋牌室列表
	 */
	public final static String getRoomListUrl=SERVERURL+"/room/getRoomList";
	/*
	 * 获取订单列表
	 */
	public final static String getOrderListUrl=SERVERURL+"/order/getOrderList";
	/*
	 * 获取订单详情
	 */
	public final static String getOrderDetailUrl=SERVERURL+"/order/getOrderDetail";
	/*
	 * 创建订单
	 */
	public final static String postAddOrderUrl=SERVERURL+"/order/addOrder";
	/*
	 * 删除订单
	 */
	public final static String deleteOrderUrl=SERVERURL+"/order/deleteOrder";
	/*
	 * 申请退款
	 */
	public final static String refundUrl=SERVERURL+"/order/refound";
	/*
	 * 获取广告数据
	 */
	public final static String getBannerMsgUrl=SERVERURL+"/ad/getAdList";
	/*
	 * 获取用户搭子信息
	 */
	public final static String getUserDaziInforUrl=SERVERURL+"/lug/getUserLugInfo";
	/*
	 * 获取全部搭子列表
	 */
	public final static String getDaziListUrl=SERVERURL+"/lug/getLugList";
	/*
	 * 获取我的邀约、我的应约列表
	 */
	public final static String getInvitesListUrl=SERVERURL+"/lug/getMyLugList";
	/*
	 * 获取我的搭子记录约列表
	 */
	public final static String getRecordListUrl=SERVERURL+"/lug/getLugRecord";
	/*
	 * 评论搭主信息
	 */
	public final static String postRateDaziUserUrl=SERVERURL+"/lug/rateLug";
	/*
	 * 获取搭子详情信息
	 */
	public final static String getDaziDetailUrl=SERVERURL+"/lug/getLugDetail";
	/*
	 * 应约搭子
	 */
	public final static String postApplyUrl=SERVERURL+"/lug/applyLug";
	/*
	 * 审核搭子成员
	 */
	public final static String getCheckMember=SERVERURL+"/lug/checkLugMember";
	/*
	 * 创建搭子
	 */
	public final static String postCreateDaziUrl=SERVERURL+"/lug/createLug";
	/*
	 * 修改用户个人信息
	 */
	public final static String postModifyInforUrl=SERVERURL+"/app/modifyUser";
	/*
	 * 获取我的评论列表
	 */
	public final static String getMyCommentListUrl=SERVERURL+"/rate/myRateList";
	/*
	 * 对订单进行评论
	 */
	public final static String addOrderCommentUrl=SERVERURL+"/rate/addRate";
	/*
	 * 发送短信验证码
	 */
	public final static String getMessageCodeUrl=SERVERURL+"/app/sendMsg";
	/*
	 * 注册
	 */
	public final static String registerUrl=SERVERURL+"/app/register";
	/*
	 * 修改密码
	 */
	public final static String modifyPwdUrl=SERVERURL+"/app/modifyPwd";
	/*
	 * 支付宝回调接口
	 */
	public final static String alipayBackUrl=SERVERURL+"/Alipay/result";
	/*
	 * 微信支付预订单接口
	 */
	public final static String weixinPayUrl=SERVERURL+"/WXAlipay/weixin";
}
