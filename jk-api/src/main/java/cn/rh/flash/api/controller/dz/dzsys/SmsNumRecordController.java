package cn.rh.flash.api.controller.dz.dzsys;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzsys.SmsNumRecord;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.dzsys.SmsNumRecordService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.utils.factory.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/dzsys/smsnumrecord")
public class SmsNumRecordController extends BaseController {
	private  Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private SmsNumRecordService smsNumRecordService;

	@Autowired
	private SysLogService sysLogService;

	@GetMapping(value = "/list")
	@RequiresPermissions(value = "smsNumRecord")
	public Ret list(@RequestParam(required = false) String phone) {
		Page<SmsNumRecord> page = new PageFactory<SmsNumRecord>().defaultPage();
		page.addFilter("phone",phone);

		List<Sort.Order> orders = new ArrayList<>();

		orders.add(Sort.Order.desc("day"));
		orders.add(Sort.Order.desc("count"));
		page.setSort(Sort.by(orders));
		page = smsNumRecordService.queryPage(page);
		return Rets.success(page);

	}
	@PostMapping
	@BussinessLog(value = "新增日发送短信次数记录", key = "name")
	@RequiresPermissions(value = "smsNumRecordAdd")
	public Ret add(@RequestBody SmsNumRecord smsNumRecord){
		smsNumRecordService.insert(smsNumRecord);
		return Rets.success();
	}
	@PutMapping
	@BussinessLog(value = "更新日发送短信次数记录", key = "name")
	@RequiresPermissions(value = "smsNumRecordUpdate")
	public Ret update(@RequestBody SmsNumRecord smsNumRecord){
		smsNumRecordService.update(smsNumRecord);
		return Rets.success();
	}
	@DeleteMapping
	@BussinessLog(value = "删除日发送短信次数记录", key = "id")
	@RequiresPermissions(value = "smsNumRecordDelete")
	public Ret remove(Long id){
		if (id == null) {
			throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
		}
		smsNumRecordService.delete(id);
		sysLogService.addSysLog(getUsername(),id,"","PC", SysLogEnum.DELETE_SMS_NUM_RECORD);
		return Rets.success();
	}
}