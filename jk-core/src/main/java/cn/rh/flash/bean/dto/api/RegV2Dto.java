package cn.rh.flash.bean.dto.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@NoArgsConstructor
@Data
@ApiModel(description = "注册对象参数",value = "RegV2Dto" )
public class RegV2Dto {
    @ApiModelProperty(value = "邀请码")
    @NotBlank(message = "PARAM_NOT_EXIST")  // 邀请码不能为空
    private String invitationCode;
    @ApiModelProperty(value = "国家码")
    @NotBlank(message = "PARAM_NOT_EXIST")  // 国家码不能为空
    private String countryCode;
    @ApiModelProperty(value = "账号")
    @NotBlank(message = "PARAM_NOT_EXIST")  // 账号不能为空
    private String account;
    @ApiModelProperty(value = "密码")
    @NotBlank(message = "PARAM_NOT_EXIST") // 密码不能为空
    private String password;
    @ApiModelProperty(value = "图形验证码")
    @NotBlank(message = "PARAM_NOT_EXIST") // 图形验证码不能为空
    private String validateCode;
    @ApiModelProperty(value = "随机码")
    @NotBlank(message = "PARAM_NOT_EXIST") // 随机码不能为空
    private String uuid;
    @ApiModelProperty(value = "手机验证码")
    private String phoneValidateCode;
    @Pattern(regexp = "^(?!.*script).*",message = "PARAM_NOT_EXIST",flags={Pattern.Flag.CASE_INSENSITIVE})
    @ApiModelProperty(value = "真实姓名")
    private String realName;
}
