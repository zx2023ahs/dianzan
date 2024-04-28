package cn.rh.flash.sdk.paymentChannel.OKPay.OKPayBean;

import cn.rh.flash.utils.StringUtil;
import lombok.Data;

import java.io.Serializable;
//创建支付参数
@Data
public class OKRechargeParam implements Serializable {
    //商户ID
    private String recvid;
    //商户订单号
    private String orderid;
    //支付金额（以元为单位）
    private String amount;
    //回调地址
    private String notifyurl;
    //签名值（32位字母大写）;
    // 示例：MD5(orderCode&amount&payType&userCode&key)
    private String sign;


    public String toSignDate(String apikey) {
            return recvid+orderid+amount+apikey;
        }
    }

