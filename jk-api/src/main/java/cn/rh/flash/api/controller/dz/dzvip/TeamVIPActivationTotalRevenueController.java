package cn.rh.flash.api.controller.dz.dzvip;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzuser.UserBalance;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.entity.dzuser.WithdrawalsRecord;
import cn.rh.flash.bean.entity.dzvip.TeamVIPActivationTotalRevenue;
import cn.rh.flash.bean.entity.system.Dict;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.dz.LevelUserVo;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.dzuser.UserBalanceService;
import cn.rh.flash.service.dzuser.UserInfoService;
import cn.rh.flash.service.dzuser.WithdrawalsRecordService;
import cn.rh.flash.service.dzvip.TeamVIPActivationTotalRevenueService;
import cn.rh.flash.service.system.impl.ConstantFactory;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.StringUtil;
import cn.rh.flash.utils.factory.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/dzvip/teamvip")
public class TeamVIPActivationTotalRevenueController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private TeamVIPActivationTotalRevenueService teamVIPActivationTotalRevenueService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserBalanceService userBalanceService;

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private WithdrawalsRecordService withdrawalsRecordService;

    @GetMapping(value = "/list")
    @RequiresPermissions(value = "teamVIPActivationTotalRevenue")
    public Ret list(@RequestParam(required = false) Long id) {
        Page<TeamVIPActivationTotalRevenue> page = new PageFactory<TeamVIPActivationTotalRevenue>().defaultPage();
        if (isProxy()) {
            page.addFilter("sourceInvitationCode", getUcode());
        }
        page.addFilter("id", id);
        page = teamVIPActivationTotalRevenueService.queryPage(page);
        return Rets.success(page);
    }

    @PostMapping
    @BussinessLog(value = "新增团队vip开通总返佣", key = "name")
    @RequiresPermissions(value = "teamVIPActivationTotalRevenueAdd")
    public Ret add(@RequestBody @Valid TeamVIPActivationTotalRevenue teamVIPActivationTotalRevenue) {

        teamVIPActivationTotalRevenue.setSourceInvitationCode(getUcode());
        teamVIPActivationTotalRevenue.setIdw(new IdWorker().nextId() + "");

        teamVIPActivationTotalRevenueService.insert(teamVIPActivationTotalRevenue);

        sysLogService.addSysLog(getUsername(), teamVIPActivationTotalRevenue.getId(), teamVIPActivationTotalRevenue.getAccount(), "PC", SysLogEnum.ADD_TEAM_VIP_ACTIVATION_TOTAL_REVENUE_INFO);
        return Rets.success();
    }

    @PutMapping
    @BussinessLog(value = "更新团队vip开通总返佣", key = "name")
    @RequiresPermissions(value = "teamVIPActivationTotalRevenueUpdate")
    public Ret update(@RequestBody @Valid TeamVIPActivationTotalRevenue teamVIPActivationTotalRevenue) {
        teamVIPActivationTotalRevenueService.update(teamVIPActivationTotalRevenue);
        sysLogService.addSysLog(getUsername(), teamVIPActivationTotalRevenue.getId(), teamVIPActivationTotalRevenue.getAccount(), "PC", SysLogEnum.ADD_WITHDRAWALS_RECORD_INFO);
        return Rets.success();
    }

    @DeleteMapping
    @BussinessLog(value = "删除团队vip开通总返佣", key = "id")
    @RequiresPermissions(value = "teamVIPActivationTotalRevenueDelete")
    public Ret remove(Long id) {
        if (id == null) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        teamVIPActivationTotalRevenueService.delete(id);
        sysLogService.addSysLog(getUsername(), id, "", "PC", SysLogEnum.DELETE_TEAM_VIP_ACTIVATION_TOTAL_REVENUE_INFO);
        return Rets.success();
    }


    /**
     * 团队统计
     * 今日新增 / 昨日新增 / vip总数
     */
    @GetMapping(value = "/teamStatisticsOne")
    public Ret teamStatisticsOne(@RequestParam String phone) {
        if (StringUtil.isEmpty(phone)) {
            throw new ApplicationException(BizExceptionEnum.PHONE_EMPTY);
        }
        return Rets.success(teamVIPActivationTotalRevenueService.teamStatisticsOne(phone));
    }

    /**
     * 团队统计
     * 新增用户  今日首充() / 昨日首充() /
     */
    @GetMapping(value = "/teamStatisticsTwo")
    public Ret teamStatisticsTwo(@RequestParam String phone) {
        if (StringUtil.isEmpty(phone)) {
            throw new ApplicationException(BizExceptionEnum.PHONE_EMPTY);
        }
        return Rets.success(teamVIPActivationTotalRevenueService.teamStatisticsTwo(phone));
    }

    /**
     * 团队统计
     * 新增用户  今日充值总金额 / 昨日充值总金额 / 总充值 /今日充值次数/ 昨日充值次数/ 总次数 /
     */
    @GetMapping(value = "/teamStatisticsThree")
    public Ret teamStatisticsThree(@RequestParam String phone) {
        if (StringUtil.isEmpty(phone)) {
            throw new ApplicationException(BizExceptionEnum.PHONE_EMPTY);
        }
        return Rets.success(teamVIPActivationTotalRevenueService.teamStatisticsThree(phone));
    }

    /**
     * 团队统计
     * 新增用户  今日提现总金额 / 昨日提现总金额 / 总提现
     */
    @GetMapping(value = "/teamStatisticsFour")
    public Ret teamStatisticsFour(@RequestParam String phone) {
        if (StringUtil.isEmpty(phone)) {
            throw new ApplicationException(BizExceptionEnum.PHONE_EMPTY);
        }
        return Rets.success(teamVIPActivationTotalRevenueService.teamStatisticsFour(phone));
    }

    /**
     * @Description: 团队统计 今日总晋级/昨日总晋级
     * @Author: yc(ny最帅的偷窥狂 汪汪汪)
     * @Date: 2023/7/4
     */
    @GetMapping(value = "/teamStatisticsFive")
    public Ret teamStatisticsFive(@RequestParam String phone) {
        if (StringUtil.isEmpty(phone)) {
            throw new ApplicationException(BizExceptionEnum.PHONE_EMPTY);
        }
        return Rets.success(teamVIPActivationTotalRevenueService.teamStatisticsFive(phone));
    }

    /**
     * 团队统计
     * 今日新增vip  / 昨日新增vip
     */
    @GetMapping(value = "/teamStatisticsSex")
    public Ret teamStatisticsSex(@RequestParam String phone) {
        if (StringUtil.isEmpty(phone)) {
            throw new ApplicationException(BizExceptionEnum.PHONE_EMPTY);
        }
        return Rets.success(teamVIPActivationTotalRevenueService.teamStatisticsSex(phone));
    }


    /**
     * 用户信息
     *
     * @return
     */
    @GetMapping(value = "/getUserInformation")
    public Ret getUserInformation(@RequestParam String phone) {
        if (StringUtil.isEmpty(phone)) {
            throw new ApplicationException(BizExceptionEnum.PHONE_EMPTY);
        }
        return Rets.success(teamVIPActivationTotalRevenueService.getUserInformation(phone));
    }

    /**
     * 用户 vip 等级数量
     *
     * @return
     */
    @GetMapping(value = "/numberOfVipLevels")
    public Ret numberOfVipLevels(@RequestParam String phone) {
        if (StringUtil.isEmpty(phone)) {
            throw new ApplicationException(BizExceptionEnum.PHONE_EMPTY);
        }
        return Rets.success(teamVIPActivationTotalRevenueService.numberOfVipLevels(phone));
    }

    // 各个下级用户详情
    @GetMapping(value = "/getLevelUserList")
    public Ret getLevelUserList(@RequestParam(required = false) String phone, @RequestParam(required = false) String level,
                                @RequestParam(required = false) String vipType) {
        UserInfo userInfo = userInfoService.get(SearchFilter.build("account", phone));
        if (userInfo == null) {
            return Rets.failure("未找到当前用户");
        }
        Page<UserInfo> page = new PageFactory<UserInfo>().defaultPage();
        page.addFilter(SearchFilter.build("sourceInvitationCode", userInfo.getSourceInvitationCode()));
        page.addFilter(SearchFilter.build("pinvitationCode", SearchFilter.Operator.LIKE, userInfo.getPinvitationCode()));
        //            page.addFilter(SearchFilter.build("levels", userInfo.getLevels() + level));
        if (StringUtil.isNotEmpty(level)){
            String[] split = level.split(",");
            page.addFilter(SearchFilter.build("levels", SearchFilter.Operator.IN,
                    Arrays.stream(split).map(str->Integer.valueOf(str)+userInfo.getLevels()).toArray()));
        }
        if (StringUtil.isNotEmpty(vipType)){
//            page.addFilter(SearchFilter.build("vipType", vipType));
            page.addFilter(SearchFilter.build("vipType", SearchFilter.Operator.IN, vipType.split(",")));
        }
        page.setSort(Sort.by(Sort.Order.asc("vipType")));
        page = userInfoService.queryPage(page);

        List<UserInfo> userInfos = page.getRecords();

        Set<Long> uids = userInfos.stream().map(UserInfo::getId).collect(Collectors.toSet());
        // 查询余额
        List<UserBalance> userBalances = userBalanceService.queryAll(SearchFilter.build("uid", SearchFilter.Operator.IN, uids));
        Map<Long, Double> balanceMap = userBalances.stream().collect(Collectors.toMap(UserBalance::getUid, UserBalance::getUserBalance));

        // vip
        List<Dict> dicts = ConstantFactory.me().getDicts("ViP类型");
        Map<String, String> dictMap = dicts.stream().collect(Collectors.toMap(Dict::getNum, Dict::getName));

        // 查询提现记录
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("uid", SearchFilter.Operator.IN, uids));
        filters.add(SearchFilter.build("rechargeStatus", SearchFilter.Operator.IN, new String[]{"suc", "sysok"}));
        List<WithdrawalsRecord> withdrawalsRecords = withdrawalsRecordService.queryAll(filters);
        Map<Long, Long> withMap = withdrawalsRecords.stream().collect(Collectors.groupingBy(WithdrawalsRecord::getUid, Collectors.counting()));

        List<LevelUserVo> collect = userInfos.stream().map(c -> {
            LevelUserVo v = new LevelUserVo();
            BeanUtils.copyProperties(c, v);

            v.setUserBalance(balanceMap.get(c.getId()) == null ? 0.00 : balanceMap.get(c.getId()));
            v.setWithNum(withMap.get(c.getId()) == null ? 0L : withMap.get(c.getId()));
            v.setVipTypeStr(dictMap.get(c.getVipType()));
            return v;
        }).collect(Collectors.toList());

        Page<LevelUserVo> resultPage = new Page<>();
        resultPage.setTotal(page.getTotal());
        resultPage.setSize(page.getSize());
        resultPage.setCurrent(page.getCurrent());
        resultPage.setRecords(collect);

        return Rets.success(resultPage);
    }


}