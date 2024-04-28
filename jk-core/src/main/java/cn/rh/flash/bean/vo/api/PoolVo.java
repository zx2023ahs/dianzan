package cn.rh.flash.bean.vo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("poolVo")
public class PoolVo {

    @ApiModelProperty("公积金池金额")
    private Double amount;

    @ApiModelProperty("图片")
    private String img;

    @ApiModelProperty("版本")
    private String version;
}
