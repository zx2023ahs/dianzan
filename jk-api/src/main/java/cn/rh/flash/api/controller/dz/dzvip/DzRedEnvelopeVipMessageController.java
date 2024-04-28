package cn.rh.flash.api.controller.dz.dzvip;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.entity.dzvip.DzRedEnvelopeVipMessage;
import cn.rh.flash.service.dzvip.DzRedEnvelopeVipMessageService;

import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;

import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.factory.Page;


import org.apache.shiro.authz.annotation.RequiresPermissions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/dz/red/envelope/vipmessage")
public class DzRedEnvelopeVipMessageController extends BaseController {
	private  Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private DzRedEnvelopeVipMessageService dzRedEnvelopeVipMessageService;

	@GetMapping(value = "/list")
	@RequiresPermissions(value = "dzRedEnvelopeVipMessage")
	public Ret list(@RequestParam(required = false) Long id) {
		Page<DzRedEnvelopeVipMessage> page = new PageFactory<DzRedEnvelopeVipMessage>().defaultPage();
		page.addFilter("id",id);
		page = dzRedEnvelopeVipMessageService.queryPage(page);
		return Rets.success(page);
	}
	@PostMapping
	@BussinessLog(value = "新增Vip红包信息", key = "name")
	@RequiresPermissions(value = "dzRedEnvelopeVipMessageAdd")
	public Ret add(@RequestBody DzRedEnvelopeVipMessage dzRedEnvelopeVipMessage){
		dzRedEnvelopeVipMessage.setIdw(new IdWorker().nextId() + "");
		dzRedEnvelopeVipMessageService.insert(dzRedEnvelopeVipMessage);
		return Rets.success();
	}
	@PutMapping
	@BussinessLog(value = "更新Vip红包信息", key = "name")
	@RequiresPermissions(value = "dzRedEnvelopeVipMessageUpdate")
	public Ret update(@RequestBody DzRedEnvelopeVipMessage dzRedEnvelopeVipMessage){
		dzRedEnvelopeVipMessageService.update(dzRedEnvelopeVipMessage);
		return Rets.success();
	}
	@DeleteMapping
	@BussinessLog(value = "删除Vip红包信息", key = "id")
	@RequiresPermissions(value = "dzRedEnvelopeVipMessageDelete")
	public Ret remove(Long id){
		if (id == null) {
			throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
		}
		dzRedEnvelopeVipMessageService.delete(id);
		return Rets.success();
	}
}