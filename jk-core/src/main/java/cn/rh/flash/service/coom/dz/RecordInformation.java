package cn.rh.flash.service.coom.dz;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.RandomUtil;
import cn.rh.flash.bean.entity.dzcredit.CreditRecord;
import cn.rh.flash.bean.entity.dzcredit.UserCredit;
import cn.rh.flash.bean.entity.dzpower.FlowingWaterPb;
import cn.rh.flash.bean.entity.dzpower.RecordPb;
import cn.rh.flash.bean.entity.dzpower.TotalBonusPb;
import cn.rh.flash.bean.entity.dzscore.UserScore;
import cn.rh.flash.bean.entity.dzscore.UserScoreHistory;
import cn.rh.flash.bean.entity.dzsys.PaymentChannel;
import cn.rh.flash.bean.entity.dzuser.*;
import cn.rh.flash.bean.entity.dzvip.DzVipMessage;
import cn.rh.flash.bean.entity.dzvip.TeamVIPActivationTotalRevenue;
import cn.rh.flash.bean.entity.dzvip.VipPurchaseHistory;
import cn.rh.flash.bean.entity.dzvip.VipRebateRecord;
import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.exception.ApiException;
import cn.rh.flash.bean.vo.api.RechargeOrderVo;
import cn.rh.flash.bean.vo.api.WithdrawOrderVo;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.cache.impl.EhcacheDao;
import cn.rh.flash.cache.impl.MyRedissonLocker;
import cn.rh.flash.dao.dzuser.TransactionRecordRepository;
import cn.rh.flash.sdk.paymentChannel.BiPay.BiPayUtil;
import cn.rh.flash.sdk.paymentChannel.BiPay.BipayBean.RechargeParam;
import cn.rh.flash.sdk.paymentChannel.BiPay.BipayBean.RechargeResp;
import cn.rh.flash.sdk.paymentChannel.BiPay.BipayBean.WithdrawParam;
import cn.rh.flash.sdk.paymentChannel.BiPay.BipayBean.WithdrawResp;
import cn.rh.flash.sdk.paymentChannel.CBPay.CBPayBean.CBPayResp;
import cn.rh.flash.sdk.paymentChannel.CBPay.CBPayBean.CBRechargeParam;
import cn.rh.flash.sdk.paymentChannel.CBPay.CBPayBean.CBWithdrawParam;
import cn.rh.flash.sdk.paymentChannel.CBPay.CBPayUtil;
import cn.rh.flash.sdk.paymentChannel.FPay.FPayBean.FPayResp;
import cn.rh.flash.sdk.paymentChannel.FPay.FPayBean.FRechargeParam;
import cn.rh.flash.sdk.paymentChannel.FPay.FPayBean.FWithdrawParam;
import cn.rh.flash.sdk.paymentChannel.FPay.FPayUtil;
import cn.rh.flash.sdk.paymentChannel.JDPay.JDPayBean.JDPayResp;
import cn.rh.flash.sdk.paymentChannel.JDPay.JDPayBean.JDRechargeParam;
import cn.rh.flash.sdk.paymentChannel.JDPay.JDPayBean.JDWithdrawParam;
import cn.rh.flash.sdk.paymentChannel.JDPay.JDPayUtil;
import cn.rh.flash.sdk.paymentChannel.KDPay.KDPayBean.KDPayResp;
import cn.rh.flash.sdk.paymentChannel.KDPay.KDPayBean.KDRechargeParam;
import cn.rh.flash.sdk.paymentChannel.KDPay.KDPayBean.KDWithdrawParam;
import cn.rh.flash.sdk.paymentChannel.KDPay.KDPayUtil;
import cn.rh.flash.sdk.paymentChannel.MYPay.MYPayBean.MYPayResp;
import cn.rh.flash.sdk.paymentChannel.MYPay.MYPayBean.MYPayWithdrawResp;
import cn.rh.flash.sdk.paymentChannel.MYPay.MYPayBean.MYRechargeParam;
import cn.rh.flash.sdk.paymentChannel.MYPay.MYPayBean.MYWithdrawParam;
import cn.rh.flash.sdk.paymentChannel.MYPay.MYPayUtil;
import cn.rh.flash.sdk.paymentChannel.Mpay.MPayUtil;
import cn.rh.flash.sdk.paymentChannel.Mpay.dao.Down;
import cn.rh.flash.sdk.paymentChannel.Mpay.dao.Up;
import cn.rh.flash.sdk.paymentChannel.Mpay.mpayBean.MPayData;
import cn.rh.flash.sdk.paymentChannel.Mpay.mpayBean.MPayWithdrawParam;
import cn.rh.flash.sdk.paymentChannel.OKPay.OKPayBean.OKPayResp;
import cn.rh.flash.sdk.paymentChannel.OKPay.OKPayBean.OKRechargeParam;
import cn.rh.flash.sdk.paymentChannel.OKPay.OKPayBean.OKWdOrderDto;
import cn.rh.flash.sdk.paymentChannel.OKPay.OKPayBean.OKWithdrawParam;
import cn.rh.flash.sdk.paymentChannel.OKPay.OKPayUtil;
import cn.rh.flash.sdk.paymentChannel.QNQBPay.QNQBPayBean.QNQBPayRechargeParam;
import cn.rh.flash.sdk.paymentChannel.QNQBPay.QNQBPayBean.QNQBPayResp;
import cn.rh.flash.sdk.paymentChannel.QNQBPay.QNQBPayBean.QNQBPayWithdrawParam;
import cn.rh.flash.sdk.paymentChannel.QNQBPay.QNQBPayBean.QNQBPayWithdrawResp;
import cn.rh.flash.sdk.paymentChannel.QNQBPay.QNQBPayUtil;
import cn.rh.flash.sdk.paymentChannel.WalletPay.WalletPayUtil;
import cn.rh.flash.sdk.paymentChannel.zimu808Pay.Zimu808PayUtil;
import cn.rh.flash.sdk.paymentChannel.zimu808Pay.zimu808Bean.Zimu808RechargeParam;
import cn.rh.flash.sdk.paymentChannel.zimu808Pay.zimu808Bean.Zimu808WithdrawParam;
import cn.rh.flash.sdk.paymentChannel.zimu808Pay.zimu808Bean.ZimuResp;
import cn.rh.flash.sdk.walletAddress.GetUsdtAddr;
import cn.rh.flash.service.dzcredit.CreditRecordService;
import cn.rh.flash.service.dzcredit.UserCreditService;
import cn.rh.flash.service.dzpower.FlowingWaterPbService;
import cn.rh.flash.service.dzpower.RecordPbService;
import cn.rh.flash.service.dzpower.TotalBonusPbService;
import cn.rh.flash.service.dzscore.UserScoreHistoryService;
import cn.rh.flash.service.dzscore.UserScoreService;
import cn.rh.flash.service.dzsys.PaymentChannelService;
import cn.rh.flash.service.dzuser.*;
import cn.rh.flash.service.dzvip.DzVipMessageService;
import cn.rh.flash.service.dzvip.TeamVIPActivationTotalRevenueService;
import cn.rh.flash.service.dzvip.VipPurchaseHistoryService;
import cn.rh.flash.service.dzvip.VipRebateRecordService;
import cn.rh.flash.utils.*;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static java.lang.Double.*;

/**
 * 记录信息
 */
@Log4j2
@Component
public class RecordInformation {

    @Autowired
    private TransactionRecordService transactionRecordService;  //（交易记录）
    @Autowired
    private CompensationRecordService compensationRecordService;  //（彩金记录）
    @Autowired
    private TotalBonusIncomeService totalBonusIncomeService;  //（彩金总收入）

    @Autowired
    private TotalRechargeAmountService totalRechargeAmountService; // （ 充值总金额 ）
    @Autowired
    private RechargeRecordService rechargeRecordService; // （充值记录）

    @Autowired
    private TotalWithdrawalAmountService totalWithdrawalAmountService; // 提现总金额
    @Autowired
    private WithdrawalsRecordService withdrawalsRecordService;  // 提现记录

    @Autowired
    private VipRebateRecordService vipRebateRecordService; // 团队开通vip返佣记录
    @Autowired
    private TeamVIPActivationTotalRevenueService teamVIPActivationTotalRevenueService; // 团队vip开通总返佣

    @Autowired
    private CreditRecordService creditRecordService;  // 用户信誉分记录

    @Autowired
    private UserBalanceService userBalanceService; // 用户余额

    @Autowired
    private UserWalletAddressService userWalletAddressService; // 用户钱包地址

    @Autowired
    private ConfigCache configCache;


    @Autowired
    private VipPurchaseHistoryService vipPurchaseHistoryService; //  vip 购买记录


    @Autowired
    private EhcacheDao ehcacheDao;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private ApiUserCoom apiUserCoom;

    @Autowired
    private DzVipMessageService vipMessageService;

    @Autowired
    private TotalBonusPbService totalBonusPbService;  // 充电宝返佣记录

    @Autowired
    private RecordPbService recordPbService; //充电宝返佣总金额

    @Autowired
    private PaymentChannelService paymentChannelService; // 通道

    @Autowired
    private FlowingWaterPbService flowingWaterPbService;

    @Autowired
    private UserScoreService userScoreService;

    @Autowired
    private UserScoreHistoryService userScoreHistoryService;

    @Autowired
    private MyRedissonLocker myRedissonLocker;

    @Autowired
    private UserBalanceLockLogService userBalanceLockLogService;

    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Autowired
    private UserCreditService userCreditService;


    /**
     * 用户信誉分记录
     *
     * @param sourceInvitationCode 来源邀请码
     * @param uid                  用户id
     * @param account              用户账号
     * @param beforeCredit         之前信誉分
     * @param creditChange         变动信誉分值
     * @param afterCredit          之后信誉分
     * @param chargeStatus         变更类型
     * @param remark               备注
     */
    @Transactional(rollbackFor = Exception.class)
    public void addCreditRecord(String sourceInvitationCode, Long uid, String account, Integer beforeCredit, Integer creditChange, Integer afterCredit, String chargeStatus, String remark) {
        CreditRecord creditRecord = new CreditRecord();
        creditRecord.setIdw(new IdWorker().nextId() + "");
        creditRecord.setSourceInvitationCode(sourceInvitationCode);
        creditRecord.setUid(uid);
        creditRecord.setAccount(account);
        creditRecord.setBefortCredit(beforeCredit);
        creditRecord.setCreditChange(creditChange);
        creditRecord.setAfterCredit(afterCredit);
        creditRecord.setChargeStatus(chargeStatus);
        creditRecord.setRemark(remark);
        creditRecordService.insert(creditRecord);
    }

    /**
     * 用户信誉分记录
     *
     * @param sourceInvitationCode 来源邀请码
     * @param uid                  用户id
     * @param account              用户账号
     * @param beforeCredit         之前信誉分
     * @param creditChange         变动信誉分值
     * @param afterCredit          之后信誉分
     * @param chargeStatus         变更类型
     * @param remark               备注
     * @param fromAccount          来源账号
     */
    @Transactional(rollbackFor = Exception.class)
    public void addCreditRecord(String sourceInvitationCode, Long uid, String account, Integer beforeCredit, Integer creditChange, Integer afterCredit, String chargeStatus, String remark, String fromAccount) {
        CreditRecord creditRecord = new CreditRecord();
        creditRecord.setIdw(new IdWorker().nextId() + "");
        creditRecord.setSourceInvitationCode(sourceInvitationCode);
        creditRecord.setUid(uid);
        creditRecord.setAccount(account);
        creditRecord.setBefortCredit(beforeCredit);
        creditRecord.setCreditChange(creditChange);
        creditRecord.setAfterCredit(afterCredit);
        creditRecord.setChargeStatus(chargeStatus);
        creditRecord.setRemark(remark);
        creditRecord.setFromAccount(fromAccount);
        creditRecordService.insert(creditRecord);
    }

//    /**
//     * @param sourceInvitationCode 来源邀请码
//     * @param uid                  用户id
//     * @param account              用户账号
//     * @param status               信誉分状态
//     * @param chargeStatus         变更类型
//     * @param isAdd                1+ 2-
//     * @param credit               变动信誉分值
//     * @param userName             操作人
//     */
//    // 后管
//    @Transactional(rollbackFor = Exception.class)
//    public void changeCredit(String sourceInvitationCode, Long uid, String account, String status, String chargeStatus, String isAdd, Integer credit, String userName,String vipType) {
//        // 查询用户信誉分
//        UserCredit userCredit = userCreditService.get(SearchFilter.build("account", account));
//        switch (isAdd) {
//            // 上分
//            case "1":
//                if (userCredit == null) {
//                    userCredit = new UserCredit();
//                    userCredit.setIdw(new IdWorker().nextId() + "");
//                    userCredit.setSourceInvitationCode(sourceInvitationCode);
//                    userCredit.setUid(uid);
//                    userCredit.setAccount(account);
//                    userCredit.setCredit(credit);
//                    userCredit.setStatus(status);
//                    addCreditRecord(sourceInvitationCode, uid, account,
//                            0, credit, credit, chargeStatus, ("2".equals(chargeStatus)) ? "操作人:" + userName : "", "管理员");
//                } else {
//                    Integer beforeCredit = userCredit.getCredit();
//                    userCredit.setCredit(beforeCredit + credit);
//                    addCreditRecord(sourceInvitationCode, uid, account,
//                            beforeCredit, credit, userCredit.getCredit(), chargeStatus, ("2".equals(chargeStatus)) ? "操作人:" + userName : "", "管理员");
//                }
//                break;
//            // 下分
//            case "2":
//                Integer beforeCredit = userCredit.getCredit();
//
//                if (beforeCredit - credit < 20) { // 信誉分最低为20分
//                    credit = beforeCredit - 20;
//                }
//
//                userCredit.setCredit(beforeCredit - credit);
//                addCreditRecord(sourceInvitationCode, uid, account,
//                        beforeCredit, credit, userCredit.getCredit(), chargeStatus, ("2".equals(chargeStatus)) ? "操作人:" + userName : "", "管理员");
//                break;
//        }
//        userCreditService.update(userCredit);
//    }

    /**
     * @param sourceInvitationCode 来源邀请码
     * @param uid                  用户id
     * @param account              用户账号
     * @param status               信誉分状态
     * @param chargeStatus         变更类型
     * @param isAdd                1+ 2-
     * @param credit               变动信誉分值
     * @param userName             操作人
     * @param fromAccount          来源账号
     */
    @Transactional(rollbackFor = Exception.class)
    public void changeCredit(String sourceInvitationCode, Long uid, String account, String status, String chargeStatus, String isAdd, Integer credit, String userName, String fromAccount, String vipType) {
        // 查询用户信誉分
        UserCredit userCredit = userCreditService.get(SearchFilter.build("account", account));
        switch (isAdd) {
            // 上分
            case "1":
                if (userCredit == null) {
                    userCredit = new UserCredit();
                    userCredit.setIdw(new IdWorker().nextId() + "");
                    userCredit.setSourceInvitationCode(sourceInvitationCode);
                    userCredit.setUid(uid);
                    userCredit.setVipType(vipType);
                    userCredit.setAccount(account);
                    userCredit.setCredit(credit);
                    userCredit.setStatus(status);
                    addCreditRecord(sourceInvitationCode, uid, account,
                            0, credit, credit, chargeStatus, ("2".equals(chargeStatus)) ? "操作人:" + userName : "", fromAccount);
                } else {
                    Integer beforeCredit = userCredit.getCredit();
                    userCredit.setCredit(beforeCredit + credit);
                    addCreditRecord(sourceInvitationCode, uid, account,
                            beforeCredit, credit, userCredit.getCredit(), chargeStatus, ("2".equals(chargeStatus)) ? "操作人:" + userName : "", fromAccount);
                }
                break;
            // 下分
            case "2":
                Integer beforeCredit = userCredit.getCredit();

                if (beforeCredit - credit < 20) { // 信誉分最低为20分//加拿大站最低0
                    credit = beforeCredit - 20;
                }

                userCredit.setCredit(beforeCredit - credit);
                addCreditRecord(sourceInvitationCode, uid, account,
                        beforeCredit, credit, userCredit.getCredit(), chargeStatus, ("2".equals(chargeStatus)) ? "操作人:" + userName : "", "管理员");
                break;
        }
        userCreditService.update(userCredit);
    }

    /**
     * 充电宝返佣记录
     *
     * @param money                金额
     * @param uid                  用户id
     * @param sourceInvitationCode 用户来源邀请码
     * @param account              用户账号
     */
    @Transactional(rollbackFor = Exception.class)
    public void addRecordPb(Double money, Long uid, String sourceInvitationCode, String account, Date ctime, String suid, Integer levels, String sourceUserAccount) {
        // 查询用户余额
        Double balance = apiUserCoom.getUserBalance(uid).doubleValue();


        RecordPb record = new RecordPb();
        record.setIdw(new IdWorker().nextId() + "");
        record.setSourceInvitationCode(sourceInvitationCode);
        record.setUid(uid);
        record.setAccount(account);
        record.setMoney(money);
        record.setRelevels(levels);
        record.setFormerCreditScore(balance);
        record.setPostCreditScore(BigDecimalUtils.add(balance, record.getMoney()));
        record.setSourceUserAccount(sourceUserAccount);
        record.setRebateTime(ctime);

        Integer tx = transactionRecordPlus(record.getSourceInvitationCode(), record.getUid(), record.getAccount(),
                record.getFormerCreditScore(), record.getMoney(), record.getPostCreditScore(),
                record.getIdw(), 5, "cdb", "", "");

        TotalBonusPb uid1 = totalBonusPbService.get(SearchFilter.build("uid", record.getUid()));

        if (uid1 != null) {
//            record.setFormerCreditScore(balance);
//            record.setPostCreditScore(BigDecimalUtils.add(balance, record.getMoney()));
            uid1.setTotalBonusIncome(BigDecimalUtils.add(uid1.getTotalBonusIncome(), record.getMoney()));
        } else {
//            record.setFormerCreditScore(balance);
//            record.setPostCreditScore(BigDecimalUtils.add(balance, record.getMoney()));
            uid1 = new TotalBonusPb();
            uid1.setIdw(new IdWorker().nextId() + "");
            uid1.setSourceInvitationCode(record.getSourceInvitationCode());
            uid1.setDzversion(0);
            uid1.setUid(record.getUid());
            uid1.setAccount(record.getAccount());
            uid1.setTotalBonusIncome(record.getMoney());
            uid1.setSourceUserAccount(suid);
        }

        if (tx != 0) {
            uid1.setDzversion(tx);
        }
        if (uid1 != null) {
            totalBonusPbService.update(uid1);
        } else {
            totalBonusPbService.insert(uid1);
        }

        recordPbService.insert(record);

    }

/*    //给上级返佣
    @Transactional(rollbackFor = Exception.class)
    public void addRecordPbjy( Double money,Long uid, String sourceInvitationCode,String account,Date ctime ,String suid){

        RecordPb record = new RecordPb();
        record.setIdw( new IdWorker().nextId()+"" );
        record.setSourceInvitationCode( sourceInvitationCode );
        record.setUid( uid );
        record.setAccount( account );
        record.setMoney( money );
        if( ctime !=null ){
            record.setCreateTime( ctime );
        }
        Integer tx = transactionRecordPlus(record.getSourceInvitationCode(), record.getUid(), record.getAccount(),
                record.getFormerCreditScore(), record.getMoney(), record.getPostCreditScore(),
                record.getIdw(), 5, "cdbyj");

        TotalBonusPb uid1 = totalBonusPbService.get( SearchFilter.build( "uid", record.getUid() ) );
        if( uid1 != null ) {

            record.setFormerCreditScore( uid1.getTotalBonusIncome() );
            record.setPostCreditScore(BigDecimalUtils.subtract(record.getFormerCreditScore(), record.getMoney()));

            uid1.setTotalBonusIncome( record.getPostCreditScore() );
            if( tx != 0 ){
                uid1.setDzversion( tx );
            }
            totalBonusPbService.update( uid1 );
        }else{

            record.setFormerCreditScore( 0D );
            record.setPostCreditScore( record.getMoney() );

            uid1 = new TotalBonusPb();
            uid1.setIdw( new IdWorker().nextId()+"" );
            uid1.setSourceInvitationCode( record.getSourceInvitationCode() );
            uid1.setDzversion(0);
            uid1.setUid( record.getUid() );
            uid1.setAccount( record.getAccount() );
            uid1.setTotalBonusIncome( record.getMoney() );
            uid1.setSourceUserAccount( suid );
            if( tx != 0 ){
                uid1.setDzversion( tx );
            }
            totalBonusPbService.insert( uid1 );
        }

        recordPbService.insert( record );

    }*/

    /**
     * 循环给父级 父父级 父父父级添加返佣 默认盘口
     * vip售价(自己跟上级相比取低的vip价格)*上级用户vip的返佣比例
     *
     * @param sourceUser   来源用户
     * @param taskOrderIdw Vip购买记录 idw  vipPurchaseHistory
     */
    @Transactional(rollbackFor = Exception.class)
    public void cycleReward(UserInfo sourceUser, String taskOrderIdw, int buyVip, String previousViPType, String vipType) {
//        UserInfo parentUser = userInfoService.get(SearchFilter.build("invitationCode", sourceUser.getSuperiorInvitationCode() ));
        UserInfo parentUser = apiUserCoom.getOneBySql(sourceUser.getSuperiorInvitationCode());
//        int vip = Integer.parseInt(sourceUser.getVipType().replace("v", ""));
        for (int i = 0; i < 10; i++) {

            if (parentUser != null) {

                // skj  限制 开通vip 收益
                if (parentUser.getLimitProfit() != null && parentUser.getLimitProfit() == 1) {
                    //return Rets.failure( MessageTemplateEnum.THE_ACCOUNT_IS_DISABLED.getCode() ,MessageTemplateEnum.THE_ACCOUNT_IS_DISABLED );
                    return;
                }


                int currentVip = Integer.parseInt(parentUser.getVipType().replace("v", ""));
                int realVip = Math.min(buyVip, currentVip);
                DzVipMessage vipMessage = vipMessageService.get(SearchFilter.build("vipType", "v" + realVip)); // 价格 取低的

                if (vipMessage != null) {
                    Double sellingPrice = vipMessage.getSellingPrice();
                    // 没设置返佣 不添加记录
                    double realReward = 0.00;

                    // 接收 返佣这个 的当前 vip等级
                    DzVipMessage vipMessageTop = vipMessageService.get(SearchFilter.build("vipType", parentUser.getVipType())); // 价格 取低的

                    switch (i) {
                        case 0:
                            realReward = (ObjUtil.isEmpty(vipMessageTop.getL1OpenVipRebate())) ? 0.00 : vipMessageTop.getL1OpenVipRebate();
                            break;
                        case 1:
                            realReward = (ObjUtil.isEmpty(vipMessageTop.getL2OpenVipRebate())) ? 0.00 : vipMessageTop.getL2OpenVipRebate();
                            break;
                        case 2:
                            realReward = (ObjUtil.isEmpty(vipMessageTop.getL3OpenVipRebate())) ? 0.00 : vipMessageTop.getL3OpenVipRebate();
                            break;
                        case 3:
                            realReward = (ObjUtil.isEmpty(vipMessageTop.getL4OpenVipRebate())) ? 0.00 : vipMessageTop.getL4OpenVipRebate();
                            break;
                        case 4:
                            realReward = (ObjUtil.isEmpty(vipMessageTop.getL5OpenVipRebate())) ? 0.00 : vipMessageTop.getL5OpenVipRebate();
                            break;
                        case 5:
                            realReward = (ObjUtil.isEmpty(vipMessageTop.getL6OpenVipRebate())) ? 0.00 : vipMessageTop.getL6OpenVipRebate();
                            break;
                        case 6:
                            realReward = (ObjUtil.isEmpty(vipMessageTop.getL7OpenVipRebate())) ? 0.00 : vipMessageTop.getL7OpenVipRebate();
                            break;
                        case 7:
                            realReward = (ObjUtil.isEmpty(vipMessageTop.getL8OpenVipRebate())) ? 0.00 : vipMessageTop.getL8OpenVipRebate();
                            break;
                        case 8:
                            realReward = (ObjUtil.isEmpty(vipMessageTop.getL9OpenVipRebate())) ? 0.00 : vipMessageTop.getL9OpenVipRebate();
                            break;
                        case 9:
                            realReward = (ObjUtil.isEmpty(vipMessageTop.getL10OpenVipRebate())) ? 0.00 : vipMessageTop.getL10OpenVipRebate();
                            break;
                    }
                    Double money = BigDecimalUtils.multiply(sellingPrice, realReward);
                    if (money > 0.00) {
                        Integer relevels = sourceUser.getLevels() - parentUser.getLevels();
                        addOpenVip(
                                // jk  2023年1月9日  添加vip变化
                                previousViPType, vipType,
                                // end
                                money, parentUser.getId(), sourceUser.getAccount(), parentUser.getSourceInvitationCode(), parentUser.getAccount(), relevels);
                    }

                    // 返佣到达顶级结束
                    if (parentUser.getInvitationCode().equals(parentUser.getSuperiorInvitationCode())) {
                        return;
                    }
                } else {
                    log.info("上级用户电话{},当前等级为v0,无法接受收益,当前时间:{}", parentUser.getAccount(), DateUtil.getTime());
                    return;
                }
                parentUser = apiUserCoom.getOneBySql(parentUser.getSuperiorInvitationCode());
            }
        }

    }

    /**
     * 循环给父级 父父级 父父父级添加返佣 (西班牙  埃及1  黄色单车)
     * vip售价(自己跟上级相比取低的vip)*(自己跟上级相比取低的vip)vip返佣比例
     *
     * @param sourceUser   来源用户
     * @param taskOrderIdw Vip购买记录 idw  vipPurchaseHistory
     */
    @Transactional(rollbackFor = Exception.class)
    public void cycleRewardTwo(UserInfo sourceUser, String taskOrderIdw, int buyVip, String previousViPType, String vipType) {
//        UserInfo parentUser = userInfoService.get(SearchFilter.build("invitationCode", sourceUser.getSuperiorInvitationCode() ));
        UserInfo parentUser = apiUserCoom.getOneBySql(sourceUser.getSuperiorInvitationCode());
//        int vip = Integer.parseInt(sourceUser.getVipType().replace("v", ""));
        for (int i = 0; i < 10; i++) {

            if (parentUser != null) {

                // skj  限制 开通vip 收益
                if (parentUser.getLimitProfit() != null && parentUser.getLimitProfit() == 1) {
                    //return Rets.failure( MessageTemplateEnum.THE_ACCOUNT_IS_DISABLED.getCode() ,MessageTemplateEnum.THE_ACCOUNT_IS_DISABLED );
                    return;
                }


                int currentVip = Integer.parseInt(parentUser.getVipType().replace("v", ""));
                int realVip = Math.min(buyVip, currentVip);
                DzVipMessage vipMessage = vipMessageService.get(SearchFilter.build("vipType", "v" + realVip)); // 价格 取低的

                if (vipMessage != null) {
                    Double sellingPrice = vipMessage.getSellingPrice();
                    // 没设置返佣 不添加记录
                    double realReward = 0.00;
                    switch (i) {
                        case 0:
                            realReward = (ObjUtil.isEmpty(vipMessage.getL1OpenVipRebate())) ? 0.00 : vipMessage.getL1OpenVipRebate();
                            break;
                        case 1:
                            realReward = (ObjUtil.isEmpty(vipMessage.getL2OpenVipRebate())) ? 0.00 : vipMessage.getL2OpenVipRebate();
                            break;
                        case 2:
                            realReward = (ObjUtil.isEmpty(vipMessage.getL3OpenVipRebate())) ? 0.00 : vipMessage.getL3OpenVipRebate();
                            break;
                        case 3:
                            realReward = (ObjUtil.isEmpty(vipMessage.getL4OpenVipRebate())) ? 0.00 : vipMessage.getL4OpenVipRebate();
                            break;
                        case 4:
                            realReward = (ObjUtil.isEmpty(vipMessage.getL5OpenVipRebate())) ? 0.00 : vipMessage.getL5OpenVipRebate();
                            break;
                        case 5:
                            realReward = (ObjUtil.isEmpty(vipMessage.getL6OpenVipRebate())) ? 0.00 : vipMessage.getL6OpenVipRebate();
                            break;
                        case 6:
                            realReward = (ObjUtil.isEmpty(vipMessage.getL7OpenVipRebate())) ? 0.00 : vipMessage.getL7OpenVipRebate();
                            break;
                        case 7:
                            realReward = (ObjUtil.isEmpty(vipMessage.getL8OpenVipRebate())) ? 0.00 : vipMessage.getL8OpenVipRebate();
                            break;
                        case 8:
                            realReward = (ObjUtil.isEmpty(vipMessage.getL9OpenVipRebate())) ? 0.00 : vipMessage.getL9OpenVipRebate();
                            break;
                        case 9:
                            realReward = (ObjUtil.isEmpty(vipMessage.getL10OpenVipRebate())) ? 0.00 : vipMessage.getL10OpenVipRebate();
                            break;
                    }
                    Double money = BigDecimalUtils.multiply(sellingPrice, realReward);
                    if (money > 0.00) {
                        Integer relevels = sourceUser.getLevels() - parentUser.getLevels();
                        addOpenVip(
                                // jk  2023年1月9日  添加vip变化
                                previousViPType, vipType,
                                // end
                                money, parentUser.getId(), sourceUser.getAccount(), parentUser.getSourceInvitationCode(), parentUser.getAccount(), relevels);
                    }

                    // 返佣到达顶级结束
                    if (parentUser.getInvitationCode().equals(parentUser.getSuperiorInvitationCode())) {
                        return;
                    }
                } else {
                    log.info("上级用户电话{},当前等级为v0,无法接受收益,当前时间:{}", parentUser.getAccount(), DateUtil.getTime());
                    return;
                }
                parentUser = apiUserCoom.getOneBySql(parentUser.getSuperiorInvitationCode());
            }
        }

    }


    /**
     * 直冲之扣
     *
     * @param money    //  金额
     * @param type     //  1+  2-
     * @param operator 操作人名
     * @param userInfo
     */
    @Transactional(rollbackFor = Exception.class)
    public void straightToTheMouth(Double money, Integer type, String remark, String operator, UserInfo userInfo) {

        // 查询用户余额
        Double balance = apiUserCoom.getUserBalance(userInfo.getId()).doubleValue();

        CompensationRecord combean = new CompensationRecord();
        combean.setIdw(new IdWorker().nextId() + "");
        combean.setSourceInvitationCode(userInfo.getSourceInvitationCode());
        combean.setUid(userInfo.getId());
        combean.setAccount(userInfo.getAccount());
        combean.setAdditionAndSubtraction(type);  //  1+  2-
        combean.setOperator(operator);
        combean.setMoney(money);

        TotalBonusIncome byUid = totalBonusIncomeService.get(SearchFilter.build("uid", combean.getUid()));
        if (byUid != null) {
            combean.setFormerCreditScore(balance);
            switch (type) {
                case 1:
                    combean.setPostCreditScore(BigDecimalUtils.add(balance, combean.getMoney()));
                    byUid.setTotalBonusIncome(BigDecimalUtils.add(byUid.getTotalBonusIncome(), combean.getMoney()));
                    break;
                case 2:
                    combean.setPostCreditScore(BigDecimalUtils.subtract(balance, combean.getMoney()));
                    byUid.setTotalBonusIncome(BigDecimalUtils.subtract(byUid.getTotalBonusIncome(), combean.getMoney()));
                    break;
            }

//            byUid.setTotalBonusIncome(combean.getPostCreditScore());
        } else {
            combean.setFormerCreditScore(0D);
            combean.setPostCreditScore(combean.getMoney());

            byUid = new TotalBonusIncome();

            byUid.setIdw(new IdWorker().nextId() + "");
            byUid.setSourceInvitationCode(combean.getSourceInvitationCode());
            byUid.setDzversion(0);
            byUid.setUid(combean.getUid());
            byUid.setAccount(combean.getAccount());
            byUid.setTotalBonusIncome(combean.getMoney());
        }

        Integer zc = 0;

        //  添加交易记录
        switch (combean.getAdditionAndSubtraction()) {
            case 1:
                zc = transactionRecordPlus(combean.getSourceInvitationCode(), combean.getUid(), combean.getAccount(),
                        combean.getFormerCreditScore(), combean.getMoney(), combean.getPostCreditScore(),
                        combean.getIdw(), 3, "zc", remark, "");

                break;
            case 2:
                zc = transactionRecordMinus(combean.getSourceInvitationCode(), combean.getUid(), combean.getAccount(),
                        combean.getFormerCreditScore(), combean.getMoney(), combean.getPostCreditScore(),
                        combean.getIdw(), 4, "zk", remark, "");
                break;

        }
        if (zc != 0) {
            byUid.setDzversion(zc);
        }
        if (byUid != null) {
            totalBonusIncomeService.update(byUid);  // 彩金总收入
        } else {
            totalBonusIncomeService.insert(byUid);

            // 彩金总收入
        }
        compensationRecordService.insert(combean);  //彩金记录
    }

    /**
     * 充值记录
     *
     * @param money                充值金额
     * @param channelName          通道
     * @param withdrawalAddress    充值地址
     * @param uid                  用户id
     * @param sourceInvitationCode 用户来源邀请码
     * @param account              用户账号
     * @param status               充值状态 1:进行中,2:待回掉,3:已完成
     */
    @Transactional(rollbackFor = Exception.class)
    public RechargeRecord addRechargeRecord(Double money, String channelName, String withdrawalAddress,String channelType, Long uid, String sourceInvitationCode, String account, String status, String orderNum, String firstCharge) {
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setIdw(new IdWorker().nextId() + "");
        rechargeRecord.setSourceInvitationCode(sourceInvitationCode);
        rechargeRecord.setUid(uid);
        rechargeRecord.setAccount(account);
        rechargeRecord.setOrderNumber(orderNum);
        rechargeRecord.setMoney(money);
        rechargeRecord.setChannelName(channelName);
        rechargeRecord.setRechargeStatus(status);  // 充值状态 1:进行中,2:待回掉,3:已完成
        rechargeRecord.setWithdrawalAddress(withdrawalAddress);
        rechargeRecord.setChannelType(channelType);
        if (StringUtil.isNotEmpty(firstCharge)) {
            rechargeRecord.setFirstCharge(firstCharge);
        }

        // 查询用户余额
        Double balance = apiUserCoom.getUserBalance(uid).doubleValue();

        rechargeRecord.setPreviousBalance(balance);
        rechargeRecord.setAfterBalance(BigDecimalUtils.add(balance, money));

        rechargeRecordService.insert(rechargeRecord);
        return rechargeRecord;
    }

    /**
     * 修改充值状态 冰更新总充值金额
     *
     * @param rechargeRecord
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public void upOkRechargeRecord(RechargeRecord rechargeRecord) {


        TotalRechargeAmount totalRechargeAmount = totalRechargeAmountService.get(SearchFilter.build("uid", rechargeRecord.getUid()));

        // 查询用户余额
        Double balance = apiUserCoom.getUserBalance(rechargeRecord.getUid()).doubleValue();

        rechargeRecord.setPreviousBalance(balance);
        rechargeRecord.setAfterBalance(BigDecimalUtils.add(balance, rechargeRecord.getMoney()));

        if (totalRechargeAmount != null) {
            //充值总记录有值，增加金额

//            rechargeRecord.setPreviousBalance(balance);
//            rechargeRecord.setAfterBalance(BigDecimalUtils.add(balance, rechargeRecord.getMoney()));

            totalRechargeAmount.setTotalRechargeAmount(BigDecimalUtils.add(totalRechargeAmount.getTotalRechargeAmount(), rechargeRecord.getMoney()));

            totalRechargeAmountService.update(totalRechargeAmount);
        } else {
//            rechargeRecord.setPreviousBalance(0D);
//            rechargeRecord.setAfterBalance(rechargeRecord.getMoney());

            //充值总记录没有，新建
            totalRechargeAmount = new TotalRechargeAmount();

            totalRechargeAmount.setIdw(new IdWorker().nextId() + "");
            totalRechargeAmount.setSourceInvitationCode(rechargeRecord.getSourceInvitationCode());
            totalRechargeAmount.setDzversion(0);
            totalRechargeAmount.setUid(rechargeRecord.getUid());
            totalRechargeAmount.setAccount(rechargeRecord.getAccount());
            totalRechargeAmount.setTotalRechargeAmount(rechargeRecord.getMoney());

            totalRechargeAmountService.insert(totalRechargeAmount);
        }
        rechargeRecord.setRechargeStatus("3");
        rechargeRecordService.update(rechargeRecord);
    }


    /**
     * 提现记录
     *
     * @param money                金额
     * @param handlingFee          手续费
     * @param channelName          通道
     * @param withdrawalAddress    提现地址
     * @param uid                  用户id
     * @param sourceInvitationCode 用户来源邀请码
     * @param account              用户账号
     * @param transactionNumber    交易编号  可以为空
     * @param remark               备注 可以为空
     */
    @Transactional(rollbackFor = Exception.class)
    public WithdrawalsRecord addWithdrawalRecord(Double money, Double handlingFee, String channelName, String withdrawalAddress, Long uid, String sourceInvitationCode, String account, String transactionNumber, String remark, String channelType) {


        // end

        WithdrawalsRecord withdrawalsRecord = new WithdrawalsRecord();
        withdrawalsRecord.setIdw(new IdWorker().nextId() + "");
        withdrawalsRecord.setSourceInvitationCode(sourceInvitationCode);
        withdrawalsRecord.setUid(uid);
        withdrawalsRecord.setAccount(account);
        withdrawalsRecord.setOrderNumber(MakeOrderNum.makeOrderNum("tx"));
        withdrawalsRecord.setTransactionNumber(transactionNumber);  // 没有传空
        withdrawalsRecord.setChannelName(channelName);
        withdrawalsRecord.setRechargeStatus("no");   //  审核状态 ok:已审核,no:审核中,er:已拒绝,suc:已成功,exit:已退款,sysok:订单通过不出款
        withdrawalsRecord.setMoney(money);
        withdrawalsRecord.setRemark(remark);  // // 没有传空
        withdrawalsRecord.setHandlingFee(handlingFee);
        withdrawalsRecord.setAmountReceived(BigDecimalUtils.subtract(money, handlingFee));
        withdrawalsRecord.setWithdrawalAddress(withdrawalAddress);
        withdrawalsRecord.setChannelType(channelType);

        // 查询用户上次提现地址
        String upWithdrawalAddress = withdrawalsRecordService.findAddressById(withdrawalsRecord.getUid());
        //
        withdrawalsRecord.setUpWithdrawalAddress(upWithdrawalAddress);

        // 查询用户余额
        Double balance = apiUserCoom.getUserBalance(withdrawalsRecord.getUid()).doubleValue();

        withdrawalsRecord.setPreviousBalance(balance);
        withdrawalsRecord.setAfterBalance(
                BigDecimalUtils.subtract(
                        balance,
                        withdrawalsRecord.getMoney()
//                        withdrawalsRecord.getHandlingFee()
                )
        );
        TotalWithdrawalAmount withdrawalAmount = totalWithdrawalAmountService.get(SearchFilter.build("uid", withdrawalsRecord.getUid()));
        // 前金额 - 后金额 = 提现金额+手续费
//        Double subtract = BigDecimalUtils.subtract(withdrawalsRecord.getPreviousBalance(), withdrawalsRecord.getAfterBalance());

        if (withdrawalAmount == null) {
            withdrawalAmount = new TotalWithdrawalAmount();
            withdrawalAmount.setIdw(new IdWorker().nextId() + "");
            withdrawalAmount.setSourceInvitationCode(withdrawalsRecord.getSourceInvitationCode());
            withdrawalAmount.setUid(withdrawalsRecord.getUid());
            withdrawalAmount.setAccount(withdrawalsRecord.getAccount());
            withdrawalAmount.setTotalWithdrawalAmount(money);
        } else {
            withdrawalAmount.setTotalWithdrawalAmount(BigDecimalUtils.add(withdrawalAmount.getTotalWithdrawalAmount(), money));
        }

        Integer tx = transactionRecordMinus(withdrawalsRecord.getSourceInvitationCode(), withdrawalsRecord.getUid(), withdrawalsRecord.getAccount(),
                withdrawalsRecord.getPreviousBalance(), money, withdrawalsRecord.getAfterBalance(),
                withdrawalsRecord.getIdw(), 2, "tx", "提现金额:" + withdrawalsRecord.getMoney() + "--手续费:" + withdrawalsRecord.getHandlingFee(), "");

//        TotalWithdrawalAmount uid1 = totalWithdrawalAmountService.get(SearchFilter.build("uid", withdrawalsRecord.getUid()));
//        if( uid1 != null ) {
//
//            withdrawalsRecord.setPreviousBalance( uid1.getTotalWithdrawalAmount() );
//            withdrawalsRecord.setAfterBalance(
//                    BigDecimalUtils.subtract(
//                            withdrawalsRecord.getPreviousBalance(),
//                            withdrawalsRecord.getMoney(),
//                            withdrawalsRecord.getHandlingFee()
//                    )
//            );
//
//            uid1.setTotalWithdrawalAmount( withdrawalsRecord.getAfterBalance() );
//            if( tx != 0 ){
//                uid1.setDzversion( tx );
//            }
//            totalWithdrawalAmountService.update( uid1 );
//        }else{
//
//            withdrawalsRecord.setPreviousBalance( 0D );
//            withdrawalsRecord.setAfterBalance(
//                    BigDecimalUtils.subtract(
//                            withdrawalsRecord.getMoney(),
//                            withdrawalsRecord.getHandlingFee()
//                    )
//            );
//
//            uid1 = new TotalWithdrawalAmount();
//            uid1.setIdw( new IdWorker().nextId()+"" );
//            uid1.setSourceInvitationCode( withdrawalsRecord.getSourceInvitationCode() );
//            uid1.setDzversion(0);
//            uid1.setUid( withdrawalsRecord.getUid() );
//            uid1.setAccount( withdrawalsRecord.getAccount() );
//            uid1.setTotalWithdrawalAmount( withdrawalsRecord.getMoney() );
//            if( tx != 0 ){
//                uid1.setDzversion( tx );
//            }
//            totalWithdrawalAmountService.insert( uid1 );
//        }


        if (withdrawalAmount == null) {

            withdrawalAmount.setDzversion(tx);
            totalWithdrawalAmountService.insert(withdrawalAmount);
        } else {
            if (tx != 0) {
                withdrawalAmount.setDzversion(tx);
            }
            totalWithdrawalAmountService.update(withdrawalAmount);
        }
        withdrawalsRecordService.insert(withdrawalsRecord);
        return withdrawalsRecord;
    }

    @Transactional(rollbackFor = Exception.class)
    public WithdrawalsRecord refuseWithdrawalRecord(WithdrawalsRecord withdrawalsRecord) {
        // 查询用户余额
        Double balance = apiUserCoom.getUserBalance(withdrawalsRecord.getUid()).doubleValue();

        TotalWithdrawalAmount uid1 = totalWithdrawalAmountService.get(SearchFilter.build("uid", withdrawalsRecord.getUid()));
        if (uid1 != null) {

            withdrawalsRecord.setPreviousBalance(balance);
            withdrawalsRecord.setAfterBalance(
                    BigDecimalUtils.add(
                            balance,
                            withdrawalsRecord.getMoney()
//                            withdrawalsRecord.getHandlingFee()
                    )
            );
            // 前金额 - 后金额 = 提现金额+手续费
//            Double subtract = BigDecimalUtils.subtract(withdrawalsRecord.getAfterBalance(),withdrawalsRecord.getPreviousBalance());
            uid1.setTotalWithdrawalAmount(BigDecimalUtils.subtract(uid1.getTotalWithdrawalAmount(), withdrawalsRecord.getMoney()));

            ///666666666666666666666666
            Integer tx = transactionRecordPlus(withdrawalsRecord.getSourceInvitationCode(), withdrawalsRecord.getUid(), withdrawalsRecord.getAccount(),
                    withdrawalsRecord.getPreviousBalance(), withdrawalsRecord.getMoney(), withdrawalsRecord.getAfterBalance(),
                    withdrawalsRecord.getIdw(), 2, "tx", withdrawalsRecord.getRemark(), "");
            if (tx != 0) {
                uid1.setDzversion(tx);
            }
            totalWithdrawalAmountService.update(uid1);
        }

        return withdrawalsRecord;
    }


    /**
     * 开通vip返佣记录
     *
     * @param money                金额
     * @param uid                  用户id
     * @param sourceAccount        来源用户账号
     * @param sourceInvitationCode 团队邀请码
     * @param account              用户账号
     * @param relevels             相对层级
     */
    @Transactional(rollbackFor = Exception.class)
    public void addOpenVip(String sourceUserVipType, String vipType, Double money, Long uid, String sourceAccount, String sourceInvitationCode, String account, Integer relevels) {
        // 查询用户余额
        Double balance = apiUserCoom.getUserBalance(uid).doubleValue();

        VipRebateRecord vipRebateRecord = new VipRebateRecord();

        // jk  2023年1月9日
        vipRebateRecord.setOldVipType(sourceUserVipType);
        vipRebateRecord.setNewVipType(vipType);
        // end

        vipRebateRecord.setIdw(new IdWorker().nextId() + "");
        vipRebateRecord.setSourceInvitationCode(sourceInvitationCode);
        vipRebateRecord.setUid(uid);
        vipRebateRecord.setAccount(account);
        vipRebateRecord.setMoney(money);
        vipRebateRecord.setSourceUserAccount(sourceAccount);

        vipRebateRecord.setRelevels(relevels);

        TeamVIPActivationTotalRevenue uid1 = teamVIPActivationTotalRevenueService.get(SearchFilter.build("uid", vipRebateRecord.getUid()));
        vipRebateRecord.setPreviousAmount(balance);
        vipRebateRecord.setAmountAfter(BigDecimalUtils.add(balance, vipRebateRecord.getMoney()));
        if (uid1 != null) {
            uid1.setTeamVIPOpeningTotalRebate(BigDecimalUtils.add(uid1.getTeamVIPOpeningTotalRebate(), vipRebateRecord.getMoney()));

        } else {
//            vipRebateRecord.setPreviousAmount(0D);
//            vipRebateRecord.setAmountAfter(vipRebateRecord.getMoney());
            uid1 = new TeamVIPActivationTotalRevenue();
            uid1.setIdw(new IdWorker().nextId() + "");
            uid1.setSourceInvitationCode(vipRebateRecord.getSourceInvitationCode());
            uid1.setUid(vipRebateRecord.getUid());
            uid1.setAccount(vipRebateRecord.getAccount());
            uid1.setTeamVIPOpeningTotalRebate(vipRebateRecord.getMoney());

        }


        Integer tdvip = transactionRecordPlus(vipRebateRecord.getSourceInvitationCode(), vipRebateRecord.getUid(), vipRebateRecord.getAccount(),
                vipRebateRecord.getPreviousAmount(), vipRebateRecord.getMoney(), vipRebateRecord.getAmountAfter(),
                vipRebateRecord.getIdw(), 8, "tdvip", "", "");


        if (uid1 != null) {

            if (tdvip != 0) {
                uid1.setDzversion(tdvip);
            }
            teamVIPActivationTotalRevenueService.update(uid1);
        } else {

            uid1.setDzversion(tdvip);

            teamVIPActivationTotalRevenueService.insert(uid1);
        }
        vipRebateRecordService.insert(vipRebateRecord);

    }

    /**
     * 自动
     * 交易记录加
     * fidw : 造假数据主键  正常数据为空
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer transactionRecordPlus(String sourceInvitationCode, Long uid, String account, Double preMoney, Double getMoney, Double sufMoney, String transactionNumber, Integer transactionType, String pre, String remark, String fidw) {
        return transactionRecord(sourceInvitationCode, uid, account, preMoney, getMoney, sufMoney, transactionNumber, transactionType, pre, 1, remark, false, fidw);
    }

    /**
     * 自动
     * 交易记录减
     * fidw : 造假数据主键  正常数据为空
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer transactionRecordMinus(String sourceInvitationCode, Long uid, String account, Double preMoney, Double getMoney, Double sufMoney, String transactionNumber, Integer transactionType, String pre, String remark, String fidw) {
        return transactionRecord(sourceInvitationCode, uid, account, preMoney, getMoney, sufMoney, transactionNumber, transactionType, pre, 2, remark, false, fidw);
    }


    /**
     * 手动领取  ( 支持批量  )
     * 交易记录加2
     * fidw : 造假数据主键  正常数据为空
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer transactionRecordPlus2(String sourceInvitationCode, Long uid, String account, Double preMoney, Double getMoney, Double sufMoney, String transactionNumber, Integer transactionType, String pre, String remark, String fidw) {
        long l = new IdWorker().nextId();
        return transactionRecord(sourceInvitationCode, uid, account, preMoney, getMoney, sufMoney, transactionNumber, transactionType, pre, 1, remark, true, fidw);
    }

    /**
     * 手动领取 ( 支持批量 )
     * 交易记录减2
     * fidw : 造假数据主键  正常数据为空
     */
    @Transactional(rollbackFor = Exception.class)
    public Integer transactionRecordMinus2(String sourceInvitationCode, Long uid, String account, Double preMoney, Double getMoney, Double sufMoney, String transactionNumber, Integer transactionType, String pre, String remark, String fidw) {
        long l = new IdWorker().nextId();
        return transactionRecord(sourceInvitationCode, uid, account, preMoney, getMoney, sufMoney, transactionNumber, transactionType, pre, 2, remark, true, fidw);
    }

    /**
     * 交易记录
     *
     * @param sourceInvitationCode   来源编号
     * @param uid                    用户id
     * @param account                用户账号
     * @param preMoney               前余额
     * @param getMoney               金额
     * @param sufMoney               后余额
     * @param transactionNumber      编号
     * @param transactionType        交易编号   1:通道一充值,2:通道一提现,3:平台赠送,4:平台扣款,5:充电宝返佣,8.vip开通返佣 9团队任务收益 10 购买vip ,11 注册,14 捐献, 15补助
     * @param pre                    订单前缀
     * @param additionAndSubtraction 1+  2-
     */
    public Integer transactionRecord(String sourceInvitationCode, Long uid, String account, Double preMoney, Double getMoney,
                                     Double sufMoney, String transactionNumber, Integer transactionType,
                                     String pre, Integer additionAndSubtraction, String remark, boolean flg, String fidw) {
        TransactionRecord transactionObj = new TransactionRecord();
        transactionObj.setIdw(new IdWorker().nextId() + "");
        transactionObj.setSourceInvitationCode(sourceInvitationCode);
        transactionObj.setUid(uid);
        transactionObj.setAccount(account);
        transactionObj.setOrderNumber(MakeOrderNum.makeOrderNum(pre));
        transactionObj.setTransactionNumber(transactionNumber);
        transactionObj.setMoney(getMoney);
        transactionObj.setPreviousBalance(preMoney);
        transactionObj.setAfterBalance(sufMoney);
        if ("signupBonus".equals(remark)) {
            transactionObj.setTransactionType("11");
        } else {
            transactionObj.setTransactionType(transactionType + "");  //  1:通道一充值,2:通道一提现,3:平台赠送,4:平台扣款,5:任务收益,6:转入共享余额,7:转出共享余额
        }
        transactionObj.setAdditionAndSubtraction(additionAndSubtraction);
        transactionObj.setRemark(remark);
        transactionObj.setFidw(fidw);
        Map<String, Object> stringObjectMap = updateUserBalance2(sourceInvitationCode, uid, account, getMoney, additionAndSubtraction, flg);
        transactionRecordService.insert(transactionObj);  //交易记录
        return (Integer) stringObjectMap.get("v");

    }

    /**
     * 交易记录(造假数据专用  请勿调用)
     */
    public Integer transactionRecordFalseDate(String sourceInvitationCode, Long uid, String account, Double preMoney, Double getMoney,
                                              Double sufMoney, String transactionNumber, Integer transactionType, String pre, String remark, String fidw, String dateTime) {
        TransactionRecord transactionObj = new TransactionRecord();
        transactionObj.setIdw(new IdWorker().nextId() + "");
        transactionObj.setSourceInvitationCode(sourceInvitationCode);
        transactionObj.setUid(uid);
        transactionObj.setAccount(account);
        transactionObj.setOrderNumber(MakeOrderNum.makeOrderNum(pre, DateUtil.parseTime(dateTime)));
        transactionObj.setTransactionNumber(transactionNumber);
        transactionObj.setMoney(getMoney);
        transactionObj.setPreviousBalance(preMoney);
        transactionObj.setAfterBalance(sufMoney);
        if ("signupBonus".equals(remark)) {
            transactionObj.setTransactionType("11");
        } else {
            transactionObj.setTransactionType(transactionType + "");  //  1:通道一充值,2:通道一提现,3:平台赠送,4:平台扣款,5:任务收益,6:转入共享余额,7:转出共享余额
        }
        transactionObj.setAdditionAndSubtraction(1);
        transactionObj.setRemark(remark);
        transactionObj.setFidw(fidw);
        Map<String, Object> stringObjectMap = updateUserBalance2(sourceInvitationCode, uid, account, getMoney, 1, true);
//        transactionRecordService.insert(transactionObj);  //交易记录
        transactionRecordRepository.execute(TransactionRecordServiceSql.insertTransactionRecord(transactionObj, dateTime));
        return (Integer) stringObjectMap.get("v");

    }


    /**
     * vip 购买记录
     *
     * @param uid             用户id
     * @param account         用户账号
     * @param previousViPType 当前vip类型
     * @param dzVipMessage    Vip信息
     * @param paymentMethod   支付方式  1:余额支付,2:USDT
     * @param orderNumber
     * @return 1 当前用户余额=0   2 当前用户余额不足   3 vip信息不存在  4 ok 5 充值订单等回调
     */
    @Transactional(rollbackFor = Exception.class)
    public int vipPurchase(String channelName,String channelType, Long uid, String account, String previousViPType, Double aDouble, DzVipMessage dzVipMessage, Integer paymentMethod, String orderNumber, String sourceInvitationCode) {

        if (dzVipMessage == null) {
            log.warn(String.format("vip信息不存在 dzVipMessage： %s", dzVipMessage));
            return 3;
        }


        VipPurchaseHistory vipobj = new VipPurchaseHistory();
        vipobj.setIdw(orderNumber);
        vipobj.setSourceInvitationCode(sourceInvitationCode);
        vipobj.setUid(uid);
        vipobj.setAccount(account);
        vipobj.setPreviousViPType(previousViPType);
        vipobj.setAfterViPType(dzVipMessage.getVipType());
        vipobj.setPaymentAmount(aDouble);
        vipobj.setPaymentMethod(paymentMethod);
        vipobj.setNumberOfTasks(dzVipMessage.getNumberOfTasks());
        vipobj.setDailyIncome(dzVipMessage.getDailyIncome());
        vipobj.setValidDate(dzVipMessage.getValidDate());
        vipobj.setWhetherToPay(1);
        vipobj.setChannelName(channelName);
        vipobj.setChannelType(channelType);


        if (paymentMethod == 1) {

            UserBalance userBalance = userBalanceService.get(SearchFilter.build("uid", uid));
            if (userBalance == null) {
                log.warn(String.format("没有余额 UserBalance： %s", userBalance));
                return 1;
            }

            double v = userBalance.getUserBalance() == null ? 0 : userBalance.getUserBalance();
            if (v < aDouble) {
                log.warn(String.format("余额不足 uid： %s", userBalance.getUid()));
                return 2;
            }

            vipobj.setWhetherToPay(2);

            if (aDouble > 0.00) {
                transactionRecordMinus(dzVipMessage.getSourceInvitationCode(), uid, account,
                        v, aDouble, BigDecimalUtils.subtract(v, aDouble),
                        new IdWorker().nextId() + "", 10, "vip", "", "");
            }
            VipPurchaseHistory insert = vipPurchaseHistoryService.insert(vipobj);

            return 4;
        }

        vipPurchaseHistoryService.insert(vipobj);

        return 5;
    }

    //更新用户余额 2
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateUserBalance2(String sourceInvitationCode, Long uid, String account, Double money, int additionAndSubtraction, boolean flg) throws ApiException {

        // flg 随机数
        String key = "balance_" + uid + "_" + account;


        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.fairLock(lock);
        Map<String, Object> map = new HashMap<>();
        Integer dzversion = 0;
        if (b) {

            UserBalance userBalance = userBalanceService.get(SearchFilter.build("uid", uid));

            if (userBalance == null) {

                userBalance = new UserBalance();
                userBalance.setIdw(new IdWorker().nextId() + "");
                userBalance.setSourceInvitationCode(sourceInvitationCode);
                userBalance.setUid(uid);
                userBalance.setAccount(account);
                userBalance.setUserBalance(money);
                userBalance.setWalletAddress(GetUsdtAddr.walletAddress(sourceInvitationCode, uid, account));
                userBalance.setDzversion(dzversion + 1);
                userBalanceService.insert(userBalance);

            } else {
                double add;
                if (additionAndSubtraction == 1) {
                    add = BigDecimalUtils.add(userBalance.getUserBalance(), money);
                } else {
                    add = BigDecimalUtils.subtract(userBalance.getUserBalance(), money);
                }
                if (add >= 0) {
                    int byDzversionAndIdForUpdateByUserBalance;
                    // 版本控制
                    if (!flg) {
                        dzversion = userBalance.getDzversion();
                        byDzversionAndIdForUpdateByUserBalance = userBalanceService.findByDzversionAndIdForUpdateByUserBalance(add, dzversion, userBalance.getId());
                    } else { // 支持批量处理
                        userBalance.setUserBalance(add);
                        userBalanceService.update(userBalance);
                        byDzversionAndIdForUpdateByUserBalance = 1;
                        // byDzversionAndIdForUpdateByUserBalance = userBalanceService.findByDzversionAndIdForUpdateByUserBalance2(add, userBalance.getId());
                    }


                    if (byDzversionAndIdForUpdateByUserBalance == 0) {
                        myRedissonLocker.unlock(lock);
                        throw new ApiException(MessageTemplateEnum.SYSTEM_IS_BUSY);  ////////// todo  系统繁忙,请稍后再试
                    }
                } else {
                    myRedissonLocker.unlock(lock);
                    throw new ApiException(MessageTemplateEnum.INSUFFICIENT_BALANCE);    ////////// todo  余额不足
                }
            }

        } else {
            log.info(" ");
            myRedissonLocker.unlock(lock);
            UserBalanceLockLog userBalanceLockLog = new UserBalanceLockLog();
            userBalanceLockLog.setUid(uid);
            userBalanceLockLog.setAccount(account);
            userBalanceLockLog.setMoney(money);
            userBalanceLockLog.setDzstatus(2);
            userBalanceLockLogService.insert(userBalanceLockLog);
            //throw new ApiException(MessageTemplateEnum.SYSTEM_IS_BUSY);  ////////// todo  系统繁忙,请稍后再试
        }
        map.put("v", dzversion + 1);
        myRedissonLocker.unlock(lock);
        return map;
    }

    /**
     * @param type        1 充值 2 VIP购买
     * @param uid         用户ID
     * @param money       金额
     * @param orderNumber 订单号
     * @param channelName 渠道
     * @param channelType 通道类型
     * @return RechargeOrderVo
     * 创建三方充值
     */
    @Transactional(rollbackFor = Exception.class)
    public RechargeOrderVo createRechargeOrderThird(Integer type, String uid, String money, String orderNumber, String channelName, String channelType) throws Exception {


        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", channelName));
        if (paymentChannel == null) {
            log.error("通道信息不存在,[{}]",channelName);
            return null;
        }
        if ("BiPay".equals(channelName)) {
            RechargeParam rechargeParam = new RechargeParam();
            rechargeParam.setCustomId(uid);
            rechargeParam.setAmount(money);
            rechargeParam.setCustomOrderId(orderNumber);
            rechargeParam.setCoinCode(channelType);

            RechargeResp rechargeResp = null;
            if (type == 1) {
                rechargeResp = BiPayUtil.createRechargeOrder(rechargeParam, paymentChannel);
            } else if (type == 2) {
                rechargeResp = BiPayUtil.createRechargeOrderVIP(rechargeParam, paymentChannel);
            }
            if (rechargeResp != null) {
                RechargeOrderVo rechargeOrderVo = new RechargeOrderVo();
                BeanUtils.copyProperties(rechargeResp, rechargeOrderVo);

                return rechargeOrderVo;
            }
            System.out.println("BiPay createRechargeOrderThird error");
        } else if ("WalletPay".equals(channelName)) {
            cn.rh.flash.sdk.paymentChannel.WalletPay.WalletPayBean.RechargeParam rechargeParam = new cn.rh.flash.sdk.paymentChannel.WalletPay.WalletPayBean.RechargeParam();
            rechargeParam.setCurrency_money(money);
            rechargeParam.setUser_order_id(orderNumber);
            rechargeParam.setUser_custom_id(uid);
            rechargeParam.setCoin_code(channelType);
            cn.rh.flash.sdk.paymentChannel.WalletPay.WalletPayBean.RechargeResp rechargeResp = null;
            if (type == 1) {
                rechargeResp = WalletPayUtil.createRechargeOrder(rechargeParam,paymentChannel);

            } else if (type == 2) {
                rechargeResp = WalletPayUtil.createRechargeOrderVIP(rechargeParam,paymentChannel);
            }
            if (rechargeResp != null){
                RechargeOrderVo rechargeOrderVo = new RechargeOrderVo();
                rechargeOrderVo.setAddress(rechargeResp.getCoin_address());
                rechargeOrderVo.setPayUrl(rechargeResp.getPay_address_url());
                rechargeOrderVo.setExpireTime(rechargeResp.getOrder_expire_time());
                rechargeOrderVo.setAmount(rechargeResp.getCurrency_money());
                rechargeOrderVo.setCoinCode(rechargeResp.getCoin_code());
                return rechargeOrderVo;
            }
            System.out.println("WalletPay createRechargeOrderThird error");
        } else if ("KDPay".equals(channelName)) {
            String baseURL= configCache.get(ConfigKeyEnum.NOTIFY_SERVER_NAME).trim();
            KDRechargeParam rechargeParam=new KDRechargeParam();
            rechargeParam.setAmount(money);
            rechargeParam.setOrderCode(orderNumber);
            KDPayResp resp=null;
            if (type==1){
                resp= KDPayUtil.createRechargeOrder(rechargeParam,paymentChannel,baseURL);
            } else if (type==2) {
                resp= KDPayUtil.createRechargeOrderVIP(rechargeParam,paymentChannel,baseURL);
            }
            if (resp!=null){
                RechargeOrderVo rechargeOrderVo = new RechargeOrderVo();
                rechargeOrderVo.setAmount(resp.getData().getPayAmount());
                rechargeOrderVo.setPayUrl(resp.getData().getUrl());
                rechargeOrderVo.setCoinCode("CNY");
//                rechargeOrderVo.setCoinCode(paymentChannel.getCurrency());
                rechargeOrderVo.setValidTime(10*60);
                rechargeOrderVo.setExpireTime(System.currentTimeMillis()+10*60*1000);
                rechargeOrderVo.setAddress(resp.getData().getOrderNo());
                return rechargeOrderVo;
            }
            System.out.println("KDPay createRechargeOrderThird error");
        }
        else if ("QNQBPay".equals(channelName)) {
            String baseURL= configCache.get(ConfigKeyEnum.NOTIFY_SERVER_NAME).trim();
            QNQBPayRechargeParam rechargeParam=new QNQBPayRechargeParam();
            rechargeParam.setAmount(money);
            rechargeParam.setOrderid(orderNumber);
            rechargeParam.setRecvid(paymentChannel.getCurrencyCode());
            QNQBPayResp resp=null;
            if (type==1){
                resp= QNQBPayUtil.createRechargeOrder(rechargeParam,paymentChannel,baseURL);
            } else if (type==2) {
                resp= QNQBPayUtil.createRechargeOrderVIP(rechargeParam,paymentChannel,baseURL);
            }
            if (resp!=null){
                RechargeOrderVo rechargeOrderVo = new RechargeOrderVo();
                rechargeOrderVo.setAmount(resp.getData().getAmount());
                rechargeOrderVo.setPayUrl(resp.getData().getNavurl());
                rechargeOrderVo.setCoinCode("CNY");
//                rechargeOrderVo.setCoinCode(paymentChannel.getCurrency());
                rechargeOrderVo.setValidTime(10*60);
                rechargeOrderVo.setExpireTime(System.currentTimeMillis()+10*60*1000);
                rechargeOrderVo.setAddress(resp.getData().getOrderid());
                return rechargeOrderVo;
            }
            System.out.println("QNQBPay createRechargeOrderThird error");
        }
        else if ("FPay".equals(channelName)) {
            String baseURL= configCache.get(ConfigKeyEnum.NOTIFY_SERVER_NAME).trim();
            FRechargeParam rechargeParam=new FRechargeParam();
            rechargeParam.setAmount(money);
            rechargeParam.setOrderid(orderNumber);
            FPayResp resp=null;
            if (type==1){
                resp= FPayUtil.createRechargeOrder(rechargeParam,paymentChannel,baseURL);
            } else if (type==2) {
                resp= FPayUtil.createRechargeOrderVIP(rechargeParam,paymentChannel,baseURL);
            }
            if (resp!=null){
                RechargeOrderVo rechargeOrderVo = new RechargeOrderVo();
                rechargeOrderVo.setAmount(resp.getData().getAmount());
                rechargeOrderVo.setPayUrl(resp.getData().getPayurl());
                rechargeOrderVo.setCoinCode("CNY");
//                rechargeOrderVo.setCoinCode(paymentChannel.getCurrency());
                rechargeOrderVo.setValidTime(10*60);
                rechargeOrderVo.setExpireTime(System.currentTimeMillis()+10*60*1000);
                rechargeOrderVo.setAddress(resp.getData().getOrderid());
                return rechargeOrderVo;
            }
            System.out.println("FPay createRechargeOrderThird error");
        }
        else if ("OKPay".equals(channelName)) {
            String baseURL= configCache.get(ConfigKeyEnum.NOTIFY_SERVER_NAME).trim();
            OKRechargeParam rechargeParam = new OKRechargeParam();
            rechargeParam.setAmount(money);
            rechargeParam.setOrderid(orderNumber);
            OKPayResp resp=null;
            if (type==1){
                resp= OKPayUtil.createRechargeOrder(rechargeParam,paymentChannel,baseURL);
            } else if (type==2) {
                resp= OKPayUtil.createRechargeOrderVIP(rechargeParam,paymentChannel,baseURL);
            }
            if (resp!=null){
                RechargeOrderVo rechargeOrderVo = new RechargeOrderVo();
                rechargeOrderVo.setAmount(resp.getAmount());
                rechargeOrderVo.setPayUrl(resp.getUrl());
                rechargeOrderVo.setCoinCode("CNY");
//                rechargeOrderVo.setCoinCode(paymentChannel.getCurrency());
                rechargeOrderVo.setValidTime(10*60);
                rechargeOrderVo.setExpireTime(System.currentTimeMillis()+10*60*1000);
                rechargeOrderVo.setAddress(resp.getMerchantOrderNo());
                return rechargeOrderVo;
            }
            System.out.println("KDPay createRechargeOrderThird error");
        }else if ("JDPay".equals(channelName)) {
            String baseURL= configCache.get(ConfigKeyEnum.NOTIFY_SERVER_NAME).trim();
            JDRechargeParam rechargeParam=new JDRechargeParam();
            rechargeParam.setAmount(money);
            rechargeParam.setOrderCode(orderNumber);
            JDPayResp resp=null;
            if (type==1){
                resp= JDPayUtil.createRechargeOrder(rechargeParam,paymentChannel,baseURL);
            } else if (type==2) {
                resp= JDPayUtil.createRechargeOrderVIP(rechargeParam,paymentChannel,baseURL);
            }
            if (resp!=null){
                RechargeOrderVo rechargeOrderVo = new RechargeOrderVo();
                rechargeOrderVo.setAmount(resp.getData().getPayAmount());
                rechargeOrderVo.setPayUrl(resp.getData().getUrl());
                rechargeOrderVo.setCoinCode("CNY");
//                rechargeOrderVo.setCoinCode(paymentChannel.getCurrency());
                rechargeOrderVo.setValidTime(10*60);
                rechargeOrderVo.setExpireTime(System.currentTimeMillis()+10*60*1000);
                rechargeOrderVo.setAddress(resp.getData().getOrderNo());
                return rechargeOrderVo;
            }
            System.out.println("JDPay createRechargeOrderThird error");
        }else if ("CBPay".equals(channelName)) {
            String baseURL= configCache.get(ConfigKeyEnum.NOTIFY_SERVER_NAME).trim();
            CBRechargeParam rechargeParam = new CBRechargeParam();
            rechargeParam.setAmount(money);
            rechargeParam.setOrderCode(orderNumber);
            CBPayResp resp=null;
            if (type==1){
                resp= CBPayUtil.createRechargeOrder(rechargeParam,paymentChannel,baseURL);
            } else if (type==2) {
                resp= CBPayUtil.createRechargeOrderVIP(rechargeParam,paymentChannel,baseURL);
            }
            if (resp!=null){
                RechargeOrderVo rechargeOrderVo = new RechargeOrderVo();
                rechargeOrderVo.setAmount(resp.getData().getPayAmount());
                rechargeOrderVo.setPayUrl(resp.getData().getUrl());
                rechargeOrderVo.setCoinCode("CNY");
//                rechargeOrderVo.setCoinCode(paymentChannel.getCurrency());
                rechargeOrderVo.setValidTime(10*60);
                rechargeOrderVo.setExpireTime(System.currentTimeMillis()+10*60*1000);
                rechargeOrderVo.setAddress(resp.getData().getOrderNo());
                return rechargeOrderVo;
            }
            System.out.println("KDPay createRechargeOrderThird error");
        } else if ("MPay".equals(channelName)) {
            String baseURL= configCache.get(ConfigKeyEnum.NOTIFY_SERVER_NAME).trim();
            MPayData rechargeParam = new MPayData();
            rechargeParam.setAmount((int)Double.parseDouble(money));
            rechargeParam.setMerchOrderId(orderNumber);
            Up resp=null;
            if (type==1){
                resp= MPayUtil.createRechargeOrder(rechargeParam,paymentChannel,baseURL);
            } else if (type==2) {
                resp= MPayUtil.createRechargeOrderVIP(rechargeParam,paymentChannel,baseURL);
            }
            if (resp!=null){
                RechargeOrderVo rechargeOrderVo = new RechargeOrderVo();
                rechargeOrderVo.setAmount(money);
                rechargeOrderVo.setPayUrl(resp.getData().getUrl());
                rechargeOrderVo.setCoinCode("CNY");
//                rechargeOrderVo.setCoinCode(paymentChannel.getCurrency());
                rechargeOrderVo.setValidTime(10*60);
                rechargeOrderVo.setExpireTime(System.currentTimeMillis()+10*60*1000);
                rechargeOrderVo.setAddress(resp.getData().getId());
                return rechargeOrderVo;
            }
            System.out.println("KDPay createRechargeOrderThird error");
        } else if ("808Pay".equals(channelName)) {
            String baseURL= configCache.get(ConfigKeyEnum.NOTIFY_SERVER_NAME).trim();
            Zimu808RechargeParam param=new Zimu808RechargeParam();
            param.setAmount(money);
            UserInfo userinfo = userInfoService.findUserByUserId(Long.valueOf(uid));
//            UserWalletAddress userWalletAddress = userWalletAddressService.get(SearchFilter.build("uid", uid));
            param.setMember(userinfo.getRealName());
            // param.setMember(uid);
            param.setNonce(RandomUtil.randomString(10));
            param.setTimestamp(String.valueOf(System.currentTimeMillis()));
            param.setMchOrderNo(orderNumber);
            ZimuResp resp=null;
            if (type==1){
                resp= Zimu808PayUtil.createRechargeOrder(param,paymentChannel,baseURL);
            } else if (type==2) {
                //....vip
            }
            if (resp!=null){
                RechargeOrderVo rechargeOrderVo = new RechargeOrderVo();
                rechargeOrderVo.setAmount(money);
                rechargeOrderVo.setPayUrl(resp.getData().getPayUrl());
                rechargeOrderVo.setCoinCode("CNY");
//                rechargeOrderVo.setCoinCode(paymentChannel.getCurrency());
                rechargeOrderVo.setValidTime(30*60);
                rechargeOrderVo.setExpireTime(System.currentTimeMillis()+30*60*1000);
                rechargeOrderVo.setAddress(resp.getData().getOrderNo());
                return rechargeOrderVo;
            }
            System.out.println("808Pay createRechargeOrderThird error");
        } else if(StringUtil.isNotEmpty(paymentChannel.getModel()))  {//mypay创建支付
            String baseURL= configCache.get(ConfigKeyEnum.NOTIFY_SERVER_NAME).trim();
            MYRechargeParam rechargeParam=new MYRechargeParam();
            rechargeParam.setAmount(money);
            rechargeParam.setMerchantOrderNo(orderNumber);
            rechargeParam.setModel(paymentChannel.getModel());
            rechargeParam.setBankCode(paymentChannel.getBankCode());
            rechargeParam.setMerchantId(paymentChannel.getCurrencyCode());
            MYPayResp resp=null;
            if (type==1){
                resp= MYPayUtil.createRechargeOrder(rechargeParam,paymentChannel,baseURL);
            } else if (type==2) {
                resp= MYPayUtil.createRechargeOrderVIP(rechargeParam,paymentChannel,baseURL);
            }
            if (resp!=null){
                RechargeOrderVo rechargeOrderVo = new RechargeOrderVo();
                rechargeOrderVo.setAmount(resp.getAmount());
                rechargeOrderVo.setPayUrl(resp.getUrl());
                rechargeOrderVo.setCoinCode("CNY");
//                rechargeOrderVo.setCoinCode(paymentChannel.getCurrency());
                rechargeOrderVo.setValidTime(10*60);
                rechargeOrderVo.setExpireTime(System.currentTimeMillis()+10*60*1000);
                rechargeOrderVo.setAddress(resp.getMerchantOrderNo());
                return rechargeOrderVo;
            }
            System.out.println("MYPay createRechargeOrderThird error");
            return null;
        }else if(1==paymentChannel.getIsPayment())  {//银行卡充值
            RechargeOrderVo rechargeOrderVo = new RechargeOrderVo();
            rechargeOrderVo.setAmount(money);
            rechargeOrderVo.setPayUrl(paymentChannel.getChannelName());
            rechargeOrderVo.setCoinCode("CNY");
//                rechargeOrderVo.setCoinCode(paymentChannel.getCurrency());
            rechargeOrderVo.setValidTime(2*60*60);
            rechargeOrderVo.setExpireTime(System.currentTimeMillis()+2*60*60*1000);
            rechargeOrderVo.setAddress(paymentChannel.getCurrencyCode());
            return rechargeOrderVo;
        }
        System.out.println(paymentChannel.toString());
        return null;
    }

    /**
     * @param uid         用户ID
     * @param money       金额
     * @param orderNumber 订单号
     * @param channelName 渠道
     * @return RechargeOrderVo
     * 创建三方提现
     */
    @Transactional(rollbackFor = Exception.class)
    public WithdrawOrderVo createWithdrawOrderThird(String uid, String money, String address, String orderNumber, String channelName, String channelType) throws Exception {

        PaymentChannel paymentChannel = paymentChannelService.get(SearchFilter.build("channelName", channelName));
        if (paymentChannel == null) {
            log.error("通道 信息不存在,[{}]",channelName);
            return null;
        }

        if ("BiPay".equals(channelName)) {

            WithdrawParam withdrawParam = new WithdrawParam();
            withdrawParam.setInstruction(uid);
            withdrawParam.setAmount(money);
            withdrawParam.setAddress(address);
            withdrawParam.setCustomOrderId(orderNumber);
            WithdrawResp withdrawResp = BiPayUtil.createWithdrawOrder(withdrawParam, paymentChannel);
            if (withdrawResp != null) {
                WithdrawOrderVo withdrawOrderVo = new WithdrawOrderVo();
                withdrawOrderVo.setTransactionNumber(withdrawResp.getId());
                withdrawOrderVo.setFee(withdrawResp.getFee());
                withdrawOrderVo.setAmount(withdrawResp.getAmount());
                withdrawOrderVo.setAddress(withdrawResp.getAddress());
                return withdrawOrderVo;
            }

        }else if ("WalletPay".equals(channelName)){
            cn.rh.flash.sdk.paymentChannel.WalletPay.WalletPayBean.WithdrawParam withdrawParam = new cn.rh.flash.sdk.paymentChannel.WalletPay.WalletPayBean.WithdrawParam();
            withdrawParam.setUser_withdrawal_id(orderNumber);
            withdrawParam.setWithdrawal_address(address);
            withdrawParam.setCurrency_amount(money);
            withdrawParam.setRemark(uid);
            withdrawParam.setCoin_code(channelType);
            cn.rh.flash.sdk.paymentChannel.WalletPay.WalletPayBean.WithdrawResp withdrawResp = WalletPayUtil.createWithdrawOrder(withdrawParam, paymentChannel);

            if (withdrawResp !=null){
                WithdrawOrderVo withdrawOrderVo = new WithdrawOrderVo();
                withdrawOrderVo.setTransactionNumber(withdrawResp.getWithdrawal_id());
                withdrawOrderVo.setFee(withdrawResp.getCommission());
                withdrawOrderVo.setAmount(withdrawResp.getCoin_money());
                withdrawOrderVo.setAddress(withdrawResp.getWithdrawal_address());
                return withdrawOrderVo;
            }

        } else if ("KDPay".equals(channelName)) {
            KDWithdrawParam withdrawParam=new KDWithdrawParam();
            withdrawParam.setOrderCode(orderNumber);//订单号
            withdrawParam.setAmount(money);//金额
            withdrawParam.setAddress(address);//用户地址
            String baseURL= configCache.get(ConfigKeyEnum.NOTIFY_SERVER_NAME).trim();
            KDPayResp kdPayResp = KDPayUtil.createWithdrawOrder(withdrawParam, paymentChannel,baseURL);
            if (kdPayResp!=null){
                WithdrawOrderVo withdrawOrderVo = new WithdrawOrderVo();
                withdrawOrderVo.setTransactionNumber(kdPayResp.getData().getOrderNo());
//                withdrawOrderVo.setFee();
                withdrawOrderVo.setAmount(money);
                withdrawOrderVo.setAddress(address);
                return withdrawOrderVo;
            }
        } else if ("FPay".equals(channelName)) {
            FWithdrawParam withdrawParam=new FWithdrawParam();
            withdrawParam.setOrderid(orderNumber);//订单号
            withdrawParam.setAmount(money);//金额
            withdrawParam.setAddress(address);//用户地址
            String baseURL= configCache.get(ConfigKeyEnum.NOTIFY_SERVER_NAME).trim();
            FPayResp PayResp = FPayUtil.createWithdrawOrder(withdrawParam, paymentChannel,baseURL);
            if (PayResp!=null){
                WithdrawOrderVo withdrawOrderVo = new WithdrawOrderVo();
                withdrawOrderVo.setTransactionNumber(PayResp.getData().getOrderid());
//                withdrawOrderVo.setFee();
                withdrawOrderVo.setAmount(money);
                withdrawOrderVo.setAddress(address);
                return withdrawOrderVo;
            }
        }else if ("JDPay".equals(channelName)) {
            JDWithdrawParam withdrawParam=new JDWithdrawParam();
            withdrawParam.setOrderCode(orderNumber);//订单号
            withdrawParam.setAmount(money);//金额
            withdrawParam.setAddress(address);//用户地址
            String baseURL= configCache.get(ConfigKeyEnum.NOTIFY_SERVER_NAME).trim();
            JDPayResp jdPayResp = JDPayUtil.createWithdrawOrder(withdrawParam, paymentChannel,baseURL);
            if (jdPayResp!=null){
                WithdrawOrderVo withdrawOrderVo = new WithdrawOrderVo();
                withdrawOrderVo.setTransactionNumber(jdPayResp.getData().getOrderNo());
//                withdrawOrderVo.setFee();
                withdrawOrderVo.setAmount(money);
                withdrawOrderVo.setAddress(address);
                return withdrawOrderVo;
            }
        }else if ("CBPay".equals(channelName)) {
            CBWithdrawParam withdrawParam=new CBWithdrawParam();
            withdrawParam.setOrderCode(orderNumber);//订单号
            withdrawParam.setAmount(money);//金额
            withdrawParam.setAddress(address);//用户地址
            String baseURL= configCache.get(ConfigKeyEnum.NOTIFY_SERVER_NAME).trim();
            CBPayResp cbPayResp = CBPayUtil.createWithdrawOrder(withdrawParam, paymentChannel,baseURL);
            if (cbPayResp!=null){
                WithdrawOrderVo withdrawOrderVo = new WithdrawOrderVo();
                withdrawOrderVo.setTransactionNumber(cbPayResp.getData().getOrderNo());
//                withdrawOrderVo.setFee();
                withdrawOrderVo.setAmount(money);
                withdrawOrderVo.setAddress(address);
                return withdrawOrderVo;
            }
        }else if ("808Pay".equals(channelName)) {
            Zimu808WithdrawParam withdrawParam=new Zimu808WithdrawParam();
            withdrawParam.setMchOrderNo(orderNumber);//订单号
            withdrawParam.setAmount(money);//金额
            withdrawParam.setReceiveAccount(address);//用户地址
            // withdrawParam.setMember(uid);//用户ID
            UserWalletAddress userWalletAddress = userWalletAddressService.get(SearchFilter.build("uid", uid));
            withdrawParam.setMember(ObjUtil.isNotEmpty(userWalletAddress) ? userWalletAddress.getWalletName() : null);

            withdrawParam.setNonce(RandomUtil.randomString(10));//随机盐1
            withdrawParam.setTimestamp(String.valueOf(System.currentTimeMillis()));//时间戳
            String baseURL= configCache.get(ConfigKeyEnum.NOTIFY_SERVER_NAME).trim();
            ZimuResp zimuResp = Zimu808PayUtil.createWithdrawOrder(withdrawParam, paymentChannel, baseURL);
            if (zimuResp!=null){
                WithdrawOrderVo withdrawOrderVo = new WithdrawOrderVo();
                withdrawOrderVo.setTransactionNumber(zimuResp.getData().getOrderNo());
//                withdrawOrderVo.setFee();
                withdrawOrderVo.setAmount(money);
                withdrawOrderVo.setAddress(address);
                return withdrawOrderVo;
            }
        } else if (channelName.contains("MYPay")) {//mypay提现通道
            UserWalletAddress userWalletAddress = userWalletAddressService.get(SearchFilter.build("uid", uid));
            if (userWalletAddress == null) {
                log.error("用户钱包 信息不存在,[{}]",userWalletAddress);
                return null;
            }
            MYWithdrawParam withdrawParam=new MYWithdrawParam();
            withdrawParam.setMerchantOrderNo(orderNumber);//订单号
            withdrawParam.setAmount(money);//金额
            withdrawParam.setBankcardAccountNo(address);//银行卡号
            withdrawParam.setBankcardAccountName(userWalletAddress.getWalletName());//银行卡绑定用户名称
            withdrawParam.setModel(paymentChannel.getModel());
            withdrawParam.setBankCode(paymentChannel.getBankCode());
            withdrawParam.setMerchantId(paymentChannel.getCurrencyCode());

            String baseURL= configCache.get(ConfigKeyEnum.NOTIFY_SERVER_NAME).trim();
            MYPayWithdrawResp withdrawOrder = MYPayUtil.createWithdrawOrder(withdrawParam, paymentChannel, baseURL);

            WithdrawOrderVo withdrawOrderVo = new WithdrawOrderVo();
            withdrawOrderVo.setTransactionNumber(withdrawOrder.getMerchantOrderNo());
            withdrawOrderVo.setAmount(money);
            withdrawOrderVo.setAddress(address);
            return withdrawOrderVo;

        }
        else if (channelName.contains("MPay")) {//mypay提现通道
            UserWalletAddress userWalletAddress = userWalletAddressService.get(SearchFilter.build("uid", uid));
            if (userWalletAddress == null) {
                log.error("用户钱包 信息不存在,[{}]",userWalletAddress);
                return null;
            }
            MPayWithdrawParam withdrawParam=new MPayWithdrawParam();
            withdrawParam.setOrderid(orderNumber);//订单号
            withdrawParam.setAmount(money);//金额
            withdrawParam.setAddress(address);//银行卡号

            String baseURL= configCache.get(ConfigKeyEnum.NOTIFY_SERVER_NAME).trim();
            Down withdrawOrder = MPayUtil.createWithdrawOrder(withdrawParam, paymentChannel, baseURL);

            WithdrawOrderVo withdrawOrderVo = new WithdrawOrderVo();
            withdrawOrderVo.setTransactionNumber(withdrawOrder.getData().getId());
            withdrawOrderVo.setAmount(money);
            withdrawOrderVo.setAddress(address);
            return withdrawOrderVo;

        }
        else if (channelName.contains("OKPay")) {//okpay提现通道
            UserWalletAddress userWalletAddress = userWalletAddressService.get(SearchFilter.build("uid", uid));
            if (userWalletAddress == null) {
                log.error("用户钱包 信息不存在,[{}]",userWalletAddress);
                return null;
            }
            OKWithdrawParam withdrawParam=new OKWithdrawParam();
            withdrawParam.setOrderid(orderNumber);//订单号
            withdrawParam.setAmount(money);//金额
            withdrawParam.setAddress(address);//交易地址
            withdrawParam.setSendid(paymentChannel.getCurrencyCode());//商户号

            String baseURL= configCache.get(ConfigKeyEnum.NOTIFY_SERVER_NAME).trim();
            OKWdOrderDto withdrawOrder = OKPayUtil.createWithdrawOrder(withdrawParam, paymentChannel, baseURL);

            WithdrawOrderVo withdrawOrderVo = new WithdrawOrderVo();
            withdrawOrderVo.setTransactionNumber(withdrawOrder.getOrderid());
            withdrawOrderVo.setAmount(money);
            withdrawOrderVo.setAddress(address);
            return withdrawOrderVo;

        }
        else if (channelName.contains("QNQBPay")) {//QNQBPay提现通道
            UserWalletAddress userWalletAddress = userWalletAddressService.get(SearchFilter.build("uid", uid));
            if (userWalletAddress == null) {
                log.error("用户钱包 信息不存在,[{}]",userWalletAddress);
                return null;
            }
            QNQBPayWithdrawParam withdrawParam=new QNQBPayWithdrawParam();
            withdrawParam.setOrderid(orderNumber);//订单号
            withdrawParam.setAmount(money);//金额
            withdrawParam.setAddress(address);//交易地址
            withdrawParam.setSendid(paymentChannel.getCurrencyCode());//商户号

            String baseURL= configCache.get(ConfigKeyEnum.NOTIFY_SERVER_NAME).trim();
            QNQBPayWithdrawResp withdrawOrder = QNQBPayUtil.createWithdrawOrder(withdrawParam, paymentChannel, baseURL);

            WithdrawOrderVo withdrawOrderVo = new WithdrawOrderVo();
            withdrawOrderVo.setTransactionNumber(orderNumber);
            withdrawOrderVo.setAmount(money);
            withdrawOrderVo.setAddress(address);
            return withdrawOrderVo;

        }else if (1==paymentChannel.getIsWithdrawal()){//银行卡提现
            WithdrawOrderVo withdrawOrderVo = new WithdrawOrderVo();
//            withdrawOrderVo.setTransactionNumber();
//            withdrawOrderVo.setFee();
            withdrawOrderVo.setAmount(money);
            withdrawOrderVo.setAddress(address);
            return withdrawOrderVo;
        }

        return null;
    }

    /**
     * @param money                记录流水金额
     * @param uid                  记录流水ID
     * @param sourceInvitationCode 来源码
     * @param account              记录流水账号
     * @param lastTime             记录流水时间
     * @param sourceAccount        记录流水来源用户账号
     */
    public void addFlowingWaterPb(Double money, Long uid, String sourceInvitationCode, String account, Date lastTime, String sourceAccount, String taskIdw, Integer level) {

        FlowingWaterPb flowingWaterPb1 = flowingWaterPbService.getFlowingWaterPb(uid, taskIdw);
        long daySub;
        if (flowingWaterPb1 == null) {
            // 查询当前时间跟任务创建时间
            daySub = DateUtil.getDaySub(DateUtil.getDay(lastTime), DateUtil.getDay());
            // 没有记录的话 时间差如果是0 说明需要添加一条记录
            daySub = daySub == 0 ? 1 : daySub;
        } else {
            // 时间差 为0 说明添加过了 不需要添加
            daySub = DateUtil.getDaySub(DateUtil.getDay(lastTime), DateUtil.getDay());
            if (daySub == 0) return;
        }

        log.info("时间差=" + daySub);

        for (int i = 1; i <= daySub; i++) {
            FlowingWaterPb flowingWaterPb = new FlowingWaterPb();
            flowingWaterPb.setIdw(new IdWorker().nextId() + ""); //
            flowingWaterPb.setSourceInvitationCode(sourceInvitationCode);
            flowingWaterPb.setUid(uid); //
            flowingWaterPb.setAccount(account);
            flowingWaterPb.setMoney(money);
            flowingWaterPb.setSourceUserAccount(sourceAccount); //
            flowingWaterPb.setRelevels(level);
            flowingWaterPb.setDzstatus(1);


            flowingWaterPb.setFlowingWaterDate(DateUtil.getAfterDayDate((i - daySub) + ""));
            flowingWaterPb.setTaskIdw(taskIdw);
            flowingWaterPbService.insert(flowingWaterPb);
        }


    }

    /**
     * 更新用户积分
     *
     * @param score
     * @param uid
     * @param sourceInvitationCode
     * @param account
     * @param type                 1签到 2邀请 3赠送 4兑换 8夺宝积分
     */
    @Transactional
    public void changeUserScore(Double score, Long uid, String sourceInvitationCode, String account, Integer type) {

        UserScoreHistory userScoreHistory = new UserScoreHistory();
        userScoreHistory.setIdw(new IdWorker().nextId() + "");
        userScoreHistory.setSourceInvitationCode(sourceInvitationCode);
        userScoreHistory.setUid(uid);
        userScoreHistory.setAccount(account);
        userScoreHistory.setUserScore(score);
        userScoreHistory.setType(type);
        userScoreHistoryService.insert(userScoreHistory);

        List<SearchFilter> filter = new ArrayList<>();
        filter.add(SearchFilter.build("account", account));
        if (type==8){//夺宝积分
            filter.add(SearchFilter.build("prizeType", type));
        }else {
            filter.add(SearchFilter.build("prizeType", 1));//签到积分
            type=1;
        }
        UserScore userScore = userScoreService.get(filter);
//        UserScore userScore = userScoreService.get(SearchFilter.build("account", account));

        if (userScore == null) {
            userScore = new UserScore();
            userScore.setIdw(new IdWorker().nextId() + "");
            userScore.setSourceInvitationCode(sourceInvitationCode);
            userScore.setUid(uid);
            userScore.setAccount(account);
            userScore.setUserScore(score);
            userScore.setPrizeType(type.toString());
            userScoreService.insert(userScore);
        } else {
            userScore.setUserScore(userScore.getUserScore() + score);
            userScoreService.update(userScore);
        }
    }

}
