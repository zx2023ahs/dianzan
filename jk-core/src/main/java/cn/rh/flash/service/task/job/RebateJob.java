package cn.rh.flash.service.task.job;

import cn.rh.flash.bean.entity.dzpower.PowerBankTask;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.entity.dzvip.DzVipMessage;
import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.service.coom.dz.RecordInformation;
import cn.rh.flash.service.dzpower.PowerBankTaskService;
import cn.rh.flash.service.dzuser.UserInfoService;
import cn.rh.flash.service.dzvip.DzVipMessageService;
import cn.rh.flash.service.task.jobUtil.JobExecuter;
import cn.rh.flash.utils.BigDecimalUtils;
import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.StringUtil;
import cn.rh.flash.utils.factory.Page;
import com.sun.istack.Nullable;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 充电宝返佣任务
 *
 * @author jreak
 */
@Log4j2
@Component
public class RebateJob extends JobExecuter {


    /**
     * 任务
     */
    @Autowired
    private PowerBankTaskService powerBankTaskService;
    /**
     * 充电宝返佣记录
     */
    @Autowired
    private RecordInformation recordInformation;
    /**
     * 用户信息
     */
    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private DzVipMessageService dzVipMessageService;

    @Autowired
    private ConfigCache configCache;


    @Override
    @Transactional
    public void execute(Map<String, Object> dataMap) {
        log.info("开始返佣流水记录任务时间 :" + DateUtil.getTime());
        // 返佣业务   // 结算时间
        String s2 = timeFormatting(dataMap.get("time") + "", dataMap.get("days") + "");
        if (StringUtil.isNotEmpty(s2)) {
            rock(1, 100, s2);
        } else {
            log.info("结算时间设置格式不正确 :" + dataMap.get("time"));
        }
        log.info("结束返佣流水记录任务时间 :" + DateUtil.getTime());
    }


//    public static void main(String[] args) throws ParseException {
//        CronExpression cronExpression = new CronExpression("0 15 03 * * ?");
//        System.out.println(cronExpression.getNextValidTimeAfter(new Date()));
//        System.out.println(cronExpression.toString());
//    }

    /**
     * 充电宝返佣
     *
     * @param page
     * @param pageSize
     */
    public void rock(int page, int pageSize, String settlement) {
        List<SearchFilter> sealist = new ArrayList<>();
        //  当天时间
        String s = DateUtil.getDay() + " " + "00:00:00";
        // 通过最后一次返佣时间 < 结算时间
        sealist.add(SearchFilter.build("lastTime", SearchFilter.Operator.LT, DateUtil.parseTime(settlement)));
        // 过期 > 当天
        sealist.add(SearchFilter.build("expireTime", SearchFilter.Operator.GT, DateUtil.parseTime(s)));
//        sealist.add(SearchFilter.build("account", SearchFilter.Operator.EQ,"7891234003"));
        Page<PowerBankTask> pageobj = new Page<>(page, pageSize);
        pageobj.setFilters(sealist);
        Page<PowerBankTask> powerBankTaskPage = powerBankTaskService.queryPage(pageobj);

        // 用户ID集合
        Set<Long> uIds = powerBankTaskPage.getRecords().stream().map(PowerBankTask::getUid).collect(Collectors.toSet());

        List<UserInfo> users = userInfoService.queryAll(SearchFilter.build("id", SearchFilter.Operator.IN, uIds));

        Map<Long, Integer> userMap = users.stream().collect(Collectors.toMap(UserInfo::getId, UserInfo::getLimitProfit));

        for (PowerBankTask record : powerBankTaskPage.getRecords()) {

            Date lastTime = record.getLastTime();
            record.setLastTime(DateUtil.parseTime(DateUtil.getTime()));
            Integer limitProfit = userMap.get(record.getUid());
            // 更新最后一次返佣时间
            // 完成返佣业务
//                completeTheCommissionBusiness( record );
            // 记录返佣流水表
            // 禁止周期内不能运行
            String week = configCache.get(ConfigKeyEnum.PROHIBIT_RELEASE_CYCLE);
            String dayWeek = DateUtil.getWeek(DateUtil.parse(DateUtil.getTime(), "yyyy-MM-dd HH:mm:ss"));

            if (limitProfit == 2) { // 未限制收益
                if ("v0v1".contains(record.getVipType())) { // v0v1没有禁止周期
                    addFlowingWaterPb(record, lastTime);
                } else {
                    if (StringUtil.isNotEmpty(week) && week.contains(dayWeek)) {
                        log.info("--------当前星期为:{},不发放收益--------", dayWeek);
                    } else {
                        addFlowingWaterPb(record, lastTime);
                    }
                }
            }
//            if (limitProfit == 2) {
//                addFlowingWaterPb(record,lastTime);
//            }
            PowerBankTask update = powerBankTaskService.update(record);
        }
        //递归下一页
        if (powerBankTaskPage.getRecords().size() == pageSize) {
            rock(page, pageSize, settlement);
        }
    }

    /**
     * 记录流水
     *
     * @param powerBankTask
     */
    @Transactional(rollbackFor = Exception.class)
    public void addFlowingWaterPb(PowerBankTask powerBankTask, Date lastTime) {
        if (powerBankTask.getPayPrice() != null && powerBankTask.getTotalQuantity() != null) {
            // 返佣金额 = 单天返金额 * 持有充电宝数量
            Double money = BigDecimalUtils.multiply(powerBankTask.getPayPrice(), powerBankTask.getTotalQuantity());
            recordInformation.addFlowingWaterPb(money, powerBankTask.getUid(), powerBankTask.getSourceInvitationCode(), powerBankTask.getAccount(), lastTime, powerBankTask.getAccount(), powerBankTask.getIdw(), 0);

            UserInfo userInfo = userInfoService.getOneBySql(powerBankTask.getUid());


            Map<String, DzVipMessage> vipMap = getVipList();

            //
            //  l1.id,l1.account,l1.fee,  l2.id,l2.account,l2.fee  ,l3.id,l3.account,l3.fee
            Map upUpUp = userInfoService.findUpUpUpCdb(userInfo);

            if (upUpUp != null) {
//                if (upUpUp.get("l1id") != null && !((upUpUp.get("l1id") + "").equals(userInfo.getId() + ""))) {
//
//                    DzVipMessage dzVipMessage = getMinVip(userInfo.getVipType(), upUpUp.get("l1vipType").toString(), vipMap);
//                    if (dzVipMessage != null && dzVipMessage.getL1TaskRebate() > 0) {
//                        recordInformation.addFlowingWaterPb(
//                                BigDecimalUtils.multiply(money, dzVipMessage.getL1TaskRebate()),
//                                Long.valueOf(upUpUp.get("l1id").toString()),
//                                powerBankTask.getSourceInvitationCode(),
//                                upUpUp.get("l1account").toString(),
//                                lastTime, powerBankTask.getAccount(), powerBankTask.getIdw(), 1);
//                    }
//
//                }
//                if (upUpUp.get("l2id") != null && !((upUpUp.get("l2id") + "").equals((upUpUp.get("l1id") + "")))) {
//                    DzVipMessage dzVipMessage = getMinVip(userInfo.getVipType(), upUpUp.get("l2vipType").toString(), vipMap);
//                    if (dzVipMessage != null && dzVipMessage.getL2TaskRebate() > 0) {
//                        recordInformation.addFlowingWaterPb(
//                                BigDecimalUtils.multiply(money, dzVipMessage.getL2TaskRebate()),
//                                Long.valueOf(upUpUp.get("l2id").toString()),
//                                powerBankTask.getSourceInvitationCode(),
//                                upUpUp.get("l2account").toString(),
//                                lastTime, powerBankTask.getAccount(), powerBankTask.getIdw(), 2);
//                    }
//
//                }
//                if (upUpUp.get("l3id") != null && !((upUpUp.get("l3id") + "").equals((upUpUp.get("l2id") + "")))) {
//                    DzVipMessage dzVipMessage = getMinVip(userInfo.getVipType(), upUpUp.get("l3vipType").toString(), vipMap);
//                    if (dzVipMessage != null && dzVipMessage.getL3TaskRebate() > 0) {
//                        recordInformation.addFlowingWaterPb(
//                                BigDecimalUtils.multiply(money, dzVipMessage.getL3TaskRebate()),
//                                Long.valueOf(upUpUp.get("l3id").toString()),
//                                powerBankTask.getSourceInvitationCode(),
//                                upUpUp.get("l3account").toString(),
//                                lastTime, powerBankTask.getAccount(), powerBankTask.getIdw(), 3);
//                    }
//                }
            }

        } else {
            log.error(String.format("充电宝返佣记录流水( 记录失败原因 单天返金额=%s 购买数量=%s ) 此条编号【 %s 】", powerBankTask.getPayPrice(), powerBankTask.getPayPrice(), powerBankTask.getIdw()));
        }
    }

    private DzVipMessage getMinVip(String vip, String upVip, Map<String, DzVipMessage> vipMap) {
        int vipInt = Integer.parseInt(vip.replace("v", ""));
        int upVipInt = Integer.parseInt(upVip.replace("v", ""));

        if (vipInt > upVipInt) {
            return vipMap.get(upVip);
        }

        return vipMap.get(vip);
    }

    private Map<String, DzVipMessage> getVipList() {
        List<DzVipMessage> dzVipMessagesList = dzVipMessageService.queryAll();
        Map<String, DzVipMessage> map = dzVipMessagesList.stream().collect(Collectors.toMap(DzVipMessage::getVipType, Function.identity()));
        return map;
    }
//
//    // 返佣业务
//
//    /**
//     * @param powerBankTask
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public void completeTheCommissionBusiness(PowerBankTask powerBankTask ){
//
//        if( powerBankTask.getPayPrice() != null && powerBankTask.getTotalQuantity() != null ){
//            // 返佣金额 = 单天返金额 * 持有充电宝数量
//            Double money = BigDecimalUtils.multiply( powerBankTask.getPayPrice(),powerBankTask.getTotalQuantity() );
//            recordInformation.addRecordPb( money,powerBankTask.getUid(),powerBankTask.getSourceInvitationCode(),powerBankTask.getAccount() ,powerBankTask.getLastTime() ,powerBankTask.getAccount());
//
//            //  l1.id,l1.account,l1.fee,  l2.id,l2.account,l2.fee  ,l3.id,l3.account,l3.fee
//            Map upUpUp = userInfoService.findUpUpUpCdb( powerBankTask.getUid() );
//            if( upUpUp != null ){
//                if( upUpUp.get( "l1id" ) != null &&  upUpUp.get( "l1fee" ) != null ){
//                    recordInformation.addRecordPb(
//                            BigDecimalUtils.multiply( money, Double.valueOf( upUpUp.get("l1fee").toString() ) ),
//                            Long.valueOf( upUpUp.get( "l1id" ).toString() ),
//                            powerBankTask.getSourceInvitationCode(),
//                            upUpUp.get( "l1account" ).toString(),
//                            powerBankTask.getLastTime() ,powerBankTask.getAccount() );
//                }
//                if( upUpUp.get( "l2id" ) != null &&  upUpUp.get( "l2fee" ) != null ){
//                    recordInformation.addRecordPb(
//                            BigDecimalUtils.multiply( money, Double.valueOf( upUpUp.get("l2fee").toString() ) ),
//                            Long.valueOf( upUpUp.get( "l2id" ).toString() ),
//                            powerBankTask.getSourceInvitationCode(),
//                            upUpUp.get( "l2account" ).toString(),
//                            powerBankTask.getLastTime() ,powerBankTask.getAccount() );
//                }
//                if( upUpUp.get( "l3id" ) != null &&  upUpUp.get( "l3fee" ) != null ){
//                    recordInformation.addRecordPb(
//                            BigDecimalUtils.multiply( money, Double.valueOf( upUpUp.get("l3fee").toString() ) ),
//                            Long.valueOf( upUpUp.get( "l3id" ).toString() ),
//                            powerBankTask.getSourceInvitationCode(),
//                            upUpUp.get( "l3account" ).toString(),
//                            powerBankTask.getLastTime() , powerBankTask.getAccount() );
//                }
//            }
//            PowerBankTask update = powerBankTaskService.update( powerBankTask );
//        }else{
//            log.error(  String.format( "充电宝返佣任务( 返佣失败原因 单天返金额=%s 购买数量=%s ) 此条编号【 %s 】",powerBankTask.getPayPrice(), powerBankTask.getPayPrice(), powerBankTask.getIdw() ) );
//        }
//    }


    /**
     * 时间格式化
     *
     * @param s1   时间日
     * @param days 天数
     * @return
     */
    @Nullable
    private String timeFormatting(String s1, String days) {

        //时间差
        Integer day = Integer.parseInt(days);
        String dayData = null;
        switch (day) {
            case 1:
                dayData = DateUtil.getAfterDayDateString("1", "yyyy-MM-dd");
                break;
            case 0:
                dayData = DateUtil.getDay();
                break;
            case -1:
                dayData = DateUtil.getAfterDayDateString("-1", "yyyy-MM-dd");
                break;
            default:
                return dayData;
        }
        return dayData + " " + s1;
    }

}
