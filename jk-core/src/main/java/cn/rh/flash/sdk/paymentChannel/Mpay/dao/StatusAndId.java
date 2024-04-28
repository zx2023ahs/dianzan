package cn.rh.flash.sdk.paymentChannel.Mpay.dao;

import lombok.Data;

import java.io.Serializable;
@Data
public class StatusAndId implements Serializable {
    public String status;
    public String id;

    public StatusAndId(String status, String id) {
        this.status = status;
        this.id = id;
    }
}
