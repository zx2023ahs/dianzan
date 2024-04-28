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
public class RechargeRecordVo {

    @ExcelProperty("编号")
    private String idw;

    @ExcelProperty("顶级账号")
    private String userAccount;

    @ExcelProperty("账号")
    private String account;

    @ExcelProperty("订单编号")
    private String orderNumber;

    @ExcelProperty("前金额")
    private Double previousBalance;

    @ExcelProperty("金额")
    private Double money;

    @ExcelProperty("后余额")
    private Double afterBalance;


    @ExcelProperty("通道名称")
    private String channelName;

    @ExcelProperty("通道类型")
    private String channelType;

    @ExcelProperty("充值地址")
    private String withdrawalAddress;

    @ExcelProperty("充值状态")
    private String rechargeStatusName;

    @ExcelProperty(value = "创建时间",format = "YYYY-MM-dd HH:mm:ss")
    private Date createTime;
}
