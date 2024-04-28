package cn.rh.flash.bean.dto.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@ApiModel(value = "SetWalletAddressDto", description = "设置地址参数")
public class SetWalletAddressDto {

    @ApiModelProperty("地址")
    @NotBlank(message = "address_cannot_be_blank")  // 地址不能为空
    private String address;

    @ApiModelProperty("通道类型")
    @Pattern(regexp = "USDT\\.(TRC20|Polygon)",message = "channel_type_error")//通道类型
    @NotBlank(message = "channel_type_cannot_be_blank")
    private String channelType;

}

