package cn.rh.flash.sdk.paymentChannel.MYPay;

import cn.rh.flash.bean.entity.dzsys.PaymentChannel;
import cn.rh.flash.sdk.paymentChannel.MYPay.MYPayBean.*;
import cn.rh.flash.utils.*;
import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j2;


@Log4j2
public class MYPayUtil {
    private static final String RECHARGE_URL = "/api/payOrder/create";

    private static final String RECHARGE_NOTIFY_URL = "/api/pay/mypay/notifyRechargeOrder";

    private static final String RECHARGE_NOTIFY_VIP_URL = "/api/pay/mypay/notifyRechargeOrderVIP";

    private static final String WITHDRAW_URL = "/api/remitOrder/create";

    private static final String WITHDRAW_NOTIFY_URL = "/api/pay/mypay/notifyWithdrawOrder";
//    private static OkHttpClient client = new OkHttpClient();

//    private static final String BASE_URL = HttpUtil.getBtServerName();
//    private static final String BASE_URL ="http://8.210.85.148:8082";

//    public static void main(String[] args) throws Exception {
//        String userCode = "231229533247";
//        String key = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAIXXQXVxuTUC+htL5jAOyiTK6srz7cxcJduXPpP6kbcNbrp3s1bMjMixmtR1T3iD2b5PIyasWGsYDYZ7Qh1ASRNmiQxx7n3k08O2Joxmz8L1Y0It+Aovep7hDr7rINGnENJ/oc4BtOfBvd9I/jYjLDAvl/vPlrr/8kHrx5Hw4Yp1AgMBAAECgYBbFswDHVn6XUNQ7pAEJxyme7/eTrOKATD2yXGajs87MRYqLCtPKLmsIUwJyqlCkSHRZtS6jlNC18TkwOCXPdg9Mrmrk5B5ccsnMTSNDdBkoD/S29roSxKGjn8M8kQVtxn57NM7jGz+hIIJChFhaLJ6zsclAOawePVF35xNghGH4QJBAMFfjpYVSUkoX7l2HoVI6SJjBuobwr2hql30kgO5qd4d46xlU0fRxOzAS9eH0PtG48qKC3DEkseTAZzjlbyA+hkCQQCxL+gc/eR6H9Egt7eIwD/UP+HYWtLBKzLq94jrDCBtAmC4rbZbItVonpEDygvEnqo5FwgXcirLJQfKr3NcWNa9AkEAndYWd1B2mh03TRUpwlcJ3ASCX1I7eTdc7QW0rQ+9pEw3Sr8F5AhHuYsYHJuCK6foRKi6v9fjirzmQx3MWanY+QJBAIjIw/aFpfkFUOtiCGSgjWXsPwxH6QVItREhMOjLPskImeRX7jvi4z7VknuLYZRTLHPQLOX0s8aJer9kvLjguD0CQDW30LF6ONroa11b5/TbDd/4et3OKlAwxkhnqnbi6X2vCxOs7RbzNaTQ9iXA/+yu4h8y1qFru0xvZn+larZ/8LM=";
//        String notifyUrl = "http://8.210.85.148:8082";
//        String orderId = MakeOrderNum.makeOrderNum("test");
//        System.out.println("orderId:--->" + orderId);
//        String url = "https://gateway.guming.vip" ;
//        //充值
//        MYRechargeParam param = new MYRechargeParam();
//        param.setMerchantOrderNo(orderId);
//        param.setModel("901");
//        param.setBankCode("DIGITWALLET");
//        param.setMerchantId("10383");
//        param.setAmount("200.00");
//        MYPayResp resp = MYPayUtil.getRechargeResp(param, notifyUrl+RECHARGE_NOTIFY_URL, userCode, url+ RECHARGE_URL, key,key);
//        System.out.println(resp);
//    }

//    public static void main(String[] args) throws Exception {
//        MYPayNotifyResp resp = new MYPayNotifyResp();
//        resp.setStatus("1");
//        resp.setMerchantId("10383");
//        resp.setAmount("10");
//        resp.setMerchantOrderNo("cz202402021622374490047");
//
//        String encrypt = RSAUtils.encrypt(resp.toSignDate(), "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKX1s9pffMUycAYIeO/pJrJoImNYmkCP4Zut2W2iDahxvb78qXu9fnBJqjmlsCHRYxgzpL1tyiD6yGDgfT2O5c4uRpxc+iJJdhyJT6NusRgTH7lsmpV64wpW7hxHCVXnC9ww8Nt4taFdoYJFzfJmE8nnIYZTJpQoJ7XfzGMa8lifAgMBAAECgYAdLgVQeQeUGJkvxl+VNzZRmhOpyhNdFK9DUx7ustaZ7l3BbeVS12+ayKkAd8xvNcSppbLqh5pJkC6ZMf9zSmPHQpSAVIuWgkfbkpp9QgmyWeRQRvUDiwAuR2sxlYCJM+cKeHuRZRNFRQvGdDGRy3Hf13qTVMHRCdOyAZ6N/fwwIQJBAO0JjUB12RzLIwM+Lvi+26S4cmt8WzmXHHhCWjBR7adWDDMRRNMXX0HEXFHLQa3bJd7vpetg4Gh/y8dc9hyOutECQQCzPIFvpwf434rDt6i/GyC8i8wkNq0/PJJHxDI4yzVobXH7eSEE0rQd1xJypMgc+urIlHSD1A76Lg+crV+yP9hvAkBXMA4+yZpElwuX48WPRVSxMA8WLjW35zdXnMBjyZ7q0CHInu973bryC/IRO/w6oMM7T1buT0H77hXcjPsR4gYhAkEAjBpTow8RcDxsn6hEQ33VDQzJudTzf2a9gVOfXj2ZtdM3MbbxVG/PUzP2u56Kvfx04e0JVrLAMlcm5PWwxAicCQJBAIpwM21FGsfa4b6nfJ/keCsufv3RMSZ+YrLjW3I8Z4dTEkhnoZgKvVLIA9wXLLy3ZTrsYqXBr/UEyXd05Ojhhh4=");
//        System.out.println(encrypt);
//
//    }

    public static MYPayResp createRechargeOrder(MYRechargeParam param, PaymentChannel paymentChannel,String BASE_URL) throws Exception {
        System.out.println("createRechargeOrder\n");
        return getRechargeResp(param,BASE_URL +"/cdb"+ RECHARGE_NOTIFY_URL,
                paymentChannel.getCurrencyCode(),paymentChannel.getDzkey()+RECHARGE_URL,paymentChannel.getPrivateKey(),paymentChannel.getPublicKey());
    }

    public static MYPayResp createRechargeOrderVIP(MYRechargeParam param, PaymentChannel paymentChannel,String BASE_URL) throws Exception {
        System.out.println("createRechargeOrderVIP\n");
        return getRechargeResp(param,BASE_URL +"/cdb"+ RECHARGE_NOTIFY_VIP_URL,
                paymentChannel.getCurrencyCode(),paymentChannel.getDzkey()+RECHARGE_URL,paymentChannel.getPrivateKey(),paymentChannel.getPublicKey());
    }

    //充值
    private static MYPayResp getRechargeResp(MYRechargeParam param,String rechargeNotifyUrl, String userCode, String url, String key,String publicKey) throws Exception {
        param.setNotifyUrl(rechargeNotifyUrl);
        String encrypt = RSAUtils.encrypt(param.toSignDate(), key);
        param.setSign(encrypt);
        String jsonString = JSON.toJSONString(param);
        System.out.println("创建支付单param:--->" + jsonString);

        String resp = HttpUtil.doPostJson(url, jsonString);
        MYPayResp myPayResp = JSON.parseObject(resp, MYPayResp.class);
        if (myPayResp!=null&&myPayResp.getCode().equals("0")){
            //验证签名
            Boolean verify = RSAUtils.verify(myPayResp.toSignDate(), myPayResp.getSign(), publicKey);
            System.out.println("mypay创建支付单回调验证签名结果："+verify);
            if (verify){
                return myPayResp;
            }
            log.error("mypay创建支付单签名验证错误："+myPayResp);
            throw new RuntimeException(String.format("[MYPay]三方返回结果: %s",myPayResp));
        }else {
            log.error("mypay创建支付单创建充值错误："+myPayResp);
            throw new RuntimeException(String.format("[MYPay]三方返回结果: %s",myPayResp));
        }
    }

    public static MYPayWithdrawResp createWithdrawOrder(MYWithdrawParam param,PaymentChannel paymentChannel,String BASE_URL) throws Exception {
        param.setNotifyUrl(BASE_URL+"/cdb"+WITHDRAW_NOTIFY_URL);
        String encrypt = RSAUtils.encrypt(param.toSignDate(), paymentChannel.getPrivateKey());
        param.setSign(encrypt);
        System.out.println("param:--->" + param);
        String jsonString = JSON.toJSONString(param);
        System.out.println(jsonString);

        String resp = HttpUtil.doPostJson(paymentChannel.getDzkey()+WITHDRAW_URL, jsonString);
        MYPayWithdrawResp myPayWithdrawResp = JSON.parseObject(resp, MYPayWithdrawResp.class);
        if (myPayWithdrawResp!=null&&myPayWithdrawResp.getCode().equals("0")){
            return myPayWithdrawResp;
        }else {
            log.error("kdpay创建提现错误："+resp);
            throw new RuntimeException(String.format("[KDPay]三方返回结果: %s", resp));
        }
    }





}
