package cn.rh.flash.sdk.paymentChannel.MYPay.MYPayBean;

import lombok.Data;

import java.io.Serializable;

@Data
public class MYPayWithdrawResp implements Serializable {
    //下单金额
    private String amount;
    //响应状态码	0为请求成功 ，其他值为失败
    private String code;
    //响应信息描述
    private String msg;
    //订单号
    private String merchantOrderNo;
    //签名值（32位字母大写）;
    //示例：MD5(orderCode&amount&userCode&status&key)
    private String sign;
    //支付状态 0：处理中 1：成功 2：失败
    private String status;

    public String toSignDate() {
        return  "amount=" + amount +
                "&code=" + code +
                "&merchantOrderNo=" + merchantOrderNo +
                "&msg=" + msg +
                "&status=" + status;
    }

}
