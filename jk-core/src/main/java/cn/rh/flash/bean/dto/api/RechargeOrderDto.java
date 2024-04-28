package cn.rh.flash.bean.dto.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@ApiModel(value = "RechargeOrderDto", description = "充值订单参数")
public class RechargeOrderDto {

    @ApiModelProperty("金额")
    @NotBlank(message = "amount_cannot_be_blank")  // 金额不能为空
    @Min(value = 10,message = "recharge_min_10")
    private String amount;

    @ApiModelProperty("通道名称")
    @NotBlank(message = "channelName_cannot_be_blank")  // 通道名称不能为空
    private String channelName;

    @ApiModelProperty("通道类型")
    @Pattern(regexp = "(USDT\\.(TRC20|Polygon))|(other)|(bank)",message = "channel_type_error")//通道类型
    @NotBlank(message = "channel_type_cannot_be_blank")
    private String channelType;

}
