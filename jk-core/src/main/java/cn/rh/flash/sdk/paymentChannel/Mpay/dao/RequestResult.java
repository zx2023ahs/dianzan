package cn.rh.flash.sdk.paymentChannel.Mpay.dao;


import lombok.Data;

import java.io.Serializable;
@Data
public class RequestResult implements Serializable {
    public int code;
    public String errMessage;
    public String data;

    public  RequestResult(int code, String errMessage, String data) {
        this.code = code;
        this.errMessage = errMessage;
        this.data = data;
    }
}