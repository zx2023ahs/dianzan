package cn.rh.flash.sdk.paymentChannel.CBPay.CBPayBean;

import lombok.Data;

import java.io.Serializable;

@Data
public class CBPayWithdrawResp implements Serializable {
    //下单金额
    private String amount;
    //签名值（32位字母大写）;
    //示例：MD5(orderCode&customerOrderCode&amount&userCode&status&key)
    private String sign;
    //订单号
    private String orderCode;
    //下发时间，格式：yyyy-MM-dd HH:mm:ss
    private String remitTime;
    //业务订单号（下发时提交的orderCode）
    private String customerOrderCode;
    //商户号
    private String userCode;
    //下发状态 1 初始 2 成功 3 失败
    private String status;

    public String toSign(String key){
        return  orderCode+ "&" +
                customerOrderCode+ "&" +
                amount+ "&" +
                userCode+ "&" +
                status+ "&" +
                key  ;
    }
}
