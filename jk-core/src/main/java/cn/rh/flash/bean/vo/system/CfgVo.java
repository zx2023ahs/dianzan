package cn.rh.flash.bean.vo.system;

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
 public class CfgVo {

    @ExcelProperty("参数名")
    private String cfgName;

    @ExcelProperty("参数值")
    private String cfgValue;

    @ExcelProperty("备注")
    private String cfgDesc;

    @ExcelProperty(value = "创建时间/注册时间",format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
