package cn.rh.flash.bean.vo.api;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("国家码返回结果")
public class CountryCodeVo{
    @ApiModelProperty("国家英文名称")
    private String countryNameEnglish;
    @ApiModelProperty("国家码")
    private String countryCode;
}
