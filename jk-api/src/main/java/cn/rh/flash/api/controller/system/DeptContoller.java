package cn.rh.flash.api.controller.system;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.system.Dept;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.Permission;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.node.DeptNode;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.system.DeptService;
import cn.rh.flash.service.system.LogObjectHolder;
import cn.rh.flash.utils.BeanUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/dept")
public class DeptContoller extends BaseController {
    private Logger logger = LoggerFactory.getLogger(MenuController.class);

    @Autowired
    private DeptService deptService;

    @Autowired
    private SysLogService sysLogService;
    @GetMapping(value = "/list")
    @RequiresPermissions(value = {Permission.DEPT})
    public Object list() {
        List<DeptNode> list = deptService.queryAllNode();
        return Rets.success(list);
    }

    @PostMapping
    @BussinessLog(value = "编辑部门", key = "simplename")
    @RequiresPermissions(value = {Permission.DEPT_EDIT})
    public Object save(@RequestBody @Valid Dept dept) {
        if (BeanUtil.isOneEmpty(dept, dept.getSimplename())) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        if (dept.getId() != null) {
            Dept old = deptService.get(dept.getId());
            LogObjectHolder.me().set(old);
            old.setPid(dept.getPid());
            old.setSimplename(dept.getSimplename());
            old.setFullname(dept.getFullname());
            old.setNum(dept.getNum());
            deptService.deptSetPids(old);
            deptService.update(old);

            sysLogService.addSysLog(getUsername(),dept.getId(),"","PC", SysLogEnum.UPDATE_DEPT_INFO);
        } else {
            deptService.deptSetPids(dept);
            deptService.insert(dept);
            sysLogService.addSysLog(getUsername(),dept.getId(),"","PC", SysLogEnum.ADD_DEPT_INFO);
        }
        return Rets.success();
    }

    @DeleteMapping
    @BussinessLog(value = "删除部门", key = "id")
    @RequiresPermissions(value = {Permission.DEPT_DEL})
    public Object remove(@RequestParam Long id) {
        if (id == null) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        if(id<2){
            return Rets.failure("请联系管理员");
        }
        deptService.deleteDept(id);
        sysLogService.addSysLog(getUsername(),id,"","PC", SysLogEnum.DELETE_DEPT_INFO);
        return Rets.success();
    }
}
