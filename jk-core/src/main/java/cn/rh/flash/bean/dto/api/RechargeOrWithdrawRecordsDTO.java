package cn.rh.flash.bean.dto.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;


@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "RechargeOrWithdrawDTO", description = "充值提现会员购买订单列表参数")
public class RechargeOrWithdrawRecordsDTO {

    @NotNull(message = "pageNo not blank") //页码不能为空
    @ApiModelProperty("页码")
    private Integer pageNo;

    @NotNull(message = "pageSize not blank") //页数量不能为空
    @ApiModelProperty("页数量")
    private Integer pageSize;
}