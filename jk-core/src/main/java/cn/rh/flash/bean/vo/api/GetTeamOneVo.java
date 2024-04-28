package cn.rh.flash.bean.vo.api;

import cn.rh.flash.utils.ImageUtil;
import cn.rh.flash.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetTeamOneVo {

    @ApiModelProperty("账号")
    private String account;
    @ApiModelProperty("头像")
    private String headPortraitKey;
    @ApiModelProperty("注册时间")
    private Date registrationTime;
    @ApiModelProperty("今日新增人数")
    private BigInteger dayPeopleNumber;
    @ApiModelProperty("团队人数")
    private BigInteger teamaSize;
    @ApiModelProperty("VIP类型")
    private String vipType;

    @ApiModelProperty("邀请码")
    private String invitationCode;

    @ApiModelProperty("用户层级")
    private String levels;

    @ApiModelProperty("vipLog")
    private String vipImg;


    public String getVipImg() {
        return ImageUtil.getImage(vipImg);
    }


    public String getHeadPortraitKey() {
        return ImageUtil.getImage(headPortraitKey);
    }
    public String getAccount() {
        return StringUtil.addXing( account );
    }

}
