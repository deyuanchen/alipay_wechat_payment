package com.wyf.pay.client;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.wyf.pay.utils.Constants;
import com.wyf.pay.utils.RequestConvertUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ivan on 2018/4/9.
 * 支付宝支付
 */
public class AlipayService {


    /**
     * 支付订单生成
     * @param outTradeNo  商户订单号
     * @param orderAmount   订单金额
     * @param passbackParams 公共回传参数
     * @return 预订单信息，可直接给客户端请求
     */
    public static String generateTradeOrder(String outTradeNo, String orderAmount, String passbackParams){
        try {
            //实例化客户端
            AlipayClient alipayClient = new DefaultAlipayClient(Constants.ALIPAY_SERVER_URL, Constants.ALIPAY_APP_ID, Constants.ALIPAY_PRIVATE_KEY, "json", "utf-8", Constants.ALIPAY_PUBLIC_KEY, "RSA2");
            //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
            AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
            //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
            AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
            //公用回传参数
            model.setPassbackParams(passbackParams);
            //商品的标题/交易标题/订单标题/订单关键字等。
            model.setSubject("");
            //该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天
            model.setTimeoutExpress("30m");

            model.setOutTradeNo(outTradeNo);
            model.setTotalAmount(orderAmount);
            model.setProductCode("QUICK_MSECURITY_PAY");
            request.setBizModel(model);
            request.setNotifyUrl(Constants.APLIPAY_BACK_URL);
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            //就是orderString 可以直接给客户端请求，无需再做处理。
            return response.getBody();
        } catch (AlipayApiException e) {
            //订单创建异常,异常处理逻辑
        }
        return null;
    }



    /**
     * 支付宝支付订单回调
     * @param request
     * @return 订单信息
     */
    public static Map orderCallback(HttpServletRequest request){
        try{
//            Map requestParams = request.getParameterMap();
//            for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
//                String name = (String) iter.next();
//                String[] values = (String[]) requestParams.get(name);
//                String valueStr = "";
//                for (int i = 0; i < values.length; i++) {
//                    valueStr = (i == values.length - 1) ? valueStr + values[i]
//                            : valueStr + values[i] + ",";
//                }
//                //乱码解决，这段代码在出现乱码时使用。
//                //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
//                params.put(name, valueStr);
//            }
            // TODO: 2018/4/9
//            logger.info("支付宝订单回调参数：" + JSON.toJSONString(params));

            //获取支付宝POST过来反馈信息
            Map<String,String> params = RequestConvertUtils.convertParseUrl(request);
            boolean flag = AlipaySignature.rsaCheckV1(params, Constants.ALIPAY_PUBLIC_KEY, "utf-8","RSA2");
            if (flag){
                return params;
            }
        }catch (Exception e){
//            logger.info("支付宝订单回调解析异常", e);
            return null;
        }
        return null;
    }



    /**
     * 支付交易订单查询
     * @param outTradeNo 商户订单号
     * @param tradeNo 支付宝交易号
     */
    public static void tradeQuery(String outTradeNo, String tradeNo){
        if (StringUtils.isBlank(outTradeNo) && StringUtils.isBlank(tradeNo)){
            //订单查询 商户订单号和支付宝交易号不能同时为空
        }
        try{
            AlipayClient alipayClient = new DefaultAlipayClient(Constants.ALIPAY_SERVER_URL, Constants.ALIPAY_APP_ID, Constants.ALIPAY_PRIVATE_KEY,"json","utf-8",Constants.ALIPAY_PUBLIC_KEY,"RSA2");
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            Map bizContent = new HashMap();
            if (StringUtils.isNotBlank(outTradeNo)){
                bizContent.put("out_trade_no", outTradeNo);
            }
            if (StringUtils.isNotBlank(tradeNo)){
                bizContent.put("trade_no", tradeNo);
            }
            request.setBizContent(JSON.toJSONString(bizContent));
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if(response.isSuccess()){
                System.out.println("调用成功");
            } else {
                System.out.println("调用失败");
            }
        }catch (Exception e){
            //订单查询异常
        }
    }



    /**
     * 交易关闭
     * @param outTradeNo 商户订单号
     * @param tradeNo 支付宝交易号
     */
    public static void tradeClose(String outTradeNo, String tradeNo){
        if (StringUtils.isBlank(outTradeNo) && StringUtils.isBlank(tradeNo)){
            //订单查询 商户订单号和支付宝交易号不能同时为空
        }
        try{
            AlipayClient alipayClient = new DefaultAlipayClient(Constants.ALIPAY_SERVER_URL, Constants.ALIPAY_APP_ID, Constants.ALIPAY_PRIVATE_KEY,"json","utf-8",Constants.ALIPAY_PUBLIC_KEY,"RSA2");
            AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
            Map bizContent = new HashMap();
            if (StringUtils.isNotBlank(outTradeNo)){
                bizContent.put("out_trade_no", outTradeNo);
            }
            if (StringUtils.isNotBlank(tradeNo)){
                bizContent.put("trade_no", tradeNo);
            }
            request.setBizContent(JSON.toJSONString(bizContent));
            AlipayTradeCloseResponse response = alipayClient.execute(request);
            if(response.isSuccess()){
                System.out.println("调用成功");
            } else {
                System.out.println("调用失败");
            }
        }catch (Exception e){
            //交易关闭异常
        }

    }



    /**
     * 交易退款
     * @param outTradeNo 商户订单号
     * @param tradeNo   支付宝订单号
     * @param refundAmount 退款金额
     * @param outRequestNo 退款流水号
     */
    public static void tradeRefund(String outTradeNo, String tradeNo, String refundAmount, String outRequestNo){
        try{
            AlipayClient alipayClient = new DefaultAlipayClient(Constants.ALIPAY_SERVER_URL, Constants.ALIPAY_APP_ID, Constants.ALIPAY_PRIVATE_KEY,"json","utf-8",Constants.ALIPAY_PUBLIC_KEY,"RSA2");
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
            Map bizContent = new HashMap();
            if (StringUtils.isNotBlank(outTradeNo)){
                bizContent.put("out_trade_no", outTradeNo);
            }
            if (StringUtils.isNotBlank(tradeNo)){
                bizContent.put("trade_no", tradeNo);
            }
            bizContent.put("refund_amount", refundAmount);

            if (StringUtils.isNotBlank(outRequestNo)){
                //标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传。
                bizContent.put("out_request_no", outRequestNo);
            }
            request.setBizContent(JSON.toJSONString(bizContent));
            AlipayTradeRefundResponse response = alipayClient.execute(request);
            if(response.isSuccess()){
                System.out.println("调用成功");
                if ("10000".equals(response.getCode())){
                    System.out.println("退款成功");
                }
            } else {
                System.out.println("调用失败");
            }
        }catch (Exception e){

        }
    }


    /**
     * 交易退款查询
     * @param outTradeNo 商户订单号
     * @param tradeNo   支付宝订单号
     * @param outRequestNo 退款流水号
     */
    public void refundQuery(String outTradeNo, String tradeNo, String outRequestNo){
        try{
            AlipayClient alipayClient = new DefaultAlipayClient(Constants.ALIPAY_SERVER_URL, Constants.ALIPAY_APP_ID, Constants.ALIPAY_PRIVATE_KEY,"json","utf-8",Constants.ALIPAY_PUBLIC_KEY,"RSA2");
            AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
            Map bizContent = new HashMap();
            if (StringUtils.isNotBlank(outTradeNo)){
                bizContent.put("out_trade_no", outTradeNo);
            }
            if (StringUtils.isNotBlank(tradeNo)){
                bizContent.put("trade_no", tradeNo);
            }

            //标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传。
            bizContent.put("out_request_no", outRequestNo);
            request.setBizContent(JSON.toJSONString(bizContent));
            AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(request);
            if(response.isSuccess()){
                System.out.println("调用成功");
            } else {
                System.out.println("调用失败");
            }
        }catch (Exception e){
            System.out.println("调用异常");
        }
    }



    /**
     *支付宝转账
     * @param payeeAccount   收款方账户
     * @param orderAmount    转账金额，单位：元。
     * @param payeeRealName   收款方真实姓名
     * @param outBizNo  商户转账唯一订单号
     * @return
     */
    public static boolean aliPayTransfer(String payeeAccount, String orderAmount, String payeeRealName, String outBizNo) {
        Map bizContent = new HashMap();
        try {
            bizContent.put("out_biz_no", outBizNo);
            bizContent.put("payee_account", payeeAccount);
            bizContent.put("amount", orderAmount);
            //收款方账户类型 ALIPAY_LOGONID：支付宝登录号，支持邮箱和手机号格式。
            bizContent.put("payee_type", "ALIPAY_LOGONID");
            if(StringUtils.isNotBlank(payeeRealName)){
                //收款方姓名，输入则强校验
                bizContent.put("payee_real_name", payeeRealName);
            }
            //付款方姓名
            bizContent.put("payer_show_name", "");
            //转账备注
            bizContent.put("remark", "");

            AlipayClient alipayClient = new DefaultAlipayClient(Constants.ALIPAY_SERVER_URL, Constants.ALIPAY_APP_ID, Constants.ALIPAY_PRIVATE_KEY,"json","utf-8",Constants.ALIPAY_PUBLIC_KEY,"RSA2");
            AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
            request.setBizContent(JSON.toJSONString(bizContent));
            bizContent.put("payState", "1");
            AlipayFundTransToaccountTransferResponse response = alipayClient.execute(request);
            if(response.isSuccess()){
                if ("10000".equals(response.getCode()) && StringUtils.isNotBlank(response.getPayDate())){
                    String payDate = response.getPayDate();
                    bizContent.put("pay_state", "2");
                    Map result = new HashMap();
                    result.put("outBizNo", outBizNo);
                    result.put("orderId", response.getOrderId());
                    result.put("pay_date", payDate);
                    //转账成功处理逻辑
                }else {
                    //转账失败处理逻辑
                }
            } else {
                //转账失败处理逻辑
                bizContent.put("pay_state", "0");
                bizContent.put("err_message", response.getSubMsg());
            }
            bizContent.put("order_id", response.getOrderId());
        }catch (Exception e){
            //转账异常处理逻辑
        }
        return false;
    }



    /**
     * 转账订单查询
     * @param outBizNo  商户转账唯一订单号
     * @param orderId   支付宝转账单据号
     */
    public void transOrderQuery(String outBizNo, String orderId){
        if (StringUtils.isBlank(outBizNo) && StringUtils.isBlank(orderId)){
            //订单查询 商户订单号和支付宝交易号不能同时为空
        }
        try{
            Map bizContent = new HashMap();
            if (StringUtils.isNotBlank(outBizNo)){
                bizContent.put("out_biz_no", outBizNo);
            }
            if (StringUtils.isNotBlank(orderId)){
                bizContent.put("order_id", orderId);
            }

            AlipayClient alipayClient = new DefaultAlipayClient(Constants.ALIPAY_SERVER_URL, Constants.ALIPAY_APP_ID, Constants.ALIPAY_PRIVATE_KEY,"json","utf-8",Constants.ALIPAY_PUBLIC_KEY,"RSA2");
            AlipayFundTransOrderQueryRequest request = new AlipayFundTransOrderQueryRequest();
            request.setBizContent(JSON.toJSONString(bizContent));
            AlipayFundTransOrderQueryResponse response = alipayClient.execute(request);
            if(response.isSuccess()){
                //调用成功，业务处理逻辑
            } else {
                //调用失败
            }
        }catch (Exception e){
            //查询异常处理逻辑
        }
    }
}
