package cn.rh.flash.bean.constant.cache;

/**
 *  API 缓存的key集合
 */
public interface CacheApiKey {

    /* 验证码常量 */
    String key = "dz:code:";

    /* 图型验证码 */
    String imgCode = key + "imgCode";

    /* 短信验证码 */
    String phoneCode = key + "phoneCode";

    ///////

    /* 登录常量 */
    String LOGIN_CONSTANT = "login:token";

    /* 记录同步余额 */
    String BLA = "BLA";

    String BalanceRanking= "user:balance:rank";

}
