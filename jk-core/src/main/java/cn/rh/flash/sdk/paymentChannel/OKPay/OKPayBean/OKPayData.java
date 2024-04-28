package cn.rh.flash.sdk.paymentChannel.OKPay.OKPayBean;

import lombok.Data;

import java.io.Serializable;

//返回参数
//    订单类型枚举类型：ordertype
//
//    PayOrder   	2      	//充值订单
//    WdOrder   	3       	//下发订单
//
//    订单状态枚举类型:  state
//
//    Created  	 1       //已创建
//    Transed	4      //已转币
//    Canceled    	8      //已取消
//    Error  	 	99      //错误
@Data
public class OKPayData implements Serializable {
    private int code = 1 ;          //返回代码，非1表示错误代码
    private String msg = "success"; //当code==1时，返回success，当code！=1时返回错误信息
    private String data;    	//序列化后实体类的json字符串
}