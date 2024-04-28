package cn.rh.flash.service.dzvip;

import cn.rh.flash.utils.StringUtil;

public class DzVipMessageServiceSql {

    public static String findVipCount(String ucode, String testCode) {
        String ucodeSql = " ";
        if (StringUtil.isNotEmpty(ucode)){
            ucodeSql = " and source_invitation_code =  '"+ucode+"' " +
                    " and levels > (SELECT levels FROM t_dzuser_user WHERE user_type = '2' and source_invitation_code = '"+ucode+"') " ;
        }
        return String.format(
                " select %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s from t_dzuser_user %s ",
                "sum( case when vip_type='v1' then 1 else 0 end) as todayNewV1 ",
                "sum( case when vip_type='v2' then 1 else 0 end) as todayNewV2 ",
                "sum( case when vip_type='v3' then 1 else 0 end) as todayNewV3 ",
                "sum( case when vip_type='v4' then 1 else 0 end) as todayNewV4 ",
                "sum( case when vip_type='v5' then 1 else 0 end) as todayNewV5 ",
                "sum( case when vip_type='v6' then 1 else 0 end) as todayNewV6 ",
                "sum( case when vip_type='v7' then 1 else 0 end) as todayNewV7 ",
                "sum( case when vip_type='v8' then 1 else 0 end) as todayNewV8 ",
                "sum( case when vip_type='v9' then 1 else 0 end) as todayNewV9 ",
                "sum( case when vip_type='v10' then 1 else 0 end) as todayNewV10 ",
                "sum( case when vip_type='v11' then 1 else 0 end) as todayNewV11 ",
                "sum( case when vip_type='v12' then 1 else 0 end) as todayNewV12 ",
                "sum( case when vip_type='v13' then 1 else 0 end) as todayNewV13 ",
                "sum( case when vip_type='v14' then 1 else 0 end) as todayNewV14 ",
                "sum( case when vip_type='v15' then 1 else 0 end) as todayNewV15 ",
                "sum( case when vip_type='v16' then 1 else 0 end) as todayNewV16 ",
                "sum( case when vip_type='v17' then 1 else 0 end) as todayNewV17 ",
                "sum( case when vip_type='v18' then 1 else 0 end) as todayNewV18 ",
                "sum( case when vip_type='v19' then 1 else 0 end) as todayNewV19 ",
                "sum( case when vip_type='v20' then 1 else 0 end) as todayNewV20 ",
                "sum( case when vip_type='v21' then 1 else 0 end) as todayNewV21 ",
                "sum( case when vip_type='v22' then 1 else 0 end) as todayNewV22 ",
                "sum( case when vip_type='v23' then 1 else 0 end) as todayNewV23 ",
                "sum( case when vip_type='v24' then 1 else 0 end) as todayNewV24 ",
                "sum( case when vip_type='v25' then 1 else 0 end) as todayNewV25 ",
                "sum( case when vip_type='v26' then 1 else 0 end) as todayNewV26 ",
                "sum( case when vip_type='v27' then 1 else 0 end) as todayNewV27 ",
                "sum( case when vip_type='v28' then 1 else 0 end) as todayNewV28 ",
                "sum( case when vip_type='v29' then 1 else 0 end) as todayNewV29 ",
                "sum( case when vip_type='v30' then 1 else 0 end) as todayNewV30 ",


                " where 1=1 and user_type = '1' and source_invitation_code != '"+testCode+"' "+ucodeSql
        );
    }

    public static String findVipImgSql(Long uid) {
        return String.format(
                " select v.power_bank_img as powerBankImg from t_dzuser_user as u left join t_dzvip_vipmessage as v on u.vip_type = v.vip_type " +
                " where u.id = '"+uid+"'"
        );
    }

    public static String getRandomRecord() {
        return " SELECT `name` FROM `t_dzvip_vipmessage` WHERE id >= ( SELECT floor( RAND() * ( SELECT MAX( id ) FROM `t_dzvip_vipmessage` ) ) ) ORDER BY id  LIMIT 5 ";
    }
}
