package cn.rh.flash.sdk.paymentChannel.Mpay.dao;

import lombok.Data;

import java.io.Serializable;

@Data
public class BindResult implements Serializable {
    public boolean success;
    public String errMessage;
    public String data;

    public  BindResult(boolean success, String errMessage, String data) {
        this.success = success;
        this.errMessage = errMessage;
        this.data = data;
    }
}

