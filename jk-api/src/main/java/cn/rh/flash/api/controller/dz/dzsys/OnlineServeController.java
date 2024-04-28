package cn.rh.flash.api.controller.dz.dzsys;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzsys.OnlineServe;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.dzsys.OnlineServeService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.OnlineServeWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/dzsys/onlineserve")
public class OnlineServeController extends BaseController {
	private  Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private OnlineServeService onlineServeService;
	@Autowired
	private SysLogService sysLogService;
	@GetMapping(value = "/list")
	@RequiresPermissions(value = "onlineServe")
	public Ret list(@RequestParam(required = false) Long id) {
		Page<OnlineServe> page = new PageFactory<OnlineServe>().defaultPage();
		page.addFilter("id",id);
		page = onlineServeService.queryPage(page);

		List list = (List) new OnlineServeWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
		page.setRecords(list);
		return Rets.success(page);
	}
	@PostMapping
	@BussinessLog(value = "新增在线客服", key = "name")
	@RequiresPermissions(value = "onlineServeAdd")
	public Ret add( @Valid @RequestBody OnlineServe onlineServe){

		onlineServeService.insert(onlineServe);
		sysLogService.addSysLog(getUsername(),onlineServe.getId(),"","PC", SysLogEnum.ADD_ONLINE_SERVE_INFO);
		return Rets.success();
	}
	@PutMapping
	@BussinessLog(value = "更新在线客服", key = "name")
	@RequiresPermissions(value = "onlineServeUpdate")
	public Ret update( @Valid @RequestBody OnlineServe onlineServe){
		onlineServeService.update(onlineServe);
		sysLogService.addSysLog(getUsername(),onlineServe.getId(),"","PC", SysLogEnum.UPDATE_ONLINE_SERVE_INFO);
		return Rets.success();
	}
	@DeleteMapping
	@BussinessLog(value = "删除在线客服", key = "id")
	@RequiresPermissions(value = "onlineServeDelete")
	public Ret remove(Long id){
		if (id == null) {
			throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
		}
		onlineServeService.delete(id);
		sysLogService.addSysLog(getUsername(),id,"","PC", SysLogEnum.DELETE_ONLINE_SERVE_INFO);
		return Rets.success();
	}
}