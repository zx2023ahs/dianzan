package cn.rh.flash.bean.vo.dz;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class UserInformation {
    private String account; //账号
    private String vipLevel; //VIP级别
    private Date lastLoginTime; //最后登录时间
    private Date lastMissionTime; //最后任务时间
    private BigDecimal totalRecharge; //充值总额
    private BigDecimal totalWithdrawal; //提现总额
    private BigDecimal totalBalance; //总余额
    private Date registrationTime; //注册时间


    public void setTotalRecharge(BigDecimal totalRecharge) {
        totalRecharge = totalRecharge==null?BigDecimal.ZERO:totalRecharge;

        this.totalRecharge = totalRecharge;
    }


    public void setTotalWithdrawal(BigDecimal totalWithdrawal) {
        totalWithdrawal = totalWithdrawal==null?BigDecimal.ZERO:totalWithdrawal;

        this.totalWithdrawal = totalWithdrawal;
    }

    public void setTotalBalance(BigDecimal totalBalance) {
        totalBalance = totalBalance==null?BigDecimal.ZERO:totalBalance;

        this.totalBalance = totalBalance;
    }


}
