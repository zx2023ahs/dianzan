package cn.rh.flash.security.apitoken;

import cn.rh.flash.bean.constant.cache.CacheApiKey;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.exception.ApiException;
import cn.rh.flash.cache.impl.EhcacheDao;
import cn.rh.flash.utils.CryptUtil;
import cn.rh.flash.utils.HttpUtil;
import cn.rh.flash.utils.JsonUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class ApiToken {

    public static final int EXPIRATION = 30*24*60*60; // 有效时间  秒

    @Autowired
    private EhcacheDao ehcacheDao;

    /**
     * 创建 api token
     * @param userInfo
     * @return
     */
     public String createToken(UserInfo userInfo, long logInTime){

         ApiLoginObject apiLoginObject = new ApiLoginObject();
         apiLoginObject.setLogInTime( logInTime );
         apiLoginObject.setUserInfo( userInfo );
         try {
             String encrypt = CryptUtil.encrypt(JsonUtil.toJson(apiLoginObject));
             if( logInTime == 0 ){
                 ehcacheDao.hset( CacheApiKey.LOGIN_CONSTANT, userInfo.getCountryCodeNumber()+"_"+userInfo.getAccount(),encrypt);
             }else{
                 ehcacheDao.hset( CacheApiKey.LOGIN_CONSTANT, userInfo.getCountryCodeNumber()+"_"+userInfo.getAccount(),encrypt,EXPIRATION);
             }

             return encrypt;
         } catch (Exception e) {
             //e.printStackTrace();
             log.error( String.format("创建token 失败 【%s】", e.getMessage() ) );
             return "";
         }
     }

    /**
     * 解析token
     * @param token
     * @return
     */
     public ApiLoginObject parseToken( String token ){
         if (token == null) {
             throw new ApiException( MessageTemplateEnum.TOKEN_EMPTY );
         }else{
             try {
                 String jsonApiLoginObject = CryptUtil.desEncrypt( token );
                 ApiLoginObject apiLoginObject = JsonUtil.fromJson(ApiLoginObject.class, jsonApiLoginObject);
                 tokenDropped( apiLoginObject.getUserInfo() );
                 return apiLoginObject;
             } catch (Exception e) {
                 if( e instanceof ApiException ){
                     throw (ApiException)e;
                 }else{
                     if( e.getMessage() != null ){
                         log.error( String.format("解析token 失败 【%s】", e.getMessage()  ) );
                     }
                     throw new ApiException( MessageTemplateEnum.PARSE_TOKEN_FAIL );
                 }
             }
         }
     }

    /**
     * 没有登录  or  别处登录
     * @param userInfo
     */
    private void tokenDropped(UserInfo userInfo) {
        Object rdsToken = ehcacheDao.hget( CacheApiKey.LOGIN_CONSTANT, userInfo.getCountryCodeNumber()+"_"+userInfo.getAccount());
        /* 没有登录 */
        if (  rdsToken == null ) {
            log.error( "token为空的用户:"+userInfo );
            log.error( "token为空的用户ip:"+HttpUtil.getIp() );
            throw new ApiException( MessageTemplateEnum.TOKEN_NULL);
        }
        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
        /* 别处登录 */
        if( !(rdsToken+"").equals( HttpUtil.getApiToken() ) ) {
            throw new ApiException( MessageTemplateEnum.TOKEN_REMOTE_LOGIN);
        }
    }

    /**
     * api token 是否过期
     * @param apiLoginObject
     * @return ==
     */
     public boolean  whetherTheTokenHasExpired( ApiLoginObject apiLoginObject ){

         if( apiLoginObject !=null ){
             long logInTime = apiLoginObject.getLogInTime();  // 是否有过期时间限制
             if( logInTime != 0 ){
                 if(  Math.abs( System.currentTimeMillis() - logInTime ) < ( EXPIRATION * 1000 )  ){
                     throw new ApiException( MessageTemplateEnum.TOKEN_EXPIRED);
                 }
             }
         }
         return true;
     }
}
