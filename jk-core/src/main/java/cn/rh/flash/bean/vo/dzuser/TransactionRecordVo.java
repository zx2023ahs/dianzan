package cn.rh.flash.bean.vo.dzuser;

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
public class TransactionRecordVo {


    @ExcelProperty("唯一值")
    private String idw;
    @ExcelProperty("来源邀请码")
    private String sourceInvitationCode;
    @ExcelProperty("用户id")
    private Long uid;
    @ExcelProperty("账号")
    private String account;
    @ExcelProperty("订单编号")
    private String orderNumber;
    @ExcelProperty("交易编号")
    private String transactionNumber;
    @ExcelProperty("交易类型")
    private String transactionTypeName;
    @ExcelProperty("前余额")
    private Double previousBalance;
    @ExcelProperty("金额")
    private String moneyName;
    @ExcelProperty("后余额")
    private Double afterBalance;
    @ExcelProperty("备注")
    private String remark;
    @ExcelProperty(value = "创建时间",format = "YYYY-MM-dd HH:mm:ss")
    private Date createTime;

}
