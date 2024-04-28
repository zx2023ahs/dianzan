package cn.rh.flash.sdk.paymentChannel.Mpay.mpayBean;

import lombok.Data;

import java.io.Serializable;

@Data
public class MPayWithdrawResp implements Serializable {
    //商户提交的上发单
    private String merchOrderId;
    //下发单状态，“finish”成功，“pending”进行中，“fail”失
    private String status;
    //上发单金额
    private String amount;
    //签名
    private String sign;

    public String toSign(String apikey) {
        return  "amount=" + amount +
                "&merchOrderId=" + merchOrderId +
                "&status=" + status +
                apikey ;
    }
}
