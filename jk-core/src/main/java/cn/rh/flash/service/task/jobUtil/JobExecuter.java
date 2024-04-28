package cn.rh.flash.service.task.jobUtil;

import cn.rh.flash.bean.entity.system.Task;
import cn.rh.flash.bean.entity.system.TaskLog;
import cn.rh.flash.bean.vo.QuartzJob;
import cn.rh.flash.dao.system.TaskLogRepository;
import cn.rh.flash.dao.system.TaskRepository;
import cn.rh.flash.service.task.TaskService;
import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.RedisUtil;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

@Component
public abstract class JobExecuter {

    protected static final Logger log = LoggerFactory.getLogger(JobExecuter.class);

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskLogRepository taskLogRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private RedisUtil redisUtil;

    private QuartzJob job;

    public void setJob(QuartzJob job) {
        this.job = job;
    }

    @Transactional
    public void execute() {
        Map dataMap = job.getDataMap();
        String taskId = job.getJobName();
        Task task = taskService.get(Long.valueOf(taskId));
        final String taskName = task.getName();
        log.info("执行定时任务[" + taskName + "]...");

        String exeResult = "执行成功";
        final TaskLog taskLog = new TaskLog();
        taskLog.setName(taskName);
        final Date exeAt = DateUtil.parseTime( DateUtil.getTime() );
        taskLog.setExecAt(exeAt);
        taskLog.setIdTask(task.getId());
        //默认是成功 出异常后改成失败
        taskLog.setExecSuccess(TaskLog.EXE_SUCCESS_RESULT);

        // 加锁防止重复调用
        String key = "job_" + taskName+taskId;
        boolean b = redisUtil.lock(key);
        if (b) {
            try {
                execute(dataMap);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("exeucte " + getClass().getName() + " error : ", e);
                exeResult = "执行失败\n";
                exeResult += ExceptionUtils.getStackTrace(e);
                taskLog.setExecSuccess(TaskLog.EXE_FAILURE_RESULT);
                taskLog.setJobException(e.getClass().getName());
            } finally {
                redisUtil.delete(key);
            }
        }else {
            log.info("执行定时任务[" + taskName + "]加锁失败，执行取消。");
            return;
        }

//        try {
//            execute(dataMap);
//        } catch (Exception e) {
//            log.error("exeucte " + getClass().getName() + " error : ", e);
//            exeResult = "执行失败\n";
//            exeResult += ExceptionUtils.getStackTrace(e);
//            taskLog.setExecSuccess(TaskLog.EXE_FAILURE_RESULT);
//            taskLog.setJobException(e.getClass().getName());
//        }
        task.setExecResult(exeResult);
        task.setExecAt(exeAt);
        taskLogRepository.save(taskLog);
        taskRepository.save(task);
        log.info("执行定时任务[" + taskName + "]结束");
    }

    /**
     * @param dataMap 数据库配置的参数
     */
    public abstract void execute(Map<String, Object> dataMap) throws Exception;

}
