package cn.rh.flash.sdk.paymentChannel.FPay.FPayBean;

import lombok.Data;

import java.io.Serializable;

@Data
public class FPayWithdrawResp implements Serializable {
    //下单金额
    private String amount;
    //签名值（32位字母大写）;
    //示例：MD5(orderCode&amount&userCode&status&key)
    private String sign;
    //订单号
    private String orderid;
    //平台订单号
    private String id;
    //商户号
    private String merchantid;
    //支付状态 2 成功
    private String state;
    //手续费
    private String charge;
    //订单创建时间
    private String addtime;
    //支付时间
    private String endtime;
    //商户备注 如果为空，不参与签名
    private String remark;
    //异步通知回掉地址
    private String notify_url;
    //收款方钱包地址
    private String address;

    public String toSign(String apikey) {
        return  "address=" + address +
                "&addtime=" + addtime +
                "&amount=" + amount +
                "&charge=" + charge +
                "&endtime=" + endtime +
                "&id=" + id +
                "&merchantid=" + merchantid +
                "&notify_url=" + notify_url +
                "&orderid=" + orderid +
                "&state=" + state +
                apikey ;
    }
}
