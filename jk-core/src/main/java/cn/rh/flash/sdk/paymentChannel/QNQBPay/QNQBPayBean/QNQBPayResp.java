package cn.rh.flash.sdk.paymentChannel.QNQBPay.QNQBPayBean;

import lombok.Data;

import java.io.Serializable;

//KDPay返回参数
@Data
public class QNQBPayResp implements Serializable {
    //状态值;0代表成功;其他值都为不成功
    public int errcode;
    //响应信息描述
    public String errmsg;
    //支付参数（data参数需进行json decode 处理）
    public QNQBPayData data;




}
