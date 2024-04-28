package cn.rh.flash.bean.vo.api;


import cn.rh.flash.utils.ImageUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("订单详情结果")
public class TaskOrderDetailVo {

    @ApiModelProperty("idw")
    private String idw;

    @ApiModelProperty("审核时间")
    private Date reviewTime;

    @ApiModelProperty("任务链接")
    private String taskLink;

    @ApiModelProperty("任务名称")
    private String name;

    @ApiModelProperty("接单要求")
    private String orderRequest;

    @ApiModelProperty("作者头像")
    private String authorAvatar;

    @ApiModelProperty("作者名称")
    private String authorName;

    @ApiModelProperty("作者需求")
    private String authorNeeds;

    @ApiModelProperty("任务状态")
    private Integer taskStatus;

    @ApiModelProperty("图片")
    private String img;

    public String getAuthorAvatar() {
        return ImageUtil.getImage(authorAvatar);
    }

    public String getImg() {
        return ImageUtil.getImage(img);
    }
}
