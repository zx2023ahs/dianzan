package cn.rh.flash.sdk.paymentChannel.Mpay.mpayBean;

import lombok.Data;

import java.io.Serializable;

@Data
public class MPayRechargeParam implements Serializable {
    //商户号
    private Integer merchId;
    //加密参数    用公钥将jsonString 进行RSA加密生成body
    private String body;
    //时间戳
    private Integer t;
    //签名Md5(merchId+body+t+md5key)
    private String key;

}
