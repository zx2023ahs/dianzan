package cn.rh.flash.bean.vo.api;

import cn.rh.flash.utils.ImageUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("会员信息返回结果")
public class VipMessageVo {
    @ApiModelProperty("idw")
    private String idw;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("昵称")
    private String nick;
    @ApiModelProperty("ViP类型")
    private String vipType;
    @ApiModelProperty("ViP图片")
    private String vipImg;
    @ApiModelProperty("ViP背景")
    private String vipBackGround;
    @ApiModelProperty("是否已购买, 2未购买 1已购买 3续费 ")
    private int flg;
    @ApiModelProperty("售价")
    private Double sellingPrice;
    @ApiModelProperty("有效天数")
    private Integer validDate;
    @ApiModelProperty("充电宝数量")
    private Integer numberOfTasks;
    @ApiModelProperty("按钮颜色")
    private String buttonColor;
    @ApiModelProperty("字体颜色")
    private String color;
    @ApiModelProperty("每日收入")
    private Double dailyIncome;
    @ApiModelProperty("状态")
    private Integer dzstatus;

    public String getVipImg() {
        return ImageUtil.getImage(vipImg);
    }

    public String getVipBackGround() {
        return ImageUtil.getImage(vipBackGround);
    }

    public Double getDailyIncome() {
        return dailyIncome==null ? 0.0 : dailyIncome;
    }
}
