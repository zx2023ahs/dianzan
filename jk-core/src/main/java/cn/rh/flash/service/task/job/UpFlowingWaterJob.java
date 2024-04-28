package cn.rh.flash.service.task.job;


import cn.rh.flash.bean.entity.dzpower.FlowingWaterPb;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.service.coom.dz.RecordInformation;
import cn.rh.flash.service.dzpower.FlowingWaterPbService;
import cn.rh.flash.service.dzuser.UserInfoService;
import cn.rh.flash.service.task.jobUtil.JobExecuter;
import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.factory.Page;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
public class UpFlowingWaterJob extends JobExecuter {

    /**
     * 用户信息
     */
    @Autowired
    private UserInfoService userInfoService;

    /**
     * 充电宝返佣记录
     */
    @Autowired
    private RecordInformation recordInformation;

    /**
     * 返佣流水记录
     */
    @Autowired
    private FlowingWaterPbService flowingWaterPbService;

    @Override
    @Transactional
    public void execute(Map<String, Object> dataMap) throws Exception {
        log.info("开始返佣业务任务时间 :" + DateUtil.getTime());

        rock(1, 100);

        log.info("结束返佣业务任务时间 :" + DateUtil.getTime());
    }

    /**
     * 充电宝返佣
     *
     * @param page
     * @param pageSize
     */
    public void rock(int page, int pageSize) {
        List<SearchFilter> sealist = new ArrayList<>();

        Page<FlowingWaterPb> pageObj = new Page<>(page, pageSize);

        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("dzstatus", 1));
        pageObj.setFilters(filters);
        Page<FlowingWaterPb> flowingWaterPbPage = flowingWaterPbService.queryPage(pageObj);

        for (FlowingWaterPb flowingWaterPb : flowingWaterPbPage.getRecords()) {
            completeTheCommissionBusiness(flowingWaterPb);
        }
        //递归下一页
        if (flowingWaterPbPage.getRecords().size() == pageSize) {
            rock(page, pageSize);
        }
    }


    // 返佣业务

    /**
     * @param flowingWaterPb
     */
    @Transactional(rollbackFor = Exception.class)
    public void completeTheCommissionBusiness(FlowingWaterPb flowingWaterPb) {

        recordInformation.addRecordPb(flowingWaterPb.getMoney(),
                flowingWaterPb.getUid(),
                flowingWaterPb.getSourceInvitationCode(),
                flowingWaterPb.getAccount(),
                flowingWaterPb.getFlowingWaterDate(),
                flowingWaterPb.getAccount(),
                flowingWaterPb.getRelevels(),
                flowingWaterPb.getSourceUserAccount());

        flowingWaterPb.setDzstatus(2);
        flowingWaterPbService.update(flowingWaterPb);
    }
}
