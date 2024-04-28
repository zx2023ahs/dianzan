package cn.rh.flash.sdk.paymentChannel.MYPay.MYPayBean;

import cn.rh.flash.utils.StringUtil;
import lombok.Data;

import java.io.Serializable;

@Data
public class MYRechargeParam implements Serializable {
    //商户ID
    private String merchantId;
    //版本号
    private String version = "1.0.0";
    //商户订单号
    private String merchantOrderNo;
    //支付金额（以元为单位）
    private String amount;
    //通道编码
    private String model;
    //银行编码    当通道编码是网银支付时 必填
    private String bankCode;
    //回调地址
    private String notifyUrl;
    //签名值（32位字母大写）;
    // 示例：MD5(orderCode&amount&payType&userCode&key)
    private String sign;


    public String toSignDate() {
        if (StringUtil.isEmpty(this.bankCode)) {
            System.out.println("-----------no bankCode------------");
            return "amount=" + amount +
                    "&merchantId=" + merchantId +
                    "&merchantOrderNo=" + merchantOrderNo +
                    "&model=" + model +
                    "&notifyUrl=" + notifyUrl +
                    "&version=" + version;
        } else {
            return "amount=" + amount +
                    "&bankCode=" + bankCode +
                    "&merchantId=" + merchantId +
                    "&merchantOrderNo=" + merchantOrderNo +
                    "&model=" + model +
                    "&notifyUrl=" + notifyUrl +
                    "&version=" + version;
        }
    }

}
