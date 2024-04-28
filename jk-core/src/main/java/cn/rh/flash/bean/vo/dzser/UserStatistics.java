package cn.rh.flash.bean.vo.dzser;

import lombok.Data;

import java.math.BigDecimal;


/**
 * 用户统计数据
 */
@Data
public class UserStatistics {

    private BigDecimal  addedToday; //今日新增
    private BigDecimal  addedYesterday; //昨日新增
    private BigDecimal  freezeAccount; // 冻结账号
    private BigDecimal  normalAccount; //正常账号

}
