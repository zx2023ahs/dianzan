package cn.rh.flash.api.controller.dz.dzuser;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzuser.CompensationRecord;
import cn.rh.flash.bean.entity.system.User;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.dzuser.CompensationRecordService;
import cn.rh.flash.service.dzuser.TotalBonusIncomeService;
import cn.rh.flash.service.dzuser.UserInfoService;
import cn.rh.flash.service.system.FileService;
import cn.rh.flash.service.system.UserService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.StringUtil;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.CompensationRecordWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


@RestController
@RequestMapping("/dzuser/compensation")
public class CompensationRecordController extends BaseController {

	private  Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CompensationRecordService compensationRecordService;
	@Autowired
	private FileService fileService;

	@Autowired
	private ConfigCache configCache;

	@Autowired
	private UserInfoService userInfoService;

	@Autowired
	private UserService userService;

	@Autowired
	private TotalBonusIncomeService totalBonusIncomeService;

	@Autowired
	private SysLogService sysLogService;
	@GetMapping(value = "/list")
	@RequiresPermissions(value = "compensationRecord")
	public Ret list( @RequestParam(required = false) String operator,@RequestParam(required = false) String account,
					 @RequestParam(required = false) String expireTimes, @RequestParam(required = false) String expireTimee,
					 @RequestParam(required = false) String sourceInvitationCode,@RequestParam(required = false) String gmt) {
		Page<CompensationRecord> page = new PageFactory<CompensationRecord>().defaultPage();
		if (isProxy()){
			page.addFilter("sourceInvitationCode",getUcode());
		}else if( StringUtil.isNotEmpty( sourceInvitationCode ) ){
//			UserInfo userInfo = userInfoService.get( SearchFilter.build( "account", sourceInvitationCode ) );
//			if (userInfo != null) {
//				page.addFilter("sourceInvitationCode", SearchFilter.Operator.LIKE,  userInfo.getSourceInvitationCode() );
//			}else {
//				return Rets.success(page);
//			}
			User user = userService.get(SearchFilter.build("account", sourceInvitationCode));
			if (user != null) {
				page.addFilter("sourceInvitationCode", SearchFilter.Operator.LIKE, user.getUcode());
			} else {
				return Rets.failure("未找到此代理账号");
			}
		}
		page.addFilter("operator",operator);
		page.addFilter("account",account);
		page.addFilter("operator",SearchFilter.Operator.NE,"reg_gift");
		String testCode = configCache.get(ConfigKeyEnum.TEST_USER_CODE);
		page.addFilter("sourceInvitationCode",SearchFilter.Operator.NE,testCode);


//		if( StringUtil.isNotEmpty( expireTimes ) && StringUtil.isNotEmpty( expireTimee ) ){
//			page.addFilter("createTime", SearchFilter.Operator.BETWEEN, Arrays.asList( DateUtil.parseTime( expireTimes ),DateUtil.parseTime( expireTimee ) ) );
//		}
		if (StringUtil.isNotEmpty( expireTimes ) && StringUtil.isNotEmpty( expireTimee )) {
			//gmt = StringUtil.isEmpty( gmt ) ? TimeZone.getDefault().getID() : gmt;
			expireTimes = DateUtil.getTimeByZone(expireTimes);
			expireTimee = DateUtil.getTimeByZone(expireTimee);
			page.addFilter( "createTime", SearchFilter.Operator.GTE, DateUtil.parseTime( expireTimes ) );
			page.addFilter( "createTime", SearchFilter.Operator.LT, DateUtil.parseTime( expireTimee ) );
		}

		page = compensationRecordService.queryPage(page);

		List list = (List) new CompensationRecordWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
		page.setRecords(list);
		return Rets.success(page);

	}
	@PostMapping
	@BussinessLog(value = "新增补分记录", key = "name")
	@RequiresPermissions(value = "compensationRecordAdd")
	public Ret add(@RequestBody CompensationRecord compensationRecord){
		compensationRecord.setIdw( new IdWorker().nextId()+"" );
		compensationRecord.setSourceInvitationCode( getUcode() );
		compensationRecordService.insert(compensationRecord);
		sysLogService.addSysLog(getUsername(),compensationRecord.getId(),compensationRecord.getAccount(),"PC", SysLogEnum.ADD_COMPENSATION_RECORD_INFO);
		return Rets.success();
	}
	@PutMapping
	@BussinessLog(value = "更新补分记录", key = "name")
	@RequiresPermissions(value = "compensationRecordUpdate")
	public Ret update(@RequestBody CompensationRecord compensationRecord){
		compensationRecordService.update(compensationRecord);
		sysLogService.addSysLog(getUsername(),compensationRecord.getId(),compensationRecord.getAccount(),"PC", SysLogEnum.UPDATE_COMPENSATION_RECORD_INFO);
		return Rets.success();
	}
	@DeleteMapping
	@BussinessLog(value = "删除补分记录", key = "id")
	@RequiresPermissions(value = "compensationRecordDelete")
	public Ret remove(Long id){
		if (id == null) {
			throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
		}
		compensationRecordService.delete(id);
		sysLogService.addSysLog(getUsername(),id,"","PC", SysLogEnum.DELETE_COMPENSATION_RECORD_INFO);
		return Rets.success();
	}

	@GetMapping(value = "/compensationExportV2")
//	@RequiresPermissions(value = "compensationExportV2") TODO 权限
	public void exportV2(HttpServletResponse response,
						 @RequestParam(required = false) String operator, @RequestParam(required = false) String account,
						 @RequestParam(required = false) String expireTimes, @RequestParam(required = false) String expireTimee,
						 @RequestParam(required = false) String sourceInvitationCode, @RequestParam(required = false) String gmt){
		Page<CompensationRecord> page = new PageFactory<CompensationRecord>().defaultPage();
		if (isProxy()){
			page.addFilter("sourceInvitationCode",getUcode());
		}else if( StringUtil.isNotEmpty( sourceInvitationCode ) ){

			User user = userService.get(SearchFilter.build("account", sourceInvitationCode));
			if (user != null) {
				page.addFilter("sourceInvitationCode", SearchFilter.Operator.LIKE, user.getUcode());
			} else {
				throw new RuntimeException("未找到此代理账号");
			}
		}
		page.addFilter("operator",operator);
		page.addFilter("account",account);
		page.addFilter("operator",SearchFilter.Operator.NE,"reg_gift");
		if( StringUtil.isNotEmpty( expireTimes ) && StringUtil.isNotEmpty( expireTimee ) ) {
			expireTimes = DateUtil.getTimeByZone(expireTimes);
			expireTimee = DateUtil.getTimeByZone(expireTimee);
			page.addFilter( "createTime", SearchFilter.Operator.GTE,  DateUtil.parseTime(expireTimes ) );
			page.addFilter( "createTime", SearchFilter.Operator.LT,  DateUtil.parseTime(expireTimee ) );
		}
		page = compensationRecordService.queryPage(page);
		if (CollUtil.isEmpty(page.getRecords()) || ObjectUtil.isEmpty(page)){
			throw new RuntimeException("查询为空");
		}
		compensationRecordService.exportV2(response,page.getRecords());
	}


	/**
	 * 补分总记录
	 * @return
	 */
	@GetMapping(value = "/branchTotal")
	public Ret branchTotal(@RequestParam(required = false) String operator,@RequestParam(required = false) String account,
						   @RequestParam(required = false) String expireTimes, @RequestParam(required = false) String expireTimee,
						   @RequestParam(required = false) String sourceInvitationCode){
		if (isProxy()){
			sourceInvitationCode= getUcode();
		}else if( StringUtil.isNotEmpty( sourceInvitationCode ) ){
//			UserInfo userInfo = userInfoService.get( SearchFilter.build( "account", sourceInvitationCode ) );
//			if (userInfo != null) {
//				sourceInvitationCode= userInfo.getSourceInvitationCode();
//			}else {
//				return Rets.success();
//			}
			User user = userService.get(SearchFilter.build("account", sourceInvitationCode));
			if (user != null) {
//				page.addFilter("sourceInvitationCode", SearchFilter.Operator.LIKE, user.getUcode());
				sourceInvitationCode = user.getUcode();
			} else {
				return Rets.failure("未找到此代理账号");
			}
		}
		if( StringUtil.isNotEmpty( expireTimes ) && StringUtil.isNotEmpty( expireTimee ) ) {
			//gmt = StringUtil.isEmpty( gmt ) ? TimeZone.getDefault().getID() : gmt;
			expireTimes = DateUtil.getTimeByZone(expireTimes);
			expireTimee = DateUtil.getTimeByZone(expireTimee);
		}
		String testCode = configCache.get(ConfigKeyEnum.TEST_USER_CODE);
		Double compensationRecord = compensationRecordService.branchTotal(operator,account,expireTimes,expireTimee,sourceInvitationCode,testCode);
		return Rets.success(compensationRecord);
	}
}