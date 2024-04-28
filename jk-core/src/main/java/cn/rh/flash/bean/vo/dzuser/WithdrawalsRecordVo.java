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
public class WithdrawalsRecordVo {

    @ExcelProperty("顶级账号")
    private String userAccount;
    @ExcelProperty("账号")
    private String account;
    @ExcelProperty("提现地址")
    private String withdrawalAddress;
    @ExcelProperty("订单编号")
    private String orderNumber;
    @ExcelProperty("vip等级")
    private String vipTypeName;
    @ExcelProperty("通道名称")
    private String channelName;
    @ExcelProperty("通道类型")
    private String channelType;
    @ExcelProperty("前余额")
    private Double previousBalance;
    @ExcelProperty("金额")
    private Double money;
    @ExcelProperty("后余额")
    private Double afterBalance;
    @ExcelProperty("提现手续费")
    private Double handlingFee;
    @ExcelProperty("到账金额")
    private Double amountReceived;
    @ExcelProperty("状态")
    private String rechargeStatusName;
    @ExcelProperty(value = "创建时间", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @ExcelProperty(value = "最后更新时间", format = "yyyy-MM-dd HH:mm:ss")
    private Date modifyTime;
    @ExcelProperty("备注")
    private String remark;
    @ExcelProperty("提现单号、姓名")
    private String transactionNumber;
}
