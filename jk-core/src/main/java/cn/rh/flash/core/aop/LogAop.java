package cn.rh.flash.core.aop;

import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.core.log.LogManager;
import cn.rh.flash.core.log.LogTaskFactory;
import cn.rh.flash.security.JwtUtil;
import cn.rh.flash.utils.HttpUtil;
import cn.rh.flash.utils.StringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 日志记录
 */
@Aspect
@Component
public class LogAop {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut(value = "@annotation(cn.rh.flash.bean.core.BussinessLog)")
    public void cutService() {
    }

    @Around("cutService()")
    public Object recordSysLog(ProceedingJoinPoint point) throws Throwable {

        //先执行业务
        Object result = point.proceed();

        try {
            handle(point);
        } catch (Exception e) {
            log.error("日志记录出错! {}", e);
        }

        return result;
    }

    private void handle(ProceedingJoinPoint point) throws Exception {

        //获取拦截的方法名
        Signature sig = point.getSignature();
        MethodSignature msig = null;
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        msig = (MethodSignature) sig;
        Object target = point.getTarget();
        Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
        String methodName = currentMethod.getName();

        //获取用户id，admin和api模块获取idUser方式不同
        Long idUser = null;
        HttpServletRequest request = HttpUtil.getRequest();
        String token = request.getHeader("Authorization");
        if (StringUtil.isNotEmpty(token)) {
            idUser = JwtUtil.getUserId(token);
        }
        if (idUser == null) {
            return;
        }

        //获取拦截方法的参数
        String className = point.getTarget().getClass().getName();
        Object[] params = point.getArgs();

        //获取操作名称
        BussinessLog annotation = currentMethod.getAnnotation(BussinessLog.class);
        String bussinessName = annotation.value();
        String key = annotation.key();


        StringBuilder sb = new StringBuilder();
        for (Object param : params) {
            sb.append(param);
            sb.append(" & ");
        }

//        String msg = "";
//        if (bussinessName.indexOf("修改") != -1 || bussinessName.indexOf("编辑") != -1 || bussinessName.indexOf("更新") !=-1 ) {
//            Object obj1 = LogObjectHolder.me().get();
//            Map<String, String> obj2 = HttpUtil.getRequestParameters();
//            try {
//                msg = BeanUtil.contrastObj(key, obj1, obj2);
//            } catch (Exception e) {
//
//            }
//        } else {
//            Map<String, String> parameters = HttpUtil.getRequestParameters();
//            msg = BeanUtil.parseMutiKey(parameters);
//        }

        LogManager.me().executeLog(LogTaskFactory.bussinessLog(idUser, bussinessName, className, methodName, sb.toString()));
    }
}