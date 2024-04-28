package cn.rh.flash.api.controller.dz.dzsys;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzsys.MultilingualLang;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.service.dzsys.MultilingualLangService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.MultilingualLangWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/dzsys/mulutilinguallang")
public class MultilingualLangController extends BaseController {
	private  Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private MultilingualLangService multilingualLangService;

	@GetMapping(value = "/list")
	@RequiresPermissions(value = "multilingualLang")
	public Ret list(@RequestParam(required = false) String langKey,@RequestParam(required = false) String langCode,
					@RequestParam(required = false) String remark) {
		Page<MultilingualLang> page = new PageFactory<MultilingualLang>().defaultPage();
		page.addFilter("langKey", SearchFilter.Operator.LIKE,langKey);
		page.addFilter("langCode",langCode);
		page.addFilter("remark", SearchFilter.Operator.LIKE,remark);
		page = multilingualLangService.queryPage(page);

		List list = (List) new MultilingualLangWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
		page.setRecords(list);
		return Rets.success(page);
	}
	@PostMapping
	@BussinessLog(value = "新增多语言", key = "name")
	@RequiresPermissions(value = "multilingualLangAdd")
	public Ret add(@RequestBody MultilingualLang multilingualLang){

		List<SearchFilter> filters = new ArrayList<>();
		filters.add(SearchFilter.build("langKey",multilingualLang.getLangKey()));
		filters.add(SearchFilter.build("langCode",multilingualLang.getLangCode()));

		MultilingualLang multilingualLangDB = multilingualLangService.get(filters);
		if (multilingualLangDB !=null){
			return Rets.failure("当前多语言key与语种已有翻译");
		}

		multilingualLangService.insert(multilingualLang);
		return Rets.success();
	}
	@PutMapping
	@BussinessLog(value = "更新多语言", key = "name")
	@RequiresPermissions(value = "multilingualLangUpdate")
	public Ret update(@RequestBody MultilingualLang multilingualLang){
		List<SearchFilter> filters = new ArrayList<>();
		filters.add(SearchFilter.build("langKey",multilingualLang.getLangKey()));
		filters.add(SearchFilter.build("langCode",multilingualLang.getLangCode()));

		MultilingualLang multilingualLangDB = multilingualLangService.get(filters);
		if (multilingualLangDB !=null){
			return Rets.failure("当前多语言key与语种已有翻译");
		}

		multilingualLangService.update(multilingualLang);
		return Rets.success();
	}
	@DeleteMapping
	@BussinessLog(value = "删除多语言", key = "id")
	@RequiresPermissions(value = "multilingualLangDelete")
	public Ret remove(Long id){
		if (id == null) {
			throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
		}
		multilingualLangService.delete(id);
		return Rets.success();
	}
}