package cn.rh.flash.api.controller.dz.dzprize;


import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzprize.ExpectWinningUser;
import cn.rh.flash.bean.entity.dzprize.Prize;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.service.dzprize.ExpectWinningUserService;
import cn.rh.flash.service.dzprize.PrizeService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.ExpectWinningUserWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dzprize/expectwinninguser")
public class ExpectWinningUserController extends BaseController {

    @Autowired
    private ExpectWinningUserService expectWinningUserService;

    @Autowired
    private PrizeService prizeService;

    @Autowired
    private SysLogService sysLogService;

    @GetMapping(value = "/list")
    @RequiresPermissions(value = "expectWinningUser")
    public Ret list(@RequestParam(required = false) String account, @RequestParam(required = false) String prizeType) {
        Page<ExpectWinningUser> page = new PageFactory<ExpectWinningUser>().defaultPage();
        page.addFilter("account", account);
        page.addFilter("prizeType", prizeType);
        page = expectWinningUserService.queryPage(page);
        List<ExpectWinningUser> records = page.getRecords();
        Set<String> collect = records.stream().map(ExpectWinningUser::getPrizeIdw).collect(Collectors.toSet());

        List<Prize> prizes = prizeService.queryAll(SearchFilter.build("idw", SearchFilter.Operator.IN, collect));
        Map<String, String> prizeMap = prizes.stream().collect(Collectors.toMap(Prize::getIdw, Prize::getPrizeName));
        for (ExpectWinningUser expectWinningUser : records) {
            expectWinningUser.setPrizeName(prizeMap.get(expectWinningUser.getPrizeIdw()));
        }
        List list = (List) new ExpectWinningUserWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();

        page.setRecords(list);

        return Rets.success(page);
    }

    @PostMapping
    @BussinessLog(value = "新增预期中奖用户", key = "name")
    @RequiresPermissions(value = "expectWinningUserAdd")
    public Ret add(@Valid @RequestBody ExpectWinningUser expectWinningUser) {
        String s = expectWinningUserService.addExpectWinningUser(expectWinningUser);
        if ("OK".equals(s)) {
            sysLogService.addSysLog(getUsername(), expectWinningUser.getId(), "", "PC", SysLogEnum.ADD_EXPECTWINNINGUSER);
            return Rets.success();
        }
        return Rets.failure(s);
    }

    @PutMapping
    @BussinessLog(value = "更新预期中奖用户", key = "name")
    @RequiresPermissions(value = "expectWinningUserUpdate")
    public Ret update(@RequestBody ExpectWinningUser expectWinningUser) {
        expectWinningUserService.update(expectWinningUser);
        sysLogService.addSysLog(getUsername(), expectWinningUser.getId(), "", "PC", SysLogEnum.UPDATE_EXPECTWINNINGUSER);
        return Rets.success();
    }

    @DeleteMapping
    @BussinessLog(value = "删除预期中奖用户", key = "id")
    @RequiresPermissions(value = "expectWinningUserDelete")
    public Ret remove(Long id) {
        if (id == null) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        ExpectWinningUser expectWinningUser = expectWinningUserService.get(id);
        if ("yes".equalsIgnoreCase(expectWinningUser.getIsPrize())){
            return Rets.failure("已中奖的预设奖品记录无法清除");
        }
        expectWinningUserService.delete(id);
        sysLogService.addSysLog(getUsername(), id, "", "PC", SysLogEnum.DELETE_EXPECTWINNINGUSER);
        return Rets.success();
    }
    @DeleteMapping("/removeAll")
    @BussinessLog(value = "删除全部未中奖的记录", key = "id")
    @RequiresPermissions(value = "expectWinningUserDelete")
    public Ret removeAll() {

        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("isPrize","no"));
        List<ExpectWinningUser> expectWinningUsers = expectWinningUserService.queryAll(filters);
        Set<Long> ids = expectWinningUsers.stream().map(ExpectWinningUser::getId).collect(Collectors.toSet());
        expectWinningUserService.delete(ids);
        sysLogService.addSysLog(getUsername(), null, "", "PC", SysLogEnum.DELETE_ALL_EXPECTWINNINGUSER);
        return Rets.success();
    }
}
