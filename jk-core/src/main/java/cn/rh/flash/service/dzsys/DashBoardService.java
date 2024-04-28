package cn.rh.flash.service.dzsys;


import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.bean.vo.dz.DashBoardVo;
import cn.rh.flash.bean.vo.dz.DayReportVo;
import cn.rh.flash.bean.vo.dz.EChartsVo;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.dao.dzsys.DashBoardRepository;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashBoardService {
    @Autowired
    private ConfigCache configCache;

    @Autowired
    private DashBoardRepository dashBoardRepository;

    /**
     * 平台用户
     *
     * @param ucode
     */
    public DashBoardVo dashBoardOne(String ucode) {
        String testCode = configCache.get(ConfigKeyEnum.TEST_USER_CODE).trim();
        String sql = DashBoardServiceSql.dashBoardOne(ucode, testCode);

        Map mapBySql = dashBoardRepository.getMapBySql(sql);
        return BeanUtil.mapToBean(mapBySql, new DashBoardVo());

    }

    /**
     * 平台vip
     *
     * @param ucode
     */
    public DashBoardVo dashBoardTwo(String ucode) {
        String testCode = configCache.get(ConfigKeyEnum.TEST_USER_CODE).trim();
        String sql = DashBoardServiceSql.dashBoardTwo(ucode, testCode);

        Map mapBySql = dashBoardRepository.getMapBySql(sql);
        return BeanUtil.mapToBean(mapBySql, new DashBoardVo());
    }

    /**
     * 充值数量非首充
     *
     * @param ucode
     * @return
     */
    public DashBoardVo dashBoardFive(String ucode) {
        String testCode = configCache.get(ConfigKeyEnum.TEST_USER_CODE).trim();
        String sql = DashBoardServiceSql.dashBoardFive(ucode, testCode);

        Map mapBySql = dashBoardRepository.getMapBySql(sql);
        return BeanUtil.mapToBean(mapBySql, new DashBoardVo());
    }

    /**
     * 充值数量首充
     *
     * @param ucode
     * @return
     */
    public DashBoardVo dashBoardFiveFirst(String ucode) {
        String testCode = configCache.get(ConfigKeyEnum.TEST_USER_CODE).trim();
        String sql = DashBoardServiceSql.dashBoardFiveFirst(ucode, testCode);

        Map mapBySql = dashBoardRepository.getMapBySql(sql);
        return BeanUtil.mapToBean(mapBySql, new DashBoardVo());
    }

    /**
     * 充值金额非首充
     *
     * @param ucode
     * @return
     */
    public DashBoardVo dashBoardSix(String ucode) {
        String testCode = configCache.get(ConfigKeyEnum.TEST_USER_CODE).trim();
        String sql = DashBoardServiceSql.dashBoardSix(ucode, testCode);

        Map mapBySql = dashBoardRepository.getMapBySql(sql);
        return BeanUtil.mapToBean(mapBySql, new DashBoardVo());
    }

    /**
     * 充值金额首充
     *
     * @param ucode
     * @return
     */
    public DashBoardVo dashBoardSixFirst(String ucode) {
        String testCode = configCache.get(ConfigKeyEnum.TEST_USER_CODE).trim();
        String sql = DashBoardServiceSql.dashBoardSixFirst(ucode, testCode);

        Map mapBySql = dashBoardRepository.getMapBySql(sql);
        return BeanUtil.mapToBean(mapBySql, new DashBoardVo());
    }

    /**
     * 提现数量
     *
     * @return
     */
    public DashBoardVo dashBoardSeven(String ucode) {
        String testCode = configCache.get(ConfigKeyEnum.TEST_USER_CODE).trim();
        String sql = DashBoardServiceSql.dashBoardSeven(ucode, testCode);

        Map mapBySql = dashBoardRepository.getMapBySql(sql);
        return BeanUtil.mapToBean(mapBySql, new DashBoardVo());
    }

    /**
     * 提现金额
     *
     * @return
     */
    public DashBoardVo dashBoardEight(String ucode) {
        String testCode = configCache.get(ConfigKeyEnum.TEST_USER_CODE).trim();
        String sql = DashBoardServiceSql.dashBoardEight(ucode, testCode);

        Map mapBySql = dashBoardRepository.getMapBySql(sql);
        return BeanUtil.mapToBean(mapBySql, new DashBoardVo());
    }

    /**
     * 用户余额
     *
     * @param ucode
     * @return
     */
    public DashBoardVo dashBoardNine(String ucode) {
        String testCode = configCache.get(ConfigKeyEnum.TEST_USER_CODE).trim();
        String sql = DashBoardServiceSql.dashBoardNine(ucode, testCode);

        Map mapBySql = dashBoardRepository.getMapBySql(sql);
        return BeanUtil.mapToBean(mapBySql, new DashBoardVo());
    }

    /**
     * 当日新增各等级Vip数量
     *
     * @param ucode
     * @return
     */
    public DashBoardVo dashBoardTen(String ucode) {
        String testCode = configCache.get(ConfigKeyEnum.TEST_USER_CODE).trim();
        String sql = DashBoardServiceSql.dashBoardTen(ucode, testCode);

        Map mapBySql = dashBoardRepository.getMapBySql(sql);
        return BeanUtil.mapToBean(mapBySql, new DashBoardVo());
    }

    /**
     * @Description: ECharts图
     * @Param:
     * @return:
     * @Author: Skj
     */
    public Map<String,List> getECharts(String ucode) {
        String testCode = configCache.get(ConfigKeyEnum.TEST_USER_CODE).trim();
        String sql = DashBoardServiceSql.getECharts(ucode, testCode);

        List<EChartsVo> eChartsVos = (List<EChartsVo>) dashBoardRepository.queryObjBySql(sql, EChartsVo.class);

        List<String> dayList = eChartsVos.stream().map(EChartsVo::getDay).collect(Collectors.toList());
        List<BigDecimal> cmoneyList = eChartsVos.stream().map(EChartsVo::getCmoney).collect(Collectors.toList());
        List<BigDecimal> tmoneyList = eChartsVos.stream().map(EChartsVo::getTmoney).collect(Collectors.toList());
        List<BigDecimal> moneyList = eChartsVos.stream().map(EChartsVo::getMoney).collect(Collectors.toList());
        Map<String,List> resultMap = new HashMap<>();
        resultMap.put("day",dayList);
        resultMap.put("cmoney",cmoneyList);
        resultMap.put("tmoney",tmoneyList);
        resultMap.put("money",moneyList);
        return resultMap;
    }

    /**
    * @Description: 日期报表
    * @Param:
    * @return:
    * @Author: Skj
    */
    public List<DayReportVo> getDayReport(String ucode, String startDay, String endDay) {
        String testCode = configCache.get(ConfigKeyEnum.TEST_USER_CODE).trim();
        long daySub = DateUtil.getDaySub(startDay, endDay)+1;
        String sql = DashBoardServiceSql.getDayReport(ucode, testCode,endDay,daySub);
        List<DayReportVo> dayReportVoList = (List<DayReportVo>) dashBoardRepository.queryObjBySql(sql, DayReportVo.class);
        return dayReportVoList;
    }

    /**
     * 获取前十五天日期
     */
//    public List<String> getDay15() {
//        List dayList = new ArrayList();
//        for (int i = 15; i >= 1; i--) {
//            String day = DateUtil.getAfterDayDateString(-i + "", "yyyy-MM-dd");
//            dayList.add(day);
//        }
//        return dayList;
//    }


}
