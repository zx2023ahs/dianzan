package cn.rh.flash.service.task.job;

import cn.rh.flash.bean.entity.dzuser.RechargeRecord;
import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.service.dzuser.RechargeRecordService;
import cn.rh.flash.service.task.jobUtil.JobExecuter;
import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.factory.Page;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Log4j2
@Component
public class RejectRechargeJob extends JobExecuter {

    @Autowired
    private RechargeRecordService rechargeRecordService;

    @Autowired
    private ConfigCache configCache;
    @Override
    @Transactional
    public void execute(Map<String, Object> dataMap) {
        log.info("------拒绝超时充值订单功能 start------ :" + DateUtil.getTime());
        String time = configCache.get(ConfigKeyEnum.REJECT_RECHARGE_TIME).trim();
        log.info("超时充值订单设定时间："+time+"小时");
        Date date = DateUtil.getAfterHourString("-"+time);
        log.info(time+"小时之前是："+date);
        rejectRecharge(1,100,date);
        log.info("------拒绝超时充值订单功能 end------ :" + DateUtil.getTime());
    }
    private void rejectRecharge(int page, int pageSize,Date date) {
        Page<RechargeRecord> pageObj = new Page<>(page, pageSize);
        pageObj.addFilter(SearchFilter.build("createTime", SearchFilter.Operator.LT,date));//创建时间小于设定时间
        pageObj.addFilter(SearchFilter.build("rechargeStatus", SearchFilter.Operator.IN,Arrays.asList("1","2") ));//进行中、回调中
        Page<RechargeRecord> recordPage = rechargeRecordService.queryPage(pageObj);

        for (RechargeRecord record : recordPage.getRecords()) {
            record.setRechargeStatus("4");
            rechargeRecordService.update(record);
        }
        //递归下一页
        if (recordPage.getRecords().size() == pageSize) {
            rejectRecharge(page, pageSize,date);
        }
    }
}
