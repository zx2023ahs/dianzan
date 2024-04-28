package cn.rh.flash.bean.dto.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;


@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "RechargeOrWithdrawDTO", description = "充值提现会员购买订单列表参数")
public class PageDto {

    @NotNull(message = "pageNo not blank") //页码不能为空
    @ApiModelProperty("页码")
    private Integer pageNo;

    @NotNull(message = "pageSize not blank") //页数量不能为空
    @ApiModelProperty("页数量")
    private Integer pageSize;

    @ApiModelProperty("用户邀请码 (非必传,未传查询的是当前登录用户相关信息,已传查询的是指定邀请码用户的相关信息)")
    private String invitationCode;

    @ApiModelProperty("用户层级,  当传递 用户邀请码 时候 必传 ")
    private Integer levels;

    @ApiModelProperty("用户账号后四位尾号，用于模糊搜索")
    private String accountFragment;



}