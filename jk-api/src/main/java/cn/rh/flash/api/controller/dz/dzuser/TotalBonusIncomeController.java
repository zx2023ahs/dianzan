package cn.rh.flash.api.controller.dz.dzuser;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzuser.TotalBonusIncome;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.dzuser.TotalBonusIncomeService;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.factory.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/dzuser/totalbonus")
public class TotalBonusIncomeController extends BaseController {
	private  Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private TotalBonusIncomeService totalBonusIncomeService;

	@Autowired
	private SysLogService sysLogService;

	@GetMapping(value = "/list")
	@RequiresPermissions(value = "totalBonusIncome")
	public Ret list(@RequestParam(required = false) Long id) {
		Page<TotalBonusIncome> page = new PageFactory<TotalBonusIncome>().defaultPage();
		if (isProxy()) {
			page.addFilter("sourceInvitationCode", getUcode());
		}
		page.addFilter("id",id);
		page = totalBonusIncomeService.queryPage(page);
		return Rets.success(page);
	}
	@PostMapping
	@BussinessLog(value = "新增赠送彩金总收入", key = "name")
	@RequiresPermissions(value = "totalBonusIncomeAdd")
	public Ret add(@RequestBody TotalBonusIncome totalBonusIncome){

		totalBonusIncome.setSourceInvitationCode( getUcode() );
		totalBonusIncome.setIdw( new IdWorker().nextId()+"" );
		totalBonusIncomeService.insert(totalBonusIncome);
		sysLogService.addSysLog(getUsername(),totalBonusIncome.getId(),totalBonusIncome.getAccount(),"PC", SysLogEnum.ADD_TOTAL_BONUS_INCOME_INFO);
		return Rets.success();
	}
	@PutMapping
	@BussinessLog(value = "更新赠送彩金总收入", key = "name")
	@RequiresPermissions(value = "totalBonusIncomeUpdate")
	public Ret update(@RequestBody TotalBonusIncome totalBonusIncome){
		totalBonusIncomeService.update(totalBonusIncome);
		sysLogService.addSysLog(getUsername(),totalBonusIncome.getId(),totalBonusIncome.getAccount(),"PC", SysLogEnum.UPDATE_TOTAL_BONUS_INCOME_INFO);
		return Rets.success();
	}
	@DeleteMapping
	@BussinessLog(value = "删除赠送彩金总收入", key = "id")
	@RequiresPermissions(value = "totalBonusIncomeDelete")
	public Ret remove(Long id){
		if (id == null) {
			throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
		}
		totalBonusIncomeService.delete(id);
		sysLogService.addSysLog(getUsername(),id,"","PC", SysLogEnum.DELETE_TOTAL_BONUS_INCOME_INFO);
		return Rets.success();
	}
}