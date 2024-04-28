package cn.rh.flash.api.controller.dz.dzpower;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzpower.PowerBankTask;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.service.dzpower.PowerBankTaskService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.StringUtil;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.PowerBankTaskWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/dzgoods/powerbanktask")
public class PowerBankTaskController extends BaseController {
	private  Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private PowerBankTaskService powerBankTaskService;
	@Autowired
	private SysLogService sysLogService;
	@GetMapping(value = "/list")
	@RequiresPermissions(value = "powerBankTask")
	public Ret list(@RequestParam(required = false) String account,
					@RequestParam(required = false) String lastTimes, @RequestParam(required = false) String lastTimee,
					@RequestParam(required = false) String expireTimes, @RequestParam(required = false) String expireTimee,
					@RequestParam(required = false) String gmt) {
		Page<PowerBankTask> page = new PageFactory<PowerBankTask>().defaultPage();
		page.addFilter("account",account);
//		if( StringUtil.isNotEmpty( expireTimes ) && StringUtil.isNotEmpty( expireTimee ) ){
//			page.addFilter("account", SearchFilter.Operator.BETWEEN, Arrays.asList( DateUtil.parseTime( expireTimes ),DateUtil.parseTime( expireTimee ) ) );
//		}
//		if( StringUtil.isNotEmpty( lastTimes ) && StringUtil.isNotEmpty( lastTimee ) ){
//			page.addFilter("account", SearchFilter.Operator.BETWEEN, Arrays.asList( DateUtil.parseTime( lastTimes ),DateUtil.parseTime( lastTimes ) ) );
//		}
		if( StringUtil.isNotEmpty( lastTimes ) && StringUtil.isNotEmpty( lastTimee ) ) {
			//gmt = StringUtil.isEmpty( gmt ) ? TimeZone.getDefault().getID() : gmt;
			lastTimes = DateUtil.getTimeByZone(lastTimes);
			lastTimee = DateUtil.getTimeByZone(lastTimee);
			page.addFilter( "lastTime", SearchFilter.Operator.GTE, DateUtil.parseTime( lastTimes ) );
			page.addFilter( "lastTime", SearchFilter.Operator.LT, DateUtil.parseTime( lastTimee ) );
		}
		if( StringUtil.isNotEmpty( expireTimes ) && StringUtil.isNotEmpty( expireTimee ) ) {
			//gmt = StringUtil.isEmpty( gmt ) ? TimeZone.getDefault().getID() : gmt;
			expireTimes = DateUtil.getTimeByZone(expireTimes);
			expireTimee = DateUtil.getTimeByZone(expireTimee);
			page.addFilter( "expireTime", SearchFilter.Operator.GTE, DateUtil.parseTime( expireTimes ) );
			page.addFilter( "expireTime", SearchFilter.Operator.LT, DateUtil.parseTime( expireTimee ) );
		}

		page = powerBankTaskService.queryPage(page);
		List list = (List) new PowerBankTaskWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
		page.setRecords(list);
		return Rets.success(page);
	}
	@PostMapping
	@BussinessLog(value = "新增充电宝返佣任务", key = "name")
	@RequiresPermissions(value = "powerBankTaskAdd")
	public Ret add( @Valid  @RequestBody PowerBankTask powerBankTask){
		powerBankTaskService.insert(powerBankTask);
		sysLogService.addSysLog(getUsername(),powerBankTask.getId(),powerBankTask.getAccount(),"PC", SysLogEnum.ADD_POWER_BANK_TASK_INFO);
		return Rets.success();
	}
	@PutMapping
	@BussinessLog(value = "更新充电宝返佣任务", key = "name")
	@RequiresPermissions(value = "powerBankTaskUpdate")
	public Ret update( @Valid @RequestBody PowerBankTask powerBankTask){
		powerBankTaskService.update(powerBankTask);
		sysLogService.addSysLog(getUsername(),powerBankTask.getId(),powerBankTask.getAccount(),"PC", SysLogEnum.UPDATE_POWER_BANK_TASK_INFO);
		return Rets.success();
	}
	@DeleteMapping
	@BussinessLog(value = "删除充电宝返佣任务", key = "id")
	@RequiresPermissions(value = "powerBankTaskDelete")
	public Ret remove(Long id){
		if (id == null) {
			throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
		}
		powerBankTaskService.delete(id);
		sysLogService.addSysLog(getUsername(),id,"","PC", SysLogEnum.DELETE_POWER_BANK_TASK_INFO);
		return Rets.success();
	}
}