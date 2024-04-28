package cn.rh.flash.api.controller;


import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.security.JwtUtil;
import cn.rh.flash.utils.HttpUtil;
import cn.rh.flash.utils.JsonUtil;
import cn.rh.flash.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;

/**
 * 基础controller
 */
public class BaseController {
    private static Logger logger = LoggerFactory.getLogger(BaseController.class);

    /**
     * 根据token获取用户id，如果不存在则抛出异常
     * @return
     */
    public Long getIdUser() {
        String token = getToken();
        Long idUser = JwtUtil.getUserId(token);
        if (idUser == null) {
            throw new RuntimeException( Rets.expire().getMsg() );
        }
        return idUser;
    }

    public String getUcode() {
        String token = getToken();

        String ucode = JwtUtil.getUcode(token);

        if (ucode == null) {
            throw new RuntimeException( Rets.expire().getMsg() );
        }
        return ucode;
    }
    /**
    * @Description: 获取当前登录的部门ID
    * @Param:
    * @return:
    * @Author: Skj
    */
    public Long getDeptId() {
        String token = getToken();
        Long deptId = JwtUtil.getDeptId(token);
        if (deptId == null) {
            throw new RuntimeException( Rets.expire().getMsg() );
        }
        return deptId;
    }

    /**
    * @Description: 是否代理商
    * @Param:
    * @return:
    * @Author: Skj
    */
    public boolean isProxy() {
        Long deptId = getDeptId();
        if ( 3 == deptId ){
            return true;
        }
        return false;
    }


    public String getUsername() {
        String token = getToken();

        String username = JwtUtil.getUsername(token);
        if (username == null) {
            throw new RuntimeException( Rets.expire().getMsg() );
        }
        return username;
    }

    // 登录日志
    public void addSysLog(){




    }



    /**
     * 获取客户端token
    * @param request
     * @return
     */
    public String getToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    public String getToken() {
        return HttpUtil.getRequest().getHeader("Authorization");
    }

    /**
     * 获取前端传递过来的json字符串<br>
     * 如果前端使用axios的data方式传参则使用改方法接收参数
    * @return
     */
    public String getjsonReq() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(HttpUtil.getRequest().getInputStream()));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);

            }
            br.close();
            if (sb.length() < 1) {
                return "";
            }
            String reqBody = URLDecoder.decode(sb.toString(), "UTF-8");
            reqBody = reqBody.substring(reqBody.indexOf("{"));
            return reqBody;

        } catch (IOException e) {

            logger.error("获取json参数错误！{}", e.getMessage());

            return "";

        }

    }

    public <T> T getFromJson(Class<T> klass) {
        String jsonStr = getjsonReq();
        if (StringUtil.isEmpty(jsonStr)) {
            return null;
        }
        return JsonUtil.fromJson(klass, jsonStr);
    }


}
