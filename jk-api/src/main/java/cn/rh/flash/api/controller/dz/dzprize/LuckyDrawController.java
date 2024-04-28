package cn.rh.flash.api.controller.dz.dzprize;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzprize.LuckyDraw;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.config.chinesePattern.ChinesePattern;
import cn.rh.flash.service.dzprize.LuckyDrawService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.RedisUtil;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.LuckyDrawWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dzprize/luckydraw")
public class LuckyDrawController extends BaseController {

    @Autowired
    private LuckyDrawService luckyDrawService;

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping(value = "/list")
    @RequiresPermissions(value = "luckyDraw")
    public Ret list(@RequestParam(required = false) String name, @RequestParam(required = false) String prizeType) {
        Page<LuckyDraw> page = new PageFactory<LuckyDraw>().defaultPage();
        page.addFilter("name",name );
        page.addFilter("prizeType", prizeType);
        page = luckyDrawService.queryPage(page);
        List list = (List) new LuckyDrawWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
        page.setRecords(list);
        return Rets.success(page);
    }
    @PostMapping
    @BussinessLog(value = "新增抽奖活动", key = "name")
    @RequiresPermissions(value = "luckyDrawAdd")
    public Ret add(  @RequestBody @Validated(ChinesePattern.OnUpdate.class) LuckyDraw luckyDraw){
        luckyDraw.setIdw(new IdWorker().nextId()+"");
        luckyDrawService.insert(luckyDraw);
        sysLogService.addSysLog(getUsername(),luckyDraw.getId(),"","PC", SysLogEnum.ADD_LUCKYDRAW);
        return Rets.success();
    }
    @PutMapping
    @BussinessLog(value = "更新抽奖活动", key = "name")
    @RequiresPermissions(value = "luckyDrawUpdate")
    public Ret update(@RequestBody @Validated(ChinesePattern.OnUpdate.class) LuckyDraw luckyDraw){
        luckyDrawService.update(luckyDraw);
        if (luckyDraw.getPrizeType().equals("10")){
            redisUtil.del("getMonopolyPrizeList");
        }
        sysLogService.addSysLog(getUsername(),luckyDraw.getId(),"","PC", SysLogEnum.UPDATE_LUCKYDRAW);
        return Rets.success();
    }
    @DeleteMapping
    @BussinessLog(value = "删除抽奖活动", key = "id")
    @RequiresPermissions(value = "luckyDrawDelete")
    public Ret remove(Long id){
        if (id == null) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        luckyDrawService.delete(id);
        sysLogService.addSysLog(getUsername(),id,"","PC", SysLogEnum.DELETE_LUCKYDRAW);
        return Rets.success();
    }
}
