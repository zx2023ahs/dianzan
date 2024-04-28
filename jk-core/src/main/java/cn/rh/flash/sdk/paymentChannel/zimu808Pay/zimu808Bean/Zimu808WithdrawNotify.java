package cn.rh.flash.sdk.paymentChannel.zimu808Pay.zimu808Bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class Zimu808WithdrawNotify implements Serializable {
    //订单金额(整数)，保留两位小数：30.00
    private String amount;
    //商户号
    private String appId;
    //商户号
    private String mchNo;
    //商户订单号，保证唯一
    private String mchOrderNo;
    //随机字符串(5位以上～15以下)
    private String nonce;
    //订单号
    private String orderNo;
    //收款钱包地址
    private String receiveAccount;
    //支付状态 2成功 5拒绝（当应用开启提现金额二次确认时）
    private String status;
    //时间戳（13位）
    private String timestamp;
    //MD5签名：ASCII码从小到大
    private String sign;

    public String toSign(String key) {
        return "amount=" + amount +
                "&appId=" + appId +
                "&mchNo=" + mchNo +
                "&mchOrderNo=" + mchOrderNo +
                "&nonce=" + nonce +
                "&orderNo=" + orderNo +
                "&receiveAccount=" + receiveAccount +
                "&status=" + status +
                "&timestamp=" + timestamp +
                "&key=" + key;
    }
}
