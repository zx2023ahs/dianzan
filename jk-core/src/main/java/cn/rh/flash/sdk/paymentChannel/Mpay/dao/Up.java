package cn.rh.flash.sdk.paymentChannel.Mpay.dao;

import lombok.Data;

import java.io.Serializable;
@Data
public class Up implements Serializable {
    public boolean success;
    public String errMessage;
    public UrlAndId data;

    public Up(boolean success, String errMessage, UrlAndId data) {
        this.success = success;
        this.errMessage = errMessage;
        this.data = data;
    }
}
