
package cn.rh.flash.sdk.paymentChannel.BiPay.BipayBean;

import lombok.Data;

@Data
public class WithdrawParam {

    //必填 string 用户id, 后台个人信息可获取
    private String userId;

    //必填 string 金额(USDT) ,最大精确度为小数点后两位
    private String amount;

    //必填 string 提现地址，Tron钱包地址
    private String address;

    //必填 string 代付订单状态改变 通知地址
    private String notifyUrl;

    //必填 string 自定义订单号
    private String customOrderId;

    //必填 string 签名
    private String sign;

    //选填 string 备注
    private String instruction;

}