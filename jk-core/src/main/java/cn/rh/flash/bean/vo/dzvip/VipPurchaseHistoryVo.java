package cn.rh.flash.bean.vo.dzvip;

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
public class VipPurchaseHistoryVo {


    @ExcelProperty("顶级账号")
    private String UserAccount;

    @ExcelProperty("账号")
    private String account;

    @ExcelProperty("之前VIP类型")
    private String  previousViPTypeName;

    @ExcelProperty("来源邀请码")
    private String sourceInvitationCode;

    @ExcelProperty("之后VIP类型")
    private String  afterViPTypeName;

    @ExcelProperty("支付方式")
    private String  paymentMethodName;

    @ExcelProperty("支付状态")
    private String  whetherToPayName;

    @ExcelProperty("充电宝数量")
    private Integer numberOfTasks;

    @ExcelProperty("每日收入")
    private Double dailyIncome;

    @ExcelProperty(value = "购买时间",format = "yyyy-MM-dd HH:mm:ss")
    private Date modifyTime;

    @ExcelProperty(value = "到期时间",format = "yyyy-MM-dd HH:mm:ss")
    private Date expireDateToDate;



}
