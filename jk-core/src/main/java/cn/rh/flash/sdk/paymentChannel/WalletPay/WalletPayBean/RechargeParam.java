
package cn.rh.flash.sdk.paymentChannel.WalletPay.WalletPayBean;

import lombok.Data;


/**
 * 发起交易订单
 */
@Data
public class RechargeParam {

    //必填 string 用户id, 后台个人信息可获取
    private String user_id;

    //必填 string 货币金额,最大精确度为小数点后两位
    private String currency_money;

    //必填 string 货币代码 USD
    private String currency_code;

    //必填 string 支付币种 USDT.TRC20
    private String coin_code;

    //选填 string 支付成功通知地址
    private String asyn_notice_url;

    //选填 string 订单成功跳转地址
    private String sync_jump_url;

    //必填 string 自定义订单号
    private String user_order_id;

    // 选填 int 支付页面语言类型1.英文，2.简体中文。默认英文
    private Integer language;

    //选填 string 自定义id 方便商户对用户支付进行对账。可以为用户名，也可以为数据库中的用户编
    private String user_custom_id;

    //选填 string 备注
    private String remark;

    public String toJson(){
        return "{\"user_id\":" + user_id +
                ",\"currency_money\":\"" + currency_money +
                "\",\"currency_code\":\"" + currency_code +
                "\",\"coin_code\":\"" + coin_code +
                "\",\"asyn_notice_url\":\"" + asyn_notice_url +
                "\",\"sync_jump_url\":\"" + sync_jump_url +
                "\",\"user_order_id\":\"" + user_order_id  +
                "\",\"language\":" + language +
                ",\"user_custom_id\":\"" + user_custom_id +
                "\",\"remark\":\"" + remark +"\"}";
    }



}