package cn.rh.flash.service.task.jobUtil;

import cn.rh.flash.bean.entity.system.Task;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.exception.ApplicationExceptionEnum;
import cn.rh.flash.bean.vo.QuartzJob;
import cn.rh.flash.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 任务服务
 */
@Service
public class JobService {
    private static final Logger logger = LoggerFactory.getLogger(JobService.class);
    @Autowired
    private Scheduler scheduler; // 调度器

    /**
     * 获取单个任务
    * @param jobName
     * @param jobGroup
     * @return
     * @throws SchedulerException
     */
    public QuartzJob getJob(String jobName, String jobGroup) throws SchedulerException {
        QuartzJob job = null;
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        Trigger trigger = scheduler.getTrigger(triggerKey);
        if (null != trigger) {
            job = new QuartzJob();
            job.setJobName(jobName);
            job.setJobGroup(jobGroup);
            job.setDescription("触发器:" + trigger.getKey());
            job.setNextTime(trigger.getNextFireTime());
            job.setPreviousTime(trigger.getPreviousFireTime());
            Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
            job.setJobStatus(triggerState.name());
            if (trigger instanceof CronTrigger) {
                CronTrigger cronTrigger = (CronTrigger) trigger;
                String cronExpression = cronTrigger.getCronExpression();
                job.setCronExpression(cronExpression);
            }
        }
        return job;
    }

    /**
     * 创建包装任列表
     * @param tasks
     * @return
     */
    public List<QuartzJob> getTaskList( List<Task> tasks) {

        List<QuartzJob> jobs = new ArrayList<>();
        for (Task task : tasks) {
            jobs.add(getJob(task));
        }
        return jobs;
    }
    public QuartzJob getJob(Task task) {
        QuartzJob job = null;
        if (task != null) {
            job = new QuartzJob();
            job.setJobName(String.valueOf(task.getId()));   // 任务id
            job.setJobGroup(task.getJobGroup());  //   任务组名
            job.setCronExpression(task.getCron());  //  定时规则
            job.setConcurrent(task.isConcurrent());  // 是否允许并发
            job.setJobClass(task.getJobClass());   // 执行类
            job.setDescription(task.getName());   // 任务描述
            job.setDisabled(task.isDisabled());  // 状态
            if (StringUtils.isNotBlank(task.getData())) {
                try {
                    Map<String, Object> dataMap = JsonUtil.fromJson(Map.class, task.getData());
                    job.setDataMap(dataMap);  //参数
                } catch (Exception e) {
                    throw new ApplicationException(ApplicationExceptionEnum.TASK_CONFIG_ERROR);
                }
            }
        }
        return job;
    }

    /**
     * 添加任务
    * @param job
     * @throws SchedulerException
     */

    public boolean addJob(QuartzJob job) throws SchedulerException {

        logger.info("新增任务Id：{}, name:{}", job.getJobName(), job.getDescription());
        if (job == null || job.isDisabled()) {
            return false;
        }
        // 校验 时间表达式
        if (!TaskUtils.isValidExpression(job.getCronExpression())) {
            logger.error("时间表达式错误（" + job.getJobName() + "," + job.getJobGroup() + "）," + job.getCronExpression());
            return false;
        } else {
            // 任务名称和任务组设置规则：    // 名称：task_1 ..    // 组 ：group_1 ..
            /* 1.创建触发器 key */
            TriggerKey triggerKey = TriggerKey.triggerKey(job.getJobName(), job.getJobGroup());
            /* 2.创建触发器   先判断触发器是否存在 于 调度器*/
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            // 不存在，创建一个
            if (null == trigger) {
                //是否允许并发执行
                Class<? extends Job> clazz = job.isConcurrent() ? BaseJob.class : NoConurrentBaseJob.class;

                JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(job.getJobName(), job.getJobGroup()).build();
                jobDetail.getJobDataMap().put("job", job);

                // 按新的表达式构建一个新的触发器                                                  表达式调度构建器
                trigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule( CronScheduleBuilder.cronSchedule(job.getCronExpression()) ).build();

                // 加入调度器
                scheduler.scheduleJob(jobDetail, trigger);

            } else {
                // 触发器已存在，则更新相应的定时设置
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
                // 按新的cronExpression表达式重新构建trigger
                trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
                // 按新的trigger重新设置job执行
                scheduler.rescheduleJob(triggerKey, trigger);
            }
        }
        return true;
    }

    /**
     * 删除任务
     */
    public boolean deleteJob(QuartzJob record) {
        logger.info("删除任务：{}", record.getJobName());
        JobKey jobKey = JobKey.jobKey(record.getJobName(), record.getJobGroup());
        try {
            scheduler.deleteJob(jobKey);
            return true;
        } catch (SchedulerException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }


}
