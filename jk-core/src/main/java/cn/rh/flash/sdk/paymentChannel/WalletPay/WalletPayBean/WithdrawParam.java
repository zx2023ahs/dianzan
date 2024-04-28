package cn.rh.flash.sdk.paymentChannel.WalletPay.WalletPayBean;


import lombok.Data;

@Data
public class WithdrawParam {

    //必填 int 用户id,后台个人信息可获取
    private String user_id;

    ////必填 string 自定义订单号
    private String user_withdrawal_id;

    //选填 string 自定义id,设置其他id
    private String user_custom_id;

    //必填string 提现地址
    private String withdrawal_address;

    //必填string 货币代码
    private String currency_code;

    //必填string 币种代码
    private String coin_code;

    //必填 string 货币金额,最大精确度为小数点后两位
    private String currency_amount;

    //必填 string代付订单状态成功 通知地址
    private String asyn_notice_url;

    //选填 string 备注
    private String remark;

    public String toJson(){
        return "{\"user_id\":" + user_id + ",\"user_withdrawal_id\":\"" + user_withdrawal_id +
                "\",\"withdrawal_address\":\"" + withdrawal_address +
                "\",\"user_custom_id\":\"" + user_custom_id +
                "\",\"currency_code\":\"" + currency_code +
                "\",\"coin_code\":\"" + coin_code +
                "\",\"currency_amount\":\"" + currency_amount  +
                "\",\"asyn_notice_url\":\"" + asyn_notice_url +
                "\",\"remark\":\"" + remark +"\"}";
    }
}
