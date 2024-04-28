package cn.rh.flash.sdk.paymentChannel.QNQBPay;

import cn.hutool.crypto.digest.MD5;
import cn.rh.flash.bean.entity.dzsys.PaymentChannel;
import cn.rh.flash.sdk.paymentChannel.QNQBPay.QNQBPayBean.*;
import cn.rh.flash.utils.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;


@Log4j2
public class QNQBPayUtil {
    private static final String RECHARGE_URL = "/create";

    private static final String RECHARGE_NOTIFY_URL = "/api/pay/qnqbpay/notifyRechargeOrder";

    private static final String RECHARGE_NOTIFY_VIP_URL = "/api/pay/qnqbpay/notifyRechargeOrderVIP";

    private static final String WITHDRAW_URL = "/create-withdraw";

    private static final String WITHDRAW_NOTIFY_URL = "/api/pay/qnqbpay/notifyWithdrawOrder";
//    private static OkHttpClient client = new OkHttpClient();

//    private static final String BASE_URL = HttpUtil.getBtServerName();
//    private static final String BASE_URL ="http://8.210.85.148:8082";

//    public static void main(String[] args) throws Exception {
//        String userCode = "231229533247";
//        String notifyUrl = "http://8.210.85.148:8082";
//        String orderId = MakeOrderNum.makeOrderNum("test");
//        System.out.println("orderId:--->" + orderId);
//        String url = "https://gateway.guming.vip" ;
//        //充值
//        QNQBRechargeParam param = new QNQBRechargeParam();
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

    public static QNQBPayResp createRechargeOrder(QNQBPayRechargeParam param, PaymentChannel paymentChannel, String BASE_URL) throws Exception {
        System.out.println("createRechargeOrder\n");
        return getRechargeResp(param,BASE_URL +"/cdb"+ RECHARGE_NOTIFY_URL,paymentChannel.getDzkey()+RECHARGE_URL,paymentChannel.getPrivateKey());
    }

    public static QNQBPayResp createRechargeOrderVIP(QNQBPayRechargeParam param, PaymentChannel paymentChannel, String BASE_URL) throws Exception {
        System.out.println("createRechargeOrderVIP\n");
        return getRechargeResp(param,BASE_URL +"/cdb"+ RECHARGE_NOTIFY_VIP_URL,paymentChannel.getDzkey()+RECHARGE_URL,paymentChannel.getPrivateKey());
    }

    //充值
    private static QNQBPayResp getRechargeResp(QNQBPayRechargeParam param, String rechargeNotifyUrl, String url, String key) throws Exception {
        param.setNotifyurl(rechargeNotifyUrl);
        //加密参数md5(recvid+orderid+amount+apikey)
        param.setSign(MD5.create().digestHex(param.getRecvid()+param.getOrderid()+param.getAmount()+key).toLowerCase());
        String jsonString = JSON.toJSONString(param);
        System.out.println("创建支付单param:--->" + jsonString);

        String resp = HttpUtil.doPostJson(url, jsonString);
        System.out.println("resp-------->"+resp);
//        QNQBPayResp myPayResp = JSON.parseObject(resp,QNQBPayResp.class);
        QNQBPayResp qnqbPayResp = new QNQBPayResp();
        JSONObject jsonObject = JSON.parseObject(resp);
        qnqbPayResp.setErrmsg(jsonObject.getString("errmsg"));
        qnqbPayResp.setErrcode(jsonObject.getInteger("errcode"));
        qnqbPayResp.setData(JSON.parseObject(jsonObject.get("data").toString(), QNQBPayData.class));

        if (qnqbPayResp!=null&&qnqbPayResp.getErrcode()==0){
            System.out.println("qnqbpay创建支付单结果："+qnqbPayResp);
            return qnqbPayResp;
        }else {
            log.error("qnqbpay创建支付单创建充值错误："+qnqbPayResp);
            throw new RuntimeException(String.format("[QNQBPay]三方返回结果: %s",qnqbPayResp));
        }
    }

    /**
     * resp返回值的数据结构
     * // {
     *      "errcode": 0,
     *      "data": {
     *      "status": 1
     *       },
     *      "errmsg": "success"
     *    }
     * @param param
     * @param paymentChannel
     * @param BASE_URL
     * @return
     * @throws Exception
     */
    public static QNQBPayWithdrawResp createWithdrawOrder(QNQBPayWithdrawParam param, PaymentChannel paymentChannel, String BASE_URL) throws Exception {
        param.setNotifyurl(BASE_URL+"/cdb"+WITHDRAW_NOTIFY_URL);
        param.setSign(MD5.create().digestHex(paymentChannel.getCurrencyCode()+param.getOrderid()+param.getAmount()+paymentChannel.getPrivateKey()).toLowerCase());
        param.setSendid(paymentChannel.getCurrencyCode());

        System.out.println("param:------>" + param);
        String jsonString = JSON.toJSONString(param);
        System.out.println("jsonString:------>"+jsonString);
        String resp = HttpUtil.doPostJson(paymentChannel.getDzkey()+WITHDRAW_URL, jsonString);
        QNQBPayWithdrawResp myPayWithdrawResp = JSON.parseObject(resp, QNQBPayWithdrawResp.class);
        System.out.println("resp:------>"+resp);
        System.out.println("myPayWithdrawResp:------>"+myPayWithdrawResp);
//        JSONObject jsonObject = JSON.parseObject(resp);
//        JSONObject jsonObject1 = JSON.parseObject(jsonObject.get("data").toString());
//        String status = jsonObject1.get("status").toString();
//        if (jsonObject!=null&&status.equals("1")){
        if (myPayWithdrawResp!=null&&myPayWithdrawResp.getData().getStatus().equals("1")){
            return myPayWithdrawResp;
        }else {
            log.error("qnqbpay创建提现错误："+resp);
            throw new RuntimeException(String.format("[QNQBKDPay]三方返回结果: %s", resp));
        }
    }





}
