package cn.rh.flash.bean.dto.api;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "TeamDto", description = "")
public class TeamDto {

//    @ApiModelProperty("邀请码")
//    @NotNull(message = "invitationCode_cannot_be_null")
//    private String invitationCode;

    @NotNull(message = "pageNo not blank") //页码不能为空
    @ApiModelProperty("页码")
    private Integer pageNo;

    @NotNull(message = "pageSize not blank") //页数量不能为空
    @ApiModelProperty("页数量")
    private Integer pageSize;

    @NotNull(message = "levels not blank") //页数量不能为空
    @ApiModelProperty("当前页码层级，必传")
    private Integer levels;

    @ApiModelProperty("用户账号后四位尾号，用于模糊搜索")
    private String accountFragment;
}
