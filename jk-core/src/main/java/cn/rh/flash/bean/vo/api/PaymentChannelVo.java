package cn.rh.flash.bean.vo.api;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("支付渠道列表")
public class PaymentChannelVo {

    @ApiModelProperty("通道名称")//银行名称
    private String channelName;

    @ApiModelProperty("通道前端显示")
    private String channelValue;

    @ApiModelProperty("币种")
    private String currency;
    @ApiModelProperty("货币代码")//卡号
    private String currencyCode;
    private String remark;//姓名
    private Integer isPayment;//充值是否开启 1开启
    private Integer isWithdrawal;//提现是否开启

    private Integer sort;//排序

}
