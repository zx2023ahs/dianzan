package cn.rh.flash.api.controller.dz.dzscore;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzscore.SignInSet;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.dzscore.SignInSetService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.factory.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/dzscore/signInSet")
public class SignInSetController extends BaseController {

    @Autowired
    private SignInSetService signInSetService;

    @Autowired
    private SysLogService sysLogService;

    @GetMapping(value = "/list")
    @RequiresPermissions(value = "signInSet")
    public Ret list(@RequestParam(required = false) String prizeName) {
        Page<SignInSet> page = new PageFactory<SignInSet>().defaultPage();
        page = signInSetService.queryPage(page);
        return Rets.success(page);
    }

    @PostMapping
    @BussinessLog(value = "新增签到设置", key = "name")
    @RequiresPermissions(value = "signInSetAdd")
    public Ret add(@Valid @RequestBody SignInSet prize) {
        prize.setIdw(new IdWorker().nextId() + "");
        signInSetService.insert(prize);
        sysLogService.addSysLog(getUsername(), prize.getId(), "", "PC", SysLogEnum.ADD_SIGN_SET);
        return Rets.success();
    }

    @PutMapping
    @BussinessLog(value = "更新签到设置", key = "name")
    @RequiresPermissions(value = "signInSetUpdate")
    public Ret update(@RequestBody SignInSet prize) {
        signInSetService.update(prize);
        sysLogService.addSysLog(getUsername(), prize.getId(), "", "PC", SysLogEnum.UPDATE_SIGN_SET);
        return Rets.success();
    }

    @DeleteMapping
    @BussinessLog(value = "删除签到设置", key = "id")
    @RequiresPermissions(value = "signInSetDelete")
    public Ret remove(Long id) {
        if (id == null) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        signInSetService.delete(id);
        sysLogService.addSysLog(getUsername(), id, "", "PC", SysLogEnum.DELETE_SIGN_SET);
        return Rets.success();
    }


}
