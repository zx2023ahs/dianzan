package cn.rh.flash.api.controller.dz.dzpool;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzpool.UserPool;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.dzpool.UserPoolService;
import cn.rh.flash.utils.factory.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/dzuser/pool")
public class UserPoolController extends BaseController {
	private  Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private UserPoolService userPoolService;

	@GetMapping(value = "/list")
	@RequiresPermissions(value = "userPool")
	public Ret list(@RequestParam(required = false) Long id) {
		Page<UserPool> page = new PageFactory<UserPool>().defaultPage();
		page.addFilter("id",id);
		page = userPoolService.queryPage(page);
		return Rets.success(page);
	}
	@PostMapping
	@BussinessLog(value = "新增用户关联爱心值表", key = "name")
	@RequiresPermissions(value = "userPoolAdd")
	public Ret add(@RequestBody UserPool userPool){
		userPoolService.insert(userPool);
		return Rets.success();
	}
	@PutMapping
	@BussinessLog(value = "更新用户关联爱心值表", key = "name")
	@RequiresPermissions(value = "userPoolUpdate")
	public Ret update(@RequestBody UserPool userPool){
		userPoolService.update(userPool);
		return Rets.success();
	}
	@DeleteMapping
	@BussinessLog(value = "删除用户关联爱心值表", key = "id")
	@RequiresPermissions(value = "userPoolDelete")
	public Ret remove(Long id){
		if (id == null) {
			throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
		}
		userPoolService.delete(id);
		return Rets.success();
	}
}