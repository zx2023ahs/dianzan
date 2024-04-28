
package cn.rh.flash.sdk.paymentChannel.BiPay.BipayBean;

import lombok.Data;


@Data
public class WithdrawResp {

    //代付id
    private String id;

    //自定义订单号
    private String customOrderId;

    //提现地址
    private String address;

    //金额(USDT)
    private String amount;

    //手续费
    private String fee;

    //状态
    private Integer status;

    //状态信息
    private String statusMsg;

    //签名 签名方式如上
    private String sign;

}