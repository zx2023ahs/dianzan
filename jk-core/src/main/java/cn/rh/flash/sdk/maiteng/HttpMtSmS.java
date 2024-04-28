package cn.rh.flash.sdk.maiteng;

import cn.rh.flash.bean.entity.dzsys.SmsMessage;
import cn.rh.flash.sdk.sms.bean.SmsResp;
import cn.rh.flash.utils.DateUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * 迈腾
 */
@Log4j2
public class HttpMtSmS {

    /**
     * App Key：
     * SqAf+HOU+GlcEm6zs2WxHA==
     * Secret Key：
     * 261b2cb257264ea4a9c8e5a9a99ecd67
     * http://47.243.160.107:8855/
     * 9763586054
     * qqww1122
     */

    private static OkHttpClient client = new OkHttpClient();

    private static final String SEND = "send";
    private static final String LOOK = "look";

//    public static void main(String[] args) {
//
//        JSONObject map = new JSONObject();
//        map.put("apikey", "SqAf+HOU+GlcEm6zs2WxHA==");
//        // 获取东八区时区
//        String dateString = DateUtil.getFormatDateString(8, "yyyyMMddHHmmss");
//        map.put("timestamp", dateString);
//        String sign = Sign.getSignValue("SqAf+HOU+GlcEm6zs2WxHA==" + dateString + "261b2cb257264ea4a9c8e5a9a99ecd67");
//        map.put("sign", sign);
//
//        map.put("mobile", "12052435640");             // 国际区号+移动号码
//        map.put("content", String.format("[%s] Your verification code is %s", "迈腾", "123456"));
//        String cdb = call("cdb", "https://api.230sms.com/outauth/verifCodeSend", map, SEND);
//
//    }


    /**
     * 迈腾发送短信
     * @return
     */
    public SmsResp SendSMS(String phone, String code, SmsMessage smsMessageObj) {

        JSONObject map = new JSONObject();
        map.put("apikey", smsMessageObj.getAppkey());
        // 获取东八区时区
        String dateString = DateUtil.getFormatDateString(8, "yyyyMMddHHmmss");
        map.put("timestamp", dateString);
        map.put("sign", Sign.getSignValue(smsMessageObj.getAppkey() + dateString + smsMessageObj.getAppse()));
        map.put("mobile", phone);             // 国际区号+移动号码
        map.put("content", String.format("[%s] Your verification code is %s", smsMessageObj.getName(), code));

        String cdb = call("cdb", smsMessageObj.getApiUrl(), map, SEND);
        return new SmsResp(cdb);
    }

    public static String call(String header, String url,JSONObject param, String flg) {

        //post请求
        MediaType mediaType = MediaType.parse("application/json;charset=UTF-8");
        Request request = new Request.Builder().addHeader("Authorization", header).url(url)
                .post(RequestBody.create(mediaType, param.toString())).build();

        String responseStr = "";
        try {
            Response response = client.newCall(request).execute();
            if (response.code() == 200) {
                //修改回调状态
                String returnStr = response.body().string();
                System.out.println(returnStr);


                switch (flg) {
                    case "look":
                        JSONObject parse = JSONObject.parseObject(returnStr);
                        if (parse.getInteger("errcode").equals(000)) {
                            parse = JSONObject.parseObject(returnStr);
                            BigDecimal balance = parse.getBigDecimal("balance");
                            return balance.toString();
                        } else {
                            log.error(flg + "------迈腾·三方请求失败------" + returnStr);
                        }
                    case "send":
                        return "ok";
                }
//
                return responseStr;
            } else {
                String returnStr = response.body().string();
                log.error("------迈腾·三方请求失败------" + returnStr);
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("------迈腾·三方请求失败------" + e.getMessage());
        }
        return "err";
    }

}
