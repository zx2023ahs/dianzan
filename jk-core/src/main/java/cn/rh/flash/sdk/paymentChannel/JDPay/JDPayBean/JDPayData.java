package cn.rh.flash.sdk.paymentChannel.JDPay.JDPayBean;

import lombok.Data;

import java.io.Serializable;

//kdpay返回参数
@Data
public class JDPayData implements Serializable {
    private String payAmount;
    //Jdpay平台订单号
    private String orderNo;
    private String rate;
    private String url;
}