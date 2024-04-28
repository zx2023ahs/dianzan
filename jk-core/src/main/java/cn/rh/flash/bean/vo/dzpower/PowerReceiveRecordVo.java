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
public class PowerReceiveRecordVo {



    @ExcelProperty("顶级账号")
    private String userAccount;

    @ExcelProperty("账号")
    private String account;

    @ExcelProperty("充电宝任务编号")
    private String taskidw;

    @ExcelProperty("充电宝编号")
    private String pbidw;

    @ExcelProperty("名称")
    private String name;

    @ExcelProperty("小时价")
    private Double payPrice;

    @ExcelProperty("购买数量")
    private Integer totalQuantity;

    @ExcelProperty("领取状态")
    private String statusName;

    @ExcelProperty("当前信誉分")
    private Integer credit;

    @ExcelProperty("收益率")
    private Double yield;

    @ExcelProperty("预计收益")
    private Double money;

    @ExcelProperty(value = "开始时间",format = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @ExcelProperty(value = "结束时间",format = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @ExcelProperty("运营状态")
    private String flgName;

    @ExcelProperty("vip类型")
    private String vipTypeName;
}
