package cn.rh.flash.service.task.job;

import cn.rh.flash.bean.entity.dzsys.HomePageTotal;
import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.service.dzpower.PowerReceiveRecordService;
import cn.rh.flash.service.dzsys.HomePageTotalService;
import cn.rh.flash.service.dzuser.UserInfoService;
import cn.rh.flash.service.system.UserService;
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

@Log4j2
@Component
public class TotalStartPowerJob extends JobExecuter {


    @Autowired
    private ConfigCache configCache;

    @Autowired
    private UserService userService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private PowerReceiveRecordService powerReceiveRecordService;

    @Autowired
    private HomePageTotalService homePageTotalService;

    @Override
    @Transactional
    public void execute(Map<String, Object> dataMap) throws Exception {
        String testCode = configCache.get(ConfigKeyEnum.TEST_USER_CODE).trim();
        // 查询所有代理商
//        List<SearchFilter> filters = new ArrayList<>();
//        filters.add(SearchFilter.build("ucode", SearchFilter.Operator.NE, testCode));
//        filters.add(SearchFilter.build("deptid", "3"));
//        List<User> userList = userService.queryAll(filters);
//
//        Set<String> collect = userList.stream().map(User::getUcode).collect(Collectors.toSet());
        Set<String> collect =userService.getUcode(testCode);
        collect.add("admin");
        for (String ucode : collect) {
            totalStartPower(testCode,ucode);
        }
    }

    public int totalStartPower(String testCode, String ucode) {
        // 获取今天日期
        String day = DateUtil.getDay();
        String  startTime = day + " 00:00:00";
        String endTime = day + " 23:59:59";

        HomePageTotal total = new HomePageTotal();
        total.setDay(day);
        total.setSourceInvitationCode(ucode);
        //查询数据库数据，校验是否重复
        List<SearchFilter> filters1 = new ArrayList<>();
        filters1.add(SearchFilter.build("day", day));
        filters1.add(SearchFilter.build("sourceInvitationCode", ucode));

        List<HomePageTotal> homePageTotals = homePageTotalService.queryAll(filters1);
        if (homePageTotals!=null&&homePageTotals.size()!=0){
            return 0;
        }
        // 查询 非测试用户
       List<SearchFilter> filters = new ArrayList<>();
//        filters.add(SearchFilter.build("userType", "1"));
//        filters.add(SearchFilter.build("sourceInvitationCode", SearchFilter.Operator.NE, testCode));
//        filters.add(SearchFilter.build("fidw", SearchFilter.Operator.ISNULL));

//        if (!"admin".equals(ucode)){
//            filters.add(SearchFilter.build("sourceInvitationCode", ucode));
//        }
     //   List<UserInfo> userInfoList = userInfoService.queryAll(filters);

       //  Set<Long> uids = userInfoService.getId(testCode,ucode);
        // Set<Long> uids = userInfoList.stream().map(UserInfo::getId).collect(Collectors.toSet());

        // 查询启用设备数量

        filters.clear();
     //    filters.add(SearchFilter.build("uid", SearchFilter.Operator.IN, uids));

        if (!"admin".equals(ucode)){
            filters.add(SearchFilter.build("sourceInvitationCode", ucode));
            filters.add(SearchFilter.build("sourceInvitationCode", SearchFilter.Operator.NE,testCode));
        }else {
            filters.add(SearchFilter.build("sourceInvitationCode", SearchFilter.Operator.NE,testCode));
        }
        filters.add(SearchFilter.build("startTime", SearchFilter.Operator.GTE, DateUtil.parseTime(startTime)));
        filters.add(SearchFilter.build("startTime", SearchFilter.Operator.LTE, DateUtil.parseTime(endTime)));
        long startPb = powerReceiveRecordService.count(filters);
        total.setStartPb(String.valueOf(startPb));

        filters.add(SearchFilter.build("vipType", "v1"));
        long tystartPb = powerReceiveRecordService.count(filters);
        total.setTystartPb(String.valueOf(tystartPb));

        homePageTotalService.insert(total);
        return 1;
    }
}
