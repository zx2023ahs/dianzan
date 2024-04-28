package cn.rh.flash.bean.dto.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "PoolResortMessagDto", description = "用户求助D参数")
public class PoolResortMessagDto {

    @NotNull(message = "userPoolAmount not blank")
    @ApiModelProperty("求助金额")
    private Double userPoolAmount;

    @NotBlank(message = "content not blank")
    @ApiModelProperty("求助内容")
    private String content;
}
