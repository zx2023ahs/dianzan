package cn.rh.flash.service.dzpower;

import cn.rh.flash.bean.entity.dzpower.RecordPb;

public class RecordPbServiceSql {
    public static String updateRecordPb(RecordPb record, String dateTime) {
        String sql =  String.format(
                " INSERT INTO t_dzgoods_recordpb SET create_time = '%s',create_by = %s,modify_time = '%s',modify_by = %s,idw = %s,source_invitation_code = '%s'," +
                        "uid = %s,account = '%s',money = %s,former_credit_score = %s,post_credit_score = %s,source_user_account = '%s',relevels = %s," +
                        "fidw = '%s',rebate_time = '%s'",
                dateTime,-1,dateTime,-1,record.getIdw(),record.getSourceInvitationCode(),record.getUid(),record.getAccount(),record.getMoney(),
                record.getFormerCreditScore(),record.getPostCreditScore(),record.getSourceUserAccount(),record.getRelevels(),record.getFidw(),dateTime
        );
        return sql;
    }

    public static String getSum(String where) {
        return "select SUM(money) as sum from t_dzgoods_recordpb  where 1=1 "+where;
    }


    public static String getCount(String startTime, String endTime, String ucode,String testCode) {
        if (!"admin".equals(ucode)){
            return "  SELECT count(1) as count FROM t_dzgoods_recordpb WHERE fidw is null "+
                    " and create_time >= '"+startTime+"' and create_time <= '"+endTime+"'  "+
                    " and source_invitation_code != '"+testCode+"'  "+
                    " and source_invitation_code =  '"+ucode+"'    " ;
        }
        return "  SELECT count(1) as count FROM t_dzgoods_recordpb WHERE fidw is null   "+
                " and create_time >= '"+startTime+"' and create_time <= '"+endTime+"'  "+
                " and source_invitation_code != '"+testCode+"'  ";
    }

    public static String getVipSum(String startTime, String endTime, String ucode,String testCode) {
        if (!"admin".equals(ucode)){
            return "  SELECT " +
                    " SUM( CASE WHEN relevels = 1 THEN money ELSE 0 END ) AS sum_relevels_1, " +
                    " SUM( CASE WHEN relevels = 2 THEN money ELSE 0 END ) AS sum_relevels_2, " +
                    " SUM( CASE WHEN relevels = 3  THEN money ELSE 0 END ) AS sum_relevels_3, " +
                    " sum(money) as sum "+
                    " FROM t_dzgoods_recordpb WHERE fidw is null "+
                    " and create_time >= '"+startTime+"' and create_time <= '"+endTime+"'  "+
                    " and source_invitation_code != '"+testCode+"'  "+
                    " and source_invitation_code =  '"+ucode+"'    " ;
        }
        return "  SELECT " +
                " SUM( CASE WHEN relevels = 1 THEN money ELSE 0 END ) AS sum_relevels_1, " +
                " SUM( CASE WHEN relevels = 2 THEN money ELSE 0 END ) AS sum_relevels_2, " +
                " SUM( CASE WHEN relevels = 3  THEN money ELSE 0 END ) AS sum_relevels_3, " +
                " sum(money) as sum "+
                " FROM t_dzgoods_recordpb WHERE fidw is null "+
                " and create_time >= '"+startTime+"' and create_time <= '"+endTime+"'  "+
                " and source_invitation_code != '"+testCode+"'  ";
    }
}
