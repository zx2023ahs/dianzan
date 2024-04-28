package cn.rh.flash.bean.vo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel( "支付信息" )
public class RechargeOrderVo {

    //
    @ApiModelProperty("支付币种地址")
    private String address;

    //
    @ApiModelProperty("支付地址")
    private String payUrl;

    //
    @ApiModelProperty("过期时间")
    private long expireTime;

    //
    @ApiModelProperty("金额")
    private String amount;

    //
    @ApiModelProperty("支付币种  eg: USDT.TRC20")
    private String coinCode;

    //
    @ApiModelProperty("有效时时长 单位：秒")
    private long validTime = 2*3600;

}
