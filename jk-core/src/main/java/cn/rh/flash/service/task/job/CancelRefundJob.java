package cn.rh.flash.service.task.job;

import cn.rh.flash.bean.entity.dzpower.PowerBankTask;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.entity.dzvip.ByVipTotalMoney;
import cn.rh.flash.bean.entity.dzvip.DzVipMessage;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.service.dzpower.PowerBankTaskService;
import cn.rh.flash.service.dzpower.RefundRecordService;
import cn.rh.flash.service.dzuser.UserInfoService;
import cn.rh.flash.service.dzvip.ByVipTotalMoneyService;
import cn.rh.flash.service.dzvip.DzVipMessageService;
import cn.rh.flash.service.task.jobUtil.JobExecuter;
import cn.rh.flash.utils.DateUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Component
public class CancelRefundJob extends JobExecuter {

    @Autowired
    private PowerBankTaskService powerBankTaskService;

    @Autowired
    private DzVipMessageService dzVipMessageService;

    @Autowired
    private RefundRecordService refundRecordService;

    @Autowired
    private ByVipTotalMoneyService byVipTotalMoneyService;

    @Autowired
    private UserInfoService userInfoService;
    @Override
    @Transactional
    public void execute(Map<String, Object> dataMap) {
        log.info("------到期充电宝退款功能 start------ :" + DateUtil.getTime());
        // 查询vip对应退款比
        List<DzVipMessage> dzVipMessages = dzVipMessageService.queryAll();
        Map<String, Double> cancelRefundMap = dzVipMessages.stream().collect(Collectors.toMap(DzVipMessage::getVipType, DzVipMessage::getCancelRefund));
        cancelRefund(1,1000, cancelRefundMap);
        log.info("------到期充电宝退款功能 end------ :" + DateUtil.getTime());
    }
    private void cancelRefund(int page, int pageSize,Map<String, Double> cancelRefundMap) {
        // 查询需要退款的充电宝任务
        List<PowerBankTask> powerBankTasks = powerBankTaskService.findCancelRefund(page, pageSize);
        Set<Long> userIds = powerBankTasks.stream().map(PowerBankTask::getUid).collect(Collectors.toSet());
        // 查询用户
        List<UserInfo> userInfoList = userInfoService.query(userIds);
        Map<Long, UserInfo> userMap = userInfoList.stream().collect(Collectors.toMap(UserInfo::getId, Function.identity()));
        // 查询用户对应退款金额
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("totalMoney", SearchFilter.Operator.GT, 0.00));
        filters.add(SearchFilter.build("uid", SearchFilter.Operator.IN, userIds));
        List<ByVipTotalMoney> totalMonies = byVipTotalMoneyService.queryAll(filters);
        Map<Long, ByVipTotalMoney> totalMoniesMap = totalMonies.stream().collect(Collectors.toMap(ByVipTotalMoney::getUid, Function.identity()));

        for (PowerBankTask powerBankTask : powerBankTasks) {
            refundRecordService.startRefund(powerBankTask,totalMoniesMap,cancelRefundMap,userMap);
            powerBankTask.setIsRefund(2);// 已退款
            powerBankTaskService.update(powerBankTask);
        }

        //递归下一页
        if (powerBankTasks.size() == pageSize) {
            cancelRefund(page, pageSize,cancelRefundMap);
        }
    }
}
