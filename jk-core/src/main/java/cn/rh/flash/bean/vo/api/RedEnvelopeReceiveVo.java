package cn.rh.flash.bean.vo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel( "红包领取信息" )
public class RedEnvelopeReceiveVo {

    @ApiModelProperty("次数")
    private Integer count;

    @ApiModelProperty("金额")
    private Double money;


}
