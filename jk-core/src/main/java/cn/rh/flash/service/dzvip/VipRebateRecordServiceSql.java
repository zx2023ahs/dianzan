package cn.rh.flash.service.dzvip;

public class VipRebateRecordServiceSql {



    public static String getSum(String where) {
        return "select SUM(money) as sum from t_dzvip_viprebaterecord  where 1=1 "+where;
    }

    public static String getVipSum(String startTime, String endTime, String ucode,String testCode) {
        if (!"admin".equals(ucode)){
            return "  SELECT "+
                    " SUM( CASE WHEN relevels = 1 THEN money ELSE 0 END ) AS sum_relevels_1, "+
                    " SUM( CASE WHEN relevels = 2 THEN money ELSE 0 END ) AS sum_relevels_2, "+
                    " SUM( CASE WHEN relevels = 3 THEN money ELSE 0 END ) AS sum_relevels_3  "+
                    " FROM "+
                    " t_dzvip_viprebaterecord "+
                    " WHERE fidw is null "+
                    " and modify_time >= '"+startTime+"' "+
                    " and modify_time <= '"+endTime+"'  "+
                    " and source_invitation_code != '"+testCode+"'  "+
                    " and source_invitation_code =  '"+ucode+"'    " ;

        }
        return "  SELECT "+
                " SUM( CASE WHEN relevels = 1 THEN money ELSE 0 END ) AS sum_relevels_1, "+
                " SUM( CASE WHEN relevels = 2 THEN money ELSE 0 END ) AS sum_relevels_2, "+
                " SUM( CASE WHEN relevels = 3 THEN money ELSE 0 END ) AS sum_relevels_3  "+
                " FROM "+
                " t_dzvip_viprebaterecord "+
                " WHERE fidw is null "+
                " and modify_time >= '"+startTime+"' "+
                " and modify_time <= '"+endTime+"'  "+
                " and source_invitation_code != '"+testCode+"'  ";

    }
}
