package cn.rh.flash.bean.vo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("公积金池用户数据")
public class MyPoolInformationVo {


    @ApiModelProperty("公积金捐献等级")
    private String poolLevel;

    @ApiModelProperty("公积金申请次数")
    private int poolNumber;

    @ApiModelProperty("捐献金额")
    private double userAmount;
}
