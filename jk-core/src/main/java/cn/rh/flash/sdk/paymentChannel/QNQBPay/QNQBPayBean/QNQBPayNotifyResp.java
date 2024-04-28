package cn.rh.flash.sdk.paymentChannel.QNQBPay.QNQBPayBean;

import lombok.Data;

import java.io.Serializable;

@Data
public class QNQBPayNotifyResp implements Serializable {
    //编号
    private String id;
    //商户订单id
    private String orderid;
    //订单状态1 成功
    private String status;
    //金额
    private String amount;
    //平台订单号
    private String trade_num;
    //透传参数， 订单回调时原样返回
    private String note;
    //签名
    private String sign;
    //商户好
    private String recvid;
    //回调签名   retsign=md5(sign+apikey) 这个参数只有在回调的数据中有。
    private String retsign;
}
