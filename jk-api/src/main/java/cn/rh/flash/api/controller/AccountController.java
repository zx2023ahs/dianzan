package cn.rh.flash.api.controller;

import cn.rh.flash.api.utils.ApiConstants;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.constant.state.ManagerStatus;
import cn.rh.flash.bean.core.ShiroUser;
import cn.rh.flash.bean.dto.LoginDto;
import cn.rh.flash.bean.entity.system.FileInfo;
import cn.rh.flash.bean.entity.system.User;
import cn.rh.flash.bean.enumeration.Permission;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.node.RouterMenu;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.TokenCache;
import cn.rh.flash.core.log.LogManager;
import cn.rh.flash.core.log.LogTaskFactory;
import cn.rh.flash.security.ShiroFactroy;
import cn.rh.flash.service.dzsys.UserIpPermissionsService;
import cn.rh.flash.service.system.FileService;
import cn.rh.flash.service.system.MenuService;
import cn.rh.flash.service.system.QrcodeService;
import cn.rh.flash.service.system.UserService;
import cn.rh.flash.utils.*;
import cn.rh.flash.utils.Google.GoogleAuthenticator;
import cn.rh.flash.utils.factory.Page;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.mapl.Mapl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AccountController
 */
@RestController
@RequestMapping("/account")
public class AccountController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private TokenCache tokenCache;

    @Autowired
    private MenuService menuService;
    @Autowired
    QrcodeService qrcodeService;

    @Autowired
    private FileService fileService;

    @Autowired
    private UserIpPermissionsService userIpPermissionsService;


    /**
     * 用户登录<br>
     * 1，验证没有注册<br>
     * 2，验证密码错误<br>
     * 3，登录成功
     *
     * @param loginDto
     * @return
     */
    @PostMapping(value = "/login")
    public Object login(@Valid @RequestBody LoginDto loginDto) {
        try {
            //1,
            String password = loginDto.getPassword();
            String userName = loginDto.getUsername();
            password = CryptUtil.desEncrypt(password);
            User user = userService.findByAccountForLogin(userName);
            // 当前用户验证器密码为"M45XH733VF26P5JL" 为超级管理员不验证IP
//            if (!"M45XH733VF26P5JL".equals(user.getAuthenticatorPassword())){
//                // 查询用户是否在IP白名单内
//                List<SearchFilter> filters = new ArrayList<>();
//                filters.add(SearchFilter.build("ip",HttpUtil.getIp()));
//                filters.add(SearchFilter.build("types","PC"));
//                filters.add(SearchFilter.build("blackOrWhite","WhiteList"));
//                UserIpPermissions userIpPermissions = userIpPermissionsService.get(filters);
//                // 当前IP不在白名单,无法登录
//                if (userIpPermissions==null){
//                    return Rets.failure( MessageTemplateEnum.THE_ACCOUNT_IS_DISABLED.getCode() , MessageTemplateEnum.THE_ACCOUNT_IS_DISABLED );
//                }
//            }

            if (user == null) {
                return Rets.failure("用户名或密码错误");
            }
            if (user.getStatus() == ManagerStatus.FREEZED.getCode()) {
                return Rets.failure("用户已冻结");
            } else if (user.getStatus() == ManagerStatus.DELETED.getCode()) {
                return Rets.failure("用户已删除");
            }
            String passwdMd5 = MD5.md5(password, user.getSalt());
            //2,
            if (!user.getPassword().equals(passwdMd5)) {
                return Rets.failure("用户名或密码错误");
            }
            //3, 谷歌验证器
            if (StringUtil.isNotEmpty(user.getAuthenticatorPassword())) {
                if (loginDto.getGgcode() == null) {
                    return Rets.failure("请输入验证码");
                }

                // 代理账号 验证码统一为1
                if (user.getDeptid() == 3){
                    if (loginDto.getGgcode() !=1){
                        return Rets.failure("验证码错误");
                    }
                }else {
                    if (!GoogleAuthenticator.check_code(user.getAuthenticatorPassword(), loginDto.getGgcode(), System.currentTimeMillis())) {
                        return Rets.failure("验证码错误");
                    }
                }

//                if (loginDto.getGgcode() != 147852369) { // 测试用

//                }

            }

            if (StringUtil.isEmpty(user.getRoleid())) {
                return Rets.failure("该用户未配置权限");
            }
            String token = userService.loginForToken(user);
            ShiroFactroy.me().shiroUser(token, user);
            Map<String, String> result = new HashMap<>(1);
            result.put("token", token);
            LogManager.me().executeLog(LogTaskFactory.loginLog(user.getId(), user.getName(), HttpUtil.getIp()));
            return Rets.success(result);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return Rets.failure("登录时失败");
    }

    @GetMapping(value = "/info")
    public Object info() {
        Long idUser = null;
        try {
            idUser = getIdUser();
        } catch (Exception e) {
            return Rets.expire();
        }
        if (idUser != null) {
            User user = userService.get(idUser);
            if (user == null) {
                //该用户可能被删除
                return Rets.expire();
            }
            if (StringUtil.isEmpty(user.getRoleid())) {
                return Rets.failure("该用户未配置权限");
            }
            ShiroUser shiroUser = tokenCache.getUser(getToken());
            Map map = Maps.newHashMap("name", user.getName(), "role", "admin", "roles", shiroUser.getRoleCodes());
            List<RouterMenu> list = menuService.getSideBarMenus(shiroUser.getRoleList());
            map.put("menus", list);
            map.put("permissions", shiroUser.getUrls());

            Map profile = (Map) Mapl.toMaplist(user);
            profile.put("dept", shiroUser.getDeptName());
            profile.put("roles", shiroUser.getRoleNames());
            map.put("profile", profile);

            return Rets.success(map);
        }
        return Rets.failure("获取用户信息失败");
    }

    @PostMapping(value = "/updatePwd")
    public Object updatePwd(String oldPassword, String password, String rePassword) {
        try {

            if (StringUtil.isEmpty(password) || StringUtil.isEmpty(rePassword)) {
                return Rets.failure("密码不能为空");
            }
            if (!password.equals(rePassword)) {
                return Rets.failure("新密码前后不一致");
            }
            User user = userService.get(getIdUser());
            if (ApiConstants.ADMIN_ACCOUNT.equals(user.getAccount())) {
                return Rets.failure("不能修改超级管理员密码");
            }
            if (!MD5.md5(oldPassword, user.getSalt()).equals(user.getPassword())) {
                return Rets.failure("旧密码输入错误");
            }

            user.setPassword(MD5.md5(password, user.getSalt()));
            userService.update(user);
            return Rets.success();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return Rets.failure("更改密码失败");
    }

    /**
     * 生成登录二维码
     *
     * @param response
     */
    @GetMapping("/qrcode/generate")
    public void generateQrcode(@RequestParam("uuid") String uuid, HttpServletResponse response) {
        BitMatrix bitMatrix = qrcodeService.createQrcode(uuid);
        response.setContentType("image/jpg");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        OutputStream stream = null;
        try {
            stream = response.getOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "jpg", stream);
        } catch (IOException e) {
            logger.error("generate QrCode error", e);
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                logger.error("close stream error", e);
            }
        }

    }


    @GetMapping(value = "/fileMgr/list")
    @RequiresPermissions(value = {Permission.FILE})
    public Object list(@RequestParam(required = false) String originalFileName) {

        Page<FileInfo> page = new PageFactory<FileInfo>().defaultPage();
        if (StringUtil.isNotEmpty(originalFileName)) {
            page.addFilter(SearchFilter.build("originalFileName", SearchFilter.Operator.LIKE, originalFileName));
        }
        page = fileService.queryPage(page);
        return Rets.success(page);
    }

}
