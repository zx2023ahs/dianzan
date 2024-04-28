package cn.rh.flash.bean.dto.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Data
@ApiModel(description = "登录对象",value = "LoginV1Dto" )
public class LoginV1Dto {
    @ApiModelProperty(value = "国际码")
    @NotBlank(message = "International_code_cannot_be_empty")  // 国际码不能为空
    private String internationalCode;
    @ApiModelProperty(value = "账号")
    @NotBlank(message = "account_cannot_be_empty")  // 帐号不能为空
    private String account;
    @ApiModelProperty(value = "密码")
    @NotBlank(message = "password_cannot_be_blank")
    @Size(min = 15,message = "password_error")
    private String pwd;
}
