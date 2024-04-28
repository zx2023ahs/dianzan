package cn.rh.flash.sdk.paymentChannel.OKPay.OKPayBean;

import lombok.Data;

import java.io.Serializable;

//KDPay返回参数
@Data
public class OKPayResp implements Serializable {
    //状态值;0代表成功;其他值都为不成功
    private String code;
    //响应信息描述
    private String msg;
    //商户订单号
    private String merchantOrderNo;
    //金额
    private String amount;
    //支付地址
    private String url;
    //签名
    private String sign;


    public String toSignDate() {
        return  "amount=" + amount +
                "&code=" + code +
                "&merchantOrderNo=" + merchantOrderNo +
                "&msg=" + msg +
                "&url=" + url ;
    }

}
