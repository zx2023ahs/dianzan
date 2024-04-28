package cn.rh.flash.bean.vo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("提现订单列表")
public class WithdrawalsRecordVo {

    @ApiModelProperty("订单编号")
    private String orderNumber;

    @ApiModelProperty("金额")
    private Double money;

    @ApiModelProperty("审核状态")
    private String rechargeStatus;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("备注")
    private String remark;

}
