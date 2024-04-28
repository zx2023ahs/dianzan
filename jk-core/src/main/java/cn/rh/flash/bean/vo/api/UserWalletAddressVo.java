package cn.rh.flash.bean.vo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "UserWalletAddressVo", description = "用户绑定钱包地址Vo")
public class UserWalletAddressVo {

    @ApiModelProperty("钱包地址")
    private String walletAddress;

    @ApiModelProperty("平台名称")
    private String platformName;

    @ApiModelProperty("姓名")
    private String walletName;

    @ApiModelProperty("通道类型")
    private String channelType;
}
