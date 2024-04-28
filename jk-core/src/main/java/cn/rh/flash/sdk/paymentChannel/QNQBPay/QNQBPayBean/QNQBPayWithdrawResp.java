package cn.rh.flash.sdk.paymentChannel.QNQBPay.QNQBPayBean;

import lombok.Data;

import java.io.Serializable;

@Data
public class QNQBPayWithdrawResp implements Serializable {
    //响应状态码	0为请求成功 ，其他值为失败
    private String errcode;
    //
    public QNQBPayWithdrawRespData data;
    //
    private String errmsg;

}
