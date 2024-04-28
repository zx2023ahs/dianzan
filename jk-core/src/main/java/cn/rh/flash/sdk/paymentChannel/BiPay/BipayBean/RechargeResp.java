
package cn.rh.flash.sdk.paymentChannel.BiPay.BipayBean;

import lombok.Data;

@Data
public class RechargeResp {

    //系统订单id
    private String orderId;

    //自定义订单id
    private String customOrderId;

    //金额
    private String amount;

    //货币代码
    private String currency;

    //支付币种
    private String coinCode;

    //支付币种金额
    private String coinAmount;

    //支付币种地址
    private String address;

    //支付地址
    private String payUrl;

    //过期时间
    private long expireTime;

    //签名
    private String sign;

}