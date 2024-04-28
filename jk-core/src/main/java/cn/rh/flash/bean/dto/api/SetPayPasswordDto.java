package cn.rh.flash.bean.dto.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "SetPayPasswordDto", description = "修改密码参数")
public class SetPayPasswordDto {

    @ApiModelProperty("新密码")
    @NotBlank(message = "newPassword_cannot_be_blank")  // 新密码不能为空
    private String newPassword;

}
