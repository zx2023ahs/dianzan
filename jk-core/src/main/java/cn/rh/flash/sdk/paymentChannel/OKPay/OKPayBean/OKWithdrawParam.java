package cn.rh.flash.sdk.paymentChannel.OKPay.OKPayBean;

import lombok.Data;

import java.io.Serializable;

@Data
public class OKWithdrawParam implements Serializable {
    //商户ID
    private String sendid;
    //商户订单号
    private String orderid;
    //下发金额（以元为单位）
    private String amount;
    //回调地址
    private String notifyurl;
    //提现钱包地址
    private String address;
    //签名值（32位字母小写）;
    // 示例：sign=md5(sendid+orderid+amount+apikey)
    private String sign;


    public String toSignDate(String apikey) {
        return  sendid+orderid+amount+apikey;
    }

}
