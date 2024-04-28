package cn.rh.flash.bean.vo.api;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeeVo {

    @ApiModelProperty("最小值")
    private Double min;
    @ApiModelProperty("最大值")
    private Double max;
    @ApiModelProperty("手续费 20 表示 20%")
    private Double fee;

    public Double getMin() {
        return min == null ? 0.0 : min;
    }

    public Double getMax() {
        return max == null ? 0.0 : max;
    }

    public Double getFee() {
        return fee == null ? 0.0 : fee;
    }
}
