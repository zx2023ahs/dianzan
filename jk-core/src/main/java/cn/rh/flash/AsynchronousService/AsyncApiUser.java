package cn.rh.flash.AsynchronousService;

import cn.hutool.core.date.DateUtil;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.utils.CryptUtil;
import cn.rh.flash.utils.HttpUtil;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component
public class AsyncApiUser {

    @Autowired
    private ConfigCache configCache;
    @Autowired
    private SysLogService sysLogService;

    /**
     * 异步发送注册用户信息
     * 账号、区号、IP、对应站点平台、注册时间
     *
     * @throws Exception
     */
    @Async
    public void toUser(UserInfo aMobileAccount) {

        try {
            log.info("注册异步推送用户开始");
            long start = System.currentTimeMillis();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("siteInformation", configCache.get(ConfigKeyEnum.SITE_NICKNAME).trim());
            jsonObject.put("sing", CryptUtil.encrypt("addForeign"));
            jsonObject.put("cdbUserJsonBo", new JSONObject(aMobileAccount));
            Map<String, String> param = new HashMap<>();
            param.put("jsonString", CryptUtil.encrypt(jsonObject.toString()));
            String s = HttpUtil.doPost(configCache.get(ConfigKeyEnum.ADDUSERURL).trim(), param);
            JSONObject jsonObj = new JSONObject(s);
            int code = jsonObj.getInt("code");
            if (code!=200){
                sysLogService.addSysLog(aMobileAccount.getAccount(), null, "APP", SysLogEnum.USER_REG_INFO,"用户注册同步失败! 时间:"+DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss")+" 异常原因:"+jsonObj.getString("message"));
            }
            long end = System.currentTimeMillis();
            log.info("注册异步推送用户结束，耗时：" + (end - start) + "毫秒");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            sysLogService.addSysLog(aMobileAccount.getAccount(), null, "APP", SysLogEnum.USER_REG_INFO,"用户注册同步失败! 时间:"+DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss")+" 异常原因:"+e.getMessage());
        }
    }


}
