
package cn.rh.flash.sdk.paymentChannel.BiPay.BipayBean;

import lombok.Data;

@Data
public class RechargeNotify {

    //订单支付币种地址
    private String address;

    //货币金额
    private String amount;

    //支付币种金额
    private String coinAmount;

    //支付币种
    private String coinCode;

    //货币代码
    private String currency;

    //自定义id
    private String customId;

    //自定义订单id
    private String customOrderId; // 11

    //系统订单id
    private String id;

    //备注
    private String remarks;

    //签名
    private String sign;

    //状态 0支付中 1支付成功 2失败 3超时
    private String status; // =1

}