package cn.rh.flash.api.controller.task;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.task.job.HomePageTotalJob;
import cn.rh.flash.service.task.job.TotalStartPowerJob;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Log4j2
@RestController
@RequestMapping("/task/test")
public class TaskTestController extends BaseController {

    @Resource
    private HomePageTotalJob homePageTotalJob;
    @Resource
    private TotalStartPowerJob totalStartPowerJob;

    /**
     * 首页统计昨天数据
     * @throws Exception
     */
    @GetMapping(value = "/homePageTotalJob")
    public Ret homePageTotalJob() throws Exception {
        long stime = System.currentTimeMillis();
        homePageTotalJob.execute(null);
        long etime = System.currentTimeMillis();
        long duration = etime - stime;
        double seconds = duration / 1000.0;
        return Rets.success("执行时长："+seconds+"秒.");
    }


    /**
     * 首页统计今天运行设备数量
     * @throws Exception
     */
    @GetMapping(value = "/totalStartPowerJob")
    public Ret totalStartPowerJob() throws Exception {
        long stime = System.currentTimeMillis();
        totalStartPowerJob.execute(null);
        long etime = System.currentTimeMillis();
        long duration = etime - stime;
        double seconds = duration / 1000.0;
        return Rets.success("执行时长："+seconds+"秒.");
    }
}
