package cn.rh.flash.sdk.shantong;

import cn.rh.flash.bean.entity.dzsys.SmsMessage;
import cn.rh.flash.sdk.sms.bean.SmsResp;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 闪通
 */
@Log4j2
public class HttpStSmS {

    private static OkHttpClient client = new OkHttpClient();

    private static final String SEND = "send";
    private static final String LOOK = "look";

    public static void main(String[] args) {


        // 发短信  注意看  国内国外方法不一样
//         new HttpStSmS().SendSMSCt( "86","18292025140","2345" );

        //查看充值
//        String quotaCt = new HttpStSmS().getQuotaCt();
//        System.out.println( quotaCt );

    }


    /**
     * 海外服务器（包括香港） 发送短信
     *
     * @return
     */
    public SmsResp SendSMS(String phone, String smsMessage, SmsMessage smsMessageObj) {

        Map<String, Object> map = new HashMap<>();
        map.put("command", smsMessageObj.getAppse());     // 短 信
        map.put("cpid", smsMessageObj.getAppid());
        map.put("cppwd", smsMessageObj.getAppkey());
        map.put("da", phone);             // 国际区号+移动号码

        map.put("sm", String.format("[%s] Your verification code is %s", smsMessageObj.getName(), smsMessage));

//        call( "cdb", "http://api2.santo.cc/submit", map ,SEND);
        String cdb = call("cdb", smsMessageObj.getApiUrl(), map, SEND);
//        return JsonUtil.fromJsonFastJSON(SmsResp.class, req);
        return new SmsResp(cdb);
    }

    /**
     * 国内服务器 发送短信
     *
     * @return
     */
    public void SendSMSCt(String code, String phone, String smsMessage, SmsMessage smsMessageObj) {

        Map<String, Object> map = new HashMap<>();
        map.put("command", smsMessageObj.getAppse());     // 短 信
        map.put("cpid", smsMessageObj.getAppid());
        map.put("cppwd", smsMessageObj.getAppkey());
        map.put("da", code + phone);             // 国际区号+移动号码
        map.put("sm", String.format("[%s] Your verification code is %s", code, smsMessage));
        call("cdb", "http://api.santo.cc/submit", map, SEND);
    }


    /**
     * 国内服务器 获取额度
     *
     * @return
     */
    public String getQuotaCt(SmsMessage smsMessageObj) {

        Map<String, Object> map = new HashMap<>();
        map.put("cpid", smsMessageObj.getAppid());
        map.put("cppwd", smsMessageObj.getAppkey());
        return call("cdb", "http://api.santo.cc/get-balance", map, LOOK);
    }

    /**
     * 国内服务器 获取额度
     *
     * @return
     */
    public String getQuota(SmsMessage smsMessageObj) {
        Map<String, Object> map = new HashMap<>();
        map.put("cpid", smsMessageObj.getAppid());
        map.put("cppwd", smsMessageObj.getAppkey());
        return call("cdb", "api2.santo.cc/get-balance", map, LOOK);
    }

    public String call(String header, String url, Map<String, Object> param, String flg) {

        StringBuilder builder = new StringBuilder();

        param.forEach((name, value) -> {
            if (builder.length() != 0) {
                builder.append('&');
            }
            builder.append(name);
            if (value != null) {
                builder.append('=');
                builder.append(value);
            }
        });

        //post请求
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        Request request = new Request.Builder().addHeader("Authorization", header).url(url)
                .post(RequestBody.create(mediaType, builder.toString())).build();

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
                            log.error(flg + "------闪通·三方请求失败------" + returnStr);
                        }
                    case "send":
                        return "ok";
                }
//
                return responseStr;
            } else {
                String returnStr = response.body().string();
                log.error("------闪通·三方请求失败------" + returnStr);
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("------闪通·三方请求失败------" + e.getMessage());
        }
        return "err";
    }

}
