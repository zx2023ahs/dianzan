package cn.rh.flash.bean.dto.api;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SendPhoneCodeDto {

    @ApiModelProperty(value = "国家码")
    @NotBlank(message = "countryCode_cannot_be_empty")  // 国家码不能为空
    private String countryCode;

    @ApiModelProperty(value = "账号")
    @NotBlank(message = "account_cannot_be_empty")  // 帐号不能为空
    private String account;

    @ApiModelProperty(value = "类型")
    @NotBlank(message = "type_cannot_be_empty")  // type不能为空  // resetpw 忘记密码   register 注册
    private String type;

    @ApiModelProperty(value = "验证码")
//    @NotBlank(message = "code_cannot_be_empty")  // 随机验证码
    private String code;
    @ApiModelProperty(value = "随机码")
    private String uuid;

}
