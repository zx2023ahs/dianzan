package cn.rh.flash.api.controller.dz.dzpool;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzpool.PoolParameter;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.dzpool.PoolParameterService;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.factory.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/dzpool/parameter")
public class PoolParameterController extends BaseController {
	private  Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private PoolParameterService poolParameterService;

	@GetMapping(value = "/list")
	@RequiresPermissions(value = "poolParameter")
	public Ret list(@RequestParam(required = false) Long id) {
		Page<PoolParameter> page = new PageFactory<PoolParameter>().defaultPage();
		page.addFilter("id",id);
		page = poolParameterService.queryPage(page);
		return Rets.success(page);
	}
	@PostMapping
	@BussinessLog(value = "新增爱心等级设定表", key = "name")
	@RequiresPermissions(value = "poolParameterAdd")
	public Ret add(@RequestBody PoolParameter poolParameter){
		poolParameter.setIdw(new IdWorker().nextId()+"");
		poolParameterService.insert(poolParameter);
		return Rets.success();
	}
	@PutMapping
	@BussinessLog(value = "更新爱心等级设定表", key = "name")
	@RequiresPermissions(value = "poolParameterUpdate")
	public Ret update(@RequestBody PoolParameter poolParameter){
		poolParameterService.update(poolParameter);
		return Rets.success();
	}
	@DeleteMapping
	@BussinessLog(value = "删除爱心等级设定表", key = "id")
	@RequiresPermissions(value = "poolParameterDelete")
	public Ret remove(Long id){
		if (id == null) {
			throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
		}
		poolParameterService.delete(id);
		return Rets.success();
	}
}