package cn.rh.flash.bean.vo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel("收益列表")
public class InComeVo {

    @ApiModelProperty("订单编号")
    private String orderNumber;

    @ApiModelProperty("金额")
    private BigDecimal money;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("交易类型  1:充值,2:提现,3:平台赠送,4:平台扣款,5:充电宝返佣,8:vip开通返佣,9:团队任务收益,10:购买vip,11:注册,101:提现拒绝 ")
    private String transactionType;

    @ApiModelProperty("备注")
    private String remark;


}
