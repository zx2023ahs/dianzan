package cn.rh.flash.bean.dto.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "PasswordDto", description = "修改密码参数")
public class PasswordDto {

    @ApiModelProperty("老密码")
    @NotBlank(message = "oldPassword_cannot_be_blank")  // 老密码不能为空
    private String oldPassword;

    @ApiModelProperty("新密码")
    @NotBlank(message = "newPassword_cannot_be_blank")  // 新密码不能为空
    private String newPassword;


}

