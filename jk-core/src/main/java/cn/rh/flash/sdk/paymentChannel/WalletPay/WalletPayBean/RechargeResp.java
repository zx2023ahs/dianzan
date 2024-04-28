
package cn.rh.flash.sdk.paymentChannel.WalletPay.WalletPayBean;

import lombok.Data;

/**
 * 发起交易订单响应
 */
@Data
public class RechargeResp {

    //系统订单id
    private String order_id;

    // 货币代码
    private String currency_code;

    //货币金额
    private String currency_money;

    //支付币种
    private String coin_code;

    //支付币种金额
    private String coin_money;

    //支付币种地址
    private String coin_address;

    //支付地址
    private String pay_address_url;

    //过期时间
    private long order_expire_time;




}