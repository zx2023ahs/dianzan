package cn.rh.flash.bean.dto.api;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "TeamTwoDto", description = "")
public class TeamTwoDto {

    @ApiModelProperty("邀请码")
    @NotNull(message = "invitationCode_cannot_be_null")
    private String invitationCode;

    @NotNull(message = "pageNo not blank") //页码不能为空
    @ApiModelProperty("页码")
    private Integer pageNo;

    @NotNull(message = "pageSize not blank") //页数量不能为空
    @ApiModelProperty("页数量")
    private Integer pageSize;

    @ApiModelProperty("用户账号后四位尾号，用于模糊搜索")
    private String accountFragment;
}
