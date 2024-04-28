package cn.rh.flash.service.task.job;

import cn.rh.flash.bean.entity.system.Cfg;
import cn.rh.flash.service.system.CfgService;
import cn.rh.flash.service.task.jobUtil.JobExecuter;
import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 測試 HelloJob
 */
@Component
public class HelloJob extends JobExecuter {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private CfgService cfgService;

    @Override
    public void execute(Map<String, Object> dataMap) {
        Cfg cfg = cfgService.get(1L);
        cfg.setCfgDesc("update by " + DateUtil.getTime());
        cfgService.update(cfg);
        logger.info("hello :" + JsonUtil.toJson(dataMap));
    }
    /*
     常用表达式例子
    （0）0/20 * * * * ?              表示每20秒 触发任务
    （1）0 0 2 1 * ?                 表示在每月的1日的凌晨2点调整任务
    （2）0 15 10 ? *                 MON-FRI 表示周一到周五每天上午10:15执行作业
    （3）0 15 10 ? 6L                2002-2006 表示2002-2006年的每个月的最后一个星期五上午10:15执行作
    （4）0 0 10,14,16 * * ?          每天上午10点，下午2点，4点
    （5）0 0/30 9-17 * * ?           朝九晚五工作时间内每半小时
    （6）0 0 12 ? * WED              表示每个星期三中午12点
    （7）0 0 12 * * ?                每天中午12点触发
    （8）0 15 10 ? * *               每天上午10:15触发
    （9）0 15 10 * * ?               每天上午10:15触发
    （10）0 15 10 * * ? *            每天上午10:15触发
    （11）0 15 10 * * ? 2005         2005年的每天上午10:15触发
    （12）0 * 14 * * ?               在每天下午2点到下午2:59期间的每1分钟触发
    （13）0 0/5 14 * * ?             在每天下午2点到下午2:55期间的每5分钟触发
    （14）0 0/5 14,18 * * ?          在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发
    （15）0 0-5 14 * * ?             在每天下午2点到下午2:05期间的每1分钟触发
    （16）0 10,44 14 ? 3 WED         每年三月的星期三的下午2:10和2:44触发
    （17）0 15 10 ? * MON-FRI        周一至周五的上午10:15触发
    （18）0 15 10 15 * ?             每月15日上午10:15触发
    （19）0 15 10 L * ?              每月最后一日的上午10:15触发
    （20）0 15 10 ? * 6L             每月的最后一个星期五上午10:15触发
    （21）0 15 10 ? * 6L 2002-2005   2002年至2005年的每月的最后一个星期五上午10:15触发
    （22）0 15 10 ? * 6#3            每月的第三个星期五上午10:15触发


    （1）* ：表示匹配该域的任意值。假如在Minutes域使用*, 即表示每分钟都会触发事件。

    （2）?：只能用在DayofMonth和DayofWeek两个域。它也匹配域的任意值，但实际不会。因为DayofMonth和DayofWeek会相互影响。例如想在每月的20日触发调度，不管20日到底是星期几，则只能使用如下写法： 13 13 15 20 * ?, 其中最后一位只能用？，而不能使用 *，如果使用*表示不管星期几都会触发，实际上并不是这样。

    （3）-：表示范围。例如在Minutes域使用5-20，表示从5分到20分钟每分钟触发一次

    （4）/：表示起始时间开始触发，然后每隔固定时间触发一次。例如在Minutes域使用5/20,则意味着5分钟触发一次，而25，45等分别触发一次.

    （5）,：表示列出枚举值。例如：在Minutes域使用5,20，则意味着在5和20分每分钟触发一次。

    （6）L：表示最后，只能出现在DayofWeek和DayofMonth域。如果在DayofWeek域使用5L,意味着在最后的一个星期四触发。

    （7）W:表示有效工作日(周一到周五),只能出现在DayofMonth域，系统将在离指定日期的最近的有效工作日触发事件。例如：在 DayofMonth使用5W，如果5日是星期六，则将在最近的工作日：星期五，即4日触发。如果5日是星期天，则在6日(周一)触发；如果5日在星期一到星期五中的一天，则就在5日触发。另外一点，W的最近寻找不会跨过月份 。

    （8）LW:这两个字符可以连用，表示在某个月最后一个工作日，即最后一个星期五。

    （9）#:用于确定每个月第几个星期几，只能出现在DayofWeek域。例如在4#2，表示某月的第二个星期三

     */

/*    public static void main(String[] args) {


        long daySub = DateUtil.getDaySub( DateUtil.getDay( DateUtil.parseDate(  "2022-11-07 12:50:52"  ) ), DateUtil.getDay() );

    }*/
}
