package cn.rh.flash.dao.system;


import cn.rh.flash.bean.entity.system.Task;
import cn.rh.flash.dao.BaseRepository;

import java.util.List;

public interface TaskRepository extends BaseRepository<Task, Long> {

    long countByNameLike(String name);

    List<Task> findByNameLike(String name);

    List<Task> findAllByDisabled(boolean disable);
}
