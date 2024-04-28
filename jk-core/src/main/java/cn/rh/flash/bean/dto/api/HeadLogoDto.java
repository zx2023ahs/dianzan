package cn.rh.flash.bean.dto.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "HeadLogoDto", description = "上传头像参数")
public class HeadLogoDto {

    @ApiModelProperty("图片地址")
    @NotBlank(message = "img not blank")
    private String img;


}

