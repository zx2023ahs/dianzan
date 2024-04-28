package cn.rh.flash.sdk.paymentChannel.Mpay.dao;

import lombok.Data;

import java.io.Serializable;
@Data
public class UpRequestResult implements Serializable {
    public int code;
    public String errMessage;
    public UrlAndId data;

    public UpRequestResult(int code, String errMessage, UrlAndId data) {
        this.code = code;
        this.errMessage = errMessage;
        this.data = data;
    }
}
