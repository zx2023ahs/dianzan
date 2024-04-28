package cn.rh.flash.bean.vo.api;

import cn.rh.flash.utils.ImageUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PowerBankVo {

    @ApiModelProperty("请上传图片")
    private String image;
    @ApiModelProperty("请输入名称")
    private String name;
    @ApiModelProperty("单天返金额")
    private Double price;
    @ApiModelProperty("状态 1 进行中  2 已结束")
    private Integer flg;

    public String getImage() {
        return ImageUtil.getImage(image);
    }

}
