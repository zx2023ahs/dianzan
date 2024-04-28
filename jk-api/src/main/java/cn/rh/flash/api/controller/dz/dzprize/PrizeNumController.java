package cn.rh.flash.api.controller.dz.dzprize;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.dto.PrizeNumDto;
import cn.rh.flash.bean.entity.dzprize.PrizeNum;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.dzprize.PrizeNumService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.PrizeNumWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/dzprize/prizenum")
public class PrizeNumController extends BaseController {

    @Autowired
    private PrizeNumService prizeNumService;

    @Autowired
    private SysLogService sysLogService;

    @GetMapping(value = "/list")
    @RequiresPermissions(value = "prizeNum")
    public Ret list(@RequestParam(required = false) String account,
                    @RequestParam(required = false) String superAccount,
                    @RequestParam(required = false) String prizeType) {
        Page<PrizeNum> page = new PageFactory<PrizeNum>().defaultPage();
        page.addFilter("account",account);
        page.addFilter("prizeType", prizeType);
        page.addFilter("user.account",superAccount);
        page = prizeNumService.queryPage(page);

        List list = (List) new PrizeNumWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
        page.setRecords(list);

        return Rets.success(page);
    }
    @PostMapping
    @BussinessLog(value = "新增抽奖次数", key = "name")
    @RequiresPermissions(value = "prizeNumAdd")
    public Ret add( @Valid @RequestBody PrizeNum prizeNum){

        prizeNumService.insert(prizeNum);
        sysLogService.addSysLog(getUsername(),prizeNum.getId(),"","PC", SysLogEnum.ADD_PRIZENUM);
        return Rets.success();
    }
    @PutMapping
    @BussinessLog(value = "更新抽奖次数", key = "name")
    @RequiresPermissions(value = "prizeNumUpdate")
    public Ret update(@RequestBody PrizeNum prizeNum){
        prizeNumService.update(prizeNum);
        sysLogService.addSysLog(getUsername(),prizeNum.getId(),"","PC", SysLogEnum.UPDATE_PRIZENUM);
        return Rets.success();
    }
    @DeleteMapping
    @BussinessLog(value = "删除抽奖次数", key = "id")
    @RequiresPermissions(value = "prizeNumDelete")
    public Ret remove(Long id){
        if (id == null) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        prizeNumService.delete(id);
        sysLogService.addSysLog(getUsername(),id,"","PC", SysLogEnum.DELETE_PRIZENUM);
        return Rets.success();
    }


    @PostMapping(value = "/upOrDownPoints")
    @BussinessLog(value = "抽奖次数上下分", key = "name")
    public Ret upOrDownPoints(@RequestBody PrizeNumDto prizeNumDto){


        String s = prizeNumService.upOrDownPoints(prizeNumDto);
        if ("OK".equals(s)){
            sysLogService.addSysLog(getUsername(), null, "PC", SysLogEnum.UPORDOWNPOINTS,getUsername()+"--在"+ DateUtil.getTime() +"--"+("1".equals(prizeNumDto.getIsAdd())?"上":"下")+"分(抽奖次数)"+prizeNumDto.getPrizeNum()+", 操作账号:"+prizeNumDto.getAccount());
            return Rets.success();
        }
       return Rets.failure(s);
    }
}
