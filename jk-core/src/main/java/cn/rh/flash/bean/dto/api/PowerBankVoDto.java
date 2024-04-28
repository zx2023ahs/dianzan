package cn.rh.flash.bean.dto.api;

import cn.rh.flash.bean.core.CheckValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "PowerBankVoDto", description = "获取返佣列表")
public class PowerBankVoDto {

    @ApiModelProperty(" 1进行中的， 2 已完成的")
    @CheckValue(intValues = {1, 2}, isRequire = true)
    private Integer flg;

}