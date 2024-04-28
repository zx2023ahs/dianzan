package cn.rh.flash.sdk.paymentChannel.Mpay.dao;

import lombok.Data;

import java.io.Serializable;
@Data
public class Down implements Serializable {
    public boolean success;
    public String errMessage;
    public StatusAndId data;

    public Down(boolean success, String errMessage, StatusAndId data) {
        this.success = success;
        this.errMessage = errMessage;
        this.data = data;
    }
}