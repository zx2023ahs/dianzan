package cn.rh.flash.bean.vo.dzuser;

import cn.rh.flash.config.chinesePattern.ChinesePattern;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class StraightAddVo {

    @NotNull( message = "类型不能为空")
    private Integer type;

    @NotNull( message = "账号金额不能为空")
    private List<StraightListVo> straightListVos;

    @ChinesePattern(regexp = "[^\\u4E00-\\u9FA5\\uF900-\\uFA2D]+",message = "禁止输入中文")
    private String remark;
}
