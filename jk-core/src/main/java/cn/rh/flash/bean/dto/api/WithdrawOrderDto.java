package cn.rh.flash.bean.dto.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "WithdrawOrderDto", description = "提现订单参数")
public class WithdrawOrderDto {

    @ApiModelProperty("提现金额")
    @NotNull(message = "amount_cannot_be_blank")  // 提现金额不能为空
    private Double amount;

    @ApiModelProperty("提现地址")
    @NotBlank(message = "address_cannot_be_blank")  // 提现地址不能为空
    private String address;

    @ApiModelProperty("交易密码")
    @NotBlank(message = "payPassword_cannot_be_blank")  // 交易密码不能为空
    private String payPassword;

    @ApiModelProperty("通道名称")
    @NotBlank(message = "channelName_cannot_be_blank")  // 通道名称不能为空
    private String channelName;

}
