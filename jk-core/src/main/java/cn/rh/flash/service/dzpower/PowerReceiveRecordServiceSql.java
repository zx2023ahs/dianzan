package cn.rh.flash.service.dzpower;

public class PowerReceiveRecordServiceSql {

    public static String getAmountGroupBy(Long uid, String time, String taskIdws) {

        StringBuffer sql = new StringBuffer();
        sql.append(" select taskidw, SUM(money) money FROM t_dzgoods_rowerreceiverecord ");
        sql.append(" where uid = "+uid);
        sql.append(" and end_time < '"+time+"' ");
        sql.append(" and taskidw in "+taskIdws);
        sql.append(" GROUP BY taskidw  ");

        return sql.toString();
//        return String(
//                " select taskidw, SUM(money) money FROM t_dzgoods_rowerreceiverecord where uid = %s and end_time < "+date+" and taskidw in %s GROUP BY taskidw ",
//                uid,taskIdws
//        );
    }

    public static String findCancelRefund(int page, int pageSize) {
        StringBuffer sql = new StringBuffer();
        sql.append(" select  ");
        sql.append(" id,  ");
        sql.append(" create_time as createTime, ");
        sql.append(" create_by as createBy, ");
        sql.append(" modify_time as modifyTime, ");
        sql.append(" modify_by as modifyBy, ");
        sql.append(" idw, ");
        sql.append(" source_invitation_code as sourceInvitationCode, ");
        sql.append(" uid, ");
        sql.append(" account, ");
        sql.append(" pbidw, ");
        sql.append(" image, ");
        sql.append(" name, ");
        sql.append(" banner_type as bannerType, ");
        sql.append(" pay_price as payPrice, ");
        sql.append(" tota_quantity as totalQuantity, ");
        sql.append(" last_time as lastTime, ");
        sql.append(" expire_time as expireTime, ");
        sql.append(" is_refund as isRefund, ");
        sql.append(" remark, ");
        sql.append(" start_time as startTime, ");
        sql.append(" end_time as endTime, ");
        sql.append(" hours, ");
        sql.append(" vip_type as vipType");
        sql.append(" FROM t_dzgoods_powerbanktask ");
        sql.append(" where expire_time < NOW() ");
        sql.append(" AND (ISNULL(is_refund) OR is_refund = 1) ");
        sql.append(" AND id IN (SELECT MAX(id) FROM `t_dzgoods_powerbanktask` GROUP BY account) limit "+page+","+pageSize);

        return sql.toString();
    }
}
