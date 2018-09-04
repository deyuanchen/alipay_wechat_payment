package com.wyf.pay.client;

import com.wyf.pay.utils.Constants;
import com.wyf.pay.utils.PayUtil;
import com.wyf.pay.utils.RequestConvertUtils;
import com.wyf.pay.utils.wechat.HttpUtils;
import com.wyf.pay.utils.wechat.XmlUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ivan on 2018/4/10.
 */
public class WeixinPayService {

    /**
     * 统一下单
     * 产生预支付订单
     * @param outTradeNo  商户订单号
     * @param orderAmount   订单金额
     */
    public void unifiedorder(HttpServletRequest request, String outTradeNo, String orderAmount){
        Map<String, String> restmap = null;
        try{
            Map paramters = new HashMap();
            //商品描述
            paramters.put("body", "购买测试");
            paramters.put("spbill_create_ip",PayUtil.getLocalIp(request));
            paramters.put("appid", Constants.WX_APP_ID);
            paramters.put("mch_id", Constants.WX_MCH_ID);
            paramters.put("notify_url", Constants.WX_NOTIFY_RUL);
            //交易类型
            paramters.put("trade_type", "APP");
            paramters.put("nonce_str", PayUtil.getNonceStr());
            paramters.put("out_trade_no", outTradeNo);
            int price100 = new BigDecimal(orderAmount).multiply(new BigDecimal(100)).intValue();
            paramters.put("total_fee", String.valueOf(price100));
            paramters.put("sign", PayUtil.getSign(paramters, Constants.WX_API_SECRET));
            String restxml = HttpUtils.posts(Constants.WX_UNIFIEDORDER_PAY, XmlUtil.xmlFormat(paramters, false));
            restmap = XmlUtil.xmlParse(restxml);
        }catch (Exception e){
            //预订单生成异常
        }

        if (!restmap.isEmpty() && "SUCCESS".equals(restmap.get("result_code"))) {
            //预订单生成成功
        }else {
            //预订单生成失败
        }

    }


    /**
     * 支付订单回调
     */
    public String orderCallback(BufferedReader br){
        Map<String, String> resultXml = new HashMap();
        try {
            String str = RequestConvertUtils.convertBody(br);
            if (StringUtils.isBlank(str)){
                resultXml.put("return_code", "FAIL");
                resultXml.put("return_msg", "接收数据异常");
                return XmlUtil.xmlFormat(resultXml, true);
            }
            //将XML字符串解析成MAP
            Map<String, String> resultMap = XmlUtil.xmlParse(str);
            if (resultMap.isEmpty()) {
                resultXml.put("return_code", "FAIL");
                resultXml.put("return_msg", "接收数据失败");
                return XmlUtil.xmlFormat(resultXml, true);
            }

            if (!"SUCCESS".equals(resultMap.get("result_code"))) {
                resultXml.put("return_code", "FAIL");
                resultXml.put("return_msg", "微信订单处理失败");
                return XmlUtil.xmlFormat(resultXml, true);
            }

            String wxSign = resultMap.get("sign");
            if (StringUtils.isBlank(wxSign)){
                resultXml.put("return_code", "FAIL");
                resultXml.put("return_msg", "签名不存在");
                return XmlUtil.xmlFormat(resultXml, true);
            }

            resultMap.remove("sign");
            String sign = PayUtil.getSign(resultMap, Constants.WX_API_SECRET);
            if (!wxSign.equals(sign)){
                resultXml.put("return_code", "FAIL");
                resultXml.put("return_msg", "签名验证错误");
                return XmlUtil.xmlFormat(resultXml, true);
            }

            //通知微信支付系统接收到信息
            resultXml.put("return_code", "SUCCESS");
            resultXml.put("return_msg", "OK");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //返回微信所需XML数据
        return XmlUtil.xmlFormat(resultXml, true);
    }



    /**
     * 支付订单查询
     * @param outTradeNo    商户订单号
     * @param transactionId 微信订单号
     */
    public static void orderQuery(String outTradeNo, String transactionId){
        Map<String, String> restmap = null;
        try{
            Map<String, String> parm = new HashMap<String, String>();
            parm.put("appid", Constants.WX_APP_ID);
            parm.put("mch_id", Constants.WX_MCH_ID);
            parm.put("out_trade_no", outTradeNo);
            parm.put("out_trade_no", outTradeNo);
            parm.put("nonce_str", PayUtil.getNonceStr());
            parm.put("sign", PayUtil.getSign(parm, Constants.WX_API_SECRET));
            String restxml = HttpUtils.posts(Constants.WX_TRANSFERS_PAY, XmlUtil.xmlFormat(parm, false));
            restmap = XmlUtil.xmlParse(restxml);
        }catch (Exception e){

        }

        if (!restmap.isEmpty() && "SUCCESS".equals(restmap.get("result_code"))) {
            //支付订单查询成功
        }else {
            //支付订单查询失败;
        }
    }



    /**
     * 企业向个人支付转账
     * @param request
     * @param openid 用户openid
     * @param tradeNo  商户订单号
     * @param orderAmount 转账金额
     */
    @RequestMapping(value = "/pay", method = RequestMethod.POST)
    public void transferPay(HttpServletRequest request, String openid, String tradeNo, String orderAmount) {
        Map<String, String> restmap = null;
        try {
            Map<String, String> parm = new HashMap<String, String>();
            parm.put("mch_appid", Constants.WX_APP_ID);
            parm.put("mchid", Constants.WX_MCH_ID);
            parm.put("partner_trade_no", tradeNo);
            parm.put("amount", orderAmount);
            parm.put("openid", openid);

            //随机字符串
            parm.put("nonce_str", PayUtil.getNonceStr());
            //校验用户姓名选项 NO_CHECK 不校验用户真实姓名
            parm.put("check_name", "NO_CHECK");

            //check_name设置为FORCE_CHECK，则必填
            //parm.put("re_user_name", "安迪");

            //企业付款描述信息
            parm.put("desc", "测试转账到个人");
            //Ip地址
            parm.put("spbill_create_ip", PayUtil.getLocalIp(request));
            parm.put("sign", PayUtil.getSign(parm, Constants.WX_API_SECRET));
            String restxml = HttpUtils.posts(Constants.WX_TRANSFERS_PAY, XmlUtil.xmlFormat(parm, false));
            restmap = XmlUtil.xmlParse(restxml);
        } catch (Exception e) {
//            LOG.error(e.getMessage(), e);
        }

        if (!restmap.isEmpty() && "SUCCESS".equals(restmap.get("result_code"))) {
//            LOG.info("转账成功：" + restmap.get("err_code") + ":" + restmap.get("err_code_des"));
            Map<String, String> transferMap = new HashMap<>();
            //商户转账订单号
            transferMap.put("partner_trade_no", restmap.get("partner_trade_no"));
            //微信订单号
            transferMap.put("payment_no", restmap.get("payment_no"));
            //微信支付成功时间
            transferMap.put("payment_time", restmap.get("payment_time"));
            //转账成功;
        }else {
            //转账失败
        }
    }



    /**
     * 企业向个人转账查询
     * @param tradeNo 商户转账订单号
     */
    public void transferQuery(String tradeNo) {
        if (StringUtils.isBlank(tradeNo)) {
            //"转账订单号不能为空"
        }

        Map<String, String> restmap = null;
        try {
            Map<String, String> parm = new HashMap<String, String>();
            parm.put("appid", Constants.WX_APP_ID);
            parm.put("mch_id", Constants.WX_MCH_ID);
            parm.put("partner_trade_no", tradeNo);
            parm.put("nonce_str", PayUtil.getNonceStr());
            parm.put("sign", PayUtil.getSign(parm, Constants.WX_API_SECRET));

            String restxml = HttpUtils.posts(Constants.WX_TRANSFERS_PAY_QUERY, XmlUtil.xmlFormat(parm, true));
            restmap = XmlUtil.xmlParse(restxml);
        } catch (Exception e) {
//            LOG.error(e.getMessage(), e);
        }

        if (!restmap.isEmpty() && "SUCCESS".equals(restmap.get("result_code"))) {
            // 订单查询成功 处理业务逻辑
//            LOG.info("订单查询：订单" + restmap.get("partner_trade_no") + "支付成功");
            Map<String, String> transferMap = new HashMap<>();
            transferMap.put("partner_trade_no", restmap.get("partner_trade_no"));//商户转账订单号
            transferMap.put("openid", restmap.get("openid")); //收款微信号
            transferMap.put("payment_amount", restmap.get("payment_amount")); //转账金额
            transferMap.put("transfer_time", restmap.get("transfer_time")); //转账时间
            transferMap.put("desc", restmap.get("desc")); //转账描述
        }else {
            //处理失败
        }
    }

}
