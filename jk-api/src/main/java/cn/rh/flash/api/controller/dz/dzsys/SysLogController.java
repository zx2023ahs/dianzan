package cn.rh.flash.api.controller.dz.dzsys;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzsys.SysLog;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.dzuser.UserInfoService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.SysLogWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/dzsys/syslog")
public class SysLogController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private UserInfoService userInfoService;

    @GetMapping(value = "/list")
    @RequiresPermissions(value = "sysLog")
    public Ret list(@RequestParam(required = false) String operator, @RequestParam(required = false) String operatorSystem,
                    @RequestParam(required = false) String phone, @RequestParam(required = false) String remark) {
        Page<SysLog> page = new PageFactory<SysLog>().defaultPage();

//		Map<Long, String> userMap = new HashMap();
//		if (StringUtil.isNotEmpty(phone)){
//			UserInfo userInfo = userInfoService.get(SearchFilter.build("account", phone));
//			if (userInfo == null){
//				return Rets.success(page);
//			}else {
//				page.addFilter("objId",userInfo.getId());
//				userMap.put(userInfo.getId(),userInfo.getAccount());
//			}
//		}

        page.addFilter("operator", operator);
        page.addFilter("operatorSystem", operatorSystem);
        page.addFilter("remark", SearchFilter.Operator.LIKE, remark);

        page = sysLogService.queryPage(page);
//		if (StringUtil.isEmpty(phone)){
//
//			Set<Long> uids = page.getRecords().stream().map(SysLog::getObjId).collect(Collectors.toSet());
//			List<UserInfo> userInfoList = userInfoService.queryAll(SearchFilter.build("id", SearchFilter.Operator.IN, uids));
//			 userMap = userInfoList.stream().collect(Collectors.toMap(UserInfo::getId, UserInfo::getAccount));
//		}
//
//		for (SysLog record : page.getRecords()) {
//			record.setAccount(userMap.get(record.getObjId()));
//		}

        List list = (List) new SysLogWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();

        page.setRecords(list);
        return Rets.success(page);
    }

    @PostMapping
    @BussinessLog(value = "新增系统日志", key = "name")
    @RequiresPermissions(value = "sysLogAdd")
    public Ret add(@Valid @RequestBody SysLog sysLog) {
        sysLog.setIdw(new IdWorker().nextId() + "");
        sysLogService.insert(sysLog);
        return Rets.success();
    }

    @PutMapping
    @BussinessLog(value = "更新系统日志", key = "name")
    @RequiresPermissions(value = "sysLogUpdate")
    public Ret update(@RequestBody SysLog sysLog) {
        sysLogService.update(sysLog);
        return Rets.success();
    }

    @DeleteMapping
    @BussinessLog(value = "删除系统日志", key = "id")
    @RequiresPermissions(value = "sysLogDelete")
    public Ret remove(Long id) {
        if (id == null) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        sysLogService.delete(id);
        return Rets.success();
    }
}