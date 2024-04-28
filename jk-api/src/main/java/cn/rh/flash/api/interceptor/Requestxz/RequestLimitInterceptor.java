package cn.rh.flash.api.interceptor.Requestxz;

import cn.rh.flash.cache.impl.EhcacheDao;
import cn.rh.flash.utils.HttpUtil;
import cn.rh.flash.utils.JsonUtil;
import cn.rh.flash.utils.StringUtil;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Timer;
import java.util.TimerTask;

@Log4j2
@Aspect
@Component
public class RequestLimitInterceptor {


    @Autowired
    private EhcacheDao ehcacheDao;

    /**
     * 请求校验
     *
     * @param joinPoint
     * @return
     */
    private static HttpServletRequest extracted(JoinPoint joinPoint) throws Exception {
        // joinpoint.getargs():获取带参方法的参数     joinpoint.getTarget():.获取他们的目标对象信息   joinpoint.getSignature() 修饰符   +    包名   +    组件名(类名)     +  方法的名字
        Object[] args = joinPoint.getArgs();
        HttpServletRequest request = HttpUtil.getRequest();
        request.setAttribute("obj", JsonUtil.toJson(args));
        return request;
    }

    @Before("within(@org.springframework.web.bind.annotation.RestController *) && @annotation(limit)")
    public void requestLimit(final JoinPoint joinPoint , RequestLimit limit) throws RequestLimitException {
        try {
            HttpServletRequest request = extracted(joinPoint);

//            String ip = request.getLocalAddr();
            String ip = HttpUtil.getIp();
            String url = request.getRequestURL().toString();
            String key = "req_limit_".concat(url).concat("/"+ip)+":";
            if (request.getHeader("token") == null){
                String s = (String) request.getAttribute("obj");
                if (StringUtil.isNotEmpty(s)){
                    key += s;
                }
            }else {
                key +=request.getHeader("token")+"";
            }

            if (ehcacheDao.get(key) == null || ehcacheDao.get(key).equals("0" )) {
                ehcacheDao.set(key, "1");
            } else {
                ehcacheDao.set(key, ( Integer.valueOf( ehcacheDao.get(key) ) + 1 )+"" );
            }
            int count = Integer.valueOf( ehcacheDao.get(key) );
            if (count > 0) {
                //创建一个定时器
                Timer timer = new Timer();
                String finalKey = key;
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        ehcacheDao.del(finalKey);
                    }
                };
                //这个定时器设定在time规定的时间之后会执行上面的remove方法，也就是说在这个时间后它可以重新访问
                timer.schedule(timerTask, limit.time());
            }
            if (count > limit.count()) {
                log.error("用户IP[" + ip + "]访问地址[" + url + "]超过了限定的次数[" + limit.count() + "]");
                throw new RequestLimitException();
            }
        }catch (RequestLimitException e){
            throw e;
        }catch (Exception e){
            log.error("发生异常",e);
        }
    }
}
