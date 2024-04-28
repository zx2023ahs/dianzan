package cn.rh.flash.sdk.paymentChannel.CBPay;

import cn.hutool.crypto.digest.MD5;
import cn.rh.flash.bean.entity.dzsys.PaymentChannel;
import cn.rh.flash.sdk.paymentChannel.CBPay.CBPayBean.CBPayResp;
import cn.rh.flash.sdk.paymentChannel.CBPay.CBPayBean.CBRechargeParam;
import cn.rh.flash.sdk.paymentChannel.CBPay.CBPayBean.CBWithdrawParam;
import cn.rh.flash.sdk.paymentChannel.KDPay.KDPayBean.KDPayResp;
import cn.rh.flash.utils.JsonUtil;
import cn.rh.flash.utils.MakeOrderNum;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;

import java.io.IOException;

@Log4j2
public class CBPayUtil {
    private static final String RECHARGE_URL = "/system/api/pay";

    private static final String RECHARGE_NOTIFY_URL = "/api/pay/cbpay/notifyRechargeOrder";

    private static final String RECHARGE_NOTIFY_VIP_URL = "/api/pay/cbpay/notifyRechargeOrderVIP";

    private static final String WITHDRAW_URL = "/system/api/remit";

    private static final String WITHDRAW_NOTIFY_URL = "/api/pay/cbpay/notifyWithdrawOrder";
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
//        CBRechargeParam param = new CBRechargeParam();
//        param.setOrderCode(orderId);
//        param.setAmount("10.00");
//        CBPayResp resp = CBPayUtil.getRechargeResp(param, notifyUrl+RECHARGE_NOTIFY_URL, userCode, url+ RECHARGE_URL, key);

        //提现
//        CBWithdrawParam param=new CBWithdrawParam();
//        param.setOrderCode(orderId);
//        param.setAmount("1.00");
//        param.setAddress("ec60d399ff25d24b71b9bca48ea9be637b");
//        CBPayResp resp = CBPayUtil.createWithdrawOrder(param, notifyUrl + WITHDRAW_NOTIFY_URL, userCode, url + WITHDRAW_URL, key);
//        System.out.println(resp);
    }

    public static CBPayResp createRechargeOrder(CBRechargeParam param, PaymentChannel paymentChannel, String BASE_URL) throws IOException {
        System.out.println("createRechargeOrder\n");
        return getRechargeResp(param,BASE_URL +"/cdb"+ RECHARGE_NOTIFY_URL,
                paymentChannel.getCurrencyCode(),paymentChannel.getDzkey()+RECHARGE_URL,paymentChannel.getPrivateKey());
    }

    public static CBPayResp createRechargeOrderVIP(CBRechargeParam param, PaymentChannel paymentChannel, String BASE_URL) throws IOException {
        System.out.println("createRechargeOrderVIP\n");
        return getRechargeResp(param,BASE_URL +"/cdb"+ RECHARGE_NOTIFY_VIP_URL,
                paymentChannel.getCurrencyCode(),paymentChannel.getDzkey()+RECHARGE_URL,paymentChannel.getPrivateKey());
    }

    //充值
    private static CBPayResp getRechargeResp(CBRechargeParam param, String rechargeNotifyUrl, String userCode, String url, String key) throws IOException{
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
        CBPayResp cbPayResp = JsonUtil.fromJsonFastJSON(CBPayResp.class, response.body().string());
        if (cbPayResp!=null&&cbPayResp.getCode()==200){
            return cbPayResp;
        }
        System.out.println(cbPayResp.toString());
        return null;
    }

    //提现
    public static CBPayResp createWithdrawOrder(CBWithdrawParam param, PaymentChannel paymentChannel, String BASE_URL) throws IOException{
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
        CBPayResp cbPayResp = JsonUtil.fromJsonFastJSON(CBPayResp.class, response.body().string());
        if (cbPayResp!=null&&cbPayResp.getCode()==200){
            return cbPayResp;
        }else {
            log.error("cbpay创建提现错误："+cbPayResp);
            throw new RuntimeException(String.format("[CBPay]三方返回结果: %s", cbPayResp));
        }
    }





}
