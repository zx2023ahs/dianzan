package cn.rh.flash.sdk.paymentChannel.QNQBPay.QNQBPayBean;

import lombok.Data;

import java.io.Serializable;

//返回参数
@Data
public class QNQBPayData implements Serializable {
    //订单金额
    public String amount;
    //创建时间
    public String createtime;
    //订单ID
    public String id;
    //商户订单号
    public String orderid;
    //商户ID（由开户资料获取）
    public String recvid;
    //订单备注
    public String remark;
    //支付地址（可直接打开跳转）
    public String navurl;
}