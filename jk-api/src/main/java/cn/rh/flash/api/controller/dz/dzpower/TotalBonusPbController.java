package cn.rh.flash.api.controller.dz.dzpower;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzpower.TotalBonusPb;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.dzpower.TotalBonusPbService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.utils.factory.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/dzgoods/totalbonuspb")
public class TotalBonusPbController extends BaseController {
	private  Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private TotalBonusPbService totalBonusPbService;
	@Autowired
	private SysLogService sysLogService;
	@GetMapping(value = "/list")
	@RequiresPermissions(value = "totalBonusPb")
	public Ret list(@RequestParam(required = false) Long id) {
		Page<TotalBonusPb> page = new PageFactory<TotalBonusPb>().defaultPage();
		if (isProxy()){
			page.addFilter("sourceInvitationCode",getUcode());
		}
		page.addFilter("id",id);
		page = totalBonusPbService.queryPage(page);
		return Rets.success(page);
	}
	@PostMapping
	@BussinessLog(value = "新增充电宝返佣总收入", key = "name")
	@RequiresPermissions(value = "totalBonusPbAdd")
	public Ret add( @Valid  @RequestBody TotalBonusPb totalBonusPb){
		totalBonusPbService.insert(totalBonusPb);
		sysLogService.addSysLog(getUsername(),totalBonusPb.getId(),totalBonusPb.getAccount(),"PC", SysLogEnum.ADD_TOTAL_BONUS_PB_INFO);
		return Rets.success();
	}
	@PutMapping
	@BussinessLog(value = "更新充电宝返佣总收入", key = "name")
	@RequiresPermissions(value = "totalBonusPbUpdate")
	public Ret update( @Valid @RequestBody TotalBonusPb totalBonusPb){
		totalBonusPbService.update(totalBonusPb);
		sysLogService.addSysLog(getUsername(),totalBonusPb.getId(),totalBonusPb.getAccount(),"PC", SysLogEnum.UPDATE_TOTAL_BONUS_PB_INFO);
		return Rets.success();
	}
	@DeleteMapping
	@BussinessLog(value = "删除充电宝返佣总收入", key = "id")
	@RequiresPermissions(value = "totalBonusPbDelete")
	public Ret remove(Long id){
		if (id == null) {
			throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
		}
		totalBonusPbService.delete(id);
		sysLogService.addSysLog(getUsername(),id,"","PC", SysLogEnum.DELETE_TOTAL_BONUS_PB_INFO);
		return Rets.success();
	}
}