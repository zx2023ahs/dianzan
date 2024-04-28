package cn.rh.flash.service.dzsys;

import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.StringUtil;

public class DashBoardServiceSql {

    public static String dashBoardOne(String ucode, String testCode) {
        String ucodeSql = " ";
        if (StringUtil.isNotEmpty(ucode)) {
            ucodeSql = " and source_invitation_code =  '" + ucode + "' ";
        }
        return String.format(
                " select %s,%s,%s,%s from t_dzuser_user  where 1 = 1 and user_type = '1' and source_invitation_code != '" + testCode + "' " + ucodeSql,
                "sum( case when DATEDIFF( registration_time,NOW())=0  then 1 else 0 end) as addUserToday ", //今日新增
                "sum( case when DATEDIFF( registration_time,NOW())=-1 then 1 else 0 end) as addUserYesterday ", //昨日新增
                "sum( case when 1=1 then 1 else 0 end) as userTotal", //总数 总新增
                "sum( case when vip_type = 'v0' then 0 when vip_type = 'v1' then 0 else 1 end) as vipTotal"//vip总数

        );
    }

    public static String dashBoardTwo(String ucode, String testCode) {
        String ucodeSql = " ";
        if (StringUtil.isNotEmpty(ucode)) {
            ucodeSql = " and source_invitation_code =  '" + ucode + "' " +
                    " and levels > (SELECT levels FROM t_dzuser_user WHERE user_type = '2' and source_invitation_code = '" + ucode + "') ";
        }
        return String.format(
                " select %s,%s from t_dzvip_vippurchase %s ",
                "sum( case when DATEDIFF( create_time,NOW())=0  then 1 else 0 end) as addVipToday ", // VIP今日新增
                "sum( case when DATEDIFF( create_time,NOW())=-1 then 1 else 0 end) as addVipYesterday ", // VIP昨日新增
//                "sum( case when 1=1 then 1 else 0 end) as vipTotal", //vip总数

                " where 1=1 and whether_to_pay = 2 and (previous_vip_type = 'v0' or previous_vip_type = 'v1') and after_vip_type != 'v1' " +
                        "and uid in (select id from t_dzuser_user where 1=1 and user_type = '1'" +
                        " and source_invitation_code != '" + testCode + "' " + ucodeSql + ")"
        );

    }

    public static String dashBoardFive(String ucode, String testCode) {
        String ucodeSql = " ";
        if (StringUtil.isNotEmpty(ucode)) {
            ucodeSql = " and source_invitation_code =  '" + ucode + "' " +
                    " and levels > (SELECT levels FROM t_dzuser_user WHERE user_type = '2' and source_invitation_code = '" + ucode + "') ";
        }
        return String.format(
                " select %s,%s,%s from t_dzuser_rechargehistory %s ",
                "sum( case when DATEDIFF( modify_time,NOW())=0  then 1 else 0 end) as chargeNumToday ", // 今日充值数量(非首充)
                "sum( case when DATEDIFF( modify_time,NOW())=-1 then 1 else 0 end) as chargeNumYesterday ", // 昨日充值数量(非首充)
                "sum( case when 1=1 then 1 else 0 end) as chargeNumTotal", //充值总数(非首充)

                " where 1=1 and recharge_status = '3' and uid in " +
                        "(select id from t_dzuser_user where 1=1 and user_type = '1'" +
                        " and source_invitation_code != '" + testCode + "' " + ucodeSql + ")"
        );
    }

    public static String dashBoardFiveFirst(String ucode, String testCode) {
        String ucodeSql = " ";
        if (StringUtil.isNotEmpty(ucode)) {
            ucodeSql = " and source_invitation_code =  '" + ucode + "' " +
                    " and levels > (SELECT levels FROM t_dzuser_user WHERE user_type = '2' and source_invitation_code = '" + ucode + "') ";
        }
        return String.format(
                " select %s,%s from (SELECT r.* FROM (SELECT * FROM t_dzuser_rechargehistory where recharge_status = '3') as r GROUP BY account) as re %s ",
                "sum( case when DATEDIFF( re.modify_time,NOW())=0  then 1 else 0 end) as chargeNumTodayFirst ", // 今日充值数量(首充)
                "sum( case when DATEDIFF( re.modify_time,NOW())=-1 then 1 else 0 end) as chargeNumYesterdayFirst ", // 昨日充值数量(首充)
//                "sum( case when 1=1 then 1 else 0 end) as chargeNumTotalFirst", //充值总数(首充)

                " where 1=1 and uid in " +
                        "(select id from t_dzuser_user where 1=1 and user_type = '1'" +
                        " and source_invitation_code != '" + testCode + "' " + ucodeSql + ")"
        );
    }

    public static String dashBoardSix(String ucode, String testCode) {
        String ucodeSql = " ";
        if (StringUtil.isNotEmpty(ucode)) {
            ucodeSql = " and source_invitation_code =  '" + ucode + "' " +
                    " and levels > (SELECT levels FROM t_dzuser_user WHERE user_type = '2' and source_invitation_code = '" + ucode + "') ";
        }
        return String.format(
                " select %s,%s,%s from t_dzuser_rechargehistory %s ",
                "sum( case when DATEDIFF( modify_time,NOW())=0  then money else 0 end) as chargeMoneyToday ", // 今日充值金额(非首充)
                "sum( case when DATEDIFF( modify_time,NOW())=-1 then money else 0 end) as chargeMoneyYesterday ", // 昨日充值金额(非首充)
                "sum( case when 1=1 then money else 0 end) as chargeMoneyTotal", //充值金额总数(非首充)

                " where 1=1 and recharge_status = '3' and uid in " +
                        "(select id from t_dzuser_user where 1=1 and user_type = '1'" +
                        " and source_invitation_code != '" + testCode + "' " + ucodeSql + ")"
        );
    }

    public static String dashBoardSixFirst(String ucode, String testCode) {
        String ucodeSql = " ";
        if (StringUtil.isNotEmpty(ucode)) {
            ucodeSql = " and source_invitation_code =  '" + ucode + "' " +
                    " and levels > (SELECT levels FROM t_dzuser_user WHERE user_type = '2' and source_invitation_code = '" + ucode + "') ";
        }
        return String.format(
                " select %s,%s from (SELECT r.* FROM (SELECT * FROM t_dzuser_rechargehistory where recharge_status = '3') as r GROUP BY account) as re %s ",
                "sum( case when DATEDIFF( re.modify_time,NOW())=0  then re.money else 0 end) as chargeMoneyTodayFirst ", // 今日充值金额(首充)
                "sum( case when DATEDIFF( re.modify_time,NOW())=-1 then re.money else 0 end) as chargeMoneyYesterdayFirst ", // 昨日充值金额(首充)
//                "sum( case when 1=1 then re.money else 0 end) as chargeMoneyTotalFirst", //充值金额总数(首充)

                " where 1=1 and uid in " +
                        "(select id from t_dzuser_user where 1=1 and user_type = '1'" +
                        " and source_invitation_code != '" + testCode + "' " + ucodeSql + ")"
        );
    }

    public static String dashBoardSeven(String ucode, String testCode) {
        String ucodeSql = " ";
        if (StringUtil.isNotEmpty(ucode)) {
            ucodeSql = " and source_invitation_code =  '" + ucode + "' " +
                    " and levels > (SELECT levels FROM t_dzuser_user WHERE user_type = '2' and source_invitation_code = '" + ucode + "') ";
        }
        return String.format(
                " select %s,%s,%s from t_dzuser_withdrawals %s ",
                "sum( case when DATEDIFF( modify_time,NOW())=0  then 1 else 0 end) as withNumToday ", // 今日提现数量
                "sum( case when DATEDIFF( modify_time,NOW())=-1 then 1 else 0 end) as withNumYesterday ", // 昨日提现数量
                "sum( case when 1=1 then 1 else 0 end) as withNumTotal", //提现总数

                " where 1=1 and (recharge_status = 'suc' or recharge_status = 'sysok') and uid in " +
                        "(select id from t_dzuser_user where 1=1 and user_type = '1'" +
                        " and source_invitation_code != '" + testCode + "' " + ucodeSql + ")"
        );
    }

    public static String dashBoardEight(String ucode, String testCode) {
        String ucodeSql = " ";
        if (StringUtil.isNotEmpty(ucode)) {
            ucodeSql = " and source_invitation_code =  '" + ucode + "' " +
                    " and levels > (SELECT levels FROM t_dzuser_user WHERE user_type = '2' and source_invitation_code = '" + ucode + "') ";
        }
        return String.format(
                " select %s,%s,%s from t_dzuser_withdrawals %s ",
                "sum( case when DATEDIFF( modify_time,NOW())=0  then money else 0 end) as withMoneyToday ", // 今日提现金额
                "sum( case when DATEDIFF( modify_time,NOW())=-1 then money else 0 end) as withMoneyYesterday ", // 昨日提现金额
                "sum( case when 1=1 then money else 0 end) as withMoneyTotal", //提现金额总数

                " where 1=1 and (recharge_status = 'suc' or recharge_status = 'sysok') and uid in " +
                        "(select id from t_dzuser_user where 1=1 and user_type = '1'" +
                        " and source_invitation_code != '" + testCode + "' " + ucodeSql + ")"
        );
    }

    public static String dashBoardNine(String ucode, String testCode) {
        String ucodeSql = " ";
        if (StringUtil.isNotEmpty(ucode)) {
            ucodeSql = " and source_invitation_code =  '" + ucode + "' " +
                    " and levels > (SELECT levels FROM t_dzuser_user WHERE user_type = '2' and source_invitation_code = '" + ucode + "') ";
        }
        String userSql = " select * from t_dzuser_user where 1 = 1 and user_type = '1' and source_invitation_code != '" + testCode + "' " + ucodeSql;
        return String.format(
                " select %s,%s,%s from (" + userSql + " ) as u left join t_dzuser_balance as b on u.id = b.uid",
                "sum( case when u.vip_type = 'v0' or u.vip_type = 'v1' then b.user_balance else 0 end) as userBalance ", // 普通用户余额
                "sum( case when u.vip_type != 'v0' and u.vip_type != 'v1'  then b.user_balance else 0 end) as vipBalance ", // vip用户余额
                "sum( case when 1=1  then b.user_balance else 0 end) as balanceTotal " // 用户余额总数
        );
    }

    public static String dashBoardTen(String ucode, String testCode) {
        String ucodeSql = " ";
        if (StringUtil.isNotEmpty(ucode)) {
            ucodeSql = " and source_invitation_code =  '" + ucode + "' " +
                    " and levels > (SELECT levels FROM t_dzuser_user WHERE user_type = '2' and source_invitation_code = '" + ucode + "') ";
        }
        return String.format(
                " select %s,%s,%s,%s,%s,%s,%s,%s,%s from t_dzvip_vippurchase %s ",
                "sum( case when DATEDIFF( create_time,NOW())=0 and after_vip_type='v2'  then 1 else 0 end) as todayNewV2 ",
                "sum( case when DATEDIFF( create_time,NOW())=0 and after_vip_type='v3'  then 1 else 0 end) as todayNewV3 ",
                "sum( case when DATEDIFF( create_time,NOW())=0 and after_vip_type='v4'  then 1 else 0 end) as todayNewV4 ",
                "sum( case when DATEDIFF( create_time,NOW())=0 and after_vip_type='v5'  then 1 else 0 end) as todayNewV5 ",
                "sum( case when DATEDIFF( create_time,NOW())=0 and after_vip_type='v6'  then 1 else 0 end) as todayNewV6 ",
                "sum( case when DATEDIFF( create_time,NOW())=0 and after_vip_type='v7'  then 1 else 0 end) as todayNewV7 ",
                "sum( case when DATEDIFF( create_time,NOW())=0 and after_vip_type='v8'  then 1 else 0 end) as todayNewV8 ",
                "sum( case when DATEDIFF( create_time,NOW())=0 and after_vip_type='v9'  then 1 else 0 end) as todayNewV9 ",
                "sum( case when DATEDIFF( create_time,NOW())=0 and after_vip_type='v10'  then 1 else 0 end) as todayNewV10 ",


                " where 1=1 and whether_to_pay = 2 and (previous_vip_type = 'v0' or previous_vip_type = 'v1') " +
                        "and uid in (select id from t_dzuser_user where 1=1 and user_type = '1' and source_invitation_code != '" + testCode + "' " + ucodeSql + ")"
        );
    }


    public static String getECharts(String ucode, String testCode) {
        String ucodeSql = " uid in (select id from t_dzuser_user where 1=1 and user_type = '1' and source_invitation_code != '" + testCode + "' ";

        if (StringUtil.isNotEmpty(ucode)) {
            ucodeSql = ucodeSql + " and source_invitation_code =  '" + ucode + "' " +
                    " and levels > (SELECT levels FROM t_dzuser_user WHERE user_type = '2' and source_invitation_code = '" + ucode + "')";
        }
        ucodeSql = ucodeSql + " ) ";
        StringBuffer sql = new StringBuffer();

        sql.append(" SELECT d.day,IFNULL(dr.money,0) as cmoney,IFNULL(dw.money,0) as tmoney, ");
        sql.append(" IFNULL(IFNULL(dr.money,0)-IFNULL(dw.money,0),0) as money FROM ");
        sql.append("    (SELECT @cdate \\:= DATE_ADD(@cdate, INTERVAL - 1 DAY) DAY FROM ");
        sql.append("        (SELECT @cdate \\:= DATE_ADD('" + DateUtil.getDay() + "', INTERVAL + 1 DAY)  ");
        sql.append("        FROM t_dzuser_rechargehistory) t0 LIMIT 15) d ");

        sql.append("        LEFT JOIN ");

        sql.append("        (SELECT DATE_FORMAT(modify_time,'%Y-%m-%d') as dayNum, SUM(money) as money ");
        sql.append("        FROM t_dzuser_rechargehistory WHERE recharge_status = 3 AND " + ucodeSql + " GROUP BY daynum) dr ON d.day = dr.dayNum ");

        sql.append("        LEFT JOIN ");

        sql.append("        (SELECT DATE_FORMAT(modify_time,'%Y-%m-%d') as dayNum, SUM(money) as money ");
        sql.append("        FROM t_dzuser_withdrawals WHERE (recharge_status = 'suc' or recharge_status = 'sysok') AND " + ucodeSql + " GROUP BY daynum) dw ON d.day = dw.dayNum ");
        sql.append(" ORDER BY d.day asc ");

        return sql.toString();
    }

    public static String getDayReport(String ucode, String testCode, String endDay, long daySub) {
        String ucodeSql = " in (select id from t_dzuser_user where 1=1 and user_type = '1' and source_invitation_code != '" + testCode + "' ";

        if (StringUtil.isNotEmpty(ucode)) {
            ucodeSql = ucodeSql + " and source_invitation_code =  '" + ucode + "' " +
                    " and levels > (SELECT levels FROM t_dzuser_user WHERE user_type = '2' and source_invitation_code = '" + ucode + "')";
        }
        ucodeSql = ucodeSql + " ) ";

        StringBuffer sql = new StringBuffer();

        sql.append(" SELECT ");
        sql.append(" d.day as day,IFNULL(u.num,0) as registrationNum,IFNULL(v.num,0) as vipNum, ");
        sql.append(" IFNULL(vr.num,0) as vipFirstNum,IFNULL(dr.num,0) as cNum,IFNULL(dr.money,0) as cMoney, ");
        sql.append(" IFNULL(dw.num,0) as tNum,IFNULL(dw.money,0) as tMoney,IFNULL(dr.money-dw.money,0) as money, ");
        sql.append(" IFNULL(dv.l1,0) as l1Vip,IFNULL(dv.l2,0) as l2Vip,IFNULL(dv.l3,0) as l3Vip, ");
        sql.append(" IFNULL(pb.l1,0) as l1Pb,IFNULL(pb.l2,0) as l2Pb,IFNULL(pb.l3,0) as l3Pb, ");
        sql.append(" IFNULL(pb.total,0) as totalPb,IFNULL(dc.money,0) as dcMoney,IFNULL(pb.l0,0) as pbNum ");
        sql.append(" FROM ");
        sql.append(" (SELECT @cdate \\:= DATE_ADD(@cdate, INTERVAL - 1 DAY) DAY FROM ");
        sql.append("  (SELECT @cdate \\:= DATE_ADD('" + endDay + "', INTERVAL + 1 DAY) ");
        sql.append("  FROM t_dzuser_rechargehistory) t0 LIMIT " + daySub + ") d ");
        sql.append(" LEFT JOIN ");
        sql.append(" (SELECT DATE_FORMAT(registration_time,'%Y-%m-%d') as dayNum, COUNT(1) as num ");
        sql.append(" FROM t_dzuser_user WHERE id " + ucodeSql + " GROUP BY daynum ) u ON d.day = u.dayNum ");
        sql.append(" LEFT JOIN ");
        sql.append(" (SELECT DATE_FORMAT(create_time,'%Y-%m-%d') as dayNum ,COUNT(1) as num ");
        sql.append(" FROM t_dzvip_vippurchase where whether_to_pay = 2 and (previous_vip_type = 'v0' or previous_vip_type = 'v1')  ");
        sql.append(" and after_vip_type != 'v1' and uid " + ucodeSql + " GROUP BY daynum) v ON d.day = v.dayNum ");
        sql.append(" LEFT JOIN ");
        sql.append(" (select DATE_FORMAT(modify_time,'%Y-%m-%d') as dayNum ,COUNT(1) as num from  ");
        sql.append(" (SELECT r.* FROM (SELECT * FROM t_dzuser_rechargehistory where recharge_status = '3' and uid IN (select id FROM t_dzuser_user where vip_type !='v0' ");
        sql.append(" and vip_type !='v1') and uid " + ucodeSql + " ) as r GROUP BY account) as re GROUP BY daynum) vr ON d.day = vr.dayNum ");
        sql.append(" LEFT JOIN ");
        sql.append(" (select DATE_FORMAT(modify_time,'%Y-%m-%d') as dayNum ,COUNT(1) as num,SUM(money)as money ");
        sql.append(" FROM t_dzuser_rechargehistory where recharge_status = '3' and uid " + ucodeSql + " GROUP BY daynum) dr ON d.day = dr.dayNum ");
        sql.append(" LEFT JOIN ");
        sql.append(" (select DATE_FORMAT(modify_time,'%Y-%m-%d') as dayNum ,COUNT(1) as num,SUM(money)as money  ");
        sql.append(" FROM t_dzuser_withdrawals where 1=1 and (recharge_status = 'suc' or recharge_status = 'sysok') and uid " + ucodeSql + " GROUP BY daynum ) dw ON d.day = dw.dayNum ");
        sql.append(" LEFT JOIN  ");
        sql.append(" (select DATE_FORMAT(create_time,'%Y-%m-%d') as dayNum ,sum( case when relevels = 1 then money else 0 end) as l1, ");
        sql.append(" sum( case when relevels = 2 then money else 0 end) as l2,sum( case when relevels = 3 then money else 0 end) as l3  ");
        sql.append(" FROM t_dzvip_viprebaterecord where uid " + ucodeSql + " GROUP BY daynum ) dv ON d.day = dv.dayNum ");
        sql.append(" LEFT JOIN  ");
        sql.append(" (select DATE_FORMAT(create_time,'%Y-%m-%d') as dayNum ,sum( case when relevels = 1 then money else 0 end) as l1, ");
        sql.append(" sum( case when relevels = 2 then money else 0 end) as l2,sum( case when relevels = 3 then money else 0 end) as l3, ");
        sql.append(" sum( case when 1=1 then money else 0 end) as total,sum( case when relevels = 0 then 1 else 0 end) as l0 ");
        sql.append(" FROM t_dzgoods_recordpb where uid " + ucodeSql + "  GROUP BY daynum ) pb ON d.day = pb.dayNum");
        sql.append(" LEFT JOIN ");
        sql.append(" (select DATE_FORMAT(create_time,'%Y-%m-%d') as dayNum,SUM(money) as money FROM ");
        sql.append(" t_dzuser_compensation WHERE operator = 'reg_gift' and uid " + ucodeSql + " GROUP BY daynum) dc ON d.day = dc.dayNum ");
        sql.append(" ORDER BY d.day asc ");

        return sql.toString();
    }
}
