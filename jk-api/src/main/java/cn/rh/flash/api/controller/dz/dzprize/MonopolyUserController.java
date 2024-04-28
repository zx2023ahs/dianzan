package cn.rh.flash.api.controller.dz.dzprize;


import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzprize.MonopolyUser;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.dzprize.MonopolyUserService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.MonopolyUserWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/dzprize/monopolyUser")
public class MonopolyUserController extends BaseController {

    @Autowired
    private MonopolyUserService monopolyUserService;

    @Autowired
    private SysLogService sysLogService;

    @GetMapping(value = "/list")
    @RequiresPermissions(value = "monopolyUser")
    public Ret list(@RequestParam(required = false) String account) {
        Page<MonopolyUser> page = new PageFactory<MonopolyUser>().defaultPage();
        page.addFilter("account",account );
        page = monopolyUserService.queryPage(page);
        List list = (List) new MonopolyUserWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
        page.setRecords(list);
        return Rets.success(page);
    }
    @PostMapping
    @BussinessLog(value = "新增抽奖活动", key = "name")
    @RequiresPermissions(value = "monopolyUserAdd")
    public Ret add( @Valid @RequestBody MonopolyUser monopolyUser){
        monopolyUser.setIdw(new IdWorker().nextId()+"");
        monopolyUserService.insert(monopolyUser);
        sysLogService.addSysLog(getUsername(),monopolyUser.getId(),"","PC", SysLogEnum.ADD_MONOPOLY_USER);
        return Rets.success();
    }
    @PutMapping
    @BussinessLog(value = "更新抽奖活动", key = "name")
    @RequiresPermissions(value = "monopolyUserUpdate")
    public Ret update(@RequestBody MonopolyUser monopolyUser){
        monopolyUserService.update(monopolyUser);
        sysLogService.addSysLog(getUsername(),monopolyUser.getId(),"","PC", SysLogEnum.UPDATE_MONOPOLY_USER);
        return Rets.success();
    }
    @DeleteMapping
    @BussinessLog(value = "删除抽奖活动", key = "id")
    @RequiresPermissions(value = "monopolyUserDelete")
    public Ret remove(Long id){
        if (id == null) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        monopolyUserService.delete(id);
        sysLogService.addSysLog(getUsername(),id,"","PC", SysLogEnum.DELETE_MONOPOLY_USER);
        return Rets.success();
    }

    @PostMapping(value = "/resettingUserState")
    @BussinessLog(value = "重置大富翁活动", key = "name")
    @RequiresPermissions(value = "resettingUserState")
    public Ret resettingUserState(){
        monopolyUserService.resettingUserState();
        sysLogService.addSysLog(getUsername(),getIdUser(),"","PC", SysLogEnum.ADD_MONOPOLY_USER);
        return Rets.success();
    }



}
