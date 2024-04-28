package cn.rh.flash.api.controller.dz.dzscore;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.entity.dzscore.SignIn;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.dzscore.SignInService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.utils.factory.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dzscore/signIn")
public class SignInController extends BaseController {

    @Autowired
    private SignInService signInService;

    @Autowired
    private SysLogService sysLogService;

    @GetMapping(value = "/list")
    @RequiresPermissions(value = "signIn")
    public Ret list(@RequestParam(required = false) String account) {
        Page<SignIn> page = new PageFactory<SignIn>().defaultPage();
        page.addFilter("account", account);
        page = signInService.queryPage(page);
        return Rets.success(page);
    }


}
