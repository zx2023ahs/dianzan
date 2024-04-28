package cn.rh.flash.api.controller.dz.dzpool;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.dto.PoolResortMessagDto;
import cn.rh.flash.bean.entity.dzpool.PoolResortMessag;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.dzpool.PoolResortMessagService;
import cn.rh.flash.utils.factory.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/dzpool/resort/messag")
public class PoolResortMessagController extends BaseController {
	private  Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private PoolResortMessagService poolResortMessagService;

	@GetMapping(value = "/list")
	@RequiresPermissions(value = "poolResortMessag")
	public Ret list(@RequestParam(required = false) Long id) {
		Page<PoolResortMessag> page = new PageFactory<PoolResortMessag>().defaultPage();
		page.addFilter("id",id);
		page = poolResortMessagService.queryPage(page);
		return Rets.success(page);
	}
	@PostMapping
	@BussinessLog(value = "新增用户求助记录表", key = "name")
	@RequiresPermissions(value = "poolResortMessagAdd")
	public Ret add(@RequestBody PoolResortMessag poolResortMessag){
		poolResortMessagService.insert(poolResortMessag);
		return Rets.success();
	}
	@PutMapping
	@BussinessLog(value = "更新用户求助记录表", key = "name")
	@RequiresPermissions(value = "poolResortMessagUpdate")
	public Ret update(@RequestBody PoolResortMessag poolResortMessag){
		poolResortMessagService.update(poolResortMessag);
		return Rets.success();
	}
	@DeleteMapping
	@BussinessLog(value = "删除用户求助记录表", key = "id")
	@RequiresPermissions(value = "poolResortMessagDelete")
	public Ret remove(Long id){
		if (id == null) {
			throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
		}
		poolResortMessagService.delete(id);
		return Rets.success();
	}


	@GetMapping("examineTrue")
	@BussinessLog(value = "审核用户求助记录通过", key = "name")
	@RequiresPermissions(value = "poolResortMessagExamineTrue")
	public Ret examineTrue(Long id){
		PoolResortMessagDto dto =new PoolResortMessagDto();
		dto.setId(id);
		dto.setState(2);
		return poolResortMessagService.examine(dto);
	}

	@GetMapping("examineFalse")
	@BussinessLog(value = "审核用户求助记录拒绝", key = "name")
	@RequiresPermissions(value = "poolResortMessagExamineFalse")
	public Ret examineFalse(@RequestParam("id") Long id){
		PoolResortMessagDto dto =new PoolResortMessagDto();
		dto.setId(id);
		dto.setState(1);
		return poolResortMessagService.examine(dto);
	}
}