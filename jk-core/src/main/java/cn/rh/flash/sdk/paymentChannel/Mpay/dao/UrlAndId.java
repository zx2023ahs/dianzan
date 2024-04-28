package cn.rh.flash.sdk.paymentChannel.Mpay.dao;

import lombok.Data;

import java.io.Serializable;
@Data
public class UrlAndId implements Serializable {
    public String url;
    public String id;

    public UrlAndId(String url, String id) {
        this.url = url;
        this.id = id;
    }
}
