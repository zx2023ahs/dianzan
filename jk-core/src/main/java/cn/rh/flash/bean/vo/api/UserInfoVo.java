package cn.rh.flash.bean.vo.api;

import cn.rh.flash.config.chinesePattern.ChinesePattern;
import cn.rh.flash.utils.ImageUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@ApiModel("用户个人信息")
public class UserInfoVo {

    @ApiModelProperty("用户ID")
    private BigInteger id;

    @ApiModelProperty("用户邀请码")
    private String invitationCode;

    @ApiModelProperty("用户名")
    private String name;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("账号")
    private String account;

    @ApiModelProperty("头像")
    private String headPortraitKey;


    @ApiModelProperty("当日利润")
    private BigDecimal profitOfTheDay;


    @ApiModelProperty("总收益")
    private BigDecimal totalRevenue;

    @ApiModelProperty("可提现总金额")
    private BigDecimal totalWithdrawalAmount;

    @ApiModelProperty("中奖金额")
    private BigDecimal toWinningAmount;

    @ApiModelProperty("团队规模")
    private BigInteger teamSize;

    @ApiModelProperty("团队报告")
    private BigDecimal teamReport;

    @ApiModelProperty("收入明细")
    private BigDecimal balance;

    @ApiModelProperty("会员到期时间")
    private BigInteger dueDay = BigInteger.ZERO;

    @ApiModelProperty("会员类型")
    private String vipType;

    @ApiModelProperty("会员LOG")
    private String vipImg;

    public String getVipImg() {
        return ImageUtil.getImage(vipImg);
    }

    public String getHeadPortraitKey() {
        return ImageUtil.getImage(headPortraitKey);
    }

    @Data
    public static class StraightBuckleVo {
        @NotNull( message = "请输入金额")
        private Double money;  //金额
        @NotNull( message = "类型不能为空")
        private Integer type;  //类型

        @NotNull( message = "用户参数异常")
        private Long uid;

        @ChinesePattern(regexp = "[^\\u4E00-\\u9FA5\\uF900-\\uFA2D]+",message = "禁止输入中文")
        private String remark;
    }
}
