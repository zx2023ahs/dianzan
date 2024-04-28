package cn.rh.flash.api.controller.system;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.system.Dict;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.Permission;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.cache.DictCache;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.system.DictService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.StringUtil;
import cn.rh.flash.warpper.DictWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典
 */
@RestController
@RequestMapping("/dict")
public class DictController extends BaseController {
    @Autowired
    private DictService dictService;
    @Autowired
    private DictCache dictCache;
    @Autowired
    private SysLogService sysLogService;
    /**
     * 获取所有字典列表
     */
    @GetMapping(value = "/list")
    @RequiresPermissions(value = {Permission.DICT})
    public Object list(String name) {

        if (StringUtil.isNotEmpty(name)) {
            List<Dict> list = dictService.findByNameLike(name);
            return Rets.success(new DictWrapper(BeanUtil.objectsToMaps(list)).warp());
        }
        List<Dict> list = dictService.findByPid(0L);
        return Rets.success(new DictWrapper(BeanUtil.objectsToMaps(list)).warp());
    }

    @PostMapping
    @BussinessLog(value = "添加字典", key = "dictName")
    @RequiresPermissions(value = {Permission.DICT_EDIT})
    public Object add(String dictName, String dictValues) {
        if (BeanUtil.isOneEmpty(dictName, dictValues)) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        dictService.addDict(dictName, dictValues);


        return Rets.success();
    }

    @PutMapping
    @BussinessLog(value = "修改字典", key = "dictName")
    @RequiresPermissions(value = {Permission.DICT_EDIT})
    public Object update(Long id, String dictName, String dictValues) {
        if (BeanUtil.isOneEmpty(dictName, dictValues)) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        dictService.editDict(id, dictName, dictValues);
        sysLogService.addSysLog(getUsername(),id,"","PC", SysLogEnum.UPDATE_DICT_INFO);
        return Rets.success();
    }


    @DeleteMapping
    @BussinessLog(value = "删除字典", key = "id")
    @RequiresPermissions(value = {Permission.DICT_EDIT})
    public Object delete(@RequestParam Long id) {
        dictService.delteDict(id);
        sysLogService.addSysLog(getUsername(),id,"","PC", SysLogEnum.DELETE_DICT_INFO);
        return Rets.success();
    }

    @GetMapping(value = "/getDicts/{dictName}")
    public Object getDicts(@PathVariable("dictName") String dictName) {
        List<Dict> dicts = dictCache.getDictsByPname(dictName);
        if( dictName.equals( "审核状态" ) ){
            dicts.remove( 1 );
        }
        return Rets.success(dicts);
    }
}
