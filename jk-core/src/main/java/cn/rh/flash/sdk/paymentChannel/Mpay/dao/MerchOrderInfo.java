package cn.rh.flash.sdk.paymentChannel.Mpay.dao;

import lombok.Data;

import java.io.Serializable;
@Data
public class MerchOrderInfo implements Serializable {
    public String merchOrderId;
    public float amount;
    public String userPayAddress;
    public int merchId;
    public String createTime;
    public String status;

    public MerchOrderInfo(String merchOrderId, float amount, String userPayAddress, int merchId, String createTime, String status) {
        this.merchOrderId = merchOrderId;
        this.amount = amount;
        this.userPayAddress = userPayAddress;
        this.merchId = merchId;
        this.createTime = createTime;
        this.status = status;
    }
}

