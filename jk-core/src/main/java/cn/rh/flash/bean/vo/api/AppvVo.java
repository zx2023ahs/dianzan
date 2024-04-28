package cn.rh.flash.bean.vo.api;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AppvVo {

    @ApiModelProperty("升级链接")
    private String appUrl;

    @ApiModelProperty("版本号")
    private String versionNumber;

    @ApiModelProperty("低于版本限制使用")
    private String minVersionNumber;

}
