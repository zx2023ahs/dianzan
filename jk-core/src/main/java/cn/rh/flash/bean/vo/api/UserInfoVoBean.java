package cn.rh.flash.bean.vo.api;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class UserInfoVoBean {

    //直冲总金额
    private BigDecimal totalBonusIncome;
    // 充电宝返佣金额
    private BigDecimal totalBonuspb;
    //下3级充电宝返佣金额
    private BigDecimal dwTotalBonuspb;
    // 下3级购买vip返佣金额
    private BigDecimal dwPayVip;

    // 开通vip 返佣
//    private BigDecimal openPayVip;

    //当日自己收益
    private BigDecimal vipToDay;
    private BigDecimal brToDay;
    private BigDecimal cdbToDay;
    // 当日下级注册奖励
    private BigDecimal reg;

    // 下级注册奖励
    private BigDecimal allReg;

    // 用户余额
    private BigDecimal userBalance;


    // 总充值金额
    private BigDecimal rechargeAmount;
    // 总提现金额
    private BigDecimal withdrawalAmount;
    // 购买vip支出
    private BigDecimal payVip;
    // 团队人数
    private BigInteger teams;

    // 中奖金额
    private BigDecimal winningAmount;

    // 加扣款+日常收益明细+下级返佣明细（晋级，收益）
    // totalBonusIncome totalBonuspb dwTotalBonuspb dwPayVip  rechargeAmount  - withdrawalAmount payVip

    // 当日收益+下级晋级返佣+下级收益返佣+注册奖励
    // vipToDay brToDay cdbToDay dwPayVip reg


    public BigDecimal getTotalBonusIncome() {
        return totalBonusIncome == null ? BigDecimal.ZERO : totalBonusIncome;
    }

    public BigDecimal getTotalBonuspb() {
        return totalBonuspb == null ? BigDecimal.ZERO : totalBonuspb;
    }

    public BigDecimal getDwTotalBonuspb() {
        return dwTotalBonuspb == null ? BigDecimal.ZERO : dwTotalBonuspb;
    }

    public BigDecimal getDwPayVip() {
        return dwPayVip == null ? BigDecimal.ZERO : dwPayVip;
    }

    public BigDecimal getVipToDay() {
        return vipToDay == null ? BigDecimal.ZERO : vipToDay;
    }

    public BigDecimal getBrToDay() {
        return brToDay == null ? BigDecimal.ZERO : brToDay;
    }

    public BigDecimal getCdbToDay() {
        return cdbToDay == null ? BigDecimal.ZERO : cdbToDay;
    }

    public BigDecimal getReg() {
        return reg == null ? BigDecimal.ZERO : reg;
    }

    public BigDecimal getRechargeAmount() {
        return rechargeAmount == null ? BigDecimal.ZERO : rechargeAmount;
    }

    public BigDecimal getWithdrawalAmount() {
        return withdrawalAmount == null ? BigDecimal.ZERO : withdrawalAmount;
    }

    public BigDecimal getPayVip() {
        return payVip == null ? BigDecimal.ZERO : payVip;
    }

    public BigInteger getTeams() {
        return teams == null ? BigInteger.ZERO : teams;
    }

    public BigDecimal getWinningAmount() {
        return winningAmount == null ? BigDecimal.ZERO : winningAmount;
    }
}
