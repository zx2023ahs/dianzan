package cn.rh.flash.bean.vo.dz;

import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.config.chinesePattern.ChinesePattern;
import lombok.Data;

import java.util.List;


@Data
public class BatchUserVo {

    private List<UserInfo> userInfos;
    private Integer flg;
    // 拒绝原因
    @ChinesePattern(regexp = "[^\\u4E00-\\u9FA5\\uF900-\\uFA2D]+",message = "禁止输入中文")
    private String value;

}
