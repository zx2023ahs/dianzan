package cn.rh.flash.bean.dto.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ReceiveTaskDto", description = "接受任务")
public class ReceiveTaskDto {

    @NotNull(message = "taskId not null") //任务ID不能为空
    @ApiModelProperty("任务IDw")
    private String taskIdw;

}
