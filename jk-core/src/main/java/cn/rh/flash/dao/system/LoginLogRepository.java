package cn.rh.flash.dao.system;

import cn.rh.flash.bean.entity.system.LoginLog;
import cn.rh.flash.dao.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


public interface LoginLogRepository extends BaseRepository<LoginLog, Long> {
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "delete from t_sys_login_log")
    int clear();
}
