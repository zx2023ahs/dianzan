package cn.rh.flash.bean.vo.api;

import cn.rh.flash.utils.ImageUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class OnlineServeVo {

    @ApiModelProperty("客服链接'")
    private String customerServiceLink;
    @ApiModelProperty("名称'")
    private String name;
    @ApiModelProperty("类型'")
    private String onlinesType;
    @ApiModelProperty("logo'")
    private String logo;


    public String getLogo() {
        return ImageUtil.getImage( logo );
    }


}
