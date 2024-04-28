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
public class PowerBankTaskVo {

    @ApiModelProperty("创建时间") // 购买充电宝时间
    private Date createTime;

    @ApiModelProperty("主键")
    private String idw;

    @ApiModelProperty("图片")
    private String image;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("vip类型")
    private String vipType;

    @ApiModelProperty("vip图片")
    private String vipImage;

    @ApiModelProperty("出租单价")
    private Double payPrice;

    @ApiModelProperty("购买数量")
    private Integer totalQuantity;

    @ApiModelProperty("开始时间")
    private Date startTime;

    @ApiModelProperty("结束时间")
    private Date endTime;

    @ApiModelProperty("预计日收益")// 24小时
    private Double dayIncome;

    @ApiModelProperty("运营状态 1 营业中  2 已结束")
    private Integer status;

    @ApiModelProperty("过期状态 1 进行中  2 已到期")
    private Integer flg;

    @ApiModelProperty("续费状态 1 可续费  2 重新开通")
    private Integer renew;

    @ApiModelProperty("时间戳")
    private long timeStamp;

    @ApiModelProperty("当前总收益")
    private Double totalIncome;


    public String getImage() {
        return ImageUtil.getImage(image);
    }
}
