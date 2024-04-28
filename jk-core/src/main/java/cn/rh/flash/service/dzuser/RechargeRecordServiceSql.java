package cn.rh.flash.service.dzuser;

import cn.rh.flash.bean.entity.dzuser.RechargeRecord;
import cn.rh.flash.utils.StringUtil;

public class RechargeRecordServiceSql {


    public static String sqlCountMoney(Integer rechargeStatus, String channelName, String account, String orderNumber, String expireTimes, String expireTimee, String sourceInvitationCode, String vipType,String channelType) {
        String s1 = " ";
        if (null != rechargeStatus ){
            s1 = " and recharge_status = '"+rechargeStatus+"' ";
        }
        String s2 = " ";
        if (StringUtil.isNotEmpty(channelName)){
            s2 = " and channel_name = '"+channelName+"' ";
        }
        String s3 = " ";
        if (StringUtil.isNotEmpty(account)){
            s3 = " and account = '"+account+"' ";
        }
        String s4 = " ";
        if (StringUtil.isNotEmpty(orderNumber)){
            s4 = " and order_number = '"+orderNumber+"' ";
        }
        String s5 = " ";
        if (StringUtil.isNotEmpty(sourceInvitationCode)){
            s5 = " and source_invitation_code = ( select ucode from t_sys_user where account = '"+sourceInvitationCode+"' )  ";
        }
        String s6 = " ";
        if (StringUtil.isNotEmpty(vipType)){
            s6 = " and vip_type = '"+vipType+"' ";
        }
        String s8 = " ";
        if (StringUtil.isNotEmpty(channelType)){
            s8 = " and channel_type = '"+channelType+"' ";
        }
        String s7 = " ";
        if (StringUtil.isNotEmpty(expireTimes)&&StringUtil.isNotEmpty(expireTimee)){
            s7 = " and modify_time BETWEEN '"+expireTimes+"' AND '"+expireTimee+"' ";
        }

        return String.format(
                " SELECT IFNULL(SUM(money),0) as countMoney FROM t_dzuser_rechargehistory" +
                        " where fidw IS NULL AND uid in (select id from t_dzuser_user where user_type = 1 "+s6+" ) " +
                        s1+s2+s8+s3+s4+s5+s7
        );
    }

    public static String newSqlCountMoney(Integer rechargeStatus, String channelName, String account,
                                          String orderNumber, String expireTimes, String expireTimee,
                                          String sourceInvitationCode, String vipType,String channelType,
                                          String withdrawalAddress, String firstCharge, String countryCodeNumber) {
        String s1 = " ";
        if (null != rechargeStatus ){
            s1 = " and recharge_status = '"+rechargeStatus+"' ";
        }
        String s2 = " ";
        if (StringUtil.isNotEmpty(channelName)){
            s2 = " and channel_name = '"+channelName+"' ";
        }
        String s3 = " ";
        if (StringUtil.isNotEmpty(account)){
            s3 = " and account = '"+account+"' ";
        }
        String s4 = " ";
        if (StringUtil.isNotEmpty(orderNumber)){
            s4 = " and order_number = '"+orderNumber+"' ";
        }
        String s5 = " ";
        if (StringUtil.isNotEmpty(sourceInvitationCode)){
            s5 = " and source_invitation_code = '"+sourceInvitationCode+"' ";
        }
        String s6 = " ";
        if (StringUtil.isNotEmpty(vipType)){
            s6 = " and vip_type = '"+vipType+"' ";
        }
        String s8 = " ";
        if (StringUtil.isNotEmpty(channelType)){
            s8 = " and channel_type = '"+channelType+"' ";
        }
        String s7 = " ";
        if (StringUtil.isNotEmpty(expireTimes)&&StringUtil.isNotEmpty(expireTimee)){
            s7 = " and modify_time >= '"+expireTimes+"' AND modify_time<'"+expireTimee+"' ";
        }
        String s9 = " ";
        if (StringUtil.isNotEmpty(withdrawalAddress)){
            s9 = " and withdrawal_address = '"+withdrawalAddress+"' ";
        }
        String s10 = " ";
        if (StringUtil.isNotEmpty(firstCharge)){
            s10 = " and first_charge = '"+firstCharge+"' ";
        }
        String s11 = " ";
        if (StringUtil.isNotEmpty(countryCodeNumber)){
            s11 = " and country_code_number = '"+countryCodeNumber+"' ";
        }

        return String.format(
                " SELECT IFNULL(SUM(money),0) as countMoney FROM t_dzuser_rechargehistory" +
                        " where fidw IS NULL AND uid in (select id from t_dzuser_user where user_type = 1 "+s6+s11+" ) " +
                        s1+s2+s8+s3+s4+s5+s7+s9+s10
        );
    }

    public static String insertRechargeRecord(RechargeRecord rechargeRecord, String dateTime) {
        String sql =  String.format(
                " INSERT INTO t_dzuser_rechargehistory SET create_time = '%s',create_by = %s,modify_time = '%s',modify_by = %s,idw = %s,source_invitation_code = '%s'," +
                        "uid = %s,account = '%s',order_number = '%s',money = %s,channel_name = '%s',recharge_status = '%s',previous_balance = %s,after_balance = %s," +
                        "withdrawal_address = '%s',fidw = '%s'",
                dateTime,-1,dateTime,-1,rechargeRecord.getIdw(),rechargeRecord.getSourceInvitationCode(),rechargeRecord.getUid(),rechargeRecord.getAccount(),
                rechargeRecord.getOrderNumber(), rechargeRecord.getMoney(),rechargeRecord.getChannelName(),rechargeRecord.getRechargeStatus(),
                rechargeRecord.getPreviousBalance(),rechargeRecord.getAfterBalance(),rechargeRecord.getWithdrawalAddress(),rechargeRecord.getFidw()
        );
        return sql;
    }

    public static String getCount(String startTime, String endTime, String ucode,String testCode) {
        if (!"admin".equals(ucode)){
            return "  SELECT count(1) as count FROM t_dzuser_rechargehistory WHERE fidw is null and recharge_status='3' "+
                    " and modify_time >= '"+startTime+"' and modify_time <= '"+endTime+"'  "+
                    " and source_invitation_code != '"+testCode+"'  "+
                    " and source_invitation_code =  '"+ucode+"'    " ;

        }
        return "  SELECT count(1) as count FROM t_dzuser_rechargehistory WHERE fidw is null   and recharge_status='3'"  +
                " and modify_time >= '"+startTime+"' and modify_time <= '"+endTime+"'  "+
                " and source_invitation_code != '"+testCode+"'  ";
    }

    public static String getSum(String startTime, String endTime, String ucode,String testCode) {
        if (!"admin".equals(ucode)){
            return "  SELECT sum(money) as sum  FROM t_dzuser_rechargehistory WHERE fidw is null  and recharge_status='3' "+
                    " and modify_time >= '"+startTime+"' and modify_time <= '"+endTime+"'  "+
                    " and source_invitation_code != '"+testCode+"'  "+
                    " and source_invitation_code =  '"+ucode+"'    " ;
        }
        return "  SELECT  sum(money) as sum  FROM t_dzuser_rechargehistory WHERE fidw is null  and recharge_status='3'" +
                " and modify_time >= '"+startTime+"' and modify_time <= '"+endTime+"'  "+
                " and source_invitation_code != '"+testCode+"'  ";
    }
}
