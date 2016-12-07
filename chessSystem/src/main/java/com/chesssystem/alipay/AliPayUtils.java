package com.chesssystem.alipay;

import com.chesssystem.NimApplication;
import com.chesssystem.util.ServerUrl;

/**
 * 支付宝支付
 * @author lyg
 * @time 2016-7-25上午11:16:05
 */
public class AliPayUtils {
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
	public static final int SDK_PAY_FLAG = 1;
	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	public static String getOrderInfo(String subject, String body, String price,String orderNumber) {

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
		orderInfo += "&notify_url=" + "\"" + ServerUrl.alipayBackUrl + "\"";

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
}
