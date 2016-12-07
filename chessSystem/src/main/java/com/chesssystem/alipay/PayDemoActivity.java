package com.chesssystem.alipay;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import com.alipay.sdk.app.PayTask;
import com.chesssystem.R;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class PayDemoActivity extends FragmentActivity {

	//商户PID(合作身份者id，以2088开头的16位纯数字，这个你申请支付宝签约成功后就会看见)
	public static final String PARTNER = "2088421417493238";
	//商户收款账号(这里填写收款支付宝账号，即你付款后到账的支付宝账号)
	public static final String SELLER = "3495571692@qq.com";
	//商户私钥，pkcs8格式
	//(商户私钥，自助生成，即rsa_private_key.pem中去掉首行，最后一行，空格和换行最后拼成一行的字符串，
	//rsa_private_key.pem这个文件等你申请支付宝签约成功后，按照文档说明你会生成的,如果android版本太高，这里要用PKCS8格式用户私钥，不然调用不会成功的，那个格式你到时候会生成的，表急。)
	public static final String RSA_PRIVATE="MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBALMyzu0kh9Ts5kc3MBiqTSO/sP4nx8I8mQ6eTH/OIt8S6dDFAc5kJ5B187wxihFoYchA/n9uJG2jEJ39GOWGg84ZFC/9cVqEXM4HyIb1E7HtUK8TWnvmqO6ypotaO7SvV/cMs/94imgoR+WfAx+LEpDGmUAt8zeLK8vmq4dkksPTAgMBAAECgYBp8Y35EfHkZrK2Q+CBlJ3KltgzSp2FLoJm7BvaK6reK7ZkoTSyKvBhLfsNtlQkeovk8tgDysBquU9nuGzxthdR0jgukDgM3pW/WciKKrvJFK49ckfeL/+1+Y/nycINnW5gcfzU4Rn7zZYVm42TlQH+vZe399taibYOBjum1PJpAQJBAONbuyc64SShMvRdLcMAkylJQTUm7JQiq4N6rkePCj3i2DIxisaQ7mpnQ+aParZLoyTA8UYE8SAA1dV6wjWUAUECQQDJxe4FrKRh19GTGKdjpEe2s0emHILV9EjbpQ5PwHpmRz3MiJ8FXTKZU9osnFvyuS/kKhFbUn1NvFioKmg6/6wTAkB4WiKkDaMIxNw4RurmgvNjs4d1H3m0oPWxz8tZzfpZ8C8Jwvf3TNkoMinbbqfKgBeIaSpPKmaJzmdjbmxH+GUBAj8l1PbMGZK4xb6F1hmMlUDdAMBuZOojM6p1hH0qdux8QN9VekhWuSLDnPVLaAXVfON95GVcuhrEeIwm+8yPQ2ECQQCZJmDukEGe6SNjtHIVCEuhTIKpzygEIdtF4nsgWdlfQIjvxgYHGA7KJuDkx3WS9jf4866ndM0qpuZjX650+pnl";
	//支付宝公钥(支付宝（RSA）公钥,用签约支付宝账号登录ms.alipay.com后，在密钥管理页面获取；或者文档上也有。  )
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCzMs7tJIfU7OZHNzAYqk0jv7D+J8fCPJkOnkx/ziLfEunQxQHOZCeQdfO8MYoRaGHIQP5/biRtoxCd/RjlhoPOGRQv/XFahFzOB8iG9ROx7VCvE1p75qjusqaLWju0r1f3DLP/eIpoKEflnwMfixKQxplALfM3iyvL5quHZJLD0wIDAQAB";
	private static final int SDK_PAY_FLAG = 1;
	private String orderNumber;
	private String price;
	private TextView tvGoodsName;
	private TextView tvPrice;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unused")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);
				/**
				 * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
				 * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
				 * docType=1) 建议商户依赖异步通知
				 */
				String resultInfo = payResult.getResult();// 同步返回需要验证的信息

				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					Toast.makeText(PayDemoActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
				} else {
					// 判断resultStatus 为非"9000"则代表可能支付失败
					// "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(PayDemoActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(PayDemoActivity.this, "支付失败", Toast.LENGTH_SHORT).show();

					}
				}
				break;
			}
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pay_main);
		initView();
	}

	private void initView() {
		orderNumber=getIntent().getStringExtra("orderNumber");
		price=getIntent().getStringExtra("price");
		tvGoodsName=(TextView) findViewById(R.id.product_subject);
		tvGoodsName.setText("订单-"+orderNumber);
		tvPrice=(TextView) findViewById(R.id.product_price);
		tvPrice.setText(price+"元");
	}

	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void pay(View v) {
		if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE) || TextUtils.isEmpty(SELLER)) {
			new AlertDialog.Builder(this).setTitle("警告").setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int i) {
							//
							finish();
						}
					}).show();
			return;
		}
		String orderInfo = getOrderInfo("订单-"+orderNumber, "该测试商品的详细描述", price);

		/**
		 * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
		 */
		String sign = sign(orderInfo);
		try {
			/**
			 * 仅需对sign 做URL编码
			 */
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		/**
		 * 完整的符合支付宝参数规范的订单信息
		 */
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(PayDemoActivity.this);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo, true);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(this);
		String version = payTask.getVersion();
		Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 原生的H5（手机网页版支付切natvie支付） 【对应页面网页支付按钮】
	 * 
	 * @param v
	 */
	public void h5Pay(View v) {
		Intent intent = new Intent(this, H5PayDemoActivity.class);
		Bundle extras = new Bundle();
		/**
		 * url是测试的网站，在app内部打开页面是基于webview打开的，demo中的webview是H5PayDemoActivity，
		 * demo中拦截url进行支付的逻辑是在H5PayDemoActivity中shouldOverrideUrlLoading方法实现，
		 * 商户可以根据自己的需求来实现
		 */
		String url = "http://m.taobao.com";
		// url可以是一号店或者淘宝等第三方的购物wap站点，在该网站的支付过程中，支付宝sdk完成拦截支付
		extras.putString("url", url);
		intent.putExtras(extras);
		startActivity(intent);
	}

	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	private String getOrderInfo(String subject, String body, String price) {

		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + orderNumber + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + "http://114.55.118.199/chess/Alipay/result" + "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	private String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	private String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	private String getSignType() {
		return "sign_type=\"RSA\"";
	}

}
