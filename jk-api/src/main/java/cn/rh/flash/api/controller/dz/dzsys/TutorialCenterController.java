package cn.rh.flash.api.controller.dz.dzsys;


import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzsys.TutorialCenter;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.dzsys.TutorialCenterService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.TutorialCenterWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/dzsys/tutorialcenter")
public class TutorialCenterController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TutorialCenterService tutorialCenterService;

    @GetMapping(value = "/list")
    @RequiresPermissions(value = "tutorialCenter")
    public Ret list(@RequestParam(required = false) String type) {
        Page<TutorialCenter> page = new PageFactory<TutorialCenter>().defaultPage();
        page.addFilter("type", type);
        page = tutorialCenterService.queryPage(page);
        List list = (List) new TutorialCenterWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
        page.setRecords(list);
        return Rets.success(page);
    }

    @PostMapping
    @BussinessLog(value = "新增教程中心", key = "name")
    @RequiresPermissions(value = "tutorialCenterAdd")
    public Ret add(@Valid @RequestBody TutorialCenter tutorialCenter) {
        tutorialCenter.setIdw(new IdWorker().nextId() + "");
        tutorialCenterService.insert(tutorialCenter);
        return Rets.success();
    }

    @PutMapping
    @BussinessLog(value = "更新教程中心", key = "name")
    @RequiresPermissions(value = "tutorialCenterUpdate")
    public Ret update(@RequestBody TutorialCenter tutorialCenter) {
        tutorialCenterService.update(tutorialCenter);
        return Rets.success();
    }

    @DeleteMapping
    @BussinessLog(value = "删除教程中心", key = "id")
    @RequiresPermissions(value = "tutorialCenterDelete")
    public Ret remove(Long id) {
        if (id == null) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        tutorialCenterService.delete(id);
        return Rets.success();
    }


}
