package cn.rh.flash.bean.vo.api;

import cn.rh.flash.utils.BigDecimalUtils;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel("今日收入查询结果")
public class TodayRevenueVo {

    private BigDecimal vipReward;

    private BigDecimal bonusReward;

    private BigDecimal reward;

    public double getReward() {
        Double add = BigDecimalUtils.add( vipReward.doubleValue(), bonusReward.doubleValue() );
        return add;
    }
}
