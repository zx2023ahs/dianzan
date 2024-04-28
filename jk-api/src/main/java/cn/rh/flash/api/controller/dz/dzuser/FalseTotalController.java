package cn.rh.flash.api.controller.dz.dzuser;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzuser.FalseTotal;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.service.dzuser.FalseTotalService;
import cn.rh.flash.utils.factory.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/dzuser/falsetotal")
public class FalseTotalController extends BaseController {
	private  Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private FalseTotalService falseTotalService;

	@GetMapping(value = "/list")
	@RequiresPermissions(value = "falseTotal")
	public Ret list(@RequestParam(required = false) String account) {
		Page<FalseTotal> page = new PageFactory<FalseTotal>().defaultPage();
		page.addFilter("account",account);
		page = falseTotalService.queryPage(page);
		return Rets.success(page);
	}
	@PostMapping
	@BussinessLog(value = "新增造假统计", key = "name")
	@RequiresPermissions(value = "falseTotalAdd")
	public Ret add(@RequestBody FalseTotal falseTotal){
		FalseTotal falseTotalDB = falseTotalService.get(SearchFilter.build("account", falseTotal.getAccount()));
		if (falseTotalDB!=null){
			return Rets.failure("当前账号已有造假统计");
		}
		falseTotalService.insert(falseTotal);
		return Rets.success();
	}
	@PutMapping
	@BussinessLog(value = "更新造假统计", key = "name")
	@RequiresPermissions(value = "falseTotalUpdate")
	public Ret update(@RequestBody FalseTotal falseTotal){
		falseTotalService.update(falseTotal);
		return Rets.success();
	}
	@DeleteMapping
	@BussinessLog(value = "删除造假统计", key = "id")
	@RequiresPermissions(value = "falseTotalDelete")
	public Ret remove(Long id){
		if (id == null) {
			throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
		}
		falseTotalService.delete(id);
		return Rets.success();
	}
}