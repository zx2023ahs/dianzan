package cn.rh.flash.api.controller.dz.dzsys;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzsys.HomePageTotal;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.dzsys.HomePageTotalService;
import cn.rh.flash.utils.factory.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/dzsys/homepagetotal")
public class HomePageTotalController extends BaseController {
	private  Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private HomePageTotalService homePageTotalService;

	@GetMapping(value = "/list")
	@RequiresPermissions(value = "homePageTotal")
	public Ret list(@RequestParam(required = false) Long id) {
		Page<HomePageTotal> page = new PageFactory<HomePageTotal>().defaultPage();
		page.addFilter("id",id);
		page = homePageTotalService.queryPage(page);
		return Rets.success(page);
	}
	@PostMapping
	@BussinessLog(value = "新增首页统计", key = "name")
	@RequiresPermissions(value = "homePageTotalAdd")
	public Ret add(@RequestBody HomePageTotal homePageTotal){
		homePageTotalService.insert(homePageTotal);
		return Rets.success();
	}
	@PutMapping
	@BussinessLog(value = "更新首页统计", key = "name")
	@RequiresPermissions(value = "homePageTotalUpdate")
	public Ret update(@RequestBody HomePageTotal homePageTotal){
		homePageTotalService.update(homePageTotal);
		return Rets.success();
	}
	@DeleteMapping
	@BussinessLog(value = "删除首页统计", key = "id")
	@RequiresPermissions(value = "homePageTotalDelete")
	public Ret remove(Long id){
		if (id == null) {
			throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
		}
		homePageTotalService.delete(id);
		return Rets.success();
	}
}