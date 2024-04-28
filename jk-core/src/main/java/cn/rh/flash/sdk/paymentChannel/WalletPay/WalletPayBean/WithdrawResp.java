
package cn.rh.flash.sdk.paymentChannel.WalletPay.WalletPayBean;

import lombok.Data;


@Data
public class WithdrawResp {

    //代付id
    private String withdrawal_id;

    //自定义订单号
    private String user_withdrawal_id;

    //提现地址
    private String withdrawal_address;

    //金额(USDT)
    private String coin_money;

    //手续费
    private String commission;

    //币种代码
    private String coin_code;

    //货币代码
    private String currency_code;

    public String toJson(){
        return "{\"withdrawal_id\":" + withdrawal_id + ",\"user_withdrawal_id\":\"" + user_withdrawal_id +
                "\",\"withdrawal_address\":\"" + withdrawal_address +
                "\",\"coin_money\":\"" + coin_money +
                "\",\"commission\":\"" + commission +
                "\",\"coin_code\":\"" + coin_code +
                "\",\"currency_code\":\"" + currency_code +"\"}";
    }
}