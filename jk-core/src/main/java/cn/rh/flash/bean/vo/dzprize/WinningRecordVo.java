package cn.rh.flash.bean.vo.dzprize;


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
public class WinningRecordVo{

    @ExcelProperty("唯一值")
    private String idw;

    @ExcelProperty("顶级账号")
    private String userAccount;

    @ExcelProperty("来源邀请码")
    private String sourceInvitationCode;

    @ExcelProperty("用户账号")
    private String account;

    @ExcelProperty("活动类型")
    private String prizeType;

    @ExcelProperty("奖品名称")
    private String prizeName;


    @ExcelProperty("剩余抽奖次数")
    private Integer surplusNumber;

    @ExcelProperty(value = "中奖时间",format = "YYYY-MM-dd HH:mm:ss")
    private Date createTime;







}
