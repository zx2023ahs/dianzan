package cn.rh.flash.service.dzuser;

import cn.rh.flash.utils.StringUtil;

/**
 * @author jreak
 */
public class UserBalanceServiceSql {

    /**
     * 余额排行
     *
     * @param limit
     * @return
     */
    public static String sqlBalanceRanking(int limit,String ucode,String testCode) {
        String ucodeSql = " ";
        if (StringUtil.isNotEmpty(ucode)){
            ucodeSql = " and source_invitation_code =  '"+ucode+"' " ;
        }

        return String.format(
                " select %s,%s,%s from t_dzuser_balance "+" where 1 = 1 and uid not in " +
                        " (select id from  t_dzuser_user where source_invitation_code = '"+testCode+"') " +
                        ucodeSql+" ORDER BY user_balance desc limit 0,%s  ",
                "user_balance as `over`",
                "account",
                " ( " +
                        "SELECT count( id ) FROM t_dzuser_user WHERE pinvitation_code like " +
                        " (select CONCAt('%[',invitation_code,']%') from  t_dzuser_user where account = t_dzuser_balance.account )" +
                        " and levels >(select levels from  t_dzuser_user where account = t_dzuser_balance.account )  " +
                " ) AS numberOfSubordinates ",
                limit
        );
    }

    public static String sqlBalanceRanking(int current,int limit,String ucode,String testCode) {
        String ucodeSql = " ";
        if (StringUtil.isNotEmpty(ucode)){
            ucodeSql = " and b.source_invitation_code =  '"+ucode+"' " ;
        }
        Integer size=(current-1)*limit;
        String sql="SELECT b.user_balance AS `over`,b.account as account ,dzu.invitation_code," +
                " (SELECT count(u1.id)-1  FROM t_dzuser_user as u1 WHERE u1.pinvitation_code LIKE  CONCAt('%',dzu.invitation_code,'%') ) AS numberOfSubordinates " +
                " FROM t_dzuser_balance as b " +
                " LEFT JOIN t_dzuser_user as dzu on b.account=dzu.account " +
                " WHERE 1=1 "+ucodeSql+" AND  b.source_invitation_code != '"+testCode+"' " +
                " ORDER BY b.user_balance DESC  LIMIT "+size+","+limit+" ;";
        return sql;
    }






    /**
     * 余额排行
     *
     * @param limit
     * @return
     */
//    public static String sqlBalanceRanking(int limit, String ucode, String testCode) {
//        String ucodeSql = " ";
//        if (StringUtil.isNotEmpty(ucode)) {
//            ucodeSql = " and source_invitation_code =  '" + ucode + "' ";
//        }
//
//        return String.format(
//                " select %s,%s from t_dzuser_balance " + " where 1 = 1 and uid not in " +
//                        " (select id from  t_dzuser_user where source_invitation_code = '" + testCode + "') " +
//                        ucodeSql + " ORDER BY user_balance desc limit 0,%s  ",
//                "user_balance as `over`",
//                "account",
//                limit
//        );
//    }
//    // 查询人数
//    public static String getUserNum(String s) {
//        String sql = " SELECT au.account as account ," +
//                " ( " +
//                " SELECT count( id ) FROM t_dzuser_user WHERE pinvitation_code like " +
//                " (select CONCAt('%[',invitation_code,']%') from  t_dzuser_user where account = au.account ) " +
//                " and levels >(select levels from  t_dzuser_user where account = au.account ) " +
//                " ) AS numberOfSubordinates " +
//                " from t_dzuser_user as au where account IN  "+s;
//        return sql;
//    }

    /**
     * ip 排行
     *
     * @param limit
     * @return
     */
    public static String sqlIpRanking(int limit, String ucode, String testCode) {

        String ucodeSql = " ";
        if (StringUtil.isNotEmpty(ucode)) {
            ucodeSql = " and source_invitation_code =  '" + ucode + "'" +
                    " and levels > (SELECT levels FROM t_dzuser_user WHERE user_type = '2' and source_invitation_code = '" + ucode + "' )  ";
        }
        return String.format(
                " select %s,%s,%s from  t_dzuser_user " + " where 1 = 1  " + ucodeSql + " and source_invitation_code != '" + testCode + "' GROUP BY last_ip  ORDER BY number desc limit 0,%S ",
                "count(last_ip) as number",
                "last_ip as ip",
                "last_ip_city as lastIpCity",
                limit
        );
    }

    /**
     * 修改用户余额
     *
     * @param userBalance
     * @param id
     * @param dzversion
     * @return
     */
    public static String updateUserBalanceByIdAndDzversion(double userBalance, Long id, Integer dzversion) {
        return String.format(
                "UPDATE t_dzuser_balance SET user_balance = %s,dzversion = %s,modify_time=NOW()  WHERE id=%s and dzversion =%s",
                userBalance, (dzversion + 1), id, dzversion

        );
    }

    //
    public static String updateUserBalanceByIdAndDzversion2(double userBalance, Long id) {
        return String.format(
                "UPDATE t_dzuser_balance SET user_balance = %s,modify_time=NOW()  WHERE id=%s",
                userBalance, id
        );
    }


}
