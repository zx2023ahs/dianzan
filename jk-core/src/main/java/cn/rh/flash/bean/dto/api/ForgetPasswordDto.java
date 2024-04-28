package cn.rh.flash.bean.dto.api;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ForgetPasswordDto {

    @ApiModelProperty(value = "国家码")
    @NotBlank(message = "countryCode_cannot_be_empty")  // 国家码不能为空
    private String countryCode;

    @ApiModelProperty(value = "账号")
    @NotBlank(message = "account_cannot_be_empty")  // 帐号不能为空
    private String account;

    @ApiModelProperty(value = "验证码")
    @NotBlank(message = "validateCode_cannot_be_empty")  // 验证码不能为空
    private String validateCode;

    @ApiModelProperty(value = "密码")
    @NotBlank(message = "password_cannot_be_empty")  // 密码不能为空
    private String password;

}
