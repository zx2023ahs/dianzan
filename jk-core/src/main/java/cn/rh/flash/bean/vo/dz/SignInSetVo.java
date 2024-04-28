package cn.rh.flash.bean.vo.dz;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SignInSetVo {

    @ApiModelProperty("签到列表")
    private List<SignInSetSubVo> list;

    @ApiModelProperty("我的积分")
    private Integer score = 0;

}
