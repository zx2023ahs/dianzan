package cn.rh.flash.bean.dto.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
@ApiModel(value = "VipMessageDetailDto", description = "会员详情参数")
public class VipMessageDetailDto {

    @ApiModelProperty("idw")
    @NotNull(message = "idw not null")
    private String idw;

    private String langCode;
}