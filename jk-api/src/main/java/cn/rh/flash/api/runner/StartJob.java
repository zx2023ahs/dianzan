package cn.rh.flash.api.runner;

import cn.rh.flash.bean.entity.system.Task;
import cn.rh.flash.bean.vo.QuartzJob;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.service.task.TaskService;
import cn.rh.flash.service.task.jobUtil.JobService;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.TimeZone;

/**
 * 启动定时任务
 */
@Data
@Configuration
@PropertySource("classpath:application.yml")//读取application.yml文件
@Component
public class StartJob implements ApplicationRunner {

    @Value("${spring.jackson.time-zone}")
    private String tz;

    @Autowired
    private JobService jobService;

    @Autowired
    private TaskService taskService;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        TimeZone.setDefault( TimeZone.getTimeZone(tz) );

        log.info("开始任务 >>>>>>>>>>>>>>>>>>>>>>>");

        // 获取所有定时任务
        List<Task> tasks = taskService.queryAll(SearchFilter.build("disabled", SearchFilter.Operator.EQ, false));
        // 包装定时任务列表
        List<QuartzJob> list = jobService.getTaskList(tasks);
        for (QuartzJob quartzJob : list) {
            jobService.addJob(quartzJob);
        }
    }
}
