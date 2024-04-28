package cn.rh.flash.bean.dto.api;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "DrawIncomeDto", description = "手动领取收益")
public class DrawIncomeDto {

    @ApiModelProperty("主键集合")
    private List<String> idws;
}
