package cn.rh.flash.service.dzuser;

import cn.rh.flash.bean.entity.dzuser.WithdrawalsRecord;
import cn.rh.flash.bean.vo.query.SqlSpecification;
import cn.rh.flash.utils.factory.Page;

public class WithdrawalsRecordServiceSql {

    // 统计总交易金额 总手续费
    public static String findCountMoney(Page<WithdrawalsRecord> page) {
        String sql =  String.format(
                " SELECT IFNULL(SUM(a.money),0) as countMoney,IFNULL(SUM(a.handling_fee),0) as countHandlingFee " +
                        " FROM t_dzuser_withdrawals as a " +
                        " left join t_dzuser_user as b on b.id = a.uid "
        );
        sql = SqlSpecification.toAddSql(sql,page.getFilters());
//        sql = sql + " and (a.recharge_status = 'suc' or a.recharge_status = 'sysok') " +
//                " and a.uid not in (select id from t_dzuser_user where user_type = 2) ";
        sql = sql + " and a.fidw is null and a.uid not in (select id from t_dzuser_user where user_type = 2) ";
        return sql;
    }

    public static String getAddressById(Long uid) {
        String sql =  String.format(
                " SELECT withdrawal_address as withdrawalAddress from t_dzuser_withdrawals where uid = %s order by id desc ",
                uid
        );
        return sql;
    }

    public static String insertWithdrawalsRecord(WithdrawalsRecord record,String dateTime) {
        String sql =  String.format(
                " INSERT INTO t_dzuser_withdrawals SET create_time = '%s',create_by = %s,modify_time = '%s',modify_by = %s,idw = %s,source_invitation_code = '%s'," +
                        "uid = %s,account = '%s',order_number = '%s',transaction_number = '%s',money = %s,channel_name = '%s',recharge_status = '%s',previous_balance = %s," +
                        "after_balance = %s,remark = '%s',handling_fee = %s,amount_received = %s,withdrawal_address = '%s',operator = '%s',up_withdrawal_address = '%s',fidw = '%s' ",
                    dateTime,record.getCreateBy(),dateTime,record.getModifyBy(),record.getIdw(),record.getSourceInvitationCode(),
                    record.getUid(),record.getAccount(),record.getOrderNumber(),record.getTransactionNumber(),record.getMoney(),record.getChannelName(),
                    record.getRechargeStatus(), record.getPreviousBalance(),record.getAfterBalance(),record.getRemark(),record.getHandlingFee(),
                    record.getAmountReceived(),record.getWithdrawalAddress(), record.getOperator(),record.getUpWithdrawalAddress(),record.getFidw()
        );
        return sql;
    }

    public static String getCount(String startTime, String endTime, String ucode,String testCode) {
        if (!"admin".equals(ucode)){
            return "  SELECT count(1) as count FROM t_dzuser_withdrawals WHERE fidw is null and recharge_status in('suc','sysok')  "+
                    " and modify_time >= '"+startTime+"' and modify_time <= '"+endTime+"'  "+
                    " and source_invitation_code != '"+testCode+"'  "+
                    " and source_invitation_code =  '"+ucode+"'    " ;

        }
        return "   SELECT count(1) as count FROM t_dzuser_withdrawals WHERE fidw is null and recharge_status in('suc','sysok')  "+
                "  and modify_time >= '"+startTime+"' and modify_time <= '"+endTime+"'  "+
                "  and source_invitation_code != '"+testCode+"'  ";
    }

    public static String getSum(String startTime, String endTime, String ucode,String testCode) {
        if (!"admin".equals(ucode)){
            return "  SELECT sum(money) as sum  FROM t_dzuser_withdrawals WHERE fidw is null and recharge_status in('suc','sysok') "+
                    " and modify_time >= '"+startTime+"' and modify_time <= '"+endTime+"'  "+
                    " and source_invitation_code != '"+testCode+"'  "+
                    " and source_invitation_code =  '"+ucode+"'    " ;
        }
        return "  SELECT  sum(money) as sum  FROM t_dzuser_withdrawals WHERE fidw is null and recharge_status in('suc','sysok') "+
                " and modify_time >= '"+startTime+"' and modify_time <= '"+endTime+"'  "+
                "  and source_invitation_code != '"+testCode+"'  ";
    }
}
