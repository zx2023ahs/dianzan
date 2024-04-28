package cn.rh.flash.sdk.paymentChannel.OKPay.OKPayBean;

import lombok.Data;

@Data
public class OKPayOrderDto {
    private String id;         	//编号
    private String orderid;        //商户订单号
    private String ordertype;     	//订单类型
    private String state;         	//订单状态
    private String sendid;     	//出币方
    private String recvid;     	//收币方
    private String amount;     	//币数量
    private String sendcharge; 	//出币手续费
    private String recvcharge; 	//收币手续费
    private String createtime; 	//创建日期
    private String transtime;  	//转币日期
    private String qrurl;       	//二维码地址
    private String notifyurl;   	//回调地址
    private String qrcode;        //二维码内容
    private String returnurl;   	//前台跳转地址
    private String note;       	//说明
    private String remark;     	//系统备注
    private String sign;        	//签名
    private String retsign;        //回调签名 创建返回时为空，服务器回调时有值
    private String navurl;        //H5支付页面
}
