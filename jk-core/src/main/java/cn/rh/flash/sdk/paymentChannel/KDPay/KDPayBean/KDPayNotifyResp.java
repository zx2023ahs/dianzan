package cn.rh.flash.sdk.paymentChannel.KDPay.KDPayBean;

import lombok.Data;

import java.io.Serializable;

@Data
public class KDPayNotifyResp implements Serializable {
    //下单金额
    private String amount;
    //签名值（32位字母大写）;
    //示例：MD5(orderCode&amount&userCode&status&key)
    private String sign;
    //订单号
    private String orderCode;
    //商户号
    private String userCode;
    //支付状态 1 初始 2 待支付 3 已支付 4 失败
    private String status;

    public String toSign(String key){
        return  orderCode+ "&" +
                amount+ "&" +
                userCode+ "&" +
                status+ "&" +
                key  ;
    }
}
