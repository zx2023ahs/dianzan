package cn.rh.flash.api.controller.dz.dzcredit;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.dto.UserCreditDto;
import cn.rh.flash.bean.entity.dzcredit.UserCredit;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.dzcredit.UserCreditService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.system.FileService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.UserCreditWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/dzcredit/usercredit")
public class UserCreditController extends BaseController {

    @Autowired
    private UserCreditService userCreditService;

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private FileService fileService;

    @GetMapping(value = "/list")
    @RequiresPermissions(value = "userCredit")
    public Ret list(@RequestParam(required = false) String account,@RequestParam(required = false) String status,
                    @RequestParam(required = false) String vipType,
                    @RequestParam(required = false) String orderField,@RequestParam(required = false) String orderName) {
        Page<UserCredit> page = new PageFactory<UserCredit>().defaultPage();
        if (isProxy()){
            page.addFilter("sourceInvitationCode",getUcode());
        }
        page.addFilter("account", account);
        page.addFilter("status", status);
        page.addFilter("vipType", vipType);

        List<Sort.Order> orders = new ArrayList<>();
        if (StringUtils.isNotBlank(orderField)&&StringUtils.isNotBlank(orderName)){
            if (orderName.equals("ascending")){
                orders.add(new Sort.Order(Sort.Direction.ASC,orderField));
            }
            if (orderName.equals("descending")){
                orders.add(new Sort.Order(Sort.Direction.DESC,orderField));
            }
        }
        orders.add(new Sort.Order(Sort.Direction.DESC,"id"));
        page.setSort(Sort.by(orders));

        page = userCreditService.queryPage(page);
        List list = (List) new UserCreditWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
        page.setRecords(list);
        return Rets.success(page);
    }

    @PostMapping
    @BussinessLog(value = "新增用户信誉分", key = "name")
    @RequiresPermissions(value = "userCreditAdd")
    public Ret add(@Valid @RequestBody UserCredit userCredit) {

        userCredit.setIdw(new IdWorker().nextId() + "");
        userCreditService.insert(userCredit);
        sysLogService.addSysLog(getUsername(), userCredit.getId(), "", "PC", SysLogEnum.ADD_USERCREDIT);
        return Rets.success();
    }

    @PutMapping
    @BussinessLog(value = "更新用户信誉分", key = "name")
    @RequiresPermissions(value = "userCreditUpdate")
    public Ret update(@RequestBody UserCredit userCredit) {
        userCreditService.update(userCredit);
        sysLogService.addSysLog(getUsername(), userCredit.getId(), "", "PC", SysLogEnum.UPDATE_USERCREDIT);
        return Rets.success();
    }

    @DeleteMapping
    @BussinessLog(value = "删除用户信誉分", key = "id")
    @RequiresPermissions(value = "userCreditDelete")
    public Ret remove(Long id) {
        if (id == null) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        userCreditService.delete(id);
        sysLogService.addSysLog(getUsername(), id, "", "PC", SysLogEnum.DELETE_USERCREDIT);
        return Rets.success();
    }

    @PostMapping(value = "/upOrDownCredit")
    @BussinessLog(value = "信誉分上下分", key = "name")
    public Ret upOrDownCredit(@RequestBody UserCreditDto userCreditDto){


        String s = userCreditService.upOrDownCredit(userCreditDto,getUsername());
        if ("OK".equals(s)){
            sysLogService.addSysLog(getUsername(), null, "PC", SysLogEnum.UPORDOWNCREDIT,getUsername()+"--在"+ DateUtil.getTime() +"--"+("1".equals(userCreditDto.getIsAdd())?"上":"下")+"信誉分"+userCreditDto.getCredit()+", 操作账号:"+userCreditDto.getAccount());
            return Rets.success();
        }
        return Rets.failure(s);
    }

    @GetMapping(value = "/exportXlsV2")
    public void exportXlsV2(HttpServletResponse response,
                           @RequestParam(required = false) String account, @RequestParam(required = false) String status,
                           @RequestParam(required = false) String orderField, @RequestParam(required = false) String orderName) {
        Page<UserCredit> page = new PageFactory<UserCredit>().defaultPage();
        if (isProxy()){
            page.addFilter("sourceInvitationCode",getUcode());
        }
        page.addFilter("account", account);
        page.addFilter("status", status);

        List<Sort.Order> orders = new ArrayList<>();
        if (StringUtils.isNotBlank(orderField)&&StringUtils.isNotBlank(orderName)){
            if (orderName.equals("ascending")){
                orders.add(new Sort.Order(Sort.Direction.ASC,orderField));
            }
            if (orderName.equals("descending")){
                orders.add(new Sort.Order(Sort.Direction.DESC,orderField));
            }
        }
        orders.add(new Sort.Order(Sort.Direction.DESC,"id"));
        page.setSort(Sort.by(orders));
        page = userCreditService.queryPage(page);
        List<Map<String,Object>> list = (List<Map<String,Object>>) new UserCreditWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
        if (ObjUtil.isEmpty(page) || CollUtil.isEmpty(list)){
            throw new RuntimeException("查询为空");
        }
        userCreditService.exportXlsV2(response,list);
    }


}
