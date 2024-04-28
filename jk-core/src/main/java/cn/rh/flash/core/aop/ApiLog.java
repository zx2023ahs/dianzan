package cn.rh.flash.core.aop;

import cn.rh.flash.bean.constant.cache.CacheApiKey;
import cn.rh.flash.bean.constant.state.LogType;
import cn.rh.flash.bean.dto.api.LoginV1Dto;
import cn.rh.flash.bean.dto.api.RegV1Dto;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.exception.ApiException;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.impl.EhcacheDao;
import cn.rh.flash.core.log.LogManager;
import cn.rh.flash.core.log.LogTaskFactory;
import cn.rh.flash.security.apitoken.ApiLoginObject;
import cn.rh.flash.security.apitoken.ApiToken;
import cn.rh.flash.service.dzuser.UserInfoService;
import cn.rh.flash.utils.Constants;
import cn.rh.flash.utils.HttpUtil;
import cn.rh.flash.utils.MD5;
import cn.rh.flash.utils.StringUtil;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 登录日志
 */
@Aspect
@Component
@Log4j2
public class ApiLog {

    @Autowired
    private  ApiToken apiToken;

    @Autowired
    private EhcacheDao ehcacheDao;

    @Autowired
    private UserInfoService userInfoService;


    @Pointcut(value = "@annotation(cn.rh.flash.bean.core.ApiLog)")
    public void cutService() {
    }

    @Around("cutService()")
    public Object recordSysLog(ProceedingJoinPoint point) throws Throwable {
        //先执行业务
        Object result = point.proceed();

        try {
            handle(point);
        } catch (Exception e) {
            log.error("日志记录出错!",e instanceof ApiException ? (ApiException)e : e.getMessage() );
        }

        return result;
    }

    // 登录业务
    private void handle(ProceedingJoinPoint point) throws Exception {

        //获取拦截的方法名
        Signature sig = point.getSignature();
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }

        //
        MethodSignature msig = (MethodSignature) sig;
        Object target = point.getTarget();
        Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
        String methodName = currentMethod.getName();  // 方法名称

        //获取名称
        cn.rh.flash.bean.core.ApiLog annotation = currentMethod.getAnnotation(cn.rh.flash.bean.core.ApiLog.class);
        String version = annotation.version();  //登录接口的版本
        String logoin = annotation.logoin();  // login 登录  exit 退出登录  reg 注册

        String apiTokkenStr = HttpUtil.getApiToken();

        String msg = null;
        switch (logoin){
            case "login":
                //获取拦截方法的参数
                String className = point.getTarget().getClass().getName();
                Object[] params = point.getArgs();
                LoginV1Dto regV1Dto = (LoginV1Dto) params[0];
                List<SearchFilter> liust = new ArrayList<>();
                liust.add( SearchFilter.build("account", regV1Dto.getAccount()) );
                liust.add( SearchFilter.build("password", MD5.md5(regV1Dto.getPwd(), "")  ) );
                UserInfo userInfo = userInfoService.get( liust );
                if (userInfo != null) {
                    msg = String.format("登录（登录版本【%s】%s方法名【%s】）", version,Constants.SEPARATOR, methodName);
                    LogManager.me().executeLog(LogTaskFactory.apiCreateLoginLog( userInfo.getId() , userInfo.getAccount(), msg,HttpUtil.getIp(), LogType.API_LOGIN ) );
                }
                break;
            case "exit":
                ApiLoginObject apiLoginObject = null;
                if (StringUtil.isNotEmpty(apiTokkenStr)) {
                    apiLoginObject = apiToken.parseToken(apiTokkenStr);
                }
                ehcacheDao.hdel( CacheApiKey.LOGIN_CONSTANT, apiLoginObject.getUserInfo().getCountryCodeNumber() +"_"+ apiLoginObject.getUserInfo().getAccount() );
                msg = String.format("退出（退出版本【%s】%s方法名【%s】）", version,Constants.SEPARATOR, methodName);
                LogManager.me().executeLog(LogTaskFactory.apiCreateLoginLog( apiLoginObject.getUserInfo().getId() , apiLoginObject.getUserInfo().getAccount(), msg,HttpUtil.getIp(), LogType.API_EXIT ) );
                break;
            case "reg":

                //获取拦截方法的参数
                String className2 = point.getTarget().getClass().getName();
                Object[] params2 = point.getArgs();
                RegV1Dto regV1Dto2 = (RegV1Dto) params2[0];
                UserInfo userInfo2 = userInfoService.get(SearchFilter.build("account", regV1Dto2.getAccount()));
                if (userInfo2 != null) {
                    msg = String.format("注册（注册版本【%s】%s方法名【%s】）", version,Constants.SEPARATOR, methodName);
                    LogManager.me().executeLog(LogTaskFactory.apiCreateLoginLog( userInfo2.getId() , userInfo2.getAccount(), msg,HttpUtil.getIp(), LogType.API_EXIT ) );
                }
                break;

        }
        // end
    }
}
