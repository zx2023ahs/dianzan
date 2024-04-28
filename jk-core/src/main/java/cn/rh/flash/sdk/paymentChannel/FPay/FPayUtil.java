package cn.rh.flash.sdk.paymentChannel.FPay;

import cn.hutool.crypto.digest.MD5;
import cn.rh.flash.bean.entity.dzsys.PaymentChannel;
import cn.rh.flash.sdk.paymentChannel.FPay.FPayBean.FPayResp;
import cn.rh.flash.sdk.paymentChannel.FPay.FPayBean.FRechargeParam;
import cn.rh.flash.sdk.paymentChannel.FPay.FPayBean.FWithdrawParam;
import cn.rh.flash.utils.JsonUtil;
import cn.rh.flash.utils.MakeOrderNum;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;

import java.io.IOException;

@Log4j2
public class FPayUtil {
    private static final String RECHARGE_URL = "/v1/payment/add";

    private static final String RECHARGE_NOTIFY_URL = "/api/pay/fpay/notifyRechargeOrder";

    private static final String RECHARGE_NOTIFY_VIP_URL = "/api/pay/fpay/notifyRechargeOrderVIP";

    private static final String WITHDRAW_URL = "/v1/withdrawal/add";

    private static final String WITHDRAW_NOTIFY_URL = "/api/pay/fpay/notifyWithdrawOrder";
    private static OkHttpClient client = new OkHttpClient();

//    private static final String BASE_URL = HttpUtil.getBtServerName();

    public static void main(String[] args) throws IOException {
        String userCode = "2024031421500704229";
        String key = "6C03A069943171E5D5071FA2AD8A59A6";

        String notifyUrl = "http://47.242.0.145:8082" ;
        String orderId = MakeOrderNum.makeOrderNum("test");
        System.out.println("orderId:--->" + orderId);

        String url = "https://55ukbzi3br.fpayfreedom.com" ;

        //充值
//        FRechargeParam param = new FRechargeParam();
//        param.setOrderid(orderId);
//        param.setAmount("10.00");
//        FPayResp resp = FPayUtil.getRechargeResp(param, notifyUrl+RECHARGE_NOTIFY_URL, userCode, url+ RECHARGE_URL, key);

        //提现
        FWithdrawParam param=new FWithdrawParam();
        param.setOrderid(orderId);
        param.setAmount("1.00");
        param.setAddress("TTQv1xVcH8kffA6qMt3tuAwvrgAPfWYnRx");
        PaymentChannel paymentChannel = new PaymentChannel();
        paymentChannel.setPrivateKey(key);
        paymentChannel.setCurrencyCode("2024031421500704229");
        paymentChannel.setDzkey(url);
        FPayResp resp = FPayUtil.createWithdrawOrder(param, paymentChannel, "http://8.210.101.247:86");
        System.out.println(resp);
    }

    public static FPayResp createRechargeOrder(FRechargeParam param, PaymentChannel paymentChannel, String BASE_URL) throws IOException {
        System.out.println("createRechargeOrder\n");
        return getRechargeResp(param,BASE_URL +"/cdb"+ RECHARGE_NOTIFY_URL,
                paymentChannel.getCurrencyCode(),paymentChannel.getDzkey()+RECHARGE_URL,paymentChannel.getPrivateKey());
    }

    public static FPayResp createRechargeOrderVIP(FRechargeParam param, PaymentChannel paymentChannel, String BASE_URL) throws IOException {
        System.out.println("createRechargeOrderVIP\n");
        return getRechargeResp(param,BASE_URL +"/cdb"+ RECHARGE_NOTIFY_VIP_URL,
                paymentChannel.getCurrencyCode(),paymentChannel.getDzkey()+RECHARGE_URL,paymentChannel.getPrivateKey());
    }

    //充值
    private static FPayResp getRechargeResp(FRechargeParam param, String rechargeNotifyUrl, String userCode, String url, String key) throws IOException{
        param.setMerchantid(userCode);
        param.setNotify_url(rechargeNotifyUrl);
        String sign = param.toSign(key);
        System.out.println("sign:--->" + sign);
        String s = MD5.create().digestHex(sign).toUpperCase();
        param.setSign(s);
        System.out.println("param:--->" + param);

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8");
        RequestBody body = RequestBody.create(mediaType, param.toString());

        Request request = new Request.Builder()
                .url(url).method("POST",body)
                .build();
        Response response = client.newCall(request).execute();
        FPayResp fPayResp = JsonUtil.fromJsonFastJSON(FPayResp.class, response.body().string());

        if (fPayResp!=null&&fPayResp.getCode()==0){
            log.info("fPayResp="+fPayResp);
            return fPayResp;
        }
        log.info("fPayResp="+fPayResp.toString());
        return null;
    }

    //提现
    public static FPayResp createWithdrawOrder(FWithdrawParam param, PaymentChannel paymentChannel, String BASE_URL) throws IOException{
        param.setMerchantid(paymentChannel.getCurrencyCode());
        param.setNotify_url(BASE_URL+"/cdb"+WITHDRAW_NOTIFY_URL);

        String sign = param.toSign(paymentChannel.getPrivateKey());
        System.out.println("sign:--->" + sign);
        String s = MD5.create().digestHex(sign).toUpperCase();
        param.setSign(s);
        System.out.println("param:--->" + param);

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8");
        RequestBody body = RequestBody.create(mediaType, param.toString());

        Request request = new Request.Builder()
                .url(paymentChannel.getDzkey()+WITHDRAW_URL).method("POST",body)
                .build();
        Response response = client.newCall(request).execute();
        FPayResp payResp = JsonUtil.fromJsonFastJSON(FPayResp.class, response.body().string());
        if (payResp!=null&&payResp.getCode()==0){
            return payResp;
        }else {
            log.error("fpay创建提现错误："+payResp);
            throw new RuntimeException(String.format("[FPay]三方返回结果: %s", payResp));
        }
    }





}
