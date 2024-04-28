
package cn.rh.flash.sdk.paymentChannel.WalletPay.WalletPayBean;

import lombok.Data;


@Data
public class WithdrawNotify {

    //代付id
    private String withdrawal_id;

    //用户id
    private String user_id;

    //自定义订单号
    private String user_withdrawal_id;

    //提现地址
    private String withdrawal_address;

    //币种金额(USDT)
    private String coin_money;

    //手续费
    private String commission;

    //币种代码
    private String coin_code;

    //交易哈希
    private String tx_id;

    //货币金额
    private String currency_amount;

    //提现状态1.审核中2.处理中3.已成功4.已驳回5.已取消6.失败
    private String withdrawal_status;

}