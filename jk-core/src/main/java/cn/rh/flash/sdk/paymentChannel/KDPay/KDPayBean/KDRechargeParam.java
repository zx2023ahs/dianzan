package cn.rh.flash.sdk.paymentChannel.KDPay.KDPayBean;

import lombok.Data;

import java.io.Serializable;

@Data
public class KDRechargeParam implements Serializable {
    //商户号
    private String userCode;
    //商户订单号
    private String orderCode;
    //支付金额（以元为单位）
    private String amount;
    //支付类型：3（固定值3：K豆支付）
    private String payType="3";
    //回调地址
    private String callbackUrl;
    //签名值（32位字母大写）;
    // 示例：MD5(orderCode&amount&payType&userCode&key)
    private String sign;

    @Override
    public String toString() {
        return  "userCode=" + userCode +
                "&orderCode=" + orderCode +
                "&amount=" + amount +
                "&payType=" + payType +
                "&callbackUrl=" + callbackUrl +
                "&sign=" + sign ;
    }

    public String toSign(String key){
        return  orderCode+ "&" +
                amount+ "&" +
                payType+ "&" +
                userCode+ "&" +
                key  ;
    }
}
