package cn.rh.flash.bean.vo.dzser;

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
public class CompensationRecordVo{


    @ExcelProperty("账号")
    private String account;

    @ExcelProperty("顶级账号")
    private String userAccount;

    @ExcelProperty("前金额")
    private Double formerCreditScore;

    @ExcelProperty("金额")
    private String moneyAndAdditionAndSubtraction;

    @ExcelProperty("后金额")
    private Double postCreditScore;

    @ExcelProperty("操作员")
    private String operator;

    @ExcelProperty(value = "创建时间/注册时间",format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
