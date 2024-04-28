package cn.rh.flash.bean.dto.api;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "ScoreDto", description = "用户积分参数")
public class ScoreDto {

    @ApiModelProperty("积分类型")
    @NotNull(message = "prizeType_cannot_be_null")
    private String prizeType;

    @NotNull(message = "current not blank") //页码不能为空
    @ApiModelProperty("页码")
    private Integer current;

    @NotNull(message = "size not blank") //页数量不能为空
    @ApiModelProperty("页数量")
    private Integer size;

}
