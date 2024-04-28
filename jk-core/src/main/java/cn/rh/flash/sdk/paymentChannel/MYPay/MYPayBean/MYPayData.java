package cn.rh.flash.sdk.paymentChannel.MYPay.MYPayBean;

import lombok.Data;

import java.io.Serializable;

//返回参数
@Data
public class MYPayData implements Serializable {
    private String payAmount;
    //平台订单号
    private String orderNo;
    private String rate;
    private String url;
}