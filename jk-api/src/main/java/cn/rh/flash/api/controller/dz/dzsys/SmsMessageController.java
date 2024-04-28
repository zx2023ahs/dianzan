package cn.rh.flash.api.controller.dz.dzsys;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzsys.SmsMessage;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.dzsys.SmsMessageService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.SmsMessageWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/dzsys/sms")
public class SmsMessageController extends BaseController {
	private  Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private SmsMessageService smsMessageService;
	@Autowired
	private SysLogService sysLogService;
	@GetMapping(value = "/list")
	@RequiresPermissions(value = "smsMessage")
	public Ret list(@RequestParam(required = false) String platformName ) {
		Page<SmsMessage> page = new PageFactory<SmsMessage>().defaultPage();
		page.addFilter("platformName",platformName );
		page = smsMessageService.queryPage(page);

		List list = (List) new SmsMessageWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
		page.setRecords(list);
		return Rets.success(page);
	}
	@PostMapping
	@BussinessLog(value = "新增短信信息", key = "name")
	@RequiresPermissions(value = "smsMessageAdd")
	public Ret add( @Valid @RequestBody SmsMessage smsMessage){
		smsMessageService.insert(smsMessage);
		sysLogService.addSysLog(getUsername(),smsMessage.getId(),"","PC", SysLogEnum.ADD_SMS_MESSAGE_INFO);
		return Rets.success();
	}
	@PutMapping
	@BussinessLog(value = "更新短信信息", key = "name")
	@RequiresPermissions(value = "smsMessageUpdate")
	public Ret update(@RequestBody SmsMessage smsMessage){
		smsMessageService.update(smsMessage);
		sysLogService.addSysLog(getUsername(),smsMessage.getId(),"","PC", SysLogEnum.UPDATE_SMS_MESSAGE_INFO);
		return Rets.success();
	}
	@DeleteMapping
	@BussinessLog(value = "删除短信信息", key = "id")
	@RequiresPermissions(value = "smsMessageDelete")
	public Ret remove(Long id){
		if (id == null) {
			throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
		}
		smsMessageService.delete(id);
		sysLogService.addSysLog(getUsername(),id,"","PC", SysLogEnum.DELETE_SMS_MESSAGE_INFO);
		return Rets.success();
	}
}