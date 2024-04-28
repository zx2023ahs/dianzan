package cn.rh.flash.dao.system;


import cn.rh.flash.bean.entity.system.Dict;
import cn.rh.flash.dao.BaseRepository;

import java.util.List;

public interface DictRepository extends BaseRepository<Dict, Long> {
    List<Dict> findByPid(Long pid);

    List<Dict> findByNameAndPid(String name, Long pid);

    List<Dict> findByNameLike(String name);
}
