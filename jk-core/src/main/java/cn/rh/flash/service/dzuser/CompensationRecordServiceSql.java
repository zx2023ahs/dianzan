package cn.rh.flash.service.dzuser;

import cn.rh.flash.utils.StringUtil;

public class CompensationRecordServiceSql {

    // 补分总记录
    public static String sqlBranchTotal(String operator,String account,String expireTimes,String expireTimee,String sourceInvitationCode,String testCode) {
        String s1 = " ";
        if (StringUtil.isNotEmpty(operator)){
            s1 = " and operator = '"+operator+"' ";
        }
        String s2 = " ";
        if (StringUtil.isNotEmpty(account)){
            s2 = " and account = '"+account+"' ";
        }
        String s3 = " ";
        if (StringUtil.isNotEmpty(sourceInvitationCode)){
            s3 = " and source_invitation_code = '"+sourceInvitationCode+"' ";
        }
        String s4 = " ";
        if (StringUtil.isNotEmpty(expireTimes)&&StringUtil.isNotEmpty(expireTimee)){
//            Date dateTimes = DateUtil.parseTime(expireTimes);
//            Date dateTimee = DateUtil.parseTime(expireTimee);
            s4 = " and create_time >= '"+expireTimes+"' AND create_time <'"+expireTimee+"' ";
        }
        return String.format(
                " select sum(IF(addition_and_subtraction = 1,money,money*-1)) as branchTotal from t_dzuser_compensation " +
                        " where source_invitation_code != '"+testCode+"' and uid not in (select id from t_dzuser_user where user_type = 2) and operator !='reg_gift'  " +
                        s1+s2+s3+s4
        );
    }

    public static String getSum(String startTime, String endTime, String ucode,String testCode) {
        if (!"admin".equals(ucode)){
         return  "SELECT SUM(money) FROM t_dzuser_compensation WHERE operator = 'reg_gift' "+
                 "  and create_time >= '"+startTime+"'  "+
                 "  and create_time <= '"+endTime+"'  "+
                 " and source_invitation_code != '"+testCode+"'  "+
                 " and source_invitation_code =  '"+ucode+"'    " ;
        }
        return  "SELECT SUM(money) FROM t_dzuser_compensation WHERE operator = 'reg_gift' "+
                "  and create_time >= '"+startTime+"'  "+
                "  and create_time <= '"+endTime+"'  "+
                "  and source_invitation_code != '"+testCode+"'  ";
    }
}
