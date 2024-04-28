package cn.rh.flash.cache.impl;

import cn.rh.flash.cache.CacheDao;
import cn.rh.flash.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * redis緩存
 */
@Component
public class EhcacheDao implements CacheDao {


    @Autowired
    private StringRedisTemplate cacheManager;

    @Override
    public void lset(String key, Collection collection) {
        String s = JsonUtil.toJson(collection);
        cacheManager.opsForValue().set(key,s,24,TimeUnit.HOURS);
    }

    @Override
    public String lget(String key) {
       return cacheManager.opsForValue().get(key);
    }


    public void ldel(String key){
        cacheManager.delete(key);
    }

    @Override
    public void hset(String key, Serializable k, Object val) {
        String s = JsonUtil.toJson(val);
        cacheManager.opsForHash().put(key, k, s);
//        Cache cache = cacheManager.geto(String.valueOf(key));
//        cache.put(k, val);
    }

    /**
     * @param time SECONDS 秒
     */
    @Override
    public void hset(String key, Serializable k, Object val, int time) {
        String s = JsonUtil.toJson(val);
        cacheManager.opsForHash().put(key, k, s);
        if (time > 0) {
            cacheManager.expire(key, time, TimeUnit.SECONDS);
        }
//        Cache cache = cacheManager.geto(String.valueOf(key));
//        cache.put(k, val);
    }

    @Override
    public Object hget(String key, Serializable k) {
        Object o = cacheManager.opsForHash().get(key, k);
        return o;
//        Cache cache = cacheManager.getCache(String.valueOf(key));
//        return cache.get(k, String.class);

    }


    @Override
    public <T> T hget(String key, Serializable k, Class<T> klass) {
        Object o = cacheManager.opsForHash().get(key, k);
        return o == null ? null : JsonUtil.fromJson(klass, o.toString());
    }

//    @Override
//    public void hset(String key, Serializable k, Object val) {
//        String s = JsonUtil.toJson(val);
//        cacheManager.opsForHash().put(key, k, s);
//        cacheManager.opsForValue().set(key+":"+k,key+":"+k);
//    }
//
//    /**
//     * @param time SECONDS 秒
//     */
//    @Override
//    public void hset(String key, Serializable k, Object val, int time) {
//        String s = JsonUtil.toJson(val);
//        cacheManager.opsForHash().put(key, k, s);
//        if (time > 0) {
//            cacheManager.opsForValue().set(key+":"+k, key+":"+k, time, TimeUnit.SECONDS);
//        }
//    }
//
//    @Override
//    public Object hget(String key, Serializable k) {
//        if (cacheManager.hasKey(key+":"+k)){
//            return cacheManager.opsForHash().get(key, k);
//        }else {
//            cacheManager.opsForHash().delete(key,k);
//            return null;
//        }
//    }
//
//    @Override
//    public <T> T hget(String key, Serializable k, Class<T> klass) {
//        if (cacheManager.hasKey(key+":"+k)){
//            Object o = cacheManager.opsForHash().get(key, k);
//            return o == null ? null : JsonUtil.fromJson(klass, o.toString());
//        }else {
//            cacheManager.opsForHash().delete(key,k);
//            return null;
//        }
//    }

    @Override
    public void set(Serializable key, Object val) {
        //  Cache cache = cacheManager.getCache(CONSTANT);
//        cache.put(key, val);
        cacheManager.opsForHash().put(CONSTANT, key, val);
    }

    @Override
    public <T> T get(Serializable key, Class<T> klass) {
        Object o = cacheManager.opsForHash().get(CONSTANT, key);
        return o == null ? null : JsonUtil.fromJson(klass, o.toString());
        //return cacheManager.getCache(CONSTANT).get(String.valueOf(key), klass);
    }

    @Override
    public String get(Serializable key) {
        Object o = cacheManager.opsForHash().get(CONSTANT, key);
        return o == null ? null : JsonUtil.fromJson(String.class, o.toString());
        //return cacheManager.getCache(CONSTANT).get(String.valueOf(key), String.class);
    }

    @Override
    public void del(Serializable key) {
        cacheManager.opsForHash().delete(CONSTANT, key);
        //cacheManager.getCache(CONSTANT).put(String.valueOf(key), null);
    }

    @Override
    public void hdel(String key, Serializable k) {
        cacheManager.opsForHash().delete(key, k);
        //cacheManager.getCache(String.valueOf(key)).put(String.valueOf(k), null);
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return cacheManager.opsForValue().increment(key, delta);
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(小于0)
     * @return
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return cacheManager.opsForValue().increment(key, -delta);
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean setTime(String key, String value, long time) {
        try {
            if (time > 0) {
                cacheManager.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public String getTime(String key) {
        return key == null ? null : cacheManager.opsForValue().get(key);
    }

    // 根据条件模糊删除key
    public void hdelByPrex(String s) {

        Set<String> keys = cacheManager.keys(s+"*");
        if (keys.size() > 0) {
            cacheManager.delete(keys);
        }
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值 map
     */
    public Map<Object, Object> hmget(String key) {
        return cacheManager.opsForHash().entries(key);
    }

    public  <T> List<T> hmget(String key, Class<T> tClass) {
        Map<Object, Object> map = cacheManager.opsForHash().entries(key);
        return map.entrySet().stream()
                .map(entry -> JsonUtil.fromJson(tClass, entry.getValue().toString()))
                .collect(Collectors.toList());
    }
}
