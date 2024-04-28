package cn.rh.flash.cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 缓存接口
* @version 2018/9/12 0012
 */
public interface CacheDao {
    String CONSTANT = "CONSTANT";
    String SESSION = "SESSION";
    String SHORT = "SHORT";

    String VIPMESSAGE = "VIPMESSAGE";

    String PAYMENT_CHANNEL="PAYMENT_CHANNEL";

    String COUNTRYCODE = "COUNTRYCODE";
    String OFFICIALNEWS = "OFFICIALNEWS";

    void lset(String key,  Collection collection );

    String lget(String key);

    void ldel(String key);

    /**
     * 设置hash key值
    * @param key
     * @param k
     * @param val
     */
    void hset(String key, Serializable k, Object val );

    // SECONDS 秒
    void hset(String key, Serializable k, Object va, int time );

    /**
     * 获取hash key值
    * @param key
     * @param k
     * @return
     */
    Object hget(String key, Serializable k);

    /**
     * 获取hash key值
    * @param key
     * @param k
     * @param klass
     * @param <T>
     * @return
     */
    <T> T hget(String key, Serializable k, Class<T> klass);

    /**
     * 设置key值，超时失效
    * @param key
     * @param val
     */
    void set(Serializable key, Object val);


    /**
     * 获取key值
    * @param key
     * @param klass
     * @return
     */
    <T> T get(Serializable key, Class<T> klass);

    String get(Serializable key);


    void del(Serializable key);

    void hdel(String key, Serializable k);


    long incr(String key, long delta);
    long decr(String key, long delta);
    boolean setTime(String key, String value, long time);
    String getTime(String key);

    Map<Object,Object> hmget(String key);

    <T> List<T> hmget(String key, Class<T> tClass);

}
