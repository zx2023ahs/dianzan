package cn.rh.flash.sdk.paymentChannel.Mpay.dao;

import lombok.Data;

import java.io.Serializable;
@Data
public class MerchOrderResult implements Serializable {
    public int code;
    public String errMessage;
    public MerchOrderInfo data;

    public MerchOrderResult(int code, String errMessage, MerchOrderInfo data) {
        this.code = code;
        this.errMessage = errMessage;
        this.data = data;
    }
}