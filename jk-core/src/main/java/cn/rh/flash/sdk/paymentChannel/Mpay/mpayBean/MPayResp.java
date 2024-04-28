package cn.rh.flash.sdk.paymentChannel.Mpay.mpayBean;

import lombok.Data;

import java.io.Serializable;

//MPay返回参数
@Data
public class MPayResp implements Serializable {
    //状态值;200代表成功;其他值都为不成功
    private Integer code;
    //状态描述
    private String msg;
    //返回数据
    private MPayData data;

}