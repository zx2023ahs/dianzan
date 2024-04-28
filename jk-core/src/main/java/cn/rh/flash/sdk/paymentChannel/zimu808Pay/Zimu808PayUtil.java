package cn.rh.flash.sdk.paymentChannel.zimu808Pay;

import cn.rh.flash.bean.entity.dzsys.PaymentChannel;
import cn.rh.flash.sdk.paymentChannel.zimu808Pay.zimu808Bean.Zimu808RechargeParam;
import cn.rh.flash.sdk.paymentChannel.zimu808Pay.zimu808Bean.Zimu808WithdrawParam;
import cn.rh.flash.sdk.paymentChannel.zimu808Pay.zimu808Bean.ZimuResp;
import cn.rh.flash.utils.HttpUtil;
import cn.rh.flash.utils.JsonUtil;
import cn.rh.flash.utils.MD5;
import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Zimu808PayUtil {
    private static final String RECHARGE_URL = "/api/v1/mch/openapi/order/placeOrder";

    private static final String RECHARGE_NOTIFY_URL = "/api/pay/808pay/notifyRechargeOrder";

    private static final String RECHARGE_NOTIFY_VIP_URL = "/api/pay/808pay/notifyRechargeOrderVIP";

    private static final String WITHDRAW_URL = "/api/v1/mch/openapi/order/withdrawOrder";

    private static final String WITHDRAW_NOTIFY_URL = "/api/pay/808pay/notifyWithdrawOrder";

    public static ZimuResp createRechargeOrder(Zimu808RechargeParam param, PaymentChannel paymentChannel, String BASE_URL) throws Exception {
        System.out.println("createRechargeOrder\n");
        return getRechargeResp(param, BASE_URL + "/cdb" + RECHARGE_NOTIFY_URL,
                paymentChannel.getCurrencyCode(), paymentChannel.getDzkey() + RECHARGE_URL, paymentChannel.getPrivateKey());
    }

//    public static ZimuResp createRechargeOrderVIP(Zimu808RechargeParam param, PaymentChannel paymentChannel,String BASE_URL) throws Exception {
//        System.out.println("createRechargeOrderVIP\n");
//        return getRechargeResp(param,BASE_URL +"/cdb"+ RECHARGE_NOTIFY_VIP_URL,
//                paymentChannel.getCurrencyCode(),paymentChannel.getDzkey()+RECHARGE_URL,paymentChannel.getPrivateKey());
//    }

    //充值
    private static ZimuResp getRechargeResp(Zimu808RechargeParam param, String rechargeNotifyUrl, String userCode, String url, String key) throws Exception {
        param.setAppId(userCode);//商户号
        param.setNotifyUrl(rechargeNotifyUrl);//回调地址
        String sign = param.toSign(key);
        System.out.println("808pay cz sign:--->" + sign);
        String s = MD5.getMD5String(sign).toUpperCase();
        System.out.println("s:--->" + s);
        param.setSign(s);
        System.out.println("param:--->" + param);

        String resp = HttpUtil.doPostJson(url, JsonUtil.toJson(param));
        ZimuResp zimuResp = JSON.parseObject(resp, ZimuResp.class);
        if (zimuResp != null && zimuResp.getCode() == 0) {
            return zimuResp;
        }
        log.error("808pay创建充值错误：" + zimuResp);
        return null;
    }

    //提现
    public static ZimuResp createWithdrawOrder(Zimu808WithdrawParam param, PaymentChannel paymentChannel, String BASE_URL) throws Exception {
        param.setAppId(paymentChannel.getCurrencyCode());//商户号
        param.setNotifyUrl(BASE_URL + "/cdb" + WITHDRAW_NOTIFY_URL);//回调地址

        String sign = param.toSign(paymentChannel.getPrivateKey());
        System.out.println("808 tx sign:--->" + sign);
        String s = MD5.getMD5String(sign).toUpperCase();
        System.out.println("s:--->" + s);
        param.setSign(s);
        System.out.println("param:--->" + param);

        String url=paymentChannel.getDzkey()+WITHDRAW_URL;
        String resp = HttpUtil.doPostJson(url, JsonUtil.toJson(param));
        ZimuResp zimuResp = JSON.parseObject(resp, ZimuResp.class);
        if (zimuResp != null && zimuResp.getCode() == 0) {
            return zimuResp;
        } else {
            log.error("808pay创建提现错误：" + zimuResp);
            throw new RuntimeException(String.format("[808Pay]三方返回结果: %s", zimuResp));
        }
    }


//    public static void main(String[] args) {//充值
//        long l=System.currentTimeMillis();
//        System.out.println(l);
//        Zimu808RechargeParam param=new Zimu808RechargeParam();
//        String key="4f0b79afcdd642e4997fbde3b8d03e8b";
//        param.setAmount("100.00");
//        param.setAppId("1760272746897649664");
//        param.setNotifyUrl("http://localhost:8084/notify");
//        param.setTimestamp(String.valueOf(l));
//        param.setMember("zhangsan");
//        param.setMchOrderNo("test202402220011");
//        param.setNonce("abcdef111");
//        String sign = param.toSign(key);
//        System.out.println(sign);
//        String s = MD5.getMD5String(sign).toUpperCase();
//        System.out.println(s);
//        param.setSign(s);
//        String url="https://api.zimu808.com/api/v1/mch/openapi/order/placeOrder";
//        String req = HttpUtil.doPostJson(url, JsonUtil.toJson(param));
//        System.out.println(req);
//    }
//    public static void main(String[] args) {//余额
//        long l=System.currentTimeMillis();
//        String key="4f0b79afcdd642e4997fbde3b8d03e8b";
//        String url="https://api.zimu808.com/api/v1/mch/openapi/account/balance";
//        String md5=MD5.getMD5String("appId=1760272746897649664&nonce=asdfghgf33&timestamp="+l+"&key="+key).toUpperCase();
//        String json="{\n" +
//                "    \"appId\": \"1760272746897649664\",\n" +
//                "    \"nonce\": \"asdfghgf33\",\n" +
//                "    \"timestamp\": \""+l+"\",\n" +
//                "    \"sign\": \""+md5+"\"\n" +
//                "}";
//        String req = HttpUtil.doPostJson(url, json);
//        System.out.println(req);
//    }
}
