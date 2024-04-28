package cn.rh.flash.api.controller.appv;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.appv.Appv;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.service.appv.AppvService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.AppvWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/app/appv")
public class AppvController extends BaseController {
	private  Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private AppvService appvService;

	@GetMapping(value = "/list")
	@RequiresPermissions(value = "appv")
	public Ret list( @RequestParam(required = false) String versionNumber, @RequestParam(required = false) String appType,
					 @RequestParam(required = false) Integer dzstatus ) {
		Page<Appv> page = new PageFactory<Appv>().defaultPage();
 		page.addFilter("versionNumber", SearchFilter.Operator.LIKE,versionNumber);
		page.addFilter("appType", SearchFilter.Operator.EQ,appType);
		page.addFilter("dzstatus", SearchFilter.Operator.EQ,dzstatus);
		page = appvService.queryPage(page);

		List list = (List) new AppvWrapper( BeanUtil.objectsToMaps(page.getRecords())).warp();
		page.setRecords(list);
		return Rets.success(page);
	}
	@PostMapping
	@BussinessLog(value = "新增版本更新", key = "name")
	@RequiresPermissions(value = "appvAdd")
	public Ret add(@RequestBody Appv appv){

		if (appv.getDzstatus() == 1) {
			changeOldEdition( appv );
		}
		appv.setIdw( new IdWorker().nextId()+"" );
		appvService.insert(appv);
		return Rets.success();
	}

	@PutMapping
	@BussinessLog(value = "更新版本更新", key = "name")
	@RequiresPermissions(value = "appvUpdate")
	public Ret update(@RequestBody Appv appv){
		if (appv.getDzstatus() == 1) {
			changeOldEdition( appv );
		}
		appvService.update(appv);
		return Rets.success();
	}
	@DeleteMapping
	@BussinessLog(value = "删除版本更新", key = "id")
	@RequiresPermissions(value = "appvDelete")
	public Ret remove(Long id){
		if (id == null) {
			throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
		}
		appvService.delete(id);
		return Rets.success();
	}

	/**
	 * 变更以往版本状态
	 * @param appv
	 */
	private void changeOldEdition(@RequestBody Appv appv) {
		List<SearchFilter> lists = new ArrayList<>();
		lists.add(  SearchFilter.build( "appType", appv.getAppType() )  );
		lists.add(  SearchFilter.build( "dzstatus", 1 )  );
		List<Appv> appvs = appvService.queryAll( lists );
		for (Appv appv1 : appvs) {
			appv1.setDzstatus( 2 );
			appvService.update( appv1 );
		}
	}
}