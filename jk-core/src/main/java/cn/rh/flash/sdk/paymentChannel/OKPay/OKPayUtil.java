package cn.rh.flash.sdk.paymentChannel.OKPay;

import cn.hutool.crypto.digest.MD5;
import cn.rh.flash.bean.entity.dzsys.PaymentChannel;
import cn.rh.flash.sdk.paymentChannel.OKPay.OKPayBean.*;
import cn.rh.flash.utils.HttpUtil;
import cn.rh.flash.utils.MakeOrderNum;
import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j2;


@Log4j2
public class OKPayUtil {
    //创建支付
    private static final String RECHARGE_URL = "/createpay";

    private static final String RECHARGE_NOTIFY_URL = "/api/pay/okpay/notifyRechargeOrder";

    private static final String RECHARGE_NOTIFY_VIP_URL = "/api/pay/okpay/notifyRechargeOrderVIP";
    //创建提现
    private static final String WITHDRAW_URL = "/createwd";

    private static final String WITHDRAW_NOTIFY_URL = "/api/pay/okpay/notifyWithdrawOrder";
//    private static OkHttpClient client = new OkHttpClient();

//    private static final String BASE_URL = HttpUtil.getBtServerName();
//    private static final String BASE_URL ="http://8.210.85.148:8082";

//    public static void main(String[] args) throws Exception {
//        String userCode = "0e6f307c-cb50-48b1-87f0-b95cb8896ec9";
//        String key = "addfb69434fd4b1080e98082772dae63";
//        String notifyUrl = "http://8.210.85.148:8082";
//        String orderId = MakeOrderNum.makeOrderNum("test");
//        System.out.println("orderId:--->" + orderId);
//        String url = "https://qse123jdsz.okpay777.com" ;
//        //充值
//        OKRechargeParam param = new OKRechargeParam();
//        param.setOrderid(orderId);
//        param.setAmount("200.00");
//        param.setRecvid(userCode);
//        OKPayResp resp = OKPayUtil.getRechargeResp(param, notifyUrl+RECHARGE_NOTIFY_URL, userCode, url+ RECHARGE_URL, key);
//        System.out.println(resp);
//    }

//提现测试
    public static void main(String[] args) throws Exception {
        OKWithdrawParam param = new OKWithdrawParam();
        String userCode = "0e6f307c-cb50-48b1-87f0-b95cb8896ec9";
        String key = "addfb69434fd4b1080e98082772dae63";
        String notifyUrl = "http://8.210.85.148:8082";
        String orderId = MakeOrderNum.makeOrderNum("test");
        param.setNotifyurl(notifyUrl);
        param.setAddress("sdasdasdasdasdasdasda");
        param.setAmount("1.12");
        param.setOrderid(orderId);
        param.setSendid(userCode);

        String lowerCase = MD5.create().digestHex(param.toSignDate(key)).toLowerCase();
        param.setSign(lowerCase);
        System.out.println("orderId:--->" + orderId);
        String url = "https://qse123jdsz.okpay777.com" ;
        String jsonString = JSON.toJSONString(param);
        String resp = HttpUtil.doPostJson(url+WITHDRAW_URL, jsonString);
        System.out.println(resp);
        OKPayData okPayData = JSON.parseObject(resp, OKPayData.class);
        System.out.println("okPayData"+okPayData);

    }


    public static OKPayResp createRechargeOrder(OKRechargeParam param, PaymentChannel paymentChannel, String BASE_URL) throws Exception {
        System.out.println("createRechargeOrder\n");
        return getRechargeResp(param,BASE_URL +"/cdb"+ RECHARGE_NOTIFY_URL,
                paymentChannel.getCurrencyCode(),paymentChannel.getDzkey()+RECHARGE_URL,paymentChannel.getPrivateKey());
    }

    public static OKPayResp createRechargeOrderVIP(OKRechargeParam param, PaymentChannel paymentChannel, String BASE_URL) throws Exception {
        System.out.println("createRechargeOrderVIP\n");
        return getRechargeResp(param,BASE_URL +"/cdb"+ RECHARGE_NOTIFY_VIP_URL,
                paymentChannel.getCurrencyCode(),paymentChannel.getDzkey()+RECHARGE_URL,paymentChannel.getPrivateKey());
    }

    //充值
    private static OKPayResp getRechargeResp(OKRechargeParam param, String rechargeNotifyUrl, String userCode, String url, String key) throws Exception {
        OKPayResp okPayResp = new OKPayResp();
        param.setNotifyurl(rechargeNotifyUrl);
        param.setRecvid(userCode);
        String signDate = param.toSignDate(key);
        System.out.println("signDate="+signDate);

        String s = MD5.create().digestHex(signDate).toLowerCase();

        param.setSign(s);
        String jsonString = JSON.toJSONString(param);
        System.out.println("创建支付单param:--->" + jsonString);

        String resp = HttpUtil.doPostJson(url, jsonString);
        System.out.println(resp);
        OKPayData okPayData = JSON.parseObject(resp, OKPayData.class);
        if (okPayData!=null&&okPayData.getCode()==1){
//            Boolean verify = RSAUtils.verify(myPayResp.toSignDate(), myPayResp.getSign());
            OKPayOrderDto OKPayOrderDto = JSON.parseObject(okPayData.getData(), OKPayOrderDto.class);
            //验证签名 创建时为空，回调时才有值
//            String lowerCase = MD5.create().digestHex(param.getSign() + key).toLowerCase();
//            System.out.println("lowerCase"+lowerCase);
//            System.out.println("payOrderDto.getRetsign()"+payOrderDto.getRetsign());
//            if (lowerCase!=payOrderDto.getRetsign()){
//                log.error("okpay创建支付单验证签名错误："+okPayData);
//                throw new RuntimeException(String.format("[okPay]三方返回结果: %s",okPayData));
//            }
            System.out.println("okpay创建支付单回调结果："+ OKPayOrderDto);
            okPayResp.setUrl(OKPayOrderDto.getNavurl());
            okPayResp.setMsg(okPayResp.getMsg());
            okPayResp.setMerchantOrderNo(OKPayOrderDto.getOrderid());
            okPayResp.setAmount(OKPayOrderDto.getAmount());
            return okPayResp;
        }else {
            log.error("okpay创建支付单创建充值错误："+okPayData);
            throw new RuntimeException(String.format("[okPay]三方返回结果: %s",okPayData));
        }
    }

    public static OKWdOrderDto createWithdrawOrder(OKWithdrawParam param, PaymentChannel paymentChannel, String BASE_URL) throws Exception {
        param.setNotifyurl(BASE_URL+"/cdb"+WITHDRAW_NOTIFY_URL);
        String lowerCase = MD5.create().digestHex(param.toSignDate(paymentChannel.getPrivateKey())).toLowerCase();
//        String encrypt = RSAUtils.encrypt(param.toSignDate(), paymentChannel.getPrivateKey());
        param.setSign(lowerCase);
        System.out.println("param:--->" + param);
        String jsonString = JSON.toJSONString(param);
        System.out.println(jsonString);

        String resp = HttpUtil.doPostJson(paymentChannel.getDzkey()+WITHDRAW_URL, jsonString);
        OKPayData okPayData = JSON.parseObject(resp, OKPayData.class);
        if (okPayData!=null&&okPayData.getCode()==1){
            OKWdOrderDto OKWdOrderDto = JSON.parseObject(okPayData.getData(), OKWdOrderDto.class);
            return OKWdOrderDto;
        }else {
            log.error("okpay创建提现错误："+resp);
            throw new RuntimeException(String.format("[okPay]三方返回结果: %s", resp));
        }
    }





}
