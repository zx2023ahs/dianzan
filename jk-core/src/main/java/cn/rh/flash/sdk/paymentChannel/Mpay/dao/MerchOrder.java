package cn.rh.flash.sdk.paymentChannel.Mpay.dao;

import lombok.Data;

import java.io.Serializable;
@Data
public class MerchOrder implements Serializable {
    public boolean success;
    public String errMessage;
    public MerchOrderInfo data;

    public  MerchOrder(boolean success, String errMessage, MerchOrderInfo data) {
        this.success = success;
        this.errMessage = errMessage;
        this.data = data;
    }
}
