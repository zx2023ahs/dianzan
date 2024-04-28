package cn.rh.flash.sdk.paymentChannel.CBPay.CBPayBean;

import lombok.Data;

import java.io.Serializable;

//kdpay返回参数
@Data
public class CBPayData implements Serializable {
    private String payAmount;
    //kdpay平台订单号
    private String orderNo;
    private String rate;
    private String url;
}