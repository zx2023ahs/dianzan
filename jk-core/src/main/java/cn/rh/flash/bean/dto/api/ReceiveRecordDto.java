package cn.rh.flash.bean.dto.api;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "ReceiveRecordDto", description = "查看领取收益列表")
public class ReceiveRecordDto {

    @ApiModelProperty("任务ID")
    private String taskidw;

    @ApiModelProperty("页")
    private Integer pageNo;

    @ApiModelProperty("多少条")
    private Integer pageSize;
}
