package cn.rh.flash.dao.system;


import cn.rh.flash.bean.entity.system.OperationLog;
import cn.rh.flash.dao.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


public interface OperationLogRepository extends BaseRepository<OperationLog, Long> {
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "delete from t_sys_operation_log")
    int clear();
}
