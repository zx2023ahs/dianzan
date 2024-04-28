package cn.rh.flash.bean.dto.api;

import cn.rh.flash.bean.core.CheckValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "BuyVipDto", description = "购买VIP")
public class BuyVipDto {

    @NotNull(message = "vipIdw not null") //vipId不能为空
    @ApiModelProperty("vipIdw")
    private String vipIdw;

    @CheckValue(intValues = {1, 2 , 3}, message = "payment_method_error", isRequire = true)
    @ApiModelProperty(value = "paymentMethod",notes = "支付方式  1:余额支付,2:USDT，3:元")
    private Integer paymentMethod;

    @ApiModelProperty("paymentPassword")
    private String paymentPassword;

    @ApiModelProperty("channelName")
    private String channelName;

    @ApiModelProperty("channelType")
    private String channelType;
}
