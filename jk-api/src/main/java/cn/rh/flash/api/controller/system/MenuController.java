package cn.rh.flash.api.controller.system;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.system.Menu;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.Permission;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.node.MenuNode;
import cn.rh.flash.bean.vo.node.Node;
import cn.rh.flash.bean.vo.node.TreeSelectNode;
import cn.rh.flash.bean.vo.node.ZTreeNode;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.system.LogObjectHolder;
import cn.rh.flash.service.system.MenuService;
import cn.rh.flash.service.system.impl.ConstantFactory;
import cn.rh.flash.utils.Maps;
import cn.rh.flash.utils.StringUtil;
import com.google.common.collect.Lists;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 菜单
 */
@RestController
@RequestMapping("/menu")
public class MenuController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(MenuController.class);
    @Autowired
    private MenuService menuService;
    @Autowired
    private SysLogService sysLogService;
    @GetMapping(value = "/list")
    public Object list() {
        List<MenuNode> list = menuService.getMenus();
        return Rets.success(list);
    }

    @GetMapping(value = "/tree")
    public Object tree() {
        List<MenuNode> list = menuService.getMenus();
        List<TreeSelectNode> treeSelectNodes = Lists.newArrayList();
        for (MenuNode menuNode : list) {
            TreeSelectNode tsn = transfer(menuNode);
            treeSelectNodes.add(tsn);
        }
        return Rets.success(treeSelectNodes);
    }

    public TreeSelectNode transfer(MenuNode node) {
        TreeSelectNode tsn = new TreeSelectNode();
        tsn.setId(node.getCode());
        tsn.setLabel(node.getName());
        if (node.getChildren() != null && !node.getChildren().isEmpty()) {
            List<TreeSelectNode> children = Lists.newArrayList();
            for (MenuNode child : node.getChildren()) {
                children.add(transfer(child));
            }
            tsn.setChildren(children);
        }
        return tsn;
    }

    @PostMapping
    @BussinessLog(value = "编辑菜单", key = "name")
    @RequiresPermissions(value = {Permission.MENU_EDIT})
    public Object save( @RequestBody @Valid Menu menu) {
        //判断是否存在该编号
        if (menu.getId() == null) {
            String existedMenuName = ConstantFactory.me().getMenuNameByCode(menu.getCode());
            if (StringUtil.isNotEmpty(existedMenuName)) {
                throw new ApplicationException(BizExceptionEnum.EXISTED_THE_MENU);
            }
        }

        //设置父级菜单编号
        menuService.menuSetPcode(menu);
        if (menu.getId() == null) {
            menuService.insert(menu);
        } else {
            menuService.update(menu);
        }
        return Rets.success();
    }

    @DeleteMapping
    @BussinessLog(value = "删除菜单", key = "id")
    @RequiresPermissions(value = {Permission.MENU_DEL})
    public Object remove(@RequestParam Long id) {
        if (id == null) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        //不允许删除初始化的菜单
//        if (id.intValue() <= 80) {
//            return Rets.failure("演示环境不允许删除初始菜单");
//        }
        //缓存菜单的名称
        LogObjectHolder.me().set(ConstantFactory.me().getMenuName(id));
        menuService.delMenuContainSubMenus(id);
        return Rets.success();
    }

    /**
     * 获取菜单树
     */
    @GetMapping(value = "/menuTreeListByRoleId")
    @RequiresPermissions(value = {Permission.MENU})
    public Object menuTreeListByRoleId(Integer roleId) {
        List<Long> menuIds = menuService.getMenuIdsByRoleId(roleId);
        List<ZTreeNode> roleTreeList = null;
        if (menuIds == null || menuIds.isEmpty()) {
            roleTreeList = menuService.menuTreeList(null);
        } else {
            roleTreeList = menuService.menuTreeList(menuIds);

        }
        List<Node> list = menuService.generateMenuTreeForRole(roleTreeList);

        //element-ui中tree控件中如果选中父节点会默认选中所有子节点，所以这里将所有非叶子节点去掉
        Map<Long, ZTreeNode> map = cn.rh.flash.utils.Lists.toMap(roleTreeList, "id");
        Map<Long, List<ZTreeNode>> group = cn.rh.flash.utils.Lists.group(roleTreeList, "pId");
        for (Map.Entry<Long, List<ZTreeNode>> entry : group.entrySet()) {
            if (entry.getValue().size() > 1) {
                roleTreeList.remove(map.get(entry.getKey()));
            }
        }

        List<Long> checkedIds = Lists.newArrayList();
        for (ZTreeNode zTreeNode : roleTreeList) {
            if (zTreeNode.getChecked() != null && zTreeNode.getChecked()
                    && zTreeNode.getpId().intValue() != 0) {
                checkedIds.add(zTreeNode.getId());
            }
        }
        return Rets.success(Maps.newHashMap("treeData", list, "checkedIds", checkedIds));
    }
}
