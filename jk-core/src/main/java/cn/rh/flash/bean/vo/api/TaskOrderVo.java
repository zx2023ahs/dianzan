package cn.rh.flash.bean.vo.api;



import cn.rh.flash.utils.ImageUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("任务订单返回结果")
public class TaskOrderVo {

    @ApiModelProperty("任务编号")
    private String missionIdw;
    @ApiModelProperty("佣金")
    private Double commission;
    @ApiModelProperty("图片")
    private String image;
    @ApiModelProperty("idw")
    private String idw;
    @ApiModelProperty("任务订单状态")
    private Integer taskOrderStatus;

    /**
     * 一下来自DzMissionInformation
     */
    @ApiModelProperty("logo")
    private String logo;
    @ApiModelProperty("任务链接")
    private String taskLink;
    @ApiModelProperty("任务类型")
    private String taskType;

    public String getImage() {
        return ImageUtil.getImage(image);
    }

    public String getLogo() {
        return ImageUtil.getImage(logo);
    }
}
