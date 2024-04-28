package cn.rh.flash.api.controller.dz.dzuser;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.api.interceptor.Requestxz.RequestLimit;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzuser.RechargeRecord;
import cn.rh.flash.bean.entity.system.User;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.dzuser.RechargeRecordService;
import cn.rh.flash.service.dzuser.UserInfoService;
import cn.rh.flash.service.system.FileService;
import cn.rh.flash.service.system.UserService;
import cn.rh.flash.utils.*;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.RechargeRecordWrapper;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


@RestController
@Log4j2
@RequestMapping("/dzuser/rechargehistory")
public class RechargeRecordController extends BaseController {
    private Logger logger = LoggerFactory.getLogger( getClass() );
    @Autowired
    private RechargeRecordService rechargeRecordService;
    @Autowired
    private FileService fileService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserService userService;

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping(value = "/list")
    @RequiresPermissions(value = "rechargeRecord")
    public Ret list(@RequestParam(required = false) Integer rechargeStatus, @RequestParam(required = false) String channelName,
                    @RequestParam(required = false) String account, @RequestParam(required = false) String orderNumber,
                    @RequestParam(required = false) String expireTimes, @RequestParam(required = false) String expireTimee,
                    @RequestParam(required = false) String sourceInvitationCode,@RequestParam(required = false) String vipType,
                    @RequestParam(required = false) String gmt,@RequestParam(required = false) String withdrawalAddress,
                    @RequestParam(required = false) String firstCharge,@RequestParam(required = false) String channelType,
                    @RequestParam(required = false) String countryCodeNumber
    ) {
        Page<RechargeRecord> page = new PageFactory<RechargeRecord>().defaultPage();
        if (isProxy()){
            page.addFilter("sourceInvitationCode",getUcode());
        }else if( StringUtil.isNotEmpty( sourceInvitationCode ) ){
//            UserInfo userInfo = userInfoService.get( SearchFilter.build( "account", sourceInvitationCode ) );
//            if (userInfo != null) {
//                page.addFilter("sourceInvitationCode", SearchFilter.Operator.LIKE,  userInfo.getSourceInvitationCode() );
//            }else {
//                return Rets.success(page);
//            }
            User user = userService.get(SearchFilter.build("account", sourceInvitationCode));
            if (user != null) {
                page.addFilter("sourceInvitationCode", SearchFilter.Operator.LIKE, user.getUcode());
            } else {
                return Rets.failure("未找到此代理账号");
            }
        }
        page.addFilter(SearchFilter.build("fidw", SearchFilter.Operator.ISNULL));
        page.addFilter( "rechargeStatus", rechargeStatus );
        page.addFilter( "channelName", channelName );
        page.addFilter( "account", account );
        page.addFilter( "orderNumber", orderNumber );
        page.addFilter( "withdrawalAddress", withdrawalAddress );
        page.addFilter( "firstCharge", firstCharge );
        page.addFilter( "channelType", channelType );

//        page.addFilter("sourceInvitationCode",sourceInvitationCode  );

//        if( StringUtil.isNotEmpty( sourceInvitationCode ) ){
//            UserInfo userInfo = userInfoService.get( SearchFilter.build( "account", sourceInvitationCode ) );
//            if (userInfo != null) {
//                page.addFilter("sourceInvitationCode", SearchFilter.Operator.LIKE,  userInfo.getSourceInvitationCode() );
//            }
//        }


        // vip等级
        page.addFilter("userInfo.vipType",vipType);
        page.addFilter("userInfo.countryCodeNumber",countryCodeNumber);

//		if( StringUtil.isNotEmpty( expireTimes ) && StringUtil.isNotEmpty( expireTimee ) ){
//			page.addFilter("createTime", SearchFilter.Operator.BETWEEN, Arrays.asList( DateUtil.parseTime( expireTimes ),DateUtil.parseTime( expireTimee ) ) );
//		}
        if (StringUtil.isNotEmpty( expireTimes ) && StringUtil.isNotEmpty( expireTimee )) {
            //gmt = StringUtil.isEmpty( gmt ) ? TimeZone.getDefault().getID() : gmt;
            expireTimes = DateUtil.getTimeByZone(expireTimes);
            expireTimee = DateUtil.getTimeByZone(expireTimee);
            page.addFilter( "modifyTime", SearchFilter.Operator.GTE, DateUtil.parseTime( expireTimes ) );
            page.addFilter( "modifyTime", SearchFilter.Operator.LT, DateUtil.parseTime( expireTimee ) );
        }
        page = rechargeRecordService.queryPage( page );

        List list = (List) new RechargeRecordWrapper( BeanUtil.objectsToMaps( page.getRecords() ) ).warp();
        page.setRecords( list );
        return Rets.success( page );
    }

    @PostMapping
    @BussinessLog(value = "新增充值记录", key = "name")
    @RequiresPermissions(value = "rechargeRecordAdd")
    public Ret add(@RequestBody RechargeRecord rechargeRecord) {

        rechargeRecord.setSourceInvitationCode( getUcode() );
        rechargeRecord.setIdw( new IdWorker().nextId() + "" );
        rechargeRecordService.insert( rechargeRecord );
        sysLogService.addSysLog(getUsername(),rechargeRecord.getId(),rechargeRecord.getAccount(),"PC", SysLogEnum.ADD_RECHARGE_RECORD_INFO);
        return Rets.success();
    }

    @PutMapping
    @BussinessLog(value = "更新充值记录", key = "name")
    @RequiresPermissions(value = "rechargeRecordUpdate")
    public Ret update(@RequestBody RechargeRecord rechargeRecord) {
        rechargeRecordService.update( rechargeRecord );
        sysLogService.addSysLog(getUsername(),rechargeRecord.getId(),rechargeRecord.getAccount(),"PC", SysLogEnum.UPDATE_RECHARGE_RECORD_INFO);
        return Rets.success();
    }

    @PutMapping("examine")
    @BussinessLog(value = "审核充值记录银行卡", key = "name")
    @RequestLimit(count = 1, time = 1000)
    @RequiresPermissions(value = "rechargeRecordExamine")
    public Ret examine(@RequestBody  RechargeRecord rechargeRecord) {
        String key = "rechargeRecordExamine"+rechargeRecord.getAccount();
        boolean b = redisUtil.lock(key);
        if (b) {
            try {
                log.info("审核充值记录银行卡获取到锁,用户账号:{}", rechargeRecord.getAccount());
                return rechargeRecordService.examine(rechargeRecord,isProxy(),getUcode());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                redisUtil.delete(key);
            }
        }
        log.error("审核充值记录银行卡未获取到锁,用户账号:{},时间:{}", rechargeRecord.getAccount(), DateUtil.getTime());
        return Rets.failure(MessageTemplateEnum.REQUEST_LIMIT.getCode(), MessageTemplateEnum.REQUEST_LIMIT);
    }


    @DeleteMapping
    @BussinessLog(value = "删除充值记录", key = "id")
    @RequiresPermissions(value = "rechargeRecordDelete")
    public Ret remove(Long id) {
        if (id == null) {
            throw new ApplicationException( BizExceptionEnum.REQUEST_NULL );
        }
        rechargeRecordService.delete( id );
        sysLogService.addSysLog(getUsername(),id,"","PC", SysLogEnum.DELETE_RECHARGE_RECORD_INFO);
        return Rets.success();
    }

    @GetMapping(value = "/rechargehistoryExportV2")
//	@RequiresPermissions(value = "rechargehistoryExport")  TODO 权限
    public void exportV2(HttpServletResponse  response,
                         @RequestParam(required = false) Integer rechargeStatus, @RequestParam(required = false) String channelName,
                         @RequestParam(required = false) String account, @RequestParam(required = false) String orderNumber,
                         @RequestParam(required = false) String expireTimes, @RequestParam(required = false) String expireTimee,
                         @RequestParam(required = false) String sourceInvitationCode,@RequestParam(required = false) String vipType,
                         @RequestParam(required = false) String withdrawalAddress, @RequestParam(required = false) String firstCharge,
                         @RequestParam(required = false) String channelType, @RequestParam(required = false) String countryCodeNumber
    ) {
        Page<RechargeRecord> page = new PageFactory<RechargeRecord>().defaultPage();
        if (isProxy()){
            page.addFilter("sourceInvitationCode",getUcode());
        }else if( StringUtil.isNotEmpty( sourceInvitationCode ) ){
            User user = userService.get(SearchFilter.build("account", sourceInvitationCode));
            if (user != null) {
                page.addFilter("sourceInvitationCode", SearchFilter.Operator.LIKE, user.getUcode());
            }else{
                throw new RuntimeException("未找到此代理账号");
            }
        }
        page.addFilter(SearchFilter.build("fidw", SearchFilter.Operator.ISNULL));
        page.addFilter( "rechargeStatus", rechargeStatus );
        page.addFilter( "channelName", channelName );
        page.addFilter( "account", account );
        page.addFilter( "orderNumber", orderNumber );
        page.addFilter( "withdrawalAddress", withdrawalAddress );
        page.addFilter( "firstCharge", firstCharge );
        page.addFilter( "channelType", channelType );
        page.addFilter("userInfo.vipType",vipType);
        page.addFilter("userInfo.countryCodeNumber",countryCodeNumber);
        if (StringUtil.isNotEmpty( expireTimes ) && StringUtil.isNotEmpty( expireTimee )) {
            expireTimes = DateUtil.getTimeByZone(expireTimes);
            expireTimee = DateUtil.getTimeByZone(expireTimee);
            page.addFilter( "modifyTime", SearchFilter.Operator.GTE, DateUtil.parseTime( expireTimes ) );
            page.addFilter( "modifyTime", SearchFilter.Operator.LT, DateUtil.parseTime( expireTimee ) );
        }
        page = rechargeRecordService.queryPage( page );
        List<Map<String,Object>> list = (List<Map<String,Object>>) new RechargeRecordWrapper( BeanUtil.objectsToMaps( page.getRecords() ) ).warp();
        if (ObjectUtil.isEmpty(page) || CollUtil.isEmpty(list)){
            throw new RuntimeException("查询为空");
        }
        rechargeRecordService.exportV2(response,list);
    }



    /**
    * @Description:  统计全部订单金额
    * @return:
    * @Author: Skj
    */
    @GetMapping(value = "/findCountMoney")
    public Ret findCountMoney(@RequestParam(required = false) Integer rechargeStatus, @RequestParam(required = false) String channelName,
                              @RequestParam(required = false) String account, @RequestParam(required = false) String orderNumber,
                              @RequestParam(required = false) String expireTimes, @RequestParam(required = false) String expireTimee,
                              @RequestParam(required = false) String sourceInvitationCode,@RequestParam(required = false) String vipType,
                              @RequestParam(required = false) String channelType, @RequestParam(required = false) String withdrawalAddress,
                              @RequestParam(required = false) String firstCharge, @RequestParam(required = false) String countryCodeNumber
    ){
        if( StringUtil.isNotEmpty( expireTimes ) && StringUtil.isNotEmpty( expireTimee ) ) {
            expireTimes = DateUtil.getTimeByZone(expireTimes);
            expireTimee = DateUtil.getTimeByZone(expireTimee);
        }
        String invitationCode="";
        if (isProxy()){
            invitationCode=getUcode();
        }else if( StringUtil.isNotEmpty( sourceInvitationCode ) ){
            User user = userService.get(SearchFilter.build("account", sourceInvitationCode));
            if (user != null) {
                invitationCode=user.getUcode();
            } else {
                return Rets.failure("未找到此代理账号");
            }
        }
        return rechargeRecordService.findCountMoney(rechargeStatus,channelName,account,orderNumber,expireTimes,expireTimee,
                invitationCode,vipType,channelType,withdrawalAddress,firstCharge,countryCodeNumber);
    }


}