package cn.rh.flash.bean.vo.api;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel( "红包信息" )
public class RedEnvelopeVo {

    @ApiModelProperty("领取时间")
    private Date collectionTime;

    @ApiModelProperty("领取金额")
    private Double collectionMoney;


}
