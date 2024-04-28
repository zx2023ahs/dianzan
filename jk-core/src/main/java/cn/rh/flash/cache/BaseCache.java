package cn.rh.flash.cache;

import cn.rh.flash.bean.vo.SpringContextHolder;
import cn.rh.flash.service.system.impl.ConstantFactory;

/**
 * @date ：Created in 2020/4/26 19:07
 */
public abstract class BaseCache implements Cache {
    @Override
    public void cache() {
        SpringContextHolder.getBean(ConstantFactory.class).cleanLocalCache();
    }
}
