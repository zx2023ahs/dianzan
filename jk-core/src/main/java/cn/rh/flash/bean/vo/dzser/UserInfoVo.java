package cn.rh.flash.bean.vo.dzser;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentFontStyle;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

@Data
@HeadRowHeight(30)  //表头行高
@ContentRowHeight(15)  //内容行高
@ColumnWidth(18)
@ContentFontStyle(fontHeightInPoints = (short) 12) //字体大小
public class UserInfoVo{

    @ExcelProperty("唯一值")
    private String idw;

    @ExcelProperty("顶级账号")
    private String superAccount;

    @ExcelProperty("国家代号")
    private String countryCodeNumber;

    @ExcelProperty("账号")
    private String account;

    @ExcelProperty("真实姓名")
    private String realName;

    @ExcelProperty("已成功提现的金额")
    private Double moneyOK;

    @ExcelProperty("正在审核中的金额")
    private Double moneyNO;

    @ExcelProperty("赠送彩金金额")
    private Double totalBonusIncomeLeft;

    @ExcelProperty("充值总金额")
    private Double totalRechargeAmountLeft;

    @ExcelProperty("开通VIP金额")
    private Double teamVIPOpeningTotalRebate;

    @ExcelProperty("充电宝返佣总金额")
    private Double sourceUserAccount;

    @ExcelProperty("用户余额")
    private Double userBalanceLeft;

    @ExcelProperty("上级邀请码")
    private String superiorInvitationCode;

    @ExcelProperty("邀请码")
    private String invitationCode;

    @ExcelProperty("用户类型")
    private String userTypeName;

    @ExcelProperty("ViP类型")
    private String vipTypeName;

    @ExcelProperty("用户状态")
    private String dzstatusName;

    @ExcelProperty("限制购买")
    private String limitBuyCdbName;

    @ExcelProperty("限制收益")
    private String limitProfitName;

    @ExcelProperty("限制提款")
    private String limitDrawingName;

    @ExcelProperty(value = "ViP到期时间",format = "yyyy-MM-dd HH:mm:ss")
    private Date vipExpireDate;

    @ExcelProperty("注册ip")
    private String registerIp;

    @ExcelProperty(value = "注册时间",format = "yyyy-MM-dd HH:mm:ss")
    private Date registrationTime;

    @ExcelProperty("最后一次登录ip")
    private String lastIp;

    @ExcelProperty(value = "最后一次登录时间", format = "yyyy-MM-dd HH:mm:ss")
    private Date lastTime;

    @ExcelProperty("层级")
    private String levels;

}

