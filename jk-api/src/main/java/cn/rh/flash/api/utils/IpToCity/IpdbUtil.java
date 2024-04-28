package cn.rh.flash.api.utils.IpToCity;

import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.utils.HttpUtil;
import lombok.extern.log4j.Log4j2;
import net.ipip.ipdb.City;

import java.io.File;
import java.util.Arrays;

@Log4j2
public class IpdbUtil {


    private static City city_DB;
//    static {
//        try {
//
//            configCache.get(ConfigKeyEnum.SYSTEM_FILE_UPLOAD_PATH) + File.separator + UUID.randomUUID().toString() + ".xlsx";
//
//
//            city_DB = new City(new IpdbUtil().getClass().getResource("/").getPath() + "ipipfree.ipdb");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 通过IP查询城市
     */
    public static String findCity(String ip,ConfigCache configCache) {

        try {
            city_DB = new City( configCache.get(ConfigKeyEnum.SYS_FILE_PATH) + File.separator +"ipipfree.ipdb");
            // [中国, 陕西, 西安]
            String[] strings = city_DB.find(ip, "CN");
            if( "本机地址".equals( strings[0] ) ){
                return "[本机地址 ]";
            }
            return Arrays.toString( strings );
        } catch (Exception e) {
            log.error( "通过IP查询城市："+e.getMessage() );
            return "";
        }
    }
    public static String findCity(ConfigCache configCache) {
        return findCity( HttpUtil.getIp() ,configCache);
    }

}
