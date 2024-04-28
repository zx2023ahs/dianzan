package cn.rh.flash.utils;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;

/**
 * 封装json工具类<br>
 * 通过该类减少项目中对特定的json库依赖，方便统一切换json库，目前使用jackson
 */
public class JsonUtil {
    private static ObjectMapper objectMapper = null;

    private static ObjectMapper objectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            //如果有特殊处理需求在下面做ObjectMapper的设置

        }
        return objectMapper;
    }

    public static String toJsonForHuman(Object obj) {

        try {
            return objectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJson(Object obj) {

        if( obj instanceof String ){
            return (String) obj;
        }
        StringWriter sw = new StringWriter();
        try {
            objectMapper().writeValue(sw, obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sw.toString();
    }

    public static String toJsonNotNull(Object obj) {
        StringWriter sw = new StringWriter();

        try {
            objectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
            objectMapper().writeValue(sw, obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sw.toString();
    }

    public static <T> T fromJson(Class<T> klass, String jsonStr) {

        T obj = null;
        try {
            obj = objectMapper().readValue(jsonStr, klass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    public static <T> T fromJsonFastJSON(Class<T> klass, String jsonStr) {

        T obj = null;
        try {
            obj = JSON.toJavaObject(JSON.parseObject(jsonStr), klass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    public static <T> List<T> fromJsonAsList(Class<T> klass, String jsonStr) {

        List<T> objList = null;
        try {
            JavaType t = objectMapper().getTypeFactory().constructParametricType(
                    List.class, klass);
            objList = objectMapper().readValue(jsonStr, t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return objList;
    }

    /**
     * 判断给定的字符串是否是json格式
    * @param jsonStr
     * @return
     */
    public static boolean isJson(String jsonStr) {
        try {
            if (jsonStr.startsWith("{")) {
                fromJson(Map.class, jsonStr);
            } else {
                fromJsonAsList(Map.class, jsonStr);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /*public static void main(String[] args) {
        User user = new User();
        user.setAccount("admin");
        user.setCreateTime( DateUtil.parseTime( DateUtil.getTime() ) );

        String json = toJson(user);

        System.out.println("压缩后的json字符串：========》");
        System.out.println(json);

        System.out.println("方便调试查看的json字符串：===》");
        System.out.println(toJsonForHuman(user));

        System.out.println("不输出值为null的json字符串：==》");
        System.out.println(toJsonNotNull(user));

        User user2 = fromJson(User.class, json);

        System.out.println("json字符串转对象：==========》");
        System.out.println(user2.getAccount() + user2.getCreateTime());

        List<User> users = Lists.newArrayList(user);
        String jsons = toJson(users);
        List<User> users2 = fromJsonAsList(User.class, jsons);
        System.out.println("转换为集合后的集合长度：=======》");
        System.out.println(users2.size());

        System.out.println(isJson(json));
        System.out.println(isJson(jsons));
        System.out.println(isJson("{\"aaaa\":}"));
    }*/

}
