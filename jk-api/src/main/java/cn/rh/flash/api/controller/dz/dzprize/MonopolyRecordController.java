package cn.rh.flash.api.controller.dz.dzprize;


import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzprize.MonopolyRecord;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.service.dzprize.MonopolyRecordService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.StringUtil;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.MonopolyRecordWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/dzprize/monopolyRecord")
public class MonopolyRecordController extends BaseController {

    @Autowired
    private MonopolyRecordService monopolyRecordService;

    @Autowired
    private SysLogService sysLogService;

    @GetMapping(value = "/list")
    @RequiresPermissions(value = "monopolyRecord")
    public Ret list(@RequestParam(required = false) String account,
                    @RequestParam(required = false) String prizeType,
                    @RequestParam(required = false) String expireTimes,
                    @RequestParam(required = false) String expireTimee) {
        Page<MonopolyRecord> page = new PageFactory<MonopolyRecord>().defaultPage();
        page.addFilter("account",account );
        page.addFilter("prizeType", prizeType);
        if (StringUtil.isNotEmpty( expireTimes ) && StringUtil.isNotEmpty( expireTimee )) {
            expireTimes = DateUtil.getTimeByZone(expireTimes);
            expireTimee = DateUtil.getTimeByZone(expireTimee);
            page.addFilter( "modifyTime", SearchFilter.Operator.GTE, DateUtil.parseTime( expireTimes ) );
            page.addFilter( "modifyTime", SearchFilter.Operator.LT, DateUtil.parseTime( expireTimee ) );
        }
        page = monopolyRecordService.queryPage(page);
        List list = (List) new MonopolyRecordWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
        page.setRecords(list);
        return Rets.success(page);
    }
    @PostMapping
    @BussinessLog(value = "新增大富翁操作记录", key = "name")
    @RequiresPermissions(value = "monopolyRecordAdd")
    public Ret add( @Valid @RequestBody MonopolyRecord monopolyRecord){
        monopolyRecord.setIdw(new IdWorker().nextId()+"");
        monopolyRecordService.insert(monopolyRecord);
        sysLogService.addSysLog(getUsername(),monopolyRecord.getId(),"","PC", SysLogEnum.ADD_MONOPOLY_RECORD);
        return Rets.success();
    }
    @PutMapping
    @BussinessLog(value = "更新大富翁操作记录", key = "name")
    @RequiresPermissions(value = "monopolyRecordUpdate")
    public Ret update(@RequestBody MonopolyRecord monopolyRecord){
        monopolyRecordService.update(monopolyRecord);
        sysLogService.addSysLog(getUsername(),monopolyRecord.getId(),"","PC", SysLogEnum.UPDATE_MONOPOLY_RECORD);
        return Rets.success();
    }
    @DeleteMapping
    @BussinessLog(value = "删除大富翁操作记录", key = "id")
    @RequiresPermissions(value = "monopolyRecordDelete")
    public Ret remove(Long id){
        if (id == null) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        monopolyRecordService.delete(id);
        sysLogService.addSysLog(getUsername(),id,"","PC", SysLogEnum.DELETE_MONOPOLY_RECORD);
        return Rets.success();
    }



}
