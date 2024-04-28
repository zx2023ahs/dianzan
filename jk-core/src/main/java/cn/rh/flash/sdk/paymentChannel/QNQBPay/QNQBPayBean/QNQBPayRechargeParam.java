package cn.rh.flash.sdk.paymentChannel.QNQBPay.QNQBPayBean;

import cn.rh.flash.utils.StringUtil;
import lombok.Data;

import java.io.Serializable;

@Data
public class QNQBPayRechargeParam implements Serializable {
    //商户ID
    private String recvid;
    //商户订单号
    private String orderid;
    //支付金额（以元为单位）
    private String amount;
    //备注/透传参数
    private String note;
    //回调地址
    private String notifyurl;
    //签名值（32位字母小写）;
    // 示例：sign=md5(recvid+orderid+amount+apikey)
    private String sign;


//    public String toSignDate(String apikey) {
//            return "recvid=" + recvid +
//                    "&bankCode=" + bankCode +
//                    "&merchantId=" + merchantId +
//                    "&merchantOrderNo=" + merchantOrderNo +
//                    "&model=" + model +
//                    "&notifyUrl=" + notifyUrl +
//    }

}
