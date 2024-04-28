package cn.rh.flash.sdk.paymentChannel.Mpay.dao;

import lombok.Data;

import java.io.Serializable;
@Data
public class DownRequestResult implements Serializable {
    public int code;
    public String errMessage;
    public StatusAndId data;

    public DownRequestResult(int code, String errMessage, StatusAndId data) {
        this.code = code;
        this.errMessage = errMessage;
        this.data = data;
    }
}