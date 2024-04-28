package cn.rh.flash.api.controller.system;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.Const;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.system.Role;
import cn.rh.flash.bean.entity.system.User;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.Permission;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.node.Node;
import cn.rh.flash.bean.vo.node.ZTreeNode;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.system.LogObjectHolder;
import cn.rh.flash.service.system.RoleService;
import cn.rh.flash.service.system.UserService;
import cn.rh.flash.service.system.impl.ConstantFactory;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.Convert;
import cn.rh.flash.utils.Maps;
import cn.rh.flash.utils.StringUtil;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.RoleWrapper;
import com.google.common.collect.Lists;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 角色
 */
@RestController
@RequestMapping("/role")
public class RoleController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private RoleService roleService;
    @Autowired
    private UserService userService;
    @Autowired
    private SysLogService sysLogService;
    @GetMapping(value = "/list")
    @RequiresPermissions(value = {Permission.ROLE})
    public Object list(@RequestParam(required = false) String name,
                       @RequestParam(required = false) String code) {

        Page page = new PageFactory().defaultPage();
        page.addFilter("name", name);
        page.addFilter("code", code);
        page = roleService.queryPage(page);
        List list = (List) new RoleWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
        page.setRecords(list);
        return Rets.success(page);
    }

    @PostMapping
    @BussinessLog(value = "编辑角色", key = "name")
    @RequiresPermissions(value = {Permission.ROLE_EDIT})
    public Object save(  @RequestBody @Valid Role role) {
        if (role.getId() == null) {
            roleService.insert(role);
        } else {
            roleService.update(role);
        }
        return Rets.success();
    }

    @DeleteMapping
    @BussinessLog(value = "删除角色", key = "roleId")
    @RequiresPermissions(value = {Permission.ROLE_DEL})
    public Object remove(@RequestParam Long roleId) {
        logger.info("id:{}", roleId);
        if (roleId == null) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        if (roleId.intValue() < 4) {
            return Rets.failure("不能删除初始角色");
        }
        List<User> userList = userService.queryAll(SearchFilter.build("roleid", SearchFilter.Operator.EQ, String.valueOf(roleId)));
        if (!userList.isEmpty()) {
            return Rets.failure("有用户使用该角色，禁止删除");
        }
        //不能删除超级管理员角色
        if (roleId.intValue() == Const.ADMIN_ROLE_ID) {
            return Rets.failure("禁止删除超级管理员角色");
        }
        //缓存被删除的角色名称
        LogObjectHolder.me().set(ConstantFactory.me().getSingleRoleName(roleId));
        roleService.delRoleById(roleId);
        return Rets.success();
    }

    @PostMapping(value = "/savePermisson")
    @BussinessLog(value = "配置角色权限", key = "roleId")
    @RequiresPermissions(value = {Permission.ROLE_EDIT})
    public Object setAuthority(Long roleId, String
            permissions) {
        if (BeanUtil.isOneEmpty(roleId)) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        roleService.setAuthority(roleId, permissions);
        return Rets.success();
    }


    /**
     * 获取角色树
     */
    @GetMapping(value = "/roleTreeListByIdUser")
    @RequiresPermissions(value = {Permission.ROLE})
    public Object roleTreeListByIdUser(Long idUser) {
        User user = userService.get(idUser);
        String roleIds = user.getRoleid();
        List<ZTreeNode> roleTreeList = null;
        if (StringUtil.isEmpty(roleIds)) {
            roleTreeList = roleService.roleTreeList();
        } else {
            Long[] roleArray = Convert.toLongArray(",", roleIds);
            roleTreeList = roleService.roleTreeListByRoleId(roleArray);

        }
        List<Node> list = roleService.generateRoleTree(roleTreeList);
        List<Long> checkedIds = Lists.newArrayList();
        for (ZTreeNode zTreeNode : roleTreeList) {
            if (zTreeNode.getChecked() != null && zTreeNode.getChecked()) {
                checkedIds.add(zTreeNode.getId());
            }
        }
        return Rets.success(Maps.newHashMap("treeData", list, "checkedIds", checkedIds));
    }
}
