package cn.rh.flash.bean.vo.dz;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 用户统计数据
 */
@Data
public class TeamStatistics {

    // 新增用户 / 昨日新增用户 / 总数  / 今日新增vip / 今日首充() / 昨日新增vip / 昨日首充() / vip总数 / 今日充值总金额 / 昨日充值总金额 / 总充值 /  今日提现总金额 / 昨日提现总金额 / 总提现 /
    private BigDecimal addedToday ; //今日新增
    private BigDecimal addedYesterday; //昨日新增
    private BigDecimal total; //总数

    private BigDecimal addedVipToday; //今日新增vip
    private BigDecimal addedVipYesterday; //昨日新增vip

    private BigDecimal totalNumberOfVips; //vip总数

    private BigDecimal firstChargeToday; //今日首充
    private BigDecimal firstChargeYesterday; //昨日首充

    private BigDecimal totalRechargeAmountToday; //今日充值总金额
    private BigDecimal totalRechargeAmountYesterday; //昨日充值总金额
    private BigDecimal totalRecharge; //总充值

    private BigDecimal totalRechargeNumToday; //今日充值总次数
    private BigDecimal totalRechargeNumYesterday; //昨日充值总次数
    private BigInteger totalNum; //总次数

    private BigDecimal firstTotalNumWithToday; //今日首充次数
    private BigDecimal firstTheTotalNumWithYesterday; //昨日首充次数

    private BigDecimal totalAmountWithdrawnToday; //今日提现总金额
    private BigDecimal theTotalAmountWithdrawnYesterday; //昨日提现总金额
    private BigDecimal totalWithdrawal; //总提现

    private BigDecimal jjYesterday; //昨日晋级
    private BigDecimal jjToday; //晋级今天

    public void setAddedToday(BigDecimal addedToday) {
        addedToday = addedToday==null?BigDecimal.ZERO:addedToday;
        this.addedToday = addedToday;
    }

    public void setAddedYesterday(BigDecimal addedYesterday) {
        addedYesterday = addedYesterday==null?BigDecimal.ZERO:addedYesterday;

        this.addedYesterday = addedYesterday;
    }

    public void setTotal(BigDecimal total) {
        total = total==null?BigDecimal.ZERO:total;

        this.total = total;
    }

    public void setAddedVipToday(BigDecimal addedVipToday) {
        addedVipToday = addedVipToday==null?BigDecimal.ZERO:addedVipToday;

        this.addedVipToday = addedVipToday;
    }

    public void setAddedVipYesterday(BigDecimal addedVipYesterday) {
        addedVipYesterday = addedVipYesterday==null?BigDecimal.ZERO:addedVipYesterday;

        this.addedVipYesterday = addedVipYesterday;
    }

    public void setTotalNumberOfVips(BigDecimal totalNumberOfVips) {
        totalNumberOfVips = totalNumberOfVips==null?BigDecimal.ZERO:totalNumberOfVips;

        this.totalNumberOfVips = totalNumberOfVips;
    }

    public void setFirstChargeToday(BigDecimal firstChargeToday) {
        firstChargeToday = firstChargeToday==null?BigDecimal.ZERO:firstChargeToday;

        this.firstChargeToday = firstChargeToday;
    }

    public void setFirstChargeYesterday(BigDecimal firstChargeYesterday) {
        firstChargeYesterday = firstChargeYesterday==null?BigDecimal.ZERO:firstChargeYesterday;

        this.firstChargeYesterday = firstChargeYesterday;
    }

    public void setTotalRechargeAmountToday(BigDecimal totalRechargeAmountToday) {
        totalRechargeAmountToday = totalRechargeAmountToday==null?BigDecimal.ZERO:totalRechargeAmountToday;

        this.totalRechargeAmountToday = totalRechargeAmountToday;
    }

    public void setTotalRechargeAmountYesterday(BigDecimal totalRechargeAmountYesterday) {
        totalRechargeAmountYesterday = totalRechargeAmountYesterday==null?BigDecimal.ZERO:totalRechargeAmountYesterday;

        this.totalRechargeAmountYesterday = totalRechargeAmountYesterday;
    }

    public void setTotalRecharge(BigDecimal totalRecharge) {
        totalRecharge = totalRecharge==null?BigDecimal.ZERO:totalRecharge;

        this.totalRecharge = totalRecharge;
    }

    public void setTotalAmountWithdrawnToday(BigDecimal totalAmountWithdrawnToday) {
        totalAmountWithdrawnToday = totalAmountWithdrawnToday==null?BigDecimal.ZERO:totalAmountWithdrawnToday;

        this.totalAmountWithdrawnToday = totalAmountWithdrawnToday;
    }

    public void setTheTotalAmountWithdrawnYesterday(BigDecimal theTotalAmountWithdrawnYesterday) {
        theTotalAmountWithdrawnYesterday = theTotalAmountWithdrawnYesterday==null?BigDecimal.ZERO:theTotalAmountWithdrawnYesterday;

        this.theTotalAmountWithdrawnYesterday = theTotalAmountWithdrawnYesterday;
    }

    public void setTotalWithdrawal(BigDecimal totalWithdrawal) {
        totalWithdrawal = totalWithdrawal==null?BigDecimal.ZERO:totalWithdrawal;

        this.totalWithdrawal = totalWithdrawal;
    }
}
