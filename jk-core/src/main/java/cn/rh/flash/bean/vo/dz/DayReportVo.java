package cn.rh.flash.bean.vo.dz;

import lombok.Data;

import java.math.BigDecimal;


/**
* @Description: 日期报表
* @Author: Skj(老子真TM帅)
* @Date: 2023/2/28
*/
@Data
public class DayReportVo {

    private String day; // 日期
    private Integer registrationNum; // 注册人数
    private Integer vipNum; // vip人数
    private Integer vipFirstNum; // vip首充人数
    private Integer cNum; // 充值数量
    private BigDecimal cMoney; // 充值金额
    private Integer tNum; // 提现数量
    private BigDecimal tMoney; // 提现金额
    private BigDecimal money; // 平台盈利
    private BigDecimal l1Vip; // l1VIP收益
    private BigDecimal l2Vip; // l2VIP收益
    private BigDecimal l3Vip; // l3VIP收益
    private BigDecimal l1Pb; // l1任务收益
    private BigDecimal l2Pb; // l2任务收益
    private BigDecimal l3Pb; // l3任务收益
    private BigDecimal totalPb; // 任务总收益
    private BigDecimal dcMoney; // 注册奖励
    private Integer pbNum; // 完成任务数量

}
