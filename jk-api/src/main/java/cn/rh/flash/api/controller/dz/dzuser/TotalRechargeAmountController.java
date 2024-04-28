package cn.rh.flash.api.controller.dz.dzuser;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzuser.TotalRechargeAmount;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.dzuser.TotalRechargeAmountService;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.factory.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/dzuser/totalrecharge")
public class TotalRechargeAmountController extends BaseController {
	private  Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private TotalRechargeAmountService totalRechargeAmountService;

	@Autowired
	private SysLogService sysLogService;

	@GetMapping(value = "/list")
	@RequiresPermissions(value = "totalRechargeAmount")
	public Ret list(@RequestParam(required = false) Long id) {
		Page<TotalRechargeAmount> page = new PageFactory<TotalRechargeAmount>().defaultPage();
		if (isProxy()) {
			page.addFilter("sourceInvitationCode", getUcode());
		}
		page.addFilter("id",id);
		page = totalRechargeAmountService.queryPage(page);
		return Rets.success(page);
	}
	@PostMapping
	@BussinessLog(value = "新增充值总金额", key = "name")
	@RequiresPermissions(value = "totalRechargeAmountAdd")
	public Ret add(@RequestBody TotalRechargeAmount totalRechargeAmount){

		totalRechargeAmount.setSourceInvitationCode( getUcode() );
		totalRechargeAmount.setIdw( new IdWorker().nextId()+"" );

		totalRechargeAmountService.insert(totalRechargeAmount);

		sysLogService.addSysLog(getUsername(),totalRechargeAmount.getId(),totalRechargeAmount.getAccount(),"PC", SysLogEnum.ADD_TOTAL_RECHARGE_AMOUNT_INFO);
		return Rets.success();
	}
	@PutMapping
	@BussinessLog(value = "更新充值总金额", key = "name")
	@RequiresPermissions(value = "totalRechargeAmountUpdate")
	public Ret update(@RequestBody TotalRechargeAmount totalRechargeAmount){
		totalRechargeAmountService.update(totalRechargeAmount);
		sysLogService.addSysLog(getUsername(),totalRechargeAmount.getId(),totalRechargeAmount.getAccount(),"PC", SysLogEnum.UPDATE_TOTAL_RECHARGE_AMOUNT_INFO);
		return Rets.success();
	}
	@DeleteMapping
	@BussinessLog(value = "删除充值总金额", key = "id")
	@RequiresPermissions(value = "totalRechargeAmountDelete")
	public Ret remove(Long id){
		if (id == null) {
			throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
		}
		totalRechargeAmountService.delete(id);
		sysLogService.addSysLog(getUsername(),id,"","PC", SysLogEnum.DELETE_TOTAL_RECHARGE_AMOUNT_INFO);
		return Rets.success();
	}
}