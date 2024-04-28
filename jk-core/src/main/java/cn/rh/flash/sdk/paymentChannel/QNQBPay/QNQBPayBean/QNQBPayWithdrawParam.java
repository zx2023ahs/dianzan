package cn.rh.flash.sdk.paymentChannel.QNQBPay.QNQBPayBean;

import lombok.Data;

import java.io.Serializable;

@Data
public class QNQBPayWithdrawParam implements Serializable {
    //商户ID
    private String sendid;
    //商户订单号
    private String orderid;
    //支付金额（以元为单位）
    private String amount;
    //回调地址
    private String notifyurl;
    //提币用户的钱包地址
    private String address;
    //签名值（32位字母小写）;
    // 示例：sign=md5(sendid+orderid+amount+apikey)
    private String sign;
}
