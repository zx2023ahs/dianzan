package cn.rh.flash.security.apitoken;

import cn.rh.flash.bean.entity.dzuser.UserInfo;
import lombok.Data;

/**
 * api登录对象
 */
@Data
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiLoginObject {
    private long logInTime;  // 登录时间   0 表示永不失效
    private UserInfo userInfo;   // 用信息

}
