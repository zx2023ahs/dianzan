package cn.rh.flash.api.controller.dz.dzuser;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.api.interceptor.Requestxz.RequestLimit;
import cn.rh.flash.api.utils.IpToCity.IpdbUtil;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.dto.UserInfoDto;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.api.UserInfoVo;
import cn.rh.flash.bean.vo.dz.BatchUserVo;
import cn.rh.flash.bean.vo.dzuser.StraightAddVo;
import cn.rh.flash.bean.vo.dzuser.StraightListVo;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.cache.impl.EhcacheDao;
import cn.rh.flash.config.chinesePattern.ChinesePattern;
import cn.rh.flash.service.coom.dz.ApiUserCoom;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.dzuser.UserInfoService;
import cn.rh.flash.service.system.FileService;
import cn.rh.flash.utils.*;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.UserInfoWrapper;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;


@RestController
@Log4j2
@RequestMapping("/dzuser/user")
public class UserInfoController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private FileService fileService;

    @Autowired
    private ApiUserCoom apiUserCoom;

    @Autowired
    private EhcacheDao ehcacheDao;

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private ConfigCache configCache;

    @Autowired
    private RedisUtil redisUtil;


//	str: '',
//	dzstatus:'',
//	vipType:'',
//	userType:'',
//	registerIp:'',
//	lastIp:''

    @GetMapping(value = "/list")
    @RequiresPermissions(value = "userInfo")
    public Ret list(@RequestParam(required = false) String str, @RequestParam(required = false) Integer dzstatus,
                    @RequestParam(required = false) String vipType, @RequestParam(required = false) String userType,
                    @RequestParam(required = false) String registerIp, @RequestParam(required = false) String lastIp,
                    @RequestParam(required = false) String expireTimes, @RequestParam(required = false) String expireTimee,
                    @RequestParam(required = false) Integer levels, @RequestParam(required = false) String sourceInvitationCode,
                    @RequestParam(required = false) String walletAddress, @RequestParam(required = false) String flag,
                    @RequestParam(required = false) Integer levelsQuery, @RequestParam(required = false) String gmt,
                    @RequestParam(required = false) String orderField, @RequestParam(required = false) String orderName,
                    @RequestParam(required = false) String countryCodeNumber, @RequestParam(required = false) String limitStr,
                    @RequestParam(required = false) String lastIpCity
    ) {

        //HttpUtil.getServerName();
        Page<UserInfo> page = new PageFactory<UserInfo>().defaultPage();
        //先判断是否代理商
        if (isProxy()) {
            page.addFilter("user.source_invitation_code", getUcode());
        }
//		else {
        //page.addFilter("user.source_invitation_code",sourceInvitationCode  );
//		}
        page.addFilter("user.vip_type", vipType);
        page.addFilter("user.user_type", userType);
        page.addFilter("user.country_code_number", countryCodeNumber);
        page.addFilter("user.last_ip_city", SearchFilter.Operator.LIKE, lastIpCity);

        page.addFilter("user.dzstatus", dzstatus);
        page.addFilter("user.last_ip", lastIp);
        page.addFilter("user.register_ip", registerIp);
        if (StringUtil.isNotEmpty(limitStr)) {
            page.addFilter("user." + limitStr, "1");
        }

//		if( StringUtil.isNotEmpty( expireTimes ) && StringUtil.isNotEmpty( expireTimee ) ){
//			page.addFilter("createTime", SearchFilter.Operator.BETWEEN, Arrays.asList( DateUtil.parseTime( expireTimes ),DateUtil.parseTime( expireTimee ) ) );
//		}


        page.addFilter("ub1.wallet_address", SearchFilter.Operator.EQ, walletAddress);
        page.addFilter(SearchFilter.build("user.fidw", SearchFilter.Operator.ISNULL));

        if (StringUtil.isNotEmpty(expireTimes) && StringUtil.isNotEmpty(expireTimee)) {
            //gmt = StringUtil.isEmpty( gmt ) ? TimeZone.getDefault().getID() : gmt;
            expireTimes = DateUtil.getTimeByZone(expireTimes);
            expireTimee = DateUtil.getTimeByZone(expireTimee);
            page.addFilter("user.create_time", SearchFilter.Operator.GTE, expireTimes);
            page.addFilter("user.create_time", SearchFilter.Operator.LT, expireTimee);
        }
        if (levels == null) {
            return Rets.failure("参数异常,请联系管理员");
        }
        if (levels.intValue() > 0) {
            if ("1".equals(flag)) { // 用户
                UserInfo userInfo = userInfoService.get(SearchFilter.build("invitationCode", str));
                if (userInfo == null) {
                    return Rets.success();
                }
                // pinvitationCode = [dkl5mb],[dupl5r],[dyqg2z],[d2wwjg],
                String pinvitationCode = userInfo.getPinvitationCode().replace("[" + userInfo.getInvitationCode() + "],", "");
                // 查询上级列表
                String[] arr = pinvitationCode.replace("[", "").replace("]", "").split(",", 0);
                page.addFilter("user.invitation_code", SearchFilter.Operator.IN, arr);
            } else { // 代理
                // 查询下级列表
                page.addFilter("user.pinvitation_code", SearchFilter.Operator.LIKE, "[" + str + "],");
                page.addFilter("user.levels", SearchFilter.Operator.GT, levels);
                if (levelsQuery != null) {
                    page.addFilter("user.levels", SearchFilter.Operator.EQ, levels + levelsQuery);
                }
            }
        } else {
            if (StringUtil.isNotEmpty(str)) {
                // 账号/邀请码/国家码
                //只能账号
                page.addFilter("user.account", str);
//                page.addFilter("user.account,user.invitation_code", SearchFilter.Operator.OR, str);
//                page.addFilter("user.country_code_number", SearchFilter.Operator.OR, str, SearchFilter.Join.or);
//                page.addFilter("user.invitation_code", SearchFilter.Operator.OR, str, SearchFilter.Join.or);
//                page.addFilter("user.register_ip", SearchFilter.Operator.LIKE, str, SearchFilter.Join.or);

//                page.addFilter(" user.account", SearchFilter.Operator.OR, "user.account LIKE "+str,SearchFilter.Join.and);
//                page.addFilter("user.country_code_number",  SearchFilter.Operator.OR, "user.country_code_number LIKE "+str,SearchFilter.Join.and);
//                page.addFilter("user.invitation_code",  SearchFilter.Operator.OR, "user.invitation_code LIKE "+str,SearchFilter.Join.and);

            }
        }
//		page = userInfoService.queryPage(page);


        page.setTotal(userInfoService.findCount(page, sourceInvitationCode));

        String orderSql = "order by u.id desc";
        if (StringUtils.isNotBlank(orderField) && StringUtils.isNotBlank(orderName)) {
            if (orderField.equals("user_balance")) {
                if (orderName.equals("ascending")) {
                    orderSql = "order by ub.user_balance asc,u.id desc";
                }
                if (orderName.equals("descending")) {
                    orderSql = "order by ub.user_balance desc,u.id desc";
                }
            }
        }

        List<UserInfo> userInfoList = userInfoService.findUserInfoPage(page, sourceInvitationCode, orderSql);

        List list = (List) new UserInfoWrapper(BeanUtil.objectsToMaps(userInfoList)).warp();
        page.setRecords(list);
        return Rets.success(page);
    }

    @PostMapping
    @BussinessLog(value = "新增用户信息", key = "name")
    @RequiresPermissions(value = "userInfoAdd")
    public Ret add(@Valid @RequestBody UserInfo userInfo) throws Exception {

        if (userInfo.getAccount().startsWith("0")) {
            if (!userInfo.getAccount().equals(userInfo.getAccount().replaceFirst("^0+", "0"))) {
                return Rets.failure(MessageTemplateEnum.WRONG_PHONE_FORMAT.getCode(), MessageTemplateEnum.WRONG_PHONE_FORMAT);
            }
            String replacePhone = userInfo.getAccount().replaceFirst("^0+", "");
            UserInfo userInfo1 = userInfoService.get(SearchFilter.build("account", replacePhone));
            if (userInfo1 != null) {
                return Rets.failure(MessageTemplateEnum.ACCOUNT_EXISTS.getCode(), MessageTemplateEnum.ACCOUNT_EXISTS);
            }
        } else {
            String replacePhone = "0" + userInfo.getAccount();
            UserInfo userInfo1 = userInfoService.get(SearchFilter.build("account", replacePhone));
            if (userInfo1 != null) {
                return Rets.failure(MessageTemplateEnum.ACCOUNT_EXISTS.getCode(), MessageTemplateEnum.ACCOUNT_EXISTS);
            }
        }

        // 验证账号格式
        if (!RegUtil.isValidAccount(userInfo.getAccount())) {
            return Rets.failure("账号格式错误");
        }

        //验证密码
        if (!RegUtil.isValidPassword(userInfo.getPassword())) {
            return Rets.failure("密码格式错误");
        }

        //验证交易密码
        if (!RegUtil.isValidPayPassword(userInfo.getPaymentPassword())) {
            return Rets.failure("交易密码格式错误");
        }


        UserInfo userInfo1 = userInfoService.get(SearchFilter.build("invitationCode", userInfo.getSuperiorInvitationCode()));
        // userType = 2 为内部用户 内部用户
        if (userInfo1 == null) {
            return Rets.failure("邀请码无效");
        }
        //查询是否注册过
//		UserInfo userInfo2 = userInfoService.findByAccountAndCountryCode(userInfo.getCountryCodeNumber(),userInfo.getAccount());
        UserInfo userInfo2 = userInfoService.get(SearchFilter.build("account", userInfo.getAccount()));
        if (userInfo2 != null) {
            return Rets.failure("当前账号已注册");
        }


        apiUserCoom.createAMobileAccount(
                StringUtil.isEmpty(userInfo.getName()) ? RandomUtil.getRandomName(8) : userInfo.getName(),
                userInfo.getCountryCodeNumber(), userInfo.getAccount(), userInfo.getPassword(),
                userInfo.getSuperiorInvitationCode(), userInfo1.getSourceInvitationCode(), userInfo1.getLevels(), userInfo1.getPinvitationCode(),
                IpdbUtil.findCity(configCache), userInfo.getPaymentPassword(),userInfo.getRealName()
        );
        sysLogService.addSysLog(getUsername(), userInfo.getId(), userInfo.getAccount(), "PC", SysLogEnum.ADD_USER_INFO);
        return Rets.success();
    }

    @PutMapping
    @BussinessLog(value = "更新用户信息", key = "name")
    @RequiresPermissions(value = "userInfoUpdate")
    public Ret update(@RequestBody @Validated(ChinesePattern.OnUpdate.class) UserInfo userInfo) {
        return userInfoService.userInfoUpdate(userInfo);
    }

    @DeleteMapping
    @BussinessLog(value = "删除用户信息", key = "id")
    @RequiresPermissions(value = "userInfoDelete")
    public Ret remove(Long id) {
        if (id == null) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        UserInfo userInfo = userInfoService.get(id);
        userInfo.setDzstatus(3);
        userInfoService.update(userInfo);
        sysLogService.addSysLog(getUsername(), userInfo.getId(), userInfo.getAccount(), "PC", SysLogEnum.DELETE_USER_INFO);
        return Rets.success();
    }

    @GetMapping("/getUserLevel")
    public Ret getUserLevel() {
        UserInfo userInfo = userInfoService.get(SearchFilter.build("invitationCode", getUcode()));
        if (userInfo == null) {
            return Rets.success(0);
        }
        return Rets.success(userInfo.getLevels());
    }

    @BussinessLog(value = "批量冻结解冻")
    @RequiresPermissions(value = "userInfoUpdate")
    @PostMapping("/batchUser")
    public Ret batchUser(@RequestBody @Validated BatchUserVo batchUserVo) {
//        userInfoService.batchUser(batchUserVo);
        List<UserInfo> userInfos = batchUserVo.getUserInfos();
        HashMap<String, String> user = new HashMap<>();
        user.put("status","5");
        user.put("remark",batchUserVo.getValue());
        user.put("dzstatus",batchUserVo.getFlg().toString());
        for (UserInfo userInfo : userInfos) {
            user.put("id",userInfo.getId().toString());
            userInfoService.updateUserByStatus(user);
//            sysLogService.addSysLog(getUsername(), userInfo.getId(), userInfo.getAccount(), "PC", SysLogEnum.USER_INFO_UPDATE_INFO);
        }

        return Rets.success();
    }

    @BussinessLog(value = "批量限制收益")
    @RequiresPermissions(value = "userInfoUpdate")
    @PostMapping("/batchLimitProfit")
    public Ret batchLimitProfit(@RequestBody @Validated BatchUserVo batchUserVo) {
//        userInfoService.batchLimitProfit(batchUserVo);
        List<UserInfo> userInfos = batchUserVo.getUserInfos();
        HashMap<String, String> user = new HashMap<>();
        user.put("status","3");
        user.put("remark",batchUserVo.getValue());
        user.put("limitProfit",batchUserVo.getFlg().toString());
        for (UserInfo userInfo : userInfos) {
            user.put("id",userInfo.getId().toString());
            userInfoService.updateUserByStatus(user);
//            sysLogService.addSysLog(getUsername(), userInfo.getId(), userInfo.getAccount(), "PC", SysLogEnum.USER_INFO_UPDATE_LIMIT_PROFIT);
        }
        return Rets.success();
    }

    @BussinessLog(value = "批量限制提款")
    @RequiresPermissions(value = "userInfoUpdate")
    @PostMapping("/batchLimitDrawing")
    public Ret batchLimitDrawing(@RequestBody @Validated BatchUserVo batchUserVo) {
//        userInfoService.batchLimitDrawing(batchUserVo);
        List<UserInfo> userInfos = batchUserVo.getUserInfos();
        HashMap<String, String> user = new HashMap<>();
        user.put("status","2");
        user.put("remark",batchUserVo.getValue());
        user.put("limitDrawing",batchUserVo.getFlg().toString());
        for (UserInfo userInfo : userInfos) {
            user.put("id",userInfo.getId().toString());
            userInfoService.updateUserByStatus(user);
//            sysLogService.addSysLog(getUsername(), userInfo.getId(), userInfo.getAccount(), "PC", SysLogEnum.USER_INFO_UPDATE_LIMIT_DRAWING);
        }
        return Rets.success();
    }

    @BussinessLog(value = "修改vip到期时间")
    @RequiresPermissions(value = "userInfoUpdate")
    @PostMapping("/updateVipDate")
    public Ret updateVipDate(@RequestBody UserInfoDto dto) {
        return userInfoService.updateVipDate(dto);
    }

    /**
     * 今日新增 /昨日新增 /冻结账号 /正常账号
     *
     * @return
     */
    @GetMapping(value = "/userStatistics")
    public Ret userStatistics() {
        String ucode = "";
        if (isProxy()) {
            ucode = getUcode();
        }
        return Rets.success(userInfoService.getUserStatistics(ucode));
    }


    /**
     * 免密登录接口
     */
    @GetMapping(value = "/secretFreeLogin")
    @BussinessLog(value = "免密登录")
    @RequiresPermissions(value = "secretFreeLogin")
    public Ret secretFreeLogin(@RequestParam(required = false) Long id) {
        return Rets.success(userInfoService.secretFreeLogin(id));
    }


    /**
     * 直扣直冲
     *
     * @return
     */
    @PostMapping(value = "/straightBuckle")
    @RequestLimit(count = 1, time = 1000)
    public Ret straightBuckle(@Valid @RequestBody UserInfoVo.StraightBuckleVo straightBuckle) {
        String key = "straightBuckle"+straightBuckle.getUid();
        boolean b = redisUtil.lock(key);
        if (b) {
            try {
                log.info("直扣直冲获取到锁,用户ID:{}", straightBuckle.getUid());
                return Rets.success(userInfoService.straightBuckle(straightBuckle, getUsername()));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                redisUtil.delete(key);
            }
        }
        log.error("直扣直冲未获取到锁,用户uid:{},时间:{}", straightBuckle.getUid(), DateUtil.getTime());
        return Rets.failure(MessageTemplateEnum.REQUEST_LIMIT.getCode(), MessageTemplateEnum.REQUEST_LIMIT);
    }

    /**
     * 批量直扣直冲
     *
     * @return
     */
    @PostMapping(value = "/straights")
    @RequestLimit(count = 1, time = 2000)
    @RequiresPermissions(value = "userStraightBuckles")
    public Ret straights(@Valid @RequestBody StraightAddVo vo) {
        return userInfoService.straights(vo,getUsername());
    }


    /**
     *  修改地址
     *  后台初始化地址
     * @return
     */
    @GetMapping(value = "/updateWalletAddr")
    @RequiresPermissions(value = "userInfoUpdate")
    public Ret updateWalletAddr(@RequestParam Long id, @RequestParam String value) {
        return userInfoService.InitWalletAddr(id, value);
    }


    /**
     * 修改用户邀请码
     *
     * @return
     */
    @GetMapping(value = "/updateInvitation")
    @RequiresPermissions(value = "userInfoUpdate")
    public Ret updateInvitation(@RequestParam Long id, @RequestParam String value) {
        return userInfoService.updateInvitation(id, value);
    }

    /**
     * 用户列表 下级数量
     * @param numberOfSubordinates 下级数量  （数量=多少）
     * @param registrationTimeStart 注册时间区间开始
     * @param registrationTimeEnd 注册时间区间结束
     * @param vipTypeSubordinate 下级VIP类型（多选）
     * @return
     */
    @GetMapping(value = "/getPartListBySubordinate")
    //@RequiresPermissions(value = "getPartListBySubordinate")
    public Ret getPartListBySubordinate(
            @RequestParam(required = false) String numberOfSubordinates,
            @RequestParam(required = false) String registrationTimeStart,
            @RequestParam(required = false) String registrationTimeEnd,
            @RequestParam(required = false) String vipTypeSubordinate) {
        HashMap<String, Object> where = new HashMap<>();
        Page<UserInfo> page = new PageFactory<UserInfo>().defaultPage();
        // 默认参数
        if (StringUtil.isEmpty(registrationTimeStart) && StringUtil.isEmpty(registrationTimeEnd)){
            LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
            registrationTimeStart = cn.hutool.core.date.DateUtil.format(oneMonthAgo,"yyyy-MM-dd");
            registrationTimeEnd =  cn.hutool.core.date.DateUtil.format(LocalDateTime.now(),"yyyy-MM-dd");
        }
        if (StringUtil.isEmpty(numberOfSubordinates)){
            numberOfSubordinates = "0";
        }
        if (StringUtil.isEmpty(vipTypeSubordinate)){
            vipTypeSubordinate="v0,v1";
        }


        if (isProxy()) {
            page.addFilter("user.source_invitation_code", getUcode());
        }
        page.addFilter(SearchFilter.build("user.fidw", SearchFilter.Operator.ISNULL));
        // 时间查询
        if (StringUtil.isNotEmpty(registrationTimeStart) && StringUtil.isNotEmpty(registrationTimeEnd)) {
            page.addFilter("user.create_time", SearchFilter.Operator.GTE, registrationTimeStart);
            page.addFilter("user.create_time", SearchFilter.Operator.LT, registrationTimeEnd);
        }
        page.setTotal(userInfoService.findCount(page));

        //下级数量 和 下级VIP类型
        String insql = "";
       if (StringUtil.isNotEmpty(vipTypeSubordinate)){
           for (String s : vipTypeSubordinate.split(",")) {
               insql += "'"+s+"',";
           }
           insql = insql.substring(0, insql.length() - 1);
       }
        if (StringUtil.isNotEmpty(numberOfSubordinates) && StringUtils.isNotEmpty(insql)){
            page.addFilter(SearchFilter.build("(\n" +
                    "SELECT count( 1 ) FROM t_dzuser_user WHERE " +
                    "fidw IS NULL  and vip_type IN ("+ insql+ ") "+
                    " and pinvitation_code != CONCAT('[',user.pinvitation_code,'],')  "+
                    "AND pinvitation_code LIKE CONCAT('%[', USER.pinvitation_code, ']%' ))", SearchFilter.Operator.EQ,numberOfSubordinates));
        }
        if (StringUtil.isNotEmpty(numberOfSubordinates) && StringUtils.isEmpty(insql)){
            page.addFilter(SearchFilter.build("(\n" +
                    "SELECT count( 1 ) FROM t_dzuser_user WHERE " +
                    "fidw IS NULL  "+
                    " and pinvitation_code != CONCAT('[',user.pinvitation_code,'],')  "+
                    "AND pinvitation_code LIKE CONCAT( '%[',USER.pinvitation_code, ']%' ))", SearchFilter.Operator.EQ,numberOfSubordinates));
        }


        where.put("vipTypes",insql);
        List<UserInfo> userInfoList = userInfoService.findUserPartInfoPage(page,where,true,false);
        List list = (List) new UserInfoWrapper(BeanUtil.objectsToMaps(userInfoList)).warp();
        page.setRecords(list);
        return Rets.success(page);
    }


    /**
     * 用户列表 盈利金额
     * @param profitAmount 盈利金额
     * @param registrationTimeStart 注册时间区间开始
     * @param registrationTimeEnd 注册时间区间结束
     * @return
     */
    @GetMapping(value = "/getPartListByMoney")
    //@RequiresPermissions(value = "getPartListByMoney")
    public Ret getPartListByMoney(
            @RequestParam(required = false) String profitAmount,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) String registrationTimeStart,
            @RequestParam(required = false) String registrationTimeEnd
    ) {
        HashMap<String, Object> where = new HashMap<>();
        Page<UserInfo> page = new PageFactory<UserInfo>().defaultPage();
        // 默认参数


        if (isProxy()) {
            page.addFilter("user.source_invitation_code", getUcode());
        }
        page.addFilter(SearchFilter.build("user.fidw", SearchFilter.Operator.ISNULL));
        // 时间查询
        if (StringUtil.isNotEmpty(registrationTimeStart) && StringUtil.isNotEmpty(registrationTimeEnd)) {
            page.addFilter("user.create_time", SearchFilter.Operator.GTE, registrationTimeStart);
            page.addFilter("user.create_time", SearchFilter.Operator.LT, registrationTimeEnd);
        }

        page.setTotal(userInfoService.findCount(page));
        SearchFilter.Operator Searoperation = SearchFilter.Operator.EQ;
        if (StringUtil.isNotEmpty(operation)){
            //eq相等   ne不相等，   gt大于， lt小于 gte 大于等于   lte 小于等于
            switch (operation){
                case "eq":
                    Searoperation = SearchFilter.Operator.EQ;
                    break;
                case "ne":
                    Searoperation = SearchFilter.Operator.NE;
                    break;
                case "gt":
                    Searoperation = SearchFilter.Operator.GT;
                    break;
                case "lt":
                    Searoperation = SearchFilter.Operator.LT;
                    break;
                case "gte":
                    Searoperation = SearchFilter.Operator.GTE;
                    break;
                case "lte":
                    Searoperation = SearchFilter.Operator.LTE;
                    break;
            }
        }

        page.addFilter(" IFNULL( w.wiMoney, 0 )- IFNULL( r.reMoney, 0 ) ", Searoperation, profitAmount);

        List<UserInfo> userInfoList = userInfoService.findUserPartInfoPage(page,where,false,true);
        List list = (List) new UserInfoWrapper(BeanUtil.objectsToMaps(userInfoList)).warp();
        page.setRecords(list);
        return Rets.success(page);
    }

    /*
    查询用户部分信息
    zx
     */
    @GetMapping(value = "/getPartList")
    @RequiresPermissions(value = "getPartList")
    public Ret getPartList(@RequestParam(required = false) String str, @RequestParam(required = false) Integer dzstatus,
                           @RequestParam(required = false) String vipType, @RequestParam(required = false) String userType,
                           @RequestParam(required = false) String registerIp, @RequestParam(required = false) String lastIp,
                           @RequestParam(required = false) String expireTimes, @RequestParam(required = false) String expireTimee,
                           @RequestParam(required = false) Integer levels, @RequestParam(required = false) String sourceInvitationCode,
                           @RequestParam(required = false) String walletAddress, @RequestParam(required = false) String flag,
                           @RequestParam(required = false) Integer levelsQuery, @RequestParam(required = false) String gmt,
                           @RequestParam(required = false) String orderField, @RequestParam(required = false) String orderName,
                           @RequestParam(required = false) String countryCodeNumber, @RequestParam(required = false) String limitStr,
                           @RequestParam(required = false) String lastIpCity, @RequestParam(required = false) String realName
    ) {


        //HttpUtil.getServerName();
        Page<UserInfo> page = new PageFactory<UserInfo>().defaultPage();
        //先判断是否代理商
        if (isProxy()) {
            page.addFilter("user.source_invitation_code", getUcode());
        }
//		else {
        //page.addFilter("user.source_invitation_code",sourceInvitationCode  );
//		}
        page.addFilter("user.vip_type",SearchFilter.Operator.OR, vipType);
        page.addFilter("user.user_type", userType);
        page.addFilter("user.country_code_number", countryCodeNumber);
        page.addFilter("user.real_name", SearchFilter.Operator.LIKE,realName);
        page.addFilter("user.last_ip_city", SearchFilter.Operator.LIKE, lastIpCity);

        page.addFilter("user.dzstatus", dzstatus);
        page.addFilter("user.last_ip", lastIp);
        page.addFilter("user.register_ip", registerIp);
        if (StringUtil.isNotEmpty(limitStr)) {
            page.addFilter("user." + limitStr, "1");
        }


        page.addFilter("ub1.wallet_address", SearchFilter.Operator.EQ, walletAddress);
        page.addFilter(SearchFilter.build("user.fidw", SearchFilter.Operator.ISNULL));

        if (StringUtil.isNotEmpty(expireTimes) && StringUtil.isNotEmpty(expireTimee)) {
            expireTimes = DateUtil.getTimeByZone(expireTimes);
            expireTimee = DateUtil.getTimeByZone(expireTimee);
            page.addFilter("user.create_time", SearchFilter.Operator.GTE, expireTimes);
            page.addFilter("user.create_time", SearchFilter.Operator.LT, expireTimee);
        }
        if (levels == null) {
            return Rets.failure("参数异常,请联系管理员");
        }
        if (levels.intValue() > 0) {
            if ("1".equals(flag)) { // 用户
                UserInfo userInfo = userInfoService.get(SearchFilter.build("invitationCode", str));
                if (userInfo == null) {
                    return Rets.success();
                }
                String pinvitationCode = userInfo.getPinvitationCode().replace("[" + userInfo.getInvitationCode() + "],", "");
                // 查询上级列表
                String[] arr = pinvitationCode.replace("[", "").replace("]", "").split(",", 0);
                page.addFilter("user.invitation_code", SearchFilter.Operator.IN, arr);
            } else { // 代理
                // 查询下级列表
                page.addFilter("user.pinvitation_code", SearchFilter.Operator.LIKE, "[" + str + "],");
                page.addFilter("user.levels", SearchFilter.Operator.GT, levels);
                if (levelsQuery != null) {
                    page.addFilter("user.levels", SearchFilter.Operator.EQ, levels + levelsQuery);
                }
            }
        } else {
            if (StringUtil.isNotEmpty(str)) {
                // 账号/邀请码/国家码
//                page.addFilter("user.account", SearchFilter.Operator.EQ, str);
                page.addFilter("user.account", SearchFilter.Operator.LIKE, str);
                page.addFilter("user.invitation_code", SearchFilter.Operator.LIKE, str,SearchFilter.Join.or);
            }
        }
        page.setTotal(userInfoService.findCount(page, sourceInvitationCode));

        String orderSql = "order by u.id desc";
        if (StringUtils.isNotBlank(orderField) && StringUtils.isNotBlank(orderName)) {
            if (orderField.equals("user_balance")) {
                if (orderName.equals("ascending")) {
                    orderSql = "order by u.userBalanceLeft asc,u.id desc";
                }
                if (orderName.equals("descending")) {
                    orderSql = "order by u.userBalanceLeft desc,u.id desc";
                }
            }
        }
        List<UserInfo> userInfoList = userInfoService.findUserPartInfoPage(page, sourceInvitationCode, orderSql);
        List list = (List) new UserInfoWrapper(BeanUtil.objectsToMaps(userInfoList)).warp();
        page.setRecords(list);
        return Rets.success(page);
    }

/*
查询用户信息通过account
 */
    @GetMapping(value = "/getUserInfoByAccount")
//    @RequiresPermissions(value = "getUserInfoByAccount")
    public Ret getUserInfoByAccount(Long id) {
        return Rets.success(userInfoService.findUserInfoByAccount(id));
    }

    /**
     * 导出v2  Excel    (easyexcel)
     *
     * @return
     */
    @GetMapping(value = "/userInfoExportV2")
    @RequiresPermissions(value = "userInfoExport")
    public void exportV2(HttpServletResponse response,@RequestParam(required = false) String str, @RequestParam(required = false) Integer dzstatus,
                         @RequestParam(required = false) String vipType, @RequestParam(required = false) String userType,
                         @RequestParam(required = false) String registerIp, @RequestParam(required = false) String lastIp,
                         @RequestParam(required = false) String expireTimes, @RequestParam(required = false) String expireTimee,
                         @RequestParam(required = false) Integer levels, @RequestParam(required = false) String sourceInvitationCode,
                         @RequestParam(required = false) String walletAddress, @RequestParam(required = false) String flag,
                         @RequestParam(required = false) Integer levelsQuery, @RequestParam(required = false) String gmt,
                         @RequestParam(required = false) String orderField, @RequestParam(required = false) String orderName,
                         @RequestParam(required = false) String countryCodeNumber, @RequestParam(required = false) String limitStr,
                         @RequestParam(required = false) String lastIpCity) {


        //HttpUtil.getServerName();
        Page<UserInfo> page = new PageFactory<UserInfo>().defaultPage();
        //先判断是否代理商
        if (isProxy()) {
            page.addFilter("user.source_invitation_code", getUcode());
        }
//		else {
        //page.addFilter("user.source_invitation_code",sourceInvitationCode  );
//		}
        page.addFilter("user.vip_type",SearchFilter.Operator.OR, vipType);
        page.addFilter("user.user_type", userType);
        page.addFilter("user.country_code_number", countryCodeNumber);
        page.addFilter("user.last_ip_city", SearchFilter.Operator.LIKE, lastIpCity);

        page.addFilter("user.dzstatus", dzstatus);
        page.addFilter("user.last_ip", lastIp);
        page.addFilter("user.register_ip", registerIp);
        if (StringUtil.isNotEmpty(limitStr)) {
            page.addFilter("user." + limitStr, "1");
        }


        page.addFilter("ub1.wallet_address", SearchFilter.Operator.EQ, walletAddress);
        page.addFilter(SearchFilter.build("user.fidw", SearchFilter.Operator.ISNULL));

        if (StringUtil.isNotEmpty(expireTimes) && StringUtil.isNotEmpty(expireTimee)) {
            expireTimes = DateUtil.getTimeByZone(expireTimes);
            expireTimee = DateUtil.getTimeByZone(expireTimee);
            page.addFilter("user.create_time", SearchFilter.Operator.GTE, expireTimes);
            page.addFilter("user.create_time", SearchFilter.Operator.LT, expireTimee);
        }
        if (levels == null) {
            throw new RuntimeException("参数异常,请联系管理员");
        }
        if (levels.intValue() > 0) {
            if ("1".equals(flag)) { // 用户
                UserInfo userInfo = userInfoService.get(SearchFilter.build("invitationCode", str));
                if (userInfo == null) {
                    return ;
                }
                String pinvitationCode = userInfo.getPinvitationCode().replace("[" + userInfo.getInvitationCode() + "],", "");
                // 查询上级列表
                String[] arr = pinvitationCode.replace("[", "").replace("]", "").split(",", 0);
                page.addFilter("user.invitation_code", SearchFilter.Operator.IN, arr);
            } else { // 代理
                // 查询下级列表
                page.addFilter("user.pinvitation_code", SearchFilter.Operator.LIKE, "[" + str + "],");
                page.addFilter("user.levels", SearchFilter.Operator.GT, levels);
                if (levelsQuery != null) {
                    page.addFilter("user.levels", SearchFilter.Operator.EQ, levels + levelsQuery);
                }
            }
        } else {
            if (StringUtil.isNotEmpty(str)) {
                // 账号/邀请码/国家码
//                page.addFilter("user.account", SearchFilter.Operator.EQ, str);
                page.addFilter("user.account", SearchFilter.Operator.LIKE, str);
                page.addFilter("user.invitation_code", SearchFilter.Operator.LIKE, str,SearchFilter.Join.or);
            }
        }
        page.setTotal(userInfoService.findCount(page, sourceInvitationCode));

        String orderSql = "order by u.id desc";
        if (StringUtils.isNotBlank(orderField) && StringUtils.isNotBlank(orderName)) {
            if (orderField.equals("user_balance")) {
                if (orderName.equals("ascending")) {
                    orderSql = "order by u.userBalanceLeft asc,u.id desc";
                }
                if (orderName.equals("descending")) {
                    orderSql = "order by u.userBalanceLeft desc,u.id desc";
                }
            }
        }
        List<UserInfo> userInfoList = userInfoService.findUserInfoExportPage(page, sourceInvitationCode, orderSql);
        if (ObjectUtil.isEmpty(page)||CollUtil.isEmpty(userInfoList)){
            throw new RuntimeException("查询为空");
        }
        userInfoService.userInfoExportV2(response,userInfoList,levels);
    }


    /**
     * status 等于什么就修改什么
     * 限制购买     1
     * 限制提款     2
     * 限制收益     3
     * 限制邀请码   4
     * 启用/冻结    5
     * @param
     * @return
     */
    @PostMapping(value = "/updateUserByStatus")
    @BussinessLog(value = "更新用户信息", key = "name")
    @RequiresPermissions(value = "userInfoUpdate")
    public Ret updateUserByStatus(@RequestBody HashMap userInfo) {
        return userInfoService.updateUserByStatus(userInfo);
    }

}

