package cn.rh.flash.cache.impl;

import cn.rh.flash.bean.entity.dzvip.DzVipMessage;
import cn.rh.flash.cache.BaseCache;
import cn.rh.flash.cache.CacheDao;
import cn.rh.flash.cache.DzVipMessageCache;
import cn.rh.flash.dao.dzvip.DzVipMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DzVipMessageCacheImpl extends BaseCache implements DzVipMessageCache {

    @Autowired
    private DzVipMessageRepository dzVipMessageRepository;

    @Autowired
    private CacheDao cacheDao;


    @Override
    public void cache() {
        super.cache();
        List<DzVipMessage> all = dzVipMessageRepository.findAll();
        for (DzVipMessage dzVipMessage : all) {
            cacheDao.hdel(CacheDao.VIPMESSAGE,String.valueOf(dzVipMessage.getVipType()));
            set(String.valueOf(dzVipMessage.getVipType()),dzVipMessage);
        }
    }
    @Override
    public Object get(String key) {
       return cacheDao.hget(CacheDao.VIPMESSAGE,key,DzVipMessage.class);
    }

    @Override
    public void set(String key, Object val) {
        cacheDao.hset(CacheDao.VIPMESSAGE, key,val);
    }

    @Override
    public DzVipMessage getByVipType(String vipType) {
        return (DzVipMessage)get("v1");
    }
}
