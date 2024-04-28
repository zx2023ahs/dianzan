package cn.rh.flash.bean.dto.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "UnameDto", description = "用户名称参数")
public class UnameDto {

    @ApiModelProperty("用户名称")
    @NotBlank(message = "name not blank")
    private String name;

}
