package cn.rh.flash.api.controller.system;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.api.utils.IpToCity.IpdbUtil;
import cn.rh.flash.bean.constant.Const;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.constant.state.ManagerStatus;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.dto.UserDto;
import cn.rh.flash.bean.entity.system.User;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.Permission;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.core.factory.UserFactory;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.system.UserService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.Google.GoogleAuthenticator;
import cn.rh.flash.utils.MD5;
import cn.rh.flash.utils.RandomUtil;
import cn.rh.flash.utils.RegUtil;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.UserWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 账号
 */
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;
    @Autowired
    private SysLogService sysLogService;
    @Autowired
    private ConfigCache configCache;

    @GetMapping(value = "/list")
    @RequiresPermissions(value = {Permission.USER})
    public Object list(@RequestParam(required = false) String account,
                       @RequestParam(required = false) String name,
                       @RequestParam(required = false) Long deptid,
                       @RequestParam(required = false) String phone,
                       @RequestParam(required = false) Integer status,
                       @RequestParam(required = false) Integer sex
    ) {
        Page page = new PageFactory().defaultPage();
        page.addFilter("name", SearchFilter.Operator.LIKE, name);
        page.addFilter("account", SearchFilter.Operator.LIKE, account);
        page.addFilter("deptid", deptid);
        page.addFilter("phone", phone);
        page.addFilter("status", status);
        page.addFilter("status", SearchFilter.Operator.GT, 0);
        page.addFilter("sex", sex);
        page = userService.queryPage(page);

        List list = (List) new UserWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
        page.setRecords(list);
        return Rets.success(page);
    }

    @PostMapping
    @BussinessLog(value = "编辑账号", key = "name")
    @RequiresPermissions(value = {Permission.USER_EDIT})
    public Object save(  @RequestBody @Valid UserDto user, BindingResult result) throws Exception {

        //验证区号
        if ( !RegUtil.isValidCountryCode(user.getPhone()) )  {
            return Rets.failure( "国家码错误");
        }

        if (user.getId() == null) {
            // 判断账号是否重复
            User theUser = userService.findByAccount(user.getAccount());
            if (theUser != null) {
                throw new ApplicationException(BizExceptionEnum.USER_ALREADY_REG);
            }
            // 完善账号信息
//            user.setPhone(user.getPhone()+"-"+user.getAccount());
            user.setSalt( RandomUtil.getRandomString(5) );
            String password = user.getPassword();
            user.setPassword(MD5.md5(password, user.getSalt()));
            user.setStatus(ManagerStatus.OK.getCode());
            // 的开头邀请码
            user.setUcode( String.format("d%s", RandomUtil.getRandomString(5) )  );
            // 谷歌验证密钥
            user.setAuthenticatorPassword( GoogleAuthenticator.generateSecretKey() );

            User userobj = UserFactory.createUser(user, new User());

//            String[] split = userobj.getPhone().trim().split("-");
//            if ( split.length != 2 ) {
//                return Rets.failure("请输入正确电话格式" );
//            }
            /* 创建用户 */
            userService.insertAll(userobj,password, IpdbUtil.findCity(configCache));
        } else {
            User oldUser = userService.get(user.getId());
            userService.update(UserFactory.updateUser(user, oldUser));

            userService.updateAll(oldUser);
        }
        return Rets.success();
    }



    @BussinessLog(value = "删除账号", key = "userId")
    @DeleteMapping
    @RequiresPermissions(value = {Permission.USER_DEL})
    public Object remove(@RequestParam Long userId) {
        if (userId == null) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        if (userId.intValue() <= 1) {
            return Rets.failure("不能删除初始用户");
        }
        User user = userService.get(userId);
        user.setStatus(ManagerStatus.DELETED.getCode());
        userService.update(user);
        return Rets.success();
    }

    @BussinessLog(value = "设置账号角色", key = "userId")
    @PostMapping(value = "/setRole")
    @RequiresPermissions(value = {Permission.USER_EDIT})
    public Object setRole(@RequestParam("userId") Long userId, @RequestParam("roleIds") String roleIds) {
        if (BeanUtil.isOneEmpty(userId, roleIds)) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        //不能修改超级管理员
        if (userId.intValue() == Const.ADMIN_ID.intValue()) {
            return Rets.failure("不能修改超级管理员得角色");
        }
        User user = userService.get(userId);
        user.setRoleid(roleIds);
        userService.update(user);
        return Rets.success();
    }

    @BussinessLog(value = "冻结/解冻账号", key = "userId")
    @GetMapping(value = "changeStatus")
    @RequiresPermissions(value = {Permission.USER_EDIT})
    public Object changeStatus(@RequestParam Long userId) {
        if (userId == null) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        if ( userId.intValue() == Const.ADMIN_ID.intValue() ) {
            return Rets.failure("不能冻结初始用户");
        }
        User user = userService.get(userId);
        user.setStatus(user.getStatus().intValue() == ManagerStatus.OK.getCode() ? ManagerStatus.FREEZED.getCode() : ManagerStatus.OK.getCode());
        userService.update(user);
        return Rets.success();
    }
    @BussinessLog(value = "重置密码", key = "userId")
    @PostMapping(value="resetPassword")
    public Object resetPassword(Long userId){
        User user = userService.get(userId);
        user.setPassword(MD5.md5(Const.ADMIN_PWD, user.getSalt()));
        userService.update(user);
        return Rets.success();
    }
}
