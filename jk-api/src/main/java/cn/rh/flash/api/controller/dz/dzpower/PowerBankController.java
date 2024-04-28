package cn.rh.flash.api.controller.dz.dzpower;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzpower.PowerBank;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.dzpower.PowerBankService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.PowerBankWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/dzgoods/powerbank")
public class PowerBankController extends BaseController {
	private  Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private PowerBankService powerBankService;
	@Autowired
	private SysLogService sysLogService;
	@GetMapping(value = "/list")
	@RequiresPermissions(value = "powerBank")
	public Ret list(@RequestParam(required = false) Long id) {
		Page<PowerBank> page = new PageFactory<PowerBank>().defaultPage();
		page.addFilter("id",id);
		page = powerBankService.queryPage(page);

		List list = (List) new PowerBankWrapper( BeanUtil.objectsToMaps(page.getRecords())).warp();
		page.setRecords(list);
		return Rets.success(page);
	}

	@PostMapping
	@BussinessLog(value = "新增充电宝", key = "name")
	@RequiresPermissions(value = "powerBankAdd")
	public Ret add( @Valid  @RequestBody PowerBank powerBank){
		powerBank.setIdw( new IdWorker().nextId()+"" );
		powerBankService.insert(powerBank);
		sysLogService.addSysLog(getUsername(),powerBank.getId(),"","PC", SysLogEnum.ADD_POWER_BANK_INFO);
		return Rets.success();
	}
	@PutMapping
	@BussinessLog(value = "更新充电宝", key = "name")
	@RequiresPermissions(value = "powerBankUpdate")
	public Ret update( @Valid @RequestBody PowerBank powerBank){
		powerBankService.update(powerBank);
		sysLogService.addSysLog(getUsername(),powerBank.getId(),"","PC", SysLogEnum.UPDATE_POWER_BANK_INFO);
		return Rets.success();
	}
	@DeleteMapping
	@BussinessLog(value = "删除充电宝", key = "id")
	@RequiresPermissions(value = "powerBankDelete")
	public Ret remove(Long id){
		if (id == null) {
			throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
		}
		powerBankService.delete(id);
		sysLogService.addSysLog(getUsername(),id,"","PC", SysLogEnum.DELETE_POWER_BANK_INFO);
		return Rets.success();
	}
}