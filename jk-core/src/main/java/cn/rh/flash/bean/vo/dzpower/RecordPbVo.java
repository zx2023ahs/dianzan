package cn.rh.flash.bean.vo.dzpower;

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
public class RecordPbVo {


    @ExcelProperty("编号")
    private String idw;

    @ExcelProperty("来源邀请码")
    private String sourceInvitationCode;

    @ExcelProperty("账号")
    private String account;

    @ExcelProperty("前余额")
    private Double formerCreditScore;

    @ExcelProperty("金额")
    private Double money;

    @ExcelProperty("后余额")
    private Double postCreditScore;

    @ExcelProperty("相对层级")
    private String relevelsName;

    @ExcelProperty("来源账号")
    private String sourceUserAccount;

    @ExcelProperty(value = "返佣时间",format = "YYYY-MM-dd HH:mm:ss")
    private Date rebateTime;

    @ExcelProperty(value = "创建时间",format = "YYYY-MM-dd HH:mm:ss")
    private Date createTime;

}
