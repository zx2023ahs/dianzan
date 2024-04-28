package cn.rh.flash.bean.dto.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "OfficialNewDto", description = "公告参数")
public class OfficialNewDto {

    @ApiModelProperty(value = "公告类型")
    @NotNull(message = "officialType_cannot_be_null")  // 公告类型不能为空
    private Integer officialType;

    @ApiModelProperty(value = "语言")
//    @NotNull(message = "language_cannot_be_null")
    @NotBlank(message = "PARAM_NOT_EXIST")
    private String language;
}
