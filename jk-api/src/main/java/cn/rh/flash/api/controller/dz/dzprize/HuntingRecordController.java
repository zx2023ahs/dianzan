package cn.rh.flash.api.controller.dz.dzprize;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzprize.HuntingRecord;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.dzprize.HuntingRecordService;
import cn.rh.flash.service.dzprize.PrizeService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.RedisUtil;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.HuntingRecordWrapper;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/dzprize/huntingrecord")
public class HuntingRecordController extends BaseController {

    @Autowired
    private HuntingRecordService huntingRecordService;

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private PrizeService prizeService;

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping(value = "/list")
    @RequiresPermissions(value = "huntingRecord")
    public Ret list(@RequestParam(required = false) String account,@RequestParam(required = false) String isFabricate) {
        Page<HuntingRecord> page = new PageFactory<HuntingRecord>().defaultPage();
        page.addFilter("account", account);
        page.addFilter("isFabricate", isFabricate);
        page = huntingRecordService.queryPage(page);
        List list = (List) new HuntingRecordWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
        page.setRecords(list);
        return Rets.success(page);
    }


    /**
     * @Description:参与夺宝记录
     * @Param:
     * @return:
     * @Author: zx
     */
    @PostMapping
    @BussinessLog(value = "新增中奖记录", key = "name")
    @RequiresPermissions(value = "huntingRecordAdd")
    public Ret add(@Valid @RequestBody Map param) {
//        huntingRecord.setIdw(new IdWorker().nextId() + "");
//        huntingRecordService.insert(huntingRecord);
//        sysLogService.addSysLog(getUsername(), huntingRecord.getId(), "", "PC", SysLogEnum.ADD_WINNINGRECORD);
//        return Rets.success();
        String addNum1 = param.get("addNum").toString();
        int addNum = Integer.parseInt(addNum1);
        String prizeIdw = param.get("prizeIdw").toString();
        if (addNum<=0){
            return Rets.failure(MessageTemplateEnum.PARAM_NOT_EXIST.getCode(), MessageTemplateEnum.PARAM_NOT_EXIST.getName());
        }

        // 加锁防止重复调用
        String key = "participateLuckDraw_" + prizeIdw;
        boolean b = redisUtil.lock(key);
        if (b) {
            try {
                return huntingRecordService.batchAdd(prizeIdw,addNum,getUsername());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                redisUtil.delete(key);
            }
        }
        return Rets.failure(MessageTemplateEnum.REQUEST_LIMIT.getCode(), MessageTemplateEnum.REQUEST_LIMIT);

    }

    /**
     * @Description:批量更新参与夺宝记录
     * @Param:
     * @return:
     * @Author: zx
     */
    @PostMapping(value = "/batchAdd")
    @BussinessLog(value = "批量新增中奖记录", key = "name")
    @RequiresPermissions(value = "batchAdd")
    public Ret batchAdd(@RequestBody Map param) {
        Integer addNum = (Integer) param.get("addNum");
        String prizeIdw = param.get("prizeIdw").toString();
        if (addNum<=0){
            return Rets.failure(MessageTemplateEnum.PARAM_NOT_EXIST.getCode(), MessageTemplateEnum.PARAM_NOT_EXIST.getName());
        }

        // 加锁防止重复调用
        String key = "participateLuckDraw_" + prizeIdw;
        boolean b = redisUtil.lock(key);
        if (b) {
            try {
                return huntingRecordService.batchAdd(prizeIdw,addNum,getUsername());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                redisUtil.delete(key);
            }
        }
        return Rets.failure(MessageTemplateEnum.REQUEST_LIMIT.getCode(), MessageTemplateEnum.REQUEST_LIMIT);

    }

}
