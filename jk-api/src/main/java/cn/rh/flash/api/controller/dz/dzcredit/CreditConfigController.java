package cn.rh.flash.api.controller.dz.dzcredit;


import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzcredit.CreditConfig;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.dzcredit.CreditConfigService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.factory.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/dzcredit/creditconfig")
public class CreditConfigController extends BaseController {
    
    @Autowired
    private CreditConfigService creditConfigService;

    @Autowired
    private SysLogService sysLogService;

    @GetMapping(value = "/list")
    @RequiresPermissions(value = "creditConfig")
    public Ret list(@RequestParam(required = false) String idw ) {
        Page<CreditConfig> page = new PageFactory<CreditConfig>().defaultPage();
        page.addFilter("idw",idw );
        page = creditConfigService.queryPage(page);
        return Rets.success(page);
    }
    @PostMapping
    @BussinessLog(value = "新增信誉分配置", key = "name")
    @RequiresPermissions(value = "creditConfigAdd")
    public Ret add( @Valid @RequestBody CreditConfig creditConfig){
        creditConfig.setIdw(new IdWorker().nextId()+"");
        creditConfigService.insert(creditConfig);
        sysLogService.addSysLog(getUsername(),creditConfig.getId(),"","PC", SysLogEnum.ADD_CREDITCONFIG);
        return Rets.success();
    }
    @PutMapping
    @BussinessLog(value = "更新信誉分配置", key = "name")
    @RequiresPermissions(value = "creditConfigUpdate")
    public Ret update(@RequestBody CreditConfig creditConfig){
        creditConfigService.update(creditConfig);
        sysLogService.addSysLog(getUsername(),creditConfig.getId(),"","PC", SysLogEnum.UPDATE_CREDITCONFIG);
        return Rets.success();
    }
    @DeleteMapping
    @BussinessLog(value = "删除信誉分配置", key = "id")
    @RequiresPermissions(value = "creditConfigDelete")
    public Ret remove(Long id){
        if (id == null) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        creditConfigService.delete(id);
        sysLogService.addSysLog(getUsername(),id,"","PC", SysLogEnum.DELETE_CREDITCONFIG);
        return Rets.success();
    }
}
