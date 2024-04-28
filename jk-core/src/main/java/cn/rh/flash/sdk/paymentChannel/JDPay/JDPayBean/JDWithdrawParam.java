package cn.rh.flash.sdk.paymentChannel.JDPay.JDPayBean;

import lombok.Data;

import java.io.Serializable;

@Data
public class JDWithdrawParam implements Serializable {
    //商户号
    private String userCode;
    //订单号
    private String orderCode;
    //下发金额（以元为单位）
    private String amount;
    //钱包地址
    private String address;
    //回调地址。
    //下发成功会同步返回下发结果。如需额外异步回调通知，需传此参数。回调格式参考目录，下发回调
    private String callbackUrl;
    //回调需要延时多少秒才能回调 延迟范围 0-30
    private String callbackDelayTime="0";
    //签名值（32位字母大写）
    //示例：MD5(orderCode&amount&address&userCode&key)
    private String sign;

    @Override
    public String toString() {
        return  "userCode=" + userCode +
                "&orderCode=" + orderCode +
                "&amount=" + amount +
                "&address=" + address +
                "&callbackUrl=" + callbackUrl +
                "&callbackDelayTime=" + callbackDelayTime +
                "&sign=" + sign ;
    }

    public String toSign(String key){
        return  orderCode+ "&" +
                amount+ "&" +
                address+ "&" +
                userCode+ "&" +
                key  ;
    }
}
