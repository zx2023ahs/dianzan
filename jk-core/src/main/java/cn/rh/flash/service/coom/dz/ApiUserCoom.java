package cn.rh.flash.service.coom.dz;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.entity.dzuser.UserBalance;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.entity.system.User;
import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.exception.ApiException;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.dao.dzuser.UserInfoRepository;
import cn.rh.flash.sdk.walletAddress.GetUsdtAddr;
import cn.rh.flash.security.apitoken.ApiLoginObject;
import cn.rh.flash.security.apitoken.ApiToken;
import cn.rh.flash.service.dzpower.PowerBankService;
import cn.rh.flash.service.dzuser.UserBalanceService;
import cn.rh.flash.service.dzuser.UserInfoService;
import cn.rh.flash.utils.*;
import cn.rh.flash.utils.Google.GoogleAuthenticator;
import com.sun.istack.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * api 用户 公共 业务
 */
@Component
public class ApiUserCoom extends BaseController {


    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private ApiToken apiToken;

    @Autowired
    private ConfigCache configCache;

    @Autowired
    private UserBalanceService userBalanceService;

    @Autowired
    private PowerBankService powerBankService;

    @Autowired
    private RecordInformation recordInformation;

    @Autowired
    private UserInfoRepository userInfoRepository;
    /*
    判断参数不能为空
    */
    public void check(Object... param) {
        for (Object value : param) {
            if (StrUtil.isBlankIfStr(value)) {
                throw new ApiException(MessageTemplateEnum.PARAM_NOT_EXIST);  //  PARAM_NOT_NULL("PARAM_NOT_NULL", "参数不能为空"),

            } else if (value instanceof String && StringUtil.isEmpty((String) value)) {
                throw new ApiException(MessageTemplateEnum.PARAM_NOT_EXIST);

            } else if (value instanceof Collection && CollUtil.isEmpty((Collection) value)) {
                throw new ApiException(MessageTemplateEnum.PARAM_NOT_EXIST);

            } else if (value instanceof BigDecimal && NumberUtil.isLessOrEqual((BigDecimal) value, new BigDecimal(0))) {
                throw new ApiException(MessageTemplateEnum.PARAM_NOT_EXIST);
            }
        }

    }


    /**
     * 获取用户信息
     *
     * @param userId
     * @return
     */
    public UserInfo getOneBySql(Long userId) {
        return userInfoService.getOneBySql(userId);
    }

    /**
     * 获取用户信息
     *
     * @param superiorInvitationCode
     * @return
     */
    public UserInfo getOneBySql(String superiorInvitationCode) {
        return userInfoService.getOneBySql(superiorInvitationCode);
    }

    /**
     * 获取请求头  language
     *
     * @return
     */
    public String getLanguage() {
        String language = request.getHeader("language");
        if (StringUtil.isEmpty(language)) {
            return "ZH_TW";
        }
        return language;
    }

    private UserInfo getApiLogObject() {
        String token = HttpUtil.getApiToken();
        if (StringUtil.isEmpty(token)) {
            return null;
        }
        ApiLoginObject apiLoginObject = apiToken.parseToken(token);
        if (apiLoginObject != null) {
            return apiLoginObject.getUserInfo();
        } else {
            return null;
        }
    }

    /**
     * 获取用户vip
     */
    @Nullable
    public String getVipType() {
        UserInfo userInfo = getApiLogObject();
        if (userInfo != null) {
            UserInfo userInfo1 = userInfoService.get(SearchFilter.build("id", userInfo.getId()));
            return userInfo1.getVipType();
        }
        return null;
    }

    @Nullable
    public Date getVipExpireDate() {
        UserInfo userInfo = getApiLogObject();
        if (userInfo != null) {
            UserInfo userInfo1 = userInfoService.get(SearchFilter.build("id", userInfo.getId()));
            return userInfo1.getVipExpireDate();
        }
        return null;
    }

    /**
     * 是否vip用户
     * @return
     */
    public boolean isVipUser() {
        String vipType = getVipType();
        if (!"v0".equals(vipType) && !"v1".equals(vipType)) {
            return true;
        }else {
            return false;
        }
    }


    /**
     * 获取用户ID
     */
    @Nullable
    public Long getUserId() {

        UserInfo userInfo = getApiLogObject();
        if (userInfo != null) {
            return userInfo.getId();
        }
        return null;
    }
    /**
     * 获取支付密码设置状态
     *
     * @return
     */
    @Nullable
    public Boolean hasPayPassword(Long userId) {
        UserInfo userInfo = userInfoService.getOneBySql(userId);
        if (userInfo != null) {
            return StringUtils.isNotEmpty(userInfo.getPaymentPassword());
        }
        return false;
    }

    /**
     * 获取余额
     */
    public BigDecimal getUserBalance(Long userId) {
        UserBalance userBalance = userBalanceService.get(SearchFilter.build("uid", userId));
        if (userBalance != null) {
            return BigDecimal.valueOf(userBalance.getUserBalance());
        }
        return BigDecimal.ZERO;
    }

    /**
     * 获取用户钱包信息
     */
    public UserBalance getUserBalanceById(Long userId) {
        UserBalance userBalance = userBalanceService.get(SearchFilter.build("uid", userId));
        if (userBalance != null) {
            return userBalance;
        }
        return null;
    }

    /**
     * 获取来源邀请码
     */
    @Nullable
    public String getSourceInvitationCode() {

        UserInfo userInfo = getApiLogObject();
        if (userInfo != null) {
            return userInfo.getSourceInvitationCode();
        }
        return null;
    }

    /**
     * 获取邀请码
     */
    @Nullable
    public String getInvitationCode() {

        UserInfo userInfo = getApiLogObject();
        if (userInfo != null) {
            return userInfo.getInvitationCode();
        }
        return null;
    }

    /**
     * 获取用户层级
     */
    @Nullable
    public Integer getLevels() {
        UserInfo userInfo = getApiLogObject();
        if (userInfo != null) {
            return userInfo.getLevels();
        }
        return null;
    }

    /**
     * 获取上级邀请码
     */
    @Nullable
    public String getSuperiorInvitationCode() {

        UserInfo userInfo = getApiLogObject();
        if (userInfo != null) {
            return userInfo.getSuperiorInvitationCode();
        }

        return null;
    }

    /**
     * 获取用户账号
     */
    @Nullable
    public String getAccount() {

        UserInfo userInfo = getApiLogObject();
        if (userInfo != null) {
            return userInfo.getAccount();
        }

        return null;
    }


    /**
     * 获取用户真实姓名
     */
    @Nullable
    public String getRealName() {

        UserInfo userInfo = getApiLogObject();
        if (userInfo != null) {
            return userInfo.getRealName();
        }
        return null;
    }

    /**
     * 同步创建移动端账号通过Ucode关联
     *
     * @param getName                用户名
     * @param countryCodeNumber      国家码
     * @param getPhone               账号
     * @param password               密码
     * @param superiorInvitationCode 上级邀请码   没有上级  上级邀请码是 来源邀请码
     * @param getUcode               来源邀请码
     */
    public UserInfo createAMobileAccount( String getName, String countryCodeNumber, String getPhone, String password,
                                          String superiorInvitationCode, String getUcode, Integer pLevels, String pinvitationCode,
                                          String registerIpCity,String paymentPassword,String realName
    )  {

        return createAMobileAccount( 1, getName,
                countryCodeNumber, getPhone, password, superiorInvitationCode, getUcode, pLevels, pinvitationCode, registerIpCity,paymentPassword,realName);
    }

    public UserInfo createAMobileAccount(

            Integer userType,
            String getName,
            String countryCodeNumber,
            String getPhone,
            String password,
            String superiorInvitationCode,
            String getUcode,
            Integer pLevels,
            String pinvitationCode,
            String registerIpCity,
            String paymentPassword,
            String realName

    )  {

        UserInfo userInfo = new UserInfo();
        userInfo.setSourceInvitationCode(getUcode);
        userInfo.setSuperiorInvitationCode(StringUtil.isEmpty(superiorInvitationCode) ? getUcode : superiorInvitationCode);

        // 默认支付密码
        if (StringUtil.isEmpty( paymentPassword )){
            paymentPassword = configCache.get( ConfigKeyEnum.USER_DEF_PAY_PWD).trim();
            paymentPassword = StringUtil.isEmpty( paymentPassword ) ? "123456" : paymentPassword;
        }
        userInfo.setPaymentPassword(MD5.md5(paymentPassword, ""));

        if (2 == userType) {
            userInfo.setInvitationCode(getUcode);
        } else {
            String s = RandomUtil.getrandomInvitationCode();
            userInfo.setInvitationCode(s);
        }

        String s = userInfo.getInvitationCode();
        // 用户关系  父级递归+用户级别
        if (pinvitationCode == null) {
            userInfo.setPinvitationCode("[" + s + "],");
            userInfo.setLevels(pLevels);
        } else {
            userInfo.setLevels(pLevels + 1);
            userInfo.setPinvitationCode(pinvitationCode + "[" + s + "],");
        }
        // end

        userInfo.setIdw(new IdWorker().nextId() + "");
        userInfo.setCountryCodeNumber(countryCodeNumber);

        userInfo.setName(getName);
        userInfo.setRealName(realName);
        userInfo.setAccount(getPhone);
        userInfo.setPassword(MD5.md5(password, ""));
        userInfo.setAuthenticatorPassword(GoogleAuthenticator.generateSecretKey());
        userInfo.setUserType(userType + "");
        userInfo.setVipType("v0");

        Date date = DateUtil.parseTime(DateUtil.getTime());
        userInfo.setVipExpireDate(date);

        userInfo.setRegisterIp(HttpUtil.getIp());
        userInfo.setRegistrationTime(date);
        userInfo.setRegisterIpCity(registerIpCity); // IP城市

        // jk 2023年1月9日
        userInfo.setLastIp(HttpUtil.getIp());
        userInfo.setLastTime(date);
        userInfo.setLastIpCity(registerIpCity);
        // end

        userInfo.setDzstatus(1);

        // 限制购买
        userInfo.setLimitBuyCdb(2);
        // 限制提款
        userInfo.setLimitDrawing(2);
        // 限制提款
        userInfo.setLimitProfit(2);
        // 限制邀请码
        userInfo.setLimitCode(2);

        userInfoService.insert(userInfo);

        powerBankService.updateVipType("0",userInfo,userInfo.getId());

        // 注册成功后相关业务
        userInfoService.relatedBusiness(userInfo);

        // 注册用户自身添加信用分
        String trim = configCache.get(ConfigKeyEnum.REGISTER_CREDIT).trim();
        recordInformation.changeCredit(userInfo.getSourceInvitationCode(),userInfo.getId(),userInfo.getAccount(),
                "1","1","1", Integer.valueOf(trim),"",userInfo.getAccount(),userInfo.getVipType());

        //上级添加信誉分
        String parentVipCredit= configCache.get(ConfigKeyEnum.REGISTER_CREDIT_SUPERIOR).trim();
        UserInfo superUser = userInfoService.get(SearchFilter.build("invitationCode", userInfo.getSuperiorInvitationCode()));
        if (superUser != null) {
            recordInformation.changeCredit(superUser.getSourceInvitationCode(), superUser.getId(), superUser.getAccount(), "1", "7", "1", (StringUtil.isEmpty(parentVipCredit) ? 0 : Integer.parseInt(parentVipCredit)), "", userInfo.getAccount(), userInfo.getVipType());
        }

        // 上级赠送积分
        //todo yc
//        UserInfo parentUser = userInfoService.get(SearchFilter.build("superiorInvitationCode", userInfo.getSuperiorInvitationCode()));
//        if (parentUser != null) {
//            String score = configCache.get(ConfigKeyEnum.REGISTER_SCORE).trim();
//            recordInformation.changeUserScore(Double.valueOf(score), parentUser.getId(), parentUser.getSourceInvitationCode(), parentUser.getAccount(), 2);
//        }

        return userInfo;

    }

    /**
     * 获取钱包地址
     *
     * @param userId
     * @return
     */
    public String getWalletAddress(Long userId) {
        UserBalance userBalance = userBalanceService.get(SearchFilter.build("uid", userId));
        if (userBalance != null) {
            return userBalance.getWalletAddress();
        }
        return "";
    }


    /**
     * 设置钱包地址
     *
     * @param userId
     */
    public void setWalletAddress(Long userId, String address,String channelType) {
        UserInfo oneBySql = getOneBySql(userId);
        userInitWalletAddress(oneBySql, address,channelType);
    }

    /**
     * 初始化钱包地址
     *
     * @param userInfo
     * @user    zx
     */
    public void userInitWalletAddress(UserInfo userInfo, String walletAddress,String channelType) {
        UserBalance userBalance = userBalanceService.get(SearchFilter.build("uid", userInfo.getId()));
        if (userBalance == null) {
            userBalance = new UserBalance();
            userBalance.setIdw(userInfo.getIdw());
            userBalance.setSourceInvitationCode(userInfo.getSourceInvitationCode());
            userBalance.setUid(userInfo.getId());
            userBalance.setAccount(userInfo.getAccount());
            userBalance.setDzversion(0);
            userBalance.setUserBalance(0.00);
            userBalance.setChannelType(channelType);

            // 获取钱包地址
            if (StringUtil.isEmpty(walletAddress)) {
                walletAddress = GetUsdtAddr.walletAddress(
                        userBalance.getSourceInvitationCode(),
                        userBalance.getUid(),
                        userBalance.getAccount()
                );
            }
            userBalance.setWalletAddress(walletAddress);
            userBalanceService.insert(userBalance);
        } else {  //  todo jk 纠错数据库里的数据
            // 获取钱包地址
            if (StringUtil.isEmpty(walletAddress)) {
                walletAddress = GetUsdtAddr.walletAddress(
                        userBalance.getSourceInvitationCode(),
                        userBalance.getUid(),
                        userBalance.getAccount()
                );
            }
            userBalance.setWalletAddress(walletAddress);
            userBalance.setChannelType(channelType);
            userBalanceService.update(userBalance);
        }
    }

    /**
     * 初始化钱包地址
     *
     * @param userInfo
     */
    public void initWalletAddress(UserInfo userInfo, String walletAddress) {
        UserBalance userBalance = userBalanceService.get(SearchFilter.build("uid", userInfo.getId()));
        if (userBalance == null) {
            userBalance = new UserBalance();
            userBalance.setIdw(userInfo.getIdw());
            userBalance.setSourceInvitationCode(userInfo.getSourceInvitationCode());
            userBalance.setUid(userInfo.getId());
            userBalance.setAccount(userInfo.getAccount());
            userBalance.setDzversion(0);
            userBalance.setUserBalance(0.00);

            // 获取钱包地址
            if (StringUtil.isEmpty(walletAddress)) {
                walletAddress = GetUsdtAddr.walletAddress(
                        userBalance.getSourceInvitationCode(),
                        userBalance.getUid(),
                        userBalance.getAccount()
                );
            }
            userBalance.setWalletAddress(walletAddress);
            userBalanceService.insert(userBalance);
        } else {  //  todo jk 纠错数据库里的数据
            // 获取钱包地址
            if (StringUtil.isEmpty(walletAddress)) {
                walletAddress = GetUsdtAddr.walletAddress(
                        userBalance.getSourceInvitationCode(),
                        userBalance.getUid(),
                        userBalance.getAccount()
                );
            }
            userBalance.setWalletAddress(walletAddress);
            userBalanceService.update(userBalance);
        }
    }

    public void updateMobileAccount(User oldUser) {
        UserInfo userInfo = userInfoService.get(SearchFilter.build("invitationCode", oldUser.getUcode()));
        userInfo.setAccount(oldUser.getAccount());
        userInfo.setCountryCodeNumber(oldUser.getPhone());
        userInfoService.update(userInfo);
    }

    @Transactional
    public void updateInvitation(String invitationCode, String invitationCode1) { // 原来邀请码    新邀请码
        List<UserInfo> userInfos = userInfoService.queryAll(SearchFilter.build("pinvitationCode", SearchFilter.Operator.LIKE, invitationCode));
        for (UserInfo userInfo : userInfos) {
            // 顶级账号
            if (invitationCode.equals(userInfo.getSuperiorInvitationCode())) {
                userInfo.setSuperiorInvitationCode(invitationCode1);
            }
            userInfo.setPinvitationCode(userInfo.getPinvitationCode().replace(invitationCode, invitationCode1));

        }
        userInfoRepository.saveAll(userInfos);
    }


}
