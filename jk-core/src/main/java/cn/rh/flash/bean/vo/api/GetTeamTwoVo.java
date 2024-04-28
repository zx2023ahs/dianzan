package cn.rh.flash.bean.vo.api;

import cn.rh.flash.utils.ImageUtil;
import cn.rh.flash.utils.StringUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetTeamTwoVo {

    @ApiModelProperty("账号")
    private String account;
    @ApiModelProperty("头像")
    private String headPortraitKey;
    @ApiModelProperty("注册时间")
    private Date registrationTime;
    @ApiModelProperty("VIP类型")
    private String vipType;

    @ApiModelProperty("vipLog")
    private String vipImg;

    public String getHeadPortraitKey() {
        return ImageUtil.getImage(headPortraitKey);
    }
    public String getVipImg() {
        return ImageUtil.getImage(vipImg);
    }
    public String getAccount() {
        return StringUtil.addXing( account );
    }

}
