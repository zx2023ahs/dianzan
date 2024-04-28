package cn.rh.flash.api.controller.dz.dzscore;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.entity.dzscore.ScorePrizeRecord;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.dzscore.ScorePrizeRecordService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.ScorePrizeRecordWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dzscore/scorePrizeRecord")
public class ScorePrizeRecordController extends BaseController {

    @Autowired
    private ScorePrizeRecordService scorePrizeRecordService;

    @Autowired
    private SysLogService sysLogService;

    @GetMapping(value = "/list")
    @RequiresPermissions(value = "scorePrizeRecord")
    public Ret list(@RequestParam(required = false) String account,
                    @RequestParam(required = false) String prizeName,
                    @RequestParam(required = false) String prizeType) {
        Page<ScorePrizeRecord> page = new PageFactory<ScorePrizeRecord>().defaultPage();
        page.addFilter("prizeName", prizeName);
        page.addFilter("account", account);
        page.addFilter("prizeType", prizeType);
        page = scorePrizeRecordService.queryPage(page);
        List list = (List) new ScorePrizeRecordWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
        page.setRecords(list);
        return Rets.success(page);
    }


}
