package cn.rh.flash.service.dzuser;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.rh.flash.bean.dto.FalseDataForm;
import cn.rh.flash.bean.dto.api.WithdrawOrderDto;
import cn.rh.flash.bean.entity.dzsys.PaymentChannel;
import cn.rh.flash.bean.entity.dzuser.*;
import cn.rh.flash.bean.entity.dzvip.DzVipMessage;
import cn.rh.flash.bean.entity.system.User;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.api.WithdrawOrderVo;
import cn.rh.flash.bean.vo.dzuser.WithdrawalsRecordVo;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.CacheDao;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.cache.impl.EhcacheDao;
import cn.rh.flash.cache.impl.MyRedissonLocker;
import cn.rh.flash.dao.dzuser.UserInfoRepository;
import cn.rh.flash.dao.dzuser.WithdrawalsRecordRepository;
import cn.rh.flash.sdk.paymentChannel.BiPay.BiPayUtil;
import cn.rh.flash.sdk.paymentChannel.BiPay.BipayBean.WithdrawNotify;
import cn.rh.flash.sdk.paymentChannel.CBPay.CBPayBean.CBPayWithdrawResp;
import cn.rh.flash.sdk.paymentChannel.FPay.FPayBean.FPayWithdrawResp;
import cn.rh.flash.sdk.paymentChannel.JDPay.JDPayBean.JDPayWithdrawResp;
import cn.rh.flash.sdk.paymentChannel.KDPay.KDPayBean.KDPayWithdrawResp;
import cn.rh.flash.sdk.paymentChannel.MYPay.MYPayBean.MYPayWithdrawNotifyResp;
import cn.rh.flash.sdk.paymentChannel.Mpay.mpayBean.MPayWithdrawResp;
import cn.rh.flash.sdk.paymentChannel.OKPay.OKPayBean.OKWdOrderDto;
import cn.rh.flash.sdk.paymentChannel.QNQBPay.QNQBPayBean.QNQBPayWithdrawNotifyResp;
import cn.rh.flash.sdk.paymentChannel.QNQBPay.QNQBPayBean.QNQBPayWithdrawResp;
import cn.rh.flash.sdk.paymentChannel.zimu808Pay.zimu808Bean.Zimu808WithdrawNotify;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.service.coom.dz.ApiUserCoom;
import cn.rh.flash.service.coom.dz.RecordInformation;
import cn.rh.flash.service.dzsys.PaymentChannelService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.dzvip.DzVipMessageService;
import cn.rh.flash.utils.*;
import cn.rh.flash.utils.factory.Page;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static cn.rh.flash.security.JwtUtil.getUsername;

@Log4j2
@Service
public class WithdrawalsRecordService extends BaseService<WithdrawalsRecord, Long, WithdrawalsRecordRepository> {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private WithdrawalsRecordRepository withdrawalsRecordRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private RecordInformation recordInformation;

    @Autowired
    private ConfigCache configCache;

    @Autowired
    private ApiUserCoom apiUserCoom;

    @Autowired
    private PaymentChannelService paymentChannelService;

    @Autowired
    private DzVipMessageService vipMessageService;

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private MyRedissonLocker myRedissonLocker;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private FalseDataService falseDataService;

    @Autowired
    private UserWalletAddressService userWalletAddressService;

    @Autowired
    private EhcacheDao ehcacheDao;

    @Autowired
    private WithdrawalsRecordService withdrawalsRecordService;


    @Transactional(rollbackFor = Exception.class)
    public Ret createWithdrawOrder(WithdrawOrderDto dto, Long userId) throws Exception {
        String userVipType = apiUserCoom.getVipType();
        //周末 禁止VIP提现，只有v0v1可以
        String withdrawWeekend = configCache.get(ConfigKeyEnum.WITHDRAW_LIMIT_WEEKEND);
        if (StringUtil.isNotEmpty(withdrawWeekend)){
            String time = DateUtil.getTime();
            Date date = DateUtil.parse(time, "yyyy-MM-dd HH:mm:ss");
            String dayWeek = DateUtil.getWeek(date);
            if (withdrawWeekend.contains(dayWeek)&&!"v0,v1".contains(userVipType)){
                logger.info("--------当前星期为:{}--------", dayWeek);
                return Rets.failure(MessageTemplateEnum.WITHDRAW_WEEKEND_LIMIT.getCode(), MessageTemplateEnum.WITHDRAW_WEEKEND_LIMIT);
            }
        }

        UserBalance userBalanceById = apiUserCoom.getUserBalanceById(userId);
        //判断是否有提现地址

        if (StringUtil.isEmpty(userBalanceById.getWalletAddress())) {
            return Rets.failure(MessageTemplateEnum.WALLET_ADDRESS_NULL.getCode(), MessageTemplateEnum.WALLET_ADDRESS_NULL);
        }
        if (userBalanceById.getUserBalance() < dto.getAmount()) {
            return Rets.failure(MessageTemplateEnum.INSUFFICIENT_BALANCE.getCode(), MessageTemplateEnum.INSUFFICIENT_BALANCE);
        }

        String channelType=userBalanceById.getChannelType();
        //若此人无通道类型，则参考配置默认
//        if (StringUtil.isEmpty(userBalanceById.getChannelType())) {
//            userBalanceById.setChannelType(configCache.get(ConfigKeyEnum.SYSTEM_CHANNEL_TYPE).trim());
//        }
        if (StringUtil.isEmpty(channelType)) {
            channelType=configCache.get(ConfigKeyEnum.SYSTEM_CHANNEL_TYPE).trim();
        }
//        switch (userBalanceById.getChannelType()){
        switch (channelType){
            case  "USDT.TRC20":if (configCache.get(ConfigKeyEnum.WITHDRAWAL_USDT_TRC20_ISOPEN).trim().equals("0")){
                return Rets.failure(MessageTemplateEnum.CHANNEL_TYPE_NOT_OPEN.getCode(), MessageTemplateEnum.CHANNEL_TYPE_NOT_OPEN);
            }
                break;
            case   "USDT.Polygon":if (configCache.get(ConfigKeyEnum.WITHDRAWAL_USDT_POLYGON_ISOPEN).trim().equals("0")){
                return Rets.failure(MessageTemplateEnum.CHANNEL_TYPE_NOT_OPEN.getCode(), MessageTemplateEnum.CHANNEL_TYPE_NOT_OPEN);
            }
                break;
            case   "other":
                ////todo--kdpay提现
                return Rets.failure(MessageTemplateEnum.PARAM_NOT_EXIST.getCode(), MessageTemplateEnum.PARAM_NOT_EXIST);

            default:return Rets.failure(MessageTemplateEnum.RECHARGE_CREATE_ERROR.getCode(), MessageTemplateEnum.RECHARGE_CREATE_ERROR);
        }
        //解密
        if (StringUtil.isNotEmpty(dto.getPayPassword())) {
            dto.setPayPassword(CryptUtil.desEncrypt(dto.getPayPassword()));
        }

        // 适配多种支付
        String charge = configCache.get(ConfigKeyEnum.SYSTEM_WITH_CHANNEL).trim();
        if (StringUtil.isNotEmpty(charge)) {
            dto.setChannelName(charge);
        }

        if (dto.getAmount() <= 0) {
            return Rets.failure(MessageTemplateEnum.WITHDRAW_AMOUNT_GT_ZERO.getCode(), MessageTemplateEnum.WITHDRAW_AMOUNT_GT_ZERO);
        }

        if (!CoinAddressUtil.isTronAddress(dto.getAddress()) && !CoinAddressUtil.isTronAddressByPolygon(dto.getAddress())) {
            return Rets.failure(MessageTemplateEnum.INVALID_ADDRESS.getCode(), MessageTemplateEnum.INVALID_ADDRESS);
        }

        UserInfo one = userInfoRepository.getOne(userId);
        if (one != null) {

            // skj  限制 提现
            if (one.getLimitDrawing() != null && one.getLimitDrawing() == 1) {
                return Rets.failure(MessageTemplateEnum.THE_ACCOUNT_IS_DISABLED.getCode(), MessageTemplateEnum.THE_ACCOUNT_IS_DISABLED);
            }

            if (StringUtils.isNotEmpty(one.getPaymentPassword())) {
                if (!one.getPaymentPassword().equals(MD5.md5(dto.getPayPassword(), ""))) {
                    return Rets.failure(MessageTemplateEnum.PAY_PASSWORD_ERROR.getCode(), MessageTemplateEnum.PAY_PASSWORD_ERROR);
                }
            } else {
                return Rets.failure(MessageTemplateEnum.PAY_PASSWORD_ERROR_NOT_SET.getCode(), MessageTemplateEnum.PAY_PASSWORD_ERROR_NOT_SET);
            }

            DzVipMessage vipMessage = vipMessageService.get(SearchFilter.build("vipType", userVipType));
            if (vipMessage == null) {
                return Rets.failure(MessageTemplateEnum.PARAM_NOT_EXIST.getCode(), MessageTemplateEnum.PARAM_NOT_EXIST);
            }

            // jk 2023年1月19日

            if (apiUserCoom.isVipUser()) {  // vip用户
                Integer num = vipMessage.getLimitNum();
                // 进行中或已成功的的订单总数 ;
                long countOk = getOrderNumDay(userId, "in");
                if (countOk >= (num == null ? 0 : num)) {
                    return Rets.failure(MessageTemplateEnum.WITHDRAW_COUNT_ONE.getCode(), MessageTemplateEnum.WITHDRAW_COUNT_ONE);
                }

                // jk 2023年1月28日
                // 拒绝 或 三方退款的订单总数
                long countErr = getOrderNumDay(userId, "out");
                if (countErr >= 3) {  //  每天最多拒绝三次
                    return Rets.failure(MessageTemplateEnum.WITHDRAW_COUNT_ONE.getCode(), MessageTemplateEnum.WITHDRAW_COUNT_ONE);
                }
                // end
            } else {  // 普通用户
                //每日提现数量
                String withDayNum = configCache.get(ConfigKeyEnum.WITH_DAY_NUM).trim();
                Integer num = Integer.valueOf(withDayNum);

                // 进行中或已成功的的订单总数 ;
                long countOk = getOrderNumDay(userId, "in");
                // 先判断  in
                if (countOk >= (num == null ? 0 : num)) {
                    return Rets.failure(MessageTemplateEnum.WITHDRAW_COUNT_ONE.getCode(), MessageTemplateEnum.WITHDRAW_COUNT_ONE);
                }

                // 拒绝 或 三方退款的订单总数
                long countErr = getOrderNumDay(userId, "out");
                // 再判断  out
                if (countErr >= 3) {  //  每天最多拒绝三次
                    return Rets.failure(MessageTemplateEnum.WITHDRAW_COUNT_ONE.getCode(), MessageTemplateEnum.WITHDRAW_COUNT_ONE);
                }
                // 再判断当前用户历史进行中或已成功的订单总数

                //每日提现数量
                String withTotalNum = configCache.get(ConfigKeyEnum.WITH_TOTAL_NUM).trim();
                Integer totalNum = StringUtil.isEmpty(withTotalNum) ? 0 : Integer.valueOf(withTotalNum);

                long totalOk = getOrderNumTotal(userId);
                if (totalOk >= totalNum) {
                    return Rets.failure(MessageTemplateEnum.WITHDRAW_COUNT_ONE.getCode(), MessageTemplateEnum.WITHDRAW_COUNT_ONE);
                }
            }
            // end


            Double withdrawalFee = vipMessage.getWithdrawalFee();

//            BigDecimal userBalance = apiUserCoom.getUserBalance(one.getId());


//            //判断对应通道类型是否开启
//            if (userBalanceById.getChannelType().equals("USDT.TRC20") && configCache.get(ConfigKeyEnum.SYSTEM_USDT_TRC20_ISOPEN).equals("0")) {
//                return Rets.failure(MessageTemplateEnum.CHANNEL_TYPE_NOT_OPEN.getCode(), MessageTemplateEnum.CHANNEL_TYPE_NOT_OPEN);
//            } else if (userBalanceById.getChannelType().equals("USDT.Polygon")&&configCache.get(ConfigKeyEnum.SYSTEM_USDT_POLYGON_ISOPEN).equals("0")) {
//                return Rets.failure(MessageTemplateEnum.CHANNEL_TYPE_NOT_OPEN.getCode(), MessageTemplateEnum.CHANNEL_TYPE_NOT_OPEN);
//            }
            /* jk */
            // 不管等级 提款10u不扣除手续费，超10u才扣除手续费。
            if (dto.getAmount() <= 10.00) {
                withdrawalFee = 0.00;
            }
            // 提现金额加手续费
//            Double add = BigDecimalUtils.add(dto.getAmount(), BigDecimalUtils.divide(withdrawalFee, 100));
//            if (add > userBalance.doubleValue()) {
//                return Rets.failure(MessageTemplateEnum.INSUFFICIENT_BALANCE.getCode(), MessageTemplateEnum.INSUFFICIENT_BALANCE);
//            }

            Double money = BigDecimalUtils.format(dto.getAmount(), 2);

            Double minimumWithdrawal = vipMessage.getMinimumWithdrawal(); // 最小提现值
            Double maximumWithdrawal = vipMessage.getMaximumWithdrawal();
            if (money < minimumWithdrawal) {
                return Rets.failure(MessageTemplateEnum.LESS_THAN_THE_WITHDRAWAL_AMOUNT.getCode(), MessageTemplateEnum.LESS_THAN_THE_WITHDRAWAL_AMOUNT);
            }

            if (money > maximumWithdrawal) {
                return Rets.failure(MessageTemplateEnum.GREATER_THAN_THE_WITHDRAWAL_AMOUNT.getCode(), MessageTemplateEnum.GREATER_THAN_THE_WITHDRAWAL_AMOUNT);
            }


            WithdrawalsRecord withdrawalsRecord = recordInformation.addWithdrawalRecord(
                    money,
                    BigDecimalUtils.multiply(money, BigDecimalUtils.divide(withdrawalFee, 100)),
                    dto.getChannelName(),
                    userBalanceById.getWalletAddress(),
                    one.getId(),
                    one.getSourceInvitationCode(),
                    one.getAccount(),
                    "",
                    "",
//                    userBalanceById.getChannelType()
                    channelType
            );
            sysLogService.addSysLog(one.getAccount(), one.getId(), one.getAccount(), "APP", SysLogEnum.CREATE_WITHDRAW_ORDER);
            // abcdef 测试账号直接通过提现
            String testCode = configCache.get(ConfigKeyEnum.TEST_USER_CODE).trim();
            if (one.getSourceInvitationCode().equals(testCode)) {
                List<WithdrawalsRecord> withdrawalsRecordList = CollUtil.newArrayList();
                withdrawalsRecordList.add(withdrawalsRecord);
                modifyStatus(withdrawalsRecordList, "suc", "", "测试账户直接通过");
            }
            return Rets.success();
        }
        return Rets.failure(MessageTemplateEnum.WITHDRAW_CREATE_ERROR.getCode(), MessageTemplateEnum.WITHDRAW_CREATE_ERROR);
    }

    //增加kdpay提现
    @Transactional(rollbackFor = Exception.class)
    public Ret createWithdrawOrderV2(WithdrawOrderDto dto, Long userId) throws Exception {
        if (dto.getAmount() <= 0) {
            return Rets.failure(MessageTemplateEnum.WITHDRAW_AMOUNT_GT_ZERO.getCode(), MessageTemplateEnum.WITHDRAW_AMOUNT_GT_ZERO);
        }
        //用户对应钱包地址
        UserWalletAddress walletAddress = userWalletAddressService.getByChannelAndUid(dto.getChannelName(), userId);
        //用户钱包余额
        UserBalance userBalanceById = apiUserCoom.getUserBalanceById(userId);
        //校验
        if (StringUtil.isEmpty(walletAddress.getWalletAddress())) {
            return Rets.failure(MessageTemplateEnum.WALLET_ADDRESS_NULL.getCode(), MessageTemplateEnum.WALLET_ADDRESS_NULL);
        }
        if (userBalanceById.getUserBalance() < dto.getAmount()) {
            return Rets.failure(MessageTemplateEnum.INSUFFICIENT_BALANCE.getCode(), MessageTemplateEnum.INSUFFICIENT_BALANCE);
        }
        //判断vip、paymentChannel对应
        String userVipType = apiUserCoom.getVipType();
        DzVipMessage vipMessage = ehcacheDao.hget(CacheDao.VIPMESSAGE, userVipType, DzVipMessage.class);
//        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", dto.getChannelName()));
//        if (paymentChannel==null||paymentChannel.getIsWithdrawal()==0){
//            return Rets.failure(MessageTemplateEnum.CHANNEL_TYPE_NOT_OPEN.getCode(), MessageTemplateEnum.CHANNEL_TYPE_NOT_OPEN);
//        }
//        if (vipMessage==null||!vipMessage.getWithdrawMethods().contains(dto.getChannelName())){
//            return Rets.failure(MessageTemplateEnum.CHANNEL_TYPE_NOT_OPEN.getCode(), MessageTemplateEnum.CHANNEL_TYPE_NOT_OPEN);
//        }

        //周末 禁止VIP提现，只有v0v1可以
        String withdrawWeekend = configCache.get(ConfigKeyEnum.WITHDRAW_LIMIT_WEEKEND);
        if (StringUtil.isNotEmpty(withdrawWeekend)){
            String time = DateUtil.getTime();
            Date date = DateUtil.parse(time, "yyyy-MM-dd HH:mm:ss");
            String dayWeek = DateUtil.getWeek(date);
            if (withdrawWeekend.contains(dayWeek)&&!"v0,v1".contains(userVipType)){
                logger.info("--------当前星期为:{}--------", dayWeek);
                return Rets.failure(MessageTemplateEnum.WITHDRAW_WEEKEND_LIMIT.getCode(), MessageTemplateEnum.WITHDRAW_WEEKEND_LIMIT);
            }
        }


        //解密
        if (StringUtil.isNotEmpty(dto.getPayPassword())) {
            dto.setPayPassword(CryptUtil.desEncrypt(dto.getPayPassword()));
        }
        UserInfo one = userInfoRepository.getOne(userId);
        if (one != null) {
            // skj  限制 提现
            if (one.getLimitDrawing() != null && one.getLimitDrawing() == 1) {
                return Rets.failure(MessageTemplateEnum.THE_ACCOUNT_IS_DISABLED.getCode(), MessageTemplateEnum.THE_ACCOUNT_IS_DISABLED);
            }
            if (StringUtils.isNotEmpty(one.getPaymentPassword())) {
                if (!one.getPaymentPassword().equals(MD5.md5(dto.getPayPassword(), ""))) {
                    return Rets.failure(MessageTemplateEnum.PAY_PASSWORD_ERROR.getCode(), MessageTemplateEnum.PAY_PASSWORD_ERROR);
                }
            } else {
                return Rets.failure(MessageTemplateEnum.PAY_PASSWORD_ERROR_NOT_SET.getCode(), MessageTemplateEnum.PAY_PASSWORD_ERROR_NOT_SET);
            }
            // jk 2023年1月19日

            if (apiUserCoom.isVipUser()) {  // vip用户
                Integer num = vipMessage.getLimitNum();
                // 进行中或已成功的的订单总数 ;
                long countOk = getOrderNumDay(userId, "in");
                if (countOk >= (num == null ? 0 : num)) {
                    return Rets.failure(MessageTemplateEnum.WITHDRAW_COUNT_ONE.getCode(), MessageTemplateEnum.WITHDRAW_COUNT_ONE);
                }

                // jk 2023年1月28日
                // 拒绝 或 三方退款的订单总数
                long countErr = getOrderNumDay(userId, "out");
                if (countErr >= 3) {  //  每天最多拒绝三次
                    return Rets.failure(MessageTemplateEnum.WITHDRAW_COUNT_ONE.getCode(), MessageTemplateEnum.WITHDRAW_COUNT_ONE);
                }
                // end
            } else {  // 普通用户
                //每日提现数量
                String withDayNum = configCache.get(ConfigKeyEnum.WITH_DAY_NUM).trim();
                Integer num = Integer.valueOf(withDayNum);

                // 进行中或已成功的的订单总数 ;
                long countOk = getOrderNumDay(userId, "in");
                // 先判断  in
                if (countOk >= (num == null ? 0 : num)) {
                    return Rets.failure(MessageTemplateEnum.WITHDRAW_COUNT_ONE.getCode(), MessageTemplateEnum.WITHDRAW_COUNT_ONE);
                }

                // 拒绝 或 三方退款的订单总数
                long countErr = getOrderNumDay(userId, "out");
                // 再判断  out
                if (countErr >= 3) {  //  每天最多拒绝三次
                    return Rets.failure(MessageTemplateEnum.WITHDRAW_COUNT_ONE.getCode(), MessageTemplateEnum.WITHDRAW_COUNT_ONE);
                }
                // 再判断当前用户历史进行中或已成功的订单总数

                //每日提现数量
                String withTotalNum = configCache.get(ConfigKeyEnum.WITH_TOTAL_NUM).trim();
                Integer totalNum = StringUtil.isEmpty(withTotalNum) ? 0 : Integer.valueOf(withTotalNum);

                long totalOk = getOrderNumTotal(userId);
                if (totalOk >= totalNum) {
                    return Rets.failure(MessageTemplateEnum.WITHDRAW_COUNT_ONE.getCode(), MessageTemplateEnum.WITHDRAW_COUNT_ONE);
                }
            }
            // end

            Double withdrawalFee = vipMessage.getWithdrawalFee();
            /* jk */
            // 不管等级 提款10u不扣除手续费，超10u才扣除手续费。
            if (dto.getAmount() <= 10.00) {
                withdrawalFee = 0.00;
            }
            Double money = BigDecimalUtils.format(dto.getAmount(), 2);
            Double minimumWithdrawal = vipMessage.getMinimumWithdrawal(); // 最小提现值
            Double maximumWithdrawal = vipMessage.getMaximumWithdrawal();
            if (money < minimumWithdrawal) {
                return Rets.failure(MessageTemplateEnum.LESS_THAN_THE_WITHDRAWAL_AMOUNT.getCode(), MessageTemplateEnum.LESS_THAN_THE_WITHDRAWAL_AMOUNT);
            }

            if (money > maximumWithdrawal) {
                return Rets.failure(MessageTemplateEnum.GREATER_THAN_THE_WITHDRAWAL_AMOUNT.getCode(), MessageTemplateEnum.GREATER_THAN_THE_WITHDRAWAL_AMOUNT);
            }


            WithdrawalsRecord withdrawalsRecord = recordInformation.addWithdrawalRecord(
                    money,
                    BigDecimalUtils.multiply(money, BigDecimalUtils.divide(withdrawalFee, 100)),
                    dto.getChannelName(),
                    walletAddress.getWalletAddress(),//KDPay的钱包地址绑定在walletAddress表里
                    one.getId(),
                    one.getSourceInvitationCode(),
                    one.getAccount(),
                    walletAddress.getWalletName(),//用户银行卡姓名
                    "",
                    walletAddress.getChannelType()
            );
            sysLogService.addSysLog(one.getAccount(), one.getId(), one.getAccount(), "APP", SysLogEnum.CREATE_WITHDRAW_ORDER);
            // abcdef 测试账号直接通过提现
            String testCode = configCache.get(ConfigKeyEnum.TEST_USER_CODE).trim();
            if (one.getSourceInvitationCode().equals(testCode)) {
                List<WithdrawalsRecord> withdrawalsRecordList = CollUtil.newArrayList();
                withdrawalsRecordList.add(withdrawalsRecord);
                modifyStatus(withdrawalsRecordList, "suc", "", "测试账户直接通过");
            }
            return Rets.success();
        }
        return Rets.failure(MessageTemplateEnum.WITHDRAW_CREATE_ERROR.getCode(), MessageTemplateEnum.WITHDRAW_CREATE_ERROR);
    }

    // 历史提现进行中或已成功的的订单总数
    private long getOrderNumTotal(Long userId) {
        // 查询当前用户提现订单
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("uid", userId));
        filters.add(SearchFilter.build("rechargeStatus", SearchFilter.Operator.IN,
                new String[]{"no", "ok", "suc", "sysok"}));
        return this.count(filters);
    }

    /**
     * 当日提现订单数
     *
     * @param userId
     * @param flg    ( in= 进行中或已成功的的订单总数 ; out= 拒绝 或 三方退款的订单总数, 其他则表示 当日所有的订单数 )
     * @return
     */
    private long getOrderNumDay(Long userId, String flg) {
        // 查询当前用户提现订单
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("uid", userId));
        filters.add(SearchFilter.build("createTime", SearchFilter.Operator.GTE, DateUtil.parseTime(DateUtil.getDay() + " 00:00:00")));
        filters.add(SearchFilter.build("createTime", SearchFilter.Operator.LTE, DateUtil.parseTime(DateUtil.getDay() + " 23:59:59")));
        // rechargeStatus;
        // 审核状态 ok:已审核,no:审核中,er:已拒绝,suc:已成功,exit:已退款,sysok:订单通过不出款    回调判断在使用请勿随意修改
        switch (flg) {
            case "in":
                filters.add(SearchFilter.build("rechargeStatus", SearchFilter.Operator.IN,
                        new String[]{"no", "ok", "suc", "sysok"}));
                break;
            case "out":
                filters.add(SearchFilter.build("rechargeStatus", SearchFilter.Operator.IN,
                        new String[]{"er", "exit"}));
                break;
        }
        return this.count(filters);
    }

    /**
     * bipay提现回调
     * @param withdrawNotify
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String notifyWithdrawOrder(WithdrawNotify withdrawNotify) {
        String reqIp = HttpUtil.getIp();

        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();

        if (!whiteIps.contains(reqIp)) {
            return "IP_NOT_WHITE_LIST";
        }
        WithdrawalsRecord withdrawalsRecord = withdrawalsRecordRepository.findByOrderNumber(withdrawNotify.getCustomOrderId());

        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", withdrawalsRecord.getChannelName()));
        if (paymentChannel == null) {
            log.error("通道信息不存在,[{}]", withdrawalsRecord.getChannelName());
            return "FAIL";
        }
        //  审核状态 ok:已审核,no:审核中,er:已拒绝,suc:已成功,exit:已退款,sysok:订单通过不出款
        if (withdrawalsRecord != null && "ok".equals(withdrawalsRecord.getRechargeStatus())) {
            //验签成功
            if (BiPayUtil.withdrawOrderNotify(withdrawNotify, paymentChannel.getPrivateKey()) != null) {
                if (withSuccess(withdrawNotify.getStatus() + 1, withdrawalsRecord)) return "OK";
            }
        }
        return "FAIL";
    }

    /**
     * walletpay提现回调
     * @param withdrawNotify
     * @return
     */
    public String walletNotifyWithdrawOrder(cn.rh.flash.sdk.paymentChannel.WalletPay.WalletPayBean.WithdrawNotify withdrawNotify) {
        String reqIp = HttpUtil.getIp();

        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();

        if (!whiteIps.contains(reqIp)) {
            return "IP_NOT_WHITE_LIST";
        }
        WithdrawalsRecord withdrawalsRecord = withdrawalsRecordRepository.findByOrderNumber(withdrawNotify.getUser_withdrawal_id());

        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", withdrawalsRecord.getChannelName()));
        if (paymentChannel == null) {
            log.error("通道信息不存在,[{}]", withdrawalsRecord.getChannelName());
            return "FAIL";
        }
        //  审核状态 ok:已审核,no:审核中,er:已拒绝,suc:已成功,exit:已退款,sysok:订单通过不出款
        if (withdrawalsRecord != null && "ok".equals(withdrawalsRecord.getRechargeStatus())) {
            if (withSuccess(Integer.valueOf(withdrawNotify.getWithdrawal_status()), withdrawalsRecord)) return "ok";
        }
        return "FAIL";
    }

    /**
     * kdpay提现回调
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String kdpayNotifyWithdrawOrder(KDPayWithdrawResp withdrawResp,PaymentChannel paymentChannel) {
        String reqIp = HttpUtil.getIp();
        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();
        if (!whiteIps.contains(reqIp)) {
            log.error("对方ip不在白名单："+reqIp);
            return "IP_NOT_WHITE_LIST";
        }

        WithdrawalsRecord withdrawalsRecord = withdrawalsRecordRepository.findByOrderNumber(withdrawResp.getCustomerOrderCode());
        //  审核状态 ok:已审核,no:审核中,er:已拒绝,suc:已成功,exit:已退款,sysok:订单通过不出款
        if (withdrawalsRecord != null && "ok".equals(withdrawalsRecord.getRechargeStatus())) {
            log.info("kdpay提现状态："+withdrawResp.getStatus());
            //下发状态 1 初始 2 成功 3 失败
            if ("2".equals(withdrawResp.getStatus())){
                if (withSuccess(3, withdrawalsRecord)) return "success";
            }
            log.error("kdpay提现回调错误：提现记录-->" + withdrawResp.toString());
            return "STATUS_FAIL_" + withdrawResp.getStatus();
        }
        return "FAIL";
    }


    /**
     * qnqbpay提现回调
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String qnqbpayNotifyWithdrawOrder(QNQBPayWithdrawNotifyResp withdrawResp, PaymentChannel paymentChannel) {
        String reqIp = HttpUtil.getIp();
        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();
        if (!whiteIps.contains(reqIp)) {
            log.error("对方ip不在白名单："+reqIp);
            return "IP_NOT_WHITE_LIST";
        }

        WithdrawalsRecord withdrawalsRecord = withdrawalsRecordRepository.findByOrderNumber(withdrawResp.getOrderid());
        //  审核状态 ok:已审核,no:审核中,er:已拒绝,suc:已成功,exit:已退款,sysok:订单通过不出款
        if (withdrawalsRecord != null && "ok".equals(withdrawalsRecord.getRechargeStatus())) {
            log.info("qnqbpay提现状态："+withdrawResp.getStatus());
            //下发状态 1 初始 2 成功 3 失败
            if ("1".equals(withdrawResp.getStatus())){
                if (withSuccess(3, withdrawalsRecord)) return "success";
            }
            log.error("qnqbpay提现回调错误：提现记录-->" + withdrawResp);
            return "STATUS_FAIL_" + withdrawResp.getStatus();
        }
        return "FAIL";
    }


    /**
     * fpay提现回调
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String fpayNotifyWithdrawOrder(FPayWithdrawResp withdrawResp, PaymentChannel paymentChannel) {
        String reqIp = HttpUtil.getIp();
        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();
        if (!whiteIps.contains(reqIp)) {
            log.error("对方ip不在白名单："+reqIp);
            return "IP_NOT_WHITE_LIST";
        }

        WithdrawalsRecord withdrawalsRecord = withdrawalsRecordRepository.findByOrderNumber(withdrawResp.getOrderid());
        //  审核状态 ok:已审核,no:审核中,er:已拒绝,suc:已成功,exit:已退款,sysok:订单通过不出款
        if (withdrawalsRecord != null && "ok".equals(withdrawalsRecord.getRechargeStatus())) {
            log.info("fpay提现状态："+withdrawResp.getState());
            //下发状态 1 初始 2 成功 3 失败
            if ("2".equals(withdrawResp.getState())||"3".equals(withdrawResp.getState())){
                if (withSuccess(3, withdrawalsRecord)) return "success";
            }
            log.error("fpay提现回调错误：提现记录-->" + withdrawResp);
            return "STATUS_FAIL_" + withdrawResp.getState();
        }
        return "FAIL";
    }

    /**
     * jdpay提现回调
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String jdpayNotifyWithdrawOrder(JDPayWithdrawResp withdrawResp, PaymentChannel paymentChannel) {
        String reqIp = HttpUtil.getIp();
        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();
        if (!whiteIps.contains(reqIp)) {
            return "IP_NOT_WHITE_LIST";
        }

        WithdrawalsRecord withdrawalsRecord = withdrawalsRecordRepository.findByOrderNumber(withdrawResp.getCustomerOrderCode());
        //  审核状态 ok:已审核,no:审核中,er:已拒绝,suc:已成功,exit:已退款,sysok:订单通过不出款
        if (withdrawalsRecord != null && "ok".equals(withdrawalsRecord.getRechargeStatus())) {
            log.info("jdpay提现状态："+withdrawResp.getStatus());
            //下发状态 1 初始 2 成功 3 失败
            if ("2".equals(withdrawResp.getStatus())){
                if (withSuccess(3, withdrawalsRecord)) return "success";
            }
            log.error("jdpay提现回调错误：提现记录-->" + withdrawResp.toString());
            return "STATUS_FAIL_" + withdrawResp.getStatus();
        }
        return "FAIL";
    }

    /**
     * cbpay提现回调
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String cbpayNotifyWithdrawOrder(CBPayWithdrawResp withdrawResp, PaymentChannel paymentChannel) {
        String reqIp = HttpUtil.getIp();
        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();
        if (!whiteIps.contains(reqIp)) {
            return "IP_NOT_WHITE_LIST";
        }

        WithdrawalsRecord withdrawalsRecord = withdrawalsRecordRepository.findByOrderNumber(withdrawResp.getCustomerOrderCode());
        //  审核状态 ok:已审核,no:审核中,er:已拒绝,suc:已成功,exit:已退款,sysok:订单通过不出款
        if (withdrawalsRecord != null && "ok".equals(withdrawalsRecord.getRechargeStatus())) {
            log.info("CBpay提现状态："+withdrawResp.getStatus());
            //下发状态 1 初始 2 成功 3 失败
            if ("2".equals(withdrawResp.getStatus())){
                if (withSuccess(3, withdrawalsRecord)) return "success";
            }
            log.error("CBpay提现回调错误：提现记录-->" + withdrawResp.toString());
            return "STATUS_FAIL_" + withdrawResp.getStatus();
        }
        return "FAIL";
    }


    /**
     * mpay提现回调
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String mpayNotifyWithdrawOrder(MPayWithdrawResp withdrawResp, PaymentChannel paymentChannel) {
        String reqIp = HttpUtil.getIp();
        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();
        if (!whiteIps.contains(reqIp)) {
            return "IP_NOT_WHITE_LIST";
        }

        WithdrawalsRecord withdrawalsRecord = withdrawalsRecordRepository.findByOrderNumber(withdrawResp.getMerchOrderId());
        //  审核状态 ok:已审核,no:审核中,er:已拒绝,suc:已成功,exit:已退款,sysok:订单通过不出款
        if (withdrawalsRecord != null && "ok".equals(withdrawalsRecord.getRechargeStatus())) {
            log.info("Mpay提现状态："+withdrawResp.getStatus());
            //下发单状态，“finish”成功，“pending”进行中，“fail”失败
            if ("finish".equals(withdrawResp.getStatus())){
                if (withSuccess(3, withdrawalsRecord)) return "success";
            }
            log.error("Mpay提现回调错误：提现记录-->" + withdrawResp);
            return "STATUS_FAIL_" + withdrawResp.getStatus();
        }
        return "FAIL";
    }

    /**
     * mypay提现回调
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String mypayNotifyWithdrawOrder(MYPayWithdrawNotifyResp withdrawResp, PaymentChannel paymentChannel) {
        String reqIp = HttpUtil.getIp();
        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();
        if (!whiteIps.contains(reqIp)) {
            log.error("对方ip不在白名单："+reqIp);
            return "IP_NOT_WHITE_LIST";
        }

        WithdrawalsRecord withdrawalsRecord = withdrawalsRecordRepository.findByOrderNumber(withdrawResp.getMerchantOrderNo());
        //  审核状态 ok:已审核,no:审核中,er:已拒绝,suc:已成功,exit:已退款,sysok:订单通过不出款
        if (withdrawalsRecord != null && "ok".equals(withdrawalsRecord.getRechargeStatus())) {
            log.info("mypay提现状态："+withdrawResp.getStatus());
            //0：处理中 1：成功 2：失败
            if ("1".equals(withdrawResp.getStatus())){
                if (withSuccess(3, withdrawalsRecord)) return "success";
            }
            log.error("mypay提现回调错误：提现记录-->" + withdrawResp.toString());
            return "STATUS_FAIL_" + withdrawResp.getStatus();
        }
        return "FAIL";
    }


    /**
     * okpay提现回调
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String okpayNotifyWithdrawOrder(OKWdOrderDto withdrawResp, PaymentChannel paymentChannel) {
        String reqIp = HttpUtil.getIp();
        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();
        System.out.println("对方ip："+reqIp);
        if (!whiteIps.contains(reqIp)) {
            System.out.println("对方ip不在白名单："+reqIp);
            log.error("对方ip不在白名单："+reqIp);
            return "IP_NOT_WHITE_LIST";
        }

        WithdrawalsRecord withdrawalsRecord = withdrawalsRecordService.get(SearchFilter.build("orderNumber",withdrawResp.getOrderid()));
//        WithdrawalsRecord withdrawalsRecord = withdrawalsRecordRepository.findByOrderNumber(withdrawResp.getOrderid());
        System.out.println("withdrawalsRecord================"+withdrawalsRecord);
        //  审核状态 ok:已审核,no:审核中,er:已拒绝,suc:已成功,exit:已退款,sysok:订单通过不出款
        if (withdrawalsRecord != null && "ok".equals(withdrawalsRecord.getRechargeStatus())) {
            log.info("okpay提现状态："+withdrawResp.getState());
            //4下发成功
            if ("4".equals(withdrawResp.getState())){
                if (withSuccess(3, withdrawalsRecord)) return "success";
            }
            log.error("okpay提现回调错误：提现记录-->" + withdrawResp);
            return "STATUS_FAIL_" + withdrawResp.getState();
        }
        return "FAIL";
    }

    /**
     * 808pay提现回调
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String zimu808payNotifyWithdrawOrder(Zimu808WithdrawNotify withdrawResp) {
        String reqIp = HttpUtil.getIp();
        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();
        if (!whiteIps.contains(reqIp)) {
            log.error("808pay IP_NOT_WHITE_LIST");
            return "IP_NOT_WHITE_LIST";
        }

        WithdrawalsRecord withdrawalsRecord = withdrawalsRecordRepository.findByOrderNumber(withdrawResp.getMchOrderNo());
        //  审核状态 ok:已审核,no:审核中,er:已拒绝,suc:已成功,exit:已退款,sysok:订单通过不出款
        if (withdrawalsRecord != null && "ok".equals(withdrawalsRecord.getRechargeStatus())) {
            log.info("808pay提现状态："+withdrawResp.getStatus());
            //2：成功
            if ("2".equals(withdrawResp.getStatus())){
                if (withSuccess(3, withdrawalsRecord)) return "success";
            }
            log.error("808pay提现回调错误：提现记录-->" + withdrawResp.toString());
            return "STATUS_FAIL_" + withdrawResp.getStatus();
        }
        return "FAIL";
    }

    // 提现回调业务处理
    @Transactional(rollbackFor = Exception.class)
    private boolean withSuccess(Integer status, WithdrawalsRecord withdrawalsRecord) {
        // 0 审核中 1 处理中 2 成功 3 已驳回 4 已取消 5 失败 biPay
        // 1 审核中 2 处理中 3 成功 4 已驳回 5 已取消 6 失败 walletPay
        // 为了统一验证码 直接给biPay 状态 +1
        if (3 == status) {
            //  审核状态 ok:已审核,no:未审核,er:已拒绝 suc:已成功 exit:已退款
            withdrawalsRecord.setRechargeStatus("suc");
//                    withdrawalsRecord.setHandlingFee(Double.parseDouble(withdrawNotify.getFee()));
//                    withdrawalsRecord.setAmountReceived(Double.parseDouble(withdrawNotify.getAmount()) - Double.parseDouble(withdrawNotify.getFee()));
            withdrawalsRecordRepository.save(withdrawalsRecord);
            return true;
        }
        if (4 == status || 5 == status || 6 == status) {
            //  审核状态 ok:已审核,no:未审核,er:已拒绝 suc:已成功 exit:已退款
            // 查询用户余额
            Double balance = apiUserCoom.getUserBalance(withdrawalsRecord.getUid()).doubleValue();

            withdrawalsRecord.setRechargeStatus("exit");
            withdrawalsRecordRepository.save(withdrawalsRecord);
            //退款
            Double add = BigDecimalUtils.add(balance, withdrawalsRecord.getMoney());
            recordInformation.transactionRecordPlus(withdrawalsRecord.getSourceInvitationCode(), withdrawalsRecord.getUid(), withdrawalsRecord.getAccount(),
                    balance, withdrawalsRecord.getMoney(), add,
                    withdrawalsRecord.getIdw(), 2, "tx", "", "");
            return true;
        }
        return false;
    }

    public Ret findCountMoney(Page<WithdrawalsRecord> page) {
        Map map = withdrawalsRecordRepository.getMapBySql(WithdrawalsRecordServiceSql.findCountMoney(page));
        return Rets.success(map);
    }

    // 查询用户上次提现地址
    public String findAddressById(Long uid) {
        String sql = WithdrawalsRecordServiceSql.getAddressById(uid);
        List<WithdrawalsRecord> withdrawalsRecords = (List<WithdrawalsRecord>) withdrawalsRecordRepository.queryObjBySql(sql, WithdrawalsRecord.class);
        String withdrawalAddress = "";
        if (withdrawalsRecords.size() > 0) {
            withdrawalAddress = withdrawalsRecords.get(0).getWithdrawalAddress();
        }
        return withdrawalAddress;
    }

    public void modifyStatus(List<WithdrawalsRecord> withdrawalsRecordList, String okorer, String reason, String username) throws Exception {
        if (withdrawalsRecordList.size() == 0) {
            throw new ApplicationException(BizExceptionEnum.MODIFY_INFORMATION_EMPTY);
        }
        int i = 0;
        for (WithdrawalsRecord withdrawalsRecord : withdrawalsRecordList) {

            WithdrawalsRecord record = this.get(withdrawalsRecord.getId());
            if (!StringUtil.equals("no", record.getRechargeStatus())) {
                continue;
            }
            withdrawalsRecord.setOperator(username);

            i++;
            withdrawalsRecord.setRechargeStatus(okorer);
            // 系统日志记录
            SysLogEnum operation = SysLogEnum.UPDATE_STATUS_WITHDRAWALS_RECORD_OK;
            if ("ok".equals(okorer)) {

                // 2023年1月30日  jk 冻结账号不能审核通过
                String account = withdrawalsRecord.getAccount();
                //UserInfo userInfo = userInfoService.get(SearchFilter.build("id", uid));
                UserInfo userInfo = userInfoService.get(SearchFilter.build("account", account));
                if (userInfo != null) {
                    //   1启用    2停用   3 已删除
                    if (userInfo.getDzstatus() != null && userInfo.getDzstatus() != 1) {
                        BizExceptionEnum accountFreezed = BizExceptionEnum.ACCOUNT_FREEZED;
                        accountFreezed.setMessage(accountFreezed.getMessage() + "[账号:" + userInfo.getAccount() + "]");
                        throw new ApplicationException(accountFreezed);
                    }
                } else {
                    throw new ApplicationException(BizExceptionEnum.USER_NOT_EXISTED);
                }
                // end


                Double subtract = BigDecimalUtils.subtract(withdrawalsRecord.getMoney(), withdrawalsRecord.getHandlingFee());
                WithdrawOrderVo vo = recordInformation.createWithdrawOrderThird(withdrawalsRecord.getUid().toString(), subtract.toString(), withdrawalsRecord.getWithdrawalAddress(), withdrawalsRecord.getOrderNumber(), withdrawalsRecord.getChannelName(), withdrawalsRecord.getChannelType());
                //以下操作一般情况下会放置在管理端
                if (vo != null) {
                    if (StringUtil.isNotEmpty(vo.getTransactionNumber())){
                        withdrawalsRecord.setTransactionNumber(vo.getTransactionNumber());
                    }
                    //todo 此处为订单状态 而不是审核状态
                    //审核状态 ok:已审核,no:未审核,er:已拒绝 suc:已成功 exit:已退款
//                    withdrawalsRecord.setHandlingFee(Double.parseDouble(vo.getFee()));
//                    withdrawalsRecord.setAmountReceived(Double.parseDouble(vo.getAmount()) - Double.parseDouble(vo.getFee()));
                    this.update(withdrawalsRecord);
                } else {
                    throw new ApplicationException(BizExceptionEnum.PAY_SERVER_ERROR);
                }
            } else if ("sysok".equals(okorer)) {
                //标记完成不去除单号、姓名
//                withdrawalsRecord.setTransactionNumber("");
                //todo 此处为订单状态 而不是审核状态
                //审核状态 ok:已审核,no:未审核,er:已拒绝 suc:已成功 exit:已退款  sysok：订单通过不出款
                this.update(withdrawalsRecord);
                operation = SysLogEnum.UPDATE_STATUS_WITHDRAWALS_RECORD_SYSOK;
            } else if ("er".equals(okorer)) {
//                if (StringUtil.isEmpty(reason)) {
//                    throw new ApplicationException(BizExceptionEnum.REFUSE_REASON_EMPTY);
//                }
                withdrawalsRecord.setRemark(reason);
                //拒绝提现退款
                recordInformation.refuseWithdrawalRecord(withdrawalsRecord);
                this.update(withdrawalsRecord);
                operation = SysLogEnum.UPDATE_STATUS_WITHDRAWALS_RECORD_ER;
            }
            sysLogService.addSysLog(username, withdrawalsRecord.getId(), withdrawalsRecord.getAccount(), "PC", operation);
        }
        if (i == 0) {
            throw new ApplicationException(BizExceptionEnum.MODIFY_INFORMATION_EMPTY);
        }

    }


    public void updateAuditReject(List<WithdrawalsRecord> withdrawalsRecordList,String username) throws Exception {
        if (CollUtil.isEmpty(withdrawalsRecordList)) {
            throw new ApplicationException(BizExceptionEnum.MODIFY_INFORMATION_EMPTY);
        }
        for (WithdrawalsRecord withdrawalsRecord : withdrawalsRecordList) {
            WithdrawalsRecord record = this.get(withdrawalsRecord.getId());
            if (ObjectUtil.isEmpty(record)) {
                throw new ApplicationException(BizExceptionEnum.WITHDRAWL_INCORRECT);
            }
            if (!StringUtil.equals("ok", record.getRechargeStatus())) {
                throw new ApplicationException(BizExceptionEnum.WITHDRAWL_STATUS_INCORRECT);
            }

            //  冻结账号不能审核通过
            UserInfo userInfo = userInfoService.get(SearchFilter.build("account", record.getAccount()));
            if (ObjectUtil.isEmpty(userInfo)) {
                throw new ApplicationException(BizExceptionEnum.USER_NOT_EXISTED);
            }
            if (userInfo.getDzstatus() != 1) {
                BizExceptionEnum accountFreezed = BizExceptionEnum.ACCOUNT_FREEZED;
                accountFreezed.setMessage(accountFreezed.getMessage() + "[账号:" + userInfo.getAccount() + "]");
                throw new ApplicationException(accountFreezed);
            }
            //金额返回用户
            recordInformation.refuseWithdrawalRecord(record);
            //更改状态
            record.setOperator(username);
            record.setRechargeStatus("exit");
            this.update(record);
            sysLogService.addSysLog(username, withdrawalsRecord.getId(), withdrawalsRecord.getAccount(), "PC", SysLogEnum.UPDATE_STATUS_WITHDRAWALS_RECORD_REJECT);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public Ret addWithFalse(FalseDataForm falseDataForm, String userName) {
        String[] falseDate = falseDataForm.getFalseDate().replace("\n", "").split(",");

        FalseData falseData = new FalseData(falseDataForm);
        String textCode = configCache.get(ConfigKeyEnum.TEST_USER_CODE);
        int count = falseDate.length;
        for (String date : falseDate) {
            String[] split = date.split("---");
            if (split.length != 3) {
                // 数据格式不对
                count--;
                continue;
            }
            String account = split[0];
            String money = split[1];
            String dateTime = split[2];
            // 查询当前用户是否为测试用户
            UserInfo userInfo = userInfoService.get(SearchFilter.build("account", account));
            if (userInfo == null) {
                count--;
                continue;
            }
            if (!userInfo.getSourceInvitationCode().equals(textCode)) {
                // 不为测试账户 直接跳过 进行下一次循环
                count--;
                continue;
            }
            WithdrawalsRecord record = new WithdrawalsRecord();
            record.setCreateBy(-1L);
            record.setModifyBy(-1L);
            record.setIdw(new IdWorker().nextId() + "");
            record.setSourceInvitationCode(userInfo.getSourceInvitationCode());
            record.setUid(userInfo.getId());
            record.setAccount(account);
            record.setOrderNumber(MakeOrderNum.makeOrderNum("txz", DateUtil.parseTime(dateTime)));
            record.setTransactionNumber("");  // 没有传空
            record.setChannelName("BiPay");
            record.setRechargeStatus("suc");   //  审核状态 ok:已审核,no:审核中,er:已拒绝,suc:已成功,exit:已退款,sysok:订单通过不出款
            record.setMoney(Double.valueOf(money));
            record.setRemark("");  // // 没有传空
            record.setHandlingFee(0.00);
            record.setAmountReceived(Double.valueOf(money));
            record.setWithdrawalAddress("");
            record.setOperator(userName);
            record.setFidw(falseData.getIdw());
//            this.insert(record);
            withdrawalsRecordRepository.execute(WithdrawalsRecordServiceSql.insertWithdrawalsRecord(record, dateTime));
        }
        falseData.setRemark("添加提现造假数据:成功" + count + "条,失败" + (falseDate.length - count) + "条");
        if (count > 0) {
            falseDataService.insert(falseData);
        }
        sysLogService.addSysLog(userName, null, "PC", SysLogEnum.FALSE_DATE,
                getUsername() + "--在" + DateUtil.getTime() + "--增加提现造假数据:" + falseDate.length + "条");

        return Rets.success("添加提现造假数据:成功" + count + "条,失败" + (falseDate.length - count) + "条");
    }

    public void exportV2(HttpServletResponse response, List<Map<String, Object>> list) {
        List<WithdrawalsRecordVo> voList = new ArrayList<>();
        for (Map<String, Object> stringObjectMap : list) {
            WithdrawalsRecordVo vo = new WithdrawalsRecordVo();
            BeanUtil.mapToBean(stringObjectMap, vo);
            User user = BeanUtil.objToBean(stringObjectMap.get("user"), User.class);
            if (ObjectUtil.isNotEmpty(stringObjectMap.get("user")) && ObjectUtil.isNotEmpty(user)) {
                vo.setUserAccount(user.getAccount());
            }
            if (ObjectUtil.isNotEmpty(stringObjectMap.get("vipType_str"))) {
                vo.setVipTypeName(stringObjectMap.get("vipType_str").toString());
            }
            if (ObjectUtil.isNotEmpty(stringObjectMap.get("channelName_str"))) {
                vo.setChannelName(stringObjectMap.get("channelName_str").toString());
            }
            if (ObjectUtil.isNotEmpty(stringObjectMap.get("rechargeStatus_str"))) {
                vo.setRechargeStatusName(stringObjectMap.get("rechargeStatus_str").toString());
            }
            voList.add(vo);
        }
        EasyExcelUtil.export(response, "提现记录", voList, WithdrawalsRecordVo.class);
    }


    public int getCount(String startTime, String endTime, String ucode,String testCode) {
        String sql = WithdrawalsRecordServiceSql.getCount(startTime,endTime,ucode,testCode);
        Map mapBySql = withdrawalsRecordRepository.getMapBySql(sql);
        if (ObjectUtil.isNotEmpty(mapBySql) && ObjectUtil.isNotEmpty(mapBySql.get("count"))){
            return  Integer.parseInt(mapBySql.get("count").toString());
        }
        return 0;
    }

    public BigDecimal getSum(String startTime, String endTime, String ucode,String testCode) {
        String sql = WithdrawalsRecordServiceSql.getSum(startTime,endTime,ucode,testCode);
        Map mapBySql = withdrawalsRecordRepository.getMapBySql(sql);
        if (ObjectUtil.isNotEmpty(mapBySql) && ObjectUtil.isNotEmpty(mapBySql.get("sum"))){
            return  new BigDecimal(mapBySql.get("sum").toString());
        }
        return new BigDecimal("0.00");

    }
}

