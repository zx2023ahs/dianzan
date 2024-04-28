package cn.rh.flash.sdk.paymentChannel.Mpay.mpayBean;

import lombok.Data;

import java.io.Serializable;

@Data
public class MPayNotifyResp implements Serializable {
    //商户提交的上发单
    private String merchOrderId;
    //上发单状态，“true”成功，“false”失败
    private String status;
    //上发单金额
    private Integer amount;
    //签名
    private String sign;

    public String toSign(String apikey) {
        return  "amount=" + amount +
                "&merchOrderId=" + merchOrderId +
                "&status=" + status +
                apikey ;
    }
}
