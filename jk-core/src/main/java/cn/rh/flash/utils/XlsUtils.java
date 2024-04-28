package cn.rh.flash.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class XlsUtils {
    public String dateFmt(Date date, String fmt) {
        if (date == null) {
            return "";
        }
        // 转换为LocalDateTime
        Instant instant = date.toInstant();
        ZonedDateTime zonedDateTime = instant.atZone( ZoneId.systemDefault());
        LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();
        if(StringUtil.isEmpty(fmt)){

            return DateUtil.getTime(localDateTime);
        }else{
            return DateUtil.formatDate(localDateTime, fmt);
        }
    }
}
