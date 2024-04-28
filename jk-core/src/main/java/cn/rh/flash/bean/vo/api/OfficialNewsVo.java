package cn.rh.flash.bean.vo.api;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("公告")
public class OfficialNewsVo {

    @ApiModelProperty("标题'")
    private String title;

    @ApiModelProperty("公告类型'")
    private String officialType;

    @ApiModelProperty("跳转链接'")
    private String jumpLink;

    @ApiModelProperty("内容'")
    private String dzcontent;

    @ApiModelProperty("语言'")
    private String language;


}
