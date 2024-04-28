package cn.rh.flash.service.task.jobUtil;

import org.quartz.DisallowConcurrentExecution;
import org.springframework.stereotype.Component;

@Component
@DisallowConcurrentExecution
public class NoConurrentBaseJob extends BaseJob {
}
