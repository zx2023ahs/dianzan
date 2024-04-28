package cn.rh.flash.warpper;

import cn.rh.flash.utils.Constants;
import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.StringUtil;

import java.util.Date;
import java.util.Map;

/**
 * 日志列表的包装类
 */
public class LogWrapper extends BaseControllerWrapper {

    public LogWrapper(Object list) {
        super(list);
    }

    @Override
    public void warpTheMap(Map<String, Object> map) {
        String message = (String) map.get("message");

        // 登录日志
        map.put("userName",  map.get("username") );

        //如果信息过长,则只截取前100位字符串
        if (StringUtil.isNotEmpty(message) && message.length() >= 100) {
            String subMessage = message.substring(0, 100) + "...";
            map.put("message", subMessage);
        }
        map.put("createtime", DateUtil.format((Date) map.get("createTime"), "yyyy-MM-dd hh:MM:ss"));
        //如果信息中包含分割符号;;;   则分割字符串返给前台
        if (StringUtil.isNotEmpty(message) && message.indexOf(Constants.SEPARATOR) != -1) {
            String[] msgs = message.split(Constants.SEPARATOR);
            map.put("regularMessage", msgs);
        } else {
            map.put("regularMessage", message);
        }
    }

}
