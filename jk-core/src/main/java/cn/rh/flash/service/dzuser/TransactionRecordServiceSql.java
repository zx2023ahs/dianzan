package cn.rh.flash.service.dzuser;

import cn.rh.flash.bean.entity.dzuser.TransactionRecord;

public class TransactionRecordServiceSql {
    public static String insertTransactionRecord(TransactionRecord transactionRecord, String dateTime) {
        String sql =  String.format(
                " INSERT INTO t_dzuser_transaction SET create_time = '%s',create_by = %s,modify_time = '%s',modify_by = %s,idw = %s,source_invitation_code = '%s'," +
                        "uid = %s,account = '%s',order_number = '%s',transaction_number = '%s',money = %s,previous_balance = %s,after_balance = %s,transaction_type = '%s'," +
                        " addition_and_subtraction = %s,fidw = '%s',remark = '%s'",
                dateTime,-1,dateTime,-1,transactionRecord.getIdw(),transactionRecord.getSourceInvitationCode(),transactionRecord.getUid(),transactionRecord.getAccount(),
                transactionRecord.getOrderNumber(), transactionRecord.getTransactionNumber(),transactionRecord.getMoney(),transactionRecord.getPreviousBalance(),
                transactionRecord.getAfterBalance(),transactionRecord.getTransactionType(),transactionRecord.getAdditionAndSubtraction(),transactionRecord.getFidw(),
                transactionRecord.getRemark()
        );
        return sql;
    }
}
