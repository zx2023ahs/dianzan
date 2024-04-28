package cn.rh.flash.sdk.paymentChannel.Mpay.mpayBean;

import lombok.Data;

import java.io.Serializable;

//mpay请求参数body
@Data
public class MPayData implements Serializable {
    //body生成规则： `1. base64Decode 公钥 `2. 将表四中参数转为 jsonString
    //
    // 3. 用公钥将jsonString 进行RSA加密（ECB模式，OAEP SHA256对⻬，MGF1填充 ）
    //
    // 4. 加密结果base64 Encode即为字符串body
    //商户订单号
    private String merchOrderId;
    //商户号
    private String merchantid;
    //amount
    private Integer amount;
    //备注
    private String remark;
    //非必要，商户会有默认的货币，正常为CNY
    private String currency;
    //用户自定义回调接口地址
    private String callBackUrl;
    //支付后同步跳转地址非必要，用户扫码支付完成后跳转该网址
    private String returnUrl;
    //商户的用户ID
    private String merchUserId;
    //支付标题
    private String title;


}