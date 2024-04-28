package cn.rh.flash.bean.vo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("VIP订单列表")
public class VipPurchaseVo {

    @ApiModelProperty("会员类型")
    private String afterVipType;

    @ApiModelProperty("订单编号")
    private String idw;

    @ApiModelProperty("金额")
    private Double paymentAmount;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("状态  1:未支付,2:已支付 ")
    private Integer flg;

}
