package cn.rh.flash.service.dzuser;

import cn.hutool.core.util.ObjectUtil;
import cn.rh.flash.bean.dto.FalseDataForm;
import cn.rh.flash.bean.dto.api.RechargeOrderDto;
import cn.rh.flash.bean.entity.dzsys.PaymentChannel;
import cn.rh.flash.bean.entity.dzuser.*;
import cn.rh.flash.bean.entity.dzvip.DzVipMessage;
import cn.rh.flash.bean.entity.dzvip.VipPurchaseHistory;
import cn.rh.flash.bean.entity.system.Dict;
import cn.rh.flash.bean.entity.system.User;
import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.vo.api.RechargeOrderVo;
import cn.rh.flash.bean.vo.dzuser.RechargeRecordVo;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.CacheDao;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.cache.impl.EhcacheDao;
import cn.rh.flash.cache.impl.MyRedissonLocker;
import cn.rh.flash.dao.dzuser.RechargeRecordRepository;
import cn.rh.flash.dao.dzuser.UserInfoRepository;
import cn.rh.flash.sdk.paymentChannel.BiPay.BiPayUtil;
import cn.rh.flash.sdk.paymentChannel.BiPay.BipayBean.RechargeNotify;
import cn.rh.flash.sdk.paymentChannel.CBPay.CBPayBean.CBPayNotifyResp;
import cn.rh.flash.sdk.paymentChannel.FPay.FPayBean.FPayNotifyResp;
import cn.rh.flash.sdk.paymentChannel.JDPay.JDPayBean.JDPayNotifyResp;
import cn.rh.flash.sdk.paymentChannel.KDPay.KDPayBean.KDPayNotifyResp;
import cn.rh.flash.sdk.paymentChannel.MYPay.MYPayBean.MYPayWithdrawNotifyResp;
import cn.rh.flash.sdk.paymentChannel.OKPay.OKPayBean.OKPayOrderDto;
import cn.rh.flash.sdk.paymentChannel.Mpay.mpayBean.MPayNotifyResp;
import cn.rh.flash.sdk.paymentChannel.QNQBPay.QNQBPayBean.QNQBPayNotifyResp;
import cn.rh.flash.sdk.paymentChannel.zimu808Pay.zimu808Bean.Zimu808RechargeNotify;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.service.coom.dz.ApiUserCoom;
import cn.rh.flash.service.coom.dz.RecordInformation;
import cn.rh.flash.service.coom.dz.ShopService;
import cn.rh.flash.service.dzsys.PaymentChannelService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.dzvip.ByVipTotalMoneyService;
import cn.rh.flash.service.dzvip.VipPurchaseHistoryService;
import cn.rh.flash.service.system.impl.ConstantFactory;
import cn.rh.flash.utils.*;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.rh.flash.security.JwtUtil.getUsername;

@Log4j2
@Service
public class RechargeRecordService extends BaseService<RechargeRecord, Long, RechargeRecordRepository> {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private RechargeRecordRepository rechargeRecordRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private RecordInformation recordInformation;

    @Autowired
    private VipPurchaseHistoryService vipPurchaseHistoryService;

    @Autowired
    private ApiUserCoom apiUserCoom;

    @Autowired
    private ConfigCache configCache;

    @Autowired
    private ShopService shopService;

    @Autowired
    private PaymentChannelService paymentChannelService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private UserBalanceService userBalanceService;

    @Autowired
    private TransactionRecordService transactionRecordService;

    @Autowired
    private MyRedissonLocker myRedissonLocker;

    @Autowired
    private FalseDataService falseDataService;

    @Autowired
    private ByVipTotalMoneyService byVipTotalMoneyService;

    @Autowired
    private TotalRechargeAmountService totalRechargeAmountService;

    @Autowired
    private EhcacheDao ehcacheDao;


    @Transactional(rollbackFor = Exception.class)
    public Ret<RechargeOrderVo> createRechargeOrder(RechargeOrderDto dto, Long userId) throws Exception {

        // 判断支付通道是否存在
        List<Dict> dictList = ConstantFactory.me().getDicts("支付通道");
        Set<String> collect = dictList.stream().map(Dict::getNum).collect(Collectors.toSet());
        if (!collect.contains(dto.getChannelName())) {
            return Rets.failure(MessageTemplateEnum.RECHARGE_CREATE_ERROR.getCode(), MessageTemplateEnum.RECHARGE_CREATE_ERROR);
        }

        UserInfo one = userInfoService.findUserByUserIdBill(userId);
        DzVipMessage vip = ehcacheDao.hget(CacheDao.VIPMESSAGE, one.getVipType(), DzVipMessage.class);

        //判定通道类型是否开启
        switch (dto.getChannelType()) {
            case "USDT.TRC20":
                if (configCache.get(ConfigKeyEnum.PAYMENT_USDT_TRC20_ISOPEN).trim().equals("0")) {
                    return Rets.failure(MessageTemplateEnum.CHANNEL_TYPE_NOT_OPEN.getCode(), MessageTemplateEnum.CHANNEL_TYPE_NOT_OPEN);
                }
                break;
            case "USDT.Polygon":
                if (configCache.get(ConfigKeyEnum.PAYMENT_USDT_POLYGON_ISOPEN).trim().equals("0")) {
                    return Rets.failure(MessageTemplateEnum.CHANNEL_TYPE_NOT_OPEN.getCode(), MessageTemplateEnum.CHANNEL_TYPE_NOT_OPEN);
                }
                break;
            case "other":
                //充值不需要限制VIP所含提现方式
//                if (vip!=null&&StringUtil.isNotEmpty(vip.getWithdrawMethods()) && !vip.getWithdrawMethods().contains(dto.getChannelName())) {
//                    return Rets.failure(MessageTemplateEnum.CHANNEL_TYPE_NOT_OPEN.getCode(), MessageTemplateEnum.CHANNEL_TYPE_NOT_OPEN);
//                }
                break;
            case "bank":
                break;
            case "MYPay":
                break;
            default:
                return Rets.failure(MessageTemplateEnum.RECHARGE_CREATE_ERROR.getCode(), MessageTemplateEnum.RECHARGE_CREATE_ERROR);
        }

        if (one != null) {
            Double money = BigDecimalUtils.format(Double.parseDouble(dto.getAmount()), 2);
            String orderNum = MakeOrderNum.makeOrderNum("cz");

            RechargeOrderVo vo = recordInformation.createRechargeOrderThird(1, one.getId().toString(), money.toString(), orderNum, dto.getChannelName(), dto.getChannelType());
            if (vo != null) {
                // 充值状态 1:进行中,2:待回调,3:已完成
                RechargeRecord record = recordInformation.addRechargeRecord(money, dto.getChannelName(), vo.getAddress(), dto.getChannelType(), one.getId(), one.getSourceInvitationCode(), one.getAccount(), "2", orderNum, null);
//                record.setWithdrawalAddress(vo.getAddress());
//                record.setRechargeStatus("2");
//                rechargeRecordRepository.save(record);
                return Rets.success(vo);
            }
        }

        sysLogService.addSysLog(one.getAccount(), one.getId(), one.getAccount(), "APP", SysLogEnum.CREATE_RECHARGE_ORDER);
        return Rets.failure(MessageTemplateEnum.RECHARGE_CREATE_ERROR.getCode(), MessageTemplateEnum.RECHARGE_CREATE_ERROR);
    }

    /**
     * bipay 回调
     *
     * @param rechargeNotify
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String notifyRechargeOrder(RechargeNotify rechargeNotify) {

        String reqIp = HttpUtil.getIp();

        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();

        if (!whiteIps.contains(reqIp)) {
            return "IP_NOT_WHITE_LIST";
        }
        if (rechargeNotify != null) {
            // 充值状态 1:进行中,2:待回掉,3:已完成
            RechargeRecord rechargeRecord = rechargeRecordRepository.findByOrderNumber(rechargeNotify.getCustomOrderId());
            if (rechargeRecord != null && "2".equals(rechargeRecord.getRechargeStatus())) {

                PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", rechargeRecord.getChannelName()));
                if (paymentChannel == null) {
                    log.error("通道 BiPay 信息不存在");
                    // return 前解锁
                    return "FAIL";
                }
                //验签成功
                if (BiPayUtil.rechargeOrderNotify(rechargeNotify, paymentChannel.getPrivateKey()) != null) {
                    // 0支付中 1支付成功 2失败 3超时
                    if ("1".equals(rechargeNotify.getStatus())) {
                        if (rechargeSuccess(rechargeNotify.getAmount(), rechargeRecord)) return "OK";
                    }
                }
            }
        }
        log.error("[充值bipay回调]支付回调验证签名失败完整返回参数:" + rechargeNotify);
        return "FAIL";
    }

    /**
     * walletpay  回调
     *
     * @param rechargeNotify
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String walletNotifyRechargeOrder(cn.rh.flash.sdk.paymentChannel.WalletPay.WalletPayBean.RechargeNotify rechargeNotify) {
        String reqIp = HttpUtil.getIp();
        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();
        if (!whiteIps.contains(reqIp)) {
            return "IP_NOT_WHITE_LIST";
        }
        if (rechargeNotify != null) {
            // 充值状态 1:进行中,2:待回掉,3:已完成
            RechargeRecord rechargeRecord = rechargeRecordRepository.findByOrderNumber(rechargeNotify.getUser_order_id());
            if (rechargeRecord != null && "2".equals(rechargeRecord.getRechargeStatus())) {

                PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", rechargeRecord.getChannelName()));
                if (paymentChannel == null) {
                    log.error("通道信息不存在,:[{}]", rechargeRecord.getChannelName());
                    return "FAIL";
                }
                // 1.支付中,2.支付成功3.支付失败4.支付超时
                if ("2".equals(rechargeNotify.getOrder_status())) {
                    if (rechargeSuccess(rechargeNotify.getCurrency_receipt_money(), rechargeRecord)) return "ok";
                }

            }
        }
        log.error("[充值walletpay回调]支付回调验证签名失败完整返回参数:" + rechargeNotify);
        return "FAIL";
    }

    /**
     * kdpay 回调
     *
     * @param rechargeNotify
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String kdpayNotifyRechargeOrder(KDPayNotifyResp rechargeNotify, PaymentChannel paymentChannel) {
        //验证IP白名单
        String reqIp = HttpUtil.getIp();
        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();
        if (!whiteIps.contains(reqIp)) {
            return "IP_NOT_WHITE_LIST";
        }
        if (rechargeNotify != null) {
            //1 初始 2 待支付 3 已支付 4 失败
            if ("3".equals(rechargeNotify.getStatus())) {
                // 充值状态 1:进行中,2:待回掉,3:已完成
                RechargeRecord rechargeRecord = rechargeRecordRepository.findByOrderNumber(rechargeNotify.getOrderCode());
                if (rechargeRecord != null && "2".equals(rechargeRecord.getRechargeStatus())) {
                    log.info("kdpay充值状态：" + rechargeNotify.getStatus());
                    if (rechargeSuccess(rechargeNotify.getAmount(), rechargeRecord)) return "success";
                }
                log.error("kdpay充值回调错误：充值记录-->" + rechargeRecord.toString());
                return "Recharge_FAIL";
            }
            return "STATUS_FAIL_" + rechargeNotify.getStatus();
        }
        return "FAIL";
    }

    /**
     * qnqbpay 回调
     *
     * @param rechargeNotify
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String qnqbpayNotifyRechargeOrder(QNQBPayNotifyResp rechargeNotify, PaymentChannel paymentChannel) {
        //验证IP白名单
        String reqIp = HttpUtil.getIp();
        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();
        if (!whiteIps.contains(reqIp)) {
            return "IP_NOT_WHITE_LIST";
        }
        if (rechargeNotify != null) {
            //1成功
            if ("1".equals(rechargeNotify.getStatus())) {
                // 充值状态 1:进行中,2:待回掉,3:已完成
                RechargeRecord rechargeRecord = rechargeRecordRepository.findByOrderNumber(rechargeNotify.getOrderid());
                if (rechargeRecord != null && "2".equals(rechargeRecord.getRechargeStatus())) {
                    log.info("qnqbpay充值状态：" + rechargeNotify.getStatus());
                    if (rechargeSuccess(rechargeNotify.getAmount(), rechargeRecord)) return "success";
                }
                log.error("qnqbpay充值回调错误：充值记录-->" + rechargeRecord.toString());
                return "Recharge_FAIL";
            }
            return "STATUS_FAIL_" + rechargeNotify.getStatus();
        }
        return "FAIL";
    }


    /**
     * fpay 回调
     *
     * @param rechargeNotify
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String fpayNotifyRechargeOrder(FPayNotifyResp rechargeNotify, PaymentChannel paymentChannel) {
        //验证IP白名单
        String reqIp = HttpUtil.getIp();
        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();
        if (!whiteIps.contains(reqIp)) {
            return "IP_NOT_WHITE_LIST";
        }
        if (rechargeNotify != null) {
            //2 成功
            if ("2".equals(rechargeNotify.getState())) {
                // 充值状态 1:进行中,2:待回掉,3:已完成
                RechargeRecord rechargeRecord = rechargeRecordRepository.findByOrderNumber(rechargeNotify.getOrderid());
                if (rechargeRecord != null && "2".equals(rechargeRecord.getRechargeStatus())) {
                    log.info("fpay充值状态：" + rechargeNotify.getState());
                    if (rechargeSuccess(rechargeNotify.getAmount(), rechargeRecord)) return "success";
                }
                log.error("fpay充值回调错误：充值记录-->" + rechargeRecord.toString());
                return "Recharge_FAIL";
            }
            return "STATUS_FAIL_" + rechargeNotify.getState();
        }
        return "FAIL";
    }

    /**
     * jdpay 回调
     *
     * @param rechargeNotify
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String jdpayNotifyRechargeOrder(JDPayNotifyResp rechargeNotify, PaymentChannel paymentChannel) {
        //验证IP白名单
        String reqIp = HttpUtil.getIp();
        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();
        if (!whiteIps.contains(reqIp)) {
            return "IP_NOT_WHITE_LIST";
        }
        if (rechargeNotify != null) {
            //1 初始 2 待支付 3 已支付 4 失败
            if ("3".equals(rechargeNotify.getStatus())) {
                // 充值状态 1:进行中,2:待回掉,3:已完成
                RechargeRecord rechargeRecord = rechargeRecordRepository.findByOrderNumber(rechargeNotify.getOrderCode());
                if (rechargeRecord != null && "2".equals(rechargeRecord.getRechargeStatus())) {
                    log.info("Jdpay充值状态：" + rechargeNotify.getStatus());
                    if (rechargeSuccess(rechargeNotify.getAmount(), rechargeRecord)) return "success";
                }
                log.error("Jdpay充值回调错误：充值记录-->" + rechargeRecord.toString());
                return "Recharge_FAIL";
            }
            return "STATUS_FAIL_" + rechargeNotify.getStatus();
        }
        return "FAIL";
    }


    /**
     * cbpay 回调
     *
     * @param rechargeNotify
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String cbpayNotifyRechargeOrder(CBPayNotifyResp rechargeNotify, PaymentChannel paymentChannel) {
        //验证IP白名单
        String reqIp = HttpUtil.getIp();
        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();
        if (!whiteIps.contains(reqIp)) {
            return "IP_NOT_WHITE_LIST";
        }
        if (rechargeNotify != null) {
            //1 初始 2 待支付 3 已支付 4 失败
            if ("3".equals(rechargeNotify.getStatus())) {
                // 充值状态 1:进行中,2:待回掉,3:已完成
                RechargeRecord rechargeRecord = rechargeRecordRepository.findByOrderNumber(rechargeNotify.getOrderCode());
                if (rechargeRecord != null && "2".equals(rechargeRecord.getRechargeStatus())) {
                    log.info("cbpay充值状态：" + rechargeNotify.getStatus());
                    if (rechargeSuccess(rechargeNotify.getAmount(), rechargeRecord)) return "success";
                }
                log.error("cbpay充值回调错误：充值记录-->" + rechargeRecord.toString());
                return "Recharge_FAIL";
            }
            return "STATUS_FAIL_" + rechargeNotify.getStatus();
        }
        return "FAIL";
    }

    /**
     * mpay 回调
     *
     * @param rechargeNotify
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String mpayNotifyRechargeOrder(MPayNotifyResp rechargeNotify, PaymentChannel paymentChannel) {
        //验证IP白名单
        String reqIp = HttpUtil.getIp();
        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();
        if (!whiteIps.contains(reqIp)) {
            return "IP_NOT_WHITE_LIST";
        }
        if (rechargeNotify != null) {
            //上发单状态，“true”成功，“false”失败
            if ("true".equals(rechargeNotify.getStatus())) {
                // 充值状态 1:进行中,2:待回掉,3:已完成
                RechargeRecord rechargeRecord = rechargeRecordRepository.findByOrderNumber(rechargeNotify.getMerchOrderId());
                if (rechargeRecord != null && "2".equals(rechargeRecord.getRechargeStatus())) {
                    log.info("mpay充值状态：" + rechargeNotify.getStatus());
                    if (rechargeSuccess(rechargeNotify.getAmount().toString(), rechargeRecord)) return "success";
                }
                log.error("mpay充值回调错误：充值记录-->" + rechargeRecord.toString());
                return "Recharge_FAIL";
            }
            return "STATUS_FAIL_" + rechargeNotify.getStatus();
        }
        return "FAIL";
    }


    /**
     * mypay 回调
     *
     * @param rechargeNotify
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String mypayNotifyRechargeOrder(MYPayWithdrawNotifyResp rechargeNotify, PaymentChannel paymentChannel) {
        //验证IP白名单
        String reqIp = HttpUtil.getIp();
        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();
        log.info("mypay充值回调数据状态rechargeNotify：" + rechargeNotify + "++++++paymentChannel" + paymentChannel);
        if (!whiteIps.contains(reqIp)) {
            log.error("IP_NOT_WHITE_LIST");
            return "IP_NOT_WHITE_LIST";
        }
        if (rechargeNotify != null) {
            //0：处理中 1：成功 2：失败
            if ("1".equals(rechargeNotify.getStatus())) {
                // 充值状态 1:进行中,2:待回掉,3:已完成
                RechargeRecord rechargeRecord = rechargeRecordRepository.findByOrderNumber(rechargeNotify.getMerchantOrderNo());
                if (rechargeRecord != null && "2".equals(rechargeRecord.getRechargeStatus())) {
                    log.info("mypay充值状态：" + rechargeNotify.getStatus());
                    if (rechargeSuccess(rechargeNotify.getAmount(), rechargeRecord)) return "success";
                }
                log.error("mypay充值回调错误：充值记录-->" + rechargeRecord.toString());
                return "Recharge_FAIL";
            }
            return "STATUS_FAIL_" + rechargeNotify.getStatus();
        }
        return "FAIL";
    }

    /**
     * OKpay 回调
     *
     * @param rechargeNotify
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String okpayNotifyRechargeOrder(OKPayOrderDto rechargeNotify, PaymentChannel paymentChannel) {
        //验证IP白名单
        String reqIp = HttpUtil.getIp();
        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();
        log.info("OKpay充值回调数据状态rechargeNotify：" + rechargeNotify + "++++++paymentChannel" + paymentChannel);
        if (!whiteIps.contains(reqIp)) {
            log.error("IP_NOT_WHITE_LIST");
            return "IP_NOT_WHITE_LIST";
        }
        if (rechargeNotify != null) {
            //Transed	4      //已转币
            if ("4".equals(rechargeNotify.getState())) {
                // 充值状态 1:进行中,2:待回掉,3:已完成
                RechargeRecord rechargeRecord = rechargeRecordRepository.findByOrderNumber(rechargeNotify.getOrderid());
                if (rechargeRecord != null && "2".equals(rechargeRecord.getRechargeStatus())) {
                    log.info("okpay充值状态：" + rechargeNotify.getState());
                    if (rechargeSuccess(rechargeNotify.getAmount(), rechargeRecord)) return "success";
                }
                log.error("okpay充值回调错误：充值记录-->" + rechargeRecord.toString());
                return "Recharge_FAIL";
            }
            return "STATUS_FAIL_" + rechargeNotify.getState();
        }
        return "FAIL";
    }

    /**
     * 808pay 回调
     *
     * @param rechargeNotify
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String zimu808payNotifyRechargeOrder(Zimu808RechargeNotify rechargeNotify) {
        //验证IP白名单
        String reqIp = HttpUtil.getIp();
        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();
        if (!whiteIps.contains(reqIp)) {
            log.error("808pay IP_NOT_WHITE_LIST");
            return "IP_NOT_WHITE_LIST";
        }
        if (rechargeNotify != null) {
            //3：成功
            if ("3".equals(rechargeNotify.getStatus())) {
                // 充值状态 1:进行中,2:待回掉,3:已完成
                RechargeRecord rechargeRecord = rechargeRecordRepository.findByOrderNumber(rechargeNotify.getMchOrderNo());
                if (rechargeRecord != null && "2".equals(rechargeRecord.getRechargeStatus())) {
                    log.info("808pay充值状态：" + rechargeNotify.getStatus());
                    if (rechargeSuccess(rechargeNotify.getAmount(), rechargeRecord)) return "success";
                }
                log.error("808pay充值回调错误：充值记录-->" + rechargeRecord.toString());
                return "Recharge_FAIL";
            }
            log.error("808pay status error : " + rechargeNotify.getStatus());
            return "STATUS_FAIL_" + rechargeNotify.getStatus();
        }
        return "FAIL";
    }

    // 支付成功回调业务处理
    private boolean rechargeSuccess(String amount, RechargeRecord rechargeRecord) {
        // 查询是否首充
        List<SearchFilter> queryFilters = new ArrayList<>();
        queryFilters.add(SearchFilter.build("uid", rechargeRecord.getUid()));
        queryFilters.add(SearchFilter.build("rechargeStatus", "3"));
        String firstCharge = this.queryAll(queryFilters).size() > 0 ? "2" : "1";
        rechargeRecord.setFirstCharge(firstCharge);
        ///  todo  jk
        /// 真实收到的钱
        Double blackDouble = Double.parseDouble(amount);
        /// 创建订单时候的金额
        Double money = rechargeRecord.getMoney();
        /// 创建订单时候的金额 - 真实收到的钱  ：  0  表示一致   >0  表示收到的钱少了     少了 rmoney
        Double rmoney = BigDecimalUtils.subtract(money, blackDouble);

        /// 创建订单时候的金额 与 真实收到的钱  不相等  修改等订单金额为 实际收到的金额
        if (rmoney.doubleValue() != 0) {
            rechargeRecord.setMoney(blackDouble);
            rechargeRecord.setAfterBalance(BigDecimalUtils.subtract(rechargeRecord.getAfterBalance(), rmoney));
        }

        //todo 加余额
        recordInformation.transactionRecordPlus(rechargeRecord.getSourceInvitationCode(), rechargeRecord.getUid(), rechargeRecord.getAccount(), rechargeRecord.getPreviousBalance(), rechargeRecord.getMoney(), rechargeRecord.getAfterBalance(), rechargeRecord.getIdw(), 1, "cz", "", "");

        // 充值状态 1:进行中,2:待回调,3:已完成
        recordInformation.upOkRechargeRecord(rechargeRecord);
        return true;
    }

    /**
     * BiPay 支付VIP回调
     *
     * @param rechargeNotify
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String notifyRechargeOrderVIP(RechargeNotify rechargeNotify) {
        String reqIp = HttpUtil.getIp();
        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();
        if (!whiteIps.contains(reqIp)) {
            return "IP_NOT_WHITE_LIST";
        }
        if (rechargeNotify != null) {

            List<SearchFilter> filters = new ArrayList<>();
            filters.add(SearchFilter.build("idw", rechargeNotify.getCustomOrderId()));
            filters.add(SearchFilter.build("whetherToPay", 1)); // 支付状态 1:未支付,2:已支付

            VipPurchaseHistory vipPurchaseHistory = vipPurchaseHistoryService.get(filters);
            if (vipPurchaseHistory != null) {
                PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", vipPurchaseHistory.getChannelName()));
                if (paymentChannel == null) {
                    log.error("通道 BiPay 信息不存在");
                    return "FAIL";
                }
                //验签成功
                if (BiPayUtil.rechargeOrderNotify(rechargeNotify, paymentChannel.getPrivateKey()) != null) {
                    // 0支付中 1支付成功 2失败 3超时
                    if ("1".equals(rechargeNotify.getStatus())) {
                        UserInfo one = userInfoRepository.getOne(vipPurchaseHistory.getUid());
                        // 回调业务逻辑处理
                        if (payVipSuccess(rechargeNotify.getAmount(), rechargeNotify.getAddress(), vipPurchaseHistory, paymentChannel, one))
                            return "OK";
                    }
                }
            }

        }
        log.error("[bipayvip购买回调]支付回调验证签名失败完整返回参数:" + rechargeNotify);
        return "FAIL";
    }

    /**
     * walletPay 支付VIP回调
     *
     * @param rechargeNotify
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String walletNotifyRechargeOrderVIP(cn.rh.flash.sdk.paymentChannel.WalletPay.WalletPayBean.RechargeNotify rechargeNotify) {
        String reqIp = HttpUtil.getIp();
        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();
        if (!whiteIps.contains(reqIp)) {
            return "IP_NOT_WHITE_LIST";
        }
        if (rechargeNotify != null) {

            List<SearchFilter> filters = new ArrayList<>();
            filters.add(SearchFilter.build("idw", rechargeNotify.getUser_order_id()));
            filters.add(SearchFilter.build("whetherToPay", 1)); // 支付状态 1:未支付,2:已支付

            VipPurchaseHistory vipPurchaseHistory = vipPurchaseHistoryService.get(filters);
            if (vipPurchaseHistory != null) {
                PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", vipPurchaseHistory.getChannelName()));
                if (paymentChannel == null) {
                    log.error("通道 WalletPay 信息不存在");
                    return "FAIL";
                }
                // 1.支付中,2.支付成功3.支付失败4.支付超时
                if ("2".equals(rechargeNotify.getOrder_status())) {
                    UserInfo one = userInfoRepository.getOne(vipPurchaseHistory.getUid());
                    // 回调业务逻辑处理
                    if (payVipSuccess(rechargeNotify.getCurrency_receipt_money(), rechargeNotify.getCoin_address(), vipPurchaseHistory, paymentChannel, one))
                        return "ok";
                }
            }

        }
        log.error("[walletvip购买回调]支付回调验证签名失败完整返回参数:" + rechargeNotify);
        return "FAIL";
    }

    /**
     * KDPay 支付VIP回调
     *
     * @param rechargeNotify
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String kdpayNotifyRechargeOrderVIP(KDPayNotifyResp rechargeNotify, PaymentChannel paymentChannel) {
        String reqIp = HttpUtil.getIp();
        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();
        if (!whiteIps.contains(reqIp)) {
            return "IP_NOT_WHITE_LIST";
        }
        if (rechargeNotify != null) {
            List<SearchFilter> filters = new ArrayList<>();
            filters.add(SearchFilter.build("idw", rechargeNotify.getOrderCode()));
            filters.add(SearchFilter.build("whetherToPay", 1)); // 支付状态 1:未支付,2:已支付

            VipPurchaseHistory vipPurchaseHistory = vipPurchaseHistoryService.get(filters);
            if (vipPurchaseHistory != null) {
                if ("3".equals(rechargeNotify.getStatus())) {
                    log.info("kdpay充值VIP状态：" + rechargeNotify.getStatus());
                    // 0支付中 1支付成功 2失败 3超时
                    UserInfo one = userInfoRepository.getOne(vipPurchaseHistory.getUid());
                    // 回调业务逻辑处理
                    if (payVipSuccess(rechargeNotify.getAmount(), rechargeNotify.getOrderCode(), vipPurchaseHistory, paymentChannel, one)) {
                        return "success";
                    }

                }
                return "STATUS_FAIL_" + rechargeNotify.getStatus();
            }
            log.error("kdpay充值VIP回调错误：充值记录-->" + vipPurchaseHistory.toString());
            return "VIP_Recharge_FAIL";
        }
        return "FAIL";
    }

    /**
     * QNQBPay 支付VIP回调
     *
     * @param rechargeNotify
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String qnqbpayNotifyRechargeOrderVIP(QNQBPayNotifyResp rechargeNotify, PaymentChannel paymentChannel) {
        String reqIp = HttpUtil.getIp();
        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();
        if (!whiteIps.contains(reqIp)) {
            return "IP_NOT_WHITE_LIST";
        }
        if (rechargeNotify != null) {
            List<SearchFilter> filters = new ArrayList<>();
            filters.add(SearchFilter.build("idw", rechargeNotify.getOrderid()));
            filters.add(SearchFilter.build("whetherToPay", 1)); // 支付状态 1:未支付,2:已支付

            VipPurchaseHistory vipPurchaseHistory = vipPurchaseHistoryService.get(filters);
            if (vipPurchaseHistory != null) {
                //订单状态1 成功
                if ("1".equals(rechargeNotify.getStatus())) {
                    log.info("qnqbpay充值VIP状态：" + rechargeNotify.getStatus());
                    // 0支付中 1支付成功 2失败 3超时
                    UserInfo one = userInfoRepository.getOne(vipPurchaseHistory.getUid());
                    // 回调业务逻辑处理
                    if (payVipSuccess(rechargeNotify.getAmount(), rechargeNotify.getOrderid(), vipPurchaseHistory, paymentChannel, one)) {
                        return "success";
                    }
                }
                return "STATUS_FAIL_" + rechargeNotify.getStatus();
            }
            log.error("qnqbpay充值VIP回调错误：充值记录-->" + vipPurchaseHistory.toString());
            return "VIP_Recharge_FAIL";
        }
        return "FAIL";
    }

    /**
     * FPay 支付VIP回调
     *
     * @param rechargeNotify
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String fpayNotifyRechargeOrderVIP(FPayNotifyResp rechargeNotify, PaymentChannel paymentChannel) {
        String reqIp = HttpUtil.getIp();
        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();
        if (!whiteIps.contains(reqIp)) {
            return "IP_NOT_WHITE_LIST";
        }
        if (rechargeNotify != null) {
            List<SearchFilter> filters = new ArrayList<>();
            filters.add(SearchFilter.build("idw", rechargeNotify.getOrderid()));
            filters.add(SearchFilter.build("whetherToPay", 1)); // 支付状态 1:未支付,2:已支付

            VipPurchaseHistory vipPurchaseHistory = vipPurchaseHistoryService.get(filters);
            if (vipPurchaseHistory != null) {
                if ("2".equals(rechargeNotify.getState())) {
                    log.info("fpay充值VIP状态：" + rechargeNotify.getState());
                    // 0支付中 1支付成功 2失败 3超时
                    UserInfo one = userInfoRepository.getOne(vipPurchaseHistory.getUid());
                    // 回调业务逻辑处理
                    if (payVipSuccess(rechargeNotify.getAmount(), rechargeNotify.getOrderid(), vipPurchaseHistory, paymentChannel, one)) {
                        return "success";
                    }

                }
                return "STATUS_FAIL_" + rechargeNotify.getState();
            }
            log.error("fpay充值VIP回调错误：充值记录-->" + vipPurchaseHistory.toString());
            return "VIP_Recharge_FAIL";
        }
        return "FAIL";
    }

    /**
     * JDPay 支付VIP回调
     *
     * @param rechargeNotify
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String jdpayNotifyRechargeOrderVIP(JDPayNotifyResp rechargeNotify, PaymentChannel paymentChannel) {
        String reqIp = HttpUtil.getIp();
        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();
        if (!whiteIps.contains(reqIp)) {
            return "IP_NOT_WHITE_LIST";
        }
        if (rechargeNotify != null) {
            List<SearchFilter> filters = new ArrayList<>();
            filters.add(SearchFilter.build("idw", rechargeNotify.getOrderCode()));
            filters.add(SearchFilter.build("whetherToPay", 1)); // 支付状态 1:未支付,2:已支付

            VipPurchaseHistory vipPurchaseHistory = vipPurchaseHistoryService.get(filters);
            if (vipPurchaseHistory != null) {
                if ("3".equals(rechargeNotify.getStatus())) {
                    log.info("jdpay充值VIP状态：" + rechargeNotify.getStatus());
                    // 0支付中 1支付成功 2失败 3超时
                    UserInfo one = userInfoRepository.getOne(vipPurchaseHistory.getUid());
                    // 回调业务逻辑处理
                    if (payVipSuccess(rechargeNotify.getAmount(), rechargeNotify.getOrderCode(), vipPurchaseHistory, paymentChannel, one)) {
                        return "success";
                    }

                }
                return "STATUS_FAIL_" + rechargeNotify.getStatus();
            }
            log.error("jdpay充值VIP回调错误：充值记录-->" + vipPurchaseHistory.toString());
            return "VIP_Recharge_FAIL";
        }
        return "FAIL";
    }

    /**
     * cbPay 支付VIP回调
     *
     * @param rechargeNotify
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String cbpayNotifyRechargeOrderVIP(CBPayNotifyResp rechargeNotify, PaymentChannel paymentChannel) {
        String reqIp = HttpUtil.getIp();
        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();
        if (!whiteIps.contains(reqIp)) {
            return "IP_NOT_WHITE_LIST";
        }
        if (rechargeNotify != null) {
            List<SearchFilter> filters = new ArrayList<>();
            filters.add(SearchFilter.build("idw", rechargeNotify.getOrderCode()));
            filters.add(SearchFilter.build("whetherToPay", 1)); // 支付状态 1:未支付,2:已支付

            VipPurchaseHistory vipPurchaseHistory = vipPurchaseHistoryService.get(filters);
            if (vipPurchaseHistory != null) {
                if ("3".equals(rechargeNotify.getStatus())) {
                    log.info("cbpay充值VIP状态：" + rechargeNotify.getStatus());
                    // 0支付中 1支付成功 2失败 3超时
                    UserInfo one = userInfoRepository.getOne(vipPurchaseHistory.getUid());
                    // 回调业务逻辑处理
                    if (payVipSuccess(rechargeNotify.getAmount(), rechargeNotify.getOrderCode(), vipPurchaseHistory, paymentChannel, one)) {
                        return "success";
                    }

                }
                return "STATUS_FAIL_" + rechargeNotify.getStatus();
            }
            log.error("CBpay充值VIP回调错误：充值记录-->" + vipPurchaseHistory.toString());
            return "VIP_Recharge_FAIL";
        }
        return "FAIL";
    }


    /**
     * MPay 支付VIP回调
     *
     * @param rechargeNotify
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String mpayNotifyRechargeOrderVIP(MPayNotifyResp rechargeNotify, PaymentChannel paymentChannel) {
        String reqIp = HttpUtil.getIp();
        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();
        if (!whiteIps.contains(reqIp)) {
            return "IP_NOT_WHITE_LIST";
        }
        if (rechargeNotify != null) {
            List<SearchFilter> filters = new ArrayList<>();
            filters.add(SearchFilter.build("idw", rechargeNotify.getMerchOrderId()));
            filters.add(SearchFilter.build("whetherToPay", 1)); // 支付状态 1:未支付,2:已支付
            VipPurchaseHistory vipPurchaseHistory = vipPurchaseHistoryService.get(filters);
            if (vipPurchaseHistory != null) {
                //上发单状态，“true”成功，“false”失败
                if ("true".equals(rechargeNotify.getStatus())) {
                    log.info("mpay充值VIP状态：" + rechargeNotify.getStatus());
                    // 0支付中 1支付成功 2失败 3超时
                    UserInfo one = userInfoRepository.getOne(vipPurchaseHistory.getUid());
                    // 回调业务逻辑处理
                    if (payVipSuccess(rechargeNotify.getAmount().toString(), rechargeNotify.getMerchOrderId(), vipPurchaseHistory, paymentChannel, one)) {
                        return "success";
                    }

                }
                return "STATUS_FAIL_" + rechargeNotify.getStatus();
            }
            log.error("Mpay充值VIP回调错误：充值记录-->" + vipPurchaseHistory.toString());
            return "VIP_Recharge_FAIL";
        }
        return "FAIL";
    }


    /**
     * MYPay 支付VIP回调
     *
     * @param rechargeNotify
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String mypayNotifyRechargeOrderVIP(MYPayWithdrawNotifyResp rechargeNotify, PaymentChannel paymentChannel) {
        String reqIp = HttpUtil.getIp();
        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();
        if (!whiteIps.contains(reqIp)) {
            return "IP_NOT_WHITE_LIST";
        }
        if (rechargeNotify != null) {
            List<SearchFilter> filters = new ArrayList<>();
            filters.add(SearchFilter.build("idw", rechargeNotify.getMerchantOrderNo()));
            filters.add(SearchFilter.build("whetherToPay", 1)); // 支付状态 1:未支付,2:已支付

            VipPurchaseHistory vipPurchaseHistory = vipPurchaseHistoryService.get(filters);
            if (vipPurchaseHistory != null) {
                //0：处理中 1：成功 2：失败
                if ("1".equals(rechargeNotify.getStatus())) {
                    log.info("mypay充值VIP状态：" + rechargeNotify.getStatus());
                    // 0支付中 1支付成功 2失败 3超时
                    UserInfo one = userInfoRepository.getOne(vipPurchaseHistory.getUid());
                    // 回调业务逻辑处理
                    if (payVipSuccess(rechargeNotify.getAmount(), rechargeNotify.getMerchantOrderNo(), vipPurchaseHistory, paymentChannel, one)) {
                        return "success";
                    }

                }
                return "STATUS_FAIL_" + rechargeNotify.getStatus();
            }
            log.error("mypay充值VIP回调错误：充值记录-->" + vipPurchaseHistory.toString());
            return "VIP_Recharge_FAIL";
        }
        return "FAIL";
    }

    /**
     * okPay 支付VIP回调
     *
     * @param rechargeNotify
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String okpayNotifyRechargeOrderVIP(OKPayOrderDto rechargeNotify, PaymentChannel paymentChannel) {
        String reqIp = HttpUtil.getIp();
        String whiteIps = configCache.get(ConfigKeyEnum.DZ_WHITE_IP).trim();
        if (!whiteIps.contains(reqIp)) {
            return "IP_NOT_WHITE_LIST";
        }
        if (rechargeNotify != null) {
            List<SearchFilter> filters = new ArrayList<>();
            filters.add(SearchFilter.build("idw", rechargeNotify.getOrderid()));
            filters.add(SearchFilter.build("whetherToPay", 1)); // 支付状态 1:未支付,2:已支付

            VipPurchaseHistory vipPurchaseHistory = vipPurchaseHistoryService.get(filters);
            if (vipPurchaseHistory != null) {
                //Transed	4      //已转币
                if ("4".equals(rechargeNotify.getState())) {
                    log.info("okpay充值VIP状态：" + rechargeNotify.getState());
                    // 0支付中 1支付成功 2失败 3超时
                    UserInfo one = userInfoRepository.getOne(vipPurchaseHistory.getUid());
                    // 回调业务逻辑处理
                    if (payVipSuccess(rechargeNotify.getAmount(), rechargeNotify.getOrderid(), vipPurchaseHistory, paymentChannel, one)) {
                        return "success";
                    }

                }
                return "STATUS_FAIL_" + rechargeNotify.getState();
            }
            log.error("okpay充值VIP回调错误：充值记录-->" + vipPurchaseHistory.toString());
            return "VIP_Recharge_FAIL";
        }
        return "FAIL";
    }

    /**
     * vip 支付成功 回调处理
     *
     * @param amount
     * @param address
     * @param vipPurchaseHistory
     * @param paymentChannel
     * @param one
     * @return
     */
    public boolean payVipSuccess(String amount, String address, VipPurchaseHistory vipPurchaseHistory, PaymentChannel paymentChannel, UserInfo one) {
        if (Double.parseDouble(amount) >= vipPurchaseHistory.getPaymentAmount()) {
            if (one != null) {
                // 用户之前等级是v0v1说明是第一次购买会员 返佣
                if ("v0".equals(vipPurchaseHistory.getPreviousViPType()) || "v1".equals(vipPurchaseHistory.getPreviousViPType())) {
                    //todo 加VIP返佣
                    //进入套娃收益
                    int buyVip = Integer.parseInt(vipPurchaseHistory.getAfterViPType().replace("v", ""));

                    String nickName = configCache.get(ConfigKeyEnum.SITE_NICKNAME).trim();
                    if (StringUtil.isEmpty(nickName)) {
                        nickName = " ";
                    }
                    if ("西班牙,埃及1,黄色单车".contains(nickName)) {
                        recordInformation.cycleRewardTwo(one, vipPurchaseHistory.getIdw(), buyVip, vipPurchaseHistory.getPreviousViPType(), vipPurchaseHistory.getAfterViPType());
                    } else {
                        recordInformation.cycleReward(one, vipPurchaseHistory.getIdw(), buyVip, vipPurchaseHistory.getPreviousViPType(), vipPurchaseHistory.getAfterViPType());
                    }

                }
                // 支付状态 1:未支付,2:已支付
                vipPurchaseHistory.setWhetherToPay(2);
                vipPurchaseHistoryService.update(vipPurchaseHistory);
                //todo jk  充电宝返佣任务
                shopService.payVipOk(vipPurchaseHistory);  //
                //修改用户等级
                shopService.changeUserVip(one, vipPurchaseHistory.getAfterViPType(), vipPurchaseHistory.getValidDate().toString());
                // 2024 - 04-01 信誉分
                Integer creditScore = Integer.parseInt(configCache.get(ConfigKeyEnum.CREADIT_SCORE).trim());
                Integer nickRange = Integer.parseInt(configCache.get(ConfigKeyEnum.SITE_NICKRANFGE).trim());
                if (creditScore > 0) {
                    switch (nickRange){
                        case 0:
                            shopService.creditScoreV1(one);
                            break;
                        case 1:
                            shopService.creditScoreV2(one);
                            break;
                    }
                }
                if (Double.parseDouble(amount) > 0.00) {
                    // 查询是否首充
                    List<SearchFilter> queryFilters = new ArrayList<>();
                    queryFilters.add(SearchFilter.build("uid", one.getId()));
                    queryFilters.add(SearchFilter.build("rechargeStatus", "3"));
                    String firstCharge = this.queryAll(queryFilters).size() > 0 ? "2" : "1";
                    // 创建充值记录
                    RechargeRecord rechargeRecord = recordInformation.addRechargeRecord(Double.parseDouble(amount), paymentChannel.getChannelName(), address, vipPurchaseHistory.getChannelType(), one.getId(), one.getSourceInvitationCode(), one.getAccount(), "3", MakeOrderNum.makeOrderNum("cz"), firstCharge);

                    // 叠加总充值金额
                    TotalRechargeAmount totalRechargeAmount = totalRechargeAmountService.get(SearchFilter.build("uid", one.getId()));
                    if (totalRechargeAmount == null) {
                        totalRechargeAmount = new TotalRechargeAmount();
                        totalRechargeAmount.setIdw(new IdWorker().nextId() + "");
                        totalRechargeAmount.setSourceInvitationCode(one.getSourceInvitationCode());
                        totalRechargeAmount.setDzversion(0);
                        totalRechargeAmount.setUid(one.getId());
                        totalRechargeAmount.setAccount(one.getAccount());
                        totalRechargeAmount.setTotalRechargeAmount(Double.valueOf(amount));
                        totalRechargeAmountService.insert(totalRechargeAmount);
                    } else {
                        totalRechargeAmount.setTotalRechargeAmount(Double.parseDouble(amount) + totalRechargeAmount.getTotalRechargeAmount());
                        totalRechargeAmountService.update(totalRechargeAmount);
                    }

                    // 创建交易记录+余额

                    TransactionRecord transactionAdd = new TransactionRecord();
                    transactionAdd.setIdw(new IdWorker().nextId() + "");
                    transactionAdd.setSourceInvitationCode(rechargeRecord.getSourceInvitationCode());
                    transactionAdd.setUid(rechargeRecord.getUid());
                    transactionAdd.setAccount(rechargeRecord.getAccount());
                    transactionAdd.setOrderNumber(MakeOrderNum.makeOrderNum("cz"));
                    transactionAdd.setTransactionNumber(rechargeRecord.getIdw());
                    transactionAdd.setMoney(Double.parseDouble(amount));
                    transactionAdd.setPreviousBalance(rechargeRecord.getPreviousBalance());
                    transactionAdd.setAfterBalance(rechargeRecord.getAfterBalance());
                    transactionAdd.setTransactionType("1");  //  1:通道一充值,2:通道一提现,3:平台赠送,4:平台扣款,5:任务收益,6:转入共享余额,7:转出共享余额
                    transactionAdd.setAdditionAndSubtraction(1);
                    transactionAdd.setRemark("");
                    transactionRecordService.insert(transactionAdd);


                    // 购买VIP 创建交易记录-余额

                    TransactionRecord transactionObj = new TransactionRecord();
                    transactionObj.setIdw(new IdWorker().nextId() + "");
                    transactionObj.setSourceInvitationCode(rechargeRecord.getSourceInvitationCode());
                    transactionObj.setUid(rechargeRecord.getUid());
                    transactionObj.setAccount(rechargeRecord.getAccount());
                    transactionObj.setOrderNumber(MakeOrderNum.makeOrderNum("vip"));
                    transactionObj.setTransactionNumber(rechargeRecord.getIdw());
                    transactionObj.setMoney(vipPurchaseHistory.getPaymentAmount());
                    transactionObj.setPreviousBalance(rechargeRecord.getAfterBalance());
                    transactionObj.setAfterBalance(BigDecimalUtils.subtract(rechargeRecord.getAfterBalance(), vipPurchaseHistory.getPaymentAmount()));
                    transactionObj.setTransactionType("10");  //  1:通道一充值,2:通道一提现,3:平台赠送,4:平台扣款,5:任务收益,6:转入共享余额,7:转出共享余额
                    transactionObj.setAdditionAndSubtraction(2);
                    transactionObj.setRemark("");
                    transactionRecordService.insert(transactionObj);

                    // 增加购买vip累计金额
                    byVipTotalMoneyService.addByVipTotalMoney(one, vipPurchaseHistory.getPaymentAmount());

                    Double subtract = BigDecimalUtils.subtract(Double.parseDouble(amount), vipPurchaseHistory.getPaymentAmount());
                    if (subtract > 0) {
                        recordInformation.updateUserBalance2(rechargeRecord.getSourceInvitationCode(), rechargeRecord.getUid(), rechargeRecord.getAccount(), subtract, 1, false);
                    }

                }
                return true;
            }
        } else {

            //  depositAddress;  //地址 = reg  表示  回调金额小于 发起金额 ,  做了充值流程  然后该单作废
            if (!"reg".equals(StringUtil.isEmpty(vipPurchaseHistory.getDepositAddress()) ? "" : vipPurchaseHistory.getDepositAddress())) {
                // 查询是否首充
                List<SearchFilter> queryFilters = new ArrayList<>();
                queryFilters.add(SearchFilter.build("uid", one.getId()));
                queryFilters.add(SearchFilter.build("rechargeStatus", "3"));
                String firstCharge = this.queryAll(queryFilters).size() > 0 ? "2" : "1";
                // 创建充值记录(因为是因为购买vip的钱不够 生成的充值订单 客户收银商户后台存的是vip订单编号 所以 将vip订单编号存入充值记录)
                RechargeRecord rechargeRecord = recordInformation.addRechargeRecord(Double.parseDouble(amount), paymentChannel.getChannelName(), address, vipPurchaseHistory.getChannelType(), one.getId(), one.getSourceInvitationCode(), one.getAccount(), "3", vipPurchaseHistory.getIdw(), firstCharge);

                // 叠加总充值金额
                TotalRechargeAmount totalRechargeAmount = totalRechargeAmountService.get(SearchFilter.build("uid", one.getId()));
                if (totalRechargeAmount == null) {
                    totalRechargeAmount = new TotalRechargeAmount();
                    totalRechargeAmount.setIdw(new IdWorker().nextId() + "");
                    totalRechargeAmount.setSourceInvitationCode(one.getSourceInvitationCode());
                    totalRechargeAmount.setDzversion(0);
                    totalRechargeAmount.setUid(one.getId());
                    totalRechargeAmount.setAccount(one.getAccount());
                    totalRechargeAmount.setTotalRechargeAmount(Double.valueOf(rechargeRecord.getMoney()));
                    totalRechargeAmountService.insert(totalRechargeAmount);
                } else {
                    totalRechargeAmount.setTotalRechargeAmount(Double.parseDouble(amount) + totalRechargeAmount.getTotalRechargeAmount());
                    totalRechargeAmountService.update(totalRechargeAmount);
                }

                // 创建交易记录+余额
                recordInformation.transactionRecordPlus(rechargeRecord.getSourceInvitationCode(), rechargeRecord.getUid(), rechargeRecord.getAccount(), rechargeRecord.getPreviousBalance(), rechargeRecord.getMoney(), rechargeRecord.getAfterBalance(), rechargeRecord.getIdw(), 1, "cz", "", "");

                //  depositAddress;  //地址 = reg  表示  回调金额小于 发起金额 ,  做了充值流程  然后该单作废
                vipPurchaseHistory.setDepositAddress("reg");
                vipPurchaseHistoryService.update(vipPurchaseHistory);

                log.error(String.format("到账金额不足; 实际到账：%s", amount));
            }
            return true;
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public void notifyHandler(String orderNumber) {
        RechargeRecord rechargeRecord = rechargeRecordRepository.findByOrderNumber(orderNumber);
        if (rechargeRecord != null && "2".equals(rechargeRecord.getRechargeStatus())) {
            //验签成功
            //todo 加余额
            recordInformation.transactionRecordPlus(rechargeRecord.getSourceInvitationCode(), rechargeRecord.getUid(), rechargeRecord.getAccount(), rechargeRecord.getPreviousBalance(), rechargeRecord.getMoney(), rechargeRecord.getAfterBalance(), rechargeRecord.getIdw(), 1, "cz", "", "");
            // 充值状态 1:进行中,2:待回调,3:已完成
            recordInformation.upOkRechargeRecord(rechargeRecord);
            rechargeRecordRepository.save(rechargeRecord);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void notifyVipHandler(String idw) {
        VipPurchaseHistory vipPurchaseHistory = vipPurchaseHistoryService.get(SearchFilter.build("idw", idw));
        if (vipPurchaseHistory != null && vipPurchaseHistory.getWhetherToPay() == 1) {
            // 0支付中 1支付成功 2失败 3超时
            //如果支付金额大于等于VIP购买价格
            UserInfo one = userInfoRepository.getOne(vipPurchaseHistory.getUid());
            if (one != null) {
                //todo 加VIP返佣
                //进入套娃收益
                int buyVip = Integer.parseInt(vipPurchaseHistory.getAfterViPType().replace("v", ""));
                recordInformation.cycleReward(one, vipPurchaseHistory.getIdw(), buyVip, vipPurchaseHistory.getPreviousViPType(), vipPurchaseHistory.getAfterViPType());
                // 支付状态 1:未支付,2:已支付
                vipPurchaseHistory.setWhetherToPay(2);
                vipPurchaseHistoryService.update(vipPurchaseHistory);
                //todo jk  充电宝返佣任务
                shopService.payVipOk(vipPurchaseHistory);
                //修改用户等级
                shopService.changeUserVip(one, vipPurchaseHistory.getAfterViPType(), vipPurchaseHistory.getValidDate().toString());
            }
        }
    }

    public Ret findCountMoney(Integer rechargeStatus, String channelName, String account, String orderNumber, String expireTimes, String expireTimee, String sourceInvitationCode, String vipType, String channelType, String withdrawalAddress, String firstCharge, String countryCodeNumber) {
        Map map = rechargeRecordRepository.getMapBySql(RechargeRecordServiceSql.newSqlCountMoney(rechargeStatus, channelName, account, orderNumber, expireTimes, expireTimee, sourceInvitationCode, vipType, channelType, withdrawalAddress, firstCharge, countryCodeNumber));
        return Rets.success(map.get("countMoney") == null ? 0 : Double.valueOf(map.get("countMoney") + ""));
    }

    public Ret addRechargeFalse(FalseDataForm falseDataForm, String username) {
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
            RechargeRecord rechargeRecord = new RechargeRecord();
            rechargeRecord.setIdw(new IdWorker().nextId() + "");
            rechargeRecord.setSourceInvitationCode(userInfo.getSourceInvitationCode());
            rechargeRecord.setUid(userInfo.getId());
            rechargeRecord.setAccount(account);
            rechargeRecord.setOrderNumber(MakeOrderNum.makeOrderNum("czz", DateUtil.parseTime(dateTime)));
            rechargeRecord.setMoney(Double.valueOf(money));
            rechargeRecord.setChannelName("BiPay");
            rechargeRecord.setRechargeStatus("3");  // 充值状态 1:进行中,2:待回掉,3:已完成
            rechargeRecord.setWithdrawalAddress("");
            // 查询用户余额
            Double balance = apiUserCoom.getUserBalance(userInfo.getId()).doubleValue();

            rechargeRecord.setPreviousBalance(balance);
            rechargeRecord.setAfterBalance(BigDecimalUtils.add(balance, Double.valueOf(money)));
            rechargeRecord.setFidw(falseData.getIdw());

            rechargeRecordRepository.execute(RechargeRecordServiceSql.insertRechargeRecord(rechargeRecord, dateTime));

            recordInformation.updateUserBalance2(rechargeRecord.getSourceInvitationCode(), rechargeRecord.getUid(), account, rechargeRecord.getMoney(), 1, true);

        }
        falseData.setRemark("添加充值造假数据:成功" + count + "条,失败" + (falseDate.length - count) + "条");
        if (count > 0) {
            falseDataService.insert(falseData);
        }
        sysLogService.addSysLog(username, null, "PC", SysLogEnum.FALSE_DATE, getUsername() + "--在" + DateUtil.getTime() + "--增加充值造假数据:" + falseDate.length + "条");

        return Rets.success("添加充值造假数据:成功" + count + "条,失败" + (falseDate.length - count) + "条");
    }

    public void exportV2(HttpServletResponse response, List<Map<String, Object>> list) {
        List<RechargeRecordVo> voList = new ArrayList<>();
        for (Map<String, Object> stringObjectMap : list) {
            RechargeRecordVo vo = new RechargeRecordVo();
            BeanUtil.mapToBean(stringObjectMap, vo);
            User user = BeanUtil.objToBean(stringObjectMap.get("user"), User.class);
            vo.setUserAccount(user.getAccount());
            if (ObjectUtil.isNotEmpty(stringObjectMap.get("channelName_str"))) {
                vo.setChannelName(stringObjectMap.get("channelName_str").toString());
            }
            if (ObjectUtil.isNotEmpty(stringObjectMap.get("rechargeStatus_str"))) {
                vo.setRechargeStatusName(stringObjectMap.get("rechargeStatus_str").toString());
            }
            voList.add(vo);
        }
        EasyExcelUtil.export(response, "充值记录", voList, RechargeRecordVo.class);


    }


    public int getCount(String startTime, String endTime, String ucode, String testCode) {
        String sql = RechargeRecordServiceSql.getCount(startTime, endTime, ucode, testCode);
        Map mapBySql = rechargeRecordRepository.getMapBySql(sql);
        if (ObjectUtil.isNotEmpty(mapBySql) && ObjectUtil.isNotEmpty(mapBySql.get("count"))) {
            return Integer.parseInt(mapBySql.get("count").toString());
        }
        return 0;
    }

    public BigDecimal getSum(String startTime, String endTime, String ucode, String testCode) {
        String sql = RechargeRecordServiceSql.getSum(startTime, endTime, ucode, testCode);
        Map mapBySql = rechargeRecordRepository.getMapBySql(sql);
        if (ObjectUtil.isNotEmpty(mapBySql) && ObjectUtil.isNotEmpty(mapBySql.get("sum"))) {
            return new BigDecimal(mapBySql.get("sum").toString());
        }
        return new BigDecimal("0.00");
    }

    public Ret examine(RechargeRecord rechargeRecord, boolean isProxy, String ucode) {
        if (!"3".equals(rechargeRecord.getRechargeStatus()) && !"4".equals(rechargeRecord.getRechargeStatus())) {
            return Rets.failure("不是有效的状态值!");
        }
        if ("3".equals(rechargeRecord.getRechargeStatus()) && ObjectUtil.isEmpty(rechargeRecord.getMoney())) {
            return Rets.failure("金额不能为空");
        }
        if ("3".equals(rechargeRecord.getRechargeStatus()) && rechargeRecord.getMoney() <= 0) {
            return Rets.failure("金额输入不能小于0!");
        }
        if ("3".equals(rechargeRecord.getRechargeStatus()) && StringUtil.isEmpty(rechargeRecord.getWithdrawalAddress())) {
            return Rets.failure("充值卡号不能为空");
        }
        val update = get(rechargeRecord.getId());
        if (ObjectUtil.isEmpty(update)) {
            return Rets.failure("数据不存在!");
        }
//        if ("3".equals(rechargeRecord.getRechargeStatus()) && rechargeRecord.getMoney() > update.getMoney()) {
        if ("3".equals(rechargeRecord.getRechargeStatus()) && rechargeRecord.getMoney() > 100000) {
            return Rets.failure("放款金额超出!");
        }
        if ("4".equals(update.getRechargeStatus()) || "3".equals(update.getRechargeStatus())) {
            return Rets.failure("已经审核过!");
        }
        if ("3".equals(rechargeRecord.getRechargeStatus()) && !"bank".equals(update.getChannelType())) {
            return Rets.failure("不是可审核的充值记录!");
        }
        if ("3".equals(rechargeRecord.getRechargeStatus()) && !ucode.equals("admin")) {
            if (isProxy && !update.getSourceInvitationCode().equals(ucode)) {
                return Rets.failure("不是你代理的账号,无权限!");
            }
        }
        if ("3".equals(rechargeRecord.getRechargeStatus())) {
            update.setWithdrawalAddress(rechargeRecord.getWithdrawalAddress());
            rechargeSuccess(rechargeRecord.getMoney().toString(), update);
        }
        if ("4".equals(rechargeRecord.getRechargeStatus())) {
            update.setRechargeStatus(rechargeRecord.getRechargeStatus());
            update(update);
        }
        sysLogService.addSysLog(getUsername(), update.getId(), update.getAccount(), "PC", SysLogEnum.EXAMINE_RECHARGE_RECORD_INFO);
        return Rets.success();

    }


}

