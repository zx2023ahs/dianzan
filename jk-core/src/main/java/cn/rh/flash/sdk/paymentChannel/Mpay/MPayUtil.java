package cn.rh.flash.sdk.paymentChannel.Mpay;

import cn.rh.flash.bean.entity.dzsys.PaymentChannel;
import cn.rh.flash.sdk.paymentChannel.Mpay.dao.Down;
import cn.rh.flash.sdk.paymentChannel.Mpay.dao.Up;
import cn.rh.flash.sdk.paymentChannel.Mpay.mpayBean.MPayData;
import cn.rh.flash.sdk.paymentChannel.Mpay.mpayBean.MPayWithdrawParam;
import cn.rh.flash.utils.MakeOrderNum;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;

import java.io.IOException;

@Log4j2
public class MPayUtil {
    private static final String RECHARGE_URL = "/api/v2/merch/user/up";

    private static final String RECHARGE_NOTIFY_URL = "/api/pay/mpay/notifyRechargeOrder";

    private static final String RECHARGE_NOTIFY_VIP_URL = "/api/pay/mpay/notifyRechargeOrderVIP";

    private static final String WITHDRAW_URL = "/api/v2/merch/user/down";

    private static final String WITHDRAW_NOTIFY_URL = "/api/pay/mpay/notifyWithdrawOrder";
    private static OkHttpClient client = new OkHttpClient();

//    private static final String BASE_URL = HttpUtil.getBtServerName();

    public static void main(String[] args) throws Exception {
        PaymentChannel paymentChannel = new PaymentChannel();
        paymentChannel.setCurrencyCode("1555");
        paymentChannel.setPrivateKey("bbc270164f1554abd59aabf8b98ad35d");
        paymentChannel.setPublicKey("LS0tLS1CRUdJTiBSU0EgUFVCTElDIEtFWS0tLS0tCk1JSUNJakFOQmdrcWhraUc5dzBCQVFFRkFBT0NBZzhBTUlJQ0NnS0NBZ0VBdzRQRzJkQi8wL29JNGp3ZzhaOGsKS2VsU0FjQ282U0ZhWkt1dWJxWjcrenA0L2hoVzR5V1Y4NkhMbkgvUFBmMDd4ZlVLc3hvblFQSWtEU0FveDhVMApJMnY4TzRmdk1UUENDRFdBeVBJQmpqOFU3SHR3ejFHejN6SUZHMFk0K0VpczlMdkZHcnVteHBtNDJqQVZyTTFGCi85R2FGUW1WRjBpWjJ3WmdtTysvdHNzWmczQVEyK3Z4NHA2NG1RWXU4MVVKd2ZJYmpkNDB6YzBodHBqRTVwQ0QKeWZyY3RPNVZjY2dRa2hCV0kxUE9jZGZ1R2YwUXkyZCtveGVOTnAwdWlTNzJLTUJyOFhhUHdBYjRDZmpQUWQ1RAovU2t3Q3E5cmhPTzMzdDc3REpuM1BRWFY5WFFteG9TZmJIeDMyM24xdmdJOWpDekFJeWp2VU1sZW1mS2d6dHpuCkxnbnlVZXk3bTNhbjNHeC9ZdytQZ3IxYzRjb3dET3NqRFErSWNvMHE0eURVN0huUktJZHVHajVNajBEODhZQWcKU0hPZGkxb3FvWHVnU3cyZlV4eFphYVN2TzRqUjd4VzdVZzVYS2xLbmFrbngzK1BTWlJwb1JORUNsN2pWSzJvdAp1SCs5NjFHVVBFeWZCMWczVlpiTG14Qmo4RWxSWUxORGtiNjNVdXZmRkRxSkFpd2QrcmxES3FveHIvTEdlZGp2CjdOSnl0cmM4cmVDcEJOWFkxL1NhWXY1dU81TnEycWJaMmJXNks5QkNxcWYrM21YN2pydXlRTUNMVDlESS9jS1oKcEFnMGFmVkp4cUhlcmFQRUs3eFVMd0NIWjQwdk1mM3gxS0xtNmtoOTVDazhKMUhLbjlUdXdPQ1c5RmZjUllDaQorbGFpTVRINUpaQlRZYTFVdXd5OURMa0NBd0VBQVE9PQotLS0tLUVORCBSU0EgUFVCTElDIEtFWS0tLS0tCg==");
        MPayClient instance = MPayClient.getInstance("https://yibo2.mpapi.app", paymentChannel.getPrivateKey(), paymentChannel.getPublicKey(), 1555);
        Up up = instance.userUp(MakeOrderNum.makeOrderNum("test"), 132,"http://8.210.85.148:8082/api/pay/mpay/notifyRechargeOrder");
        System.out.println(up);
    }

    public static Up createRechargeOrder(MPayData param, PaymentChannel paymentChannel, String BASE_URL) throws Exception {
        System.out.println("createRechargeOrder\n");
        return getRechargeResp(param,BASE_URL +"/cdb"+ RECHARGE_NOTIFY_URL,paymentChannel);
    }

    public static Up createRechargeOrderVIP(MPayData param, PaymentChannel paymentChannel, String BASE_URL) throws Exception {
        System.out.println("createRechargeOrderVIP\n");
        return getRechargeResp(param,BASE_URL +"/cdb"+ RECHARGE_NOTIFY_VIP_URL,paymentChannel);
    }

    //充值
    private static Up getRechargeResp(MPayData param, String rechargeNotifyUrl,PaymentChannel paymentChannel) throws Exception {
        MPayClient instance = MPayClient.getInstance(paymentChannel.getDzkey(), paymentChannel.getPrivateKey(), paymentChannel.getPublicKey(), Integer.valueOf(paymentChannel.getCurrencyCode()));
        Up mpay = instance.userUp(param.getMerchOrderId(), param.getAmount(),rechargeNotifyUrl);
        if (mpay!=null){
            log.info("mpay="+mpay);
            return mpay;
        }
        log.info("mpay="+mpay.toString());
        return null;
    }

    //提现
    public static Down createWithdrawOrder(MPayWithdrawParam param, PaymentChannel paymentChannel, String BASE_URL) throws IOException{
        MPayClient instance = MPayClient.getInstance(paymentChannel.getDzkey(), paymentChannel.getPrivateKey(), paymentChannel.getPublicKey(), Integer.valueOf(paymentChannel.getCurrencyCode()));
        Down mpay = instance.userDown(param.getOrderid(), Float.valueOf(param.getAmount()), param.getAddress(), "", BASE_URL+"/cdb"+"/api/pay/mpay/notifyWithdrawOrder");
        if (mpay!=null){
            return mpay;
        }else {
            log.error("mpay创建提现错误："+mpay);
            throw new RuntimeException(String.format("[mpay]三方返回结果: %s", mpay));
        }
    }





}
