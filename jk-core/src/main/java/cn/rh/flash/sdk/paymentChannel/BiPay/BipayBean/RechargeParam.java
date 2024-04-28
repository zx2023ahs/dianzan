
package cn.rh.flash.sdk.paymentChannel.BiPay.BipayBean;

import lombok.Data;

@Data
public class RechargeParam {

    //必填 string 用户id, 后台个人信息可获取
    private String userId;
    //必填 string 货币金额,最大精确度为小数点后两位
    private String amount;
    //必填 string 货币代码 USD
    private String currency;
    //必填 string 支付币种 USDT.TRC20   USDT.Polygon                USDT.TRC20
    private String coinCode;
    //必填 string 支付成功通知地址
    private String notifyUrl;
    //必填 string 自定义订单号
    private String customOrderId;
    //必填 string 签名
    private String sign;
    //选填 string 自定义id 方便商户对用户支付进行对账。可以为用户名，也可以为数据库中的用户编
    private String customId;
//    //选填 string 备注
//    private String remarks;
//    //选填 string 订单成功跳转地址
//    private String redirectUrl;


}