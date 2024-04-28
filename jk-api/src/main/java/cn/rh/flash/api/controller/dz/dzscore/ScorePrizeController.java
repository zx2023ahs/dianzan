package cn.rh.flash.api.controller.dz.dzscore;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzscore.ScorePrize;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.service.dzscore.ScorePrizeService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.ScorePrizeWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/dzscore/scorePrize")
public class ScorePrizeController extends BaseController {

    @Autowired
    private ScorePrizeService scorePrizeService;

    @Autowired
    private SysLogService sysLogService;

    @GetMapping(value = "/list")
    @RequiresPermissions(value = "scorePrize")
    public Ret list(@RequestParam(required = false) String prizeName, @RequestParam(required = false) String prizeType) {
        Page<ScorePrize> page = new PageFactory<ScorePrize>().defaultPage();
        page.addFilter("prizeName", SearchFilter.Operator.LIKE, prizeName);
        page.addFilter("prizeType", prizeType);
        page = scorePrizeService.queryPage(page);

        List list = (List) new ScorePrizeWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
        page.setRecords(list);
        return Rets.success(page);
    }

    @PostMapping
    @BussinessLog(value = "新增兑换奖品", key = "name")
    @RequiresPermissions(value = "scorePrizeAdd")
    public Ret add(@Valid @RequestBody ScorePrize prize) {
        if ("2".equals(prize.getTypes())) {
            prize.setAmount(0.00);
        }
        prize.setIdw(new IdWorker().nextId() + "");
        scorePrizeService.insert(prize);
        sysLogService.addSysLog(getUsername(), prize.getId(), "", "PC", SysLogEnum.ADD_SCORE_PRIZE);
        return Rets.success();
    }

    @PutMapping
    @BussinessLog(value = "更新兑换奖品", key = "name")
    @RequiresPermissions(value = "scorePrizeUpdate")
    public Ret update(@RequestBody ScorePrize prize) {
        if ("2".equals(prize.getTypes())) {
            prize.setAmount(0.00);
        }
        scorePrizeService.update(prize);
        sysLogService.addSysLog(getUsername(), prize.getId(), "", "PC", SysLogEnum.UPDATE_SCORE_PRIZE);
        return Rets.success();
    }

    @DeleteMapping
    @BussinessLog(value = "删除兑换奖品", key = "id")
    @RequiresPermissions(value = "scorePrizeDelete")
    public Ret remove(Long id) {
        if (id == null) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        scorePrizeService.delete(id);
        sysLogService.addSysLog(getUsername(), id, "", "PC", SysLogEnum.DELETE_SCORE_PRIZE);
        return Rets.success();
    }


}
