package cn.rh.flash.service.dzuser;

public class FalseDataServiceSql {


    public static String delWithFalse(String fidw) {
       return " DELETE FROM t_dzuser_withdrawals WHERE fidw = '"+fidw+"' ";
    }

    public static String delRecordFalse(String fidw) {
        return " DELETE FROM t_dzgoods_recordpb WHERE fidw = '"+fidw+"' ";
    }

    public static String delTranFalse(String fidw) {
        return " DELETE FROM t_dzuser_transaction WHERE fidw = '"+fidw+"' ";
    }


    public static String delUser(String fidw) {
        return " UPDATE t_dzuser_user SET dzstatus = 3 WHERE fidw = '"+fidw+"' ";
    }
    public static String delVipRebate(String fidw) {
        return " DELETE FROM t_dzvip_viprebaterecord WHERE fidw = '"+fidw+"' ";
    }
    public static String delCdbRebate(String fidw) {
        return " DELETE FROM t_dzgoods_recordpb WHERE fidw = '"+fidw+"' ";
    }


    public static String delRechargeFalse(String fidw) {
        return " DELETE FROM t_dzuser_rechargehistory WHERE fidw = '"+fidw+"' ";
    }


}
