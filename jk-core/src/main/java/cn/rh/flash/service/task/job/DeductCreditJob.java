package cn.rh.flash.service.task.job;

import cn.rh.flash.bean.entity.dzcredit.UserCredit;
import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.service.coom.dz.RecordInformation;
import cn.rh.flash.service.dzcredit.UserCreditService;
import cn.rh.flash.service.task.jobUtil.JobExecuter;
import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.StringUtil;
import cn.rh.flash.utils.factory.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;


@Component
public class DeductCreditJob extends JobExecuter {

    @Autowired
    private UserCreditService userCreditService;

    @Autowired
    private RecordInformation recordInformation;

    @Autowired
    private ConfigCache configCache;

    @Override
    @Transactional
    public void execute(Map<String, Object> dataMap) throws Exception {
        log.info("开始扣除信誉分任务时间 :" + DateUtil.getTime());

        rock(1, 100);

        log.info("结束扣除信誉分任务时间 :" + DateUtil.getTime());
    }

    public void rock(int page, int pageSize) {

        Page<UserCredit> pageObj = new Page<>(page, pageSize);
        pageObj.addFilter(SearchFilter.build("vipType", SearchFilter.Operator.NOTIN,new String[]{"v0","v1"} ));
        Page<UserCredit> userCreditPage = userCreditService.queryPage(pageObj);

        for (UserCredit userCredit : userCreditPage.getRecords()) {
            deductCredit(userCredit);
        }

        //递归下一页
        if (userCreditPage.getRecords().size() == pageSize) {
            log.info("扣除信誉分执行到:{}页",page);
            rock(page+1, pageSize);
        }
    }

    // 扣信誉分业务
    @Transactional(rollbackFor = Exception.class)
    public void deductCredit(UserCredit userCredit) {
        // 获取上次运行时间
        String finalDate = userCredit.getFinalDate();
        Integer credit = userCredit.getCredit();
        // 没有运行时间不执行下面逻辑
        if (StringUtil.isEmpty(finalDate)) {
            return;
        }

        /**
         *  周末不运行设备，不计算在内，不参与扣信誉分
         */
        String week = configCache.get(ConfigKeyEnum.PROHIBIT_RELEASE_CYCLE);
        week=StringUtil.isNotEmpty(week)?week:"0";

        String day = DateUtil.getDay();
//        long daySub = DateUtil.getDaySub(finalDate, DateUtil.getDay()) - 1;
        long daySub = DateUtil.getDaySubEx(DateUtil.getAfterDayDate(DateUtil.parseDate(finalDate),"1"), DateUtil.getAfterDayDateString("-1"), week);
        if (userCredit.getStatus().equals("1")){
            System.out.println("扣除信誉分--->账户："+userCredit.getAccount()+"   ,间隔天数："+daySub);
            System.out.println(userCredit.getAccount()+"-----------"+userCredit.getFinalDate());
        }
        String deduct = configCache.get(ConfigKeyEnum.DEDUCT_CREDIT).trim();
        String[] split = deduct.split(",");
        if (split.length == 0){
            split = new String[]{"3","10"};
        }

        if (daySub >= 3 && "1".equals(userCredit.getStatus())) { // 1,正常 2,3天未运行 3,5天未运行
            // 扣三分
            int sub = Integer.parseInt(split[0]);
            String remark = "";
            if (credit - sub < 20) { // 信誉分最低为20分//加拿大站最低0
                sub = credit-20;
                remark = "信誉分最低为20分";
            }
            recordInformation.addCreditRecord(userCredit.getSourceInvitationCode(), userCredit.getId(), userCredit.getAccount(),
                    credit, sub, credit - sub, "5", remark,userCredit.getAccount());
            userCredit.setCredit(credit - sub);
            userCredit.setStatus("2");
            userCreditService.update(userCredit);
        } else if (daySub >= 5 && "2".equals(userCredit.getStatus())) {
            // 扣十分
            int sub = Integer.parseInt(split[1]);
            String remark = "";
            if (credit - sub < 20) { // 信誉分最低为20分//加拿大站最低0
                sub = credit-20;
                remark = "信誉分最低为20分";
            }
            recordInformation.addCreditRecord(userCredit.getSourceInvitationCode(), userCredit.getId(), userCredit.getAccount(),
                    credit, sub, credit - sub, "6", remark,userCredit.getAccount());
            userCredit.setCredit(credit - sub);
            userCredit.setStatus("3");
            userCreditService.update(userCredit);
        }
    }


/*    public static void main(String[] args) {
        String begin="2023-10-06";
        long daySub = DateUtil.getDaySub(begin, DateUtil.getDay())-1;
        System.out.println(daySub);
        List<String> list = DateUtil.getBetWeenDate(DateUtil.getAfterDayDate(DateUtil.parseDate(begin),"1"), DateUtil.getAfterDayDateString("-1"));
        String s="星期六,星期日";
        list.forEach(day->{
            String week = DateUtil.getWeek(DateUtil.parseDate(day));
            if (!s.contains(week)){
                System.out.println(week);
            }

        });
        long l = DateUtil.getDaySubEx(DateUtil.getAfterDayDate(DateUtil.parseDate(begin),"1"), DateUtil.getAfterDayDateString("-1"), s);
        System.out.println("去除"+s+"天数为:"+l);
    }*/
}
