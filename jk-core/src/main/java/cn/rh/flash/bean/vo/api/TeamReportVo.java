package cn.rh.flash.bean.vo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("团队报告结果")
public class TeamReportVo {

    @ApiModelProperty("L1累计收益")
    private Double l1Commission = 0.0;

    @ApiModelProperty("L2累计收益")
    private Double l2Commission = 0.0;

    @ApiModelProperty("L3累计收益")
    private Double l3Commission = 0.0;
//
//    @ApiModelProperty("L1用户累计收益")
//    private List<UserInfoSubVo> l1Records;
//
//    @ApiModelProperty("L2用户累计收益")
//    private List<UserInfoSubVo> l2Records;
//
//    @ApiModelProperty("L3用户累计收益")
//    private List<UserInfoSubVo> l3Records;

}
