package cn.rh.flash.service.coom.dz;

import cn.rh.flash.bean.dto.PrizeNumDto;
import cn.rh.flash.bean.dto.api.BuyVipDto;
import cn.rh.flash.bean.entity.dzpower.PowerBank;
import cn.rh.flash.bean.entity.dzpower.PowerBankTask;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.entity.dzvip.DzVipMessage;
import cn.rh.flash.bean.entity.dzvip.VipPurchaseHistory;
import cn.rh.flash.bean.entity.system.Task;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.api.RechargeOrderVo;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.CacheDao;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.cache.impl.EhcacheDao;
import cn.rh.flash.service.dzpower.PowerBankService;
import cn.rh.flash.service.dzpower.PowerBankTaskService;
import cn.rh.flash.service.dzprize.PrizeNumService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.dzuser.UserInfoService;
import cn.rh.flash.service.dzvip.ByVipTotalMoneyService;
import cn.rh.flash.service.dzvip.DzVipMessageService;
import cn.rh.flash.service.dzvip.VipPurchaseHistoryService;
import cn.rh.flash.service.system.impl.ConstantFactory;
import cn.rh.flash.service.task.TaskService;
import cn.rh.flash.utils.*;
import lombok.extern.log4j.Log4j2;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 商品购买
 */
@Log4j2
@Component
public class ShopService extends ApiUserCoom {


    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private RecordInformation recordInformation;

    @Autowired
    private DzVipMessageService dzVipMessageService;

    @Autowired
    private PowerBankService powerBankService;
    @Autowired
    private PowerBankTaskService powerBankTaskService;

    @Autowired
    private VipPurchaseHistoryService vipPurchaseHistoryService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ByVipTotalMoneyService byVipTotalMoneyService;

    @Autowired
    private EhcacheDao ehcacheDao;

    @Autowired
    private ConfigCache configCache;

    @Autowired
    private PrizeNumService prizeNumService;

    @Transactional(rollbackFor = Exception.class)
    public Ret<RechargeOrderVo> buyVip(BuyVipDto buyVipDto) throws Exception {
        //解密
        if (StringUtil.isNotEmpty(buyVipDto.getPaymentPassword())) {
            buyVipDto.setPaymentPassword(CryptUtil.desEncrypt(buyVipDto.getPaymentPassword()));
        }
        List<SearchFilter> filter = new ArrayList<>();
        filter.add(SearchFilter.build("idw", buyVipDto.getVipIdw()));
        filter.add(SearchFilter.build("dzstatus", "1"));
        DzVipMessage dzVipMessage = dzVipMessageService.get(filter);

        if (dzVipMessage == null) {
            return Rets.failure(MessageTemplateEnum.VIP_INFORMATION_DOES_NOT_EXIST.getCode(), MessageTemplateEnum.VIP_INFORMATION_DOES_NOT_EXIST);
        }

        UserInfo oneBySql = getOneBySql(getUserId());
        // 校验交易密码
        if (buyVipDto.getPaymentMethod() == 1) { // 1为余额交易
            if (!oneBySql.getPaymentPassword().equals(MD5.md5(buyVipDto.getPaymentPassword(), ""))) {
                log.info("用户: " + oneBySql.getAccount() + " ,密码校验不一致,注册密码: " + oneBySql.getPaymentPassword() + ", 输入密码: " + buyVipDto.getPaymentPassword() + ", 输入密码加密后: " + MD5.md5(buyVipDto.getPaymentPassword(), ""));
                return Rets.failure(MessageTemplateEnum.PAY_PASSWORD_ERROR.getCode(), MessageTemplateEnum.PAY_PASSWORD_ERROR);
            }
        }

        // skj  限制 购买vip
        if (oneBySql.getLimitBuyCdb() != null && oneBySql.getLimitBuyCdb() == 1) {
            return Rets.failure(MessageTemplateEnum.THE_ACCOUNT_IS_DISABLED.getCode(), MessageTemplateEnum.THE_ACCOUNT_IS_DISABLED);
        }

//        int vip = Integer.parseInt(oneBySql.getVipType().replace("v", ""));
//        int buyVip = Integer.parseInt(dzVipMessage.getVipType().replace("v", ""));
//
//        if (vip >= buyVip) {
//            return Rets.failure(MessageTemplateEnum.VIP_LEVEL_CAN_LESS_THAN_NOW.getCode(), MessageTemplateEnum.VIP_LEVEL_CAN_LESS_THAN_NOW);
//        }

        //是否过期
        boolean expired = DateUtil.isExpired(getVipExpireDate());
        int vip = Integer.parseInt(oneBySql.getVipType().replace("v", ""));
        int buyVip = Integer.parseInt(dzVipMessage.getVipType().replace("v", ""));
        if (!expired) { //未过期不准购买低级、同级VIP
            if (vip >= buyVip) {
                return Rets.failure(MessageTemplateEnum.VIP_LEVEL_CAN_LESS_THAN_NOW.getCode(), MessageTemplateEnum.VIP_LEVEL_CAN_LESS_THAN_NOW);
            }
        }


        // 查询当前用户之前是否购买过v1体验会员
        List<SearchFilter> filter1 = new ArrayList<>();
        filter1.add(SearchFilter.build("uid", oneBySql.getId()));
        filter1.add(SearchFilter.build("afterViPType", "v1"));
        filter1.add(SearchFilter.build("whetherToPay", 2));

        List<VipPurchaseHistory> vipPurchaseHistories1 = vipPurchaseHistoryService.queryAll(filter1);
        if ("v1".equals(dzVipMessage.getVipType()) && vipPurchaseHistories1.size() > 0) {
            return Rets.failure(MessageTemplateEnum.CANNOT_REPEAT_PURCHASE.getCode(), MessageTemplateEnum.CANNOT_REPEAT_PURCHASE);
        }

        // 同个账号 同个等级限制只能买一次, 手动调整也不能重复购买,只能购买更高等级 2024-01-22
//        List<SearchFilter> filters = new ArrayList<>();
//        filters.add(SearchFilter.build("uid", oneBySql.getId()));
//        filters.add(SearchFilter.build("afterViPType", dzVipMessage.getVipType()));
//        filters.add(SearchFilter.build("whetherToPay", 2));
//
//        List<VipPurchaseHistory> vipPurchaseHistories = vipPurchaseHistoryService.queryAll(filters);
//        if (vipPurchaseHistories.size() > 0) {
//            return Rets.failure(MessageTemplateEnum.CANNOT_REPEAT_PURCHASE.getCode(), MessageTemplateEnum.CANNOT_REPEAT_PURCHASE);
//        }

        String orderNumber = new IdWorker().nextId() + "";

        // skj 当前VIP价格为升级会员价格减去当前会员等级价格，购买过该等级的VIP (支付差价)
        String vipType = getVipType();
        boolean flag = true; // 判断是否是升级补差价购买
        Double aDouble = dzVipMessage.getSellingPrice();
        if (!"v0".equals(vipType) && !"v1".equals(vipType)) { // 用户之前等级是v0v1说明是第一次购买会员 返佣
            // 查询原来的VIP
            DzVipMessage dzVipMessageOld = dzVipMessageService.get(SearchFilter.build("vipType", vipType));

            if (dzVipMessage.getSellingPrice() <= dzVipMessageOld.getSellingPrice()) {
                aDouble = 0.00;
            } else {
                aDouble = BigDecimalUtils.subtract(dzVipMessage.getSellingPrice(), dzVipMessageOld.getSellingPrice());
            }
            // 重新设置购买Vip价格
            flag = false; // false 不给上级返佣
        }

        //手动修改vip的情况也不返佣
        //查询vip购买记录，v1不参与判断
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("uid", oneBySql.getId()));
        filters.add(SearchFilter.build("afterViPType", SearchFilter.Operator.NE, "v1"));
        filters.add(SearchFilter.build("whetherToPay", 2));

        List<VipPurchaseHistory> vipPurchaseHistories = vipPurchaseHistoryService.queryAll(filters);
        if (vipPurchaseHistories.size() > 0) {
            flag = false; // false 不给上级返佣
        }


        //购买vip
        int i = recordInformation.vipPurchase(buyVipDto.getChannelName(), buyVipDto.getChannelType(), getUserId(), getAccount(), vipType, aDouble, dzVipMessage, buyVipDto.getPaymentMethod(), orderNumber, getSourceInvitationCode());
        // 1 当前用户余额=0   2 当前用户余额不足   3 vip信息不存在  4 ok 5 充值订单购买VIP等回调
        switch (i) {
            case 1:
                return Rets.failure(MessageTemplateEnum.INSUFFICIENT_BALANCE_ZERO.getCode(), MessageTemplateEnum.INSUFFICIENT_BALANCE_ZERO);
            case 2:
                return Rets.failure(MessageTemplateEnum.INSUFFICIENT_BALANCE.getCode(), MessageTemplateEnum.INSUFFICIENT_BALANCE);
            case 3:
                return Rets.failure(MessageTemplateEnum.VIP_INFORMATION_DOES_NOT_EXIST.getCode(), MessageTemplateEnum.VIP_INFORMATION_DOES_NOT_EXIST);
            case 4:
                //进入套娃收益
                if (flag) {
                    String nickName = configCache.get(ConfigKeyEnum.SITE_NICKNAME).trim();
                    if (StringUtil.isEmpty(nickName)) {
                        nickName = " ";
                    }
                    if ("西班牙,埃及1,黄色单车".contains(nickName)) {
                        recordInformation.cycleRewardTwo(userInfoService.get(getUserId()), orderNumber, buyVip, vipType, dzVipMessage.getVipType());

                    } else {
                        recordInformation.cycleReward(userInfoService.get(getUserId()), orderNumber, buyVip, vipType, dzVipMessage.getVipType());
                    }
                }
                shopService.payVipOk(dzVipMessage, oneBySql);
                shopService.changeUserVip(oneBySql, dzVipMessage.getVipType(), dzVipMessage.getValidDate().toString());
                // 增加购买vip累计金额
                byVipTotalMoneyService.addByVipTotalMoney(oneBySql, aDouble);
                // 2024 - 04-01 信誉分
                Integer creditScore = Integer.parseInt(configCache.get(ConfigKeyEnum.CREADIT_SCORE).trim());
                Integer nickRange = Integer.parseInt(configCache.get(ConfigKeyEnum.SITE_NICKRANFGE).trim());
                if (creditScore > 0) {
                    switch (nickRange) {
                        case 0:
                            creditScoreV1(oneBySql);
                            break;
                        case 1:
                            creditScoreV2(oneBySql);
                            break;
                    }
                }
                //赠送大富翁抽奖次数     v1不赠送
                if(!dzVipMessage.getVipType().equals("v1")){
                    giftMonopolyNumber(oneBySql);
                }

                break;
            case 5:
                //判断金额是否大于0
                if (dzVipMessage.getSellingPrice() <= 0) {
                    return Rets.failure(MessageTemplateEnum.RECHARGE_CREATE_ERROR.getCode(), MessageTemplateEnum.RECHARGE_CREATE_ERROR);
                }
                //todo 发起三方订单 orderNumber 返回充值地址给前台
                //判定通道类型是否开启
                switch (buyVipDto.getChannelType()) {
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
//                        DzVipMessage vipOld = ehcacheDao.hget(CacheDao.VIPMESSAGE, vipType, DzVipMessage.class);
//                        if (vipOld!=null&&StringUtil.isNotEmpty(vipOld.getWithdrawMethods())&&!vipOld.getWithdrawMethods().contains(buyVipDto.getChannelName())){
//                            return Rets.failure(MessageTemplateEnum.CHANNEL_TYPE_NOT_OPEN.getCode(), MessageTemplateEnum.CHANNEL_TYPE_NOT_OPEN);
//                        }
                        break;
                    case "bank":
                        log.info(buyVipDto.getChannelName() + "银行卡购买VIP");
                        break;
                    default:
                        return Rets.failure(MessageTemplateEnum.RECHARGE_CREATE_ERROR.getCode(), MessageTemplateEnum.RECHARGE_CREATE_ERROR);
                }
                RechargeOrderVo rechargeOrderVo = recordInformation.createRechargeOrderThird(2, getUserId().toString(), dzVipMessage.getSellingPrice().toString(), orderNumber, buyVipDto.getChannelName(), buyVipDto.getChannelType());
                if (rechargeOrderVo != null) {
                    VipPurchaseHistory vipPurchaseHistory = vipPurchaseHistoryService.get(SearchFilter.build("idw", orderNumber));
                    vipPurchaseHistory.setDepositAddress(rechargeOrderVo.getAddress());
                    vipPurchaseHistoryService.update(vipPurchaseHistory);
                }
                return Rets.success(rechargeOrderVo);
        }
        sysLogService.addSysLog(oneBySql.getAccount(), oneBySql.getId(), oneBySql.getAccount(), "APP", SysLogEnum.BUY_VIP);

        return Rets.success();
    }


    /**
     * vip 购买成功后 相关业务  在线
     */
    public void payVipOk(VipPurchaseHistory vipPurchaseHistory) {
        // 23-5-8 防止购买vip时 跟支付回调VIP信息不一样
        DzVipMessage dzVipMessage = dzVipMessageService.get(SearchFilter.build("vipType", vipPurchaseHistory.getAfterViPType()));
        buyPowerBank(vipPurchaseHistory.getSourceInvitationCode(), vipPurchaseHistory.getUid(), vipPurchaseHistory.getAccount(), dzVipMessage.getNumberOfTasks(), dzVipMessage.getValidDate(), dzVipMessage.getDailyIncome(), vipPurchaseHistory.getAfterViPType());
    }

    /**
     * vip 购买成功后 相关业务  余额
     */
    public void payVipOk(DzVipMessage dzVipMessage, UserInfo oneBySql) {
        buyPowerBank(oneBySql.getSourceInvitationCode(), oneBySql.getId(), oneBySql.getAccount(), dzVipMessage.getNumberOfTasks(), dzVipMessage.getValidDate(), dzVipMessage.getDailyIncome(), dzVipMessage.getVipType());
    }

    /**
     * 购买充电宝
     *
     * @param getSourceInvitationCode 来源编号
     * @param getUid                  用户uid
     * @param getAccount              用户账号
     * @param getNumberOfTasks        购买数量
     * @param getValidDate            有效天数
     */
    @Transactional
    public void buyPowerBank(String getSourceInvitationCode, Long getUid, String getAccount, Integer getNumberOfTasks, Integer getValidDate, Double getDailyIncome, String vipType) {
        // 因为只有一种 充电宝
        PowerBank powerBank = powerBankService.get(SearchFilter.build("dzstatus", 1));
        DzVipMessage dzVipMessage = dzVipMessageService.get(SearchFilter.build("vipType", vipType));
        String gear = ConstantFactory.me().getDictsByName("档次类型", dzVipMessage.getGearCode());
        if (powerBank != null) {
            PowerBankTask obj = new PowerBankTask();
            obj.setIdw(new IdWorker().nextId() + "");
            obj.setSourceInvitationCode(getSourceInvitationCode);
            obj.setUid(getUid);
            obj.setAccount(getAccount);
            obj.setPbidw(powerBank.getIdw());
            obj.setImage(dzVipMessage.getPowerBankImg());
            obj.setName(dzVipMessage.getNick());
            obj.setBannerType(powerBank.getBannerType());
            obj.setPayPrice(getDailyIncome);
            obj.setTotalQuantity(getNumberOfTasks);
            obj.setVipType(vipType);
            obj.setHours(gear);


            Task task = taskService.get(SearchFilter.build("jobClass", "cn.rh.flash.service.task.job.RebateJob"));
            // 获取充电宝任务下次执行时间
            CronExpression cronExpression = null;
            try {
                cronExpression = new CronExpression(task.getCron());
            } catch (ParseException e) {
                e.printStackTrace();
                log.error("充电宝任务获取下次执行时间失败");
                throw new ApplicationException(BizExceptionEnum.SERVER_ERROR);
            }
            Date after = cronExpression.getNextValidTimeAfter(new Date());
            String afterDate = DateUtil.formatDate(DateUtil.getLocalDateTime(after), "HH:mm:ss");

            // 购买vip时间
            String date = DateUtil.formatDate(DateUtil.getLocalDateTime(new Date()), "HH:mm:ss");

            // 2-21 用户注册时间 时分秒 在定时任务执行之前的话 需要将最后一次执行时间 跟到期时间 向前偏移一天
            if (isAdvance(date, afterDate)) {
                obj.setLastTime(DateUtil.getAfterDayDate(-1 + ""));
                obj.setRemark("购买时间:" + date + ";返佣时间:" + afterDate);

            } else {
                obj.setLastTime(DateUtil.parseTime(DateUtil.getTime()));
            }
            obj.setExpireTime(DateUtil.getAfterDayDate(getValidDate + ""));


            powerBankTaskService.insert(obj);

            overPowerBank(getUid, obj.getIdw());

        }
    }

    // 判断当前用户注册时间是否在任务执行时间前
    private boolean isAdvance(String date, String afterDate) {
        // 获取用户注册时间
        Integer registrationNum = Integer.valueOf(date.replace(":", ""));

        Integer afterNum = Integer.valueOf(afterDate.replace(":", ""));
        if (registrationNum < afterNum) {
            return true;
        }
        return false;
    }

    /**
     * 结束vip 任务
     *
     * @doc 购买vip 时候 停止上一条 返佣任务
     */
    @Transactional
    public void overPowerBank(Long uid, String idw) {

        List<SearchFilter> lists = new ArrayList<>();
        lists.add(SearchFilter.build("uid", uid));
        lists.add(SearchFilter.build("idw", SearchFilter.Operator.NE, idw));
        List<PowerBankTask> powerBankTask = powerBankTaskService.queryAll(lists);
        for (PowerBankTask bankTask : powerBankTask) {
            // 表示昨天是最后一次返佣了
            bankTask.setExpireTime(DateUtil.getAfterDayDate("-1"));
            bankTask.setIsRefund(2);
            powerBankTaskService.update(bankTask);
        }
    }


    /**
     * 购买VIP修改VIP等级
     */
    public void changeUserVip(UserInfo oneBySql, String vipType, String validDate) {
        oneBySql.setVipType(vipType);
        oneBySql.setVipExpireDate(DateUtil.getAfterDayDate(validDate));
        userInfoService.update(oneBySql);
    }

    /**
     * 购买VIP权限
     *
     * @param vipType 要购买的  vip 类型
     * @return
     */
    public Ret<RechargeOrderVo> isBuyVip(String vipType) {

        DzVipMessage dzVipMessage = dzVipMessageService.get(SearchFilter.build("idw", vipType));
        if (dzVipMessage == null) {
            return Rets.failure(MessageTemplateEnum.PARAM_NOT_EXIST.getCode(), MessageTemplateEnum.PARAM_NOT_EXIST);
        }

        UserInfo oneBySql = getOneBySql(getUserId());

        int vip = Integer.parseInt(oneBySql.getVipType().replace("v", ""));
        int buyVip = Integer.parseInt(dzVipMessage.getVipType().replace("v", ""));
        boolean expired = DateUtil.isExpired(getVipExpireDate());

        if (!expired) { //未过期不准购买低级、同级VIP
            if (vip >= buyVip) {
                return Rets.failure(MessageTemplateEnum.VIP_LEVEL_CAN_LESS_THAN_NOW.getCode(), MessageTemplateEnum.VIP_LEVEL_CAN_LESS_THAN_NOW);
            }
        }

        return Rets.success();
    }

    /**
     * 返佣信誉分
     * 国内
     * 升级vip       首次自身赠送信誉分 1次 后面升级不加
     * 升级vip上级   首次赠送信誉分    1次 后面升级不加
     * 去除体验vip
     * 自定义参数: 返佣层级 上级返佣分数 自身返佣分数
     */
    public void creditScoreV1(UserInfo oneBySql) {
        List<SearchFilter> filters1 = new ArrayList<>();
        filters1.add(SearchFilter.build("uid", oneBySql.getId()));
        filters1.add(SearchFilter.build("afterViPType", SearchFilter.Operator.NE, "v0"));
        filters1.add(SearchFilter.build("paymentAmount", SearchFilter.Operator.GT, 0));
        filters1.add(SearchFilter.build("whetherToPay", 2));
        long count = vipPurchaseHistoryService.count(filters1);
        // 返佣层级 后台配置
        int i = Integer.parseInt(configCache.get(ConfigKeyEnum.CREADIT_SCORE).trim());
        if (count == i) {
            String upVipCredit = configCache.get(ConfigKeyEnum.UP_VIP_CREDIT).trim();
            recordInformation.changeCredit(oneBySql.getSourceInvitationCode(), oneBySql.getId(), oneBySql.getAccount(), "1", "3", "1", (StringUtil.isEmpty(upVipCredit) ? 0 : Integer.parseInt(upVipCredit)), "", oneBySql.getAccount(), oneBySql.getVipType());
            // 给上级增加信誉分
            String parentVipCredit = configCache.get(ConfigKeyEnum.PARENT_VIP_CREDIT).trim();
            // 查询上级
            if (!oneBySql.getInvitationCode().equals(oneBySql.getSuperiorInvitationCode())) { // 相等说明上级是自己
                UserInfo superUser = userInfoService.get(SearchFilter.build("invitationCode", oneBySql.getSuperiorInvitationCode()));
                if (superUser != null) {
                    recordInformation.changeCredit(superUser.getSourceInvitationCode(), superUser.getId(), superUser.getAccount(), "1", "4", "1", (StringUtil.isEmpty(parentVipCredit) ? 0 : Integer.parseInt(parentVipCredit)), "", oneBySql.getAccount(), oneBySql.getVipType());
                }
            }
        }
    }

    /**
     * 海外
     * 升级vip  首次自身不加   大于等于2次才增加
     * 升级vip上级 每次送
     * 去除体验vip
     * 自定义参数: 返佣层级 上级返佣分数 自身返佣分数
     */
    public void creditScoreV2(UserInfo oneBySql) {
        List<SearchFilter> filters1 = new ArrayList<>();
        filters1.add(SearchFilter.build("uid", oneBySql.getId()));
        filters1.add(SearchFilter.build("afterViPType", SearchFilter.Operator.NE, "v0"));
        filters1.add(SearchFilter.build("paymentAmount", SearchFilter.Operator.GT, 0));
        filters1.add(SearchFilter.build("whetherToPay", 2));
        long count = vipPurchaseHistoryService.count(filters1);
        // 返佣层级 后台配置
        int i = Integer.parseInt(configCache.get(ConfigKeyEnum.CREADIT_SCORE).trim());
        if (count >= i) {
            // 升级vip  首次自身不加赠送信誉分    2次才增加 后面每次也增加
            String upVipCredit = configCache.get(ConfigKeyEnum.UP_VIP_CREDIT).trim();
            recordInformation.changeCredit(oneBySql.getSourceInvitationCode(), oneBySql.getId(), oneBySql.getAccount(), "1", "3", "1", (StringUtil.isEmpty(upVipCredit) ? 0 : Integer.parseInt(upVipCredit)), "", oneBySql.getAccount(), oneBySql.getVipType());
        }
        if (count >= 1) {
            // 升级vip上级 首次赠送信誉分  后面每次也赠送
            String parentVipCredit = configCache.get(ConfigKeyEnum.PARENT_VIP_CREDIT).trim();
            if (!oneBySql.getInvitationCode().equals(oneBySql.getSuperiorInvitationCode())) { // 相等说明上级是自己
                UserInfo superUser = userInfoService.get(SearchFilter.build("invitationCode", oneBySql.getSuperiorInvitationCode()));
                if (superUser != null) {
                    recordInformation.changeCredit(superUser.getSourceInvitationCode(), superUser.getId(), superUser.getAccount(), "1", "4", "1", (StringUtil.isEmpty(parentVipCredit) ? 0 : Integer.parseInt(parentVipCredit)), "", oneBySql.getAccount(), oneBySql.getVipType());
                }
            }
        }
    }

    /**
     * 赠送大富翁抽奖次数
     * 只有正式会员才参与赠送
     * @param oneBySql 用户
     */
    public void giftMonopolyNumber(UserInfo oneBySql) {
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("uid", oneBySql.getId()));
        filters.add(SearchFilter.build("afterViPType", SearchFilter.Operator.NE, "v0"));
        filters.add(SearchFilter.build("afterViPType", SearchFilter.Operator.NE, "v1"));
        filters.add(SearchFilter.build("paymentAmount", SearchFilter.Operator.GT, 0));
        filters.add(SearchFilter.build("whetherToPay", 2));
        long count = vipPurchaseHistoryService.count(filters);

        //赠送抽奖次数
        Integer creditScore1 = Integer.parseInt(configCache.get(ConfigKeyEnum.USER_UPGRADE_GIVEAWAYS).trim());
        //邀请者赠送几次
        Integer creditScore2 = Integer.parseInt(configCache.get(ConfigKeyEnum.NUMBER_OF_INVITER).trim());

        //赠送vip用户本人
        PrizeNumDto prizeNumDto = new PrizeNumDto();
        prizeNumDto.setPrizeNum(creditScore1);
        prizeNumDto.setPrizeType("10");
        prizeNumDto.setAccount(oneBySql.getAccount());
        prizeNumDto.setIsAdd("1");
        String s = prizeNumService.upOrDownPoints(prizeNumDto);
        if ("OK".equals(s)){
            sysLogService.addSysLog(prizeNumDto.getAccount(), null, "APP", SysLogEnum.UPORDOWNPOINTS,prizeNumDto.getAccount()+"--在"+ DateUtil.getTime() +"购买vip本账号--"+("1".equals(prizeNumDto.getIsAdd())?"上":"下")+"分(抽奖次数)"+prizeNumDto.getPrizeNum()+", 操作账号:"+prizeNumDto.getAccount());
        }

        //赠送邀请人
        //当上级就是本人时，不额外赠送，并且当已经赠送次数小于邀请者赠送次数时，才赠送
        if (!oneBySql.getInvitationCode().equals(oneBySql.getSuperiorInvitationCode())&&count<creditScore2){
            UserInfo oneBySql1 = userInfoService.getOneBySql(oneBySql.getSuperiorInvitationCode());
            PrizeNumDto prizeNumDto2 = new PrizeNumDto();
            prizeNumDto2.setPrizeNum(creditScore1);
            prizeNumDto2.setPrizeType("10");
            prizeNumDto2.setAccount(oneBySql1.getAccount());
            prizeNumDto2.setIsAdd("1");
            String s1 = prizeNumService.upOrDownPoints(prizeNumDto2);
            if ("OK".equals(s1)){
                sysLogService.addSysLog(prizeNumDto2.getAccount(), null, "APP", SysLogEnum.UPORDOWNPOINTS,prizeNumDto2.getAccount()+"--在"+ DateUtil.getTime() +"购买vip上级账号--"+("1".equals(prizeNumDto2.getIsAdd())?"上":"下")+"分(抽奖次数)"+prizeNumDto2.getPrizeNum()+", 操作账号:"+prizeNumDto.getAccount());
            }
        }
    }


}
