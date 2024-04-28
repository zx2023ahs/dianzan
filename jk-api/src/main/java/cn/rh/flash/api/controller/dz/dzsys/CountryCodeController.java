package cn.rh.flash.api.controller.dz.dzsys;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzsys.CountryCode;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.CacheDao;
import cn.rh.flash.cache.impl.EhcacheDao;
import cn.rh.flash.service.dzsys.CountryCodeService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.factory.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/dzsys/country")
public class CountryCodeController extends BaseController {
	private  Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private CountryCodeService countryCodeService;

	@Autowired
	private EhcacheDao ehcacheDao;
	@Autowired
	private SysLogService sysLogService;
	@GetMapping(value = "/list")
	//@RequiresPermissions(value = "countryCode")
	public Ret list(@RequestParam(required = false) String str) {
		Page<CountryCode> page = new PageFactory<CountryCode>().defaultPage();
		page.addFilter("countryName",str, SearchFilter.Join.or);
		page.addFilter("countryCode",str,SearchFilter.Join.or);
		page.addFilter("countryCodeNumber",str,SearchFilter.Join.or);
		page = countryCodeService.queryPage(page);
		return Rets.success(page);
	}

	@GetMapping(value = "/getListAll")
	public Ret getListAll() {
		return Rets.success(  countryCodeService.queryAll() );
	}

	@PostMapping
	@BussinessLog(value = "新增国家区号", key = "name")
	//@RequiresPermissions(value = "countryCodeAdd")
	public Ret add( @Valid  @RequestBody CountryCode countryCode){
		countryCode.setIdw( new IdWorker().nextId()+"" );
		countryCodeService.insert(countryCode);
		sysLogService.addSysLog(getUsername(),countryCode.getId(),"","PC", SysLogEnum.ADD_COUNTRY_CODE_INFO);
		ehcacheDao.ldel(CacheDao.COUNTRYCODE);
		return Rets.success();
	}
	@PutMapping
	@BussinessLog(value = "更新国家区号", key = "name")
	//@RequiresPermissions(value = "countryCodeUpdate")
	public Ret update( @RequestBody CountryCode countryCode){
		countryCodeService.update(countryCode);
		sysLogService.addSysLog(getUsername(),countryCode.getId(),"","PC", SysLogEnum.UPDATE_COUNTRY_CODE_INFO);
		ehcacheDao.ldel(CacheDao.COUNTRYCODE);
		return Rets.success();
	}
	@DeleteMapping
	@BussinessLog(value = "删除国家区号", key = "id")
	//@RequiresPermissions(value = "countryCodeDelete")
	public Ret remove(Long id){
		if (id == null) {
			throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
		}
		countryCodeService.delete(id);
		sysLogService.addSysLog(getUsername(),id,"","PC", SysLogEnum.DELETE_COUNTRY_CODE_INFO);
		ehcacheDao.ldel(CacheDao.COUNTRYCODE);
		return Rets.success();
	}
}