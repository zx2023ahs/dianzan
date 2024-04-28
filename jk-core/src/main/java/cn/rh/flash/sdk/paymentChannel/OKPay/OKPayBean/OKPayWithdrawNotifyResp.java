package cn.rh.flash.sdk.paymentChannel.OKPay.OKPayBean;

import lombok.Data;

import java.io.Serializable;

@Data
public class OKPayWithdrawNotifyResp implements Serializable {
    //下单金额
    private String amount;
    //平台给定商户唯一标识
    private String merchantId;
    //订单号
    private String merchantOrderNo;
    //签名值（32位字母大写）;
    //示例：MD5(orderCode&amount&userCode&status&key)
    private String sign;
    //支付状态 0：处理中 1：成功 2：失败
    private String status;

    public String toSignDate() {
        return  "amount=" + amount +
                "&merchantId=" + merchantId +
                "&merchantOrderNo=" + merchantOrderNo +
                "&status=" + status;
    }
}
