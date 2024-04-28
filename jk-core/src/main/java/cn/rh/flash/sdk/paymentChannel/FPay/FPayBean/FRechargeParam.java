package cn.rh.flash.sdk.paymentChannel.FPay.FPayBean;

import lombok.Data;

import java.io.Serializable;

@Data
public class FRechargeParam implements Serializable {
    //商户号
    private String merchantid;
    //商户订单号
    private String orderid;
    //支付金额（以元为单位）
    private String amount;
    //异步通知的地址
    private String notify_url;
    //支付成功后同步跳转地址
    private String return_url;
    //商户备注，回调时原样返回  如果为空，不参与签名
    private String remark;

    //签名值（32位字母大写）;
    // 示例：MD5(orderCode&amount&payType&userCode&key)
    private String sign;

    public String toSign(String apikey) {
        return  "amount=" + amount +
                "&merchantid=" + merchantid +
                "&notify_url=" + notify_url +
                "&orderid=" + orderid +
                 apikey ;
    }

    @Override
    public String toString() {
        return  "amount=" + amount +
                "&merchantid=" + merchantid +
                "&notify_url=" + notify_url +
                "&orderid=" + orderid +
                "&sign=" + sign ;
    }

}
