package cn.rh.flash.core.log;

import cn.rh.flash.bean.constant.state.LogSucceed;
import cn.rh.flash.bean.constant.state.LogType;
import cn.rh.flash.bean.entity.system.LoginLog;
import cn.rh.flash.bean.entity.system.OperationLog;
import cn.rh.flash.utils.DateUtil;

/**
 * 日志对象创建工厂
 */
public class LogFactory {

    /**
     * 创建操作日志
     */
    public static OperationLog createOperationLog(LogType logType, Long userId, String bussinessName, String clazzName, String methodName, String msg, LogSucceed succeed) {
        OperationLog operationLog = new OperationLog();
        operationLog.setLogtype(logType.getMessage());
        operationLog.setLogname(bussinessName);
        operationLog.setUserid(userId.intValue());
        operationLog.setClassname(clazzName);
        operationLog.setMethod(methodName);
        operationLog.setCreateTime( DateUtil.parseTime( DateUtil.getTime() ) );
        operationLog.setSucceed(succeed.getMessage());
        operationLog.setMessage(msg);
        return operationLog;
    }

    /**
     * 创建登录日志
     */
    public static LoginLog createLoginLog(LogType logType,String userName, Long userId, String msg, String ip) {
        LoginLog loginLog = new LoginLog();
        loginLog.setLogname(logType.getMessage());
        loginLog.setUserid(userId.intValue());
        loginLog.setCreateTime( DateUtil.parseTime( DateUtil.getTime() ) );
        loginLog.setSucceed(LogSucceed.SUCCESS.getMessage());
        loginLog.setIp(ip);
        loginLog.setMessage(msg);
        loginLog.setUsername( userName );
        return loginLog;
    }

    /**
     * API 日志
     * @param userId
     * @param msg
     * @param ip
     * @return
     */
    public static LoginLog apiCreateLoginLog( Long userId,String userName, String msg, String ip,LogType logType) {
        LoginLog loginLog = new LoginLog();
        loginLog.setLogname( logType.getMessage() );
        loginLog.setUserid( userId.intValue() );
        loginLog.setCreateTime( DateUtil.parseTime( DateUtil.getTime() ) );
        loginLog.setSucceed(LogSucceed.SUCCESS.getMessage());
        loginLog.setIp( ip );
        loginLog.setMessage( msg );
        loginLog.setUsername( userName );
        return loginLog;
    }

}
