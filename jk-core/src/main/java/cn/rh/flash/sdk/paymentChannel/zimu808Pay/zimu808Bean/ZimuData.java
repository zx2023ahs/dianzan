package cn.rh.flash.sdk.paymentChannel.zimu808Pay.zimu808Bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class ZimuData implements Serializable {

    //808平台订单号
    private String orderNo;
    //支付地址二维码
    private String payUrl;
    //状态 1待处理（当应用开启提现金额二次确认时） 2成功
    private String status;
}
