package cn.rh.flash.sdk.paymentChannel.zimu808Pay.zimu808Bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class Zimu808WithdrawParam implements Serializable {

    //必填 string 金额
    private String amount;
    //必填 string 商户号
    private String appId;
    //必填 string 订单号-唯一
    private String mchOrderNo;
    //必填 string 本平台会员号
    private String member;
    //必填 string 随机字符串(5位以上～15以下)
    private String nonce;
    //必填 string 支付成功通知地址
    private String notifyUrl;
    //必填 string 收款钱包地址
    private String receiveAccount;
    //时间戳
    private String timestamp;
    //必填 string 签名
    private String sign;

    public String toSign(String key) {
        return  "amount=" + amount +
                "&appId=" + appId +
                "&mchOrderNo=" + mchOrderNo +
                "&member=" + member +
                "&nonce=" + nonce +
                "&notifyUrl=" + notifyUrl +
                "&receiveAccount=" + receiveAccount +
                "&timestamp=" + timestamp +
                "&key=" + key ;
    }
}
