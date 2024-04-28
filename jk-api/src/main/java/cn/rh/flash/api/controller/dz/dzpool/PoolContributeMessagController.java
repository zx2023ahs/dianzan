package cn.rh.flash.api.controller.dz.dzpool;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzpool.PoolContributeMessag;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.dzpool.PoolContributeMessagService;
import cn.rh.flash.utils.factory.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/dzpool/contribute/messag")
public class PoolContributeMessagController extends BaseController {
	private  Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private PoolContributeMessagService poolContributeMessagService;

	@GetMapping(value = "/list")
	@RequiresPermissions(value = "poolContributeMessag")
	public Ret list(@RequestParam(required = false) Long id) {
		Page<PoolContributeMessag> page = new PageFactory<PoolContributeMessag>().defaultPage();
		page.addFilter("id",id);
		page = poolContributeMessagService.queryPage(page);
		return Rets.success(page);
	}
	@PostMapping
	@BussinessLog(value = "新增用户捐助记录表", key = "name")
	@RequiresPermissions(value = "poolContributeMessagAdd")
	public Ret add(@RequestBody PoolContributeMessag poolContributeMessag){
		poolContributeMessagService.insert(poolContributeMessag);
		return Rets.success();
	}
	@PutMapping
	@BussinessLog(value = "更新用户捐助记录表", key = "name")
	@RequiresPermissions(value = "poolContributeMessagUpdate")
	public Ret update(@RequestBody PoolContributeMessag poolContributeMessag){
		poolContributeMessagService.update(poolContributeMessag);
		return Rets.success();
	}
	@DeleteMapping
	@BussinessLog(value = "删除用户捐助记录表", key = "id")
	@RequiresPermissions(value = "poolContributeMessagDelete")
	public Ret remove(Long id){
		if (id == null) {
			throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
		}
		poolContributeMessagService.delete(id);
		return Rets.success();
	}
}