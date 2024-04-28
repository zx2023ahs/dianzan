package cn.rh.flash.api.controller.dz.dzprize;


import cn.hutool.core.collection.CollUtil;
import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzprize.WinningRecord;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.service.dzprize.PrizeService;
import cn.rh.flash.service.dzprize.WinningRecordService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.system.FileService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.StringUtil;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.WinningRecordWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dzprize/winningrecord")
public class WinningRecordController extends BaseController {

    @Autowired
    private WinningRecordService winningRecordService;

    @Autowired
    private PrizeService prizeService;

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private FileService fileService;


    @GetMapping(value = "/list")
    @RequiresPermissions(value = "winningRecord")
    public Ret list(@RequestParam(required = false) String account,
                    @RequestParam(required = false) String prizeType,
                    @RequestParam(required = false) String superAccount,
                    @RequestParam(required = false) String expireTimes,
                    @RequestParam(required = false) String expireTimee) {
        Page<WinningRecord> page = new PageFactory<WinningRecord>().defaultPage();
        page.addFilter("account", account);
        page.addFilter("prizeType", prizeType);
        page.addFilter("user.account",superAccount);

        if (StringUtil.isNotEmpty( expireTimes ) && StringUtil.isNotEmpty( expireTimee )) {
            expireTimes = DateUtil.getTimeByZone(expireTimes);
            expireTimee = DateUtil.getTimeByZone(expireTimee);
            page.addFilter( "modifyTime", SearchFilter.Operator.GTE, DateUtil.parseTime( expireTimes ) );
            page.addFilter( "modifyTime", SearchFilter.Operator.LT, DateUtil.parseTime( expireTimee ) );
        }

        page = winningRecordService.queryPage(page);

        List list = (List) new WinningRecordWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
        page.setRecords(list);

//        List<WinningRecord> winningRecords = page.getRecords();
//        Set<String> collect = winningRecords.stream().map(WinningRecord::getPrizeIdw).collect(Collectors.toSet());
//        List<Prize> prizes = prizeService.queryAll(SearchFilter.build("idw", SearchFilter.Operator.IN, collect));
//        Map<String, String> prizeMap = prizes.stream().collect(Collectors.toMap(Prize::getIdw, Prize::getPrizeName));
//        for (WinningRecord winningRecord : winningRecords) {
//            winningRecord.setPrizeName(prizeMap.get(winningRecord.getPrizeIdw()));
//        }
        return Rets.success(page);
    }

    @GetMapping(value = "/winningrecordExportV2")
    public void winningrecordExportV2(
            @RequestParam(required = false) String account,
            @RequestParam(required = false) String prizeType,
            @RequestParam(required = false) String superAccount,
            @RequestParam(required = false) String expireTimes,
            @RequestParam(required = false) String expireTimee,
            HttpServletResponse response) {
        Page<WinningRecord> page = new PageFactory<WinningRecord>().defaultPage();
        page.addFilter("account", account);
        page.addFilter("prizeType", prizeType);
        page.addFilter("user.account",superAccount);

        if (StringUtil.isNotEmpty( expireTimes ) && StringUtil.isNotEmpty( expireTimee )) {
            expireTimes = DateUtil.getTimeByZone(expireTimes);
            expireTimee = DateUtil.getTimeByZone(expireTimee);
            page.addFilter( "modifyTime", SearchFilter.Operator.GTE, DateUtil.parseTime( expireTimes ) );
            page.addFilter( "modifyTime", SearchFilter.Operator.LT, DateUtil.parseTime( expireTimee ) );
        }
        page = winningRecordService.queryPage(page);
        List<Map<String,Object>> list = (List<Map<String,Object>>) new WinningRecordWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
        if (CollUtil.isEmpty(list)){
            throw  new RuntimeException("查询为空");
        }
         winningRecordService.export(list,response);
    }






    @PostMapping
    @BussinessLog(value = "新增中奖记录", key = "name")
    @RequiresPermissions(value = "winningRecordAdd")
    public Ret add(@Valid @RequestBody WinningRecord winningRecord) {
        winningRecord.setIdw(new IdWorker().nextId() + "");
        winningRecordService.insert(winningRecord);
        sysLogService.addSysLog(getUsername(), winningRecord.getId(), "", "PC", SysLogEnum.ADD_WINNINGRECORD);
        return Rets.success();
    }

    @PutMapping
    @BussinessLog(value = "更新中奖记录", key = "name")
    @RequiresPermissions(value = "winningRecordUpdate")
    public Ret update(@RequestBody WinningRecord winningRecord) {
        winningRecordService.update(winningRecord);
        sysLogService.addSysLog(getUsername(), winningRecord.getId(), "", "PC", SysLogEnum.UPDATE_WINNINGRECORD);
        return Rets.success();
    }

    @DeleteMapping
    @BussinessLog(value = "删除中奖记录", key = "id")
    @RequiresPermissions(value = "winningRecordDelete")
    public Ret remove(Long id) {
        if (id == null) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        winningRecordService.delete(id);
        sysLogService.addSysLog(getUsername(), id, "", "PC", SysLogEnum.DELETE_WINNINGRECORD);
        return Rets.success();
    }
}
