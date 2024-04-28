package cn.rh.flash.sdk.paymentChannel.KDPay;

import cn.hutool.crypto.digest.MD5;
import cn.rh.flash.bean.entity.dzsys.PaymentChannel;
import cn.rh.flash.sdk.paymentChannel.KDPay.KDPayBean.KDPayResp;
import cn.rh.flash.sdk.paymentChannel.KDPay.KDPayBean.KDRechargeParam;
import cn.rh.flash.sdk.paymentChannel.KDPay.KDPayBean.KDWithdrawParam;
import cn.rh.flash.utils.JsonUtil;
import cn.rh.flash.utils.MakeOrderNum;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;

import java.io.IOException;

@Log4j2
public class KDPayUtil {
    private static final String RECHARGE_URL = "/system/api/pay";

    private static final String RECHARGE_NOTIFY_URL = "/api/pay/kdpay/notifyRechargeOrder";

    private static final String RECHARGE_NOTIFY_VIP_URL = "/api/pay/kdpay/notifyRechargeOrderVIP";

    private static final String WITHDRAW_URL = "/system/api/remit";

    private static final String WITHDRAW_NOTIFY_URL = "/api/pay/kdpay/notifyWithdrawOrder";
    private static OkHttpClient client = new OkHttpClient();

//    private static final String BASE_URL = HttpUtil.getBtServerName();

    public static void main(String[] args) throws IOException {
        String userCode = "231229533247";
        String key = "YcbC2ie0iMV811dgNcGQLl03yhINSbbr";

        String notifyUrl = "http://47.242.0.145:8082" ;
        String orderId = MakeOrderNum.makeOrderNum("test");
        System.out.println("orderId:--->" + orderId);

        String url = "http://kbyapi.abillioncoin.com" ;

        //充值
        KDRechargeParam param = new KDRechargeParam();
        param.setOrderCode(orderId);
        param.setAmount("10.00");
        KDPayResp resp = KDPayUtil.getRechargeResp(param, notifyUrl+RECHARGE_NOTIFY_URL, userCode, url+ RECHARGE_URL, key);

        //提现
//        KDWithdrawParam param=new KDWithdrawParam();
//        param.setOrderCode(orderId);
//        param.setAmount("1.00");
//        param.setAddress("ec60d399ff25d24b71b9bca48ea9be637b");
//        KDPayResp resp = KDPayUtil.getWithdrawResp(param, notifyUrl + WITHDRAW_NOTIFY_URL, userCode, url + WITHDRAW_URL, key);
        System.out.println(resp);
    }

    public static KDPayResp createRechargeOrder(KDRechargeParam param, PaymentChannel paymentChannel,String BASE_URL) throws IOException {
        System.out.println("createRechargeOrder\n");
        return getRechargeResp(param,BASE_URL +"/cdb"+ RECHARGE_NOTIFY_URL,
                paymentChannel.getCurrencyCode(),paymentChannel.getDzkey()+RECHARGE_URL,paymentChannel.getPrivateKey());
    }

    public static KDPayResp createRechargeOrderVIP(KDRechargeParam param, PaymentChannel paymentChannel,String BASE_URL) throws IOException {
        System.out.println("createRechargeOrderVIP\n");
        return getRechargeResp(param,BASE_URL +"/cdb"+ RECHARGE_NOTIFY_VIP_URL,
                paymentChannel.getCurrencyCode(),paymentChannel.getDzkey()+RECHARGE_URL,paymentChannel.getPrivateKey());
    }

    //充值
    private static KDPayResp getRechargeResp(KDRechargeParam param,String rechargeNotifyUrl, String userCode, String url, String key) throws IOException{
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
        KDPayResp kdPayResp = JsonUtil.fromJsonFastJSON(KDPayResp.class, response.body().string());
        if (kdPayResp!=null&&kdPayResp.getCode()==200){
            return kdPayResp;
        }
        System.out.println(kdPayResp.toString());
        return null;
    }

    //提现
    public static KDPayResp createWithdrawOrder(KDWithdrawParam param,PaymentChannel paymentChannel,String BASE_URL) throws IOException{
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
        KDPayResp kdPayResp = JsonUtil.fromJsonFastJSON(KDPayResp.class, response.body().string());
        if (kdPayResp!=null&&kdPayResp.getCode()==200){
            return kdPayResp;
        }else {
            log.error("kdpay创建提现错误："+kdPayResp);
            throw new RuntimeException(String.format("[KDPay]三方返回结果: %s", kdPayResp));
        }
    }





}
