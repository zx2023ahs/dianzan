package cn.rh.flash.sdk.sms.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmsResp {
    private String result;
    private String messageid;
    private String code;

    public SmsResp(String s) {
        code = "0";
        messageid = s; // TODO 1 表示请求超时
    }
}
