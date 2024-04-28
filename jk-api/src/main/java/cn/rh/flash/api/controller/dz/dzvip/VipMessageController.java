package cn.rh.flash.api.controller.dz.dzvip;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzvip.DzVipMessage;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.dz.DzVipCountVo;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.DzVipMessageCache;
import cn.rh.flash.service.dzpower.PowerBankTaskService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.dzvip.DzVipMessageService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.VipMessageWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/dzvip/vipmessage")
public class VipMessageController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private DzVipMessageService dzVipMessageService;

    @Autowired
    private SysLogService sysLogService;
    @Autowired
    private DzVipMessageCache dzVipMessageCache;

    @Autowired
    private PowerBankTaskService powerBankTaskService;

    @GetMapping(value = "/list")
    @RequiresPermissions(value = "vipMessage")
    public Ret list(@RequestParam(required = false) Long id) {
        Page<DzVipMessage> page = new PageFactory<DzVipMessage>().defaultPage();
        if (isProxy()) {
            page.addFilter("sourceInvitationCode", getUcode());
        }
        page.addFilter("id", id);
        page = dzVipMessageService.queryPage(page);
        List list = (List) new VipMessageWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
        page.setRecords(list);
        dzVipMessageCache.cache();
        return Rets.success(page);
    }

    @PostMapping
    @BussinessLog(value = "新增Vip信息", key = "name")
    @RequiresPermissions(value = "vipMessageAdd")
    public Ret add(@RequestBody DzVipMessage dzVipMessage) {

        dzVipMessage.setIdw(new IdWorker().nextId() + "");
        dzVipMessage.setSourceInvitationCode(getUcode());

        dzVipMessageService.insert(dzVipMessage);

        sysLogService.addSysLog(getUsername(), dzVipMessage.getId(), "", "PC", SysLogEnum.ADD_DZ_VIP_MESSAGE_INFO);
        dzVipMessageCache.cache();
        return Rets.success();
    }

    @PutMapping
    @BussinessLog(value = "更新Vip信息", key = "name")
    @RequiresPermissions(value = "vipMessageUpdate")
    public Ret update(@RequestBody DzVipMessage dzVipMessage) {
        // 查询数据库当前vip信息 比较每日返佣 与充电宝数量 如果不相等 需要修改已购买当前vip的返佣任务

        DzVipMessage oldVipMessage = dzVipMessageService.get(SearchFilter.build("id", dzVipMessage.getId()));

        if (!oldVipMessage.getDailyIncome().equals(dzVipMessage.getDailyIncome())
                || !oldVipMessage.getNumberOfTasks().equals(dzVipMessage.getNumberOfTasks())
                || !dzVipMessage.getGearCode().equals(oldVipMessage.getGearCode())) {
            // 修改vip返佣任务
            powerBankTaskService.updateVipAmount(dzVipMessage);
            /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();*/
            String format = DateUtil.getTime();
            //String format = sdf.format(date);
            sysLogService.addSysLog(getUsername(), dzVipMessage.getId(), "PC",
                    SysLogEnum.UPDATE_DZ_VIP_MESSAGE_INFO,
                    getUsername()+"--在"+format+"--修改"+dzVipMessage.getVipType()+"每日收入或充电宝数量--" +
                            "修改前:-每日收入:"+oldVipMessage.getDailyIncome()+"-充电宝数量:"+oldVipMessage.getNumberOfTasks()+"-档位:"+oldVipMessage.getGearCode()+
                            "修改后:-每日收入:"+dzVipMessage.getDailyIncome()+"-充电宝数量:"+dzVipMessage.getNumberOfTasks()+"-档位:"+dzVipMessage.getGearCode()
            );
        }

        dzVipMessageService.update(dzVipMessage);

        sysLogService.addSysLog(getUsername(), dzVipMessage.getId(), "", "PC", SysLogEnum.UPDATE_DZ_VIP_MESSAGE_INFO);
        dzVipMessageCache.cache();
        return Rets.success();
    }

    @DeleteMapping
    @BussinessLog(value = "删除Vip信息", key = "id")
    @RequiresPermissions(value = "vipMessageDelete")
    public Ret remove(Long id) {
        if (id == null) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        dzVipMessageService.delete(id);
        sysLogService.addSysLog(getUsername(), id, "", "PC", SysLogEnum.DELETE_DZ_VIP_MESSAGE_INFO);
        dzVipMessageCache.cache();
        return Rets.success();
    }

    @GetMapping(value = "/findVipCount")
    @RequiresPermissions(value = "vipMessage")
    public Ret findVipCount() {
        String ucode = "";
        if (isProxy()) {
            ucode = getUcode();
        }
        DzVipCountVo vipCountVo = dzVipMessageService.findVipCount(ucode);
        return Rets.success(vipCountVo);
    }



}