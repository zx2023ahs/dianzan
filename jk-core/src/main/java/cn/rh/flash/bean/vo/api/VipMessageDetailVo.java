package cn.rh.flash.bean.vo.api;

import cn.rh.flash.utils.ImageUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("会员信息详情返回结果")
public class VipMessageDetailVo {
    @ApiModelProperty("售价")
    private Double sellingPrice;
    @ApiModelProperty("有效天数")
    private Integer validDate;
    @ApiModelProperty("ViP图片")
    private String vipImg;
    @ApiModelProperty("任务数量")
    private Integer numberOfTasks;
    @ApiModelProperty("日收入")
    private Double dailyIncome;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("ViP类型")
    private String vipType;
    @ApiModelProperty("idw")
    private String idw;
    @ApiModelProperty("按钮颜色")
    private String buttonColor;
    @ApiModelProperty("字体颜色")
    private String color;

    @ApiModelProperty("vip背景图")
    private String vipBackGround;

    private String langContext;

    @ApiModelProperty("是否已购买, 2未购买 1已购买 3续费 ")
    private int flg;


    public String getVipImg() {
        return ImageUtil.getImage(vipImg);
    }
    public String getVipBackGround() {
        return ImageUtil.getImage(vipBackGround);
    }

    public Double getDailyIncome() {
        return dailyIncome== null ? 0.0 : dailyIncome;
    }
}
