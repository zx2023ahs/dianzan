package cn.rh.flash.api.controller.dz.dzuser;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzuser.TotalWithdrawalAmount;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.dzuser.TotalWithdrawalAmountService;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.factory.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/dzuser/totalwithdrawal")
public class TotalWithdrawalAmountController extends BaseController {
	private  Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private TotalWithdrawalAmountService totalWithdrawalAmountService;
	@Autowired
	private SysLogService sysLogService;

	@GetMapping(value = "/list")
	@RequiresPermissions(value = "totalWithdrawalAmount")
	public Ret list(@RequestParam(required = false) Long id) {
		Page<TotalWithdrawalAmount> page = new PageFactory<TotalWithdrawalAmount>().defaultPage();
		if (isProxy()) {
			page.addFilter("sourceInvitationCode", getUcode());
		}
		page.addFilter("id",id);
		page = totalWithdrawalAmountService.queryPage(page);
		return Rets.success(page);
	}
	@PostMapping
	@BussinessLog(value = "新增提现总金额", key = "name")
	@RequiresPermissions(value = "totalWithdrawalAmountAdd")
	public Ret add(@RequestBody TotalWithdrawalAmount totalWithdrawalAmount){

		totalWithdrawalAmount.setSourceInvitationCode( getUcode() );
		totalWithdrawalAmount.setIdw( new IdWorker().nextId()+"" );

		totalWithdrawalAmountService.insert(totalWithdrawalAmount);
		sysLogService.addSysLog(getUsername(),totalWithdrawalAmount.getId(),totalWithdrawalAmount.getAccount(),"PC", SysLogEnum.ADD_TOTAL_WITHDRAWAL_AMOUNT_INFO);
		return Rets.success();
	}
	@PutMapping
	@BussinessLog(value = "更新提现总金额", key = "name")
	@RequiresPermissions(value = "totalWithdrawalAmountUpdate")
	public Ret update(@RequestBody TotalWithdrawalAmount totalWithdrawalAmount){
		totalWithdrawalAmountService.update(totalWithdrawalAmount);
		sysLogService.addSysLog(getUsername(),totalWithdrawalAmount.getId(),totalWithdrawalAmount.getAccount(),"PC", SysLogEnum.UPDATE_TOTAL_WITHDRAWAL_AMOUNT_INFO);
		return Rets.success();
	}
	@DeleteMapping
	@BussinessLog(value = "删除提现总金额", key = "id")
	@RequiresPermissions(value = "totalWithdrawalAmountDelete")
	public Ret remove(Long id){
		if (id == null) {
			throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
		}
		totalWithdrawalAmountService.delete(id);
		sysLogService.addSysLog(getUsername(),id,"","PC", SysLogEnum.DELETE_TOTAL_WITHDRAWAL_AMOUNT_INFO);
		return Rets.success();
	}
}