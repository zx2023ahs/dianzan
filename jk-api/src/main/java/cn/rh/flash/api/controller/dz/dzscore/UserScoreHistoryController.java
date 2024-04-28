package cn.rh.flash.api.controller.dz.dzscore;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzscore.UserScoreHistory;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.dzscore.UserScoreHistoryService;
import cn.rh.flash.utils.factory.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/dzscore/userscorehistory")
public class UserScoreHistoryController extends BaseController {
	private  Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private UserScoreHistoryService userScoreHistoryService;

	@GetMapping(value = "/list")
	@RequiresPermissions(value = "userScoreHistory")
	public Ret list(@RequestParam(required = false) String account,@RequestParam(required = false) Integer type) {
		Page<UserScoreHistory> page = new PageFactory<UserScoreHistory>().defaultPage();
		page.addFilter("account",account);
		page.addFilter("type",type);
		page = userScoreHistoryService.queryPage(page);
		return Rets.success(page);
	}




//	@PostMapping
//	@BussinessLog(value = "新增用户积分记录", key = "name")
//	@RequiresPermissions(value = "userScoreHistoryAdd")
//	public Ret add(@RequestBody UserScoreHistory userScoreHistory){
//		userScoreHistoryService.insert(userScoreHistory);
//		return Rets.success();
//	}
//	@PutMapping
//	@BussinessLog(value = "更新用户积分记录", key = "name")
//	@RequiresPermissions(value = "userScoreHistoryUpdate")
//	public Ret update(@RequestBody UserScoreHistory userScoreHistory){
//		userScoreHistoryService.update(userScoreHistory);
//		return Rets.success();
//	}
	@DeleteMapping
	@BussinessLog(value = "删除用户积分记录", key = "id")
	@RequiresPermissions(value = "userScoreHistoryDelete")
	public Ret remove(Long id){
		if (id == null) {
			throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
		}
		userScoreHistoryService.delete(id);
		return Rets.success();
	}
}