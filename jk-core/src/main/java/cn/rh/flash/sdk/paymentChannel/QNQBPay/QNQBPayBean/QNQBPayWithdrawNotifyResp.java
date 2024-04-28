package cn.rh.flash.sdk.paymentChannel.QNQBPay.QNQBPayBean;

import lombok.Data;

import java.io.Serializable;

@Data
public class QNQBPayWithdrawNotifyResp implements Serializable {
    //商户编号
    private String recvid;
    //商户订单号
    private String orderid;
    //订单状态	1为成功
    private String status;
    //下发币数量	可以带小数
    private String amount;
    //sign=md5(recvid&orderid&amount&apikey)
    private String sign;


}
