
package cn.rh.flash.sdk.paymentChannel.WalletPay.WalletPayBean;

import lombok.Data;

/**
 * 交易订单支付成功通知
 */
@Data
public class RechargeNotify {

    //order_id
    private String order_id;

    //订单状态:1.支付中,2.支付成功3.支付失败4.支付超时
    private String order_status; // =1

    //货币代码
    private String currency_code;

    //支付币种
    private String coin_code;

    //订单支付币种地址
    private String coin_address;

    //自定义订单id
    private String user_order_id; // 11

    //实际到账币种金额
    private String coin_receipt_money;

    //货币金额
    private String currency_receipt_money;



}