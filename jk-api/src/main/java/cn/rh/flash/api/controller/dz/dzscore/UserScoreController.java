package cn.rh.flash.api.controller.dz.dzscore;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.api.controller.frontapi.ContentApi;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzscore.UserScore;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.impl.MyRedissonLocker;
import cn.rh.flash.service.coom.dz.RecordInformation;
import cn.rh.flash.service.dzscore.UserScoreService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.dzuser.UserInfoService;
import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.factory.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/dzscore/userScore")
public class UserScoreController extends BaseController {

    @Autowired
    private UserScoreService scorePrizeService;

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private RecordInformation recordInformation;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private MyRedissonLocker myRedissonLocker;

    @Autowired
    private ContentApi contentApi;


    @GetMapping(value = "/list")
    @RequiresPermissions(value = "userScore")
    public Ret list(@RequestParam(required = false) String account,@RequestParam(required = false) String prizeType) {
        Page<UserScore> page = new PageFactory<UserScore>().defaultPage();
        page.addFilter("account", account);
        page.addFilter("prizeType", prizeType);
        page = scorePrizeService.queryPage(page);
        return Rets.success(page);
    }

    @PostMapping
    @BussinessLog(value = "新增用户积分", key = "name")
//    @RequiresPermissions(value = "userScoreAdd")
    public Ret add(@Valid @RequestBody UserScore prize) {

        // 加锁防止重复调用
        String key = "sign_"+prize.getAccount();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.lock(lock);
        if (b) {
            try {
                UserInfo userInfo = userInfoService.get(SearchFilter.build("account", prize.getAccount()));
                if (userInfo != null) {
                    //type 1签到 2邀请 3赠送   8夺宝活动
                    recordInformation.changeUserScore(prize.getUserScore(), userInfo.getId(),
                            userInfo.getSourceInvitationCode(), userInfo.getAccount(), Integer.valueOf(prize.getPrizeType()));
                }
                sysLogService.addSysLog(getUsername(), prize.getId(), "", "PC", SysLogEnum.ADD_SCORE);
                return Rets.success();
            } catch (Exception e) {
                e.printStackTrace();
                return Rets.failure(e.getMessage());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        log.error("用户积分没有获取到锁,用户ID:{},时间:{}", contentApi.getAccount(), DateUtil.getTime());
        return Rets.failure("当前用户正在操作积分请稍后再试");


    }

}
