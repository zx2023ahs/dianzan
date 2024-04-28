package cn.rh.flash.sdk.paymentChannel.Mpay.mpayBean;

import lombok.Data;

import java.io.Serializable;

@Data
public class MPayWithdrawParam implements Serializable {
    //商户号
    private String merchantid;
    //订单号
    private String orderid;
    //下发金额（以元为单位）
    private String amount;
    //接收钱包地址
    private String address;
    //回调地址。
    private String notify_url;
    //商户备注，回调时原样返回
    private String remark;
    //签名值（32位字母大写）
    //示例：MD5(orderCode&amount&address&userCode&key)
    private String sign;

    @Override
    public String toString() {
        return  "merchantid=" + merchantid +
                "&orderid=" + orderid +
                "&amount=" + amount +
                "&address=" + address +
                "&notify_url=" + notify_url +
                "&sign=" + sign ;
    }

    public String toSign(String key){
        return  "address=" + address +
                "&amount=" + amount +
                "&merchantid=" + merchantid +
                "&notify_url=" + notify_url +
                "&orderid=" + orderid +
                 key;
    }
}
