package cn.rh.flash.bean.enumeration;

public enum ConfigKeyEnum {
    /**
     * 系统默认上传路径
     */
    SYSTEM_FILE_UPLOAD_PATH("system.file.upload.path"),
    /**
     * 系统名称
     */
    SYSTEM_APP_NAME("system.app.name"),


    /**
     * 支付类型 支付密码/验证器密码 ( dz.pay.type = 1/ dz.pay.type = 2 )
     */
    DZ_PAY_TYPE("dz.pay.type"),

    /**
     * 邀请一个有效vip 加 ？ 分
     */
    DZ_GET_VIP_NUM("dz.get.vip.num"),

    /**
     * 邀请链接
     */
    DZ_IV_LIKE("dz.iv.like"),

    /**
     * execlPATH
     */
    SYS_FILE_PATH("sys.file.path"),

    /**
     *
     * 用户注册赠送彩金
     */

    REG_GIFT_MONEY("reg.gift.money"),

    /**
     * 测试内部账号邀请码
     */
    TEST_USER_CODE("test.user.code"),

    /**
     * 每日提现数量
     */
    WITH_DAY_NUM("with.day.num"),

    /**
     * 普通会员历史提现总次数限制
     */
    WITH_TOTAL_NUM("with.total.num"),

    /**
     * 用户的默认支付密码
     */
    USER_DEF_PAY_PWD("user.def.pay.pwd"),

    /**
     * H5默认语言
     */
    SYSTEM_H5_DEF_LANG("system.h5.def.lang"),

    /**
     * 最迟运营时间
     */
    LAST_MOVE_TIME("last.move.time"),

    /**
     * 手动领取禁止运营的周期
     */
    PROHIBIT_RELEASE_CYCLE("prohibit.release.cycle"),

    /**
     * 回调IP白名单
     */
    DZ_WHITE_IP("dz.white.ip"),

//    // 转盘名称
//    DZ_PRIZE_NAME("dz.prize.name"),

    // 信誉分返佣层级
    CREADIT_SCORE("credit_score"),

    // 升级vip赠送信誉分
    UP_VIP_CREDIT("up.vip.credit"),

    // 升级vip上级 赠送信誉分
    PARENT_VIP_CREDIT("parent.vip.credit"),

    // 注册赠送信誉分自身
    REGISTER_CREDIT("register.credit"),

    //注册赠送信誉分上级
    REGISTER_CREDIT_SUPERIOR("register.credit_superior"),

    // 注册上级赠送积分
    REGISTER_SCORE("register.score"),

    // 站点名称
    SITE_NICKNAME("site.nickname"),

    // 注册限制vip
    REG_LIMIT_VIP("reg.limit.vip"),

    // 未运行设备扣分
    DEDUCT_CREDIT("deduct.credit"),

    // VIP奖品列表展示用
    PRIZE_SHOW_LIST("prize.list"),

    // 充值默认支付通道
    SYSTEM_PAY_CHANNEL("system.pay.channel"),

    // 提现默认支付通道
    SYSTEM_WITH_CHANNEL("system.with.channel"),

    SMS_DAY_NUM("sms.day.num"),

    // 默认通道类型
    SYSTEM_CHANNEL_TYPE("system.channel.type"),

    // 是否开启支付USDT.Polygon通道   1开启/0关闭
    PAYMENT_USDT_POLYGON_ISOPEN("PAYMENT.USDT.Polygon.ISOPEN"),
    // 是否开启支付USDT.TRC20通道   1开启/0关闭
    PAYMENT_USDT_TRC20_ISOPEN("PAYMENT.USDT.TRC20.ISOPEN"),
    // 团队规模层级。0:不让查看团队规模/1：控制本层/2：控制到第二层/3:控制到第三层
    TEAM_SIZE_HIREARCHY("TEAM.SIZE.HIREARCHY"),
    // 是否开启提现USDT.Polygon通道   1开启/0关闭
    WITHDRAWAL_USDT_POLYGON_ISOPEN("WITHDRAWAL.USDT.Polygon.ISOPEN"),
    // 是否开启提现USDT.TRC20通道   1开启/0关闭
    WITHDRAWAL_USDT_TRC20_ISOPEN("WITHDRAWAL.USDT.TRC20.ISOPEN"),
    // 是否开启多人共用同一钱包地址1：开启/0：关闭
    MULTIPLE_PEOPLE_USING_ADDRESS("MULTIPLE.PEOPLE.USING.ADDRESS"),
    //求助次数 (判断几次可以申请求助)
    RESORT_NUMBER("resortNumber"),
    //同一个账号、同一个充值通道，间隔时间--单位秒s
    RECHARGE_LIMIT_TIME("recharge.limit.time"),
    //回调展示域名
    NOTIFY_SERVER_NAME("notify.server.name"),

    //周末提现限制
    WITHDRAW_LIMIT_WEEKEND("withdraw.limit.weekend"),

    //禁止输入中文
    CHINESE_INPUT_PATTERN("chinese.input.pattern"),

    //活动区是否开启
    ACTIVITY_ISOPEN("activity.isopen"),
    //注册远程推送url
    ADDUSERURL("AddUserUrl"),


    //首页造假中奖记录开关
    RANDOWRECORDSTATE("random.record.state"),

    //站点范围
    SITE_NICKRANFGE("site.nickrange"),
    //用户vip升级第二次vip时，邀请人是否赠送大富翁抽奖次数（1：用户升级第一次时赠送，2:用户升级第一次，第二次都赠送）
    NUMBER_OF_INVITER("number.of.inviter"),
    //用户vip升级赠送大富翁抽奖次数（适用于邀请者）1:赠送一次大富翁抽奖次数
    USER_UPGRADE_GIVEAWAYS("user.upgrade.giveaways"),
    APP_DOWNLOAD_URL("app.download.url"),
    REJECT_RECHARGE_TIME("reject.recharge.time"),
    MESSAGE_CODE_TIME("message.code.time"),
    //失效订单删除时间
    INVALID_RECHARGE_DELETE_TIME("invalid.recharge.delete.time"),
    ;


    /*
        @Autowired
        private ConfigCache configCache;

        String withDayNum = configCache.get(ConfigKeyEnum.USER_DEF_PAY_PWD).trim();

     */


    private String value;

    ConfigKeyEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
