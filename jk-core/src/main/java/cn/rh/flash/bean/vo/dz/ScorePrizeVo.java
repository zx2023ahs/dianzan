package cn.rh.flash.bean.vo.dz;

import cn.rh.flash.utils.ImageUtil;
import lombok.Data;

@Data
public class ScorePrizeVo {


    private String idw; // 唯一值

    private String prizeName; // 奖品名称

    private String score; // 奖品积分

    private String url; // 奖品图片

    private String vipType; // vip限制

    private String prizeType; // 奖品类型 8 夺宝积分

    private String types;//1余额 2实物



    public String getUrl() {
        return ImageUtil.getImage( url );
    }

}
