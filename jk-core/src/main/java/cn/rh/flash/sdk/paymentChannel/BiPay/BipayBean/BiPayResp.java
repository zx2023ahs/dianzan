
package cn.rh.flash.sdk.paymentChannel.BiPay.BipayBean;

import lombok.Data;

import java.util.Date;

@Data
public class BiPayResp {

    private Integer code;
    private String msg;
    private Long timestamp;
    private String data;
    private Date date;

    public Date getDate() {
        return new Date(timestamp);
    }
}