package cn.rh.flash.sdk.sms;

import cn.rh.flash.bean.entity.dzsys.SmsMessage;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.sdk.maiteng.HttpMtSmS;
import cn.rh.flash.sdk.shantong.HttpStSmS;
import cn.rh.flash.sdk.sms.bean.SmsResp;
import cn.rh.flash.sdk.zhongdong.HttpZdSmS;
import cn.rh.flash.service.dzsys.SmsMessageService;
import cn.rh.flash.utils.HttpUtil;
import cn.rh.flash.utils.JsonUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Log4j2
public class SMSUtil {


    @Autowired
    private SmsMessageService smsMessageService;

    //private static final String APPKEY = "q1GjlM84";

    //private static final String SECRETKEY = "nOhQw0OP";

    //private static final String SIGN_NAME = "POWERBANK";

    //private static final String API = "http://api.wftqm.com/api/sms/mtsend";

//    public static void main(String[] args) {
////        sendCode( "639273069531", "123456" );
//    }

    public SmsResp sendCode(String phone, String code) {
        SmsResp smsResp = new SmsResp("00");
        List<SmsMessage> smsMessages = smsMessageService.queryAll(SearchFilter.build("dzstatus", 1));
        for (SmsMessage smsMessage : smsMessages) {
            switch (smsMessage.getPlatformName()) {
                case "su":
                    smsResp = sendCodeSu(phone, code, smsMessage);
                    break;
                case "st":
                    smsResp = new HttpStSmS().SendSMS( phone, code, smsMessage);
                    break;
                case "mt":
                    smsResp = new HttpMtSmS().SendSMS( phone, code, smsMessage);
                    break;
                case "zd":
                    smsResp = new HttpZdSmS().SendSMS( phone, code, smsMessage);
                    break;
                case "zdc": // zd 跟zdc 是一家短信供应商 只修改企业编号跟密钥
                    smsResp = new HttpZdSmS().SendSMS( phone, code, smsMessage);
                    break;
                default:
                    log.error("短信服务已禁用");
                    return new SmsResp("00");
            }
        }
        return smsResp;
    }

    public SmsResp sendCodeSu(String phone, String code, SmsMessage smsMessage) {
        // 启用状态
        Map<String, String> params = new HashMap<>();
        params.put("appkey", smsMessage.getAppkey());
        params.put("secretkey", smsMessage.getAppse());
        params.put("phone", phone);
        params.put("content", String.format("[%s] Your verification code is %s", smsMessage.getName(), code));
        String req = null;
        try {
            req = HttpUtil.doPost(smsMessage.getApiUrl(), params);
        } catch (Exception e) {
            if (StringUtils.isEmpty(req)) {
                log.error("远端短信服务请求失败sms!");
                return new SmsResp("0");
            } else {
                log.error(req);
            }
            return null;
        }
        return JsonUtil.fromJsonFastJSON(SmsResp.class, req);

    }
}