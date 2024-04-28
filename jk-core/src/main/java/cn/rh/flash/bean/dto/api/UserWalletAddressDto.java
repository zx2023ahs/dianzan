package cn.rh.flash.bean.dto.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@ApiModel(value = "UserWalletAddressDto", description = "用户绑定钱包地址参数")
public class UserWalletAddressDto {


    @ApiModelProperty("钱包地址")
    @NotBlank(message = "walletAddress not blank")
    @Pattern(regexp = "^(?!.*script).*",message = "PARAM_NOT_EXIST",flags={Pattern.Flag.CASE_INSENSITIVE})
    private String walletAddress;

    @ApiModelProperty("平台名称")
    @NotBlank(message = "platformName not blank")
    @Pattern(regexp = "^(?!.*script).*",message = "PARAM_NOT_EXIST",flags={Pattern.Flag.CASE_INSENSITIVE})
    private String platformName;

    @ApiModelProperty("姓名")
    @NotBlank(message = "wallet_name not blank")
    @Pattern(regexp = "^(?!.*script).*",message = "PARAM_NOT_EXIST",flags={Pattern.Flag.CASE_INSENSITIVE})
    private String walletName;

    @ApiModelProperty("通道类型")
    @NotBlank(message = "channelType not blank")
    @Pattern(regexp = "(USDT\\.(TRC20|Polygon))|(other)|(bank)",message = "channel_type_error")//通道类型
    private String channelType;




}
