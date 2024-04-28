package cn.rh.flash.sdk.paymentChannel.FPay.FPayBean;

import lombok.Data;

import java.io.Serializable;

//fpay返回参数
@Data
public class FPayData implements Serializable {
    //fpay平台订单号
    private String id;
    //商户订单号
    private String orderid;
    //商户号
    private String merchantid;
    //出币方钱包地址
    private String buyer;
    //amount
    private String amount;
    //手续费
    private String charge;
    //备注
    private String remark;
    //0创建成功
    private String state;
    //支付地址
    private String payurl;
    //异步回调地址
    private String notify_url;
    //支付后同步跳转地址
    private String return_url;
    //订单创建时间
    private String addtime;
    //签名
    private String sign;

}