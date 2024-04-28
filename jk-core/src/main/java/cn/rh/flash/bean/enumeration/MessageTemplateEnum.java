package cn.rh.flash.bean.enumeration;

/**
 * descript
 */
public enum MessageTemplateEnum {

    WITHDRAW_AMOUNT_GT_ZERO("WITHDRAW_AMOUNT_GE_ZERO","提现金额大于0"),
    LESS_THAN_THE_WITHDRAWAL_AMOUNT("LESS_THAN_THE_WITHDRAWAL_AMOUNT","小于提现金额范围"),
    GREATER_THAN_THE_WITHDRAWAL_AMOUNT("GREATER_THAN_THE_WITHDRAWAL_AMOUNT","大于提现金额范围"),
//    WITHDRAW_COUNT_ONE("WITHDRAW_COUNT_ONE","每日提款一次"),
    WITHDRAW_COUNT_ONE("WITHDRAW_COUNT_ONE","超过最大提现次数"),
    WALLET_ADDRESS_NULL("WALLET_ADDRESS_NULL","钱包地址不存在"),
    ACCOUNT_PASSWORD_ERROR("ACCOUNT_PASSWORD_ERROR", "帐户密码错误"),
    ACCOUNT_EXISTS("ACCOUNT_EXISTS", "账户已经存在"),
    ACCOUNT_NOT_EXISTS("ACCOUNT_NOT_EXISTS", "账户不存在"),
    INVITATION_CODE_NOT_EXIST("INVITATION_CODE_NOT_EXIST", "邀请码不存在"),
    COUNTRY_CODE_NOT_EXIST("COUNTRY_CODE_NOT_EXIST", "国家码不存在"),
    THE_ACCOUNT_IS_DISABLED("THE_ACCOUNT_IS_DISABLED", "该账号已禁用"),
    WRONG_VALIDATION_CODE("WRONG_VALIDATION_CODE", "验证码错误"),
    WRONG_VALIDATION_IMG_CODE("WRONG_VALIDATION_IMG_CODE", "图形验证码错误"),
    WRONG_OLD_PASSWORD("WRONG_OLD_PASSWORD","原密碼不正確"),
    WRONG_PASSWORD_FORMAT("WRONG_PASSWORD_FORMAT","密碼格式错误"),
    RECHARGE_CREATE_ERROR("RECHARGE_CREATE_ERROR","创建支付订单失败"),
    WITHDRAW_CREATE_ERROR("WITHDRAW_CREATE_ERROR","创建提现订单失败"),
    INSUFFICIENT_BALANCE("INSUFFICIENT_BALANCE","余额不足"),
    INSUFFICIENT_BALANCE_ZERO("INSUFFICIENT_BALANCE_ZERO","余额不足"),
    VIP_INFORMATION_DOES_NOT_EXIST("VIP_INFORMATION_DOES_NOT_EXIST","vip信息不存在;参数异常"),
    VIP_LEVEL_CAN_LESS_THAN_NOW("VIP_LEVEL_CAN_LESS_THAN_NOW","购买VIP不能小于等于当前VIP等级"),
    INVALID_ADDRESS("INVALID_ADDRESS","无效地址"),

    EXISTS_ADDRESS("EXISTS_ADDRESS","地址绑定其他账号"),

    WRONG_PHONE_FORMAT("WRONG_PHONE_FORMAT","手機號格式錯誤"),
    SMS_SEVER_STOP("SMS_SEVER_STOP","短信服务禁用"),

    PARAM_NOT_EXIST("PARAM_NOT_EXIST","参数异常"),

    PAY_PASSWORD_ERROR("PAY_PASSWORD_ERROR","交易密碼不正確"),
    PAY_PASSWORD_ERROR_NOT_SET("PAY_PASSWORD_ERROR_NOT_SET","未设置交易密碼"),

    TASK_INFORMATION_DOES_NOT_EXIST("TASK_INFORMATION_DOES_NOT_EXIST","任务信息不存在;参数异常"),
    TASK_NUMS("TASK_NUMS","可接任务数据以耗尽"),
    SYSTEM_IS_BUSY("SYSTEM_IS_BUSY","系统繁忙,请稍后再试"),
    TASK_No_PERMISSION("TASK_No_PERMISSION","暂无权限,请升级VIP等级"),

    FAILED_TO_UPLOAD_FILE("FAILED_TO_UPLOAD_FILE","上传文件失败"),
    LOAD_TO_NO_FILE("LOAD_TO_NO_FILE","文件不存在"),

    SIGN_CANNOT_EMPTY("SIGN_CANNOT_EMPTY", "签名不能为空(sign)"),
    SIGN_ERROR("SIGN_ERROR", "签名验证失败(-1)"),
    TIMESTAMP_CANNOT_EMPTY("TIMESTAMP_CANNOT_EMPTY", "timestamp 参数不能是空"),
    TIMESTAMP_ABNORMAL_FORMAT("TIMESTAMP_ABNORMAL_FORMAT", "签名参数异常"),
    SIGN_EXPIRED("SIGN_EXPIRED", "签名过期"),

    PARSE_TOKEN_FAIL("PARSE_TOKEN_FAIL", "解析token失败"),
    TOKEN_EXPIRED("TOKEN_EXPIRED", "token 过期"),
    TOKEN_NULL("TOKEN_NULL", "token 为null"),
    TOKEN_REMOTE_LOGIN("TOKEN_REMOTE_LOGIN", "token 异地登录/异机登录"),
    TOKEN_EMPTY( "TOKEN_EMPTY", "token 不能为空"),

    REQUEST_LIMIT( "REQUEST_LIMIT", "访问太过频繁请稍后再试"),

    CANNOT_REPEAT_PURCHASE( "CANNOT_REPEAT_PURCHASE", "无法重复购买"),
    // 充电宝手动领取相关
    NON_OPERATING_HOURS( "NON_OPERATING_HOURS", "非营业时间，请在营业时间内投放"),
    TASK_IN_OPERATION( "TASK_IN_OPERATION", "当前任务正在营业中"),
    EQUIPMENT_RETURNING( "EQUIPMENT_RETURNING", "设备返场中，请0点后点击"),
    INSUFFICIENT_TIME_LEFT( "INSUFFICIENT_TIME_LEFT", "当日剩余时间不足最小收益时间"), // 弃用
    // 转盘抽奖
    COUNT_RUN_OUT("COUNT_RUN_OUT","抱歉,您的抽奖次数已用尽"),
    lUCKEDRAM_NOT_STARTED("lUCKEDRAM_NOT_STARTED","活动未开始"),
    SIGN_REPEAT("SIGN_REPEAT","签到重复"),
    PRIZE_MAX_VIP("PRIZE_MAX_VIP","未达到奖品所需VIP"),
    SCORE_NOT_ENOUGH("SCORE_NOT_ENOUGH","积分不足"),
    SMS_DAY_NUM_LIMIT("SMS_DAY_NUM_LIMIT","超过当前短信限制，请联系客服"),
    CHANNEL_TYPE_NOT_OPEN("CHANNEL_TYPE_NOT_OPEN","通道未开启"),
    PRIZE_QUOTA_IS_FULL("PRIZE_QUOTA_IS_FULL","奖品名额已满"),
    LUCKY_DRAW_IS_END("LUCKY_DRAW_IS_END","夺宝活动已结束"),
    LUCKY_DRAW_PRIZE_IS_NULL("LUCKY_DRAW_PRIZE_IS_NULL","夺宝奖品不存在"),
    LUCKY_DRAW_IS_OPEN("LUCKY_DRAW_IS_OPEN","夺宝活动已开奖"),
    TEAM_SIZE_NOT_SUPPORT("TEAM_SIZE_NOT_SUPPORT","团队规模暂不支持查看"),


    FUND_EXIST("The_fund_pool_does_not_exist","基金池不存在"),
    TRANSACTION_FAILED("Transaction_failed","交易失败"),
    NO_PERMISSION("No_permission","无权限"),
    THE_AMOUT_CANNOT_BE_0("The_amount_cannot_be_0","金额不能为0"),


    ONE_WALLET_ADDRESS ("A_user_can_only_bind_one_wallet_address,please_contact_the_administrator","一个用户只能绑定一个钱包地址,请联系管理员!"),
    REALNAME_EXIST("Account_binding_name_inconsistent","账号绑定姓名不一致"),
    WALLETADDRESS_EXIST("walletAddress_EXIST","钱包地址校验失败"),
    PARAMETER_ERROR("parameter_error","参数错误"),
    OPERATION_FAILED("operation_failed","操作失败"),
    //2024-02-10暂无翻译
    WITHDRAW_WEEKEND_LIMIT("WITHDRAW_WEEKEND_LIMIT","提款时间为:周一至周五"),
;

    private String code;
    private String name;

    MessageTemplateEnum(String code, String name) {
        this.name = name;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "[code=" + code + ", message=" + name +"]";
    }

}
