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

import java.util.Date;
import java.util.Map;

@Log4j2
@Component
public class DeleteRechargeJob extends JobExecuter {

    @Autowired
    private RechargeRecordService rechargeRecordService;

    @Autowired
    private ConfigCache configCache;

    @Override
    @Transactional
    public void execute(Map<String, Object> dataMap) {
        String time = configCache.get(ConfigKeyEnum.INVALID_RECHARGE_DELETE_TIME).trim();
        log.info("------删除失效充值订单功能 start------ :" + DateUtil.getTime());
        Date date = DateUtil.getAfterDayDate("-"+time);//可自定义配置删除时间
        log.info("时间："+date);
        deleteRecharge(1,100,date);
        log.info("------删除失效充值订单功能 end------ :" + DateUtil.getTime());
    }
    private void deleteRecharge(int page, int pageSize,Date date) {
        Page<RechargeRecord> pageObj = new Page<>(page, pageSize);
        pageObj.addFilter(SearchFilter.build("createTime", SearchFilter.Operator.LT,date));//创建时间小于设定时间
        pageObj.addFilter(SearchFilter.build("rechargeStatus", "4" ));//拒绝
        Page<RechargeRecord> recordPage = rechargeRecordService.queryPage(pageObj);

        for (RechargeRecord record : recordPage.getRecords()) {
            log.info("------删除失效充值订单------ 用户账户:" +record.getAccount()+"通道类型："+record.getChannelName());
            rechargeRecordService.delete(record.getId());
        }
        //递归下一页
        if (recordPage.getRecords().size() == pageSize) {
            deleteRecharge(page, pageSize,date);
        }
    }
}
