package cn.rh.flash.api.controller.dz.dzsys;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzsys.UserIpPermissions;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.dzsys.UserIpPermissionsService;
import cn.rh.flash.utils.factory.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/dzsys/userIpPermissions")
public class UserIpPermissionsController extends BaseController {
	private  Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private UserIpPermissionsService userIpPermissionsService;
	@Autowired
	private SysLogService sysLogService;
	@GetMapping(value = "/list")
//	@RequiresPermissions(value = "userIpPermissions")
	public Ret list(@RequestParam(required = false) String ip) {
		Page<UserIpPermissions> page = new PageFactory<UserIpPermissions>().defaultPage();
		page.addFilter("ip",ip);
		page = userIpPermissionsService.queryPage(page);
		return Rets.success(page);
	}

	@PostMapping
	@BussinessLog(value = "新增用户IP权限", key = "name")
//	@RequiresPermissions(value = "userIpPermissions")
	public Ret add( @Valid  @RequestBody UserIpPermissions userIpPermissions){
		userIpPermissionsService.insert(userIpPermissions);
		sysLogService.addSysLog(getUsername(),userIpPermissions.getId(),"","PC", SysLogEnum.ADD_USER_IP_PERMISSIONS_INFO);
		return Rets.success();
	}
	@PutMapping
	@BussinessLog(value = "更新用户IP权限", key = "name")
//	@RequiresPermissions(value = "userIpPermissions")
	public Ret update( @RequestBody UserIpPermissions userIpPermissions){
		userIpPermissionsService.update(userIpPermissions);
		sysLogService.addSysLog(getUsername(),userIpPermissions.getId(),"","PC", SysLogEnum.UPDATE_USER_IP_PERMISSIONS_INFO);
		return Rets.success();
	}
	@DeleteMapping
	@BussinessLog(value = "删除用户IP权限", key = "id")
//	@RequiresPermissions(value = "userIpPermissions")
	public Ret remove(Long id){
		if (id == null) {
			throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
		}
		userIpPermissionsService.delete(id);
		sysLogService.addSysLog(getUsername(),id,"","PC", SysLogEnum.DELETE_USER_IP_PERMISSIONS_INFO);
		return Rets.success();
	}
}