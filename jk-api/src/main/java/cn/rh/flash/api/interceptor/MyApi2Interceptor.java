package cn.rh.flash.api.interceptor;

import cn.rh.flash.security.apitoken.ApiLoginObject;
import cn.rh.flash.security.apitoken.ApiToken;
import cn.rh.flash.utils.HttpUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jk
 * @data 2020年9月27日 23点47分
 * <th>拦截app请求进行token认证</th>
 */
@Log4j2
@Component
public class MyApi2Interceptor implements HandlerInterceptor {


    @Autowired
    private  ApiToken apiToken;

    //请求之前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {




        response.setCharacterEncoding("UTF-8");//设置编码格式
        response.setContentType("application/json;charset=UTF-8");

        if (request.getMethod().equals(RequestMethod.OPTIONS.name())) {
            log.info( "跨域了" );
            response.setHeader("Access-Control-Allow-Origin",request.getHeader("Origin"));//支持跨域请求
            response.setHeader("Access-Control-Allow-Methods", "*");
            response.setHeader("Access-Control-Allow-Credentials", "true");//是否支持cookie跨域
            response.addHeader("Access-Control-Allow-Headers","Authorization,Origin, X-Requested-With, Content-Type, Accept,Access-Token,Content-Type,X-Requested-With,accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers,token,tk");
            return  true;
        }


        // 解析token
        ApiLoginObject apiLoginObject = apiToken.parseToken( HttpUtil.getApiToken() );
        // 判断 token 是否过期
        apiToken.whetherTheTokenHasExpired(apiLoginObject);

        return  true;
    }





    //请求时
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }

    //请求完成
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }


}
