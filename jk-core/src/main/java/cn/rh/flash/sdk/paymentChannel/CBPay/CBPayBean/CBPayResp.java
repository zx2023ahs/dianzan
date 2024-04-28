package cn.rh.flash.sdk.paymentChannel.CBPay.CBPayBean;

import lombok.Data;

import java.io.Serializable;

//KDPay返回参数
@Data
public class CBPayResp implements Serializable {
    //状态值;200代表成功;其他值都为不成功
    private Integer code;
    //状态描述
    private String message;
    //返回数据
    private CBPayData data;

}