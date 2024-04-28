package cn.rh.flash.bean.dto.api;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PrizeExchangeDto {

    @ApiModelProperty(value = "奖品ID")
    @NotBlank(message = "idw_cannot_be_blank")
    private String idw;

}
