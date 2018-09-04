package com.wyf.pay.utils;

/**
 * Created by wuyifei on 2018/4/10.
 */
public class Constants {

    //支付宝serverUrl
    public static final String ALIPAY_SERVER_URL = "https://openapi.alipay.com/gateway.do";
    //支付宝分配给开发者的应用ID
    public static final String ALIPAY_APP_ID = "";
    //支付宝开发者私钥
    public static final String ALIPAY_PRIVATE_KEY = "";
    //支付宝公钥
    public static final String ALIPAY_PUBLIC_KEY = "";
    //支付订单回调地址
    public static final String APLIPAY_BACK_URL = "";


    //微信APP支付
    public static final String WX_UNIFIEDORDER_PAY = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    //微信企业转账url
    public static final String WX_TRANSFERS_PAY = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";

    //微信查询订单url
    public static final String WX_TRANSFERS_PAY_QUERY = "https://api.mch.weixin.qq.com/mmpaymkttransfers/gettransferinfo"; // 企业付款查询

    //微信分配的账号ID（企业号corpid即为此appId）
    public static final String WX_APP_ID = "wx2440bee27b45a19a";

    //微信支付分配的商户号
    public static final String WX_MCH_ID = "1501713741";

    //微信API密钥
    public static final String WX_API_SECRET = "D45C88355F8244EC8D9B6B3829D8CFF3";

    //微信APP支付回调地址
    public static final String WX_NOTIFY_RUL = "http://www.baidu.com";

    //证书密码
//    public static final String WX_KEY_STORE_PASSWORD = "1501713741";


}
