package cn.rh.flash.service.task.jobUtil;

import cn.rh.flash.bean.vo.QuartzJob;
import cn.rh.flash.bean.vo.SpringContextHolder;
import cn.rh.flash.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;

public class TaskUtils {

    private static final Logger logger = LoggerFactory.getLogger(TaskUtils.class);

    /**
     * 通过反射调用job中定义的方法
    * @param job
     * @throws Exception
     */
    public static void executeJob(QuartzJob job) throws Exception {
        JobExecuter jobExecuter = null;
        Class<?> clazz = null;
        if (StringUtils.isNotBlank(job.getJobClass())) {
            clazz = Class.forName(job.getJobClass());
            jobExecuter = (JobExecuter) SpringContextHolder.getBean(clazz);
            jobExecuter.setJob(job);
        }
        if (jobExecuter == null) {
            throw new RuntimeException("任务名称 = [" + job.getDescription() + "]未启动成功，请检查执行类是否配置正确！！！");
        }
        jobExecuter.execute();
    }

    /**
     * 判断cron时间表达式正确性
    * @param cronExpression
     * @return
     */
    public static boolean isValidExpression(final String cronExpression) {
        CronTriggerImpl trigger = new CronTriggerImpl();
        try {
            trigger.setCronExpression(cronExpression);
            Date date = trigger.computeFirstFireTime(null);
            return date != null && date.after( DateUtil.parseTime( DateUtil.getTime() ) );
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }
    
}
