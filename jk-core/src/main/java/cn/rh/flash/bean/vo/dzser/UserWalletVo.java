package cn.rh.flash.bean.vo.dzser;

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
public class UserWalletVo {
    @ExcelProperty("用户ID")
    private Long uid;

    @ExcelProperty("来源邀请码")
    private String sourceInvitationCode;

    @ExcelProperty("账号")
    private String account;

    @ExcelProperty("姓名")
    private String walletName;

    @ExcelProperty("卡号")
    private String walletAddress;

    @ExcelProperty("平台名称")
    private String platformName;

    @ExcelProperty("类型")
    private String channelType;
}
