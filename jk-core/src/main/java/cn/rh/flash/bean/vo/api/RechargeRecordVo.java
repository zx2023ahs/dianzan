package cn.rh.flash.bean.vo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("支付订单列表")
public class RechargeRecordVo{


    @ApiModelProperty("订单编号")
    private String orderNumber;

    @ApiModelProperty("金额")
    private Double money;

    @ApiModelProperty("充值状态")
    private String rechargeStatus;

    @ApiModelProperty("创建时间")
    private Date createTime;


}
