package cn.rh.flash.sdk.paymentChannel.zimu808Pay.zimu808Bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class ZimuResp implements Serializable {
    //状态值;0代表成功;其他值都为不成功
    private Integer code;
    //状态描述
    private String msg;
    //返回数据
    private ZimuData data;
}
