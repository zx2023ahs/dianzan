package cn.rh.flash.api.controller.dz.dzuser;


import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.api.utils.IpToCity.IpdbUtil;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.dto.FalseDataForm;
import cn.rh.flash.bean.entity.dzuser.FalseData;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.dzuser.*;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.FalseDataWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dzuser/falsedata")
public class FalseDataController extends BaseController {

    @Autowired
    private WithdrawalsRecordService withdrawalsRecordService;

    @Autowired
    private TransactionRecordService transactionRecordService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private ConfigCache configCache;

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private FalseDataService falseDataService;

    @Autowired
    private RechargeRecordService rechargeRecordService;

    @GetMapping(value = "/list")
    @RequiresPermissions(value = "falseData")
    public Ret list(@RequestParam(required = false) String falseType) {
        Page<FalseData> page = new PageFactory<FalseData>().defaultPage();
        page.addFilter("falseType",falseType);
        page = falseDataService.queryPage(page);
        List list = (List) new FalseDataWrapper( BeanUtil.objectsToMaps( page.getRecords() ) ).warp();
        page.setRecords( list );
        return Rets.success(page);
    }

    @PostMapping(value = "/addWithFalse")
    @BussinessLog(value = "新增造假提现记录", key = "name")
    public Ret addWithFalse(@RequestBody FalseDataForm falseDataForm) {
        return withdrawalsRecordService.addWithFalse(falseDataForm,getUsername());
    }

    @PostMapping(value = "/addTranFalse")
    @BussinessLog(value = "新增交易提现记录", key = "name")
    public Ret addTranFalse(@RequestBody FalseDataForm falseDataForm) {
        return transactionRecordService.addTranFalse(falseDataForm,getUsername());
    }

    @PostMapping(value = "/addUser")
    @BussinessLog(value = "新增用户下级", key = "name")
    public Ret addUser(@RequestBody FalseDataForm falseDataForm) {
        return userInfoService.addUser(falseDataForm,getUsername(), IpdbUtil.findCity(configCache));
    }

    @PostMapping(value = "/addRechargeFalse")
    @BussinessLog(value = "新增充值记录", key = "name")
    public Ret addRechargeFalse(@RequestBody FalseDataForm falseDataForm) {
        return rechargeRecordService.addRechargeFalse(falseDataForm,getUsername());
    }

    @DeleteMapping
    @BussinessLog(value = "删除造假记录", key = "id")
    @RequiresPermissions(value = "falseData")
    public Ret delFalseData(Long id) {
        Ret ret = falseDataService.delFalseData(id);
        sysLogService.addSysLog(getUsername(),id,"","PC", SysLogEnum.DELETE_USER_BALANCE_INFO);
        return Rets.success(ret);
    }



}
