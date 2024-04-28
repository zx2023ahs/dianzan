package cn.rh.flash.dao.system;

import cn.rh.flash.bean.entity.system.Cfg;
import cn.rh.flash.dao.BaseRepository;

/**
 * 全局参数dao
 */
public interface CfgRepository extends BaseRepository<Cfg, Long> {

    Cfg findByCfgName(String cfgName);
}
