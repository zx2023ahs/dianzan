package cn.rh.flash.api.interceptor;

import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.exception.ApiException;
import cn.rh.flash.utils.MD5;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author jk
 * @data 2020年9月27日 23点47分
 * <th>拦截app请求进行签名认证</th>
 */
@Component
public class MyApiInterceptor implements HandlerInterceptor {


    private static final int EXPIRATION = 15; // 15分钟

    private Map<String, String> ksys() {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("b5a51391f271f062867e5984sddffs", "B5A51391F271F0628E5984E2FCFFEE");
        return stringStringHashMap;
    }

    //请求之前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String sign = null;

        Map<String, String> params = new HashMap<String, String>();
        /* 获取全部参数 */
        Enumeration<?> pNames = request.getParameterNames();
        while (pNames.hasMoreElements()) {
            String pName = (String) pNames.nextElement();
            String pValue = request.getParameter(pName);
            if ("sign".equals(pName)) {
                /* 获取签名 */
                sign = pValue;
                continue;
            }
            params.put(pName, pValue);
        }

        String timestamp = params.get("timestamp");   //随机数
        String AccessKey = params.get("AccessKey");   //公钥

        // 校验签名前后过期时间
        checkSignTime(timestamp);

        //公钥获取密钥
        String SecretKey = ksys().get(AccessKey);

        // 验证签名
        checkSign(params, sign, SecretKey);


        return true;
    }


    //请求时
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    //请求完成
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    /**
     * 校验签名前后过期时间  15分钟
     */
    private void checkSignTime(String timestamp) throws RuntimeException {
        if (StringUtils.isBlank(timestamp)) {
            //throw new Exception("timestamp 参数不能是空");
            throw new ApiException(MessageTemplateEnum.TIMESTAMP_CANNOT_EMPTY);

        }
        long currentTime = System.currentTimeMillis() / 1000;// 统一都传毫秒
        long requestTime = 0;
        try {
            requestTime = Long.parseLong(timestamp) / 1000;
        } catch (NumberFormatException e) {
            throw new ApiException(MessageTemplateEnum.TIMESTAMP_ABNORMAL_FORMAT);
            //throw new Exception("签名参数异常{timestamp="+timestamp+"}");
        }

        if ( Math.abs(currentTime - requestTime) > (EXPIRATION * 60)) {
            // throw new Exception("签名已过期");
            throw new ApiException(MessageTemplateEnum.SIGN_EXPIRED);
        }
    }


    /**
     * @param SecretKey 密钥
     * @param sign      待验证的签名
     */
    private void checkSign(Map<String, String> params, String SecretKey, String sign) throws RuntimeException {
        if (StringUtils.isBlank(sign)) {
            //throw new Exception("签名不能为空");
            throw new ApiException(MessageTemplateEnum.SIGN_CANNOT_EMPTY);
        }
        String _sign = createSign(params, SecretKey);
        if (sign.equalsIgnoreCase(_sign)) {
            //throw new Exception("签名验证失败(-1)");
            throw new ApiException(MessageTemplateEnum.SIGN_ERROR);
        }
    }


    /**
     * Eg:
     * Map<String, String> params = new HashMap<>();
     * params.put("AccessKey","AccessKey值");    //AccessKey 公钥
     * params.put("你的参数","你的参数值");
     * params.put("你的参数n","你的参数值n");
     * params.put("timestamp",new Date().getTime()+"");
     *
     * @param params    Eg
     * @param SecretKey 密钥
     * @return 返回签名
     * TODO 创建签名
     * @a
     */
    private String createSign(Map<String, String> params, String SecretKey) {

        Set<String> keysSet = params.keySet();
        Object[] keys = keysSet.toArray();
        Arrays.sort(keys);
        StringBuffer temp = new StringBuffer();
        boolean first = true;
        for (Object key : keys) {
            if (first) {
                first = false;
            } else {
                temp.append("&");
            }
            temp.append(key).append("=");
            Object value = params.get(key);
            String valueString = "";
            if (null != value) {
                valueString = String.valueOf(value);
            }
            temp.append(valueString);

        }
        temp.append("&SecretKey=" + SecretKey);
        return MD5.md5(temp.toString(),"").toUpperCase();
    }


}
