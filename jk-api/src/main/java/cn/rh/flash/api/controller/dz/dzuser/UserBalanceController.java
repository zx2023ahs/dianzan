package cn.rh.flash.api.controller.dz.dzuser;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.cache.CacheApiKey;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzuser.UserBalance;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.dz.BalanceRanking;
import cn.rh.flash.bean.vo.dz.IpRanking;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.cache.impl.EhcacheDao;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.dzuser.UserBalanceService;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.StringUtil;
import cn.rh.flash.utils.factory.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/dzuser/balance")
public class UserBalanceController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private UserBalanceService userBalanceService;

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private EhcacheDao redisUtil;
    @GetMapping(value = "/list")
    @RequiresPermissions(value = "userBalance")
    public Ret list(@RequestParam(required = false) Long id) {
        Page<UserBalance> page = new PageFactory<UserBalance>().defaultPage();
        if (isProxy()) {
            page.addFilter("sourceInvitationCode", getUcode());
        }
        page.addFilter("id", id);
        page = userBalanceService.queryPage(page);
        return Rets.success(page);
    }

    @PostMapping
    @BussinessLog(value = "新增用户余额", key = "name")
    @RequiresPermissions(value = "userBalanceAdd")
    public Ret add(@RequestBody UserBalance userBalance) {

        userBalance.setSourceInvitationCode(getUcode());
        userBalance.setIdw(new IdWorker().nextId() + "");

        userBalanceService.insert(userBalance);
        sysLogService.addSysLog(getUsername(),userBalance.getId(),userBalance.getAccount(),"PC", SysLogEnum.ADD_USER_BALANCE_INFO);
        return Rets.success();
    }

    @PutMapping
    @BussinessLog(value = "更新用户余额", key = "name")
    @RequiresPermissions(value = "userBalanceUpdate")
    public Ret update(@RequestBody UserBalance userBalance) {
        userBalanceService.update(userBalance);
        sysLogService.addSysLog(getUsername(),userBalance.getId(),userBalance.getAccount(),"PC", SysLogEnum.UPDATE_USER_BALANCE_INFO);
        return Rets.success();
    }

    @DeleteMapping
    @BussinessLog(value = "删除用户余额", key = "id")
    @RequiresPermissions(value = "userBalanceDelete")
    public Ret remove(Long id) {
        if (id == null) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        userBalanceService.delete(id);
        sysLogService.addSysLog(getUsername(),id,"","PC", SysLogEnum.DELETE_USER_BALANCE_INFO);
        return Rets.success();
    }


    @GetMapping(value = "/phbl")
    @RequiresPermissions(value = "ph")
    public Ret phbl() {
        Page<UserBalance> page = new PageFactory<UserBalance>().defaultPage();
        String ucode = "";
        if (isProxy()){
            ucode = getUcode();
        }

        List hget = redisUtil.hget(CacheApiKey.BalanceRanking, getUcode(), List.class);
        List<BalanceRanking> balanceRankings =new ArrayList<>();
        if (StringUtil.isNullOrEmpty(hget)){
            balanceRankings = userBalanceService.queryPageSqlBYBalance(page,ucode);
        }else {
            balanceRankings=hget;
        }
        redisUtil.hset(CacheApiKey.BalanceRanking,getUcode(),balanceRankings,3600*24);
        return Rets.success(balanceRankings);
    }

    @GetMapping(value = "/phip")
    @RequiresPermissions(value = "ph")
    public Ret phip() {
        Page<UserBalance> page = new PageFactory<UserBalance>().defaultPage();
        String ucode = "";
        if (isProxy()){
            ucode = getUcode();
        }
        List<IpRanking> balanceRankings = userBalanceService.queryPageSqlByIp(page,ucode);
        return Rets.success(balanceRankings);
    }

    //刷新用户金额排行redis-cache
    @GetMapping(value = "/rephbl")
//    @RequiresPermissions(value = "rephbl")
    public Ret rephbl() {
        Page<UserBalance> page = new PageFactory<UserBalance>().defaultPage();
        String ucode = "";
        if (isProxy()){
            ucode = getUcode();
        }
        List<BalanceRanking> balanceRankings = userBalanceService.queryPageSqlBYBalance(page,ucode);
        redisUtil.hset(CacheApiKey.BalanceRanking,getUcode(),balanceRankings,3600*24);
        return Rets.success(balanceRankings);
    }
}