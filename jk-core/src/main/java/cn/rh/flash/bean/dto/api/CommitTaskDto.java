package cn.rh.flash.bean.dto.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "CommitTaskDto", description = "提交任务")
public class CommitTaskDto {

    @NotNull(message = "taskOrderIdw not null") //任务订单ID不能为空
    @ApiModelProperty("任务订单IDW")
    private String taskOrderIdw;

    @NotNull(message = "img not null") //任务图片不能为空
    @ApiModelProperty("任务图片")
    private String img;

}
