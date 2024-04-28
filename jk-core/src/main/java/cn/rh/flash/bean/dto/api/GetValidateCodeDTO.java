package cn.rh.flash.bean.dto.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;


@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "GetValidateCodeDTO", description = "图形验证码参数")
public class GetValidateCodeDTO {

    @Min(value = 1, message = "countryCode not null") //国家码不能为空
    @ApiModelProperty("国家码")
    private Integer countryCode;

    @NotBlank(message = "account not blank") //手机号不能为空
    @ApiModelProperty("手机号")
    private String account;
}