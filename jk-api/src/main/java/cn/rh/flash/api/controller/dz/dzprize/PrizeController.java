package cn.rh.flash.api.controller.dz.dzprize;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzprize.Prize;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.service.dzprize.PrizeService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.RedisUtil;
import cn.rh.flash.utils.StringUtil;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.PrizeWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/dzprize/prize")
public class PrizeController extends BaseController {

    @Autowired
    private PrizeService prizeService;

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping(value = "/list")
    @RequiresPermissions(value = "prize")
    public Ret list(@RequestParam(required = false) String prizeName, @RequestParam(required = false) String prizeType) {
        Page<Prize> page = new PageFactory<Prize>().defaultPage();
        page.addFilter("prizeName", prizeName);
        page.addFilter("prizeType", prizeType);
        page = prizeService.queryPage(page);

        List list = (List) new PrizeWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
        page.setRecords(list);
        return Rets.success(page);
    }

    @PostMapping
    @BussinessLog(value = "新增抽奖奖品", key = "name")
    @RequiresPermissions(value = "prizeAdd")
    public Ret add(@Valid @RequestBody Prize prize) {
        if ("8".equals(prize.getPrizeType())){//夺宝活动 prizeType=8
            prize.setParticipateNumber(0);
            prize.setIsEnd("0");
        } else if ("2".equals(prize.getTypes())) {
            prize.setAmount(0.00);
        }
        else if ("10".equals(prize.getPrizeType())) {//大富翁活动奖品刷新缓存
            redisUtil.delete("getMonopolyPrizeList");
        }
        prize.setIdw(new IdWorker().nextId() + "");
        prizeService.insert(prize);
        sysLogService.addSysLog(getUsername(), prize.getId(), "", "PC", SysLogEnum.ADD_PRIZE);
        return Rets.success();
    }

    @PutMapping
    @BussinessLog(value = "更新抽奖奖品", key = "name")
    @RequiresPermissions(value = "prizeUpdate")
    public Ret update(@RequestBody Prize prize) {
        if ("8".equals(prize.getPrizeType())){//夺宝活动 prizeType=8

        }else if ("2".equals(prize.getTypes())) {
            prize.setAmount(0.00);
        }
        else if ("10".equals(prize.getPrizeType())) {//大富翁活动奖品刷新缓存
            redisUtil.delete("getMonopolyPrizeList");
        }
        prizeService.update(prize);
        sysLogService.addSysLog(getUsername(), prize.getId(), "", "PC", SysLogEnum.UPDATE_PRIZE);
        return Rets.success();
    }

    @DeleteMapping
    @BussinessLog(value = "删除抽奖奖品", key = "id")
    @RequiresPermissions(value = "prizeDelete")
    public Ret remove(Long id) {
        if (id == null) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        prizeService.delete(id);
        sysLogService.addSysLog(getUsername(), id, "", "PC", SysLogEnum.DELETE_PRIZE);
        return Rets.success();
    }

    @GetMapping(value = "/getPrizeList")
    public Ret getPrizeList(@RequestParam(required = false) String prizeType) {
        List<SearchFilter> filters = new ArrayList<>();
        if (StringUtil.isNotEmpty(prizeType)){
            filters.add(SearchFilter.build("prizeType",prizeType));
            if (prizeType.equals("8")){
                filters.add(SearchFilter.build("isEnd","0"));
            }
        }
        List<Prize> list = prizeService.queryAll(filters);
            return Rets.success(list);
    }


    @PostMapping(value = "/prizeBatchAdd")
    @BussinessLog(value = "批量新增大富翁奖品", key = "name")
//    @RequiresPermissions(value = "prizeBatchAdd")
    public Ret prizeBatchAdd(@Valid @RequestBody Prize prize) {
        ArrayList<Prize> prizes = new ArrayList<>();
        for (int i=0;i<prize.getTotalNumber();i++){
            Prize prize1 = new Prize();
            prize1.setIdw(new IdWorker().nextId() + "");
            prize1.setPrizeType("10");
            //设定排序字段
            prize1.setParticipateNumber(i);
            prize1.setTypes("0");
            prize1.setPrizeName("大富翁测试奖品"+i);
            prize1.setIsEnd("0");
            prize1.setAmount(0.0);
            prizes.add(prize1);
        }
        prizeService.saveAll(prizes);
        sysLogService.addSysLog(getUsername(), prize.getId(), "数据量："+prizes.size(), "PC", SysLogEnum.BATCH_ADD_PRIZE);
        return Rets.success();
    }

    @PostMapping(value = "/delRedisByKey")
    @BussinessLog(value = "刷新缓存", key = "String")
//    @RequiresPermissions(value = "delRedisByKey")
    public Ret delRedisByKey(@RequestParam String key) {
        redisUtil.delete(key);
        return Rets.success();
    }




}
