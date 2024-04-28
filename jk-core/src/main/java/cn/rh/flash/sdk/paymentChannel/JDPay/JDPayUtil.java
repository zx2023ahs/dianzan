package cn.rh.flash.sdk.paymentChannel.JDPay;

import cn.hutool.crypto.digest.MD5;
import cn.rh.flash.bean.entity.dzsys.PaymentChannel;
import cn.rh.flash.sdk.paymentChannel.JDPay.JDPayBean.JDPayResp;
import cn.rh.flash.sdk.paymentChannel.JDPay.JDPayBean.JDRechargeParam;
import cn.rh.flash.sdk.paymentChannel.JDPay.JDPayBean.JDWithdrawParam;
import cn.rh.flash.utils.JsonUtil;
import cn.rh.flash.utils.MakeOrderNum;
import io.lettuce.core.ScriptOutputType;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;

import java.io.IOException;

@Log4j2
public class JDPayUtil {
    private static final String RECHARGE_URL = "/jdpayOpen/api/pay";

    private static final String RECHARGE_NOTIFY_URL = "/api/pay/jdpay/notifyRechargeOrder";

    private static final String RECHARGE_NOTIFY_VIP_URL = "/api/pay/jdpay/notifyRechargeOrderVIP";

    private static final String WITHDRAW_URL = "/jdpayOpen/api/remit";

    private static final String WITHDRAW_NOTIFY_URL = "/api/pay/jdpay/notifyWithdrawOrder";
    private static OkHttpClient client = new OkHttpClient();

//    private static final String BASE_URL = HttpUtil.getBtServerName();

    public static void main(String[] args) throws IOException {
        String userCode = "240311551363";
        String key = "UTMRM770iOfjZmDmaPeft7qZnIMmP7TC";

        String notifyUrl = "http://47.242.0.145:8082" ;
        String orderId = MakeOrderNum.makeOrderNum("test");
        System.out.println("orderId:--->" + orderId);

        String url = "https://openapi.jdpayapi.com" ;

        //充值
        JDRechargeParam param = new JDRechargeParam();
        param.setOrderCode(orderId);
        param.setAmount("10.00");
        JDPayResp resp = JDPayUtil.getRechargeResp(param, notifyUrl+RECHARGE_NOTIFY_URL, userCode, url+ RECHARGE_URL, key);

        //提现
//        KDWithdrawParam param=new KDWithdrawParam();
//        param.setOrderCode(orderId);
//        param.setAmount("1.00");
//        param.setAddress("ec60d399ff25d24b71b9bca48ea9be637b");
//        KDPayResp resp = KDPayUtil.getWithdrawResp(param, notifyUrl + WITHDRAW_NOTIFY_URL, userCode, url + WITHDRAW_URL, key);
        System.out.println(resp);
    }

    public static JDPayResp createRechargeOrder(JDRechargeParam param, PaymentChannel paymentChannel, String BASE_URL) throws IOException {
        System.out.println("createRechargeOrder\n");
        return getRechargeResp(param,BASE_URL +"/cdb"+ RECHARGE_NOTIFY_URL,
                paymentChannel.getCurrencyCode(),paymentChannel.getDzkey()+RECHARGE_URL,paymentChannel.getPrivateKey());
    }

    public static JDPayResp createRechargeOrderVIP(JDRechargeParam param, PaymentChannel paymentChannel, String BASE_URL) throws IOException {
        System.out.println("createRechargeOrderVIP\n");
        return getRechargeResp(param,BASE_URL +"/cdb"+ RECHARGE_NOTIFY_VIP_URL,
                paymentChannel.getCurrencyCode(),paymentChannel.getDzkey()+RECHARGE_URL,paymentChannel.getPrivateKey());
    }

    //充值
    private static JDPayResp getRechargeResp(JDRechargeParam param, String rechargeNotifyUrl, String userCode, String url, String key) throws IOException{
        param.setUserCode(userCode);
        param.setCallbackUrl(rechargeNotifyUrl);
        String sign = param.toSign(key);
        System.out.println("sign:--->" + sign);
        String s = MD5.create().digestHex(sign).toUpperCase();
        System.out.println("s:--->" + s);
        param.setSign(s);
        System.out.println("param:--->" + param);

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8");
        RequestBody body = RequestBody.create(mediaType, param.toString());

        Request request = new Request.Builder()
                .url(url).method("POST",body)
                .build();
        Response response = client.newCall(request).execute();
        JDPayResp PayResp = JsonUtil.fromJsonFastJSON(JDPayResp.class, response.body().string());
        if (PayResp!=null&&PayResp.getCode()==200){
            return PayResp;
        }
        System.out.println(PayResp.toString());
        return null;
    }

    //提现
    public static JDPayResp createWithdrawOrder(JDWithdrawParam param, PaymentChannel paymentChannel, String BASE_URL) throws IOException{
        param.setUserCode(paymentChannel.getCurrencyCode());
        param.setCallbackUrl(BASE_URL+"/cdb"+WITHDRAW_NOTIFY_URL);

        String sign = param.toSign(paymentChannel.getPrivateKey());
        System.out.println("sign:--->" + sign);
        String s = MD5.create().digestHex(sign).toUpperCase();
        System.out.println("s:--->" + s);
        param.setSign(s);
        System.out.println("param:--->" + param);

        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8");
        RequestBody body = RequestBody.create(mediaType, param.toString());

        Request request = new Request.Builder()
                .url(paymentChannel.getDzkey()+WITHDRAW_URL).method("POST",body)
                .build();
        Response response = client.newCall(request).execute();
        JDPayResp PayResp = JsonUtil.fromJsonFastJSON(JDPayResp.class, response.body().string());
        if (PayResp!=null&&PayResp.getCode()==200){
            return PayResp;
        }else {
            log.error("jdpay创建提现错误："+PayResp);
            throw new RuntimeException(String.format("[JDPay]三方返回结果: %s", PayResp));
        }
    }





}
