package cn.rh.flash.service.task.job;

import cn.hutool.core.util.ObjUtil;
import cn.rh.flash.bean.entity.dzsys.HomePageTotal;
import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.dao.dzsys.HomePageTotalRepository;
import cn.rh.flash.service.dzpower.PowerReceiveRecordService;
import cn.rh.flash.service.dzpower.RecordPbService;
import cn.rh.flash.service.dzsys.HomePageTotalService;
import cn.rh.flash.service.dzuser.CompensationRecordService;
import cn.rh.flash.service.dzuser.RechargeRecordService;
import cn.rh.flash.service.dzuser.UserInfoService;
import cn.rh.flash.service.dzuser.WithdrawalsRecordService;
import cn.rh.flash.service.dzvip.VipPurchaseHistoryService;
import cn.rh.flash.service.dzvip.VipRebateRecordService;
import cn.rh.flash.service.system.UserService;
import cn.rh.flash.service.task.jobUtil.JobExecuter;
import cn.rh.flash.utils.DateUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


@Log4j2
@Component
public class HomePageTotalJob extends JobExecuter {

    @Autowired
    private ConfigCache configCache;

    @Autowired
    private HomePageTotalService homePageTotalService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private VipPurchaseHistoryService vipPurchaseHistoryService;

    @Autowired
    private RechargeRecordService rechargeRecordService;

    @Autowired
    private WithdrawalsRecordService withdrawalsRecordService;

    @Autowired
    private VipRebateRecordService vipRebateRecordService;

    @Autowired
    private RecordPbService recordPbService;

    @Autowired
    private CompensationRecordService compensationRecordService;

    @Autowired
    private UserService userService;

    @Autowired
    private PowerReceiveRecordService powerReceiveRecordService;

    @Autowired
    private HomePageTotalRepository homePageTotalRepository;

    @Override
    public void execute(Map<String, Object> dataMap) throws Exception {

        String testCode = configCache.get(ConfigKeyEnum.TEST_USER_CODE).trim();
        // 查询所有代理商
         List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("ucode", SearchFilter.Operator.NE, testCode));
        filters.add(SearchFilter.build("deptid", "3"));

        Set<String> collect =userService.getUcode(testCode);

        collect.add("admin");
        // 获取前一天日期
//        String dayNow = DateUtil.getAfterDayDateString("0", "yyyy-MM-dd");
//        // 获取当天的数据
//        List<HomePageTotal> homePageTotalsNow = homePageTotalService.queryAll(SearchFilter.build("day", dayNow));
//        //查询当天数据，如果存在则不进行操作
//        if (homePageTotalsNow!=null&&homePageTotalsNow.size()!=0){
//            return;
//        }

        // 获取前一天日期
        String day = DateUtil.getAfterDayDateString("-1", "yyyy-MM-dd");
        String startTime = day + " 00:00:00";
        String endTime = day + " 23:59:59";

        // 获取昨天的数据
        List<HomePageTotal> homePageTotals = homePageTotalService.queryAll(SearchFilter.build("day", day));

        Map<String, HomePageTotal> totalMap = homePageTotals.stream().collect(Collectors.toMap(HomePageTotal::getSourceInvitationCode, Function.identity()));

        for (String ucode : collect) {
            if (totalMap.get(ucode) != null){
                homePageTotal(testCode,ucode, startTime, endTime,totalMap.get(ucode));
            }
        }
        homePageTotalRepository.saveAll(homePageTotals);
    }

    @Transactional(rollbackFor = Exception.class)
    public void homePageTotal(String testCode,String ucode,String startTime,String endTime,HomePageTotal total) {

        // 查询 非测试用户
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("userType", "1"));
        filters.add(SearchFilter.build("sourceInvitationCode", SearchFilter.Operator.NE, testCode));
        filters.add(SearchFilter.build("fidw", SearchFilter.Operator.ISNULL));

//        if (!"admin".equals(ucode)){
//
//            filters.add(SearchFilter.build("sourceInvitationCode", ucode));
//        }



      // Set<Long> uids = userInfoService.getId(testCode,ucode);

//
//        List<UserInfo> userInfoList = userInfoService.queryAll(filters);
//        Set<Long> uids = userInfoList.stream().map(UserInfo::getId).collect(Collectors.toSet());

        //  注册人数
        filters.clear();
        filters.add(SearchFilter.build("registrationTime", SearchFilter.Operator.GTE, DateUtil.parseTime(startTime)));
        filters.add(SearchFilter.build("registrationTime", SearchFilter.Operator.LTE, DateUtil.parseTime(endTime)));
        if (!"admin".equals(ucode)){
            filters.add(SearchFilter.build("sourceInvitationCode", ucode));
            filters.add(SearchFilter.build("sourceInvitationCode", SearchFilter.Operator.NE,testCode));
        }else {
            filters.add(SearchFilter.build("sourceInvitationCode", SearchFilter.Operator.NE,testCode));
        }
//        filters.add(SearchFilter.build("id", SearchFilter.Operator.IN, uids));
        long count = userInfoService.count(filters);
        total.setRegistrationNum(String.valueOf(count));




        // VIP新增人数 vipNum
        filters.clear();
        filters.add(SearchFilter.build("whetherToPay", "2"));
        filters.add(SearchFilter.build("previousViPType", SearchFilter.Operator.IN, new String[]{"v0", "v1"}));
        filters.add(SearchFilter.build("afterViPType", SearchFilter.Operator.NE, "v1"));
        filters.add(SearchFilter.build("createTime", SearchFilter.Operator.GTE, DateUtil.parseTime(startTime)));
        filters.add(SearchFilter.build("createTime", SearchFilter.Operator.LTE, DateUtil.parseTime(endTime)));

        if (!"admin".equals(ucode)){
            filters.add(SearchFilter.build("sourceInvitationCode", ucode));
            filters.add(SearchFilter.build("sourceInvitationCode", SearchFilter.Operator.NE,testCode));
        }else {
            filters.add(SearchFilter.build("sourceInvitationCode", SearchFilter.Operator.NE,testCode));
        }

        // filters.add(SearchFilter.build("uid", SearchFilter.Operator.IN, uids));
        long count1 = vipPurchaseHistoryService.count(filters);
        total.setVipNum(String.valueOf(count1));

        // vipFirstNum, -- 首充人数
        filters.clear();
       // filters.add(SearchFilter.build("uid", SearchFilter.Operator.IN, uids));
        if (!"admin".equals(ucode)){
            filters.add(SearchFilter.build("sourceInvitationCode", ucode));
            filters.add(SearchFilter.build("sourceInvitationCode", SearchFilter.Operator.NE,testCode));
        }else {
            filters.add(SearchFilter.build("sourceInvitationCode", SearchFilter.Operator.NE,testCode));
        }
        filters.add(SearchFilter.build("rechargeStatus", "3"));
        filters.add(SearchFilter.build("firstCharge", "1"));
        filters.add(SearchFilter.build("modifyTime", SearchFilter.Operator.GTE, DateUtil.parseTime(startTime)));
        filters.add(SearchFilter.build("modifyTime", SearchFilter.Operator.LTE, DateUtil.parseTime(endTime)));
        filters.add(SearchFilter.build("fidw", SearchFilter.Operator.ISNULL));
        long count2 = rechargeRecordService.count(filters);
        total.setVipFirstNum(String.valueOf(count2));

        // -- 充值数量 充值金额 cNum cMoney
//        filters.clear();
//        filters.add(SearchFilter.build("uid", SearchFilter.Operator.IN, uids));
//        filters.add(SearchFilter.build("rechargeStatus", "3"));
//        filters.add(SearchFilter.build("modifyTime", SearchFilter.Operator.GTE, DateUtil.parseTime(startTime)));
//        filters.add(SearchFilter.build("modifyTime", SearchFilter.Operator.LTE, DateUtil.parseTime(endTime)));
//        filters.add(SearchFilter.build("fidw", SearchFilter.Operator.ISNULL));

        int rechargeRecordCount=rechargeRecordService.getCount(startTime,endTime,ucode,testCode);
        total.setCNum(String.valueOf(rechargeRecordCount));
        BigDecimal rechargeRecordSum=rechargeRecordService.getSum(startTime,endTime,ucode,testCode);
        total.setCMoney(rechargeRecordSum.setScale(2, RoundingMode.HALF_UP).toString());

//        List<RechargeRecord> rechargeRecords = rechargeRecordService.queryAll(filters);
//        total.setCNum(String.valueOf(rechargeRecords.size()));
//        double sum = 0.00;
//        for (RechargeRecord rechargeRecord : rechargeRecords) {
//            sum = BigDecimalUtils.add(sum, rechargeRecord.getMoney());
//        }
//        total.setCMoney(String.valueOf(sum));

        // 提现数量 提现金额 tNum tMoney
//        filters.clear();
//        filters.add(SearchFilter.build("uid", SearchFilter.Operator.IN, uids));
//        filters.add(SearchFilter.build("rechargeStatus", SearchFilter.Operator.IN, new String[]{"suc", "sysok"}));
//        filters.add(SearchFilter.build("modifyTime", SearchFilter.Operator.GTE, DateUtil.parseTime(startTime)));
//        filters.add(SearchFilter.build("modifyTime", SearchFilter.Operator.LTE, DateUtil.parseTime(endTime)));
//        filters.add(SearchFilter.build("fidw", SearchFilter.Operator.ISNULL));

        int withdrawalsRecordCount=withdrawalsRecordService.getCount(startTime,endTime,ucode,testCode);
        total.setTNum(String.valueOf(withdrawalsRecordCount));
        BigDecimal withdrawalsRecordSum=withdrawalsRecordService.getSum(startTime,endTime,ucode,testCode);
        total.setTMoney(withdrawalsRecordSum.setScale(2, RoundingMode.HALF_UP).toString());
        total.setMoney(rechargeRecordSum.subtract(withdrawalsRecordSum).setScale(2, RoundingMode.HALF_UP).toString());

//        List<WithdrawalsRecord> withdrawalsRecords = withdrawalsRecordService.queryAll(filters);
//        total.setTNum(String.valueOf(withdrawalsRecords.size()));
//        double sum1 = 0.00;
//
//        for (WithdrawalsRecord withdrawalsRecord : withdrawalsRecords) {
//            sum1 = BigDecimalUtils.add(sum1, withdrawalsRecord.getMoney());
//        }
//
//        total.setTMoney(String.valueOf(sum1));

    //    total.setMoney(BigDecimalUtils.subtract(sum1,sum);

    //    total.setMoney(String.valueOf(BigDecimalUtils.subtract(rechargeRecordSum, sum1)));
        // VIP返佣 l1Vip l2Vip l3Vip
//        filters.clear();
//        filters.add(SearchFilter.build("modifyTime", SearchFilter.Operator.GTE, DateUtil.parseTime(startTime)));
//        filters.add(SearchFilter.build("modifyTime", SearchFilter.Operator.LTE, DateUtil.parseTime(endTime)));
//        filters.add(SearchFilter.build("uid", SearchFilter.Operator.IN, uids));
//        filters.add(SearchFilter.build("fidw", SearchFilter.Operator.ISNULL));



      //  List<VipRebateRecord> vipRebateRecords = vipRebateRecordService.queryAll(filters);

//    Map<Integer, Double> collect = vipRebateRecords.stream().collect(Collectors.groupingBy(VipRebateRecord::getRelevels, Collectors.summingDouble(VipRebateRecord::getMoney)));
//        double v1 = 0.00;
//        double v2 = 0.00;
//        double v3 = 0.00;
//        for (VipRebateRecord vipRebateRecord : vipRebateRecords) {
//            if (vipRebateRecord.getRelevels() == 1) {
//                v1 = BigDecimalUtils.add(v1, vipRebateRecord.getMoney());
//            } else if (vipRebateRecord.getRelevels() == 2) {
//                v2 = BigDecimalUtils.add(v2, vipRebateRecord.getMoney());
//
//            } else if (vipRebateRecord.getRelevels() == 3) {
//                v3 = BigDecimalUtils.add(v3, vipRebateRecord.getMoney());
//            }
//        }
//        total.setL1Vip(String.valueOf(0.00));
//        total.setL2Vip(String.valueOf(0.00));
//        total.setL3Vip(String.valueOf(0.00));

        // VIP返佣 l1Vip l2Vip l3Vip
        total.setL1Vip("0.00");
        total.setL2Vip("0.00");
        total.setL3Vip("0.00");
        Map map =vipRebateRecordService.getVipSum(startTime,endTime,ucode,testCode);
        if (ObjUtil.isNotEmpty(map.get("sum_relevels_1"))){
            String sumRelevels1 = new BigDecimal(map.get("sum_relevels_1").toString()).setScale(2, RoundingMode.HALF_UP).toString();
            total.setL1Vip(sumRelevels1);
        }
        if (ObjUtil.isNotEmpty(map.get("sum_relevels_2"))){
            String sumRelevels2 = new BigDecimal(map.get("sum_relevels_2").toString()).setScale(2, RoundingMode.HALF_UP).toString();
            total.setL2Vip(sumRelevels2);
        }
        if (ObjUtil.isNotEmpty(map.get("sum_relevels_3"))){
            String sumRelevels3 = new BigDecimal(map.get("sum_relevels_3").toString()).setScale(2, RoundingMode.HALF_UP).toString();
            total.setL3Vip(sumRelevels3);
        }


        // 任务返佣 发放总佣金 完成任务数量 l1Pb l2Pb l3Pb totalPb pbNum
//        filters.clear();
//        filters.add(SearchFilter.build("uid", SearchFilter.Operator.IN, uids));
//        filters.add(SearchFilter.build("createTime", SearchFilter.Operator.GTE, DateUtil.parseTime(startTime)));
//        filters.add(SearchFilter.build("createTime", SearchFilter.Operator.LTE, DateUtil.parseTime(endTime)));
//        filters.add(SearchFilter.build("fidw", SearchFilter.Operator.ISNULL));
//        List<RecordPb> recordPbs = recordPbService.queryAll(filters);
//        total.setPbNum(String.valueOf(recordPbs.size()));
//        double sum2 = 0.00;
//
////        Map<Integer, Double> collect1 = recordPbs.stream().collect(Collectors.groupingBy(RecordPb::getRelevels, Collectors.summingDouble(RecordPb::getMoney)));
//
//        double p1 = 0.00;
//        double p2 = 0.00;
//        double p3 = 0.00;
//        for (RecordPb recordPb : recordPbs) {
//            if (recordPb.getRelevels() == 1) {
//                p1 = BigDecimalUtils.add(p1, recordPb.getMoney());
//            } else if (recordPb.getRelevels() == 2) {
//                p2 = BigDecimalUtils.add(p2, recordPb.getMoney());
//
//            } else if (recordPb.getRelevels() == 3) {
//                p3 = BigDecimalUtils.add(p3, recordPb.getMoney());
//            }
//            sum2 = BigDecimalUtils.add(sum2, recordPb.getMoney());
//        }
//        total.setL1Pb(String.valueOf(p1));
//        total.setL2Pb(String.valueOf(p2));
//        total.setL3Pb(String.valueOf(p3));
//        total.setTotalPb(String.valueOf(sum2));

        int recordPbCount=recordPbService.getCount(startTime,endTime,ucode,testCode);
        total.setPbNum(String.valueOf(recordPbCount));
        Map<String, BigDecimal> reLevelsMap=recordPbService.getReLevelsMap(startTime,endTime,ucode,testCode);
        total.setL1Pb(reLevelsMap.get("sum_relevels_1").setScale(2, RoundingMode.HALF_UP).toString());
        total.setL2Pb(reLevelsMap.get("sum_relevels_2").setScale(2, RoundingMode.HALF_UP).toString());
        total.setL3Pb(reLevelsMap.get("sum_relevels_3").setScale(2, RoundingMode.HALF_UP).toString());
        total.setTotalPb(reLevelsMap.get("sum").setScale(2, RoundingMode.HALF_UP).toString());


        // 注册奖励 dcMoney
//        filters.clear();
//        filters.add(SearchFilter.build("uid", SearchFilter.Operator.IN, uids));
//        filters.add(SearchFilter.build("createTime", SearchFilter.Operator.GTE, DateUtil.parseTime(startTime)));
//        filters.add(SearchFilter.build("createTime", SearchFilter.Operator.LTE, DateUtil.parseTime(endTime)));
    //        filters.add(SearchFilter.build("operator", "reg_gift"));
//
//        List<CompensationRecord> compensationRecords = compensationRecordService.queryAll(filters);
//        double sum3 = 0.00;
//        for (CompensationRecord compensationRecord : compensationRecords) {
//            sum3 = BigDecimalUtils.add(sum3, compensationRecord.getMoney());
//        }
//        total.setDcMoney(String.valueOf(sum3));

        BigDecimal compenSationRecordSum= compensationRecordService.getSum(startTime,endTime,ucode,testCode);
        total.setDcMoney(compenSationRecordSum.setScale(2, RoundingMode.HALF_UP).toString());



//        // 查询启用设备数量
//
//        filters.clear();
//        filters.add(SearchFilter.build("uid", SearchFilter.Operator.IN, uids));
//        filters.add(SearchFilter.build("startTime", SearchFilter.Operator.GTE, DateUtil.parseTime(startTime)));
//        filters.add(SearchFilter.build("startTime", SearchFilter.Operator.LTE, DateUtil.parseTime(endTime)));
//        long startPb = powerReceiveRecordService.count(filters);
//        total.setStartPb(String.valueOf(startPb));
//
//        filters.add(SearchFilter.build("vipType","v1"));
//        long tystartPb = powerReceiveRecordService.count(filters);
//        total.setTystartPb(String.valueOf(tystartPb));

//        homePageTotalService.insert(total);


    }

}
