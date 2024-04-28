package cn.rh.flash.bean.dto.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


/**
 * @author yc
 */
@NoArgsConstructor
@Data
@ApiModel(description = "注册对象参数",value = "RegV1Dto" )
public class RegV1Dto {
    @ApiModelProperty(value = "邀请码")
    @NotBlank(message = "invitationCode_cannot_be_empty")  // 邀请码不能为空
    private String invitationCode;
    @ApiModelProperty(value = "国家码")
    @NotBlank(message = "countryCode_cannot_be_empty")  // 国家码不能为空
    private String countryCode;
    @ApiModelProperty(value = "账号")
    @NotBlank(message = "account_cannot_be_empty")  // 账号不能为空
    private String account;
    @ApiModelProperty(value = "密码")
    @NotBlank(message = "password_can_not_be_blank") // 密码不能为空
    private String password;
    @ApiModelProperty(value = "图形验证码")
    @NotBlank(message = "validateCode_can_not_be_blank") // 图形验证码不能为空
    private String validateCode;
    @ApiModelProperty(value = "手机验证码")
    private String phoneValidateCode;
//    @NotBlank(message = "realName_can_not_be_blank") // 真实姓名不能为空
    @Pattern(regexp = "^(?!.*script).*",message = "PARAM_NOT_EXIST",flags={Pattern.Flag.CASE_INSENSITIVE})
    @ApiModelProperty(value = "真实姓名")
    private String realName;
}
