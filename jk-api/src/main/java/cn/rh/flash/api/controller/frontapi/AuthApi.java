package cn.rh.flash.api.controller.frontapi;

import cn.rh.flash.api.interceptor.Requestxz.RequestLimit;
import cn.rh.flash.api.utils.IpToCity.IpdbUtil;
import cn.rh.flash.bean.core.ApiLog;
import cn.rh.flash.bean.dto.api.*;
import cn.rh.flash.bean.entity.dzsys.TutorialCenter;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.entity.dzvip.DzVipMessage;
import cn.rh.flash.bean.entity.system.FileInfo;
import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.vo.api.*;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.CacheDao;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.cache.impl.EhcacheDao;
import cn.rh.flash.cache.impl.MyRedissonLocker;
import cn.rh.flash.security.apitoken.ApiToken;
import cn.rh.flash.service.coom.dz.ApiUserCoom;
import cn.rh.flash.service.dzsys.SmsMessageService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.dzsys.TutorialCenterService;
import cn.rh.flash.service.dzsys.UserIpPermissionsService;
import cn.rh.flash.service.dzuser.UserInfoService;
import cn.rh.flash.service.dzvip.DzVipMessageService;
import cn.rh.flash.service.system.FileService;
import cn.rh.flash.utils.*;
import com.alibaba.fastjson.JSONObject;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping("/api/auth")
@Api(tags = "用户信息")
@CrossOrigin
public class AuthApi extends ApiUserCoom {

    @Autowired
    private ApiToken apiToken;

    @Autowired
    private UserInfoService userInfoService;  //用户数据服务

    @Autowired
    private FileService fileService;

    @Autowired
    private EhcacheDao ehcacheDao;

    @Autowired
    private UserIpPermissionsService userIpPermissionsService;

    @Autowired
    private DzVipMessageService vipMessageService;

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private SmsMessageService smsMessageService;

    @Autowired
    private ConfigCache configCache;

    @Autowired
    private MyRedissonLocker myRedissonLocker;

    @Autowired
    private ContentApi contentApi;

    @Autowired
    private TutorialCenterService tutorialCenterService;

    @ApiOperationSupport(author = "jk")
    @ApiOperation( value = "账号密码登录" , notes = "v1 版本")
    @PostMapping("/login_v1")
    @ApiLog( version = "v1")
    public Ret loginV1( @Valid @RequestBody LoginV1Dto v1Vo) {

        // 移动段登录验证是否在IP黑名单  黑名单内无法登录
//        List<SearchFilter> filters = new ArrayList<>();
//.......
//        filters.add(SearchFilter.build("ip",HttpUtil.getIp()));
//        filters.add(SearchFilter.build("types","MOVE"));
//        filters.add(SearchFilter.build("blackOrWhite","BlackList"));
//        UserIpPermissions userIpPermissions = userIpPermissionsService.get(filters);
//        // 当前IP在黑名单,无法登录
//        if (userIpPermissions!=null){
//            return Rets.failure( MessageTemplateEnum.THE_ACCOUNT_IS_DISABLED.getCode() , MessageTemplateEnum.THE_ACCOUNT_IS_DISABLED );
//        }
        String pwd = CryptUtil.desEncrypt(v1Vo.getPwd());
        pwd = MD5.md5(pwd,"");

        //  账号密码登录
        List<SearchFilter> sealist = new ArrayList<>();
        sealist.add( SearchFilter.build("countryCodeNumber",v1Vo.getInternationalCode() ) );
        sealist.add( SearchFilter.build("account",v1Vo.getAccount() ) );
        sealist.add( SearchFilter.build("password",pwd) );
        UserInfo userInfo = userInfoService.get( sealist );

        if( userInfo == null ) {
            return Rets.failure( MessageTemplateEnum.ACCOUNT_PASSWORD_ERROR.getCode() , MessageTemplateEnum.ACCOUNT_PASSWORD_ERROR );
        }
        if( userInfo.getDzstatus() != 1 ) {
            return Rets.failure( MessageTemplateEnum.THE_ACCOUNT_IS_DISABLED.getCode() , MessageTemplateEnum.ACCOUNT_PASSWORD_ERROR );
        }

        // 更新登录信息
        userInfo.setLastIp( HttpUtil.getIp() );
        userInfo.setLastTime( DateUtil.parseTime( DateUtil.getTime() ) );
        userInfo.setLastIpCity( IpdbUtil.findCity(configCache) );
        userInfoService.update( userInfo );
//        CompletableFuture.runAsync(()->userInfoService.update( userInfo ));
//        threadPoolTaskExecutor.execute(()->userInfoService.update( userInfo ));
        //生成 登录 token
        //登录日志
//        CompletableFuture.runAsync(()->sysLogService.addSysLog(userInfo.getAccount(),userInfo.getId(),userInfo.getAccount(),"APP", SysLogEnum.USER_LOGIN_INFO));
//        threadPoolTaskExecutor.execute(()->sysLogService.addSysLog(userInfo.getAccount(),userInfo.getId(),userInfo.getAccount(),"APP", SysLogEnum.USER_LOGIN_INFO));
        sysLogService.addSysLog(userInfo.getAccount(),userInfo.getId(),userInfo.getAccount(),"APP", SysLogEnum.USER_LOGIN_INFO);
        return Rets.success( apiToken.createToken( userInfo,System.currentTimeMillis() ) );
    }


    @ApiOperationSupport(author = "yc")
    @ApiOperation( value = "注册" , notes = "v1 版本")
    @PostMapping("/reg_v1")
    @ApiLog( version = "v1", logoin = "reg")
    public Ret regV1(@Valid @RequestBody RegV1Dto regV1Dto) {
        return userInfoService.regV1(regV1Dto, IpdbUtil.findCity(configCache) );
    }

    @ApiOperation( value = "注册" , notes = "v2 版本")
    @PostMapping("/reg_v2")
    @ApiLog( version = "v2", logoin = "reg")
    public Ret regV2(@Valid @RequestBody RegV2Dto regV2Dto) {
        return userInfoService.regV2(regV2Dto, IpdbUtil.findCity(configCache) );
    }

    @ApiOperationSupport(author = "yc")
    @ApiOperation( value = "设置钱包地址" , notes = "v1 版本")
    @PostMapping("/setWalletAddress")
    public Ret setWalletAddress(@Valid @RequestBody SetWalletAddressDto setWalletAddressDto) {
        return userInfoService.setWalletAddress(setWalletAddressDto,getUserId());
    }

    @ApiOperationSupport(author = "yc")
    @ApiOperation(value = "获取用户钱包地址", notes = "v1 版本")
    @GetMapping("/getWalletAddress")
    public Ret<String> getWalletAddress() {
        return Rets.success(getWalletAddress(getUserId()));
    }

    @ApiOperationSupport(author = "zx")
    @ApiOperation(value = "获取用户钱包地址信息", notes = "v1 版本")
    @GetMapping("/getUserWalletAddress")
    public Ret getUserWalletAddress() {
        return Rets.success(getUserBalanceById(getUserId()));
    }

    @ApiOperationSupport(author = "yc")
    @ApiOperation( value = "获取验证码" , notes = "v1 版本")
    @PostMapping("/getValidateCode")
    @RequestLimit(count = 5)
    public Ret getValidateCode() {
        return userInfoService.getValidateCode();
    }

    @ApiOperation( value = "获取验证码" , notes = "v2 版本")
    @PostMapping("/getValidateCodeV2")
    @RequestLimit(count = 5)
    public Ret getValidateCodeV2() {
        return userInfoService.getValidateCodeV2();
    }

    @ApiOperationSupport(author = "yc")
    @ApiOperation(value = "获取用户余额", notes = "v1 版本")
    @GetMapping("/getUserBalance")
    public Ret<BigDecimal> getUserBalance() {
        return Rets.success(getUserBalance(getUserId()));
    }

    @ApiOperationSupport(author = "yc")
    @ApiOperation( value = "收益记录" , notes = "v1 版本")
    @PostMapping("/incomeList")
    public Ret<List<InComeVo>> incomeList(@Valid @RequestBody PageDto pageDto) {
        return userInfoService.getIncomeList(pageDto,getUserId());
    }


    @ApiOperationSupport(author = "yc")
    @ApiOperation( value = "用户信息V1" , notes = "v1 版本")
    @GetMapping("/userInfo")
    public Ret<UserInfoVo> userInfo() {
        return userInfoService.getUserInfo(getUserId());
    }


    @ApiOperationSupport(author = "yangFuYu")
    @ApiOperation( value = "用户信息V2" , notes = "v2 版本")
    @GetMapping("/userInfoV2")
    public Ret<UserInfoVo> userInfoV2() {
        return userInfoService.getUserInfoV2(getUserId());
    }

    @ApiOperationSupport(author = "yc")
    @ApiOperation( value = "邀请页面" , notes = "v1 版本")
    @GetMapping("/invitation")
    public Ret<InvitationInfoVo> invitation() {
        return userInfoService.getInvitationInfo(getUserId());
    }

    @ApiOperationSupport(author = "yc")
    @ApiOperation( value = "充值记录" , notes = "v1 版本")
    @PostMapping("/rechargeList")
    public Ret<List<RechargeRecordVo>> rechargeList(@Valid @RequestBody RechargeOrWithdrawRecordsDTO rechargeOrWithdrawRecordsDTO) {
        return userInfoService.getRechargeList(rechargeOrWithdrawRecordsDTO,getUserId());
    }

    @ApiOperationSupport(author = "yc")
    @ApiOperation( value = "提现记录" , notes = "v1 版本")
    @PostMapping("/withdrawList")
    public Ret<List<WithdrawalsRecordVo>> withdrawList(@Valid @RequestBody RechargeOrWithdrawRecordsDTO rechargeOrWithdrawRecordsDTO) {
        return userInfoService.getWithdrawList(rechargeOrWithdrawRecordsDTO,getUserId());
    }

    @ApiOperationSupport(author = "yc")
    @ApiOperation( value = "VIP购买记录" , notes = "v1 版本")
    @PostMapping("/vipPurchaseList")
    public Ret<List<VipPurchaseVo>> vipPurchaseList(@Valid @RequestBody RechargeOrWithdrawRecordsDTO rechargeOrWithdrawRecordsDTO) {
        return userInfoService.getVipPurchaseList(rechargeOrWithdrawRecordsDTO,getUserId());
    }

    @ApiOperationSupport(author = "yc")
    @ApiOperation( value = "修改密码" , notes = "v1 版本")
    @PostMapping("/changePassword")
    public Ret changePassword(@Valid @RequestBody PasswordDto passwordDto) {
        return userInfoService.changePassword(passwordDto,getUserId(),1);
    }


    @ApiOperationSupport(author = "jk")
    @ApiOperation( value = "获取提现手续费" , notes = "v1 版本")
    @PostMapping("/handlingFee")
    public Ret<FeeVo> handlingFee() {
        FeeVo feeVo = new FeeVo();
        DzVipMessage vipMessage = vipMessageService.get(SearchFilter.build("vipType", getVipType()));
        if (vipMessage != null) {
            feeVo.setMin( vipMessage.getMinimumWithdrawal() );
            feeVo.setMax( vipMessage.getMaximumWithdrawal() );
            feeVo.setFee( vipMessage.getWithdrawalFee());
        }
        return Rets.success( feeVo );
    }

    @ApiOperationSupport(author = "yc")
    @ApiOperation( value = "修改交易密码" , notes = "v1 版本")
    @PostMapping("/changePayPassword")
    public Ret changePayPassword(@Valid @RequestBody PasswordDto passwordDto) {
        return userInfoService.changePassword(passwordDto,getUserId(),2);
    }


    @ApiOperationSupport(author = "jk")
    @ApiOperation( value = "设置交易密码" , notes = "v1 版本")
    @PostMapping("/setPayPassword")
    public Ret setPayPassword(@Valid @RequestBody SetPayPasswordDto setPayPasswordDto) {
        return userInfoService.changePassword(new PasswordDto("", setPayPasswordDto.getNewPassword()),getUserId(),3);
    }

//    @ApiOperationSupport(author = "skj")
//    @ApiOperation( value = "重置交易密码" , notes = "v1 版本")
//    @PostMapping("/reSetPayPassword")
//    public Ret reSetPayPassword(@Valid @RequestBody ForgetPasswordDto dto) {
//        //验证码短信验证码判断
//        String phone = dto.getCountryCode()+dto.getAccount();
//        String code = (String)ehcacheDao.hget(CacheApiKey.phoneCode, phone);
//        if (StringUtils.isEmpty(code) || !dto.getValidateCode().equals(code)){
//            return Rets.failure( MessageTemplateEnum.WRONG_VALIDATION_CODE.getCode() , MessageTemplateEnum.WRONG_VALIDATION_CODE );
//        }
//        return userInfoService.changePassword(new PasswordDto("", dto.getPassword()),getUserId(),4);
//    }

    @ApiOperationSupport(author = "yc")
    @ApiOperation(value = "获取是否设置交易密码", notes = "v1 版本")
    @GetMapping("/getPayPasswordStatus")
    public Ret<Boolean> getPayPasswordStatus() {
        return Rets.success(hasPayPassword(getUserId()));
    }


    @ApiOperationSupport(author = "yc")
    @ApiOperation( value = "上传头像" , notes = "v1 版本")
    @PostMapping("/changeHeadLogo")
    public Ret changeHeadLogo(@Valid @RequestBody HeadLogoDto headLogoDto) {
        return userInfoService.changeHeadLogo(headLogoDto,getUserId());
    }

    @ApiOperationSupport(author = "jk")
    @ApiOperation( value = "修改用户名称" , notes = "v1 版本")
    @PostMapping("/changeUname")
    public Ret changeUname(@Valid @RequestBody UnameDto unameDto) {
        return userInfoService.changeUname(unameDto,getUserId());
    }


    @ApiOperationSupport(author = "yc")
    @ApiOperation(value = "上传接口", notes = "v1 版本")
    @PostMapping("/upload")
    public Ret<FileInfo> upload(@RequestPart("file") MultipartFile multipartFile) {
        try {
            FileInfo fileInfo = fileService.uploadApi(multipartFile,getUserId());
            return Rets.success(fileInfo);
        } catch (Exception e) {
            return Rets.failure(MessageTemplateEnum.FAILED_TO_UPLOAD_FILE.getCode(),MessageTemplateEnum.FAILED_TO_UPLOAD_FILE);
        }
    }

    @ApiOperationSupport(author = "yc")
    @ApiOperation(value = "团队报告", notes = "v1 版本")
    @GetMapping("/teamReport")
    public Ret<TeamReportVo> getTeamReport() {
        return userInfoService.getTeamReport(getUserId());
    }

    @ApiOperationSupport(author = "yc")
    @ApiOperation(value = "退出", notes = "v1 版本")
    @GetMapping("/logout")
    @ApiLog( version = "v1", logoin = "eixt")
    public Ret logout() {
        // 记录完成日志后 清除
        return Rets.success();
    }

    @ApiOperationSupport(author = "yc")
    @ApiOperation(value = "忘记密码", notes = "v1 版本")
    @PostMapping("/forgetPassword")
    public Ret forgetPassword(@Valid @RequestBody ForgetPasswordDto forgetPassword) throws Exception {
        return userInfoService.forgetPassword(forgetPassword);
    }


    @ApiOperationSupport(author = "yc")
    @ApiOperation(value = "发送手机验证码", notes = "v1 版本")
    @GetMapping("/sendPhoneCode")
    @RequestLimit(count = 1,time = 60000)
    public Ret sendPhoneCode(@RequestParam String params) throws Exception {

        String s = CryptUtil.desEncrypt(params);  // 获取里面的key 和 iv   (不要修改)
        JSONObject jsonObject = JSONObject.parseObject(s);
        // 获取参数   (参数key都换一下不要和之前一样)
        String ctCode = jsonObject.getString("ctCode");
        String dzPhone = jsonObject.getString("dzPhone");
        String imgCode = jsonObject.getString("imgCode");
        String reqType = jsonObject.getString("reqType");

        // 下面的数据 没用 单纯为了增加解密难度
        String q4fj5lhd5 = jsonObject.getString("q4fj5lhd5");
        String f48d6qe4t = jsonObject.getString("f48d6qe4t");
        String g7wr4f2qv = jsonObject.getString("g7wr4f2qv");

        check(ctCode,dzPhone,imgCode,reqType,q4fj5lhd5,f48d6qe4t,g7wr4f2qv);

        SendPhoneCodeDto sendPhoneCodeDto = new SendPhoneCodeDto();
        sendPhoneCodeDto.setCountryCode(ctCode);
        sendPhoneCodeDto.setAccount(dzPhone);
        sendPhoneCodeDto.setCode(imgCode);
        sendPhoneCodeDto.setType(reqType);

        // 加锁防止重复调用
        String key = "sendPhoneCode_"+dzPhone;
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.lock(lock);
        if (b) {
            try {
                log.info("发送验证码获取到锁,用户ID:{}", dzPhone);
                return userInfoService.sendPhoneCode(sendPhoneCodeDto);
            } catch (Exception e) {
                e.printStackTrace();
                return Rets.failure(e.getMessage());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        log.error("发送验证码没有获取到锁,用户ID:{},时间:{}", dzPhone, DateUtil.getTime());
        return Rets.failure( MessageTemplateEnum.REQUEST_LIMIT.getCode(), MessageTemplateEnum.REQUEST_LIMIT );
    }
    @ApiOperation(value = "发送手机验证码", notes = "v2 版本")
    @GetMapping("/sendPhoneCodeV2")
    @RequestLimit(count = 1,time = 60000)
    public Ret sendPhoneCodeV2(@RequestParam String params) throws Exception {
        String s = CryptUtil.desEncrypt(params);  // 获取里面的key 和 iv   (不要修改)
        JSONObject jsonObject = JSONObject.parseObject(s);
        // 获取参数   (参数key都换一下不要和之前一样)
        String ctCode = jsonObject.getString("ctCode");
        String dzPhone = jsonObject.getString("dzPhone");
        String imgCode = jsonObject.getString("imgCode");
        String reqType = jsonObject.getString("reqType");
        String uuid = jsonObject.getString("uuid");
        // 下面的数据 没用 单纯为了增加解密难度
        String q4fj5lhd5 = jsonObject.getString("q4fj5lhd5");
        String f48d6qe4t = jsonObject.getString("f48d6qe4t");
        String g7wr4f2qv = jsonObject.getString("g7wr4f2qv");
        check(ctCode,dzPhone,imgCode,uuid,reqType,q4fj5lhd5,f48d6qe4t,g7wr4f2qv);
        SendPhoneCodeDto sendPhoneCodeDto = new SendPhoneCodeDto();
        sendPhoneCodeDto.setCountryCode(ctCode);
        sendPhoneCodeDto.setAccount(dzPhone);
        sendPhoneCodeDto.setCode(imgCode);
        sendPhoneCodeDto.setType(reqType);
        sendPhoneCodeDto.setUuid(uuid);
        // 加锁防止重复调用
        String key = "sendPhoneCode_"+dzPhone;
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.lock(lock);
        if (b) {
            try {
                log.info("发送验证码获取到锁,用户ID:{}", dzPhone);
                return userInfoService.sendPhoneCodeV2(sendPhoneCodeDto);
            } catch (Exception e) {
                e.printStackTrace();
                return Rets.failure(e.getMessage());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        log.error("发送验证码没有获取到锁,用户ID:{},时间:{}", dzPhone, DateUtil.getTime());
        return Rets.failure( MessageTemplateEnum.REQUEST_LIMIT.getCode(), MessageTemplateEnum.REQUEST_LIMIT );
    }

    @ApiOperationSupport(author = "yc")
    @ApiOperation(value = "是否启用手机验证码", notes = "v1 版本")
    @PostMapping("/isSendPhoneCode")
    public Ret isSendPhoneCode() {
        boolean isSendPhoneCode = smsMessageService.isSendPhoneCode();
        return Rets.success(isSendPhoneCode);
    }


    @ApiOperationSupport(author = "skj")
    @ApiOperation(value = "H5默认语言", notes = "v1 版本")
    @PostMapping("/getH5DefLang")
    public Ret getH5DefLang() {
        String lang = configCache.get(ConfigKeyEnum.SYSTEM_H5_DEF_LANG).trim();
        lang = StringUtil.isEmpty( lang ) ? "ZH_EN" : lang;
        return Rets.success(lang);
    }

    @ApiOperationSupport(author = "skj")
    @ApiOperation(value = "获取帮助中心数据", notes = "v1 版本")
    @PostMapping("/getTutorialCenter")
    public Ret getTutorialCenter() {
        List<TutorialCenter> tutorialCenters = tutorialCenterService.queryAll(SearchFilter.build("status", "1"));
        return Rets.success(tutorialCenters);
    }


    @ApiOperationSupport(author = "zx")
    @ApiOperation(value = "获取通道类型是否开启信息", notes = "v1 版本")
    @PostMapping("/getChannelTypeIsopen")
    public Ret getChannelTypeIsopen() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("polygon",configCache.get(ConfigKeyEnum.PAYMENT_USDT_POLYGON_ISOPEN).trim());
        map.put("trc20",configCache.get(ConfigKeyEnum.PAYMENT_USDT_TRC20_ISOPEN).trim());
        map.put("withdrawal_polygon",configCache.get(ConfigKeyEnum.WITHDRAWAL_USDT_POLYGON_ISOPEN).trim());
        map.put("withdrawal_trc20",configCache.get(ConfigKeyEnum.WITHDRAWAL_USDT_TRC20_ISOPEN).trim());
        return Rets.success(map);
    }

//    @ApiOperation(value = "获取VIP对应提现通道类型信息", notes = "v1 版本")
//    @PostMapping("/getVIPChannel")
//    public Ret getVIPChannel() {
//        DzVipMessage vip = ehcacheDao.hget(CacheDao.VIPMESSAGE, getVipType(),DzVipMessage.class);
//        if (vip==null){
//            return Rets.success();
//        }
//        String[] withdrawMethods = vip.getWithdrawMethods().split(",");
//        List<PaymentChannelVo> voList=new ArrayList<>();
//        for (String withdrawMethod:withdrawMethods){
//            PaymentChannelVo channelVo = ehcacheDao.hget(CacheDao.PAYMENT_CHANNEL, withdrawMethod, PaymentChannelVo.class);
//            if (channelVo!=null&&1==channelVo.getIsWithdrawal()){
//                voList.add(channelVo);
//            }
//        }
//        return Rets.success(voList);
//    }

    @ApiOperation(value = "获取充值通道类型信息", notes = "v1 版本")
    @PostMapping("/getRechargeChannel")
    public Ret getRechargeChannel() {
        return Rets.success(ehcacheDao.hmget(CacheDao.PAYMENT_CHANNEL,PaymentChannelVo.class)
                .stream().filter(p->1==p.getIsPayment())
                .sorted(Comparator.comparing(PaymentChannelVo::getSort).reversed())
                .collect(Collectors.toList())
        );
    }

    @ApiOperation(value = "获取活动区域是否开启信息", notes = "v1 版本")
    @PostMapping("/getActivity")
    public Ret getActivity() {
        return Rets.success(configCache.get(ConfigKeyEnum.ACTIVITY_ISOPEN));
    }

    @ApiOperation(value = "获取app下载链接", notes = "v1 版本")
    @PostMapping("/getAppDownLoadUrl")
    public Ret getAppDownLoadUrl() {
        return Rets.success(configCache.get(ConfigKeyEnum.APP_DOWNLOAD_URL));
    }

}
