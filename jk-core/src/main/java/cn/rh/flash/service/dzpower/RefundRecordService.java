package cn.rh.flash.service.dzpower;


import cn.rh.flash.bean.entity.dzpower.PowerBankTask;
import cn.rh.flash.bean.entity.dzpower.RefundRecord;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.entity.dzvip.ByVipTotalMoney;
import cn.rh.flash.dao.dzpower.RefundRecordRepository;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.service.coom.dz.ApiUserCoom;
import cn.rh.flash.service.coom.dz.RecordInformation;
import cn.rh.flash.service.dzuser.UserInfoService;
import cn.rh.flash.service.dzvip.ByVipTotalMoneyService;
import cn.rh.flash.utils.IdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class RefundRecordService extends BaseService<RefundRecord,Long, RefundRecordRepository> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RefundRecordRepository refundRecordRepository;

    @Autowired
    private ByVipTotalMoneyService byVipTotalMoneyService;

    @Autowired
    private RecordInformation recordInformation;

    @Autowired
    private ApiUserCoom apiUserCoom;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private PowerBankTaskService powerBankTaskService;


    @Transactional(rollbackFor = Exception.class)
    public void startRefund(PowerBankTask powerBankTask, Map<Long, ByVipTotalMoney> totalMoniesMap, Map<String, Double> cancelRefundMap, Map<Long, UserInfo> userMap) {

        // 1.计算充电宝退费费用
        ByVipTotalMoney byVipTotalMoney = totalMoniesMap.get(powerBankTask.getUid());
        Double totalMoney = 0.00;
        if (byVipTotalMoney != null){
            totalMoney= byVipTotalMoney.getTotalMoney(); // 用户购买vip总费用
        }

        UserInfo userInfo = userMap.get(powerBankTask.getUid());
        String vipType = userInfo.getVipType();
        Double cancelRefund = cancelRefundMap.get(userInfo.getVipType()); // vip 退费比

        double money = totalMoney * (cancelRefund == null ? 0.00 : cancelRefund); // 实际退款金额
        if (money > 0.00){
            // 2.清空累计用户购买vip总费用
            byVipTotalMoney.setTotalMoney(0.00);
            byVipTotalMoneyService.update(byVipTotalMoney);
            // 3.修改用户vip等级

            userInfo.setVipType("v0");
            userInfoService.update(userInfo);
            // 4.给用户退款
            // 查询用户余额
            Double balance = apiUserCoom.getUserBalance(powerBankTask.getUid()).doubleValue();
            recordInformation.transactionRecordPlus(powerBankTask.getSourceInvitationCode(), powerBankTask.getUid(), powerBankTask.getAccount(),
                    balance, money, balance+money,
                    powerBankTask.getIdw(), 20, "cdbrf", "充电宝到期退款","");
            // 5.生成退费记录
            RefundRecord refundRecord = new RefundRecord();
            refundRecord.setIdw(new IdWorker().nextId() + "");
            refundRecord.setSourceInvitationCode(powerBankTask.getSourceInvitationCode());
            refundRecord.setVipType(vipType);
            refundRecord.setTotalMoney(totalMoney);
            refundRecord.setCancelRefund(cancelRefund);
            refundRecord.setMoney(money);
            refundRecord.setUid(powerBankTask.getUid());
            refundRecord.setAccount(powerBankTask.getAccount());
            this.insert(refundRecord);
            logger.info("--------用户:{},退款完成退款金额:{},--------",powerBankTask.getAccount(),money);
        }else {
            logger.info("--------用户:{},退款金额为0,未生成退款记录--------",powerBankTask.getAccount());
        }


    }
}
