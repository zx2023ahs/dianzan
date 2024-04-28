package cn.rh.flash.bean.vo.dzvip;


import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentFontStyle;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

import java.util.Date;

@Data
@HeadRowHeight(30)  //表头行高
@ContentRowHeight(15)  //内容行高
@ColumnWidth(18)
@ContentFontStyle(fontHeightInPoints = (short) 12) //字体大小
public class VipRebateRecordVo {

    @ExcelProperty("唯一值")
    private String idw;

    @ExcelProperty("账号")
    private String account;

    @ExcelProperty("来源邀请码")
    private String sourceInvitationCode;

    @ExcelProperty("前金额")
    private Double previousAmount;

    @ExcelProperty("金额")
    private Double money;

    @ExcelProperty("后金额")
    private Double amountAfter;

    @ExcelProperty("来源下级等级")
    private String relevelsName;

    @ExcelProperty("来源用户账号vip升级")
    private String VipTypeSourlyName;

    @ExcelProperty(value = "创建时间",format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    // 字段拼接
    @ExcelIgnore
    private String sourceUserAccount;
    @ExcelIgnore
    private String oldVipType_str;
    @ExcelIgnore
    private String newVipType_str;
}
