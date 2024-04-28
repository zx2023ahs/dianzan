package cn.rh.flash.bean.dto.api;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "LuckyDrawUserDto", description = "查询夺宝奖品列表且包含中奖用户")
public class LuckyDrawUserDto {


    @ApiModelProperty("邀请码")
    @NotNull(message = "invitationCode_cannot_be_null")
    private String invitationCode;

    @NotNull(message = "pageNo not blank") //页码不能为空
    @ApiModelProperty("页码")
    private Integer pageNo;

    @NotNull(message = "pageSize not blank") //页数量不能为空
    @ApiModelProperty("页数量")
    private Integer pageSize;



}
