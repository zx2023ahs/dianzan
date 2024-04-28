package cn.rh.flash.bean.vo.api;

import lombok.Data;

@Data
public class WithdrawOrderVo {

    //三方单号
    private String transactionNumber;

    //手续费
    private String fee;

    //提现金额
    private String amount;

    //提现地址
    private String address;

}
