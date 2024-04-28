package cn.rh.flash.api.controller.dz.dzuser;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.api.controller.frontapi.ContentApi;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzuser.WithdrawalsRecord;
import cn.rh.flash.bean.entity.system.User;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.dz.WithdrawalsRecordsVo;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.cache.impl.MyRedissonLocker;
import cn.rh.flash.config.chinesePattern.ChinesePattern;
import cn.rh.flash.service.coom.dz.RecordInformation;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.dzuser.UserInfoService;
import cn.rh.flash.service.dzuser.WithdrawalsRecordService;
import cn.rh.flash.service.system.FileService;
import cn.rh.flash.service.system.UserService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.StringUtil;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.WithdrawalsRecordWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/dzuser/withdrawals")
public class WithdrawalsRecordController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private WithdrawalsRecordService withdrawalsRecordService;

    @Autowired
    private RecordInformation recordInformation;
    @Autowired
    private FileService fileService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserService userService;

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private ConfigCache configCache;

    @Autowired
    private ContentApi contentApi;
//
//    @Autowired
//    private RedisUtil redisUtil;

    @Autowired
    private MyRedissonLocker myRedissonLocker;

    @GetMapping(value = "/list")
    @RequiresPermissions(value = {"withdrawalsRecord1", "withdrawalsRecord", "withdrawalsRecord2", "withdrawalsRecord3"}, logical = Logical.OR)

    public Ret list(@RequestParam(required = false) String sourceInvitationCode, @RequestParam(required = false) String orderNumber,
                    @RequestParam(required = false) String account, @RequestParam(required = false) String rechargeStatus,
                    @RequestParam(required = false) String channelName, @RequestParam(required = false) Integer flg,
                    @RequestParam(required = false) String expireTimes, @RequestParam(required = false) String expireTimee,
                    @RequestParam(required = false) String minnumber, @RequestParam(required = false) String maxnumber,
                    @RequestParam(required = false) String vipType, @RequestParam(required = false) String gmt,
                    @RequestParam(required = false) String orderField, @RequestParam(required = false) String orderName,
                    @RequestParam(required = false) String withdrawalAddress, @RequestParam(required = false) String transactionNumber,
                    @RequestParam(required = false) String channelType,@RequestParam(required = false) String countryCodeNumber) {

        Page<WithdrawalsRecord> page = new PageFactory<WithdrawalsRecord>().defaultPage();


        /* 2022年12月21日 */
        if (isProxy()) {
            page.addFilter("sourceInvitationCode", getUcode());
        } else if (StringUtil.isNotEmpty(sourceInvitationCode)) {
//            UserInfo userInfo = userInfoService.get(SearchFilter.build("account", sourceInvitationCode));
//            if (userInfo != null) {
//                page.addFilter("sourceInvitationCode", SearchFilter.Operator.LIKE, userInfo.getSourceInvitationCode());
//            } else {
//                return Rets.success(page);
//            }
            User user = userService.get(SearchFilter.build("account", sourceInvitationCode));
            if (user != null) {
                page.addFilter("sourceInvitationCode", SearchFilter.Operator.LIKE, user.getUcode());
            } else {
                return Rets.failure("未找到此代理账号");
            }
        }
//        if (StringUtil.isNotEmpty(sourceInvitationCode)) {
//            UserInfo userInfo = userInfoService.get(SearchFilter.build("account", sourceInvitationCode));
//            if (userInfo != null) {
//                page.addFilter("sourceInvitationCode", SearchFilter.Operator.LIKE, userInfo.getSourceInvitationCode());
//            }
//        }
        // end

        String testCode = configCache.get(ConfigKeyEnum.TEST_USER_CODE).trim();
        page.addFilter(SearchFilter.build("sourceInvitationCode", SearchFilter.Operator.NE, testCode));

        page.addFilter(SearchFilter.build("fidw", SearchFilter.Operator.ISNULL));
        page.addFilter("orderNumber", orderNumber);
        page.addFilter("account", account);
        page.addFilter("rechargeStatus", rechargeStatus);
        page.addFilter("channelName", channelName);
        page.addFilter("channelType", channelType);
        page.addFilter("transactionNumber", transactionNumber);
        page.addFilter("withdrawalAddress", withdrawalAddress);


        if (StringUtil.isNotEmpty(minnumber)) {
            page.addFilter("money", SearchFilter.Operator.GTE, minnumber);
        }
        if (StringUtil.isNotEmpty(maxnumber)) {
            page.addFilter("money", SearchFilter.Operator.LTE, maxnumber);
        }

        // vip等级
        page.addFilter("userInfo.vipType", vipType);
        page.addFilter("userInfo.countryCodeNumber",countryCodeNumber);

        if (flg != null && flg == 2) {
            page.addFilter("rechargeStatus", SearchFilter.Operator.NE, "no", SearchFilter.Join.and);
        }

//		if( StringUtil.isNotEmpty( expireTimes ) && StringUtil.isNotEmpty( expireTimee ) ){
//			page.addFilter("createTime", SearchFilter.Operator.BETWEEN, Arrays.asList( DateUtil.parseTime( expireTimes ),DateUtil.parseTime( expireTimee ) ) );
//		}
        if (StringUtil.isNotEmpty(expireTimes) && StringUtil.isNotEmpty(expireTimee)) {

            // 转换时区
            //gmt = StringUtil.isEmpty( gmt ) ? TimeZone.getDefault().getID() : gmt;
            expireTimes = DateUtil.getTimeByZone(expireTimes);
            expireTimee = DateUtil.getTimeByZone(expireTimee);

//            logger.info("----------------");
//            logger.info(expireTimes);logger.info(expireTimee);
//            logger.info("----------------");

            page.addFilter("modifyTime", SearchFilter.Operator.GTE, DateUtil.parseTime(expireTimes));
            page.addFilter("modifyTime", SearchFilter.Operator.LT, DateUtil.parseTime(expireTimee));
        }

//        Sort sort = Sort.by(Sort.Direction.DESC,"modifyTime");
//        page.setSort(sort);

        List<Sort.Order> orders = new ArrayList<>();
        if (StringUtils.isNotBlank(orderField) && StringUtils.isNotBlank(orderName)) {
            if (orderName.equals("ascending")) {
                orders.add(new Sort.Order(Sort.Direction.ASC, orderField));
            }
            if (orderName.equals("descending")) {
                orders.add(new Sort.Order(Sort.Direction.DESC, orderField));
            }
        }
        orders.add(new Sort.Order(Sort.Direction.DESC, "modifyTime"));
        page.setSort(Sort.by(orders));

        page = withdrawalsRecordService.queryPage(page);

        if (flg == 1) {
            // 提现审核时 查看用户已成功提现次数

            Set<Long> uids = page.getRecords().stream().map(WithdrawalsRecord::getUid).collect(Collectors.toSet());

            List<SearchFilter> filters = new ArrayList<>();
            filters.add(SearchFilter.build("uid", SearchFilter.Operator.IN, uids));
            filters.add(SearchFilter.build("rechargeStatus", SearchFilter.Operator.IN,new String[]{"suc","sysok"}));

            List<WithdrawalsRecord> withdrawalsRecords = withdrawalsRecordService.queryAll(filters);

            Map<Long, Long> map = withdrawalsRecords.stream().collect(Collectors.groupingBy(WithdrawalsRecord::getUid, Collectors.counting()));

            for (WithdrawalsRecord record : page.getRecords()) {
                record.setWithNum(map.get(record.getUid()) == null ? 0 : map.get(record.getUid()));
            }

        }

        List list = (List) new WithdrawalsRecordWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
        page.setRecords(list);
        return Rets.success(page);
    }

    @PostMapping
    @BussinessLog(value = "新增提现记录", key = "name")
    @RequiresPermissions(value = "withdrawalsRecordAdd")
    public Ret add(@RequestBody WithdrawalsRecord withdrawalsRecord) {

        withdrawalsRecord.setSourceInvitationCode(getUcode());
        withdrawalsRecord.setIdw(new IdWorker().nextId() + "");
        withdrawalsRecordService.insert(withdrawalsRecord);
        sysLogService.addSysLog(getUsername(), withdrawalsRecord.getId(), withdrawalsRecord.getAccount(), "PC", SysLogEnum.ADD_WITHDRAWALS_RECORD_INFO);
        return Rets.success();
    }

    @PutMapping
    @BussinessLog(value = "更新提现记录", key = "name")
    @RequiresPermissions(value = {"withdrawalsRecordUpdate", "withdrawalsRecordUpdate2"})
    public Ret update(@RequestBody @Validated(ChinesePattern.OnUpdate.class) WithdrawalsRecord withdrawalsRecord) {

        withdrawalsRecordService.update(withdrawalsRecord);
        sysLogService.addSysLog(getUsername(), withdrawalsRecord.getId(), withdrawalsRecord.getAccount(), "PC", SysLogEnum.UPDATE_WITHDRAWALS_RECORD_INFO);
        return Rets.success();
    }


    @DeleteMapping
    @BussinessLog(value = "删除提现记录", key = "id")
    @RequiresPermissions(value = "withdrawalsRecordDelete")
    public Ret remove(Long id) {
        if (id == null) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        withdrawalsRecordService.delete(id);
        sysLogService.addSysLog(getUsername(), id, "", "PC", SysLogEnum.DELETE_WITHDRAWALS_RECORD_INFO);
        return Rets.success();
    }

    /* 提现通过 */
    @PostMapping(value = "/updateAudit")
    @RequiresPermissions(value = "withdrawalsRecordUpdate")
    public Ret updateAudit(@RequestBody @Valid List<WithdrawalsRecord> withdrawalsRecordList) {
        // 加锁防止重复调用
        String key = "withdrawalsRecord";
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.lock(lock);
        if (b) {
            try {
                logger.info("提现通过获取到锁,用户ID:{}", contentApi.getAccount());
                withdrawalsRecordService.modifyStatus(withdrawalsRecordList, "ok", "", getUsername());
                return Rets.success();
            } catch (Exception e) {
                e.printStackTrace();
                return Rets.failure(e.getMessage());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        logger.error("提现通过没有获取到锁,用户ID:{},时间:{}", contentApi.getAccount(), DateUtil.getTime());
        return Rets.failure("其他操作员正在操作审核功能,请稍后再试");
    }


    /* 提现拒绝 */
    @PostMapping(value = "/updateAuditNo")
    @RequiresPermissions(value = "withdrawalsRecordUpdate")
    public Ret updateAuditNo(@RequestBody @Valid WithdrawalsRecordsVo withdrawalsRecordsVo) {

        // 加锁防止重复调用
        String key = "withdrawalsRecord";
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.lock(lock);
        if (b) {
            try {
                logger.info("提现拒绝获取到锁,用户ID:{}", contentApi.getAccount());
                withdrawalsRecordService.modifyStatus(withdrawalsRecordsVo.getWithdrawalsRecordList(), "er", withdrawalsRecordsVo.getValue(), getUsername());
                return Rets.success();
            } catch (Exception e) {
                e.printStackTrace();
                return Rets.failure(e.getMessage());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        logger.error("提现拒绝没有获取到锁,用户ID:{},时间:{}", contentApi.getAccount(), DateUtil.getTime());
        return Rets.failure("其他操作员正在操作审核功能,请稍后再试");
    }


    /* 驳回 */
    @PostMapping(value = "/updateAuditReject")
    @RequiresPermissions(value = "updateAuditReject")
    public Ret updateAuditReject(@RequestBody @Valid WithdrawalsRecordsVo withdrawalsRecordsVo) {
        // 加锁防止重复调用
        String key = "updateAuditReject";
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.lock(lock);
        if (b) {
            try {
                logger.info("提现拒绝获取到锁,用户ID:{}", contentApi.getAccount());
                withdrawalsRecordService.updateAuditReject(withdrawalsRecordsVo.getWithdrawalsRecordList(), getUsername());
                return Rets.success();
            } catch (Exception e) {
                e.printStackTrace();
                return Rets.failure(e.getMessage());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        logger.error("驳回没有获取到锁,用户ID:{},时间:{}", contentApi.getAccount(), DateUtil.getTime());
        return Rets.failure("其他操作员正在操作驳回功能,请稍后再试");
    }




    /* 手动完成 订单通过不出款 */
    @PostMapping(value = "/updateManual")
    @RequiresPermissions(value ="withdrawalsRecordUpdateManual")
    public Ret updateManual(@RequestBody @Valid List<WithdrawalsRecord> withdrawalsRecordList) {

        // 加锁防止重复调用
        String key = "withdrawalsRecord";
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.lock(lock);
        if (b) {
            try {
                logger.info("提现拒绝获取到锁,用户ID:{}", contentApi.getAccount());
                withdrawalsRecordService.modifyStatus(withdrawalsRecordList, "sysok", "", getUsername());
                return Rets.success();
            } catch (Exception e) {
                e.printStackTrace();
                return Rets.failure(e.getMessage());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        logger.error("提现拒绝没有获取到锁,用户ID:{},时间:{}", contentApi.getAccount(), DateUtil.getTime());
        return Rets.failure("其他操作员正在操作审核功能,请稍后再试");
    }


    /* 手动修改已完成状态（ recharge_status ！= no） */
    @PostMapping(value = "/updateStatus")
    @RequiresPermissions(value = "withdrawalsRecordUpdate")
    public Ret updateStatus(@RequestBody @Valid WithdrawalsRecord record) {

        WithdrawalsRecord withdrawalsRecord = withdrawalsRecordService.get(record.getId());
        if (!"no".equals(withdrawalsRecord.getRechargeStatus())&&!"ok".equals(withdrawalsRecord.getRechargeStatus())){
            return Rets.failure("只有 已审核 和 审核中 是可以操作标记成功！");
        }
        withdrawalsRecord.setRechargeStatus("suc");
        withdrawalsRecordService.update(withdrawalsRecord);
        sysLogService.addSysLog(getUsername(), withdrawalsRecord.getId(), withdrawalsRecord.getAccount(), "PC", SysLogEnum.UPDATE_STATUS_WITHDRAWALS_RECORD_SYSOK);
        return Rets.success();
    }




//    /**
//     * 修改状态
//     *
//     * @param withdrawalsRecordList
//     * @param okorer
//     */
//    public void modifyStatus(@RequestBody @Valid List<WithdrawalsRecord> withdrawalsRecordList, String okorer, String reason ) {
//        if (withdrawalsRecordList.size() == 0) {
//            throw new ApplicationException(BizExceptionEnum.MODIFY_INFORMATION_EMPTY);
//        }
//        int i = 0;
//        for (WithdrawalsRecord withdrawalsRecord : withdrawalsRecordList) {
//            withdrawalsRecord.setOperator(getUsername());
//            if (!StringUtil.equals("no", withdrawalsRecord.getRechargeStatus())) {
//                continue;
//            }
//            i++;
//            withdrawalsRecord.setRechargeStatus(okorer);
//            // 系统日志记录
//            SysLogEnum operation = SysLogEnum.UPDATE_STATUS_WITHDRAWALS_RECORD_OK;
//            if ("ok".equals(okorer)) {
//
//                // 2023年1月30日  jk 冻结账号不能审核通过
//                String account = withdrawalsRecord.getAccount();
//                //UserInfo userInfo = userInfoService.get(SearchFilter.build("id", uid));
//                UserInfo userInfo = userInfoService.get(SearchFilter.build("account", account ) );
//                if (userInfo != null) {
//                    //   1启用    2停用   3 已删除
//                    if(  userInfo.getDzstatus() != null && userInfo.getDzstatus() != 1 ){
//                        BizExceptionEnum accountFreezed = BizExceptionEnum.ACCOUNT_FREEZED;
//                        accountFreezed.setMessage( accountFreezed.getMessage()+"[账号:"+ userInfo.getAccount()+"]" );
//                        throw new ApplicationException(accountFreezed);
//                    }
//                }else{
//                    throw new ApplicationException(BizExceptionEnum.USER_NOT_EXISTED);
//                }
//                // end
//
//
//                Double subtract = BigDecimalUtils.subtract(withdrawalsRecord.getMoney(), withdrawalsRecord.getHandlingFee());
//                WithdrawOrderVo vo = recordInformation.createWithdrawOrderThird(withdrawalsRecord.getUid().toString(), subtract.toString(), withdrawalsRecord.getWithdrawalAddress(), withdrawalsRecord.getOrderNumber(), withdrawalsRecord.getChannelName());
//                //以下操作一般情况下会放置在管理端
//                if (vo != null) {
//                    withdrawalsRecord.setTransactionNumber(vo.getTransactionNumber());
//                    //todo 此处为订单状态 而不是审核状态
//                    //审核状态 ok:已审核,no:未审核,er:已拒绝 suc:已成功 exit:已退款
////                    withdrawalsRecord.setHandlingFee(Double.parseDouble(vo.getFee()));
////                    withdrawalsRecord.setAmountReceived(Double.parseDouble(vo.getAmount()) - Double.parseDouble(vo.getFee()));
//                    withdrawalsRecordService.update(withdrawalsRecord);
//                }else {
//                    throw new ApplicationException(BizExceptionEnum.PAY_SERVER_ERROR);
//                }
//            } else if ("sysok".equals(okorer)) {
//                withdrawalsRecord.setTransactionNumber("");
//                //todo 此处为订单状态 而不是审核状态
//                //审核状态 ok:已审核,no:未审核,er:已拒绝 suc:已成功 exit:已退款  sysok：订单通过不出款
//                withdrawalsRecordService.update(withdrawalsRecord);
//                operation = SysLogEnum.UPDATE_STATUS_WITHDRAWALS_RECORD_SYSOK;
//            } else if ("er".equals(okorer)) {
////                if (StringUtil.isEmpty(reason)) {
////                    throw new ApplicationException(BizExceptionEnum.REFUSE_REASON_EMPTY);
////                }
//                withdrawalsRecord.setRemark(reason);
//                //拒绝提现退款
//                recordInformation.refuseWithdrawalRecord(withdrawalsRecord);
//                withdrawalsRecordService.update(withdrawalsRecord);
//                operation = SysLogEnum.UPDATE_STATUS_WITHDRAWALS_RECORD_ER;
//            }
//            sysLogService.addSysLog(getUsername(),withdrawalsRecord.getId(),withdrawalsRecord.getAccount(),"PC", operation);
//        }
//        if (i == 0) {
//            throw new ApplicationException(BizExceptionEnum.MODIFY_INFORMATION_EMPTY);
//        }
//    }


    @GetMapping(value = "/withdrawalsExportV2")
//	@RequiresPermissions(value = "withdrawalsExport") TODO 权限
    public void exportV2(HttpServletResponse response,
                         @RequestParam(required = false) String sourceInvitationCode, @RequestParam(required = false) String orderNumber,
                         @RequestParam(required = false) String account, @RequestParam(required = false) String rechargeStatus,
                         @RequestParam(required = false) String channelName, @RequestParam(required = false) Integer flg,
                         @RequestParam(required = false) String expireTimes, @RequestParam(required = false) String expireTimee,
                         @RequestParam(required = false) String withdrawalAddress, @RequestParam(required = false) String transactionNumber,
                         @RequestParam(required = false) String vipType, @RequestParam(required = false) String gmt,
                         @RequestParam(required = false) String minnumber, @RequestParam(required = false) String maxnumber,
                         @RequestParam(required = false) String channelType,@RequestParam(required = false) String countryCodeNumber
    ) {

        Page<WithdrawalsRecord> page = new PageFactory<WithdrawalsRecord>().defaultPage();

        if (isProxy()) {
            page.addFilter("sourceInvitationCode", getUcode());
        } else if (StringUtil.isNotEmpty(sourceInvitationCode)) {
            User user = userService.get(SearchFilter.build("account", sourceInvitationCode));
            if (user != null) {
                page.addFilter("sourceInvitationCode", SearchFilter.Operator.LIKE, user.getUcode());
            } else {
                throw  new RuntimeException("未找到此代理账号");
            }
        }
        String testCode = configCache.get(ConfigKeyEnum.TEST_USER_CODE).trim();
        page.addFilter(SearchFilter.build("sourceInvitationCode", SearchFilter.Operator.NE, testCode));
        page.addFilter(SearchFilter.build("fidw", SearchFilter.Operator.ISNULL));
        page.addFilter("orderNumber", orderNumber);
        page.addFilter("account", account);
        page.addFilter("rechargeStatus", rechargeStatus);
        page.addFilter("withdrawalAddress", withdrawalAddress);
        page.addFilter("transactionNumber", transactionNumber);
        page.addFilter("channelName", channelName);
        page.addFilter("channelType", channelType);
        if (StringUtil.isNotEmpty(minnumber)) {
            page.addFilter("money", SearchFilter.Operator.GTE, minnumber);
        }
        if (StringUtil.isNotEmpty(maxnumber)) {
            page.addFilter("money", SearchFilter.Operator.LTE, maxnumber);
        }
        page.addFilter("userInfo.vipType", vipType);
        page.addFilter("userInfo.countryCodeNumber",countryCodeNumber);

        String str = "提现审核";
        if (flg != null && flg == 2) {
            page.addFilter("rechargeStatus", SearchFilter.Operator.NE, "no", SearchFilter.Join.and);
            str = "提现记录";
        }

        if (StringUtil.isNotEmpty(expireTimes) && StringUtil.isNotEmpty(expireTimee)) {
            expireTimes = DateUtil.getTimeByZone(expireTimes);
            expireTimee = DateUtil.getTimeByZone(expireTimee);
            page.addFilter("modifyTime", SearchFilter.Operator.GTE, DateUtil.parseTime(expireTimes));
            page.addFilter("modifyTime", SearchFilter.Operator.LT, DateUtil.parseTime(expireTimee));
        }
        page = withdrawalsRecordService.queryPage(page);
        List<Map<String,Object>> list = (List) new WithdrawalsRecordWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
        if (ObjUtil.isEmpty(page) || CollUtil.isEmpty(list)){
            throw new RuntimeException("查询为空");
        }
        withdrawalsRecordService.exportV2(response,list);

    }


    /**
     * @Description: 统计总手续费 总金额
     * @Param:
     * @return:
     * @Author: Skj
     */
    @GetMapping(value = "/findCountMoney")
    public Ret findCountMoney(@RequestParam(required = false) String sourceInvitationCode, @RequestParam(required = false) String orderNumber,
                              @RequestParam(required = false) String account, @RequestParam(required = false) String rechargeStatus,
                              @RequestParam(required = false) String channelName, @RequestParam(required = false) Integer flg,
                              @RequestParam(required = false) String expireTimes, @RequestParam(required = false) String expireTimee,
                              @RequestParam(required = false) String minnumber, @RequestParam(required = false) String maxnumber,
                              @RequestParam(required = false) String withdrawalAddress, @RequestParam(required = false) String transactionNumber,
                              @RequestParam(required = false) String vipType,@RequestParam(required = false) String channelType,
                              @RequestParam(required = false) String countryCodeNumber
                              ) {
        Page<WithdrawalsRecord> page = new PageFactory<WithdrawalsRecord>().defaultPage();

//        if (StringUtil.isNotEmpty(sourceInvitationCode)) {
//            User user = userService.get(SearchFilter.build("account", sourceInvitationCode));
//            if (user != null) {
//                page.addFilter("a.source_invitation_code", SearchFilter.Operator.LIKE, user.getUcode());
//            } else {
//                return Rets.success(page);
//            }
//        }
        if (isProxy()) {
            page.addFilter("a.source_invitation_code", getUcode());
        } else if (StringUtil.isNotEmpty(sourceInvitationCode)) {
//            UserInfo userInfo = userInfoService.get(SearchFilter.build("account", sourceInvitationCode));
//            if (userInfo != null) {
//                page.addFilter("a.source_invitation_code", SearchFilter.Operator.LIKE, userInfo.getSourceInvitationCode());
//            } else {
//                return Rets.success(page);
//            }
            User user = userService.get(SearchFilter.build("account", sourceInvitationCode));
            if (user != null) {
                page.addFilter("a.source_invitation_code", SearchFilter.Operator.LIKE, user.getUcode());
            } else {
                return Rets.failure("未找到此代理账号");
            }
        }

        //        page.addFilter(SearchFilter.build("a.fidw", SearchFilter.Operator.ISNULL));
        String testCode = configCache.get(ConfigKeyEnum.TEST_USER_CODE).trim();
        page.addFilter(SearchFilter.build("a.source_invitation_code", SearchFilter.Operator.NE, testCode));
        page.addFilter("a.channel_name", channelName);
        page.addFilter("a.recharge_status", rechargeStatus);
        page.addFilter("a.transaction_number", transactionNumber);
        page.addFilter("a.withdrawal_address", withdrawalAddress);
        page.addFilter("a.order_number", orderNumber);
        page.addFilter("a.account", account);
//        page.addFilter("recharge_status", rechargeStatus);
        page.addFilter("b.vip_type", vipType);
        page.addFilter("b.country_code_number", countryCodeNumber);
        page.addFilter("a.channel_type", channelType);
        if (StringUtil.isNotEmpty(minnumber)) {
            page.addFilter("a.money", SearchFilter.Operator.GTE, minnumber);
        }
        if (StringUtil.isNotEmpty(maxnumber)) {
            page.addFilter("a.money", SearchFilter.Operator.LTE, maxnumber);
        }

        if (flg != null && flg == 2) {
            page.addFilter("a.recharge_status", SearchFilter.Operator.NE, "no", SearchFilter.Join.and);
        }
        if (StringUtil.isNotEmpty(expireTimes) && StringUtil.isNotEmpty(expireTimee)) {
            expireTimes = DateUtil.getTimeByZone(expireTimes);
            expireTimee = DateUtil.getTimeByZone(expireTimee);

            page.addFilter("a.modify_time", SearchFilter.Operator.GTE, expireTimes);
            page.addFilter("a.modify_time", SearchFilter.Operator.LT, expireTimee);
        }
        return withdrawalsRecordService.findCountMoney(page);
    }

}