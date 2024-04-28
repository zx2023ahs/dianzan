package cn.rh.flash.service.dzuser;


import cn.hutool.core.util.ObjUtil;
import cn.rh.flash.AsynchronousService.AsyncApiUser;
import cn.rh.flash.bean.constant.cache.CacheApiKey;
import cn.rh.flash.bean.dto.FalseDataForm;
import cn.rh.flash.bean.dto.UserInfoDto;
import cn.rh.flash.bean.dto.api.*;
import cn.rh.flash.bean.entity.dzpower.PowerBankTask;
import cn.rh.flash.bean.entity.dzpower.RecordPb;
import cn.rh.flash.bean.entity.dzsys.CountryCode;
import cn.rh.flash.bean.entity.dzsys.SmsNumRecord;
import cn.rh.flash.bean.entity.dzuser.*;
import cn.rh.flash.bean.entity.dzvip.VipPurchaseHistory;
import cn.rh.flash.bean.entity.dzvip.VipRebateRecord;
import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.vo.api.*;
import cn.rh.flash.bean.vo.dz.BatchUserVo;
import cn.rh.flash.bean.vo.dz.UserStatistics;
import cn.rh.flash.bean.vo.dzuser.StraightAddVo;
import cn.rh.flash.bean.vo.dzuser.StraightListVo;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.DynamicSpecifications;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.bean.vo.query.SqlSpecification;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.cache.impl.EhcacheDao;
import cn.rh.flash.dao.dzpower.RecordPbRepository;
import cn.rh.flash.dao.dzsys.CountryCodeRepository;
import cn.rh.flash.dao.dzuser.UserBalanceRepository;
import cn.rh.flash.dao.dzuser.UserInfoRepository;
import cn.rh.flash.dao.dzvip.VipRebateRecordRepository;
import cn.rh.flash.sdk.sms.SMSUtil;
import cn.rh.flash.sdk.sms.bean.SmsResp;
import cn.rh.flash.sdk.telegram.TelegramMachine;
import cn.rh.flash.security.apitoken.ApiToken;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.service.coom.dz.ApiUserCoom;
import cn.rh.flash.service.coom.dz.RecordInformation;
import cn.rh.flash.service.dzpower.PowerBankService;
import cn.rh.flash.service.dzpower.PowerBankTaskService;
import cn.rh.flash.service.dzpower.TotalBonusPbService;
import cn.rh.flash.service.dzsys.SmsMessageService;
import cn.rh.flash.service.dzsys.SmsNumRecordService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.dzvip.VipPurchaseHistoryService;
import cn.rh.flash.service.system.impl.ConstantFactory;
import cn.rh.flash.utils.*;
import cn.rh.flash.utils.Google.GoogleAuthenticator;
import cn.rh.flash.utils.factory.Page;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.sun.istack.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cn.rh.flash.security.JwtUtil.getUcode;
import static cn.rh.flash.security.JwtUtil.getUsername;

@Service
public class UserInfoService extends BaseService<UserInfo, Long, UserInfoRepository> {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private CountryCodeRepository countryCodeRepository;

    @Autowired
    private ApiUserCoom apiUserCoom;

    @Autowired
    private EhcacheDao redisUtil;

    @Resource
    private DefaultKaptcha captchaProducer;

    @Autowired
    private RecordInformation recordInformation;

    @Autowired
    private RechargeRecordService rechargeRecordService;

    @Autowired
    private WithdrawalsRecordService withdrawalsRecordService;

    @Autowired
    private VipPurchaseHistoryService vipPurchaseHistoryService;

    @Autowired
    private ConfigCache configCache;

    @Autowired
    private TransactionRecordService transactionRecordService;

    @Autowired
    private SMSUtil smsUtil;

    @Autowired
    private UserBalanceRepository userBalanceRepository;

    @Autowired
    private UserBalanceService userBalanceService;

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private EhcacheDao ehcacheDao;

    @Autowired
    private SmsMessageService smsMessageService;

    @Autowired
    private PowerBankService powerBankService;

    @Autowired
    private TelegramMachine telegramMachine;

    @Autowired
    private ApiToken apiToken;

    @Autowired
    private FalseDataService falseDataService;

    @Autowired
    private PowerBankTaskService powerBankTaskService;

    @Autowired
    private VipRebateRecordRepository vipRebateRecordRepository;

    @Autowired
    private RecordPbRepository recordPbRepository;

    @Autowired
    private TotalBonusIncomeService totalBonusIncomeService;

    @Autowired
    private TotalBonusPbService totalBonusPbService;

    @Autowired
    private TotalRechargeAmountService totalRechargeAmountService;

    @Autowired
    private TotalWithdrawalAmountService totalWithdrawalAmountService;

    @Autowired
    private FalseTotalService falseTotalService;

    @Autowired
    private SmsNumRecordService smsNumRecordService;
    @Autowired
    private AsyncApiUser asyncApiUser;


    /**
     * 用户注册
     */
    @Transactional(rollbackFor = Exception.class)
    public Ret regV1(RegV1Dto dto, String registerIpCity){
        //平衡车  86 验证手机号格式
//        if (!RegUtil.isCHPhone(dto.getCountryCode(),dto.getAccount())){
//            return Rets.failure(MessageTemplateEnum.WRONG_PHONE_FORMAT.getCode(), MessageTemplateEnum.WRONG_PHONE_FORMAT);
//        }


//        boolean passWordMatches = dto.getPassword().matches("^(?=.*[0-9].*)(?=.*[A-Z].*)(?=.*[a-z].*).{6,12}$");
//        if (!passWordMatches){
//            return Rets.failure("密码格式错误");
//        }


        if (dto.getAccount().startsWith("0")) {
            if (!dto.getAccount().equals(dto.getAccount().replaceFirst("^0+", "0"))) {
                return Rets.failure(MessageTemplateEnum.WRONG_PHONE_FORMAT.getCode(), MessageTemplateEnum.WRONG_PHONE_FORMAT);
            }
            String replacePhone = dto.getAccount().replaceFirst("^0+", "");
            UserInfo userInfo = this.get(SearchFilter.build("account", replacePhone));
            if (userInfo != null) {
                return Rets.failure(MessageTemplateEnum.ACCOUNT_EXISTS.getCode(), MessageTemplateEnum.ACCOUNT_EXISTS);
            }
        } else {
            String replacePhone = "0" + dto.getAccount();
            UserInfo userInfo = this.get(SearchFilter.build("account", replacePhone));
            if (userInfo != null) {
                return Rets.failure(MessageTemplateEnum.ACCOUNT_EXISTS.getCode(), MessageTemplateEnum.ACCOUNT_EXISTS);
            }
        }

        // 验证账号格式
        if (!RegUtil.isValidAccount(dto.getAccount())) {
            return Rets.failure(MessageTemplateEnum.WRONG_PHONE_FORMAT.getCode(), MessageTemplateEnum.WRONG_PHONE_FORMAT);
        }

        //验证密码
        if (StringUtil.isNotEmpty(dto.getPassword())){
            dto.setPassword(CryptUtil.desEncrypt(dto.getPassword()));
        }
        if (!RegUtil.isValidPassword(dto.getPassword())) {
            return Rets.failure(MessageTemplateEnum.WRONG_PASSWORD_FORMAT.getCode(), MessageTemplateEnum.WRONG_PASSWORD_FORMAT);
        }

        //验证码图形验证码
//        Object vCode = redisUtil.hget(CacheApiKey.imgCode, phonekey);
        Object vCode = redisUtil.hget(CacheApiKey.imgCode, dto.getValidateCode().toUpperCase());
        if (vCode == null || !dto.getValidateCode().toUpperCase().equals((vCode + "").toUpperCase())) {
            return Rets.failure(MessageTemplateEnum.WRONG_VALIDATION_IMG_CODE.getCode(), MessageTemplateEnum.WRONG_VALIDATION_IMG_CODE);
        }
        // 查询是否需要验证 手机验证码
        boolean sendPhoneCode = smsMessageService.isSendPhoneCode();
        String phone = dto.getCountryCode() + dto.getAccount();
        if (sendPhoneCode) {
            String code = (String) redisUtil.hget(CacheApiKey.phoneCode, phone);
            if (StringUtils.isEmpty(code) || !dto.getPhoneValidateCode().equals(code)) {
                return Rets.failure(MessageTemplateEnum.WRONG_VALIDATION_CODE.getCode(), MessageTemplateEnum.WRONG_VALIDATION_CODE);
            }
        }


        //查询国家码
        List<CountryCode> byCountryCode = countryCodeRepository.findByCountryCode(dto.getCountryCode());
        if (byCountryCode.size() == 0) {
            return Rets.failure(MessageTemplateEnum.COUNTRY_CODE_NOT_EXIST.getCode(), MessageTemplateEnum.COUNTRY_CODE_NOT_EXIST);
        }

        //查询是否注册过
//        UserInfo userInfo = findByAccountAndCountryCode(dto.getCountryCode(), dto.getAccount());
        UserInfo userInfo = this.get(SearchFilter.build("account", dto.getAccount()));
        if (userInfo != null) {
            return Rets.failure(MessageTemplateEnum.ACCOUNT_EXISTS.getCode(), MessageTemplateEnum.ACCOUNT_EXISTS);
        }

        //查询邀请码的父级用户
        UserInfo parentUser = findByInvitationCode(dto.getInvitationCode());


//        if (parentUser == null || parentUser.getDzstatus() == 3) {
        if (parentUser == null) {
            return Rets.failure(MessageTemplateEnum.INVITATION_CODE_NOT_EXIST.getCode(), MessageTemplateEnum.INVITATION_CODE_NOT_EXIST);
        }
        if (parentUser.getDzstatus() != 1) {
            return Rets.failure(MessageTemplateEnum.INVITATION_CODE_NOT_EXIST.getCode(), MessageTemplateEnum.INVITATION_CODE_NOT_EXIST);
        }
        if (parentUser.getLimitCode() == null || parentUser.getLimitCode() == 1) {
            return Rets.failure(MessageTemplateEnum.INVITATION_CODE_NOT_EXIST.getCode(), MessageTemplateEnum.INVITATION_CODE_NOT_EXIST);
        }

//        // 埃及1 v0和v1的邀请码设置成无法邀请
//        String nickName = configCache.get(ConfigKeyEnum.SITE_NICKNAME).trim();
//        if (StringUtil.isEmpty(nickName)) {
//            nickName = " ";
//        }
//        if ("埃及1,中东".contains(nickName) && "v0v1".contains(parentUser.getVipType())) {
//            return Rets.failure(MessageTemplateEnum.INVITATION_CODE_NOT_EXIST.getCode(), MessageTemplateEnum.INVITATION_CODE_NOT_EXIST);
//        }


        // 邀请码注册限制vip
        String limitVip = configCache.get(ConfigKeyEnum.REG_LIMIT_VIP).trim();
        if (StringUtil.isEmpty(limitVip)) {
            limitVip = " ";
        }
        if (limitVip.contains(parentUser.getVipType())) {
            return Rets.failure(MessageTemplateEnum.INVITATION_CODE_NOT_EXIST.getCode(), MessageTemplateEnum.INVITATION_CODE_NOT_EXIST);
        }


        //注册用户
//        UserInfo aMobileAccount = apiUserCoom.createAMobileAccount(RandomUtil.getRandomName(8), dto.getCountryCode(), dto.getAccount(), dto.getPassword(), parentUser.getInvitationCode(),
//                parentUser.getSourceInvitationCode(), parentUser.getLevels(), parentUser.getPinvitationCode(), registerIpCity, "");
        UserInfo aMobileAccount = apiUserCoom.createAMobileAccount(RandomUtil.getRandomName(8), dto.getCountryCode(), dto.getAccount(), dto.getPassword(), parentUser.getInvitationCode(),
                parentUser.getSourceInvitationCode(), parentUser.getLevels(), parentUser.getPinvitationCode(), registerIpCity, "",dto.getRealName());

        redisUtil.hdel(CacheApiKey.phoneCode, phone);
        //注册日志
        sysLogService.addSysLog(aMobileAccount.getAccount(), aMobileAccount.getId(), aMobileAccount.getAccount(), "APP", SysLogEnum.USER_REG_INFO);
        //远程推送
        asyncApiUser.toUser(aMobileAccount);
        return Rets.success();
    }

    @Transactional(rollbackFor = Exception.class)
    public Ret regV2(RegV2Dto dto, String registerIpCity){
        if (dto.getAccount().startsWith("0")) {
            if (!dto.getAccount().equals(dto.getAccount().replaceFirst("^0+", "0"))) {
                return Rets.failure(MessageTemplateEnum.WRONG_PHONE_FORMAT.getCode(), MessageTemplateEnum.WRONG_PHONE_FORMAT);
            }
            String replacePhone = dto.getAccount().replaceFirst("^0+", "");
            UserInfo userInfo = this.get(SearchFilter.build("account", replacePhone));
            if (userInfo != null) {
                return Rets.failure(MessageTemplateEnum.ACCOUNT_EXISTS.getCode(), MessageTemplateEnum.ACCOUNT_EXISTS);
            }
        } else {
            String replacePhone = "0" + dto.getAccount();
            UserInfo userInfo = this.get(SearchFilter.build("account", replacePhone));
            if (userInfo != null) {
                return Rets.failure(MessageTemplateEnum.ACCOUNT_EXISTS.getCode(), MessageTemplateEnum.ACCOUNT_EXISTS);
            }
        }
        // 验证账号格式
        if (!RegUtil.isValidAccount(dto.getAccount())) {
            return Rets.failure(MessageTemplateEnum.WRONG_PHONE_FORMAT.getCode(), MessageTemplateEnum.WRONG_PHONE_FORMAT);
        }
        //验证密码
        if (StringUtil.isNotEmpty(dto.getPassword())){
            dto.setPassword(CryptUtil.desEncrypt(dto.getPassword()));
        }
        if (!RegUtil.isValidPassword(dto.getPassword())) {
            return Rets.failure(MessageTemplateEnum.WRONG_PASSWORD_FORMAT.getCode(), MessageTemplateEnum.WRONG_PASSWORD_FORMAT);
        }
        //验证码图形验证码
        Object vCode = redisUtil.hget(CacheApiKey.imgCode, dto.getUuid());
        redisUtil.hdel(CacheApiKey.imgCode, dto.getUuid());
        if (vCode == null || !dto.getValidateCode().toUpperCase().equals((vCode + "").toUpperCase())) {
            return Rets.failure(MessageTemplateEnum.WRONG_VALIDATION_IMG_CODE.getCode(), MessageTemplateEnum.WRONG_VALIDATION_IMG_CODE);
        }
        // 查询是否需要验证 手机验证码
        boolean sendPhoneCode = smsMessageService.isSendPhoneCode();
        String phone = dto.getCountryCode() + dto.getAccount();
        if (sendPhoneCode) {
            String code = (String) redisUtil.hget(CacheApiKey.phoneCode, phone);
            if (StringUtils.isEmpty(code) || !dto.getPhoneValidateCode().equals(code)) {
                return Rets.failure(MessageTemplateEnum.WRONG_VALIDATION_CODE.getCode(), MessageTemplateEnum.WRONG_VALIDATION_CODE);
            }
        }
        //查询国家码
        List<CountryCode> byCountryCode = countryCodeRepository.findByCountryCode(dto.getCountryCode());
        if (byCountryCode.size() == 0) {
            return Rets.failure(MessageTemplateEnum.COUNTRY_CODE_NOT_EXIST.getCode(), MessageTemplateEnum.COUNTRY_CODE_NOT_EXIST);
        }
        //查询是否注册过
        UserInfo userInfo = this.get(SearchFilter.build("account", dto.getAccount()));
        if (userInfo != null) {
            return Rets.failure(MessageTemplateEnum.ACCOUNT_EXISTS.getCode(), MessageTemplateEnum.ACCOUNT_EXISTS);
        }
        //查询邀请码的父级用户
        UserInfo parentUser = findByInvitationCode(dto.getInvitationCode());
        if (parentUser == null) {
            return Rets.failure(MessageTemplateEnum.INVITATION_CODE_NOT_EXIST.getCode(), MessageTemplateEnum.INVITATION_CODE_NOT_EXIST);
        }
        if (parentUser.getDzstatus() != 1) {
            return Rets.failure(MessageTemplateEnum.INVITATION_CODE_NOT_EXIST.getCode(), MessageTemplateEnum.INVITATION_CODE_NOT_EXIST);
        }
        if (parentUser.getLimitCode() == null || parentUser.getLimitCode() == 1) {
            return Rets.failure(MessageTemplateEnum.INVITATION_CODE_NOT_EXIST.getCode(), MessageTemplateEnum.INVITATION_CODE_NOT_EXIST);
        }
        // 邀请码注册限制vip
        String limitVip = configCache.get(ConfigKeyEnum.REG_LIMIT_VIP).trim();
        if (StringUtil.isEmpty(limitVip)) {
            limitVip = " ";
        }
        if (limitVip.contains(parentUser.getVipType())) {
            return Rets.failure(MessageTemplateEnum.INVITATION_CODE_NOT_EXIST.getCode(), MessageTemplateEnum.INVITATION_CODE_NOT_EXIST);
        }
        //注册用户
        UserInfo aMobileAccount = apiUserCoom.createAMobileAccount(RandomUtil.getRandomName(8), dto.getCountryCode(), dto.getAccount(), dto.getPassword(), parentUser.getInvitationCode(),
                parentUser.getSourceInvitationCode(), parentUser.getLevels(), parentUser.getPinvitationCode(), registerIpCity, "",dto.getRealName());

        redisUtil.hdel(CacheApiKey.phoneCode, phone);
        //注册日志
        sysLogService.addSysLog(aMobileAccount.getAccount(), aMobileAccount.getId(), aMobileAccount.getAccount(), "APP", SysLogEnum.USER_REG_INFO);
        //远程推送
        asyncApiUser.toUser(aMobileAccount);
        return Rets.success();
    }


    // 注册成功后相关业务
    public void relatedBusiness(UserInfo aMobileAccount) {
        // 1. 注册返佣
//        registeredReturn(aMobileAccount);
        // 2.

        // 注册赠送彩金
        String money = configCache.get(ConfigKeyEnum.REG_GIFT_MONEY).trim();
        if (StringUtil.isNotEmpty(money)) {
            recordInformation.straightToTheMouth(new BigDecimal(money).doubleValue(), 1, "signupBonus", "reg_gift", aMobileAccount);
        }
    }

    // 注册返佣
//    public void registeredReturn(UserInfo aMobileAccount) {
//        String sql = UserInfoServiceSql.findUpUpUpCdb(aMobileAccount.getSuperiorInvitationCode());
//        Map upUpUp = userInfoRepository.getMapBySql(sql);
//        if (upUpUp.size() > 0) {
//            if (upUpUp.get("l1id") != null && upUpUp.get("l1fee") != null) {
//                recordInformation.transactionRecordPlus(aMobileAccount.getSourceInvitationCode(), Long.valueOf(upUpUp.get("l1id").toString()), upUpUp.get("l1account").toString(),
//                        0.00, Double.valueOf(upUpUp.get("l1fee").toString()), Double.valueOf(upUpUp.get("l1fee").toString()),
//                        aMobileAccount.getIdw(), 11, "reg","");
//            }
//            if (upUpUp.get("l2id") != null && upUpUp.get("l2fee") != null) {
//                recordInformation.transactionRecordPlus(aMobileAccount.getSourceInvitationCode(), Long.valueOf(upUpUp.get("l2id").toString()), upUpUp.get("l2account").toString(),
//                        0.00, Double.valueOf(upUpUp.get("l2fee").toString()), Double.valueOf(upUpUp.get("l2fee").toString()),
//                        aMobileAccount.getIdw(), 11, "reg","");
//            }
//            if (upUpUp.get("l3id") != null && upUpUp.get("l3fee") != null) {
//                recordInformation.transactionRecordPlus(aMobileAccount.getSourceInvitationCode(), Long.valueOf(upUpUp.get("l3id").toString()), upUpUp.get("l3account").toString(),
//                        0.00, Double.valueOf(upUpUp.get("l3fee").toString()), Double.valueOf(upUpUp.get("l3fee").toString()),
//                        aMobileAccount.getIdw(), 11, "reg","");
//            }
//        }
//    }

    /**
     * 用户生成验证码
     */
    public Ret getValidateCode() {
        //JSONObject equation = YZMUtil.createCode();
        //String question = equation.getString("question");
        //生成验证码并存到redis
//        String phonekey = "+" + dto.getCountryCode() + dto.getAccount();
//        redisUtil.hset(CacheApiKey.imgCode, phonekey, question,300);
//        redisUtil.hset(CacheApiKey.imgCode, question.toUpperCase(), question, 300);
//        String yzmBase64 = YZMUtil.getYZMCode(captchaProducer, question);


        Map<String, String> yzmCode = YZMUtil.getYZMCode();
        redisUtil.hset(CacheApiKey.imgCode, yzmCode.get(YZMUtil.KEY_CODE).toUpperCase(), yzmCode.get(YZMUtil.KEY_CODE), 300);
        return Rets.success(yzmCode.get(YZMUtil.KEY_IMG));
    }
    public Ret getValidateCodeV2() {
        Map<String, String> yzmCode = YZMUtil.getYZMCode();
        String randomString = cn.hutool.core.util.RandomUtil.randomString(12);
        redisUtil.hset(CacheApiKey.imgCode, randomString, yzmCode.get(YZMUtil.KEY_CODE), 300);
        Map map=new HashMap();
        map.put("uuid",randomString);
        map.put("img",yzmCode.get(YZMUtil.KEY_IMG));
        return Rets.success(map);
    }

    /**
     * 用户统计数据
     *
     * @return
     */
    public UserStatistics getUserStatistics(String ucode) {
        String sql = UserInfoServiceSql.sqlMap(configCache.get(ConfigKeyEnum.TEST_USER_CODE).trim());
        if (StringUtil.isNotEmpty(ucode)) {
            sql = sql + " and source_invitation_code =  '" + ucode + "' ";
        }

        Map mapBySql = userInfoRepository.getMapBySql(sql);
        return BeanUtil.mapToBean(mapBySql, new UserStatistics());
    }


    /**
     * 获取用户个人信息
     */
    public Ret<UserInfoVo> getUserInfo(Long userId) {
//        UserInfo one = userInfoRepository.getOne(userId);
//        UserInfo one = this.findUserByUserId(userId);
        String sql="select u.id,u.account,u.head_portrait_key as headPortraitKey,u.vip_type as vipType,u.levels," +
                "u.invitation_code as invitationCode,u.name,u.vip_expire_date as vipExpireDate,v.vip_img as vipImg " +
                "from t_dzuser_user as u left join t_dzvip_vipmessage as v on u.vip_type=v.vip_type where u.id="+userId;
        List<?> objects = userInfoRepository.queryObjBySql(sql, UserInfo.class);
        UserInfo one =  (UserInfo) objects.get(0);

        UserInfoVo userInfoVo = new UserInfoVo();
        if (one != null) {
            userInfoVo.setId(BigInteger.valueOf(one.getId()));
            userInfoVo.setAccount(StringUtil.addXing(false, one.getAccount()));

            userInfoVo.setHeadPortraitKey(one.getHeadPortraitKey());
            userInfoVo.setVipType(one.getVipType());
            userInfoVo.setVipImg(one.getVipImg());
            userInfoVo.setInvitationCode(one.getInvitationCode());
            userInfoVo.setName(one.getName());

            if (one.getVipExpireDate() != null) {
                Date now = DateUtil.parseTime(DateUtil.getTime());
                long s = now.getTime() - one.getVipExpireDate().getTime();
                TimeUnit time = TimeUnit.DAYS;
                long days = time.convert(s, TimeUnit.MILLISECONDS);
                if (days <= 0) {
                    days = 0;
                }
                userInfoVo.setDueDay(BigInteger.valueOf(days));
            }

            // 查询当前账号是否有 造假统计

            FalseTotal falseTotal = falseTotalService.get(SearchFilter.build("account", one.getAccount()));

            if (falseTotal != null) {
                userInfoVo.setBalance(new BigDecimal(falseTotal.getBalance()));
                userInfoVo.setProfitOfTheDay(new BigDecimal(falseTotal.getProfitOfTheDay()));
                userInfoVo.setTotalWithdrawalAmount(new BigDecimal(falseTotal.getTotalWithdrawalAmount()));
                userInfoVo.setTotalRevenue(new BigDecimal(falseTotal.getTotalRevenue()));
                userInfoVo.setTeamSize(new BigInteger(falseTotal.getTeamSize()));
                userInfoVo.setTeamReport(new BigDecimal(falseTotal.getTeamReport()));
                return Rets.success(userInfoVo);
            }

            // 统计数据
            String vipFriendSql = UserInfoServiceSql.sqlBalanceInformation(one);
//            System.out.println( vipFriendSql );
            Map mapBySql = userInfoRepository.getMapBySql(vipFriendSql);
            UserInfoVoBean uib = BeanUtil.mapToBean(mapBySql, new UserInfoVoBean());

            //收入明细=加扣款+日常收益明细+下级返佣明细（晋级，收益）
            //totalBonusIncome totalBonuspb dwTotalBonuspb   dwPayVip    rechargeAmount  - withdrawalAmount payVip
            //  直冲总金额       充电宝返佣金额  下级充电宝返佣金额 购买vip返佣金额  总充值金额           总提现          购买vip
//            System.out.println(
//                    uib.getAllReg().doubleValue()+"--"+uib.getTotalBonusIncome().doubleValue()+"--"+uib.getTotalBonuspb().doubleValue()
//                            +"--"+ uib.getDwTotalBonuspb().doubleValue()+"--"+uib.getDwPayVip().doubleValue()+"--"+uib.getRechargeAmount().doubleValue()
//            );
            // 22-12-24 收入明细：收益+手动加扣款+返佣-消费金额（晋级）
//            userInfoVo.setBalance(
//                    BigDecimal.valueOf(
//                            BigDecimalUtils.subtract(
//                                    BigDecimalUtils.add(uib.getAllReg().doubleValue(), uib.getTotalBonusIncome().doubleValue(),
//                                            uib.getTotalBonuspb().doubleValue(), uib.getDwTotalBonuspb().doubleValue(), uib.getDwPayVip().doubleValue(), uib.getRechargeAmount().doubleValue()),
//                                    BigDecimalUtils.add(uib.getWithdrawalAmount().doubleValue(), uib.getPayVip().doubleValue())
//                            )
//                    )
//            );
            // 22-12-29收益明细计算改：充电宝日常返佣 + 手动上分 -手动下分 + 下级返佣（下级充电宝返佣金额,下级购买vip返佣金额）- 购买充电宝消费金额
            // 23-01-02收入明细：充电宝日常返佣 + 手动上分 +手动下分 + 下级返佣
            userInfoVo.setBalance(
                    BigDecimal.valueOf(
//                            BigDecimalUtils.subtract(
                            BigDecimalUtils.add(uib.getTotalBonuspb().doubleValue(), // 充电宝返佣金额(自己包括自己下级)
                                    uib.getTotalBonusIncome().doubleValue(), // 直冲直扣
//                                    uib.getDwTotalBonuspb().doubleValue(), // 下级充电宝返佣金额
                                    uib.getDwPayVip().doubleValue()) // 下级购买vip返佣金额
//                                    uib.getPayVip().doubleValue() // -购买充电宝消费金额
//                            )
                    )
            );


            // 22-12-24 当日收益：当天收益+当天手动加扣款+当天返佣
            userInfoVo.setProfitOfTheDay(
                    BigDecimal.valueOf(
                            BigDecimalUtils.add(uib.getVipToDay().doubleValue(), uib.getBrToDay().doubleValue(), uib.getCdbToDay().doubleValue())
                    )
            );


            //可提现总金额=总收入+充值-消费金额-提款
//            userInfoVo.setTotalWithdrawalAmount(
//                BigDecimal.valueOf(
//                    one.getUserBalance() !=null
//                        ? BigDecimalUtils.subtract( one.getUserBalance().getUserBalance(), BigDecimalUtils.add( uib.getWithdrawalAmount().doubleValue(), uib.getPayVip().doubleValue() ) )
//                        : 0
//                )
//            );
            // 用户余额
            userInfoVo.setTotalWithdrawalAmount(uib.getUserBalance());

            // 总收益 =  团队报告+加扣款+总日常收益 + 注册
            // 总收益 = 充电宝返佣金额 + 下3级充电宝返佣金额 + 下3级购买vip返佣金额
            // 总收益 =  充值、购买vip消费 、提款，收益明细 (收益明细+充值-购买Vip-提款)

//            userInfoVo.setTotalRevenue(
//                    BigDecimal.valueOf(
//                            BigDecimalUtils.add(uib.getTotalBonuspb().doubleValue(),
//                                    uib.getDwTotalBonuspb().doubleValue(),
//                                    uib.getDwPayVip().doubleValue())
//                    )
//            );
            // 总收益 =  充值、购买vip消费 、提款，收益明细 (收益明细+充值-购买Vip-提款)
//            userInfoVo.setTotalRevenue(
//                    BigDecimal.valueOf(
//                            BigDecimalUtils.subtract(BigDecimalUtils.add(
//                                    userInfoVo.getBalance().doubleValue(),uib.getRechargeAmount().doubleValue()
//                            ),uib.getPayVip().doubleValue(),uib.getWithdrawalAmount().doubleValue())
//                    )
//            );
            // 总收益 = 收益明细
            userInfoVo.setTotalRevenue(userInfoVo.getBalance());

            //团队规模 =vip人数    （往下3级）
            userInfoVo.setTeamSize(uib.getTeams());

            // 团队报告=下级晋级返佣+下级收益返佣  dwTotalBonuspb   dwPayVip
            // 团队报告=下3级晋级返佣+下3级收益返佣
            userInfoVo.setTeamReport(
                    BigDecimal.valueOf(
                            BigDecimalUtils.add(uib.getDwPayVip().doubleValue(), uib.getDwTotalBonuspb().doubleValue())
                    )
            );
        }

        return Rets.success(userInfoVo);
    }




    @Transactional
    public Ret straightBuckle(UserInfoVo.StraightBuckleVo straightBuckle, String operator) {
        UserInfo one = userInfoRepository.getOne(straightBuckle.getUid());

        if (straightBuckle.getMoney() > 50000) {
            return Rets.failure("大哥，你又把金额输成账号了？");
        }

        recordInformation.straightToTheMouth(straightBuckle.getMoney(), straightBuckle.getType(), straightBuckle.getRemark(), operator, one);
        sysLogService.addSysLog(operator, one.getId(), one.getAccount()
                , "PC", straightBuckle.getType() == 1 ? SysLogEnum.STRAIGHT_BUCKLE_UP : SysLogEnum.STRAIGHT_BUCKLE_LO);
        return Rets.success();
    }

    @Transactional(rollbackFor = Exception.class)
    public Ret updateWalletAddr(Long id, String value) {
        UserInfo one = userInfoRepository.getOne(id);
        if (one == null) {
            return Rets.failure(MessageTemplateEnum.ACCOUNT_NOT_EXISTS.getCode());
        }
        sysLogService.addSysLog(getUsername(), id, one.getAccount()
                , "PC", SysLogEnum.USER_INFO_UPDATE_INFO);
        apiUserCoom.initWalletAddress(one, value);
        return Rets.success();
    }

    /**
     * 批量直冲
     * @param vo
     * @param operator
     * @return
     */
    public Ret straights(StraightAddVo vo, String operator){

        for (StraightListVo straightListVo : vo.getStraightListVos()) {
            //验证金额
            if (ObjUtil.isEmpty(straightListVo.getMoney())){
                return Rets.failure("金额不能为0!");
            }
            if (straightListVo.getMoney() <=0){
                return Rets.failure("金额不能为0!");
            }
            if (straightListVo.getMoney() > 30000) {
                return Rets.failure("大哥，你又把金额输成账号了？");
            }
            //验证账号
            UserInfo byAccount = this.findByAccount(straightListVo.getAccounts());
            if (ObjUtil.isEmpty(byAccount)){
                return Rets.failure(straightListVo.getAccounts()+"账号不存在!");
            }
        }
        for (StraightListVo straightListVo : vo.getStraightListVos()) {
            UserInfo one = this.findByAccount(straightListVo.getAccounts());
            recordInformation.straightToTheMouth(straightListVo.getMoney(), vo.getType(), vo.getRemark(), operator, one);
            sysLogService.addSysLog(operator, one.getId(), one.getAccount()
                    , "PC", vo.getType() == 1 ? SysLogEnum.STRAIGHT_BUCKLE_UP : SysLogEnum.STRAIGHT_BUCKLE_LO);
        }
        return Rets.success();
    }

    /**
     * 后台初始化钱包地址
     *
     * @param
     * @user    zx
     */
    public Ret InitWalletAddr(Long id, String value) {
        UserInfo one = userInfoRepository.getOne(id);
        if (one == null) {
            return Rets.failure(MessageTemplateEnum.ACCOUNT_NOT_EXISTS.getCode());
        }

        //参数为空则重置地址
        if (value==null||value.equals("")){
            sysLogService.addSysLog(getUsername(), id, one.getAccount()
                    , "PC", SysLogEnum.UPDATE_WALLET_ADDR_INFO);
            apiUserCoom.userInitWalletAddress(one, value,"");
            return Rets.success();
        }

        boolean tronAddress = false;
        char c = value.charAt(0);
        String channelType;
        if (c=='T'){
            channelType="USDT.TRC20";
            tronAddress = CoinAddressUtil.isTronAddress(value);
        }else if (c=='0'){
            channelType="USDT.Polygon";
            tronAddress=CoinAddressUtil.isTronAddressByPolygon(value);
        }else {
            return Rets.failure(MessageTemplateEnum.INVALID_ADDRESS.getCode(), MessageTemplateEnum.INVALID_ADDRESS);
        }

        if (!tronAddress) {
            return Rets.failure(MessageTemplateEnum.INVALID_ADDRESS.getCode(), MessageTemplateEnum.INVALID_ADDRESS);
        }

        sysLogService.addSysLog(getUsername(), id, one.getAccount()
                , "PC", SysLogEnum.UPDATE_WALLET_ADDR_INFO);
        apiUserCoom.userInitWalletAddress(one, value,channelType);
        return Rets.success();
    }

    public Ret<InvitationInfoVo> getInvitationInfo(Long userId) {

        UserInfo one = userInfoRepository.getOne(userId);

        InvitationInfoVo invitationInfoVo = new InvitationInfoVo();
        if (one != null) {
            invitationInfoVo.setInvitationCode(one.getInvitationCode());

            invitationInfoVo.setTeamVipActivationTotalRevenue(
                    one.getTeamVIPOpeningTotalRebate()
            );
            String vipFriendSql = UserInfoServiceSql.sqlVipFriends(one.getInvitationCode(), one.getLevels());
            Map mapBySql = userInfoRepository.getMapBySql(vipFriendSql);
            invitationInfoVo.setVipFriends(((BigInteger) mapBySql.get("vipFriends")).intValue());

            invitationInfoVo.setInvitationLink(configCache.get(ConfigKeyEnum.DZ_IV_LIKE).trim() + invitationInfoVo.getInvitationCode());
        }
        return Rets.success(invitationInfoVo);
    }


    public Ret<List<RechargeRecordVo>> getRechargeList(RechargeOrWithdrawRecordsDTO rechargeOrWithdrawRecordsDTO, Long userId) {
        UserInfo one = userInfoRepository.getOne(userId);
        if (one != null) {
            Page<RechargeRecord> page = new Page<>(rechargeOrWithdrawRecordsDTO.getPageNo(), rechargeOrWithdrawRecordsDTO.getPageSize(), "createTime", false);
            page.addFilter(SearchFilter.build("uid", one.getId()));
            List<RechargeRecordVo> rechargeRecords = rechargeRecordService.queryPage(page).getRecords().stream().map(v -> {
                RechargeRecordVo rechargeRecordVo = new RechargeRecordVo();
                BeanUtils.copyProperties(v, rechargeRecordVo);
                rechargeRecordVo.setOrderNumber(v.getOrderNumber().replace("czz", "cz"));
                return rechargeRecordVo;
            }).collect(Collectors.toList());
            return Rets.success(rechargeRecords);
        }
        return Rets.success();
    }

    public Ret<List<WithdrawalsRecordVo>> getWithdrawList(RechargeOrWithdrawRecordsDTO rechargeOrWithdrawRecordsDTO, Long userId) {
        UserInfo one = userInfoRepository.getOne(userId);
        if (one != null) {
            Page<WithdrawalsRecord> page = new Page<>(rechargeOrWithdrawRecordsDTO.getPageNo(), rechargeOrWithdrawRecordsDTO.getPageSize(), "createTime", false);
            page.addFilter(SearchFilter.build("uid", one.getId()));
            List<WithdrawalsRecordVo> withdrawalsRecords = withdrawalsRecordService.queryPage(page).getRecords().stream().map(v -> {
                WithdrawalsRecordVo withdrawalsRecordVo = new WithdrawalsRecordVo();
                BeanUtils.copyProperties(v, withdrawalsRecordVo);
                withdrawalsRecordVo.setOrderNumber(v.getOrderNumber().replace("txz", "tx"));
                return withdrawalsRecordVo;
            }).collect(Collectors.toList());
            return Rets.success(withdrawalsRecords);
        }
        return Rets.success();
    }

    public Ret<List<VipPurchaseVo>> getVipPurchaseList(RechargeOrWithdrawRecordsDTO rechargeOrWithdrawRecordsDTO, Long userId) {
        UserInfo one = userInfoRepository.getOne(userId);
        if (one != null) {
            Page<VipPurchaseHistory> page = new Page<>(rechargeOrWithdrawRecordsDTO.getPageNo(), rechargeOrWithdrawRecordsDTO.getPageSize(), "createTime", false);
            page.addFilter(SearchFilter.build("uid", one.getId()));
            page.addFilter(SearchFilter.build("whetherToPay", "2"));
            List<VipPurchaseVo> vipPurchaseVos = vipPurchaseHistoryService.queryPage(page).getRecords().stream().map(v -> {
                VipPurchaseVo vipPurchaseVo = new VipPurchaseVo();
                BeanUtils.copyProperties(v, vipPurchaseVo);

                // 支付状态 1:未支付,2:已支付
                vipPurchaseVo.setFlg(v.getWhetherToPay());
                return vipPurchaseVo;
            }).collect(Collectors.toList());
            return Rets.success(vipPurchaseVos);
        }
        return Rets.success();
    }

    @Transactional(rollbackFor = Exception.class)
    public Ret changePassword(PasswordDto dto, Long userId, Integer type) {
        UserInfo one = userInfoRepository.getOne(userId);
        if (one == null) {
            return Rets.failure(MessageTemplateEnum.ACCOUNT_NOT_EXISTS.getCode(), MessageTemplateEnum.ACCOUNT_NOT_EXISTS);
        }
        //解密
        if (StringUtil.isNotEmpty(dto.getNewPassword())){
            dto.setNewPassword(CryptUtil.desEncrypt(dto.getNewPassword()));
        }
        if (StringUtil.isNotEmpty(dto.getOldPassword())){
            dto.setOldPassword(CryptUtil.desEncrypt(dto.getOldPassword()));
        }

        String password;
        SysLogEnum sysLogEnum = null;

        switch (type) {
            // 修改密码
            case 1:
                password = MD5.md5(dto.getOldPassword(), "");
                if (!password.equals(one.getPassword())) {
                    return Rets.failure(MessageTemplateEnum.WRONG_OLD_PASSWORD.getCode(), MessageTemplateEnum.WRONG_OLD_PASSWORD);
                }
                if (!StringUtil.isNotEmpty(dto.getNewPassword())) {
                    return Rets.failure(MessageTemplateEnum.WRONG_PASSWORD_FORMAT.getCode(), MessageTemplateEnum.WRONG_PASSWORD_FORMAT);
                }
                //验证密码
                if (!RegUtil.isValidPassword(dto.getNewPassword())) {
                    return Rets.failure(MessageTemplateEnum.WRONG_PASSWORD_FORMAT.getCode(), MessageTemplateEnum.WRONG_PASSWORD_FORMAT);
                }
                password = MD5.md5(dto.getNewPassword(), "");
                one.setPassword(password);
                sysLogEnum = SysLogEnum.USER_CHANGE_PASSWORD_INFO;
                break;
            // 修改支付密码
            case 2:
                password = MD5.md5(dto.getOldPassword(), "");
                if (!password.equals(one.getPaymentPassword())) {
                    return Rets.failure(MessageTemplateEnum.WRONG_OLD_PASSWORD.getCode(), MessageTemplateEnum.WRONG_OLD_PASSWORD);
                }
                if (!StringUtil.isNotEmpty(dto.getNewPassword())) {
                    return Rets.failure(MessageTemplateEnum.WRONG_PASSWORD_FORMAT.getCode(), MessageTemplateEnum.WRONG_PASSWORD_FORMAT);
                }
                //验证密码
                if (!RegUtil.isValidPayPassword(dto.getNewPassword())) {
                    return Rets.failure(MessageTemplateEnum.WRONG_PASSWORD_FORMAT.getCode(), MessageTemplateEnum.WRONG_PASSWORD_FORMAT);
                }
                password = MD5.md5(dto.getNewPassword(), "");
                one.setPaymentPassword(password);
                sysLogEnum = SysLogEnum.USER_CHANGE_PAY_PASSWORD_INFO;
                break;
            // 设置支付密码
            case 3:

                if (StringUtil.isNotEmpty(one.getPaymentPassword())) {
                    return Rets.failure(MessageTemplateEnum.WRONG_OLD_PASSWORD.getCode(), MessageTemplateEnum.WRONG_OLD_PASSWORD);
                }
                if (!StringUtil.isNotEmpty(dto.getNewPassword())) {
                    return Rets.failure(MessageTemplateEnum.WRONG_PASSWORD_FORMAT.getCode(), MessageTemplateEnum.WRONG_PASSWORD_FORMAT);
                }
                //验证密码
                if (!RegUtil.isValidPayPassword(dto.getNewPassword())) {
                    return Rets.failure(MessageTemplateEnum.WRONG_PASSWORD_FORMAT.getCode(), MessageTemplateEnum.WRONG_PASSWORD_FORMAT);
                }
                password = MD5.md5(dto.getNewPassword(), "");
                one.setPaymentPassword(password);
                sysLogEnum = SysLogEnum.USER_SET_UP_PAY_PASSWORD_INFO;
                break;
            // 重置交易密码
//            case 4 :
//                if (!StringUtil.isNotEmpty(dto.getNewPassword())){
//                    return Rets.failure( MessageTemplateEnum.WRONG_PASSWORD_FORMAT.getCode() , MessageTemplateEnum.WRONG_PASSWORD_FORMAT );
//                }
//                password = MD5.md5(dto.getNewPassword(), "");
//                one.setPaymentPassword(password);
//                break;
        }
        userInfoRepository.save(one);
        sysLogService.addSysLog(one.getAccount(), one.getId(), one.getAccount(), "APP", sysLogEnum);
        return Rets.success();
    }
    @Transactional(rollbackFor = Exception.class)
    public Ret changeHeadLogo(HeadLogoDto headLogoDto, Long userId) {
        UserInfo one = userInfoRepository.getOne(userId);
        if (one != null) {
            one.setHeadPortraitKey(headLogoDto.getImg());
            userInfoRepository.save(one);
        }
        sysLogService.addSysLog(one.getAccount(), one.getId(), one.getAccount(), "APP", SysLogEnum.USER_SET_AVATAR_INFO);
        return Rets.success();
    }


    @Transactional(rollbackFor = Exception.class)
    public Ret setWalletAddress(SetWalletAddressDto dto, Long userId) {
        // 2022年12月22日
//        boolean tronAddress = CoinAddressUtil.isTronAddress(dto.getAddress());
        //2023.11.14 增加Polygon
        //校验后台多人共用地址是否开放
        String trim = configCache.get(ConfigKeyEnum.MULTIPLE_PEOPLE_USING_ADDRESS).trim();
        if (trim!=null&&trim.equals("0")){
            UserBalance userBalanceDB = userBalanceService.get(SearchFilter.build("walletAddress", dto.getAddress()));

            if (userBalanceDB != null) {
                return Rets.failure(MessageTemplateEnum.EXISTS_ADDRESS.getCode(), MessageTemplateEnum.EXISTS_ADDRESS);
            }
        }

        boolean tronAddress = false;
        if ("USDT.TRC20".equals(dto.getChannelType())){
            tronAddress = CoinAddressUtil.isTronAddress(dto.getAddress());
        }else {
            tronAddress=CoinAddressUtil.isTronAddressByPolygon(dto.getAddress());
        }

        if (!tronAddress) {
            return Rets.failure(MessageTemplateEnum.INVALID_ADDRESS.getCode(), MessageTemplateEnum.INVALID_ADDRESS);
        }

        // end

        UserInfo one = getOneBySql(userId);
        if (one != null) {
            if (StringUtils.isEmpty(apiUserCoom.getWalletAddress(userId))) {
                // todo jk
                if (dto.getAddress().equals(StringUtils.deleteWhitespace(dto.getAddress()))) {
                    apiUserCoom.setWalletAddress(one.getId(), dto.getAddress(),dto.getChannelType());
                }
            }
        }
        sysLogService.addSysLog(one.getAccount(), one.getId(), one.getAccount(), "APP", SysLogEnum.SET_WALLET_ADDR_INFO);
        return Rets.success();
    }
    @Transactional(rollbackFor = Exception.class)
    public Ret changeUname(UnameDto unameDto, Long userId) {
        UserInfo one = userInfoRepository.getOne(userId);
        if (one != null) {
            one.setName(unameDto.getName());
            userInfoRepository.save(one);
        }
        sysLogService.addSysLog(one.getAccount(), one.getId(), one.getAccount(), "APP", SysLogEnum.USER_CHANGE_NAME_INFO);
        return Rets.success();
    }

    public Ret getTeamReport(Long userId) {
        UserInfo parent = getOneBySql(userId);
        if (parent != null) {
            TeamReportVo vo = getLevelCommission(parent);
//            vo.setL1Records(getLevelIdInfo(parent,1,4));
//            vo.setL2Records(getLevelIdInfo(parent,2,4));
//            vo.setL3Records(getLevelIdInfo(parent,3,4));
            return Rets.success(vo);
        } else {
            return Rets.failure(MessageTemplateEnum.ACCOUNT_NOT_EXISTS.getCode(), MessageTemplateEnum.ACCOUNT_NOT_EXISTS);
        }

    }

    // 团队报告结果
    private TeamReportVo getLevelCommission(UserInfo parent) {
        TeamReportVo vo = new TeamReportVo();
        for (int i = 1; i < 4; i++) {
            List accounts = getLevelIdInfo(parent, i, 3);
            if (!CollectionUtils.isEmpty(accounts)) {

                String sql = UserInfoServiceSql.sqlTeamProfit(parent.getId(), accounts);
                Map teamProfit = userInfoRepository.getMapBySql(sql);
                Double money = BigDecimalUtils.add(BigDecimalUtils.add(((BigDecimal) teamProfit.get("tmoney")).doubleValue(), ((BigDecimal) teamProfit.get("vmoney")).doubleValue()));
                switch (i) {
                    case 1:
                        vo.setL1Commission(money);
                        break;
                    case 2:
                        vo.setL2Commission(money);
                        break;
                    case 3:
                        vo.setL3Commission(money);
                        break;
                }
            }
        }
        return vo;
    }

    /**
     * @param parent 父级用户
     * @param level  层级
     * @param type   1 查ID 2 查邀请码 3 查账号 4 查所有
     * @return
     */
    @Nullable
    public List getLevelIdInfo(UserInfo parent, Integer level, Integer type) {
        switch (level) {
            case 1:
                // 一级用户集合
                String sqlOne = UserInfoServiceSql.sqlUserInfoSubVo(parent.getInvitationCode(), parent.getLevels() + 1);
                List<UserInfoSubVo> l1Id = (List<UserInfoSubVo>) userInfoRepository.queryObjBySql(sqlOne, UserInfoSubVo.class);
                if (!CollectionUtils.isEmpty(l1Id)) {
                    switch (type) {
                        case 1:
                            return l1Id.stream().map(v -> v.getId()).collect(Collectors.toList());
                        case 2:
                            return l1Id.stream().map(v -> v.getInvitationCode()).collect(Collectors.toList());
                        case 3:
                            return l1Id.stream().map(v -> v.getAccount()).collect(Collectors.toList());
                        case 4:
                            for (UserInfoSubVo vo : l1Id) {
                                String sql = UserInfoServiceSql.sqlTeamProfit2(parent.getId(), vo.getAccount());
                                Map mapBySql = userInfoRepository.getMapBySql(sql);
                                vo.setCommission(BigDecimalUtils.add(((BigDecimal) mapBySql.get("tmoney")).doubleValue(), ((BigDecimal) mapBySql.get("vmoney")).doubleValue()));
                            }
                            return l1Id;
                    }
                }
                break;
            case 2:
                //2级用户集合
                String sqlTwo = UserInfoServiceSql.sqlUserInfoSubVo(parent.getInvitationCode(), parent.getLevels() + 2);
                List<UserInfoSubVo> l2Id = (List<UserInfoSubVo>) userInfoRepository.queryObjBySql(sqlTwo, UserInfoSubVo.class);
                if (!CollectionUtils.isEmpty(l2Id)) {
                    switch (type) {
                        case 1:
                            return l2Id.stream().map(v -> v.getId()).collect(Collectors.toList());
                        case 2:
                            return l2Id.stream().map(v -> v.getInvitationCode()).collect(Collectors.toList());
                        case 3:
                            return l2Id.stream().map(v -> v.getAccount()).collect(Collectors.toList());
                        case 4:
                            for (UserInfoSubVo vo : l2Id) {
                                String sql = UserInfoServiceSql.sqlTeamProfit2(parent.getId(), vo.getAccount());
                                Map mapBySql = userInfoRepository.getMapBySql(sql);
                                vo.setCommission(BigDecimalUtils.add(((BigDecimal) mapBySql.get("tmoney")).doubleValue(), ((BigDecimal) mapBySql.get("vmoney")).doubleValue()));
                            }
                            return l2Id;
                    }
                }
                break;
            case 3:
                String sqlIdThree = UserInfoServiceSql.sqlUserInfoSubVo(parent.getInvitationCode(), parent.getLevels() + 3);
                List<UserInfoSubVo> l3Id = (List<UserInfoSubVo>) userInfoRepository.queryObjBySql(sqlIdThree, UserInfoSubVo.class);
                if (!CollectionUtils.isEmpty(l3Id)) {
                    switch (type) {
                        case 1:
                            return l3Id.stream().map(v -> v.getId()).collect(Collectors.toList());
                        case 2:
                            return l3Id.stream().map(v -> v.getInvitationCode()).collect(Collectors.toList());
                        case 3:
                            return l3Id.stream().map(v -> v.getAccount()).collect(Collectors.toList());
                        case 4:
                            for (UserInfoSubVo vo : l3Id) {
                                String sql = UserInfoServiceSql.sqlTeamProfit2(parent.getId(), vo.getAccount());
                                Map mapBySql = userInfoRepository.getMapBySql(sql);
                                vo.setCommission(BigDecimalUtils.add(((BigDecimal) mapBySql.get("tmoney")).doubleValue(), ((BigDecimal) mapBySql.get("vmoney")).doubleValue()));
                            }
                            return l3Id;
                    }
                }
                break;
        }
        return null;
    }


    public List<GetTeamOneVo> getTeamOne(String invitationCode, Integer levels, PageDto pageObj) {
        // 团队用户信息  账号  头像 注册是时间    当日新增人数  团队规模  vip等级
        String sqlGetTeamOneVo = UserInfoServiceSql.sqlGetTeamOneVo(invitationCode, levels);
        System.out.println(sqlGetTeamOneVo);
        sqlGetTeamOneVo = SqlSpecification.toSqlLimit(pageObj.getPageNo(), pageObj.getPageSize(), sqlGetTeamOneVo, "id");
        List<GetTeamOneVo> l1Id = (List<GetTeamOneVo>) userInfoRepository.queryObjBySql(sqlGetTeamOneVo, GetTeamOneVo.class);
        if (l1Id.size() == 1) {
            if (l1Id.get(0).getRegistrationTime() == null) {
                l1Id.remove(0);
            }
        }
        return l1Id;
    }

    /**
     * 团队查询新接口
     * @param invitationCode
     * @param levels
     * @param pageObj
     * @return
     */
    public List<GetTeamOneVo> newGetTeamOne(String invitationCode, Integer levels, PageDto pageObj) {
        // 团队用户信息  账号  头像 注册是时间    当日新增人数  团队规模  vip等级
        String sqlGetTeamOneVo = UserInfoServiceSql.sqlGetTeamVo(invitationCode, levels,pageObj.getAccountFragment());
        System.out.println(sqlGetTeamOneVo);
        sqlGetTeamOneVo = SqlSpecification.toSqlLimit(pageObj.getPageNo(), pageObj.getPageSize(), sqlGetTeamOneVo, "id");
        List<GetTeamOneVo> l1Id = (List<GetTeamOneVo>) userInfoRepository.queryObjBySql(sqlGetTeamOneVo, GetTeamOneVo.class);
        if (l1Id.size() == 1) {
            if (l1Id.get(0).getRegistrationTime() == null) {
                l1Id.remove(0);
            }
        }
        return l1Id;
    }

    public List<GetTeamTwoVo> getTeamTwo(TeamTwoDto teamTwoDto) {

        // 直推 用户信息  【 头像，账号，vip类型,注册是时间 】
        String sqlGetTeamTwoVo = UserInfoServiceSql.sqlGetTeamTwoVo(teamTwoDto.getInvitationCode());
//        String sqlGetTeamTwoVo = UserInfoServiceSql.sqlGetTeamThreeVo(teamTwoDto.getInvitationCode(),teamTwoDto.getAccountFragment());

        sqlGetTeamTwoVo = SqlSpecification.toSqlLimit(teamTwoDto.getPageNo(), teamTwoDto.getPageSize(), sqlGetTeamTwoVo, "id");

        List<GetTeamTwoVo> l1Id = (List<GetTeamTwoVo>) userInfoRepository.queryObjBySql(sqlGetTeamTwoVo, GetTeamTwoVo.class);
        return l1Id;
    }

    /**
     * 查询三级团队信息方法
     * @param teamTwoDto
     * @return
     */
    public List<GetTeamTwoVo> getTeamThree(TeamTwoDto teamTwoDto) {

        // 直推 用户信息  【 头像，账号，vip类型,注册是时间 】
//        String sqlGetTeamTwoVo = UserInfoServiceSql.sqlGetTeamTwoVo(teamTwoDto.getInvitationCode());
        String sqlGetTeamTwoVo = UserInfoServiceSql.sqlGetTeamThreeVo(teamTwoDto.getInvitationCode(),teamTwoDto.getAccountFragment());

        sqlGetTeamTwoVo = SqlSpecification.toSqlLimit(teamTwoDto.getPageNo(), teamTwoDto.getPageSize(), sqlGetTeamTwoVo, "id");

        List<GetTeamTwoVo> l1Id = (List<GetTeamTwoVo>) userInfoRepository.queryObjBySql(sqlGetTeamTwoVo, GetTeamTwoVo.class);
        return l1Id;
    }

    public Ret<List<InComeVo>> getIncomeList(PageDto pageDto, Long userId) {
        UserInfo one = getOneBySql(userId);
        if (one != null) {
            Page<TransactionRecord> page = new Page<>(pageDto.getPageNo(), pageDto.getPageSize(), "createTime", false);
            page.addFilter(SearchFilter.build("uid", one.getId()));
            page.addFilter("transactionType", SearchFilter.Operator.NOTIN, Arrays.asList("1", "2", "10"));
//            page.addFilter("additionAndSubtraction",1);
            // transactionType   交易编号   1:充值,2:提现,3:平台赠送,4:平台扣款,5:充电宝返佣,8:vip开通返佣,9:团队任务收益,10:购买vip,11:注册
//            ArrayList<String> transactionTypes = new ArrayList<>();
//            transactionTypes.add("1");
//            transactionTypes.add("3");
//            transactionTypes.add("5");
//            transactionTypes.add("7");
//            transactionTypes.add("8");
//            transactionTypes.add("9");
//            transactionTypes.add("11");
//            page.addFilter(SearchFilter.build("transactionType", SearchFilter.Operator.IN,transactionTypes));
            List<InComeVo> inComeVos = transactionRecordService.queryPage(page).getRecords().stream().map(v -> {
                InComeVo inComeVo = new InComeVo();
                BeanUtils.copyProperties(v, inComeVo);
                inComeVo.setMoney(BigDecimal.valueOf(v.getMoney()));
                inComeVo.setOrderNumber(v.getOrderNumber().replace("cdbz", "cdb"));
                if ("2".equals(v.getTransactionType()) && v.getAdditionAndSubtraction() == 1) {
                    inComeVo.setTransactionType("101");
                }
                return inComeVo;
            }).collect(Collectors.toList());
            return Rets.success(inComeVos);
        }
        return Rets.success();
    }


    /**
     * 获取用户信息
     *
     * @param uid
     * @return
     */
    public UserInfo getOneBySql(Long uid) {
        String sql = UserInfoServiceSql.sqlUserInfo(uid);
        Map mapBySql = userInfoRepository.getMapBySql(sql);
        if (mapBySql.size() == 0) {
            return null;
        }
        mapBySql.put("id", Long.valueOf(mapBySql.get("id").toString()));
        return BeanUtil.mapToBean(mapBySql, new UserInfo());
    }

    /**
     * 获取用户信息
     *
     * @param superiorInvitationCode
     * @return
     */
    public UserInfo getOneBySql(String superiorInvitationCode) {
        String sql = UserInfoServiceSql.sqlUserInfoByInvitationCode(superiorInvitationCode);
        Map mapBySql = userInfoRepository.getMapBySql(sql);
        if (mapBySql.size() == 0) {
            return null;
        }
        mapBySql.put("id", Long.valueOf(mapBySql.get("id").toString()));
        return BeanUtil.mapToBean(mapBySql, new UserInfo());
    }

    public UserInfo findByInvitationCode(String invitationCode) {
        String sql = UserInfoServiceSql.sqlUserInfoByInvitationCode(invitationCode);
        Map mapBySql = userInfoRepository.getMapBySql(sql);
        if (mapBySql.size() == 0) {
            return null;
        }
        mapBySql.put("id", Long.valueOf(mapBySql.get("id").toString()));
        return BeanUtil.mapToBean(mapBySql, new UserInfo());
    }

    public UserInfo findByAccountAndCountryCode(String getCountryCode, String getAccount) {
        String sql = UserInfoServiceSql.sqlUserInfoByCodeAndAccount(getCountryCode, getAccount);
        Map mapBySql = userInfoRepository.getMapBySql(sql);
        if (mapBySql.size() == 0) {
            return null;
        }
        mapBySql.put("id", Long.valueOf(mapBySql.get("id").toString()));
        return BeanUtil.mapToBean(mapBySql, new UserInfo());
    }
    public UserInfo findByAccount(String account) {
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("account",account));
        List<UserInfo> userInfos = this.queryAll(filters);
        if (userInfos.size() == 1) {
            return userInfos.get(0);
        }
        return null;
    }

    /**
     * 获取自己的  上级 上上级   上上上级  信息【l1.id,l1.account,l1.fee,  l2.id,l2.account,l2.fee  ,l3.id,l3.account,l3.fee】
     */
    public Map findUpUpUpCdb(UserInfo userInfo) {
//        UserInfo oneBySql = getOneBySql(uid);
        if (userInfo != null) {
            String sql = UserInfoServiceSql.findUpUpUpCdb(userInfo.getSuperiorInvitationCode());
            Map mapBySql = userInfoRepository.getMapBySql(sql);

            if (mapBySql.size() == 0) {
                return null;
            }
            return mapBySql;
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public Ret forgetPassword(ForgetPasswordDto dto) throws Exception {
        Optional<UserInfo> oneBySql = userInfoRepository.findOne(DynamicSpecifications.bySearchFilter(SearchFilter.build("account", dto.getAccount()), UserInfo.class));
        if (oneBySql.isPresent()) {

            UserInfo userInfo = oneBySql.get();
            //验证密码
            if (StringUtil.isNotEmpty(dto.getPassword())){
                dto.setPassword(CryptUtil.desEncrypt(dto.getPassword()));
            }
            if (!RegUtil.isValidPassword(dto.getPassword())) {
                return Rets.failure(MessageTemplateEnum.WRONG_PASSWORD_FORMAT.getCode(), MessageTemplateEnum.WRONG_PASSWORD_FORMAT);
            }

            //验证码短信验证码判断
            String phone = dto.getCountryCode() + dto.getAccount();
            String code = (String) redisUtil.hget(CacheApiKey.phoneCode, phone);

            if (StringUtils.isEmpty(code) || !dto.getValidateCode().equals(code)) {
                return Rets.failure(MessageTemplateEnum.WRONG_VALIDATION_CODE.getCode(), MessageTemplateEnum.WRONG_VALIDATION_CODE);
            }

            userInfo.setPassword(MD5.md5(dto.getPassword(), ""));
            userInfoRepository.save(userInfo);
            redisUtil.hdel(CacheApiKey.phoneCode, phone);
            sysLogService.addSysLog(userInfo.getAccount(), userInfo.getId(), userInfo.getAccount(), "APP", SysLogEnum.USER_RESET_NAME_INFO);

            return Rets.success();
        } else {
            return Rets.failure(MessageTemplateEnum.ACCOUNT_NOT_EXISTS.getCode(), MessageTemplateEnum.ACCOUNT_NOT_EXISTS);
        }
    }

    public Ret sendPhoneCode(SendPhoneCodeDto dto) {

        // 发送短信之前 验证图形验证码
        Object vCode = redisUtil.hget(CacheApiKey.imgCode, dto.getCode().toUpperCase());
        if (vCode == null || !dto.getCode().toUpperCase().equals((vCode + "").toUpperCase())) {
            return Rets.failure(MessageTemplateEnum.WRONG_VALIDATION_IMG_CODE.getCode(), MessageTemplateEnum.WRONG_VALIDATION_IMG_CODE);
        }

        // skj 2-20 注册时 已注册的 不发验证码  忘记密码时  dzstatus = 1 为启用时 发送验证码
        if (StringUtil.isEmpty(dto.getType())) {
            return Rets.failure(MessageTemplateEnum.PARAM_NOT_EXIST.getCode(), MessageTemplateEnum.PARAM_NOT_EXIST);
        }
        // 获取当天短信次数
        String trim = configCache.get(ConfigKeyEnum.SMS_DAY_NUM).trim();
        Integer smsNum = StringUtil.isNotEmpty(trim) ? Integer.valueOf(trim) : 10;

        // 查询当天发送短信次数
        List<SearchFilter> fil = new ArrayList<>();
        fil.add(SearchFilter.build("countryCodeNumber", dto.getCountryCode()));
        fil.add(SearchFilter.build("phone", dto.getAccount()));
        fil.add(SearchFilter.build("day", DateUtil.getDay()));
        SmsNumRecord smsNumRecord = smsNumRecordService.get(fil);

        if (smsNumRecord != null && smsNumRecord.getCount() >= smsNum) {
            return Rets.failure(MessageTemplateEnum.SMS_DAY_NUM_LIMIT.getCode(), MessageTemplateEnum.SMS_DAY_NUM_LIMIT);
        }

        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("countryCodeNumber", dto.getCountryCode()));
        filters.add(SearchFilter.build("account", dto.getAccount()));
        UserInfo userInfo = this.get(filters);

        switch (dto.getType()) {
            case "register": // 注册
                if (userInfo != null) {
                    return Rets.failure(MessageTemplateEnum.ACCOUNT_EXISTS.getCode(), MessageTemplateEnum.ACCOUNT_EXISTS);
                }
                break;
            case "resetpw": // 忘记密码时
                if (userInfo == null) {
                    return Rets.failure(MessageTemplateEnum.ACCOUNT_NOT_EXISTS.getCode(), MessageTemplateEnum.ACCOUNT_NOT_EXISTS);
                }
                if (userInfo.getDzstatus() != 1) {
                    return Rets.failure(MessageTemplateEnum.THE_ACCOUNT_IS_DISABLED.getCode(), MessageTemplateEnum.THE_ACCOUNT_IS_DISABLED);
                }
                break;
        }


        String phone = dto.getCountryCode() + dto.getAccount();
        String code = RandomUtil.getRandomYZMNumber(6);
        redisUtil.hset(CacheApiKey.phoneCode, phone, code, 300);
//        logger.error(phone + "----" + code);


        SmsResp smsResp = smsUtil.sendCode(phone, code);
        //验证码发送失败
        if (smsResp == null || !"0".equals(smsResp.getCode())) {
            return Rets.failure(MessageTemplateEnum.WRONG_PHONE_FORMAT.getCode(), MessageTemplateEnum.WRONG_PHONE_FORMAT);
        } else {
            if ("1".equals(smsResp.getMessageid())) {
                return Rets.failure(MessageTemplateEnum.SYSTEM_IS_BUSY.getCode(), MessageTemplateEnum.SYSTEM_IS_BUSY);
            }
            if ("00".equals(smsResp.getCode())) {
                return Rets.failure(MessageTemplateEnum.SMS_SEVER_STOP.getCode(), MessageTemplateEnum.SMS_SEVER_STOP);
            }
        }

        // 发送成功之后 记录

        if (smsNumRecord == null) {
            smsNumRecord = new SmsNumRecord();
            smsNumRecord.setCountryCodeNumber(dto.getCountryCode());
            smsNumRecord.setPhone(dto.getAccount());
            smsNumRecord.setDay(DateUtil.getDay());
            smsNumRecord.setCount(1);
        } else {
            smsNumRecord.setCount(smsNumRecord.getCount() + 1);
        }

        smsNumRecordService.update(smsNumRecord);

//        telegramMachine.sendMsg(phone + "----" + code + "----" + TimeZone.getDefault());
        return Rets.success();
    }
    public Ret sendPhoneCodeV2(SendPhoneCodeDto dto) {
        // 发送短信之前 验证图形验证码
        Object vCode = redisUtil.hget(CacheApiKey.imgCode, dto.getUuid());
        if (vCode == null || !dto.getCode().toUpperCase().equals((vCode + "").toUpperCase())) {
            return Rets.failure(MessageTemplateEnum.WRONG_VALIDATION_IMG_CODE.getCode(), MessageTemplateEnum.WRONG_VALIDATION_IMG_CODE);
        }
        // skj 2-20 注册时 已注册的 不发验证码  忘记密码时  dzstatus = 1 为启用时 发送验证码
        if (StringUtil.isEmpty(dto.getType())) {
            return Rets.failure(MessageTemplateEnum.PARAM_NOT_EXIST.getCode(), MessageTemplateEnum.PARAM_NOT_EXIST);
        }
        // 获取当天短信次数
        String trim = configCache.get(ConfigKeyEnum.SMS_DAY_NUM).trim();
        Integer smsNum = StringUtil.isNotEmpty(trim) ? Integer.valueOf(trim) : 10;
        // 查询当天发送短信次数
        List<SearchFilter> fil = new ArrayList<>();
        fil.add(SearchFilter.build("countryCodeNumber", dto.getCountryCode()));
        fil.add(SearchFilter.build("phone", dto.getAccount()));
        fil.add(SearchFilter.build("day", DateUtil.getDay()));
        SmsNumRecord smsNumRecord = smsNumRecordService.get(fil);
        if (smsNumRecord != null && smsNumRecord.getCount() >= smsNum) {
            return Rets.failure(MessageTemplateEnum.SMS_DAY_NUM_LIMIT.getCode(), MessageTemplateEnum.SMS_DAY_NUM_LIMIT);
        }
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("countryCodeNumber", dto.getCountryCode()));
        filters.add(SearchFilter.build("account", dto.getAccount()));
        UserInfo userInfo = this.get(filters);
        switch (dto.getType()) {
            case "register": // 注册
                if (userInfo != null) {
                    return Rets.failure(MessageTemplateEnum.ACCOUNT_EXISTS.getCode(), MessageTemplateEnum.ACCOUNT_EXISTS);
                }
                break;
            case "resetpw": // 忘记密码时
                if (userInfo == null) {
                    return Rets.failure(MessageTemplateEnum.ACCOUNT_NOT_EXISTS.getCode(), MessageTemplateEnum.ACCOUNT_NOT_EXISTS);
                }
                if (userInfo.getDzstatus() != 1) {
                    return Rets.failure(MessageTemplateEnum.THE_ACCOUNT_IS_DISABLED.getCode(), MessageTemplateEnum.THE_ACCOUNT_IS_DISABLED);
                }
                break;
        }
        String phone = dto.getCountryCode() + dto.getAccount();
        String code = RandomUtil.getRandomYZMNumber(6);
        redisUtil.hset(CacheApiKey.phoneCode, phone, code, 300);
        SmsResp smsResp = smsUtil.sendCode(phone, code);
        //验证码发送失败
        if (smsResp == null || !"0".equals(smsResp.getCode())) {
            return Rets.failure(MessageTemplateEnum.WRONG_PHONE_FORMAT.getCode(), MessageTemplateEnum.WRONG_PHONE_FORMAT);
        } else {
            if ("1".equals(smsResp.getMessageid())) {
                return Rets.failure(MessageTemplateEnum.SYSTEM_IS_BUSY.getCode(), MessageTemplateEnum.SYSTEM_IS_BUSY);
            }
            if ("00".equals(smsResp.getCode())) {
                return Rets.failure(MessageTemplateEnum.SMS_SEVER_STOP.getCode(), MessageTemplateEnum.SMS_SEVER_STOP);
            }
        }
        // 发送成功之后 记录
        if (smsNumRecord == null) {
            smsNumRecord = new SmsNumRecord();
            smsNumRecord.setCountryCodeNumber(dto.getCountryCode());
            smsNumRecord.setPhone(dto.getAccount());
            smsNumRecord.setDay(DateUtil.getDay());
            smsNumRecord.setCount(1);
        } else {
            smsNumRecord.setCount(smsNumRecord.getCount() + 1);
        }
        smsNumRecordService.update(smsNumRecord);
//        telegramMachine.sendMsg(phone + "----" + code + "----" + TimeZone.getDefault());
        return Rets.success();
    }

    @Transactional
    public Ret updateInvitation(Long id, String value) {
        UserInfo one = userInfoRepository.getOne(id);
        if (one == null) {
            return Rets.failure(MessageTemplateEnum.ACCOUNT_NOT_EXISTS.getCode());
        }

        if (one.getDzstatus() == 1) {
            return Rets.failure("修改邀请码之前先冻结用户再修改");
        }

        // 原邀请码
        String invitationCode = one.getInvitationCode();
        one.setInvitationCode(RandomUtil.getrandomInvitationCode());

        /*String sql = UserInfoServiceSql.updateInvitation( invitationCode, one.getInvitationCode() );
        userInfoRepository.execute( sql );*/
        // 处理下级关系递归
        apiUserCoom.updateInvitation(invitationCode, one.getInvitationCode());
        userInfoRepository.save(one);
        String remark = getUsername() + "修改邀请码,---修改前邀请码:" + invitationCode + ",---修改后邀请码:" + one.getSuperiorInvitationCode();

        sysLogService.addSysLog(getUsername(), one.getId(), "PC", SysLogEnum.UPDATE_USER_INFO, remark);
        return Rets.success();
    }

    public UserInfo findUserByUserId(Long userId) {
        String sql = UserInfoServiceSql.findUserByUserId(userId);
        List<?> objects = userInfoRepository.queryObjBySql(sql, UserInfo.class);
        if (objects.size() == 0) {
            return new UserInfo();
        }
        return (UserInfo) objects.get(0);
    };

    public UserInfo findUserByUserIdBill(Long userId) {
        String sql = UserInfoServiceSql.findUserByUserIdBill(userId);
        List<?> objects = userInfoRepository.queryObjBySql(sql, UserInfo.class);
        if (objects.size() == 0) {
            return new UserInfo();
        }
        return (UserInfo) objects.get(0);
    };



    // PC段列表查询
    public List<UserInfo> findUserInfoPage(Page<UserInfo> page, String sourceInvitationCode) {
        String sql = UserInfoServiceSql.findUserInfoPage(page, sourceInvitationCode);
        List<?> userInfoList = userInfoRepository.queryObjBySql(sql, UserInfo.class);
        return (List<UserInfo>) userInfoList;
    }

    public List<UserInfo> findUserInfoPage(Page<UserInfo> page, String sourceInvitationCode, String orderSql) {
        String sql = UserInfoServiceSql.findUserInfoPage(page, sourceInvitationCode, orderSql);
        List<?> userInfoList = userInfoRepository.queryObjBySql(sql, UserInfo.class);
        return (List<UserInfo>) userInfoList;
    }

    public List<UserInfo> findUserInfoExportPage(Page<UserInfo> page, String sourceInvitationCode, String orderSql) {
        String sql = UserInfoServiceSql.findUserInfoExportPage(page, sourceInvitationCode, orderSql);
        List<?> userInfoList = userInfoRepository.queryObjBySql(sql, UserInfo.class);
        return (List<UserInfo>) userInfoList;
    }

    public Map findUserInfoByAccount(Long id) {
        String userInfoByAccount = UserInfoServiceSql.findUserInfoByAccount(id);
//        System.out.println(userInfoByAccount);
//        List<?> userInfoList = userInfoRepository.queryObjBySql(userInfoByAccount, UserInfo.class);
        Map mapBySql = userInfoRepository.getMapBySql(userInfoByAccount);
        return (Map) mapBySql;
    }

    /*
    查询用户部分信息，降低数据库压力，提高响应效率
     */
    public List<UserInfo> findUserPartInfoPage(Page<UserInfo> page, String sourceInvitationCode, String orderSql) {
        String sql = UserInfoServiceSql.findPartUserInfoPage(page, sourceInvitationCode, orderSql);
        List<?> userInfoList = userInfoRepository.queryObjBySql(sql, UserInfo.class);
        return (List<UserInfo>) userInfoList;
    }

    public List<UserInfo> findUserPartInfoPage(Page<UserInfo> page,Map<String,Object> where,boolean subordinate,boolean money) {
        String sql = UserInfoServiceSql.findPartUserInfoPage(page,where,subordinate,money);
        List<?> userInfoList = userInfoRepository.queryObjBySql(sql, UserInfo.class);
        return (List<UserInfo>) userInfoList;
    }

    // PC段列表查询总条数
    public Integer findCount(Page<UserInfo> page, String sourceInvitationCode) {
        String sql = UserInfoServiceSql.newFindCount(page, sourceInvitationCode);
        Map mapBySql = userInfoRepository.getMapBySql(sql);
        return ((BigInteger) mapBySql.get("count")).intValue();
    }

    // PC段列表查询总条数
    public Integer findCount(Page<UserInfo> page) {
        String sql = UserInfoServiceSql.newFindCount(page);
        Map mapBySql = userInfoRepository.getMapBySql(sql);
        return ((BigInteger) mapBySql.get("count")).intValue();
    }

    /**
     * @Description: 批量冻结解冻
     * @Param:
     * @return:
     * @Author: Skj
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchUser(BatchUserVo batchUserVo) {

        List<UserInfo> userInfos = batchUserVo.getUserInfos();
        //  Set<Long> collect = userInfos.stream().map(UserInfo::getId).collect(Collectors.toSet());
        for (UserInfo userInfo : userInfos) {
            userInfo.setDzstatus(batchUserVo.getFlg());
            userInfo.setRemark(batchUserVo.getValue());
            userInfoRepository.save(userInfo);
            // 强制下线前端登录状态
            ehcacheDao.hdel(CacheApiKey.LOGIN_CONSTANT, userInfo.getCountryCodeNumber() + "_" + userInfo.getAccount());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchLimitDrawing(BatchUserVo batchUserVo) {
        List<UserInfo> userInfos = batchUserVo.getUserInfos();
        for (UserInfo userInfo : userInfos) {
            userInfo.setLimitDrawing(batchUserVo.getFlg());
            userInfo.setRemark(batchUserVo.getValue());
            userInfoRepository.save(userInfo);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void batchLimitProfit(BatchUserVo batchUserVo) {
        List<UserInfo> userInfos = batchUserVo.getUserInfos();
        for (UserInfo userInfo : userInfos) {
            userInfo.setLimitProfit(batchUserVo.getFlg());
            userInfo.setRemark(batchUserVo.getValue());
            userInfoRepository.save(userInfo);
        }
    }

    @Transactional
    public Ret userInfoUpdate(UserInfo userInfo) {
        UserInfo userInfo1 = this.get(SearchFilter.build("invitationCode", userInfo.getSuperiorInvitationCode()));
        if (userInfo1 == null) {
            return Rets.failure("邀请码无效");
        }

        UserInfo userInfo2 = this.get(SearchFilter.build("id", userInfo.getId()));
        if (userInfo2 == null) {
            return Rets.expire("用户不存在");
        }
        if (userInfo.getPassword().length() < 32) {

            //验证密码
            if (!RegUtil.isValidPassword(userInfo.getPassword())) {
                return Rets.failure("密码格式错误");
            }
            userInfo2.setPassword(MD5.md5(userInfo.getPassword(), ""));
        }

        if (StringUtil.isEmpty(userInfo.getPaymentPassword())) {
            userInfo.setPaymentPassword("123456");
        }

        if (userInfo.getPaymentPassword().length() < 32) {
            //验证交易密码
            if (!RegUtil.isValidPayPassword(userInfo.getPaymentPassword())) {
                return Rets.failure("交易密码格式错误");
            }
            userInfo2.setPaymentPassword(MD5.md5(userInfo.getPaymentPassword(), ""));
        }
        userInfo2.setDzstatus(userInfo.getDzstatus());
//			userInfo2.setSuperiorInvitationCode(  userInfo.getSuperiorInvitationCode()  );


        // 当前传进来邀请码跟之前邀请码不一致  执行修改上级邀请码逻辑
        // 弃用
//            if (!userInfo2.getSuperiorInvitationCode().equals(userInfo.getSuperiorInvitationCode())) {
//                boolean code = updateSuperCode(userInfo1, userInfo2);
//                if (!code) {
//                    return Rets.failure("当前用户为顶级账号无法修改上级邀请码");
//                }
//                String remark = getUsername()+"修改上级邀请码,---修改前邀请码:"+userInfo2.getSuperiorInvitationCode()+",---修改后邀请码:"+userInfo.getSuperiorInvitationCode();
//                sysLogService.addSysLog(getUsername(), userInfo.getId(), "PC", SysLogEnum.UPDATE_USER_INFO,remark);
//
//            }
        String format = DateUtil.getTime();

        // 处理之前的老数据
        if (userInfo2.getVipExpireDate() == null) {
            // 如果没有vip到期时间 设置为注册时间
            userInfo2.setVipExpireDate(userInfo2.getRegistrationTime());
        }

        // vip类型有变动
        if (!userInfo2.getVipType().equals(userInfo.getVipType())) {
            // vip类型变成v0到期时间 改成昨天
            Integer day = powerBankService.updateVipType("1", userInfo, userInfo2.getId());
            userInfo2.setVipExpireDate(DateUtil.getAfterDayDate(day + ""));
            sysLogService.addSysLog(getUsername(), userInfo.getId(), "PC", SysLogEnum.UPDATE_USER_INFO
                    , getUsername() + "--在" + format + "--修改用户vip:" + userInfo2.getVipType() + "----->" + userInfo.getVipType() + "操作账号:" + userInfo.getAccount());
        }

        userInfo2.setVipType(userInfo.getVipType());

        // 2023年1月2日  备注信息
        userInfo2.setRemark(userInfo.getRemark());
        //真实姓名
        userInfo2.setRealName(userInfo.getRealName());
        if (userInfo.getDzstatus() != 1) {
            // 强制下线前端登录状态
            ehcacheDao.hdel(CacheApiKey.LOGIN_CONSTANT, userInfo.getCountryCodeNumber() + "_" + userInfo.getAccount());
        }
        /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();*/


        if (userInfo2.getLimitBuyCdb() != userInfo.getLimitBuyCdb()) { // 限制购买CDB

            sysLogService.addSysLog(getUsername(), userInfo.getId(), "PC", SysLogEnum.UPDATE_USER_LIMIT
                    , getUsername() + "--在" + format + "--修改限制购买CDB:" + (userInfo.getLimitBuyCdb() == 1 ? "限制购买" : "取消限制") + ", 操作账号:" + userInfo.getAccount());
        } else if (userInfo2.getLimitDrawing() != userInfo.getLimitDrawing()) { // 限制提款

            sysLogService.addSysLog(getUsername(), userInfo.getId(), "PC", SysLogEnum.UPDATE_USER_LIMIT
                    , getUsername() + "--在" + format + "--修改限制提款:" + (userInfo.getLimitDrawing() == 1 ? "限制提款" : "取消限制") + ", 操作账号:" + userInfo.getAccount());
        } else if (userInfo2.getLimitProfit() != userInfo.getLimitProfit()) { // 限制收益

            sysLogService.addSysLog(getUsername(), userInfo.getId(), "PC", SysLogEnum.UPDATE_USER_LIMIT
                    , getUsername() + "--在" + format + "--修改限制收益:" + (userInfo.getLimitProfit() == 1 ? "限制收益" : "取消限制") + ", 操作账号:" + userInfo.getAccount());
        } else if (userInfo2.getLimitCode() != userInfo.getLimitCode()) { // 限制收益

            sysLogService.addSysLog(getUsername(), userInfo.getId(), "PC", SysLogEnum.UPDATE_USER_LIMIT
                    , getUsername() + "--在" + format + "--修改限制邀请码:" + (userInfo.getLimitProfit() == 1 ? "限制邀请码" : "取消限制") + ", 操作账号:" + userInfo.getAccount());
        } else {
            sysLogService.addSysLog(getUsername(), userInfo.getId(), userInfo.getAccount(), "PC", SysLogEnum.UPDATE_USER_INFO);
        }
        userInfo2.setLimitBuyCdb(userInfo.getLimitBuyCdb());
        userInfo2.setLimitProfit(userInfo.getLimitProfit());
        userInfo2.setLimitDrawing(userInfo.getLimitDrawing());
        userInfo2.setLimitCode(userInfo.getLimitCode());
        this.update(userInfo2);

        return Rets.success();
    }

    /**
     * 修改上级邀请码业务
     *
     * @param userInfoSup
     * @param userInfo
     * @return false 表示当前修改用户为顶级账号 不能修改上级邀请码
     */
    @Transactional
    public boolean updateSuperCode(UserInfo userInfoSup, UserInfo userInfo) {
        // 上级邀请码 = 自己邀请码 = 团队邀请码 说明 就是最顶级的  所以对层级无需操作
        if (userInfo.getInvitationCode().equals(userInfo.getSourceInvitationCode())
                && userInfo.getInvitationCode().equals(userInfo.getSuperiorInvitationCode())) {
            return false;
        }
        Integer levels = userInfo.getLevels();
        userInfo.setLevels(userInfoSup.getLevels() + 1);
        userInfo.setPinvitationCode(userInfoSup.getPinvitationCode() + "[" + userInfo.getInvitationCode() + "],");
        userInfo.setSuperiorInvitationCode(userInfoSup.getInvitationCode());
        userInfo.setSourceInvitationCode(userInfoSup.getSourceInvitationCode());
        // 修改自己下级
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("pinvitationCode", SearchFilter.Operator.LIKE, "[" + userInfo.getInvitationCode() + "],"));
        filters.add(SearchFilter.build("levels", SearchFilter.Operator.GT, levels));
        List<UserInfo> userInfoList = this.queryAll(filters);

        for (UserInfo info : userInfoList) {
            info.setSourceInvitationCode(userInfo.getSourceInvitationCode());
            info.setPinvitationCode(userInfo.getPinvitationCode() + "[" + info.getInvitationCode() + "],");
            info.setLevels(userInfo.getLevels() + 1);
            this.update(info);
        }
        return true;
    }

    // 免密登录
    public Ret secretFreeLogin(Long id) {
        UserInfo userInfo = this.get(id);
        if (userInfo == null) {
            return Rets.failure("用户信息错误");
        }
//        String token = apiToken.createToken(userInfo, System.currentTimeMillis());
        Object token = ehcacheDao.hget(CacheApiKey.LOGIN_CONSTANT, userInfo.getCountryCodeNumber() + "_" + userInfo.getAccount());
        if (token==null){
            token = apiToken.createToken(userInfo, System.currentTimeMillis());
        }
        System.out.println(token);
        String str = configCache.get(ConfigKeyEnum.DZ_IV_LIKE);
        if (StringUtil.isEmpty(str)) {
            return Rets.failure("缺少H5端URL参数");
        }
        System.out.println(token);
        String url = str.split("/#/")[0] + "/#/?tk=" + UriUtils.encode(UriUtils.encode(token.toString(), "utf8"), "utf8");
        return Rets.success(url);
    }

    // 数据造假
    @Transactional(rollbackFor = Exception.class)
    public Ret addUser(FalseDataForm falseDataForm, String userName, String registerIpCity) {
        String[] falseDate = falseDataForm.getFalseDate().replace("\n", "").split(",");

        FalseData falseData = new FalseData(falseDataForm);

        String textCode = configCache.get(ConfigKeyEnum.TEST_USER_CODE);
        int count = falseDate.length;
        List<UserInfo> userInfoList = new ArrayList<>();
        List<VipRebateRecord> vipRebateList = new ArrayList<>();
        List<RecordPb> cdbRebateList = new ArrayList<>();
        for (String data : falseDate) {
            String[] split = data.split("---");
            if (split.length != 5) {
                // 数据格式不对
                count--;
                continue;
            }
            String account = split[0];
            Integer num = Integer.valueOf(split[1]);

            if (num > 50) {
                return Rets.failure("单条数据每次最多创建50个下级");
            }

            String vipType = split[2];
            String vipRebate = split[3]; // 每个下级给自己的vip返佣
            String cdbRebate = split[4]; // 每个下级给自己的cdb返佣
            // 查询当前用户是否为测试用户
            UserInfo userInfo = this.get(SearchFilter.build("account", account));
            if (userInfo == null) {
                count--;
                continue;
            }
            if (!userInfo.getSourceInvitationCode().equals(textCode)) {
                // 不为测试账户 直接跳过 进行下一次循环
                count--;
                continue;
            }

            for (Integer i = 0; i < num; i++) {
                UserInfo user = new UserInfo();
                user.setSourceInvitationCode(userInfo.getSourceInvitationCode());
                user.setSuperiorInvitationCode(userInfo.getInvitationCode());
                // 默认支付密码
                String paymentPassword = configCache.get(ConfigKeyEnum.USER_DEF_PAY_PWD).trim();
                paymentPassword = StringUtil.isEmpty(paymentPassword) ? "123456" : paymentPassword;
                user.setPaymentPassword(MD5.md5(paymentPassword, ""));
                user.setInvitationCode(RandomUtil.getrandomInvitationCode());
                String s = user.getInvitationCode();
                // 用户关系  父级递归+用户级别
                user.setLevels(userInfo.getLevels() + 1);
                user.setPinvitationCode(userInfo.getPinvitationCode() + "[" + s + "],");
                // end
                user.setIdw(new IdWorker().nextId() + "");
                user.setCountryCodeNumber(userInfo.getCountryCodeNumber());
                user.setName(RandomUtil.getRandomName(8));
                String phone = "";
                // 查询当前生成的电话号码 有没有注册 如果注册了 就重新生成
                while (StringUtil.isEmpty(phone)) {
                    String randomYZMNumber = RandomUtil.getRandomYZMNumber(userInfo.getAccount().length());
                    List<SearchFilter> filters = new ArrayList<>();
                    filters.add(SearchFilter.build("countryCodeNumber", userInfo.getCountryCodeNumber()));
                    filters.add(SearchFilter.build("account", randomYZMNumber));
                    UserInfo newUser = this.get(filters);
                    if (newUser == null) {
                        phone = randomYZMNumber;
                    }
                }
                user.setAccount(phone);
                user.setPassword(MD5.md5("123456", ""));
                user.setAuthenticatorPassword(GoogleAuthenticator.generateSecretKey());
                user.setUserType("1");
                user.setVipType(vipType);
                user.setVipExpireDate(null);
                Date date = DateUtil.parseTime(DateUtil.getTime());
                user.setRegisterIp(HttpUtil.getIp());
                user.setRegistrationTime(date);
                user.setRegisterIpCity(registerIpCity); // IP城市
                // jk 2023年1月9日
                user.setLastIp(HttpUtil.getIp());
                user.setLastTime(date);
                user.setLastIpCity(registerIpCity);
                // end
                user.setDzstatus(1);
                // 限制购买
                user.setLimitBuyCdb(2);
                // 限制提款
                user.setLimitDrawing(2);
                // 限制提款
                user.setLimitProfit(2);
                user.setFidw(falseData.getIdw());
                userInfoList.add(user);
                // 增加vip返佣
                VipRebateRecord vipRebateRecord = new VipRebateRecord();
                vipRebateRecord.setIdw(new IdWorker().nextId() + "");
                vipRebateRecord.setSourceInvitationCode(userInfo.getSourceInvitationCode());
                vipRebateRecord.setUid(userInfo.getId());
                vipRebateRecord.setAccount(userInfo.getAccount());
                vipRebateRecord.setMoney(Double.valueOf(vipRebate));
                vipRebateRecord.setPreviousAmount(0.00);
                vipRebateRecord.setAmountAfter(0.00);
                vipRebateRecord.setSourceUserAccount(phone);
                vipRebateRecord.setOldVipType("v0");
                vipRebateRecord.setNewVipType(vipType);
                vipRebateRecord.setRelevels(1);
                vipRebateRecord.setFidw(falseData.getIdw());
                vipRebateList.add(vipRebateRecord);
                // 增加cdb返佣
                RecordPb recordPb = new RecordPb();
                recordPb.setIdw(new IdWorker().nextId() + "");
                recordPb.setSourceInvitationCode(userInfo.getSourceInvitationCode());
                recordPb.setUid(userInfo.getId());
                recordPb.setAccount(userInfo.getAccount());
                recordPb.setMoney(Double.valueOf(cdbRebate));
                recordPb.setFormerCreditScore(0.00);
                recordPb.setPostCreditScore(0.00);
                recordPb.setSourceUserAccount(phone);
                recordPb.setRelevels(1);
                recordPb.setFidw(falseData.getIdw());
                cdbRebateList.add(recordPb);
            }

        }

        userInfoRepository.saveAll(userInfoList);
        vipRebateRecordRepository.saveAll(vipRebateList);
        recordPbRepository.saveAll(cdbRebateList);

        falseData.setRemark("添加用户下级造假数据:成功" + userInfoList.size() + "条");
        if (userInfoList.size() > 0) {
            falseDataService.insert(falseData);
        }
        sysLogService.addSysLog(userName, null, "PC", SysLogEnum.FALSE_DATE,
                getUsername() + "--在" + DateUtil.getTime() + "--增加用户下级造假数据:" + falseDate.length + "条");

        return Rets.success("添加用户下级造假数据:成功" + userInfoList.size() + "条");

    }

    // 修改vip 到期时间
    @Transactional(rollbackFor = Exception.class)
    public Ret updateVipDate(UserInfoDto dto) {

        if (dto.getVipExpireDate() == null) {
            return Rets.failure("到期时间不能为空");
        }
        UserInfo userInfo = this.get(dto.getId());

        if ("v0".equals(userInfo.getVipType())) {
            return Rets.failure("非会员无法修改到期时间");
        }
        String time = DateUtil.getTime();
        if (DateUtil.compareDate(time, DateUtil.format(dto.getVipExpireDate(), "yyyy-MM-dd HH:mm:ss"))) {
            return Rets.failure("到期时间不能小于当前时间");
        }
        userInfo.setVipExpireDate(dto.getVipExpireDate());
        this.update(userInfo);

        // 更新充电宝到期时间

        // 查询当前用户有没有未过期充电宝任务
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("uid", dto.getId()));
        // 过期 < 当天
        filters.add(SearchFilter.build("expireTime", SearchFilter.Operator.GT, DateUtil.parseTime(time)));
        List<PowerBankTask> powerBankTasks = powerBankTaskService.queryAll(filters);

        for (PowerBankTask powerBankTask : powerBankTasks) {
            powerBankTask.setExpireTime(dto.getVipExpireDate());
            powerBankTaskService.update(powerBankTask);
        }
        return Rets.success();
    }

    /**
     * 获取用户个人信息V2
     */
    public Ret<UserInfoVo> getUserInfoV2(Long userId) {
        String sql="select u.id,u.account,u.real_name as realName,u.head_portrait_key as headPortraitKey,u.vip_type as vipType,u.levels," +
                "u.invitation_code as invitationCode,u.name,u.vip_expire_date as vipExpireDate,v.vip_img as vipImg " +
                "from t_dzuser_user as u left join t_dzvip_vipmessage as v on u.vip_type=v.vip_type where u.id="+userId;
        List<?> objects = userInfoRepository.queryObjBySql(sql, UserInfo.class);
        UserInfo one =  (UserInfo) objects.get(0);

        UserInfoVo userInfoVo = new UserInfoVo();
        if (one != null) {
            userInfoVo.setId(BigInteger.valueOf(one.getId()));
            userInfoVo.setAccount(StringUtil.addXing(false, one.getAccount()));
            userInfoVo.setHeadPortraitKey(one.getHeadPortraitKey());
            userInfoVo.setVipType(one.getVipType());
            userInfoVo.setVipImg(one.getVipImg());
            userInfoVo.setInvitationCode(one.getInvitationCode());
            userInfoVo.setName(one.getName());
            userInfoVo.setRealName(one.getRealName());
            if (one.getVipExpireDate() != null) {
                Date now = DateUtil.parseTime(DateUtil.getTime());
                long s = now.getTime() - one.getVipExpireDate().getTime();
                TimeUnit time = TimeUnit.DAYS;
                long days = time.convert(s, TimeUnit.MILLISECONDS);
                if (days <= 0) {
                    days = 0;
                }
                userInfoVo.setDueDay(BigInteger.valueOf(days));
            }
            // 查询当前账号是否有 造假统计
            FalseTotal falseTotal = falseTotalService.get(SearchFilter.build("account", one.getAccount()));

            if (falseTotal != null) {
                userInfoVo.setBalance(new BigDecimal(falseTotal.getBalance()));
                userInfoVo.setProfitOfTheDay(new BigDecimal(falseTotal.getProfitOfTheDay()));
                userInfoVo.setTotalWithdrawalAmount(new BigDecimal(falseTotal.getTotalWithdrawalAmount()));
                userInfoVo.setTotalRevenue(new BigDecimal(falseTotal.getTotalRevenue()));
                userInfoVo.setTeamSize(new BigInteger(falseTotal.getTeamSize()));
                userInfoVo.setTeamReport(new BigDecimal(falseTotal.getTeamReport()));
                return Rets.success(userInfoVo);
            }

            // 统计数据
            String vipFriendSql = UserInfoServiceSql.sqlBalanceInformationV2(one);
            Map mapBySql = userInfoRepository.getMapBySql(vipFriendSql);
            UserInfoVoBean uib = BeanUtil.mapToBean(mapBySql, new UserInfoVoBean());

            userInfoVo.setBalance(
                    BigDecimal.valueOf(
                            BigDecimalUtils.add(uib.getTotalBonuspb().doubleValue(), // 充电宝返佣金额(自己包括自己下级)
                                    uib.getTotalBonusIncome().doubleValue(), // 直冲直扣
                                    uib.getDwPayVip().doubleValue()) // 下级购买vip返佣金额
                    )
            );


            // 22-12-24 当日收益：当天收益+当天手动加扣款+当天返佣
            userInfoVo.setProfitOfTheDay(
                    BigDecimal.valueOf(
                            BigDecimalUtils.add(uib.getVipToDay().doubleValue(), uib.getBrToDay().doubleValue(), uib.getCdbToDay().doubleValue())
                    )
            );
            // 用户余额
            userInfoVo.setTotalWithdrawalAmount(uib.getUserBalance());
            //中奖金额
            userInfoVo.setToWinningAmount(uib.getWinningAmount());
            // 总收益 = 收益明细
            userInfoVo.setTotalRevenue(userInfoVo.getBalance());
            //团队规模 =vip人数    （往下3级去除v0 v1)
            userInfoVo.setTeamSize(uib.getTeams());
            // 团队报告=下级晋级返佣+下级收益返佣  dwTotalBonuspb   dwPayVip
            // 团队报告=下3级晋级返佣+下3级收益返佣
            userInfoVo.setTeamReport(
                    BigDecimal.valueOf(
                            BigDecimalUtils.add(uib.getDwPayVip().doubleValue(), uib.getDwTotalBonuspb().doubleValue())
                    )
            );
        }
        return Rets.success(userInfoVo);
    }

    public void userInfoExportV2(HttpServletResponse response, List<UserInfo> userInfoList,Integer levels) {
        //字典
        Map<String, String> userType = ConstantFactory.me().getDictsToMap("用户类型");
        Map<String, String> vipType = ConstantFactory.me().getDictsToMap("ViP类型");


        List<cn.rh.flash.bean.vo.dzser.UserInfoVo> userInfoVos = new ArrayList<>();
        for (UserInfo userInfo : userInfoList) {
            cn.rh.flash.bean.vo.dzser.UserInfoVo vo=new cn.rh.flash.bean.vo.dzser.UserInfoVo();
            BeanUtils.copyProperties(userInfo,vo);
            //翻译
            if (ObjUtil.isNotEmpty(userType) && StringUtil.isNotEmpty(userType.get(userInfo.getUserType()))){
                vo.setUserTypeName(userType.get(userInfo.getUserType()));
            }
            if (ObjUtil.isNotEmpty(vipType) && StringUtil.isNotEmpty(vipType.get(userInfo.getVipType()))){
                vo.setVipTypeName(vipType.get(userInfo.getVipType()));
            }
            if (ObjUtil.isNotEmpty(userInfo.getDzstatus())){
                switch (userInfo.getDzstatus()){
                    case 1:
                        vo.setDzstatusName("启用");
                        break;
                    case 2:
                        vo.setDzstatusName("停用");
                        break;
                    case 3:
                        vo.setDzstatusName("删除");
                        break;
                }
            }
            if (ObjUtil.isNotEmpty(userInfo.getLimitBuyCdb())){
                switch (userInfo.getLimitBuyCdb()){
                    case 1:
                        vo.setLimitBuyCdbName("已限购");
                        break;
                    case 2:
                        vo.setLimitBuyCdbName("未限购");
                        break;
                }
            }
            if (ObjUtil.isNotEmpty(userInfo.getLimitProfit())){
                switch (userInfo.getLimitProfit()){
                    case 1:
                        vo.setLimitProfitName("已限收益");
                        break;
                    case 2:
                        vo.setLimitProfitName("未限收益");
                        break;
                }
            }
            if (ObjUtil.isNotEmpty(userInfo.getLimitDrawing())){
                switch (userInfo.getLimitDrawing()){
                    case 1:
                        vo.setLimitDrawingName("未开启");
                        break;
                    case 2:
                        vo.setLimitDrawingName("已开启");
                        break;
                }
            }
            vo.setLevels((userInfo.getLevels() - levels )+ "级");
            userInfoVos.add(vo);
        }
        EasyExcelUtil.export(response,"用户数据",userInfoVos, cn.rh.flash.bean.vo.dzser.UserInfoVo.class);

    }

    /**
     * 查询团队最新接口
     * @param invitationCode    本人邀请码
     * @param selfLevels        本人等级
     * @param teamDto
     * @return
     */
    public List<GetTeamOneVo> newGetTeam(String invitationCode, Integer selfLevels,TeamDto teamDto) {
        // 团队用户信息  账号  头像 注册是时间    当日新增人数  团队规模  vip等级
        String sqlGetTeamOneVo = UserInfoServiceSql.sqlGetTeam(invitationCode, selfLevels,teamDto.getAccountFragment(),teamDto.getLevels());
        System.out.println(sqlGetTeamOneVo);
        sqlGetTeamOneVo = SqlSpecification.toSqlLimit(teamDto.getPageNo(), teamDto.getPageSize(), sqlGetTeamOneVo, "id");
        List<GetTeamOneVo> l1Id = (List<GetTeamOneVo>) userInfoRepository.queryObjBySql(sqlGetTeamOneVo, GetTeamOneVo.class);
        if (l1Id.size() == 1) {
            if (l1Id.get(0).getRegistrationTime() == null) {
                l1Id.remove(0);
            }
        }
        return l1Id;
    }


    public Set<Long> getId(String testCode,String ucode) {
        Set<Long> set=new HashSet<>();
        List<UserInfo> list = (List<UserInfo>) userInfoRepository.queryObjBySql(UserInfoServiceSql.getIdSql(testCode, ucode), UserInfo.class);
        for (UserInfo userInfo : list) {
            set.add(userInfo.getId());
        }
        return set;
    }

    public Set<Long> newGetId(String testCode,String ucode) {
        Set<Long> set=new HashSet<>();
        List<UserInfo> list =userInfoRepository.queryBySql(UserInfoServiceSql.getIdSql(testCode, ucode));
        for (UserInfo userInfo : list) {
            set.add(userInfo.getId());
        }
        return set;
    }


    /**
     * 修改用户状态
     * status 等于什么就修改什么
     * 限制购买     1
     * 限制提款     2
     * 限制收益     3
     * 限制邀请码   4
     * 启用/冻结    5
     * @param userInfo
     * @return
     */
    @Transactional
    public Ret updateUserByStatus(HashMap userInfo) {
        Long id = Long.valueOf(userInfo.get("id").toString());
        String ucode = getUcode();
        UserInfo userInfo1 = this.get(id);
        switch (userInfo.get("status").toString()){
            case "1":
                    userInfo1.setLimitBuyCdb(Integer.valueOf(userInfo.get("limitBuyCdb").toString()));
                sysLogService.addSysLog(getUsername(), id, "PC", SysLogEnum.UPDATE_USER_LIMIT
                        , getUsername() + "--在" + "--修改限制购买:" + (userInfo1.getLimitBuyCdb() == 1 ? "限制" : "取消限制") + ", 被操作账号:" + userInfo1.getAccount());
                break;
            case "2":
                    userInfo1.setLimitDrawing(Integer.valueOf(userInfo.get("limitDrawing").toString()));
                sysLogService.addSysLog(getUsername(), id, "PC", SysLogEnum.UPDATE_USER_LIMIT
                        , getUsername() + "--在" + "--修改限制提款:" + (userInfo1.getLimitDrawing() == 1 ? "限制" : "取消限制") + ", 被操作账号:" + userInfo1.getAccount());
                break;
                case "3":
                    userInfo1.setLimitProfit(Integer.valueOf(userInfo.get("limitProfit").toString()));
                sysLogService.addSysLog(getUsername(), id, "PC", SysLogEnum.UPDATE_USER_LIMIT
                        , getUsername() + "--在" + "--修改限制收益:" + (userInfo1.getLimitProfit() == 1 ? "限制" : "取消限制") + ", 被操作账号:" + userInfo1.getAccount());
                break;
                case "4":
                    userInfo1.setLimitCode(Integer.valueOf(userInfo.get("limitCode").toString()));
                sysLogService.addSysLog(getUsername(), id, "PC", SysLogEnum.UPDATE_USER_LIMIT
                        , getUsername() + "--在" + "--修改限制邀请码:" + (userInfo1.getLimitCode() == 1 ? "限制" : "取消限制") + ", 被操作账号:" + userInfo1.getAccount());
                break;
                case "5":
                    userInfo1.setDzstatus(Integer.valueOf(userInfo.get("dzstatus").toString()));
                sysLogService.addSysLog(getUsername(), id, "PC", SysLogEnum.UPDATE_USER_LIMIT
                        , getUsername() + "--在" + "--修改启用/冻结:" + (userInfo1.getLimitBuyCdb() == 1 ? "启用" : "冻结") + ", 被操作账号:" + userInfo1.getAccount());
                break;
//            case "5":
//                userInfo1.setDzstatus(Integer.valueOf(userInfo.get("dzstatus").toString()));
//                sysLogService.addSysLog(getUsername(), id, "PC", SysLogEnum.UPDATE_USER_LIMIT
//                        , getUsername() + "--在" + "--修改启用/冻结:" + (userInfo1.getLimitBuyCdb() == 1 ? "启用" : "冻结") + ", 被操作账号:" + ucode);
//                break;
        }
        if (userInfo.get("remark")!=null){
            userInfo1.setRemark(userInfo.get("remark").toString());
        }
        this.update(userInfo1);
        if (userInfo1.getDzstatus() != 1) {
            // 强制下线前端登录状态
            ehcacheDao.hdel(CacheApiKey.LOGIN_CONSTANT, userInfo1.getCountryCodeNumber() + "_" + userInfo1.getAccount());
        }
        return Rets.success();
    }


}

