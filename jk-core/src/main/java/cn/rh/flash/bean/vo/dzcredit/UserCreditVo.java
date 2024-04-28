package cn.rh.flash.bean.vo.dzcredit;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentFontStyle;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

@Data
@HeadRowHeight(30)  //表头行高
@ContentRowHeight(15)  //内容行高
@ColumnWidth(18)
@ContentFontStyle(fontHeightInPoints = (short) 12) //字体大小
public class UserCreditVo {

    @ExcelProperty("来源邀请码")
    private String sourceInvitationCode;

    @ExcelProperty("账号")
    private String account;

    @ExcelProperty("vip类型")
    private String vipTypeName;

    @ExcelProperty("信誉分")
    private Integer credit;

    @ExcelProperty(value = "设备最后一次运营时间",format = "yyyy-MM-dd")
    private String finalDate;

    @ExcelProperty("信誉分状态")
    private String statusName;


}
