package cn.rh.flash.api.controller.dz.dzsys;


import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.entity.dzsys.HomePageTotal;
import cn.rh.flash.bean.vo.dz.DashBoardVo;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.dzsys.DashBoardService;
import cn.rh.flash.service.dzsys.HomePageTotalService;
import cn.rh.flash.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/dzsys/dashboard")
public class DashBoardController extends BaseController {

    @Autowired
    private DashBoardService dashBoardService;

    @Autowired
    private HomePageTotalService homePageTotalService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * server版本号
     */
    @GetMapping(value = "/getVersion")
    public Ret getVersion(){
        return Rets.success("10.02.22");
    }
    /**
     * 平台用户
     * @return
     */
    @GetMapping(value = "/dashBoardOne")
    public Ret dashBoardOne(){
        String ucode = "";
        if (isProxy()){
            ucode = getUcode();
        }
        //查询缓存
//        if (redisUtil.hasKey("dashBoardOne"+ucode)){
//            Object o = redisUtil.get("dashBoardOne" + ucode);
//            return Rets.success(o);
//        }
        DashBoardVo dashBoardVo = dashBoardService.dashBoardOne(ucode);
//        redisUtil.set("dashBoardOne"+ucode,dashBoardVo,1800);
        return Rets.success(dashBoardVo);
    }
    /**
     * 平台vip
     * @return
     */
    @GetMapping(value = "/dashBoardTwo")
    public Ret dashBoardTwo(){
        String ucode = "";
        if (isProxy()){
            ucode = getUcode();
        }
        //查询缓存
//        if (redisUtil.hasKey("dashBoardTwo"+ucode)){
//            Object o = redisUtil.get("dashBoardTwo" + ucode);
//            return Rets.success(o);
//        }
        DashBoardVo dashBoardVo = dashBoardService.dashBoardTwo(ucode);
//        redisUtil.set("dashBoardTwo"+ucode,dashBoardVo,1800);

        return Rets.success(dashBoardVo);
    }
    /**
     * 新用户做任务
     * @return
     */
//    @GetMapping(value = "/dashBoardThree")
//    public Ret dashBoardThree(){
//
//
//
//
//        return Rets.success();
//    }

    // ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
    //    这俩暂时没用
    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    /**
     * 总用户做任务
     * @return
     */
//    @GetMapping(value = "/dashBoardFour")
//    public Ret dashBoardFour(){
//
//
//
//
//        return Rets.success();
//    }
    /**
     * 充值数量
     * @return
     */
    @GetMapping(value = "/dashBoardFive1")
    public Ret dashBoardFive1() throws Exception {
        String ucode = "";
        if (isProxy()){
            ucode = getUcode();
        }
        //查询缓存
//        if (redisUtil.hasKey("dashBoardFive"+ucode)){
//            Object o = redisUtil.get("dashBoardFive" + ucode);
//            return Rets.success(o);
//        }

        String finalUcode = ucode;

        CompletableFuture<DashBoardVo> dashBoardVoFuture = CompletableFuture.supplyAsync(() -> dashBoardService.dashBoardFive(finalUcode));
        CompletableFuture<DashBoardVo> dashBoardVoFirstFuture = CompletableFuture.supplyAsync(() -> dashBoardService.dashBoardFiveFirst(finalUcode));

        CompletableFuture.allOf(dashBoardVoFuture,dashBoardVoFirstFuture).get();

//        DashBoardVo dashBoardVo22 = dashBoardService.dashBoardFive(ucode);
        DashBoardVo dashBoardVo = dashBoardVoFuture.get();
        // 查首充
//        DashBoardVo dashBoardVoFirst22 =dashBoardService.dashBoardFiveFirst(ucode);
        DashBoardVo dashBoardVoFirst = dashBoardVoFirstFuture.get();

//        dashBoardVo.setChargeNumToday(dashBoardVo.getChargeNumToday().subtract(dashBoardVoFirst.getChargeNumTodayFirst()));
//        dashBoardVo.setChargeNumYesterday(dashBoardVo.getChargeNumYesterday().subtract(dashBoardVoFirst.getChargeNumYesterdayFirst()));
//        dashBoardVo.setChargeNumTotal(dashBoardVo.getChargeNumTotal().subtract(dashBoardVoFirst.getChargeNumTotalFirst()));
        dashBoardVo.setChargeNumTodayFirst(dashBoardVoFirst.getChargeNumTodayFirst());
        dashBoardVo.setChargeNumYesterdayFirst(dashBoardVoFirst.getChargeNumYesterdayFirst());
//        dashBoardVo.setChargeNumTotalFirst(dashBoardVoFirst.getChargeNumTotalFirst());
//        redisUtil.set("dashBoardFive"+ucode,dashBoardVo,1800);
        return Rets.success(dashBoardVo);
    }
    /**
     * 充值金额
     * @return
     */
    @GetMapping(value = "/dashBoardSix1")
    public Ret dashBoardSix1() throws Exception {
        String ucode = "";
        if (isProxy()){
            ucode = getUcode();
        }
        //查询缓存
//        if (redisUtil.hasKey("dashBoardSix"+ucode)){
//            Object o = redisUtil.get("dashBoardSix" + ucode);
//            return Rets.success(o);
//        }
        String finalUcode = ucode;

        CompletableFuture<DashBoardVo> dashBoardVoFuture = CompletableFuture.supplyAsync(() -> dashBoardService.dashBoardSix(finalUcode));
        CompletableFuture<DashBoardVo> dashBoardVoFirstFuture = CompletableFuture.supplyAsync(() -> dashBoardService.dashBoardSixFirst(finalUcode));

        CompletableFuture.allOf(dashBoardVoFuture,dashBoardVoFirstFuture).get();
        DashBoardVo dashBoardVo = dashBoardVoFuture.get();
        DashBoardVo dashBoardVoFirst = dashBoardVoFirstFuture.get();
//        dashBoardVo.setChargeMoneyToday(dashBoardVo.getChargeMoneyToday().subtract(dashBoardVoFirst.getChargeMoneyTodayFirst()));
//        dashBoardVo.setChargeMoneyYesterday(dashBoardVo.getChargeMoneyYesterday().subtract(dashBoardVoFirst.getChargeMoneyYesterdayFirst()));
//        dashBoardVo.setChargeMoneyTotal(dashBoardVo.getChargeMoneyTotal().subtract(dashBoardVoFirst.getChargeMoneyTotalFirst()));
        dashBoardVo.setChargeMoneyTodayFirst(dashBoardVoFirst.getChargeMoneyTodayFirst());
        dashBoardVo.setChargeMoneyYesterdayFirst(dashBoardVoFirst.getChargeMoneyYesterdayFirst());
//        dashBoardVo.setChargeMoneyTotalFirst(dashBoardVoFirst.getChargeMoneyTotalFirst());
//        redisUtil.set("dashBoardSix"+ucode,dashBoardVo,1800);
        return Rets.success(dashBoardVo);
    }
    /**
     * 提现数量
     * @return
     */
    @GetMapping(value = "/dashBoardSeven")
    public Ret dashBoardSeven(){
        String ucode = "";
        if (isProxy()){
            ucode = getUcode();
        }
        //查询缓存
//        if (redisUtil.hasKey("dashBoardSeven"+ucode)){
//            Object o = redisUtil.get("dashBoardSeven" + ucode);
//            return Rets.success(o);
//        }
        DashBoardVo dashBoardVo =dashBoardService.dashBoardSeven(ucode);
//        redisUtil.set("dashBoardSeven"+ucode,dashBoardVo,1800);

        return Rets.success(dashBoardVo);
    }
    /**
     * 提现金额
     * @return
     */
    @GetMapping(value = "/dashBoardEight")
    public Ret dashBoardEight(){
        String ucode = "";
        if (isProxy()){
            ucode = getUcode();
        }
        //查询缓存
//        if (redisUtil.hasKey("dashBoardEight"+ucode)){
//            Object o = redisUtil.get("dashBoardEight" + ucode);
//            return Rets.success(o);
//        }
        DashBoardVo dashBoardVo =dashBoardService.dashBoardEight(ucode);
//        redisUtil.set("dashBoardEight"+ucode,dashBoardVo,1800);

        return Rets.success(dashBoardVo);
    }
    /**
     * 用户余额
     * @return
     */
    @GetMapping(value = "/dashBoardNine")
    public Ret dashBoardNine(){
        String ucode = "";
        if (isProxy()){
            ucode = getUcode();
        }
        //查询缓存
//        if (redisUtil.hasKey("dashBoardNine"+ucode)){
//            Object o = redisUtil.get("dashBoardNine" + ucode);
//            return Rets.success(o);
//        }
        DashBoardVo dashBoardVo =dashBoardService.dashBoardNine(ucode);
//        redisUtil.set("dashBoardNine"+ucode,dashBoardVo,1800);

        return Rets.success(dashBoardVo);
    }
    /**
     * 当日新增各等级Vip数量
     * @return
     */
    @GetMapping(value = "/dashBoardTen")
    public Ret dashBoardTen(){
        String ucode = "";
        if (isProxy()){
            ucode = getUcode();
        }
        //查询缓存
//        if (redisUtil.hasKey("dashBoardTen"+ucode)){
//            Object o = redisUtil.get("dashBoardTen" + ucode);
//            return Rets.success(o);
//        }
        DashBoardVo dashBoardVo =dashBoardService.dashBoardTen(ucode);
//        redisUtil.set("dashBoardTen"+ucode,dashBoardVo,1800);

        return Rets.success(dashBoardVo);
    }

    /**
     * @Description: eCharts统计
     * @Param:
     * @return:
     * @Author: Skj
     */
    @GetMapping(value = "/getECharts")
    public Ret getECharts(){
        String ucode = "";
        if (isProxy()){
            ucode = getUcode();
        }
        //查询缓存
//        if (redisUtil.hasKey("getECharts"+ucode)){
//            Object o = redisUtil.get("getECharts" + ucode);
//            return Rets.success(o);
//        }
        Map<String, List> resultMap = dashBoardService.getECharts(ucode);
//        redisUtil.set("getECharts"+ucode,resultMap,1800);

        return Rets.success(resultMap);
    }

    /**
     * @Description: 日期报表
     * @Param:
     * @return:
     * @Author: Skj
     */
    @GetMapping(value = "/getDayReport")
    public Ret getDayReport(@RequestParam String startDay, @RequestParam String endDay){
//        if (StringUtil.isEmpty(startDay) || StringUtil.isEmpty(endDay)) {
//            return Rets.failure("请选择日期");
//        }
//        String ucode = "";
//        if (isProxy()){
//            ucode = getUcode();
//        }
//        List<DayReportVo> dayReportVos = dashBoardService.getDayReport(ucode, startDay, endDay);
//        return Rets.success(dayReportVos);
        String ucode = "admin";
        if (isProxy()){
            ucode = getUcode();
        }
        //查询缓存
//        if (redisUtil.hasKey("getDayReport"+ucode)){
//            Object o = redisUtil.get("getDayReport" + ucode);
//            return Rets.success(o);
//        }
        List<HomePageTotal> homePageTotals = homePageTotalService.getDayReport(startDay, endDay, ucode);
//        redisUtil.set("getDayReport"+ucode,homePageTotals,1800);

        return Rets.success(homePageTotals);
    }

    /**
     * @Description: 删除redis缓存，更新数据
     * @Param:
     * @return:
     * @Author: zx
     */
    @GetMapping(value = "/delRedisByKey")
    public Ret delRedisByKey(@RequestParam String key){
        String ucode = "";
        if (isProxy()){
            ucode = getUcode();
        }
        redisUtil.delete(key+ucode);
        return Rets.success(true);
    }



    /**
     * 充值数量
     * @return
     */
    @GetMapping(value = "/dashBoardFive")
    public Ret dashBoardFive() throws Exception {
        String ucode = "";
        if (isProxy()){
            ucode = getUcode();
        }
        //查询缓存
//        if (redisUtil.hasKey("dashBoardFive"+ucode)){
//            Object o = redisUtil.get("dashBoardFive" + ucode);
//            return Rets.success(o);
//        }
        String finalUcode = ucode;
        DashBoardVo dashBoardVo1 = dashBoardService.dashBoardFive(finalUcode);
        DashBoardVo dashBoardVo2 = dashBoardService.dashBoardFiveFirst(finalUcode);
        DashBoardVo dashBoardVo = new DashBoardVo();
        dashBoardVo.setChargeNumToday(dashBoardVo1.getChargeNumToday());
        dashBoardVo.setChargeNumYesterday(dashBoardVo1.getChargeNumYesterday());
        dashBoardVo.setChargeNumTotal(dashBoardVo1.getChargeNumTotal());
        dashBoardVo.setChargeNumTodayFirst(dashBoardVo2.getChargeNumTodayFirst());
        dashBoardVo.setChargeNumYesterdayFirst(dashBoardVo2.getChargeNumYesterdayFirst());
//        redisUtil.set("dashBoardFive"+ucode,dashBoardVo,1800);
        return Rets.success(dashBoardVo);
    }
    /**
     * 充值金额
     * @return
     */
    @GetMapping(value = "/dashBoardSix")
    public Ret dashBoardSix() throws Exception {
        String ucode = "";
        if (isProxy()){
            ucode = getUcode();
        }
        //查询缓存
//        if (redisUtil.hasKey("dashBoardSix"+ucode)){
//            Object o = redisUtil.get("dashBoardSix" + ucode);
//            return Rets.success(o);
//        }
        String finalUcode = ucode;

//        CompletableFuture<DashBoardVo> dashBoardVoFuture = CompletableFuture.supplyAsync(() -> dashBoardService.dashBoardSix(finalUcode));
//        CompletableFuture<DashBoardVo> dashBoardVoFirstFuture = CompletableFuture.supplyAsync(() -> dashBoardService.dashBoardSixFirst(finalUcode));

        DashBoardVo dashBoardVo1 = dashBoardService.dashBoardSix(finalUcode);
        DashBoardVo dashBoardVo2 = dashBoardService.dashBoardSixFirst(finalUcode);
        DashBoardVo dashBoardVo = new DashBoardVo();
        dashBoardVo.setChargeMoneyToday(dashBoardVo1.getChargeMoneyToday());
        dashBoardVo.setChargeMoneyYesterday(dashBoardVo1.getChargeMoneyYesterday());
        dashBoardVo.setChargeMoneyTotal(dashBoardVo1.getChargeMoneyTotal());
        dashBoardVo.setChargeMoneyTodayFirst(dashBoardVo2.getChargeMoneyTodayFirst());
        dashBoardVo.setChargeMoneyYesterdayFirst(dashBoardVo2.getChargeMoneyYesterdayFirst());
//        redisUtil.set("dashBoardSix"+ucode,dashBoardVo,1800);
        return Rets.success(dashBoardVo);
    }


}