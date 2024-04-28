package cn.rh.flash.api.controller.dz.dzcredit;


import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzcredit.CreditRecord;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.service.dzcredit.CreditRecordService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.dzuser.UserInfoService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.StringUtil;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.CreditRecordWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/dzcredit/creditrecord")
public class CreditRecordController extends BaseController {

    @Autowired
    private CreditRecordService creditRecordService;

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping(value = "/list")
    @RequiresPermissions(value = "creditRecord")
    public Ret list(@RequestParam(required = false) String chargeStatus, @RequestParam(required = false) String account,
                    @RequestParam(required = false) String fromAccount,@RequestParam(required = false) String upAccount,
                    @RequestParam(required = false) String expireTimes,@RequestParam(required = false) String expireTimee) {
        Page<CreditRecord> page = new PageFactory<CreditRecord>().defaultPage();
        page.addFilter("chargeStatus", chargeStatus);
        page.addFilter("account", account);
        page.addFilter("fromAccount", fromAccount);

        if (isProxy()){
            page.addFilter("sourceInvitationCode",getUcode());
        }
        if (StringUtil.isNotEmpty(upAccount)){
            UserInfo userInfo = userInfoService.get(SearchFilter.build("account", upAccount));
            if (userInfo == null){
                return Rets.success(page);
            }
            page.addFilter("sourceInvitationCode", userInfo.getSourceInvitationCode());
        }

        if( StringUtil.isNotEmpty( expireTimes ) && StringUtil.isNotEmpty( expireTimee ) ) {

            page.addFilter( "createTime", SearchFilter.Operator.GTE,  DateUtil.parseTime(expireTimes ) );
            page.addFilter( "createTime", SearchFilter.Operator.LT,  DateUtil.parseTime(expireTimee ) );
        }

        page = creditRecordService.queryPage(page);
        List list = (List) new CreditRecordWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
        page.setRecords(list);
        return Rets.success(page);
    }

    @PostMapping
    @BussinessLog(value = "新增信誉分变动记录", key = "name")
    @RequiresPermissions(value = "creditRecordAdd")
    public Ret add(@Valid @RequestBody CreditRecord creditRecord) {

        creditRecord.setIdw(new IdWorker().nextId() + "");
        creditRecordService.insert(creditRecord);
        sysLogService.addSysLog(getUsername(), creditRecord.getId(), "", "PC", SysLogEnum.ADD_CREDITRECORD);
        return Rets.success();
    }

    @PutMapping
    @BussinessLog(value = "更新信誉分变动记录", key = "name")
    @RequiresPermissions(value = "creditRecordUpdate")
    public Ret update(@RequestBody CreditRecord creditRecord) {
        creditRecordService.update(creditRecord);
        sysLogService.addSysLog(getUsername(), creditRecord.getId(), "", "PC", SysLogEnum.UPDATE_CREDITRECORD);
        return Rets.success();
    }

    @DeleteMapping
    @BussinessLog(value = "删除信誉分变动记录", key = "id")
    @RequiresPermissions(value = "creditRecordDelete")
    public Ret remove(Long id) {
        if (id == null) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        creditRecordService.delete(id);
        sysLogService.addSysLog(getUsername(), id, "", "PC", SysLogEnum.DELETE_CREDITRECORD);
        return Rets.success();
    }
}
