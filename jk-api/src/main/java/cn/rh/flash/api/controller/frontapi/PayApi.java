package cn.rh.flash.api.controller.frontapi;

import cn.hutool.crypto.digest.MD5;
import cn.rh.flash.bean.dto.api.RechargeOrderDto;
import cn.rh.flash.bean.dto.api.WithdrawOrderDto;
import cn.rh.flash.bean.entity.dzsys.PaymentChannel;
import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.vo.api.RechargeOrderVo;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.cache.impl.MyRedissonLocker;
import cn.rh.flash.sdk.paymentChannel.BiPay.BipayBean.RechargeNotify;
import cn.rh.flash.sdk.paymentChannel.BiPay.BipayBean.WithdrawNotify;
import cn.rh.flash.sdk.paymentChannel.CBPay.CBPayBean.CBPayNotifyResp;
import cn.rh.flash.sdk.paymentChannel.CBPay.CBPayBean.CBPayWithdrawResp;
import cn.rh.flash.sdk.paymentChannel.FPay.FPayBean.FPayNotifyResp;
import cn.rh.flash.sdk.paymentChannel.FPay.FPayBean.FPayWithdrawResp;
import cn.rh.flash.sdk.paymentChannel.JDPay.JDPayBean.JDPayNotifyResp;
import cn.rh.flash.sdk.paymentChannel.JDPay.JDPayBean.JDPayWithdrawResp;
import cn.rh.flash.sdk.paymentChannel.KDPay.KDPayBean.KDPayNotifyResp;
import cn.rh.flash.sdk.paymentChannel.KDPay.KDPayBean.KDPayWithdrawResp;
import cn.rh.flash.sdk.paymentChannel.MYPay.MYPayBean.MYPayWithdrawNotifyResp;
import cn.rh.flash.sdk.paymentChannel.Mpay.mpayBean.MPayWithdrawResp;
import cn.rh.flash.sdk.paymentChannel.OKPay.OKPayBean.OKPayOrderDto;
import cn.rh.flash.sdk.paymentChannel.OKPay.OKPayBean.OKWdOrderDto;
import cn.rh.flash.sdk.paymentChannel.QNQBPay.QNQBPayBean.QNQBPayNotifyResp;
import cn.rh.flash.sdk.paymentChannel.QNQBPay.QNQBPayBean.QNQBPayWithdrawNotifyResp;
import cn.rh.flash.sdk.paymentChannel.WalletPay.WalletPayBean.WalletPayResp;
import cn.rh.flash.sdk.paymentChannel.Mpay.mpayBean.MPayNotifyResp;
import cn.rh.flash.sdk.paymentChannel.zimu808Pay.zimu808Bean.Zimu808RechargeNotify;
import cn.rh.flash.sdk.paymentChannel.zimu808Pay.zimu808Bean.Zimu808WithdrawNotify;
import cn.rh.flash.service.coom.dz.ApiUserCoom;
import cn.rh.flash.service.dzsys.PaymentChannelService;
import cn.rh.flash.service.dzuser.RechargeRecordService;
import cn.rh.flash.service.dzuser.WithdrawalsRecordService;
import cn.rh.flash.utils.*;
import com.aliyun.oss.common.auth.HmacSHA256Signature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;


@RestController
@RequestMapping("/api/pay")
@Api(tags = "支付接口")
@Log4j2
public class
PayApi extends ApiUserCoom {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private RechargeRecordService rechargeRecordService;

    @Autowired
    private WithdrawalsRecordService withdrawalsRecordService;

    @Autowired
    private MyRedissonLocker myRedissonLocker;

    @Autowired
    private PaymentChannelService paymentChannelService;

    @Autowired
    private ConfigCache configCache;

    @Autowired
    private RedisUtil redisUtil;

    @ApiOperationSupport(author = "yc")
    @ApiOperation(value = "创建支付订单", notes = "v1 版本")
    @PostMapping("/createRechargeOrder")
    public Ret<RechargeOrderVo> createRechargeOrder(@Valid @RequestBody RechargeOrderDto rechargeOrderDto) throws Exception {
        //同一个账号、同一个充值通道，间隔时间限制
        String time = configCache.get(ConfigKeyEnum.RECHARGE_LIMIT_TIME);
        long t=10L;
        if (RegUtil.isPlusAndNum(time)){
            t=Long.parseLong(time);
        }
        String key="recharge:"+rechargeOrderDto.getChannelName()+":"+getUserId();
        //默认10秒
        if (redisUtil.hasKey(key)){
            return Rets.failure(MessageTemplateEnum.REQUEST_LIMIT.getCode(), MessageTemplateEnum.REQUEST_LIMIT);
        }else {
            redisUtil.set(key,getAccount(),t);
        }
        return rechargeRecordService.createRechargeOrder(rechargeOrderDto, getUserId());
    }

    // 充值回调 bipay
    @ApiOperationSupport(author = "yc")
    @ApiOperation(value = "支付回调接口 BiPay", notes = "v1 版本")
    @PostMapping("/notifyRechargeOrder")
    public String notifyRechargeOrder() {
        RechargeNotify rechargeNotify = JsonUtil.fromJsonFastJSON(RechargeNotify.class, getjsonReq());

        // 枷锁防止重复调用
        String key = "bi_pay_Recharge"+rechargeNotify.getCustomOrderId();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        if (b) {
            try {
                return rechargeRecordService.notifyRechargeOrder(rechargeNotify);
            } catch (Exception e) {
                log.error("[充值]支付回调没有获取到锁等待下次回调 订单编号:" + rechargeNotify.getCustomOrderId());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        return "FAIL";
    }

    // 充值回调 walletpay
    @ApiOperationSupport(author = "skj")
    @ApiOperation(value = "支付回调接口 WalletPay", notes = "v1 版本")
    @PostMapping("/wallet/notifyRechargeOrder")
    public String walletNotifyRechargeOrder() throws Exception {

        String resp = getjsonReq();
        WalletPayResp walletPayResp = JsonUtil.fromJsonFastJSON(WalletPayResp.class,resp );

        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", "WalletPay"));
        // 验签
        String data1 = objectMapper.writeValueAsString(objectMapper.readValue(resp, Map.class).get("data"));
        HmacSHA256Signature sha256_ = new HmacSHA256Signature();
        String sign256 = sha256_.computeSignature(paymentChannel.getPrivateKey(), data1);
        sign256 = sign256.replaceAll("/", "_").replaceAll("\\+", "-");

        if (!sign256.equals(walletPayResp.getSign())) {
            log.error("[充值]支付回调验证签名失败本地签名证书等:" + paymentChannel);
            log.error("[充值]支付回调验证签名失败返回参数:" + walletPayResp);
            log.error("[充值]支付回调验证签名失败本地加密之后参数:" + sign256);
            log.error("[充值]支付回调验证签名失败完整返回参数:" + resp);
            return "FAIL";
        }
        cn.rh.flash.sdk.paymentChannel.WalletPay.WalletPayBean.RechargeNotify rechargeNotify =
                JsonUtil.fromJsonFastJSON(cn.rh.flash.sdk.paymentChannel.WalletPay.WalletPayBean.RechargeNotify.class, walletPayResp.getData());
        // 枷锁防止重复调用
        String key = "wallet_pay_Recharge"+rechargeNotify.getUser_order_id();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        if (b) {
            try {
                return rechargeRecordService.walletNotifyRechargeOrder(rechargeNotify);
            } catch (Exception e) {
                log.error("[充值]支付回调没有获取到锁等待下次回调 订单编号:" + rechargeNotify.getUser_order_id());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        return "FAIL";
    }


    @ApiOperationSupport(author = "yc")
    @ApiOperation(value = "支付VIP回调接口 BiPay", notes = "v1 版本")
    @PostMapping("/notifyRechargeOrderVIP")
    public String notifyRechargeOrderVIP() {
        RechargeNotify rechargeNotify = JsonUtil.fromJsonFastJSON(RechargeNotify.class, getjsonReq());

        // 枷锁防止重复调用
        String key = "bi_pay_Recharge_vip"+rechargeNotify.getCustomOrderId();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);

        if (b) {
            try {
                return rechargeRecordService.notifyRechargeOrderVIP(rechargeNotify);
            } catch (Exception e) {
                log.error("[VIP]支付回调没有获取到锁等待下次回调 订单编号:" + rechargeNotify.getCustomOrderId());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        return "FAIL";
    }

    @ApiOperationSupport(author = "skj")
    @ApiOperation(value = "支付VIP回调接口 WalletPay", notes = "v1 版本")
    @PostMapping("/wallet/notifyRechargeOrderVIP")
    public String walletNotifyRechargeOrderVIP() throws Exception {

        String resp = getjsonReq();
        WalletPayResp walletPayResp = JsonUtil.fromJsonFastJSON(WalletPayResp.class, resp);

        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", "WalletPay"));
        // 验签
        String data1 = objectMapper.writeValueAsString(objectMapper.readValue(resp, Map.class).get("data"));
        HmacSHA256Signature sha256_ = new HmacSHA256Signature();
        String sign256 = sha256_.computeSignature(paymentChannel.getPrivateKey(), data1);
        sign256 = sign256.replaceAll("/", "_").replaceAll("\\+", "-");

        if (!sign256.equals(walletPayResp.getSign())) {
            log.error("[wallet支付vip]支付回调验证签名失败本地签名证书等:" + paymentChannel);
            log.error("[wallet支付vip]支付回调验证签名失败返回参数:" + walletPayResp);
            log.error("[wallet支付vip]支付回调验证签名失败本地加密之后参数:" + sign256);
            log.error("[wallet支付vip]支付回调验证签名失败完整返回参数:" + resp);
            return "FAIL";
        }

        cn.rh.flash.sdk.paymentChannel.WalletPay.WalletPayBean.RechargeNotify rechargeNotify =
                JsonUtil.fromJsonFastJSON(cn.rh.flash.sdk.paymentChannel.WalletPay.WalletPayBean.RechargeNotify.class, walletPayResp.getData());
        // 枷锁防止重复调用
        String key = "wallet_pay_Recharge_vip"+rechargeNotify.getUser_order_id();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        if (b) {
            try {
                return rechargeRecordService.walletNotifyRechargeOrderVIP(rechargeNotify);
            } catch (Exception e) {
                log.error("[VIP]支付回调没有获取到锁等待下次回调 订单编号:" + rechargeNotify.getUser_order_id());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        return "FAIL";
    }


    @ApiOperationSupport(author = "yc")
    @ApiOperation(value = "创建提现订单", notes = "v1 版本")
    @PostMapping("/createWithdrawOrder")
    public Ret createWithdrawOrder(@Valid @RequestBody WithdrawOrderDto withdrawOrderDto)  {
        Long userId = getUserId();
        // 枷锁防止重复调用
        String key = "createWithdrawOrder"+userId;
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.lock(lock);
        if (b) {
            try {
                return withdrawalsRecordService.createWithdrawOrder(withdrawOrderDto, userId);
            } catch (Exception e) {
                log.error("创建提现订单为获取到锁 用户ID:" + userId);
            } finally {
                myRedissonLocker.unlock(lock);
            }

        }
        return Rets.failure(MessageTemplateEnum.WITHDRAW_CREATE_ERROR.getCode());
    }

    @ApiOperation(value = "创建提现订单v2--kdpay", notes = "v2 版本")
    @PostMapping("/createWithdrawOrderV2")
    public Ret createWithdrawOrderV2(@Valid @RequestBody WithdrawOrderDto withdrawOrderDto) {
        Long userId = getUserId();
        // 枷锁防止重复调用
        String key = "createWithdrawOrderV2_"+userId;
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.lock(lock);
        if (b) {
            try {
                return withdrawalsRecordService.createWithdrawOrderV2(withdrawOrderDto, userId);
            } catch (Exception e) {
                log.error("创建"+withdrawOrderDto.getChannelName()+"提现订单未获取到锁 用户ID:" + userId);
            } finally {
                myRedissonLocker.unlock(lock);
            }

        }
        return Rets.failure(MessageTemplateEnum.WITHDRAW_CREATE_ERROR.getCode());
    }

    @ApiOperationSupport(author = "yc")
    @ApiOperation(value = "提现回调接口 BiPay", notes = "v1 版本")
    @PostMapping("/notifyWithdrawOrder")
    public String notifyWithdrawOrder() {
        WithdrawNotify withdrawNotify = JsonUtil.fromJsonFastJSON(WithdrawNotify.class, getjsonReq());
        // 枷锁防止重复调用
        String key = "pay_withdraw_" + withdrawNotify.getCustomOrderId();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        if (b) {
            try {
                // 验签
                return withdrawalsRecordService.notifyWithdrawOrder(withdrawNotify);
            } catch (Exception e) {
                log.error("[提现]bipay提现回调没有获取到锁等待下次回调 订单编号:" + withdrawNotify.getCustomOrderId());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        return "FAIL";
    }

    @ApiOperationSupport(author = "skj")
    @ApiOperation(value = "提现回调接口 WalletPay", notes = "v1 版本")
    @PostMapping("/wallet/notifyWithdrawOrder")
    public String walletNotifyWithdrawOrder() throws Exception {
        String resp = getjsonReq();

        WalletPayResp walletPayResp = JsonUtil.fromJsonFastJSON(WalletPayResp.class, resp);

        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", "WalletPay"));
        // 验签
        String data1 = objectMapper.writeValueAsString(objectMapper.readValue(resp, Map.class).get("data"));
        HmacSHA256Signature sha256_ = new HmacSHA256Signature();
        String sign256 = sha256_.computeSignature(paymentChannel.getPrivateKey(), data1);
        sign256 = sign256.replaceAll("/", "_").replaceAll("\\+", "-");

        if (!sign256.equals(walletPayResp.getSign())) {
            log.error("[wallet提现]支付回调验证签名失败本地签名证书等:" + paymentChannel);
            log.error("[wallet提现]支付回调验证签名失败返回参数:" + walletPayResp);
            log.error("[wallet提现]支付回调验证签名失败本地加密之后参数:" + sign256);
            log.error("[wallet提现]支付回调验证签名失败完整返回参数:" + resp);
            return "FAIL";
        }
        cn.rh.flash.sdk.paymentChannel.WalletPay.WalletPayBean.WithdrawNotify withdrawNotify
                = JsonUtil.fromJsonFastJSON(cn.rh.flash.sdk.paymentChannel.WalletPay.WalletPayBean.WithdrawNotify.class, walletPayResp.getData());
        // 枷锁防止重复调用
        String key = "pay_withdraw_" + withdrawNotify.getUser_withdrawal_id();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        if (b) {
            try {
                // 验签
                return withdrawalsRecordService.walletNotifyWithdrawOrder(withdrawNotify);
            } catch (Exception e) {
                log.error("[提现]WalletPay提现回调没有获取到锁等待下次回调 订单编号:" + withdrawNotify.getUser_withdrawal_id());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        return "FAIL";
    }

    // 充值回调 kdpay
    @ApiOperation(value = "支付回调接口 KDPay", notes = "v1 版本")
    @PostMapping("/kdpay/notifyRechargeOrder")
    public String kdpayNotifyRechargeOrder(@RequestParam Map<String, String> KDPayNotify) {
        log.info("KDPay支付回调-----------------------");
        KDPayNotifyResp rechargeNotify = JsonUtil.fromJsonFastJSON(KDPayNotifyResp.class, JsonUtil.toJson(KDPayNotify));
        if (KDPayNotify==null||rechargeNotify==null){
            log.error("KDPay充值回调信息空,:[{}]", KDPayNotify);
            return "FAIL";
        }
        log.info("kdpay充值回调："+rechargeNotify.toString());
        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", "KDPay"));
//        if (paymentChannel == null||!rechargeNotify.getUserCode().equals(paymentChannel.getCurrencyCode())) {
        if (paymentChannel == null) {
            log.error("KDPay通道信息不存在,:[{}]", rechargeNotify.toString());
            return "FAIL";
        }
        // 验签
        String sign = rechargeNotify.toSign(paymentChannel.getPrivateKey());
        System.out.println("sign:--->" + sign);
        String s = MD5.create().digestHex(sign).toUpperCase();
        System.out.println("s:--->" + s);
        if (!rechargeNotify.getSign().equals(s)) {
            log.error("KDPay充值验签错误,:[{}]", KDPayNotify);
            return "SIGN_FAIL";
        }
        // 枷锁防止重复调用
        String key = "KDPay_Recharge_"+rechargeNotify.getOrderCode();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        if (b) {
            try {
                return rechargeRecordService.kdpayNotifyRechargeOrder(rechargeNotify,paymentChannel);
            } catch (Exception e) {
                log.error("[充值]支付回调没有获取到锁等待下次回调 订单编号:" + rechargeNotify.getOrderCode());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        log.error(rechargeNotify.getOrderCode()+"---last--->"+rechargeNotify.getStatus());
//        System.out.println(rechargeNotify.getOrderCode()+"---last--->"+rechargeNotify.getStatus());
        return "FAIL";
    }

    // 充值回调 qnqbpay
    @ApiOperation(value = "支付回调接口 QNQBPay", notes = "v1 版本")
    @PostMapping("/qnqbpay/notifyRechargeOrder")
    public String qnqbpayNotifyRechargeOrder(@RequestParam Map<String, String> QNQBPayNotify) {
        log.info("QNQBPay支付回调-----------------------");
        QNQBPayNotifyResp rechargeNotify = JsonUtil.fromJsonFastJSON(QNQBPayNotifyResp.class, JsonUtil.toJson(QNQBPayNotify));
        if (QNQBPayNotify==null||rechargeNotify==null){
            log.error("QNQBPay充值回调信息空,:[{}]", QNQBPayNotify);
            return "FAIL";
        }
        log.info("qnqbpay充值回调："+rechargeNotify);
        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", "QNQBPay"));
//        if (paymentChannel == null||!rechargeNotify.getUserCode().equals(paymentChannel.getCurrencyCode())) {
        if (paymentChannel == null) {
            log.error("QNQBPay通道信息不存在,:[{}]", rechargeNotify.toString());
            return "FAIL";
        }
        // 验签
        String sign = rechargeNotify.getSign()+paymentChannel.getPrivateKey();
        System.out.println("sign:--->" + sign);
        String s = MD5.create().digestHex(sign).toLowerCase();
        System.out.println("s:--->" + s);
        if (!rechargeNotify.getRetsign().equals(s)) {
            log.error("QNQBPay充值验签错误,:[{}]", QNQBPayNotify);
            return "SIGN_FAIL";
        }
        // 枷锁防止重复调用
        String key = "QNQBPay_Recharge_"+rechargeNotify.getOrderid();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        if (b) {
            try {
                return rechargeRecordService.qnqbpayNotifyRechargeOrder(rechargeNotify,paymentChannel);
            } catch (Exception e) {
                log.error("[QNQB充值]支付回调没有获取到锁等待下次回调 订单编号:" + rechargeNotify.getOrderid());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        log.error(rechargeNotify.getOrderid()+"---last--->"+rechargeNotify.getStatus());
//        System.out.println(rechargeNotify.getOrderCode()+"---last--->"+rechargeNotify.getStatus());
        return "FAIL";
    }

    // 充值回调 fpay
    @ApiOperation(value = "支付回调接口 FPay", notes = "v1 版本")
    @PostMapping("/fpay/notifyRechargeOrder")
    public String fpayNotifyRechargeOrder(@RequestParam Map<String, String> FPayNotify) {
        log.info("FPay支付回调-----------------------");
        FPayNotifyResp rechargeNotify = JsonUtil.fromJsonFastJSON(FPayNotifyResp.class, JsonUtil.toJson(FPayNotify));
        if (FPayNotify==null||rechargeNotify==null){
            log.error("FPay充值回调信息空,:[{}]", FPayNotify);
            return "FAIL";
        }
        log.info("fpay充值回调："+rechargeNotify);
        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", "FPay"));
//        if (paymentChannel == null||!rechargeNotify.getUserCode().equals(paymentChannel.getCurrencyCode())) {
        if (paymentChannel == null) {
            log.error("FPay通道信息不存在,:[{}]", rechargeNotify);
            return "FAIL";
        }
        // 验签
        String sign = rechargeNotify.toSign(paymentChannel.getPrivateKey());
        System.out.println("sign:--->" + sign);
        String s = MD5.create().digestHex(sign).toUpperCase();
        if (!rechargeNotify.getSign().equals(s)) {
            log.error("FPay充值验签错误,:[{}]", FPayNotify);
            return "SIGN_FAIL";
        }
        // 枷锁防止重复调用
        String key = "FPay_Recharge_"+rechargeNotify.getOrderid();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        if (b) {
            try {
                return rechargeRecordService.fpayNotifyRechargeOrder(rechargeNotify,paymentChannel);
            } catch (Exception e) {
                log.error("[充值]支付回调没有获取到锁等待下次回调 订单编号:" + rechargeNotify.getOrderid());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        log.error(rechargeNotify.getOrderid()+"---last--->"+rechargeNotify.getState());
//        System.out.println(rechargeNotify.getOrderCode()+"---last--->"+rechargeNotify.getStatus());
        return "FAIL";
    }


    // 充值回调 jdpay
    @ApiOperation(value = "支付回调接口 JDPay", notes = "v1 版本")
    @PostMapping("/jdpay/notifyRechargeOrder")
    public String jdpayNotifyRechargeOrder(@RequestParam Map<String, String> JDPayNotify) {
        log.info("JDPay支付回调-----------------------");
        JDPayNotifyResp rechargeNotify = JsonUtil.fromJsonFastJSON(JDPayNotifyResp.class, JsonUtil.toJson(JDPayNotify));
        if (JDPayNotify==null||rechargeNotify==null){
            log.error("JDPay充值回调信息空,:[{}]", JDPayNotify);
            return "FAIL";
        }
        log.info("Jdpay充值回调："+rechargeNotify.toString());
        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", "JDPay"));
//        if (paymentChannel == null||!rechargeNotify.getUserCode().equals(paymentChannel.getCurrencyCode())) {
        if (paymentChannel == null) {
            log.error("JDPay通道信息不存在,:[{}]", rechargeNotify.toString());
            return "FAIL";
        }
        // 验签
        String sign = rechargeNotify.toSign(paymentChannel.getPrivateKey());
        System.out.println("sign:--->" + sign);
        String s = MD5.create().digestHex(sign).toUpperCase();
        System.out.println("s:--->" + s);
        if (!rechargeNotify.getSign().equals(s)) {
            log.error("JDPay充值验签错误,:[{}]", JDPayNotify);
            return "SIGN_FAIL";
        }
        // 枷锁防止重复调用
        String key = "JDPay_Recharge_"+rechargeNotify.getOrderCode();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        if (b) {
            try {
                return rechargeRecordService.jdpayNotifyRechargeOrder(rechargeNotify,paymentChannel);
            } catch (Exception e) {
                log.error("[充值]支付回调没有获取到锁等待下次回调 订单编号:" + rechargeNotify.getOrderCode());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        log.error(rechargeNotify.getOrderCode()+"---last--->"+rechargeNotify.getStatus());
//        System.out.println(rechargeNotify.getOrderCode()+"---last--->"+rechargeNotify.getStatus());
        return "FAIL";
    }

    // 充值回调 cbpay
    @ApiOperation(value = "支付回调接口 CBPay", notes = "v1 版本")
    @PostMapping("/cbpay/notifyRechargeOrder")
    public String cbpayNotifyRechargeOrder(@RequestParam Map<String, String> CBPayNotify) {
        log.info("CBPay支付回调-----------------------");
        CBPayNotifyResp rechargeNotify = JsonUtil.fromJsonFastJSON(CBPayNotifyResp.class, JsonUtil.toJson(CBPayNotify));
        if (CBPayNotify==null||rechargeNotify==null){
            log.error("CBPay充值回调信息空,:[{}]", CBPayNotify);
            return "FAIL";
        }
        log.info("cbpay充值回调："+rechargeNotify.toString());
        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", "CBPay"));
//        if (paymentChannel == null||!rechargeNotify.getUserCode().equals(paymentChannel.getCurrencyCode())) {
        if (paymentChannel == null) {
            log.error("CBPay通道信息不存在,:[{}]", rechargeNotify.toString());
            return "FAIL";
        }
        // 验签
        String sign = rechargeNotify.toSign(paymentChannel.getPrivateKey());
        System.out.println("sign:--->" + sign);
        String s = MD5.create().digestHex(sign).toUpperCase();
        System.out.println("s:--->" + s);
        if (!rechargeNotify.getSign().equals(s)) {
            log.error("CBPay充值验签错误,:[{}]", CBPayNotify);
            return "SIGN_FAIL";
        }
        // 枷锁防止重复调用
        String key = "CBPay_Recharge_"+rechargeNotify.getOrderCode();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        if (b) {
            try {
                return rechargeRecordService.cbpayNotifyRechargeOrder(rechargeNotify,paymentChannel);
            } catch (Exception e) {
                log.error("[充值]支付回调没有获取到锁等待下次回调 订单编号:" + rechargeNotify.getOrderCode());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        log.error(rechargeNotify.getOrderCode()+"---last--->"+rechargeNotify.getStatus());
//        System.out.println(rechargeNotify.getOrderCode()+"---last--->"+rechargeNotify.getStatus());
        return "FAIL";
    }

    // 充值回调 cbpay
    @ApiOperation(value = "支付回调接口 MPay", notes = "v1 版本")
    @PostMapping("/mpay/notifyRechargeOrder")
    public String mpayNotifyRechargeOrder(@RequestParam Map<String, String> MPayNotify) {
        log.info("MPay支付回调-----------------------");
        MPayNotifyResp rechargeNotify = JsonUtil.fromJsonFastJSON(MPayNotifyResp.class, JsonUtil.toJson(MPayNotify));
        if (MPayNotify==null||rechargeNotify==null){
            log.error("MPay充值回调信息空,:[{}]", MPayNotify);
            return "FAIL";
        }
        log.info("mpay充值回调："+rechargeNotify);
        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", "MPay"));
        if (paymentChannel == null) {
            log.error("MPay通道信息不存在,:[{}]", rechargeNotify.toString());
            return "FAIL";
        }
        // 验签
        String sign = rechargeNotify.toSign(paymentChannel.getPrivateKey());
        System.out.println("sign:--->" + sign);
//        String s = MD5.create().digestHex(sign).toUpperCase();
        //这个接口的签名验证是md5小写
        String s = MD5.create().digestHex(sign).toLowerCase();
//        String s = MD5Utils.hash(sign);
        System.out.println("s:--->" + s);
        if (!rechargeNotify.getSign().equals(s)) {
            log.error("MPay充值验签错误,:[{}]", MPayNotify);
            return "SIGN_FAIL";
        }
        // 枷锁防止重复调用
        String key = "MPay_Recharge_"+rechargeNotify.getMerchOrderId();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        if (b) {
            try {
                return rechargeRecordService.mpayNotifyRechargeOrder(rechargeNotify,paymentChannel);
            } catch (Exception e) {
                log.error("[mpay充值]支付回调没有获取到锁等待次下回调 订单编号:" + rechargeNotify.getMerchOrderId());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        log.error(rechargeNotify+"---last--->"+rechargeNotify.getStatus());
//        System.out.println(rechargeNotify.getOrderCode()+"---last--->"+rechargeNotify.getStatus());
        return "FAIL";
    }

    //支付VIP回调KDPAY
    @ApiOperation(value = "支付VIP回调接口 KDPay", notes = "v1 版本")
    @PostMapping("/kdpay/notifyRechargeOrderVIP")
    public String kdpayNNotifyRechargeOrderVIP(@RequestParam Map<String, String> KDPayNotify) {
        log.info("KDPay支付VIP回调-----------------------");
        KDPayNotifyResp rechargeNotify = JsonUtil.fromJsonFastJSON(KDPayNotifyResp.class, JsonUtil.toJson(KDPayNotify));
        if (KDPayNotify==null||rechargeNotify==null){
            log.error("KDPay支付VIP回调信息空,:[{}]", KDPayNotify);
            return "FAIL";
        }
        log.info("kdpay充值VIP回调："+rechargeNotify.toString());
        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", "KDPay"));
        if (paymentChannel == null) {
            log.error("KDPay通道信息不存在,:[{}]",rechargeNotify.toString());
            return "FAIL";
        }
        // 验签
        String sign = rechargeNotify.toSign(paymentChannel.getPrivateKey());
        System.out.println("sign:--->" + sign);
        String s = MD5.create().digestHex(sign).toUpperCase();
        System.out.println("s:--->" + s);
        if (!rechargeNotify.getSign().equals(s)) {
            log.error("KDPay支付VIP验签错误,:[{}]", KDPayNotify);
            return "SIGN_FAIL";
        }

        // 枷锁防止重复调用
        String key = "KDPay_Recharge_vip_"+rechargeNotify.getOrderCode();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        if (b) {
            try {
                return rechargeRecordService.kdpayNotifyRechargeOrderVIP(rechargeNotify,paymentChannel);
            } catch (Exception e) {
                log.error("[VIP]KDPay支付回调没有获取到锁等待下次回调 订单编号:" + rechargeNotify.getOrderCode());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        log.error(rechargeNotify.getOrderCode()+"---last--->"+rechargeNotify.getStatus());
        return "FAIL";
    }


    //支付VIP回调QNQBPAY
    @ApiOperation(value = "支付VIP回调接口 QNQBPay", notes = "v1 版本")
    @PostMapping("/qnqbpay/notifyRechargeOrderVIP")
    public String qnqbpayNNotifyRechargeOrderVIP(@RequestParam Map<String, String> QNQBPayNotify) {
        log.info("QNQBPay支付VIP回调-----------------------");
        QNQBPayNotifyResp rechargeNotify = JsonUtil.fromJsonFastJSON(QNQBPayNotifyResp.class, JsonUtil.toJson(QNQBPayNotify));
        if (QNQBPayNotify==null||rechargeNotify==null){
            log.error("QNQBPay支付VIP回调信息空,:[{}]", QNQBPayNotify);
            return "FAIL";
        }
        log.info("qnqbpay充值VIP回调："+rechargeNotify);
        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", "QNQBPay"));
        if (paymentChannel == null) {
            log.error("QNQBPay通道信息不存在,:[{}]",rechargeNotify.toString());
            return "FAIL";
        }
        // 验签
        String sign = rechargeNotify.getSign()+paymentChannel.getPrivateKey();
        System.out.println("sign:--->" + sign);
        String s = MD5.create().digestHex(sign).toLowerCase();
        System.out.println("s:--->" + s);
        if (!rechargeNotify.getSign().equals(s)) {
            log.error("QNQBPay支付VIP验签错误,:[{}]", QNQBPayNotify);
            return "SIGN_FAIL";
        }

        // 枷锁防止重复调用
        String key = "QNQBPay_Recharge_vip_"+rechargeNotify.getOrderid();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        if (b) {
            try {
                return rechargeRecordService.qnqbpayNotifyRechargeOrderVIP(rechargeNotify,paymentChannel);
            } catch (Exception e) {
                log.error("[VIP]QNQBPay支付回调没有获取到锁等待下次回调 订单编号:" + rechargeNotify.getOrderid());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        log.error(rechargeNotify.getOrderid()+"---last--->"+rechargeNotify.getStatus());
        return "FAIL";
    }


    //支付VIP回调KDPAY
    @ApiOperation(value = "支付VIP回调接口 FPay", notes = "v1 版本")
    @PostMapping("/fpay/notifyRechargeOrderVIP")
    public String fpayNNotifyRechargeOrderVIP(@RequestParam Map<String, String> FPayNotify) {
        log.info("FPay支付VIP回调-----------------------");
        FPayNotifyResp rechargeNotify = JsonUtil.fromJsonFastJSON(FPayNotifyResp.class, JsonUtil.toJson(FPayNotify));
        if (FPayNotify==null||rechargeNotify==null){
            log.error("FPay支付VIP回调信息空,:[{}]", FPayNotify);
            return "FAIL";
        }
        log.info("fpay充值VIP回调："+rechargeNotify);
        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", "FPay"));
        if (paymentChannel == null) {
            log.error("KDPay通道信息不存在,:[{}]",rechargeNotify.toString());
            return "FAIL";
        }
        // 验签
        String sign = rechargeNotify.toSign(paymentChannel.getPrivateKey());
        System.out.println("sign:--->" + sign);
        String s = MD5.create().digestHex(sign).toUpperCase();
        System.out.println("s:--->" + s);
        if (!rechargeNotify.getSign().equals(s)) {
            log.error("FPay支付VIP验签错误,:[{}]", FPayNotify);
            return "SIGN_FAIL";
        }

        // 枷锁防止重复调用
        String key = "FPay_Recharge_vip_"+rechargeNotify.getOrderid();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        if (b) {
            try {
                return rechargeRecordService.fpayNotifyRechargeOrderVIP(rechargeNotify,paymentChannel);
            } catch (Exception e) {
                log.error("[VIP]FPay支付回调没有获取到锁等待下次回调 订单编号:" + rechargeNotify.getOrderid());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        log.error(rechargeNotify.getOrderid()+"---last--->"+rechargeNotify.getState());
        return "FAIL";
    }


    //支付VIP回调JDPAY
    @ApiOperation(value = "支付VIP回调接口 JDPay", notes = "v1 版本")
    @PostMapping("/jdpay/notifyRechargeOrderVIP")
    public String jdpayNNotifyRechargeOrderVIP(@RequestParam Map<String, String> JDPayNotify) {
        log.info("JDPay支付VIP回调-----------------------");
        JDPayNotifyResp rechargeNotify = JsonUtil.fromJsonFastJSON(JDPayNotifyResp.class, JsonUtil.toJson(JDPayNotify));
        if (JDPayNotify==null||rechargeNotify==null){
            log.error("JDPay支付VIP回调信息空,:[{}]", JDPayNotify);
            return "FAIL";
        }
        log.info("Jdpay充值VIP回调："+rechargeNotify.toString());
        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", "JDPay"));
        if (paymentChannel == null) {
            log.error("JDPay通道信息不存在,:[{}]",rechargeNotify.toString());
            return "FAIL";
        }
        // 验签
        String sign = rechargeNotify.toSign(paymentChannel.getPrivateKey());
        System.out.println("sign:--->" + sign);
        String s = MD5.create().digestHex(sign).toUpperCase();
        System.out.println("s:--->" + s);
        if (!rechargeNotify.getSign().equals(s)) {
            log.error("JDPay支付VIP验签错误,:[{}]", JDPayNotify);
            return "SIGN_FAIL";
        }

        // 枷锁防止重复调用
        String key = "JDPay_Recharge_vip_"+rechargeNotify.getOrderCode();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        if (b) {
            try {
                return rechargeRecordService.jdpayNotifyRechargeOrderVIP(rechargeNotify,paymentChannel);
            } catch (Exception e) {
                log.error("[VIP]JDPay支付回调没有获取到锁等待下次回调 订单编号:" + rechargeNotify.getOrderCode());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        log.error(rechargeNotify.getOrderCode()+"---last--->"+rechargeNotify.getStatus());
        return "FAIL";
    }

    //支付VIP回调CBPAY
    @ApiOperation(value = "支付VIP回调接口 CBPay", notes = "v1 版本")
    @PostMapping("/cbpay/notifyRechargeOrderVIP")
    public String cbpayNNotifyRechargeOrderVIP(@RequestParam Map<String, String> CBPayNotify) {
        log.info("CBPay支付VIP回调-----------------------");
        CBPayNotifyResp rechargeNotify = JsonUtil.fromJsonFastJSON(CBPayNotifyResp.class, JsonUtil.toJson(CBPayNotify));
        if (CBPayNotify==null||rechargeNotify==null){
            log.error("CBPay支付VIP回调信息空,:[{}]", CBPayNotify);
            return "FAIL";
        }
        log.info("cbpay充值VIP回调："+rechargeNotify.toString());
        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", "CBPay"));
        if (paymentChannel == null) {
            log.error("CBPay通道信息不存在,:[{}]",rechargeNotify.toString());
            return "FAIL";
        }
        // 验签
        String sign = rechargeNotify.toSign(paymentChannel.getPrivateKey());
        System.out.println("sign:--->" + sign);
        String s = MD5.create().digestHex(sign).toUpperCase();
        System.out.println("s:--->" + s);
        if (!rechargeNotify.getSign().equals(s)) {
            log.error("CBPay支付VIP验签错误,:[{}]", CBPayNotify);
            return "SIGN_FAIL";
        }

        // 枷锁防止重复调用
        String key = "CBPay_Recharge_vip_"+rechargeNotify.getOrderCode();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        if (b) {
            try {
                return rechargeRecordService.cbpayNotifyRechargeOrderVIP(rechargeNotify,paymentChannel);
            } catch (Exception e) {
                log.error("[VIP]CBPay支付回调没有获取到锁等待下次回调 订单编号:" + rechargeNotify.getOrderCode());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        log.error(rechargeNotify.getOrderCode()+"---last--->"+rechargeNotify.getStatus());
        return "FAIL";
    }


    //支付VIP回调CBPAY
    @ApiOperation(value = "支付VIP回调接口 MPay", notes = "v1 版本")
    @PostMapping("/mpay/notifyRechargeOrderVIP")
    public String mpayNotifyRechargeOrderVIP(@RequestParam Map<String, String> MPayNotify) {
        log.info("MPay支付VIP回调-----------------------");
        MPayNotifyResp rechargeNotify = JsonUtil.fromJsonFastJSON(MPayNotifyResp.class, JsonUtil.toJson(MPayNotify));
        if (MPayNotify==null||rechargeNotify==null){
            log.error("MPay支付VIP回调信息空,:[{}]", MPayNotify);
            return "FAIL";
        }
        log.info("cbpay充值VIP回调："+rechargeNotify.toString());
        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", "MPay"));
        if (paymentChannel == null) {
            log.error("MPay通道信息不存在,:[{}]",rechargeNotify.toString());
            return "FAIL";
        }
        // 验签
        String sign = rechargeNotify.toSign(paymentChannel.getPrivateKey());
        System.out.println("sign:--->" + sign);
//        String s = MD5.create().digestHex(sign).toUpperCase();
//        String s = MD5Utils.hash(sign);
        String s = MD5.create().digestHex(sign).toLowerCase();
        System.out.println("s:--->" + s);
        if (!rechargeNotify.getSign().equals(s)) {
            log.error("MPay支付VIP验签错误,:[{}]", MPayNotify);
            return "SIGN_FAIL";
        }

        // 枷锁防止重复调用
        String key = "MPay_Recharge_vip_"+rechargeNotify.getMerchOrderId();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        if (b) {
            try {
                return rechargeRecordService.mpayNotifyRechargeOrderVIP(rechargeNotify,paymentChannel);
            } catch (Exception e) {
                log.error("[VIP]MPay支付回调没有获取到锁等待下次回调 订单编号:" + rechargeNotify.getMerchOrderId());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        log.error(rechargeNotify.getMerchOrderId()+"---last--->"+rechargeNotify.getStatus());
        return "FAIL";
    }

    @ApiOperation(value = "提现回调接口 KDPay", notes = "v1 版本")
    @PostMapping("/kdpay/notifyWithdrawOrder")
    public String kdpayNotifyWithdrawOrder(@RequestParam Map<String, String> KDPayNotify){
        log.info("KDPay提现回调-----------------------");
        KDPayWithdrawResp withdrawResp = JsonUtil.fromJsonFastJSON(KDPayWithdrawResp.class, JsonUtil.toJson(KDPayNotify));
        if (KDPayNotify==null||withdrawResp==null){
            log.error("KDPay提现回调空,:[{}]", KDPayNotify);
            return "FAIL";
        }
        log.info("kdpay提现回调："+withdrawResp.toString());
        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", "KDPay"));
        // 验签
        String sign = withdrawResp.toSign(paymentChannel.getPrivateKey());
        System.out.println("sign:--->" + sign);
        String s = MD5.create().digestHex(sign).toUpperCase();
        System.out.println("s:--->" + s);
        if (!withdrawResp.getSign().equals(s)) {
            log.error("KDPay提现回调验签错误,:[{}]", KDPayNotify);
            return "SIGN_FAIL";
        }
        // 枷锁防止重复调用
        String key = "kdpay_withdraw_" + withdrawResp.getOrderCode();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        if (b) {
            try {
                // 验签
                return withdrawalsRecordService.kdpayNotifyWithdrawOrder(withdrawResp,paymentChannel);
            } catch (Exception e) {
                log.error("[提现]KDPay提现回调没有获取到锁等待下次回调 订单编号:" + withdrawResp.getOrderCode());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }

        log.error(withdrawResp.getOrderCode()+"---last--->"+withdrawResp.getStatus());
//        System.out.println("last--->"+withdrawResp.getStatus());
        return "FAIL";
    }



    @ApiOperation(value = "提现回调接口 QNQBPay", notes = "v1 版本")
    @PostMapping("/qnqbpay/notifyWithdrawOrder")
    public String qnqbpayNotifyWithdrawOrder(@RequestParam Map<String, String> QNQBPayNotify){
        log.info("QNQBPay提现回调-----------------------");
        QNQBPayWithdrawNotifyResp withdrawResp = JsonUtil.fromJsonFastJSON(QNQBPayWithdrawNotifyResp.class, JsonUtil.toJson(QNQBPayNotify));
        if (QNQBPayNotify==null||withdrawResp==null){
            log.error("QNQBPay提现回调空,:[{}]", QNQBPayNotify);
            return "FAIL";
        }
        log.info("qnqbpay提现回调："+withdrawResp);
        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", "QNQBPay"));
        // 验签
        String sign = withdrawResp.getRecvid()+"&"+withdrawResp.getOrderid()+"&"+withdrawResp.getAmount()+"&"+paymentChannel.getPrivateKey();
        System.out.println("sign:--->" + sign);
        String s = MD5.create().digestHex(sign).toLowerCase();
        System.out.println("s:--->" + s);
        if (!withdrawResp.getSign().equals(s)) {
            log.error("QNQBPay提现回调验签错误,:[{}]", QNQBPayNotify);
            return "SIGN_FAIL";
        }
        // 枷锁防止重复调用
        String key = "qnqbpay_withdraw_" + withdrawResp.getOrderid();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        if (b) {
            try {
                // 验签
                return withdrawalsRecordService.qnqbpayNotifyWithdrawOrder(withdrawResp,paymentChannel);
            } catch (Exception e) {
                log.error("[提现]QNQBPay提现回调没有获取到锁等待下次回调 订单编号:" + withdrawResp.getOrderid());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }

        log.error(withdrawResp.getOrderid()+"---last--->"+withdrawResp.getStatus());
//        System.out.println("last--->"+withdrawResp.getStatus());
        return "FAIL";
    }


    @ApiOperation(value = "提现回调接口 FPay", notes = "v1 版本")
    @PostMapping("/fpay/notifyWithdrawOrder")
    public String fpayNotifyWithdrawOrder(@RequestParam Map<String, String> FPayNotify){
        log.info("FPay提现回调-----------------------");
        FPayWithdrawResp withdrawResp = JsonUtil.fromJsonFastJSON(FPayWithdrawResp.class, JsonUtil.toJson(FPayNotify));
        if (FPayNotify==null||withdrawResp==null){
            log.error("FPay提现回调空,:[{}]", FPayNotify);
            return "FAIL";
        }
        log.info("fpay提现回调："+withdrawResp);
        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", "FPay"));
        // 验签
        String sign = withdrawResp.toSign(paymentChannel.getPrivateKey());
        System.out.println("sign:--->" + sign);
        String s = MD5.create().digestHex(sign).toUpperCase();
        System.out.println("s:--->" + s);
        if (!withdrawResp.getSign().equals(s)) {
            log.error("FPay提现回调验签错误,:[{}]", FPayNotify);
            return "SIGN_FAIL";
        }
        // 枷锁防止重复调用
        String key = "fpay_withdraw_" + withdrawResp.getOrderid();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        if (b) {
            try {
                // 验签
                return withdrawalsRecordService.fpayNotifyWithdrawOrder(withdrawResp,paymentChannel);
            } catch (Exception e) {
                log.error("[提现]FPay提现回调没有获取到锁等待下次回调 订单编号:" + withdrawResp.getOrderid());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }

        log.error(withdrawResp.getOrderid()+"---last--->"+withdrawResp.getState());
//        System.out.println("last--->"+withdrawResp.getStatus());
        return "FAIL";
    }


    @ApiOperation(value = "提现回调接口 JDPay", notes = "v1 版本")
    @PostMapping("/jdpay/notifyWithdrawOrder")
    public String jdpayNotifyWithdrawOrder(@RequestParam Map<String, String> JDPayNotify){
        log.info("JDPay提现回调-----------------------");
        JDPayWithdrawResp withdrawResp = JsonUtil.fromJsonFastJSON(JDPayWithdrawResp.class, JsonUtil.toJson(JDPayNotify));
        if (JDPayNotify==null||withdrawResp==null){
            log.error("JDPay提现回调空,:[{}]", JDPayNotify);
            return "FAIL";
        }
        log.info("Jdpay提现回调："+withdrawResp.toString());
        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", "JDPay"));
        // 验签
        String sign = withdrawResp.toSign(paymentChannel.getPrivateKey());
        System.out.println("sign:--->" + sign);
        String s = MD5.create().digestHex(sign).toUpperCase();
        System.out.println("s:--->" + s);
        if (!withdrawResp.getSign().equals(s)) {
            log.error("JDPay提现回调验签错误,:[{}]", JDPayNotify);
            return "SIGN_FAIL";
        }
        // 枷锁防止重复调用
        String key = "jdpay_withdraw_" + withdrawResp.getOrderCode();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        if (b) {
            try {
                // 验签
                return withdrawalsRecordService.jdpayNotifyWithdrawOrder(withdrawResp,paymentChannel);
            } catch (Exception e) {
                log.error("[提现]JDPay提现回调没有获取到锁等待下次回调 订单编号:" + withdrawResp.getOrderCode());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }

        log.error(withdrawResp.getOrderCode()+"---last--->"+withdrawResp.getStatus());
//        System.out.println("last--->"+withdrawResp.getStatus());
        return "FAIL";
    }

    @ApiOperation(value = "提现回调接口 CBPay", notes = "v1 版本")
    @PostMapping("/cbpay/notifyWithdrawOrder")
    public String cbpayNotifyWithdrawOrder(@RequestParam Map<String, String> CBPayNotify){
        log.info("CBPay提现回调-----------------------");
        CBPayWithdrawResp withdrawResp = JsonUtil.fromJsonFastJSON(CBPayWithdrawResp.class, JsonUtil.toJson(CBPayNotify));
        if (CBPayNotify==null||withdrawResp==null){
            log.error("CBPay提现回调空,:[{}]", CBPayNotify);
            return "FAIL";
        }
        log.info("CBpay提现回调："+withdrawResp.toString());
        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", "CBPay"));
        // 验签
        String sign = withdrawResp.toSign(paymentChannel.getPrivateKey());
        System.out.println("sign:--->" + sign);
        String s = MD5.create().digestHex(sign).toUpperCase();
        System.out.println("s:--->" + s);
        if (!withdrawResp.getSign().equals(s)) {
            log.error("CBPay提现回调验签错误,:[{}]", CBPayNotify);
            return "SIGN_FAIL";
        }
        // 枷锁防止重复调用
        String key = "cbpay_withdraw_" + withdrawResp.getOrderCode();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        if (b) {
            try {
                // 验签
                return withdrawalsRecordService.cbpayNotifyWithdrawOrder(withdrawResp,paymentChannel);
            } catch (Exception e) {
                log.error("[提现]CBPay提现回调没有获取到锁等待下次回调 订单编号:" + withdrawResp.getOrderCode());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }

        log.error(withdrawResp.getOrderCode()+"---last--->"+withdrawResp.getStatus());
//        System.out.println("last--->"+withdrawResp.getStatus());
        return "FAIL";
    }


    @ApiOperation(value = "提现回调接口 MPay", notes = "v1 版本")
    @PostMapping("/mpay/notifyWithdrawOrder")
    public String mpayNotifyWithdrawOrder(@RequestParam Map<String, String> MPayNotify){
        log.info("MPay提现回调原始数据-----------------------"+MPayNotify);
        MPayWithdrawResp withdrawResp = JsonUtil.fromJsonFastJSON(MPayWithdrawResp.class, JsonUtil.toJson(MPayNotify));
        if (MPayNotify==null||withdrawResp==null){
            log.error("MPay提现回调空,:[{}]", MPayNotify);
            return "FAIL";
        }
        log.info("Mpay提现回调接收数据："+withdrawResp);
        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", "MPay"));
        // 验签
        String sign = withdrawResp.toSign(paymentChannel.getPrivateKey());
        System.out.println("sign:--->" + sign);
//        String s = MD5.create().digestHex(sign).toUpperCase();
//        String s = MD5Utils.hash(sign);
        String s = MD5.create().digestHex(sign).toLowerCase();
        System.out.println("s:--->" + s);
        if (!withdrawResp.getSign().equals(s)) {
            log.error("MPay提现回调验签错误,:[{}]", MPayNotify);
            return "SIGN_FAIL";
        }
        // 枷锁防止重复调用
        String key = "mpay_withdraw_" + withdrawResp.getMerchOrderId();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        if (b) {
            try {
                // 验签
                return withdrawalsRecordService.mpayNotifyWithdrawOrder(withdrawResp,paymentChannel);
            } catch (Exception e) {
                log.error("[提现]MPay提现回调没有获取到锁等待下次回调 订单编号:" + withdrawResp.getMerchOrderId());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        log.error(withdrawResp.getMerchOrderId()+"---last--->"+withdrawResp.getStatus());
        return "FAIL";
    }



    // 充值回调 mypay
    @ApiOperation(value = "支付回调接口 MYPay", notes = "v1 版本")
    @PostMapping("/mypay/notifyRechargeOrder")
    public String mypayNotifyRechargeOrder(@RequestBody MYPayWithdrawNotifyResp rechargeNotify) throws Exception {
        log.info("MYPay支付回调-----------------------");
//        MYPayNotifyResp rechargeNotify = JsonUtil.fromJsonFastJSON(MYPayNotifyResp.class, JsonUtil.toJson(MYPayNotify));
        if (rechargeNotify==null){
            log.error("MYPay充值回调信息空,:[{}]", rechargeNotify);
            return "FAIL";
        }
        log.info("mypay充值回调："+rechargeNotify);
        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", "MYPay"));
//        if (paymentChannel == null||!rechargeNotify.getUserCode().equals(paymentChannel.getCurrencyCode())) {
        if (paymentChannel == null) {
            log.error("MYPay通道信息不存在,:[{}]", rechargeNotify.toString());
            return "FAIL";
        }

        Boolean verify = RSAUtils.verify(rechargeNotify.toSignDate(), rechargeNotify.getSign(), paymentChannel.getPublicKey());
        log.error("MYPay充值验签,:[{}]", verify);
        if (!verify) {
            log.error("MYPay充值验签错误,:[{}]", rechargeNotify);
            return "SIGN_FAIL";
        }
        // 枷锁防止重复调用
        String key = "MYPay_Recharge_"+rechargeNotify.getMerchantOrderNo();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        if (b) {
            try {
                return rechargeRecordService.mypayNotifyRechargeOrder(rechargeNotify,paymentChannel);
            } catch (Exception e) {
                log.error("[充值]支付回调没有获取到锁等待下次回调 订单编号:" + rechargeNotify.getMerchantOrderNo());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        log.error(rechargeNotify.getMerchantOrderNo()+"---last--->"+rechargeNotify.getStatus());
//        System.out.println(rechargeNotify.getOrderCode()+"---last--->"+rechargeNotify.getStatus());
        return "FAIL";
    }


    // 充值回调 okpay
    @ApiOperation(value = "支付回调接口 okPay", notes = "v1 版本")
    @PostMapping("/okpay/notifyRechargeOrder")
    public String okpayNotifyRechargeOrder(@RequestBody OKPayOrderDto payOrderDto) throws Exception {
        log.info("OKPay支付回调-----------------------");
//        MYPayNotifyResp rechargeNotify = JsonUtil.fromJsonFastJSON(MYPayNotifyResp.class, JsonUtil.toJson(MYPayNotify));
        if (payOrderDto==null){
            log.error("OKPay充值回调信息空,:[{}]", payOrderDto);
            return "FAIL";
        }
        log.info("OKpay充值回调："+payOrderDto);
        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", "OKPay"));
//        if (paymentChannel == null||!rechargeNotify.getUserCode().equals(paymentChannel.getCurrencyCode())) {
        if (paymentChannel == null) {
            log.error("MYPay通道信息不存在,:[{}]", payOrderDto.toString());
            return "FAIL";
        }

//        Boolean verify = RSAUtils.verify(rechargeNotify.toSignDate(), rechargeNotify.getSign(), paymentChannel.getPublicKey());
        //验证签名 创建时为空，回调时才有值
            String lowerCase = MD5.create().digestHex(payOrderDto.getSign() + paymentChannel.getPrivateKey()).toLowerCase();
            System.out.println("回调加密签名lowerCase"+lowerCase);
            System.out.println("回调签名payOrderDto.getRetsign()"+payOrderDto.getRetsign());
            if (!lowerCase.equals(payOrderDto.getRetsign())){
                log.error("okpay创建支付单验证签名错误："+payOrderDto);
                throw new RuntimeException(String.format("[okPay]三方返回结果: %s",payOrderDto));
            }
//        log.error("MYPay充值验签,:[{}]", verify);
//        if (!verify) {
//            log.error("MYPay充值验签错误,:[{}]", rechargeNotify);
//            return "SIGN_FAIL";
//        }
        // 枷锁防止重复调用
        String key = "MYPay_Recharge_"+payOrderDto.getOrderid();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        if (b) {
            try {
                return rechargeRecordService.okpayNotifyRechargeOrder(payOrderDto,paymentChannel);
            } catch (Exception e) {
                log.error("[充值]支付回调没有获取到锁等待下次回调 订单编号:" + payOrderDto.getOrderid());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        log.error(payOrderDto.getOrderid()+"---last--->"+payOrderDto.getState());
//        System.out.println(rechargeNotify.getOrderCode()+"---last--->"+rechargeNotify.getStatus());
        return "FAIL";
    }

    //支付VIP回调MYPAY
    @ApiOperation(value = "支付VIP回调接口 MYPay", notes = "v1 版本")
    @PostMapping("/mypay/notifyRechargeOrderVIP")
    public String mypayNNotifyRechargeOrderVIP(@RequestParam Map<String, String> MYPayNotify) throws Exception {
        log.info("MYPay支付VIP回调-----------------------");
        MYPayWithdrawNotifyResp rechargeNotify = JsonUtil.fromJsonFastJSON(MYPayWithdrawNotifyResp.class, JsonUtil.toJson(MYPayNotify));
        if (MYPayNotify==null||rechargeNotify==null){
            log.error("MYPay支付VIP回调信息空,:[{}]", MYPayNotify);
            return "FAIL";
        }
        log.info("mypay充值VIP回调："+rechargeNotify);
        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", "MYPay"));
        if (paymentChannel == null) {
            log.error("MYPay通道信息不存在,:[{}]",rechargeNotify.toString());
            return "FAIL";
        }
        // 验签
        String sign = rechargeNotify.toSignDate();
        String encrypt = RSAUtils.encrypt(sign, paymentChannel.getPrivateKey());
        System.out.println("回调sign:--->" + rechargeNotify.getSign());
        System.out.println("本地加密之后的sign:--->" + encrypt);
        if (!rechargeNotify.getSign().equals(encrypt)) {
            log.error("MYPay充值验签错误,:[{}]", rechargeNotify);
            return "SIGN_FAIL";
        }

        // 枷锁防止重复调用
        String key = "MYPay_Recharge_vip_"+rechargeNotify.getMerchantOrderNo();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        if (b) {
            try {
                return rechargeRecordService.mypayNotifyRechargeOrderVIP(rechargeNotify,paymentChannel);
            } catch (Exception e) {
                log.error("[VIP]MYPay支付回调没有获取到锁等待下次回调 订单编号:" + rechargeNotify.getMerchantOrderNo());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        log.error(rechargeNotify.getMerchantOrderNo()+"---last--->"+rechargeNotify.getStatus());
        return "FAIL";
    }

    // 充值回调 okpay
    @ApiOperation(value = "支付回调接口 okPay", notes = "v1 版本")
    @PostMapping("/okpay/notifyRechargeOrderVIP")
    public String okpayNNotifyRechargeOrderVIP(@RequestBody OKPayOrderDto payOrderDto) throws Exception {
        log.info("OKPay支付回调-----------------------");
//        MYPayNotifyResp rechargeNotify = JsonUtil.fromJsonFastJSON(MYPayNotifyResp.class, JsonUtil.toJson(MYPayNotify));
        if (payOrderDto==null){
            log.error("OKPay充值回调信息空,:[{}]", payOrderDto);
            return "FAIL";
        }
        log.info("OKpay充值回调："+payOrderDto);
        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", "OKPay"));
//        if (paymentChannel == null||!rechargeNotify.getUserCode().equals(paymentChannel.getCurrencyCode())) {
        if (paymentChannel == null) {
            log.error("MYPay通道信息不存在,:[{}]", payOrderDto.toString());
            return "FAIL";
        }

//        Boolean verify = RSAUtils.verify(rechargeNotify.toSignDate(), rechargeNotify.getSign(), paymentChannel.getPublicKey());
        //验证签名 创建时为空，回调时才有值
        String lowerCase = MD5.create().digestHex(payOrderDto.getSign() + paymentChannel.getPrivateKey()).toLowerCase();
        System.out.println("回调加密签名lowerCase"+lowerCase);
        System.out.println("回调签名payOrderDto.getRetsign()"+payOrderDto.getRetsign());
        if (!lowerCase.equals(payOrderDto.getRetsign())){
            log.error("okpay创建支付单验证签名错误："+payOrderDto);
            throw new RuntimeException(String.format("[okPay]三方返回结果: %s",payOrderDto));
        }
//        log.error("MYPay充值验签,:[{}]", verify);
//        if (!verify) {
//            log.error("MYPay充值验签错误,:[{}]", rechargeNotify);
//            return "SIGN_FAIL";
//        }
        // 枷锁防止重复调用
        String key = "MYPay_Recharge_"+payOrderDto.getOrderid();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        if (b) {
            try {
                return rechargeRecordService.okpayNotifyRechargeOrder(payOrderDto,paymentChannel);
            } catch (Exception e) {
                log.error("[充值]支付回调没有获取到锁等待下次回调 订单编号:" + payOrderDto.getOrderid());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        log.error(payOrderDto.getOrderid()+"---last--->"+payOrderDto.getState());
//        System.out.println(rechargeNotify.getOrderCode()+"---last--->"+rechargeNotify.getStatus());
        return "FAIL";
    }

    @ApiOperation(value = "提现回调接口 MYPay", notes = "v1 版本")
    @PostMapping("/mypay/notifyWithdrawOrder")
    public String mypayNotifyWithdrawOrder(@RequestParam Map<String, String> resp) throws Exception {
        log.info("MYPay提现回调-----------------------");
        MYPayWithdrawNotifyResp withdrawResp = JsonUtil.fromJsonFastJSON(MYPayWithdrawNotifyResp.class, JsonUtil.toJson(resp));
        if (resp==null||withdrawResp==null){
            log.error("MYPay提现回调空,:[{}]", resp);
            return "FAIL";
        }
        log.info("mypay提现回调："+withdrawResp.toString());
        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", "MYPay"));
        // 验签
        String sign = withdrawResp.toSignDate();
        String encrypt = RSAUtils.encrypt(sign, paymentChannel.getPrivateKey());
        System.out.println("回调sign:--->" + withdrawResp.getSign());
        System.out.println("本地加密之后的sign:--->" + encrypt);
        if (!withdrawResp.getSign().equals(encrypt)) {
            log.error("MYPay提现验签错误,:[{}]", withdrawResp);
            return "SIGN_FAIL";
        }
        // 枷锁防止重复调用
        String key = "mypay_withdraw_" + withdrawResp.getMerchantOrderNo();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        if (b) {
            try {
                // 验签
                return withdrawalsRecordService.mypayNotifyWithdrawOrder(withdrawResp,paymentChannel);
            } catch (Exception e) {
                log.error("[提现]MYPay提现回调没有获取到锁等待下次回调 订单编号:" + withdrawResp.getMerchantOrderNo());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        log.error(withdrawResp.getMerchantOrderNo()+"---last--->"+withdrawResp.getStatus());
        return "FAIL";
    }

    @ApiOperation(value = "提现回调接口 OKPay", notes = "v1 版本")
    @PostMapping("/okpay/notifyWithdrawOrder")
    public String okpayNotifyWithdrawOrder(@RequestBody OKWdOrderDto wdOrderDto) throws Exception {
        log.info("OKPay提现回调-----------------------");
//        MYPayWithdrawNotifyResp withdrawResp = JsonUtil.fromJsonFastJSON(MYPayWithdrawNotifyResp.class, JsonUtil.toJson(resp));
        if (wdOrderDto==null){
            log.error("OKPay提现回调空,:[{}]", wdOrderDto);
            return "FAIL";
        }
        log.info("OKpay提现回调："+wdOrderDto);
        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", "OKPay"));
        //验证签名 创建时为空，回调时才有值
        //sign=md5(sendid+orderid+amount+apikey)
        double aDouble = Double.parseDouble(wdOrderDto.getAmount());
        String lowerCase = MD5.create().digestHex(wdOrderDto.getSendid()+wdOrderDto.getOrderid()+aDouble+paymentChannel.getPrivateKey()).toLowerCase();
        System.out.println("回调加密签名lowerCase"+lowerCase);
        System.out.println("回调签名payOrderDto.getRetsign()"+wdOrderDto.getSign());
        if (!lowerCase.equals(wdOrderDto.getSign())){
            log.error("okpay提现回调单验证签名错误："+wdOrderDto);
            throw new RuntimeException(String.format("[okPay]三方返回结果: %s",wdOrderDto));
        }
        // 枷锁防止重复调用
        String key = "okpay_withdraw_" + wdOrderDto.getOrderid();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        // 获取延迟时间（毫秒），这里设置为5秒
        long delayMillis = 5000;
        if (b) {
            try {
                //okpay的回调太快，这里加延迟防止幻读
                Thread.sleep(delayMillis);
                // 验签
                return withdrawalsRecordService.okpayNotifyWithdrawOrder(wdOrderDto,paymentChannel);
            } catch (Exception e) {
                log.error("[提现]MYPay提现回调没有获取到锁等待下次回调 订单编号:" + wdOrderDto.getOrderid());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        log.error(wdOrderDto.getOrderid()+"---last--->"+wdOrderDto.getState());
        return "FAIL";
    }


    @ApiOperation(value = "支付回调接口 808Pay", notes = "v1 版本")
    @PostMapping("/808pay/notifyRechargeOrder")
    public String zimu808notifyRechargeOrder(@RequestBody Zimu808RechargeNotify rechargeNotify) throws Exception {
        log.info("808Pay支付回调-----------------------");
        if (rechargeNotify==null){
            log.error("808Pay充值回调信息空,:[{}]", rechargeNotify);
            return "FAIL";
        }
        log.info("808pay充值回调："+rechargeNotify);
        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", "808Pay"));
        if (paymentChannel == null) {
            log.error("808Pay通道信息不存在,:[{}]", rechargeNotify.toString());
            return "CHANNEL_FAIL";
        }
        // 验签
        String md5 = rechargeNotify.toSign(paymentChannel.getPrivateKey());
        if (!cn.rh.flash.utils.MD5.getMD5String(md5).toUpperCase().equals(rechargeNotify.getSign())) {
            log.error("808Pay充值验签错误,:[{}]", md5);
            return "SIGN_FAIL";
        }
        // 枷锁防止重复调用
        String key = "808Pay_Recharge_"+rechargeNotify.getMchOrderNo();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        if (b) {
            try {
                return rechargeRecordService.zimu808payNotifyRechargeOrder(rechargeNotify);
            } catch (Exception e) {
                log.error("[充值]支付回调没有获取到锁等待下次回调 订单编号:" + rechargeNotify.getMchOrderNo());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        log.error(rechargeNotify.getMchOrderNo()+"---last--->"+rechargeNotify.getStatus());
//        System.out.println(rechargeNotify.getOrderCode()+"---last--->"+rechargeNotify.getStatus());
        return "FAIL";
    }

    @ApiOperation(value = "提现回调接口 808pay", notes = "v1 版本")
    @PostMapping("/808pay/notifyWithdrawOrder")
    public String zimu808payNotifyWithdrawOrder(@RequestBody Zimu808WithdrawNotify withdrawNotify) throws Exception {
        log.info("808Pay提现回调-----------------------");
        if (StringUtil.isNullOrEmpty(withdrawNotify)){
            log.error("808Pay提现回调空,:[{}]", withdrawNotify);
            return "FAIL";
        }
        log.info("808pay提现回调："+withdrawNotify.toString());
        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", "808Pay"));
        if (paymentChannel == null) {
            log.error("808Pay通道信息不存在,:[{}]", withdrawNotify.toString());
            return "CHANNEL_FAIL";
        }
        // 验签
        String md5 = withdrawNotify.toSign(paymentChannel.getPrivateKey());
        if (!cn.rh.flash.utils.MD5.getMD5String(md5).toUpperCase().equals(withdrawNotify.getSign())) {
            log.error("808Pay提现验签错误,:[{}]", md5);
            return "SIGN_FAIL";
        }
        // 枷锁防止重复调用
        String key = "808pay_withdraw_" + withdrawNotify.getMchOrderNo();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        if (b) {
            try {
                return withdrawalsRecordService.zimu808payNotifyWithdrawOrder(withdrawNotify);
            } catch (Exception e) {
                log.error("[提现]808Pay提现回调没有获取到锁等待下次回调 订单编号:" + withdrawNotify.getMchOrderNo());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        log.error(withdrawNotify.getMchOrderNo()+"---last--->"+withdrawNotify.getStatus());
        return "FAIL";
    }
}
