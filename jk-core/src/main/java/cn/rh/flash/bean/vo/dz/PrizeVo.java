package cn.rh.flash.bean.vo.dz;

import cn.rh.flash.utils.ImageUtil;
import lombok.Data;

@Data
public class PrizeVo {

    private String idw; // 唯一值

    private String prizeName; // 奖品名称

    private String url; // 奖品图片



    public String getUrl() {
        return ImageUtil.getImage( url );
    }
}
