package cn.rh.flash.api.controller.dz.dzsys;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzsys.DzBanner;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.dzsys.DzBannerService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.factory.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/dzsys/dzbanner")
public class DzBannerController extends BaseController {
	private  Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private DzBannerService dzBannerService;
	@Autowired
	private SysLogService sysLogService;
	@GetMapping(value = "/list")
	@RequiresPermissions(value = "dzBanner")
	public Ret list(@RequestParam(required = false) Long id) {
		Page<DzBanner> page = new PageFactory<DzBanner>().defaultPage();
		page.addFilter("id",id);
		page = dzBannerService.queryPage(page);
		return Rets.success(page);
	}
	@PostMapping
	@BussinessLog(value = "新增轮播图", key = "name")
	@RequiresPermissions(value = "dzBannerAdd")
	public Ret add( @Valid @RequestBody DzBanner dzBanner){
		dzBanner.setIdw( new IdWorker().nextId()+"" );
		dzBannerService.insert(dzBanner);
		sysLogService.addSysLog(getUsername(),dzBanner.getId(),"","PC", SysLogEnum.ADD_DZ_BANNER_INFO);
		return Rets.success();
	}
	@PutMapping
	@BussinessLog(value = "更新轮播图", key = "name")
	@RequiresPermissions(value = "dzBannerUpdate")
	public Ret update(@RequestBody DzBanner dzBanner){
		dzBannerService.update(dzBanner);
		sysLogService.addSysLog(getUsername(),dzBanner.getId(),"","PC", SysLogEnum.UPDATE_DZ_BANNER_INFO);
		return Rets.success();
	}
	@DeleteMapping
	@BussinessLog(value = "删除轮播图", key = "id")
	@RequiresPermissions(value = "dzBannerDelete")
	public Ret remove(Long id){
		if (id == null) {
			throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
		}
		dzBannerService.delete(id);
		sysLogService.addSysLog(getUsername(),id,"","PC", SysLogEnum.DELETE_DZ_BANNER_INFO);
		return Rets.success();
	}
}