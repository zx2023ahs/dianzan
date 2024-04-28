package cn.rh.flash.bean.vo.dz;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SignInSetSubVo {

    @ApiModelProperty("奖励")
    private String reward = "0";

    @ApiModelProperty("是否签到")
    private Integer isSign = 0;

    private Integer day = 0;


}
