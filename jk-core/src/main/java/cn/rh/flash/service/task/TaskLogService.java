package cn.rh.flash.service.task;


import cn.rh.flash.bean.entity.system.TaskLog;
import cn.rh.flash.dao.system.TaskLogRepository;
import cn.rh.flash.service.BaseService;
import org.springframework.stereotype.Service;

/**
 * 定时任务日志服务类
 */
@Service
public class TaskLogService extends BaseService<TaskLog, Long, TaskLogRepository> {
}
