/**
 * Copyright (c) 2015-2016, Chill Zhuang 庄骞 (smallchill@163.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.rh.flash.utils;


import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Log4j2
public class DateUtil {


    private static final Object LOCK = new Object();

    private static final Map<String, ThreadLocal<SimpleDateFormat>> POOL = new HashMap<String, ThreadLocal<SimpleDateFormat>>();


    public static LocalDateTime getLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZonedDateTime zonedDateTime = instant.atZone( ZoneId.systemDefault());
        LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();
        return localDateTime;
    }

    public static Date localDateTimeToDate(LocalDateTime localDate) {
        ZonedDateTime zonedDateTime = localDate.atZone(ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }


    /**
     * @Description: 获取某个时区的当前时间
     * @Param:  timeZoneOffset : 时区 8 代表东八区
     * @return:
     * @Author: Skj
     */
    public static String getFormatDateString(float timeZoneOffset,String format){
        if (timeZoneOffset > 13 || timeZoneOffset < -12) {
            timeZoneOffset = 0;
        }

        int newTime=(int)(timeZoneOffset * 60 * 60 * 1000);
        TimeZone timeZone;
        String[] ids = TimeZone.getAvailableIDs(newTime);
        if (ids.length == 0) {
            timeZone = TimeZone.getDefault();
        } else {
            timeZone = new SimpleTimeZone(newTime, ids[0]);
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(timeZone);
        return sdf.format(new Date());
    }


    /**
     * 时区转化
     */
    public static String getTimeByZone2(String parseTime) {

        // cli_zone
        String cliZone = HttpUtil.getCliZone();
        if (StringUtil.isEmpty(cliZone)) {
            log.warn("getCliZone 获取失败");
            cliZone = TimeZone.getDefault().getID();
        }
        SimpleDateFormat userTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        userTime.setTimeZone(TimeZone.getTimeZone(cliZone));  // 用户所在时区
        String format = userTime.format(parseTime(parseTime));

        SimpleDateFormat sysTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  // 服务器数据库的 时区
        sysTime.setTimeZone(TimeZone.getDefault());  // 系统时区
        //sysTime.setTimeZone( TimeZone.getTimeZone("GMT+11") );  // 模拟系统时区
        return sysTime.format(parseTime(format));
    }

    /**
     * 获取YYYY格式
     *
     * @return
     */
    public static String getYear() {
        LocalDateTime now = LocalDateTime.now( ZoneId.systemDefault());
        return formatDate( now, "yyyy");
    }

    /**
     * 获取YYYY格式
     *
     * @return
     */
    public static String getYear(Date date) {
        LocalDateTime localDateTime = getLocalDateTime( date );
        //LocalDateTime now = LocalDateTime.now( ZoneId.systemDefault());
        return formatDate( localDateTime, "yyyy");
    }

    /**
     * 获取YYYY-MM-DD格式
     *
     * @return
     */
    public static String getDay() {
        LocalDateTime now = LocalDateTime.now( ZoneId.systemDefault());
        return formatDate(now, "yyyy-MM-dd");
    }

    /**
     * 获取YYYY-MM-DD格式
     *
     * @return
     */
    public static String getDay(Date date) {

        // 转换为LocalDateTime
        Instant instant = date.toInstant();
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        LocalDateTime localDateTime = zonedDateTime.toLocalDateTime();

        // System.out.println("LocalDateTime: " + localDateTime);
        return formatDate( localDateTime, "yyyy-MM-dd");
    }

    /**
     * 获取YYYYMMDD格式
     *
     * @return
     */
    public static String getDays() {
        LocalDateTime now = LocalDateTime.now( ZoneId.systemDefault());
        return formatDate(now, "yyyyMMdd");
    }

    /**
     * 获取YYYYMMDD格式
     * LocalDateTime now = LocalDateTime.now( ZoneId.systemDefault());
     * @return
     */
    public static String getDays( LocalDateTime date) {
        return formatDate(date, "yyyyMMdd");
    }

    /**
     * 获取YYYY-MM-DD HH:mm:ss格式
     *
     * @return
     */
    public static String getTime() {

        LocalDateTime now = LocalDateTime.now( ZoneId.systemDefault());
        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
       // String formattedTime = now.format(formatter);
        //System.out.println("Server time is: " + formattedTime);

        return formatDate(now, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 获取YYYY-MM-DD HH:mm:ss.SSS格式
     *
     * @return
     */
    public static String getMsTime() {
        LocalDateTime now = LocalDateTime.now( ZoneId.systemDefault());
        return formatDate( now, "yyyy-MM-dd HH:mm:ss.SSS");
    }

    /**
     * 获取YYYYMMDDHHmmss格式
     *
     * @return
     */
    public static String getAllTime() {
        LocalDateTime now = LocalDateTime.now( ZoneId.systemDefault());
        return formatDate(now, "yyyyMMddHHmmss");
    }

    /**
     * 获取YYYY-MM-DD HH:mm:ss格式
     *
     * @return
     */
    public static String getTime(LocalDateTime date) {
        return formatDate(date, "yyyy-MM-dd HH:mm:ss");

    }


    public static String formatDate(LocalDateTime now, String pattern) {
        String formatDate = null;
        if (StringUtil.isNotEmpty(pattern)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            formatDate = now.format(formatter);
            //formatDate = DateFormatUtils.format(date, pattern,TimeZone.getDefault());
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            formatDate = now.format(formatter);
            //formatDate = DateFormatUtils.format(date, "",TimeZone.getDefault());
        }
        return formatDate;
    }

    /**
     * @param s
     * @param e
     * @return boolean
     * @throws
     * @Title: compareDate
     * @Description:(日期比较，如果s>=e 返回true 否则返回false)
     * @author luguosui
     */
    public static boolean compareDate(String s, String e) {
        if (parseDate(s) == null || parseDate(e) == null) {
            return false;
        }
        return parseDate(s).getTime() >= parseDate(e).getTime();
    }

    /**
     * 格式化日期
     *
     * @return
     */
    public static Date parseDate(String date) {
        return parse(date, "yyyy-MM-dd");
    }

    /**
     * 格式化日期
     *
     * @return
     */
    public static Date parseTime(String date) {
        return parse(date, "yyyy-MM-dd HH:mm:ss");
    }


    /**
     * 格式化日期
     *
     * @return
     */
    public static Date parse(String date, String pattern) {
        if (date != null) {
            if (pattern == null || "".equals(pattern)) {
                return null;
            }
            DateFormat format = getDFormat(pattern);
            try {
                format.setTimeZone(TimeZone.getDefault());
                return format.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static SimpleDateFormat getDFormat(String pattern) {
        ThreadLocal<SimpleDateFormat> tl = POOL.get(pattern);
        if (tl == null) {
            synchronized (LOCK) {
                tl = POOL.get(pattern);
                if (tl == null) {
                    final String p = pattern;
                    tl = new ThreadLocal<SimpleDateFormat>() {
                        @Override
                        protected synchronized SimpleDateFormat initialValue() {
                            return new SimpleDateFormat(p);
                        }
                    };
                    POOL.put(p, tl);
                }
            }
        }
        return tl.get();
    }

    /**
     * 格式化日期
     *
     * @return
     */
    public static String format(Date date, String pattern) {
        return DateFormatUtils.format(date, pattern, TimeZone.getDefault());
    }

    /**
     * 把日期转换为Timestamp
     *
     * @param date
     * @return
     */
    public static Timestamp format(Date date) {
        return new Timestamp(date.getTime());
    }

    /**
     * 校验日期是否合法
     *
     * @return
     */
    public static boolean isValidDate(String s) {
        return parse(s, "yyyy-MM-dd HH:mm:ss") != null;
    }

    /**
     * 校验日期是否合法
     *
     * @return
     */
    public static boolean isValidDate(String s, String pattern) {
        return parse(s, pattern) != null;
    }

    public static int getDiffYear(String startTime, String endTime) {
        DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        try {
            int years = (int) (((fmt.parse(endTime).getTime() - fmt.parse(
                    startTime).getTime()) / (1000 * 60 * 60 * 24)) / 365);
            return years;
        } catch (Exception e) {
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            return 0;
        }
    }

    /**
     * <li>功能描述：时间相减得到天数
     *
     * @param beginDateStr
     * @param endDateStr
     * @return long
     * @author Administrator
     */
    public static long getDaySub(String beginDateStr, String endDateStr) {
        long day = 0;
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd");
        Date beginDate = null;
        Date endDate = null;

        try {
            beginDate = format.parse(beginDateStr);
            endDate = format.parse(endDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        day = (endDate.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000);
        // System.out.println("相隔的天数="+day);
        return day;
    }

    /**
     * <li>功能描述：时间相减得到天数
     *
     * @param beginDateStr
     * @param endDateStr
     * @return long
     * @author Administrator
     */
    public static long getDaySub(Date beginDateStr, Date endDateStr) {
        long day = 0;
        day = (endDateStr.getTime() - beginDateStr.getTime()) / (24 * 60 * 60 * 1000);
        return day;
    }





    /**
     * 获取时间段内天数---去除星期内指定天数
     * @param begin
     * @param end
     * @param exclude  去除的星期几
     * @return
     */
    public static long getDaySubEx(String begin,String end,String exclude){
        List<String> list = DateUtil.getBetWeenDate(begin,end);
        long count = list.stream().filter(i->!exclude.contains(DateUtil.getWeek(DateUtil.parseDate(i)))).count();
        return count;
    }

    /**
     * 得到毫秒差
     *
     * @param beginDateStr
     * @param endDateStr
     * @return
     */
    public static long getDaySubLong(String beginDateStr, String endDateStr) {
        long day = 0;
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        Date beginDate = null;
        Date endDate = null;

        try {
            beginDate = format.parse(beginDateStr);
            endDate = format.parse(endDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        day = endDate.getTime() - beginDate.getTime();
        // System.out.println("相隔的天数="+day);
        return day;
    }


    /**
     * 得到n天之后的日期
     *
     * @param days
     * @return
     */
    public static String getAfterDayDateString(String days) {
        return getAfterDayDateString(days, "yyyy-MM-dd HH:mm:ss");
    }

    public static String getAfterDayDateString(String days, String frmt) {
        int daysInt = Integer.parseInt(days);

        Calendar canlendar = Calendar.getInstance(TimeZone.getDefault()); // java.util包
        canlendar.add(Calendar.DATE, daysInt); // 日期减 如果不够减会将月变动
        Date date = canlendar.getTime();

        SimpleDateFormat sdfd = new SimpleDateFormat(frmt);
        String dateStr = sdfd.format(date);

        return dateStr;
    }

    public static Date getAfterDayDate(String days) {

        return parseTime(getAfterDayDateString(days));
    }

    public static String getAfterDayDate(Date beginDate, String days) {
        int daysInt = Integer.parseInt(days);

        Calendar canlendar = Calendar.getInstance(TimeZone.getDefault()); // java.util包
        canlendar.setTime(beginDate);
        canlendar.add(Calendar.DATE, daysInt); // 日期减 如果不够减会将月变动
        Date date = canlendar.getTime();

        SimpleDateFormat sdfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sdfd.format(date);

        return dateStr;
    }

    /**
     *  获取两个日期之间的所有日期 (年月日)
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static List<String> getBetWeenDate(String startTime, String endTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 声明保存日期集合
        List<String> list = new ArrayList<String>();
        try {
            // 转化成日期类型
            Date startDate = sdf.parse(startTime);
            Date endDate = sdf.parse(endTime);

            //用Calendar 进行日期比较判断
            Calendar calendar = Calendar.getInstance();
            while (startDate.getTime()<=endDate.getTime()){
                // 把日期添加到集合
                list.add(sdf.format(startDate));
                // 设置日期
                calendar.setTime(startDate);
                //把日期增加一天
                calendar.add(Calendar.DATE, 1);
                // 获取增加后的日期
                startDate=calendar.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }

    //根据日期取得星期几
    public static String getWeek(Date date){
        String[] weeks = {"星期日","星期一","星期二","星期三","星期四","星期五","星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if(week_index<0){
            week_index = 0;
        }
        return weeks[week_index];
    }

    /**
     * 得到n天之后是周几
     *
     * @param days
     * @return
     */
    public static String getAfterDayWeek(String days) {
        int daysInt = Integer.parseInt(days);

        Calendar canlendar = Calendar.getInstance(TimeZone.getDefault()); // java.util包
        canlendar.add(Calendar.DATE, daysInt); // 日期减 如果不够减会将月变动
        Date date = canlendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("E");
        String dateStr = sdf.format(date);

        return dateStr;
    }

    //  方法不用了
    public static String getTimeByZone(String expireTimes) {
        return expireTimes;
    }


    /**
     * 判断当前时间是否在[startTime, endTime]区间，注意时间格式要一致
     * @param nowTime 当前时间
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     * @Author: Skj
     */
    public static boolean betWeen(Date nowTime, Date startTime, Date endTime) {

        if (startTime == null || endTime == null){
            return false;
        }

        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 判断时间是否过期
     * @param targetTime
     * @return
     */
    public static boolean isExpired(Date targetTime) {
        // 获取当前时间
        Calendar currentTime = Calendar.getInstance();
        // 获取目标时间
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(targetTime);

        // 比较当前时间和目标时间
        if (currentTime.after(targetCalendar)) {
            // 当前时间晚于目标时间，即时间已过期
            return true;
        }

        // 时间未过期
        return false;
    }

    //得到n小时之后的时间
//    public static String getAfterHourString(String time) {
//        return getAfterHourString(time, "yyyy-MM-dd HH:mm:ss");
//    }
    public static Date getAfterHourString(String time) {
        int timeInt = Integer.parseInt(time);
        Calendar canlendar = Calendar.getInstance(TimeZone.getDefault()); // java.util包
        canlendar.add(Calendar.HOUR, timeInt); // 日期减 如果不够减会将月变动
        Date date = canlendar.getTime();
//        SimpleDateFormat sdfd = new SimpleDateFormat(frmt);
//        String dateStr = sdfd.format(date);
        return date;
    }

}
