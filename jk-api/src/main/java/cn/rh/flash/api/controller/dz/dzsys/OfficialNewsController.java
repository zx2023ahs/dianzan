package cn.rh.flash.api.controller.dz.dzsys;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzsys.DzOfficialNews;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.dzsys.DzOfficialNewsService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.DzOfficialNewsWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/dzsys/officialnews")
public class OfficialNewsController extends BaseController {
	private  Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private DzOfficialNewsService dzOfficialNewsService;
	@Autowired
	private SysLogService sysLogService;
	@GetMapping(value = "/list")
	@RequiresPermissions(value = "officialNews")
	public Ret list( @RequestParam(required = false) String language,@RequestParam(required = false) Long officialType,@RequestParam(required = false) String title ) {

		Page<DzOfficialNews> page = new PageFactory<DzOfficialNews>().defaultPage();

		page.addFilter("language",language);
		page.addFilter("officialType",officialType);
		page.addFilter("title",title);
		page = dzOfficialNewsService.queryPage(page);

		List list = (List) new DzOfficialNewsWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
		page.setRecords(list);
		return Rets.success(page);
	}
	@PostMapping
	@BussinessLog(value = "新增公告信息", key = "name")
	@RequiresPermissions(value = "officialNewsAdd")
	public Ret add( @Valid @RequestBody DzOfficialNews dzOfficialNews){
		dzOfficialNews.setIdw( new IdWorker().nextId()+"" );

		dzOfficialNewsService.insert(dzOfficialNews);
		sysLogService.addSysLog(getUsername(),dzOfficialNews.getId(),"","PC", SysLogEnum.ADD_OFFICIAL_NEWS_INFO);
		return Rets.success();
	}
	@PutMapping
	@BussinessLog(value = "更新公告信息", key = "name")
	@RequiresPermissions(value = "officialNewsUpdate")
	public Ret update(@Valid @RequestBody DzOfficialNews dzOfficialNews){
		dzOfficialNewsService.update(dzOfficialNews);
		sysLogService.addSysLog(getUsername(),dzOfficialNews.getId(),"","PC", SysLogEnum.UPDATE_OFFICIAL_NEWS_INFO);
		return Rets.success();
	}
	@DeleteMapping
	@BussinessLog(value = "删除公告信息", key = "id")
	@RequiresPermissions(value = "officialNewsDelete")
	public Ret remove(Long id){
		if (id == null) {
			throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
		}

		dzOfficialNewsService.delete(id);
		sysLogService.addSysLog(getUsername(),id,"","PC", SysLogEnum.DELETE_OFFICIAL_NEWS_INFO);
		return Rets.success();
	}
}