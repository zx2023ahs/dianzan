
package cn.rh.flash.sdk.paymentChannel.BiPay.BipayBean;

import lombok.Data;


@Data
public class WithdrawNotify {

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

    //状态 0 审核中 1 处理中 2 成功 3 已驳回 4 已取消 5 失败
    private Integer status;

    //状态信息
    private String statusMsg;

    //签名 签名方式如上
    private String sign;

}