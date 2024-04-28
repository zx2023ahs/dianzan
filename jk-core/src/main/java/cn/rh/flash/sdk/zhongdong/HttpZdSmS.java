package cn.rh.flash.sdk.zhongdong;

import cn.rh.flash.bean.entity.dzsys.SmsMessage;
import cn.rh.flash.sdk.sms.bean.SmsResp;
import cn.rh.flash.utils.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import org.springframework.web.util.UriUtils;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 中东短信
 */
@Log4j2
public class HttpZdSmS {

    private static OkHttpClient client = new OkHttpClient().newBuilder().build();

    private static final String SEND = "send";
    private static final String LOOK = "look";

//    public static void main(String[] args) {
//        String s = Sign.getSignValue("RZFebmzq" + "测试短信" + "111111" + "ZLZRWBXIAHSOYLQDRMDIWPALDDWLKWVX").toUpperCase();
//        System.out.println(s);
//    }


//        public static void main(String []args){
//
//            Map<String, String> map = new HashMap<>();
//            map.put("orgCode","RZFebmzq");  // 企业编号
//            map.put("mobileArea","+0");  // 号码区域 固定值：+0
//            map.put("mobiles", "639560636670".trim() ); // 国际区号+移动号码
//            String content = "测试短信";//String.format("[%s] Your verification code is %s", "MASDAS", "111111");
//            String encode = UriUtils.encode(UriUtils.encode(content, "utf8"), "utf8");
//            map.put("content", encode);
//            map.put("rand","111111");  // 6位随机数
//            //map.put("sign",Sign.getSignValue(map.get("orgCode")+content+map.get("rand")+"ZLZRWBXIAHSOYLQDRMDIWPALDDWLKWVX").toUpperCase());  // 密钥
//            map.put("sign","5E63C44AC9DC42EE1FFBE67CE9464735");
//
//            String s = HttpUtil.doPost("http://smsapi.abosend.com:8205/api/sendSMS", map);
//        }

    /**
     *  发送短信
     *
     * @return
     */
    public SmsResp SendSMS(String phone, String smsMessage, SmsMessage smsMessageObj) {

        Map<String, String> map = new LinkedHashMap<>();
        map.put("orgCode",smsMessageObj.getAppid() );  // 企业编号
        map.put("mobileArea","+0");  // 号码区域 固定值：+0
        map.put("rand",smsMessage);  // 6位随机数
        map.put("mobiles", phone); // 国际区号+移动号码
        String content = String.format("[%s] Your verification code is %s", smsMessageObj.getName(), smsMessage);
        String encode = UriUtils.encode(UriUtils.encode(content, "utf8"), "utf8");
        map.put("content", encode);
        map.put("sign",Sign.getSignValue(map.get("orgCode")+content+smsMessage+smsMessageObj.getAppkey()).toUpperCase());  // 密钥
        String cdb = call("cdb", smsMessageObj.getApiUrl(), map, SEND);
        return new SmsResp(cdb);
    }



    public String call(String header, String url, Map<String, String> param, String flg) {


        try {

            String returnStr = HttpUtil.doPost(url, param);
            JSONObject response = JSONObject.parseObject(returnStr);
            if (response.getInteger("code") == 200) {
                //修改回调状态
                System.out.println(returnStr);
                switch (flg) {
                    case "look":
                        JSONObject parse = JSONObject.parseObject(returnStr);
                        if (parse.getInteger("errcode").equals(000)) {
                            parse = JSONObject.parseObject(returnStr);
                            BigDecimal balance = parse.getBigDecimal("balance");
                            return balance.toString();
                        } else {
                            log.error(flg + "------中东·三方请求失败------" + returnStr);
                        }
                    case "send":
                        return "ok";
                }
//
                return returnStr;
            } else {
                log.error("------中东·三方请求失败------" + returnStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("------中东·三方请求失败------" + e.getMessage());
        }
        return "err";
    }

}
