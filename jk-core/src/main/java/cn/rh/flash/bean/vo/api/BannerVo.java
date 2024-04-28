package cn.rh.flash.bean.vo.api;


import cn.rh.flash.utils.ImageUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("轮播图")
public class BannerVo {

    @ApiModelProperty("图片")
    private String image;

    @ApiModelProperty("轮播图类型")
    private String bannerType;

    @ApiModelProperty("跳转链接")
    private String jumpLink;

    public String getImage() {
        return ImageUtil.getImage(image);
    }
}
