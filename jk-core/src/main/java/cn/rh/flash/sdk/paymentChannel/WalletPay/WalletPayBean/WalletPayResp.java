package cn.rh.flash.sdk.paymentChannel.WalletPay.WalletPayBean;

import lombok.Data;

@Data
public class WalletPayResp {

    private Integer code;
    private String msg;
    private String message;
    private String sign;
    private Long timestamp;
    private String data;

}
