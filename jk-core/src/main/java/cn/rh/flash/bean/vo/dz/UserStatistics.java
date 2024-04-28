package cn.rh.flash.bean.vo.dz;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class UserStatistics {
    private BigDecimal addedToday; //今日新增
    private BigDecimal  addedYesterday; //昨日新增
    private BigDecimal  freezeAccount; // 冻结账号
    private BigDecimal  normalAccount; //正常账号

    public void setAddedToday(BigDecimal addedToday) {
        addedToday = addedToday==null?BigDecimal.ZERO:addedToday;
        this.addedToday = addedToday;
    }

    public void setAddedYesterday(BigDecimal addedYesterday) {
        addedYesterday = addedYesterday==null?BigDecimal.ZERO:addedYesterday;
        this.addedYesterday = addedYesterday;
    }

    public void setFreezeAccount(BigDecimal freezeAccount) {
        freezeAccount = freezeAccount==null?BigDecimal.ZERO:freezeAccount;
        this.freezeAccount = freezeAccount;
    }

    public void setNormalAccount(BigDecimal normalAccount) {
        normalAccount = normalAccount==null?BigDecimal.ZERO:normalAccount;
        this.normalAccount = normalAccount;
    }
}
