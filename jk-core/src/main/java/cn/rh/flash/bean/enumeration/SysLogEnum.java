package cn.rh.flash.bean.enumeration;

public enum SysLogEnum  {
    UPDATE_USERCREDIT( "修改用户信誉分"),
    DELETE_USERCREDIT( "删除用户信誉分"),
    ADD_USERCREDIT( "新增用户信誉分"),
    UPORDOWNCREDIT( "信誉分上下分"),

    UPDATE_CREDITRECORD( "修改信誉分变动"),
    DELETE_CREDITRECORD( "删除信誉分变动"),
    ADD_CREDITRECORD( "新增信誉分变动"),

    UPDATE_CREDITCONFIG( "修改信誉分配置"),
    DELETE_CREDITCONFIG( "删除信誉分配置"),
    ADD_CREDITCONFIG( "新增信誉分配置"),

    UPDATE_PRIZENUM( "修改抽奖次数"),
    DELETE_PRIZENUM( "删除抽奖次数"),
    ADD_PRIZENUM( "新增抽奖次数"),
    UPORDOWNPOINTS( "抽奖次数上下分"),


    UPDATE_EXPECTWINNINGUSER( "修改预期中奖用户"),
    DELETE_EXPECTWINNINGUSER( "删除预期中奖用户"),
    DELETE_ALL_EXPECTWINNINGUSER( "删除全部未中奖的记录"),
    ADD_EXPECTWINNINGUSER( "新增预期中奖用户"),

    UPDATE_WINNINGRECORD( "修改中奖记录"),
    DELETE_WINNINGRECORD( "删除中奖记录"),
    ADD_WINNINGRECORD( "新增中奖记录"),

    UPDATE_PRIZE( "修改抽奖奖品"),
    DELETE_PRIZE( "删除抽奖奖品"),
    ADD_PRIZE( "新增抽奖奖品"),

    UPDATE_SCORE_PRIZE( "修改兑换奖品"),
    DELETE_SCORE_PRIZE( "删除兑换奖品"),
    ADD_SCORE_PRIZE( "新增兑换奖品"),

    UPDATE_SIGN_SET( "修改签到设置"),
    DELETE_SIGN_SET( "删除签到设置"),
    ADD_SIGN_SET( "新增签到设置"),

    ADD_SCORE( "新增积分"),

    UPDATE_LUCKYDRAW( "修改抽奖活动"),
    DELETE_LUCKYDRAW( "删除抽奖活动"),
    ADD_LUCKYDRAW( "新增抽奖活动"),

//    UPDATE_PRODUCT_VIEW( "修改产品分布"),
//    DELETE_PRODUCT_VIEW( "删除产品分布"),
//    ADD_PRODUCT_VIEW( "新增产品分布"),

    FALSE_DATE("增加造假数据"),

    UPDATE_USER_INFO( "修改用户信息"),
    UPDATE_USER_LIMIT( "修改用户限制"),
    DELETE_USER_INFO( "删除用户信息"),
    ADD_USER_INFO( "新增用户信息"),
    USER_INFO_UPDATE_INFO( "批量冻结解冻"),
    USER_INFO_UPDATE_LIMIT_DRAWING( "批量限制提款"),
    USER_INFO_UPDATE_LIMIT_PROFIT( "批量限制收益"),
    UPDATE_WALLET_ADDR_INFO( "修改钱包地址"),
    DELETE_WALLET_ADDR_INFO( "删除钱包地址"),
    SET_WALLET_ADDR_INFO( "设置钱包地址"),
    STRAIGHT_BUCKLE_UP( "后台直冲"),
    STRAIGHT_BUCKLE_LO( "后台直扣"),



    UPDATE_POWER_BANK_INFO( "修改充电宝"),
    DELETE_POWER_BANK_INFO( "删除充电宝"),
    ADD_POWER_BANK_INFO( "新增充电宝"),

    UPDATE_POWER_BANK_TASK_INFO( "修改充电宝返佣任务"),
    DELETE_POWER_BANK_TASK_INFO( "删除充电宝返佣任务"),
    ADD_POWER_BANK_TASK_INFO( "新增充电宝返佣任务"),

    UPDATE_RECORD_PB_INFO( "修改充电宝返佣记录"),
    DELETE_RECORD_PB_INFO( "删除充电宝返佣记录"),
    ADD_RECORD_PB_INFO( "新增充电宝返佣记录"),

    UPDATE_TOTAL_BONUS_PB_INFO( "修改充电宝返佣总收入"),
    DELETE_TOTAL_BONUS_PB_INFO( "删除充电宝返佣总收入"),
    ADD_TOTAL_BONUS_PB_INFO( "新增充电宝返佣总收入"),

    UPDATE_COUNTRY_CODE_INFO( "修改国家区号"),
    DELETE_COUNTRY_CODE_INFO( "删除国家区号"),
    ADD_COUNTRY_CODE_INFO( "新增国家区号"),

    UPDATE_DZ_BANNER_INFO( "修改轮播图"),
    DELETE_DZ_BANNER_INFO( "删除轮播图"),
    ADD_DZ_BANNER_INFO( "新增轮播图"),

    UPDATE_OFFICIAL_NEWS_INFO( "修改公告信息"),
    DELETE_OFFICIAL_NEWS_INFO( "删除公告信息"),
    ADD_OFFICIAL_NEWS_INFO( "新增公告信息"),

    UPDATE_ONLINE_SERVE_INFO( "修改在线客服"),
    DELETE_ONLINE_SERVE_INFO( "删除在线客服"),
    ADD_ONLINE_SERVE_INFO( "新增在线客服"),

    UPDATE_PAYMENT_CHANNEL_INFO( "修改付款频道"),
    DELETE_PAYMENT_CHANNEL_INFO( "删除付款频道"),
    ADD_PAYMENT_CHANNEL_INFO( "新增付款频道"),

    UPDATE_SMS_MESSAGE_INFO( "修改短信信息"),
    DELETE_SMS_MESSAGE_INFO( "删除短信信息"),
    ADD_SMS_MESSAGE_INFO( "新增短信信息"),

    DELETE_SMS_NUM_RECORD( "删除日发送短信次数记录"),

    UPDATE_SYS_LOG_INFO( "修改系统日志"),
    DELETE_SYS_LOG_INFO( "删除系统日志"),
    ADD_SYS_LOG_INFO( "新增系统日志"),

    UPDATE_USER_IP_PERMISSIONS_INFO( "修改用户IP权限"),
    DELETE_USER_IP_PERMISSIONS_INFO( "删除用户IP权限"),
    ADD_USER_IP_PERMISSIONS_INFO( "新增用户IP权限"),

    UPDATE_COMPENSATION_RECORD_INFO( "修改补分记录"),
    DELETE_COMPENSATION_RECORD_INFO( "删除补分记录"),
    ADD_COMPENSATION_RECORD_INFO( "新增补分记录"),

    UPDATE_RECHARGE_RECORD_INFO( "修改充值记录"),
    DELETE_RECHARGE_RECORD_INFO( "删除充值记录"),
    ADD_RECHARGE_RECORD_INFO( "新增充值记录"),

    UPDATE_TOTAL_BONUS_INCOME_INFO( "修改赠送彩金总收入"),
    DELETE_TOTAL_BONUS_INCOME_INFO( "删除赠送彩金总收入"),
    ADD_TOTAL_BONUS_INCOME_INFO( "新增赠送彩金总收入"),

    UPDATE_TOTAL_RECHARGE_AMOUNT_INFO( "修改充值总金额"),
    DELETE_TOTAL_RECHARGE_AMOUNT_INFO( "删除充值总金额"),
    ADD_TOTAL_RECHARGE_AMOUNT_INFO( "新增充值总金额"),

    UPDATE_TOTAL_WITHDRAWAL_AMOUNT_INFO( "修改提现总金额"),
    DELETE_TOTAL_WITHDRAWAL_AMOUNT_INFO( "删除提现总金额"),
    ADD_TOTAL_WITHDRAWAL_AMOUNT_INFO( "新增提现总金额"),

    UPDATE_TRANSACTION_RECORD_INFO( "修改交易记录"),
    DELETE_TRANSACTION_RECORD_INFO( "删除交易记录"),
    ADD_TRANSACTION_RECORD_INFO( "新增交易记录"),

    UPDATE_USER_BALANCE_INFO( "修改用户余额"),
    DELETE_USER_BALANCE_INFO( "删除用户余额"),
    ADD_USER_BALANCE_INFO( "新增用户余额"),

    UPDATE_WITHDRAWALS_RECORD_INFO( "修改提现记录"),
    DELETE_WITHDRAWALS_RECORD_INFO( "删除提现记录"),
    ADD_WITHDRAWALS_RECORD_INFO( "新增提现记录"),
    UPDATE_STATUS_WITHDRAWALS_RECORD_OK( "提现通过"),
    UPDATE_STATUS_WITHDRAWALS_RECORD_ER( "提现拒绝"),
    UPDATE_STATUS_WITHDRAWALS_RECORD_SYSOK( "手动完成"),

    UPDATE_TEAM_VIP_ACTIVATION_TOTAL_REVENUE_INFO( "修改团队vip开通总返佣"),
    DELETE_TEAM_VIP_ACTIVATION_TOTAL_REVENUE_INFO( "删除团队vip开通总返佣"),
    ADD_TEAM_VIP_ACTIVATION_TOTAL_REVENUE_INFO( "新增团队vip开通总返佣"),

    UPDATE_DZ_VIP_MESSAGE_INFO( "修改Vip信息"),
    DELETE_DZ_VIP_MESSAGE_INFO( "删除Vip信息"),
    ADD_DZ_VIP_MESSAGE_INFO( "新增Vip信息"),

    UPDATE_VIP_PURCHASE_HISTORY_INFO( "修改Vip购买记录"),
    DELETE_VIP_PURCHASE_HISTORY_INFO( "删除Vip购买记录"),
    ADD_VIP_PURCHASE_HISTORY_INFO( "新增Vip购买记录"),

    UPDATE_VIP_REBATE_RECORD_INFO( "修改团队开通vip返佣记录"),
    DELETE_VIP_REBATE_RECORD_INFO( "删除团队开通vip返佣记录"),
    ADD_VIP_REBATE_RECORD_INFO( "新增团队开通vip返佣记录"),

    UPDATE_CFG_INFO( "修改参数"),
    DELETE_CFG_INFO( "删除参数"),
    ADD_CFG_INFO( "新增参数"),

    UPDATE_DEPT_INFO( "修改部门"),
    DELETE_DEPT_INFO( "删除部门"),
    ADD_DEPT_INFO( "新增部门"),

    UPDATE_DICT_INFO( "修改字典"),
    DELETE_DICT_INFO( "删除字典"),
    ADD_DICT_INFO( "新增字典"),

    USER_LOGIN_INFO( "用户登录"),
    USER_REG_INFO( "用户注册"),
    USER_CHANGE_PASSWORD_INFO( "用户修改密码"),
    USER_CHANGE_PAY_PASSWORD_INFO( "用户修改支付密码"),
    USER_SET_UP_PAY_PASSWORD_INFO( "用户设置支付密码"),
    USER_SET_AVATAR_INFO( "用户上传头像"),
    USER_CHANGE_NAME_INFO( "用户修改名称"),
    USER_RESET_NAME_INFO( "用户忘记密码"),
    CREATE_RECHARGE_ORDER( "创建支付订单"),
    CREATE_WITHDRAW_ORDER( "创建提现订单"),
    BUY_VIP( "购买VIP"),

    UPDATE_HUNTING_RECORD("修改夺宝参与记录"),
    DELETE_HUNTING_RECORD( "删除夺宝参与记录"),
    ADD_HUNTING_RECORD( "新增夺宝参与记录"),

    UPDATE_MONOPOLY_RECORD( "修改大富翁操作记录"),
    DELETE_MONOPOLY_RECORD( "删除大富翁操作记录"),
    ADD_MONOPOLY_RECORD( "新增大富翁操作记录"),

    UPDATE_MONOPOLY_USER( "修改大富翁用户信息"),
    DELETE_MONOPOLY_USER( "删除大富翁用户信息"),
    ADD_MONOPOLY_USER( "新增大富翁用户信息"),
    BATCH_ADD_PRIZE( "批量新增抽奖奖品"),
    MONOPOLY_RESET( "大富翁活动重置"),
    EXAMINE_RECHARGE_RECORD_INFO( "审核充值记录"),

    UPDATE_STATUS_WITHDRAWALS_RECORD_REJECT( "提现驳回");


    private String message;

    SysLogEnum( String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
