package cn.rh.flash.api.controller.dz.dzpool;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzpool.Pool;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.dzpool.PoolService;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.factory.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/dzpool/pool")
public class PoolController extends BaseController {
	private  Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private PoolService poolService;

	@GetMapping(value = "/list")
	@RequiresPermissions(value = "pool")
	public Ret list(@RequestParam(required = false) Long id) {
		Page<Pool> page = new PageFactory<Pool>().defaultPage();
		page.addFilter("id",id);
		page = poolService.queryPage(page);
		return Rets.success(page);
	}
	@PostMapping
	@BussinessLog(value = "新增公积金池表", key = "name")
	@RequiresPermissions(value = "poolAdd")
	public Ret add(@RequestBody Pool pool){
		pool.setIdw(new IdWorker().nextId()+"");
		poolService.insert(pool);
		return Rets.success();
	}
	@PutMapping
	@BussinessLog(value = "更新公积金池表", key = "name")
	@RequiresPermissions(value = "poolUpdate")
	public Ret update(@RequestBody Pool pool){
		poolService.update(pool);
		return Rets.success();
	}
	@DeleteMapping
	@BussinessLog(value = "删除公积金池表", key = "id")
	@RequiresPermissions(value = "poolDelete")
	public Ret remove(Long id){
		if (id == null) {
			throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
		}
		poolService.delete(id);
		return Rets.success();
	}



}