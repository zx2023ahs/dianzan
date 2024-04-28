package cn.rh.flash.bean.vo.api;

import cn.rh.flash.utils.ImageUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("任务信息")
public class DzMissionInformationVo  {

    @ApiModelProperty("任务类型")
    private String taskType;
    @ApiModelProperty("VIP类型")
    private String vipType;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("logo")
    private String logo;
    @ApiModelProperty("任务链接")
    private String taskLink;
    @ApiModelProperty("佣金")
    private Double commission;
    @ApiModelProperty("余量")
    private Integer margin;
    @ApiModelProperty("总量")
    private Integer total;
    @ApiModelProperty("idw")
    private String idw;

    public String getLogo() {
        return ImageUtil.getImage(logo);
    }
}
