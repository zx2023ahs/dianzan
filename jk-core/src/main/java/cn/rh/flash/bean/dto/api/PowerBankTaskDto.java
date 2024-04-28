package cn.rh.flash.bean.dto.api;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "PowerBankVoDto", description = "点击开始运营充电宝")
public class PowerBankTaskDto {

    @ApiModelProperty("ID")
    private String idw;
}
