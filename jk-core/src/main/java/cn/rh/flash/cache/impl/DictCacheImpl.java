package cn.rh.flash.cache.impl;

import cn.rh.flash.bean.constant.cache.CacheKey;
import cn.rh.flash.bean.entity.system.Dict;
import cn.rh.flash.cache.BaseCache;
import cn.rh.flash.cache.CacheDao;
import cn.rh.flash.cache.DictCache;
import cn.rh.flash.dao.system.DictRepository;
import cn.rh.flash.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DictCacheImpl extends BaseCache implements DictCache {
    @Autowired
    private DictRepository dictRepository;
    @Autowired
    private CacheDao cacheDao;

    @Override
    public List<Dict> getDictsByPname(String dictName) {
        List<Dict> dicts = new ArrayList<>();
        List hget = cacheDao.hget(CacheDao.CONSTANT, CacheKey.DICT + dictName, List.class);

        if ( hget == null ) return dicts;

        for (Object o : hget) {
            String s = JsonUtil.toJson(o);
            dicts.add(JsonUtil.fromJson( Dict.class, s ));
        }
        return dicts;
    }

    @Override
    public String getDict(Long dictId) {
        return (String) get(CacheKey.DICT_NAME + dictId);
    }

    @Override
    public void cache() {
        super.cache();
        List<Dict> list = dictRepository.findByPid(0L);
        for (Dict dict : list) {
            List<Dict> children = dictRepository.findByPid(dict.getId());
            if (children.isEmpty()) {
                continue;
            }
            set(String.valueOf(dict.getId()), children);
            set(dict.getName(), children);
            for (Dict child : children) {
                set(CacheKey.DICT_NAME + child.getId(), child.getName());
            }

        }

    }

    @Override
    public Object get(String key) {
        return cacheDao.hget(CacheDao.CONSTANT, CacheKey.DICT + key);
    }

    @Override
    public void set(String key, Object val) {
        cacheDao.hset(CacheDao.CONSTANT, CacheKey.DICT + key, val);

    }
}
