package cn.rh.flash.bean.vo.dz;


import lombok.Data;

import java.math.BigDecimal;


/** 
* @Description: 首页ECharts图数据
* @Author: Skj(老子真TM帅)
* @Date: 2023/2/27 
*/

@Data
public class EChartsVo {

    // 日期
    private String day;

    // 充值金额
    private BigDecimal cmoney;

    // 提现金额
    private BigDecimal tmoney;

    // 充提差
    private BigDecimal money;

}
