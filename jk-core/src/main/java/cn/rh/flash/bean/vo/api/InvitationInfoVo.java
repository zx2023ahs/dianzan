package cn.rh.flash.bean.vo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("邀请页面结果")
public class InvitationInfoVo {

    @ApiModelProperty("邀请码")
    private String invitationCode;

    @ApiModelProperty("团队会员人数")
    private Integer vipFriends = 0;

    @ApiModelProperty("团队会员返佣")
    private Double teamVipActivationTotalRevenue;

    @ApiModelProperty("邀请链接")
    private String invitationLink;

}
