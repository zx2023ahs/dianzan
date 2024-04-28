package cn.rh.flash.sdk.paymentChannel.WalletPay;

import cn.rh.flash.bean.entity.dzsys.PaymentChannel;
import cn.rh.flash.sdk.paymentChannel.WalletPay.WalletPayBean.*;
import cn.rh.flash.utils.HttpUtil;
import cn.rh.flash.utils.JsonUtil;
import cn.rh.flash.utils.StringUtil;
import com.aliyun.oss.common.auth.HmacSHA256Signature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;


@Log4j2
public class WalletPayUtil {


    private static OkHttpClient client = new OkHttpClient();

    private static final String RECHARGE_URL = "/api/order/create";

    private static final String WITHDRAW_URL = "/api/withdrawal/create";

    private static final String RECHARGE_NOTIFY_URL = "/api/pay/wallet/notifyRechargeOrder";

    private static final String RECHARGE_NOTIFY_VIP_URL = "/api/pay/wallet/notifyRechargeOrderVIP";

    private static final String WITHDRAW_NOTIFY_URL = "/api/pay/wallet/notifyWithdrawOrder";

    private static final String BASE_URL = HttpUtil.getBtServerName();
    private static final  ObjectMapper objectMapper  = new ObjectMapper();


//    public static void main1(String[] args) throws Exception {
//        String userid = "1711295983490180";
//        String key = "8R9ZfsFee04XPg1bhSOyaXk5qVnhfDZ6";
//        String payUrl = "http://1.117.2.84:4551" + RECHARGE_NOTIFY_URL;
//
//        String url =  "https://api.aa.im/api/order/create";
//        RechargeParam rechargeParam = new RechargeParam();
//        rechargeParam.setUser_id(userid);
//        rechargeParam.setCurrency_money("50.00");
//        rechargeParam.setCurrency_code("USD");
//        rechargeParam.setCoin_code("USDT.TRC20");
//        rechargeParam.setLanguage(1);
//        rechargeParam.setAsyn_notice_url(payUrl);
//        rechargeParam.setSync_jump_url("");
//        rechargeParam.setUser_order_id("cs001047");
//        rechargeParam.setUser_custom_id("001");
//        rechargeParam.setRemark("测试");
//
//        String timesamp = System.currentTimeMillis()/1000+"";
//        String data = timesamp + userid + "POST" + RECHARGE_URL + rechargeParam.toJson();
//
//        HmacSHA256Signature sha256 = new HmacSHA256Signature();
//        String sign = sha256.computeSignature(key, data);
//
//        sign = sign.replaceAll("/", "_").replaceAll("\\+", "-");
//
//        MediaType mediaType = MediaType.parse("application/json;charset=UTF-8");
//        Request request = new Request.Builder()
//                .url(url)
//                .method("POST", RequestBody.create(mediaType, rechargeParam.toJson()))
//                .addHeader("X-ACCESS-SIGN", sign)
//                .addHeader("X-ACCESS-TIMESTAMP",timesamp)
//                .addHeader("X-ACCESS-USERID",userid)
//                .addHeader("Content-Type", "application/json;charset=utf-8")
//                .build();
//        Response response = client.newCall(request).execute();
//        String resp = response.body().string();
//        String data1 = objectMapper.writeValueAsString(objectMapper.readValue(resp, Map.class).get("data"));
//        // 验签
//        HmacSHA256Signature sha256_ = new HmacSHA256Signature();
//        String sign256 = sha256_.computeSignature(key, data1);
//        sign256 = sign256.replaceAll("/", "_").replaceAll("\\+", "-");
//
//        WalletPayResp walletPayResp = JsonUtil.fromJsonFastJSON(WalletPayResp.class, resp);
//        RechargeResp rechargeResp = JsonUtil.fromJsonFastJSON(RechargeResp.class,  walletPayResp.getData());
//        System.out.println(sign256);
//        System.out.println(walletPayResp.getSign());
//
//    }

    /**
     * 充值支付订单
     *
     * @param rechargeParam
     * @return
     */
    public static RechargeResp createRechargeOrder(RechargeParam rechargeParam, PaymentChannel paymentChannel) throws Exception {
        return getRechargeResp(rechargeParam, BASE_URL + RECHARGE_NOTIFY_URL, paymentChannel.getCurrencyCode(), paymentChannel.getDzkey(), paymentChannel.getPrivateKey());
    }

    /**
     * 购买vip支付订单
     *
     * @param rechargeParam
     * @return
     */
    public static RechargeResp createRechargeOrderVIP(RechargeParam rechargeParam, PaymentChannel paymentChannel) throws Exception {
        return getRechargeResp(rechargeParam, BASE_URL + RECHARGE_NOTIFY_VIP_URL, paymentChannel.getCurrencyCode(), paymentChannel.getDzkey(), paymentChannel.getPrivateKey());
    }


    /*
     * 创建 支付订单
     * @param rechargeParam
     * @param rechargeNotifyUrl  回调地址
     * @return
     */
    private static RechargeResp getRechargeResp(RechargeParam rechargeParam, String rechargeNotifyUrl, String user_id, String url, String secret_key) throws Exception {
        rechargeParam.setCurrency_code("USD");
        rechargeParam.setUser_id(user_id);
//        rechargeParam.setCoin_code("USDT.TRC20");
        if (StringUtil.isEmpty(rechargeParam.getCoin_code())){
            rechargeParam.setCoin_code("USDT.TRC20");
        }
        rechargeParam.setLanguage(1);
        rechargeParam.setAsyn_notice_url(rechargeNotifyUrl);
        rechargeParam.setSync_jump_url("");
        rechargeParam.setRemark("");
        String timesamp = System.currentTimeMillis()/1000+"";
        String data = timesamp + user_id + "POST" + RECHARGE_URL + rechargeParam.toJson();

        HmacSHA256Signature sha256 = new HmacSHA256Signature();
        String sign = sha256.computeSignature(secret_key, data);

        sign = sign.replaceAll("/", "_").replaceAll("\\+", "-");

        MediaType mediaType = MediaType.parse("application/json;charset=UTF-8");
        Request request = new Request.Builder()
                .url(url+RECHARGE_URL)
                .method("POST", RequestBody.create(mediaType, rechargeParam.toJson()))
                .addHeader("X-ACCESS-SIGN", sign)
                .addHeader("X-ACCESS-TIMESTAMP",timesamp)
                .addHeader("X-ACCESS-USERID",user_id)
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .build();
        Response response = client.newCall(request).execute();
        String resp = response.body().string();

        WalletPayResp walletPayResp = JsonUtil.fromJsonFastJSON(WalletPayResp.class, resp);

        if (walletPayResp != null && 200 == walletPayResp.getCode()){
            // 验签
            String data1 = objectMapper.writeValueAsString(objectMapper.readValue(resp, Map.class).get("data"));
            HmacSHA256Signature sha256_ = new HmacSHA256Signature();
            String sign256 = sha256_.computeSignature(secret_key, data1);
            sign256 = sign256.replaceAll("/", "_").replaceAll("\\+", "-");

            if (walletPayResp.getSign().equals(sign256)){
                RechargeResp rechargeResp = JsonUtil.fromJsonFastJSON(RechargeResp.class,  walletPayResp.getData());
                return rechargeResp;
            }else {
                log.info("创建 支付订单响应验签不通过");
            }
        }
        return null;
    }

//    public static void main(String[] args) throws IOException {
//        String userid = "1711295983490180";
//        String key = "8R9ZfsFee04XPg1bhSOyaXk5qVnhfDZ6";
//        String payUrl = "http://1.117.2.84:4551" + WITHDRAW_NOTIFY_URL;
//        String url =  "https://api.aa.im/api/withdrawal/create";
//
//        WithdrawParam withdrawParam = new WithdrawParam();
//        withdrawParam.setUser_id(userid);
//        withdrawParam.setUser_withdrawal_id("cstx000000003");
//        withdrawParam.setWithdrawal_address("TTVWBAFoFsBnKfYQWmm62NW7njkWePHUxc");
//        withdrawParam.setUser_custom_id("");
//        withdrawParam.setCurrency_code("USD");
//        withdrawParam.setCoin_code("USDT.TRC20");
//        withdrawParam.setCurrency_amount("100");
//        withdrawParam.setAsyn_notice_url(payUrl);
//        withdrawParam.setRemark("ceshi");
//
//        String timesamp = System.currentTimeMillis()/1000+"";
//        String data = timesamp + userid + "POST" + WITHDRAW_URL + withdrawParam.toJson();
//
//        HmacSHA256Signature sha256 = new HmacSHA256Signature();
//        String sign = sha256.computeSignature(key, data);
//        sign = sign.replaceAll("/", "_").replaceAll("\\+", "-");
//        MediaType mediaType = MediaType.parse("application/json;charset=UTF-8");
//        Request request = new Request.Builder()
//                .url(url)
//                .method("POST", RequestBody.create(mediaType, withdrawParam.toJson()))
//                .addHeader("X-ACCESS-SIGN", sign)
//                .addHeader("X-ACCESS-TIMESTAMP",timesamp)
//                .addHeader("X-ACCESS-USERID",userid)
//                .addHeader("Content-Type", "application/json;charset=utf-8")
//                .build();
//        Response response = client.newCall(request).execute();
//        String resp = response.body().string();
//        WalletPayResp walletPayResp = JsonUtil.fromJsonFastJSON(WalletPayResp.class, resp);
//
//        WithdrawResp WithdrawResp = JsonUtil.fromJsonFastJSON(WithdrawResp.class, walletPayResp.getData());
//
//        HmacSHA256Signature sha2561 = new HmacSHA256Signature();
//        String sign1 = sha256.computeSignature(key, WithdrawResp.toJson());
//        sign1 = sign1.replaceAll("/", "_").replaceAll("\\+", "-");
//
//        System.out.println(sign1);
//        System.out.println(walletPayResp.getSign());
//        System.out.println(resp);
//
//    }

    /**
     * 创建 提现订单
     * @param withdrawParam
     * @param paymentChannel
     * @return
     * @throws IOException
     */
    public static WithdrawResp createWithdrawOrder(WithdrawParam withdrawParam, PaymentChannel paymentChannel) throws IOException {

        withdrawParam.setAsyn_notice_url(BASE_URL + WITHDRAW_NOTIFY_URL);
        withdrawParam.setUser_id(paymentChannel.getCurrencyCode());
        withdrawParam.setUser_custom_id("");
        withdrawParam.setCurrency_code("USD");
        if (withdrawParam.getCoin_code()==null){
            withdrawParam.setCoin_code("USDT.TRC20");
        }
//        withdrawParam.setCoin_code("USDT.TRC20");

        String timesamp = System.currentTimeMillis()/1000+"";
        String data = timesamp + paymentChannel.getCurrencyCode() + "POST" + WITHDRAW_URL + withdrawParam.toJson();

        HmacSHA256Signature sha256 = new HmacSHA256Signature();
        String sign = sha256.computeSignature(paymentChannel.getPrivateKey(), data);
        sign = sign.replaceAll("/", "_").replaceAll("\\+", "-");

        MediaType mediaType = MediaType.parse("application/json;charset=UTF-8");
        Request request = new Request.Builder()
                .url(paymentChannel.getDzkey() + WITHDRAW_URL)
                .method("POST", RequestBody.create(mediaType, withdrawParam.toJson()))
                .addHeader("X-ACCESS-SIGN", sign)
                .addHeader("X-ACCESS-TIMESTAMP",timesamp)
                .addHeader("X-ACCESS-USERID",paymentChannel.getCurrencyCode())
                .addHeader("Content-Type", "application/json;charset=utf-8")
                .build();
        Response response = client.newCall(request).execute();
        String resp = response.body().string();
        WalletPayResp walletPayResp = JsonUtil.fromJsonFastJSON(WalletPayResp.class, resp);

        if (walletPayResp != null &&200 == walletPayResp.getCode()){

            // 验签
            String data1 = objectMapper.writeValueAsString(objectMapper.readValue(resp, Map.class).get("data"));
            HmacSHA256Signature sha256_ = new HmacSHA256Signature();
            String sign256 = sha256_.computeSignature(paymentChannel.getPrivateKey(), data1);
            sign256 = sign256.replaceAll("/", "_").replaceAll("\\+", "-");

            if (walletPayResp.getSign().equals(sign256)){
                WithdrawResp WithdrawResp = JsonUtil.fromJsonFastJSON(WithdrawResp.class, walletPayResp.getData());
                return WithdrawResp;
            }else {
                log.info("创建 提现订单响应验签不通过");
            }

        }else {
            throw new RuntimeException(String.format("[WalletPay]三方返回结果: %s", resp));
        }

        return null;
    }




}
