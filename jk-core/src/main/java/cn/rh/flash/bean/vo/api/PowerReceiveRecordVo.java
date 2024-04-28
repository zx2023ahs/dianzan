package cn.rh.flash.bean.vo.api;

import cn.rh.flash.utils.ImageUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PowerReceiveRecordVo {
    @ApiModelProperty("主键")
    private String idw;
    @ApiModelProperty("图片")
    private String image;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("VIP类型")
    private String vipType;
    @ApiModelProperty("收益")
    private Double money;
    @ApiModelProperty("运营状态 1 营业中  2 已结束")
    private Integer flg;
    @ApiModelProperty("领取状态 1 未领取 2 已领取")
    private Integer status;
    @ApiModelProperty("出租单价/小时")
    private Double payPrice;
    @ApiModelProperty("充电宝数量")
    private Integer totalQuantity;
    @ApiModelProperty("投放小时")
    private Long incomeHour;
    @ApiModelProperty("结束时间")
    private Date endTime;
    @ApiModelProperty("vip图片")
    private String vipImage;
    public String getImage() {
        return ImageUtil.getImage(image);
    }

}
