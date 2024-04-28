package cn.rh.flash.bean.vo.dz;


import lombok.Data;

import java.math.BigDecimal;

/**
* @Description: 首页统计数据VO
* @Author: Skj(老子真TM帅)
* @Date: 2023/1/10
*/
@Data
public class DashBoardVo {

    // one 平台用户
    private BigDecimal addUserToday; // 用户今日新增
    private BigDecimal addUserYesterday; // 用户昨日新增
    private BigDecimal userTotal; // 用户总数
    private BigDecimal vipTotal; // VIP总数


    // two 平台会员
    private BigDecimal addVipToday; // VIP今日新增
    private BigDecimal addVipYesterday; // VIP昨日新增

    // three 新用户做任务

    // four 总用户做任务

    // five 充值总数
    private BigDecimal chargeNumToday; // 今日充值数量(非首充)
    private BigDecimal chargeNumYesterday; // 昨日充值数量(非首充)
    private BigDecimal chargeNumTotal; // 充值总数

    private BigDecimal chargeNumTodayFirst; // 今日充值数量(首充)
    private BigDecimal chargeNumYesterdayFirst; // 昨日充值数量(首充)
//    private BigDecimal chargeNumTotalFirst; // 充值总数(首充)

    // six 充值金额
    private BigDecimal chargeMoneyToday; // 今日充值金额(非首充)
    private BigDecimal chargeMoneyYesterday; // 昨日充值金额(非首充)
    private BigDecimal chargeMoneyTotal; // 充值金额总数

    private BigDecimal chargeMoneyTodayFirst; // 今日充值金额(首充)
    private BigDecimal chargeMoneyYesterdayFirst; // 昨日充值金额(首充)
//    private BigDecimal chargeMoneyTotalFirst; // 充值金额总数(首充)

    // seven 提现数量
    private BigDecimal withNumToday; // 今日提现数量
    private BigDecimal withNumYesterday; // 昨日提现数量
    private BigDecimal withNumTotal; // 提现总数

    // eight 提现金额
    private BigDecimal withMoneyToday; // 今日提现金额
    private BigDecimal withMoneyYesterday; // 昨日提现金额
    private BigDecimal withMoneyTotal; // 提现金额总数

    // nine 用户余额
    private BigDecimal userBalance; // 普通用户余额
    private BigDecimal vipBalance; // vip用户余额
    private BigDecimal balanceTotal; // 金额总数

    // 平台当日新增各等级Vip数量
    private BigDecimal todayNewV2;
    private BigDecimal todayNewV3;
    private BigDecimal todayNewV4;
    private BigDecimal todayNewV5;
    private BigDecimal todayNewV6;
    private BigDecimal todayNewV7;
    private BigDecimal todayNewV8;
    private BigDecimal todayNewV9;
    private BigDecimal todayNewV10;


    public void setAddUserToday(BigDecimal addUserToday) {
        addUserToday = addUserToday==null?BigDecimal.ZERO:addUserToday;
        this.addUserToday = addUserToday;
    }

    public void setAddUserYesterday(BigDecimal addUserYesterday) {
        addUserYesterday = addUserYesterday==null?BigDecimal.ZERO:addUserYesterday;
        this.addUserYesterday = addUserYesterday;
    }

    public void setUserTotal(BigDecimal userTotal) {
        userTotal = userTotal==null?BigDecimal.ZERO:userTotal;
        this.userTotal = userTotal;
    }

    public void setAddVipToday(BigDecimal addVipToday) {
        addVipToday = addVipToday==null?BigDecimal.ZERO:addVipToday;
        this.addVipToday = addVipToday;
    }

    public void setAddVipYesterday(BigDecimal addVipYesterday) {
        addVipYesterday = addVipYesterday==null?BigDecimal.ZERO:addVipYesterday;
        this.addVipYesterday = addVipYesterday;
    }

    public void setVipTotal(BigDecimal vipTotal) {
        vipTotal = vipTotal==null?BigDecimal.ZERO:vipTotal;
        this.vipTotal = vipTotal;
    }

    public void setChargeNumToday(BigDecimal chargeNumToday) {
        chargeNumToday = chargeNumToday==null?BigDecimal.ZERO:chargeNumToday;
        this.chargeNumToday = chargeNumToday;
    }

    public void setChargeNumYesterday(BigDecimal chargeNumYesterday) {
        chargeNumYesterday = chargeNumYesterday==null?BigDecimal.ZERO:chargeNumYesterday;
        this.chargeNumYesterday = chargeNumYesterday;
    }

    public void setChargeNumTotal(BigDecimal chargeNumTotal) {
        chargeNumTotal = chargeNumTotal==null?BigDecimal.ZERO:chargeNumTotal;
        this.chargeNumTotal = chargeNumTotal;
    }

    public void setChargeMoneyToday(BigDecimal chargeMoneyToday) {
        chargeMoneyToday = chargeMoneyToday==null?BigDecimal.ZERO:chargeMoneyToday;
        this.chargeMoneyToday = chargeMoneyToday;
    }

    public void setChargeMoneyYesterday(BigDecimal chargeMoneyYesterday) {
        chargeMoneyYesterday = chargeMoneyYesterday==null?BigDecimal.ZERO:chargeMoneyYesterday;
        this.chargeMoneyYesterday = chargeMoneyYesterday;
    }

    public void setChargeMoneyTotal(BigDecimal chargeMoneyTotal) {
        chargeMoneyTotal = chargeMoneyTotal==null?BigDecimal.ZERO:chargeMoneyTotal;
        this.chargeMoneyTotal = chargeMoneyTotal;
    }

    public void setWithNumToday(BigDecimal withNumToday) {
        withNumToday = withNumToday==null?BigDecimal.ZERO:withNumToday;
        this.withNumToday = withNumToday;
    }

    public void setWithNumYesterday(BigDecimal withNumYesterday) {
        withNumYesterday = withNumYesterday==null?BigDecimal.ZERO:withNumYesterday;
        this.withNumYesterday = withNumYesterday;
    }

    public void setWithNumTotal(BigDecimal withNumTotal) {
        withNumTotal = withNumTotal==null?BigDecimal.ZERO:withNumTotal;
        this.withNumTotal = withNumTotal;
    }

    public void setWithMoneyToday(BigDecimal withMoneyToday) {
        withMoneyToday = withMoneyToday==null?BigDecimal.ZERO:withMoneyToday;
        this.withMoneyToday = withMoneyToday;
    }

    public void setWithMoneyYesterday(BigDecimal withMoneyYesterday) {
        withMoneyYesterday = withMoneyYesterday==null?BigDecimal.ZERO:withMoneyYesterday;
        this.withMoneyYesterday = withMoneyYesterday;
    }

    public void setWithMoneyTotal(BigDecimal withMoneyTotal) {
        withMoneyTotal = withMoneyTotal==null?BigDecimal.ZERO:withMoneyTotal;
        this.withMoneyTotal = withMoneyTotal;
    }

    public void setUserBalance(BigDecimal userBalance) {
        userBalance = userBalance==null?BigDecimal.ZERO:userBalance;
        this.userBalance = userBalance;
    }

    public void setVipBalance(BigDecimal vipBalance) {
        vipBalance = vipBalance==null?BigDecimal.ZERO:vipBalance;
        this.vipBalance = vipBalance;
    }

    public void setBalanceTotal(BigDecimal balanceTotal) {
        balanceTotal = balanceTotal==null?BigDecimal.ZERO:balanceTotal;
        this.balanceTotal = balanceTotal;
    }

    public void setTodayNewV2(BigDecimal todayNewV2) {
        todayNewV2 = todayNewV2==null?BigDecimal.ZERO:todayNewV2;
        this.todayNewV2 = todayNewV2;
    }

    public void setTodayNewV3(BigDecimal todayNewV3) {
        todayNewV3 = todayNewV3==null?BigDecimal.ZERO:todayNewV3;
        this.todayNewV3 = todayNewV3;
    }

    public void setTodayNewV4(BigDecimal todayNewV4) {
        todayNewV4 = todayNewV4==null?BigDecimal.ZERO:todayNewV4;
        this.todayNewV4 = todayNewV4;
    }

    public void setTodayNewV5(BigDecimal todayNewV5) {
        todayNewV5 = todayNewV5==null?BigDecimal.ZERO:todayNewV5;
        this.todayNewV5 = todayNewV5;
    }

    public void setTodayNewV6(BigDecimal todayNewV6) {
        todayNewV6 = todayNewV6==null?BigDecimal.ZERO:todayNewV6;
        this.todayNewV6 = todayNewV6;
    }

    public void setTodayNewV7(BigDecimal todayNewV7) {
        todayNewV7 = todayNewV7==null?BigDecimal.ZERO:todayNewV7;
        this.todayNewV7 = todayNewV7;
    }

    public void setTodayNewV8(BigDecimal todayNewV8) {
        todayNewV8 = todayNewV8==null?BigDecimal.ZERO:todayNewV8;
        this.todayNewV8 = todayNewV8;
    }

    public void setTodayNewV9(BigDecimal todayNewV9) {
        todayNewV9 = todayNewV9==null?BigDecimal.ZERO:todayNewV9;
        this.todayNewV9 = todayNewV9;
    }

    public void setTodayNewV10(BigDecimal todayNewV10) {
        todayNewV10 = todayNewV10==null?BigDecimal.ZERO:todayNewV10;
        this.todayNewV10 = todayNewV10;
    }

    public void setChargeNumTodayFirst(BigDecimal chargeNumTodayFirst) {
        chargeNumTodayFirst = chargeNumTodayFirst==null?BigDecimal.ZERO:chargeNumTodayFirst;
        this.chargeNumTodayFirst = chargeNumTodayFirst;
    }

    public void setChargeNumYesterdayFirst(BigDecimal chargeNumYesterdayFirst) {
        chargeNumYesterdayFirst = chargeNumYesterdayFirst==null?BigDecimal.ZERO:chargeNumYesterdayFirst;
        this.chargeNumYesterdayFirst = chargeNumYesterdayFirst;
    }

//    public void setChargeNumTotalFirst(BigDecimal chargeNumTotalFirst) {
//        chargeNumTotalFirst = chargeNumTotalFirst==null?BigDecimal.ZERO:chargeNumTotalFirst;
//        this.chargeNumTotalFirst = chargeNumTotalFirst;
//    }

    public void setChargeMoneyTodayFirst(BigDecimal chargeMoneyTodayFirst) {
        chargeMoneyTodayFirst = chargeMoneyTodayFirst==null?BigDecimal.ZERO:chargeMoneyTodayFirst;
        this.chargeMoneyTodayFirst = chargeMoneyTodayFirst;
    }

    public void setChargeMoneyYesterdayFirst(BigDecimal chargeMoneyYesterdayFirst) {
        chargeMoneyYesterdayFirst = chargeMoneyYesterdayFirst==null?BigDecimal.ZERO:chargeMoneyYesterdayFirst;
        this.chargeMoneyYesterdayFirst = chargeMoneyYesterdayFirst;
    }

//    public void setChargeMoneyTotalFirst(BigDecimal chargeMoneyTotalFirst) {
//        chargeMoneyTotalFirst = chargeMoneyTotalFirst==null?BigDecimal.ZERO:chargeMoneyTotalFirst;
//        this.chargeMoneyTotalFirst = chargeMoneyTotalFirst;
//    }
}
