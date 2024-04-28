package cn.rh.flash.api.controller.dz.dzvip;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzuser.RechargeRecord;
import cn.rh.flash.bean.entity.dzvip.VipPurchaseHistory;
import cn.rh.flash.bean.entity.system.User;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.dzuser.UserInfoService;
import cn.rh.flash.service.dzvip.VipPurchaseHistoryService;
import cn.rh.flash.service.system.FileService;
import cn.rh.flash.service.system.UserService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.StringUtil;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.VipPurchaseHistoryWrapper;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/dzvip/vippurchase")
public class VipPurchaseHistoryController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private VipPurchaseHistoryService vipPurchaseHistoryService;
    @Autowired
    private FileService fileService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private SysLogService sysLogService;
    @Autowired
    private UserService userService;

    @GetMapping(value = "/list")
    @RequiresPermissions(value = {"vipPurchaseHistory", "vipPurchaseHistory2"}, logical = Logical.OR)
    public Ret list(@RequestParam(required = false) String account, @RequestParam(required = false) String previousViPType,
                    @RequestParam(required = false) String afterViPType, @RequestParam(required = false) String paymentMethod,
                    @RequestParam(required = false) String expireTimes, @RequestParam(required = false) String expireTimee,
                    @RequestParam(required = false) Integer whetherToPay, @RequestParam(required = false) String sourceInvitationCode,
                    @RequestParam(required = false) String gmt, @RequestParam(required = false) String idw,
                    @RequestParam(required = false) String depositAddress, @RequestParam(required = false) String isVip) {
        Page<VipPurchaseHistory> page = new PageFactory<VipPurchaseHistory>().defaultPage();
        if (isProxy()) {
            page.addFilter("sourceInvitationCode", getUcode());
        } else if (StringUtil.isNotEmpty(sourceInvitationCode)) {
//            UserInfo userInfo = userInfoService.get(SearchFilter.build("account", sourceInvitationCode));
//            if (userInfo != null) {
//                page.addFilter("sourceInvitationCode", SearchFilter.Operator.LIKE, userInfo.getSourceInvitationCode());
//            }else {
//                return Rets.success(page);
//            }
            User user = userService.get(SearchFilter.build("account", sourceInvitationCode));
            if (user != null) {
                page.addFilter("sourceInvitationCode", SearchFilter.Operator.LIKE, user.getUcode());
            } else {
                return Rets.failure("未找到此代理账号");
            }
        }

        page.addFilter("idw", idw);
        page.addFilter("account", account);

        if (StringUtil.isNotEmpty(previousViPType)) {
            page.addFilter("previousViPType", SearchFilter.Operator.IN, previousViPType.split("_"));
        }
        if (StringUtil.isNotEmpty(afterViPType)) {
            page.addFilter("afterViPType", SearchFilter.Operator.IN, afterViPType.split("_"));
        }

        page.addFilter("paymentMethod", paymentMethod);
        page.addFilter("whetherToPay", whetherToPay);
        page.addFilter("depositAddress", depositAddress);

        if (StringUtil.isNotEmpty(isVip) && "0".equals(isVip)) {
            page.addFilter("afterViPType", SearchFilter.Operator.NE, "v1");
        }

        if (StringUtil.isNotEmpty(expireTimes) && StringUtil.isNotEmpty(expireTimee)) {
            //gmt = StringUtil.isEmpty( gmt ) ? TimeZone.getDefault().getID() : gmt;
//            expireTimes = DateUtil.getTimeByZone(expireTimes);
//            expireTimee = DateUtil.getTimeByZone(expireTimee);
            page.addFilter("createTime", SearchFilter.Operator.GTE, DateUtil.parseTime(expireTimes));
            page.addFilter("createTime", SearchFilter.Operator.LT, DateUtil.parseTime(expireTimee));
        }
        page = vipPurchaseHistoryService.queryPage(page);

        List list = (List) new VipPurchaseHistoryWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
        page.setRecords(list);
        return Rets.success(page);
    }

    @PostMapping
    @BussinessLog(value = "新增Vip购买记录", key = "name")
    @RequiresPermissions(value = "vipPurchaseHistoryAdd")
    public Ret add(@RequestBody VipPurchaseHistory vipPurchaseHistory) {

        vipPurchaseHistory.setSourceInvitationCode(getUcode());
        vipPurchaseHistory.setIdw(new IdWorker().nextId() + "");

        vipPurchaseHistoryService.insert(vipPurchaseHistory);

        sysLogService.addSysLog(getUsername(), vipPurchaseHistory.getId(), vipPurchaseHistory.getAccount(), "PC", SysLogEnum.ADD_VIP_PURCHASE_HISTORY_INFO);
        return Rets.success();
    }

    @PutMapping
    @BussinessLog(value = "更新Vip购买记录", key = "name")
    @RequiresPermissions(value = "vipPurchaseHistoryUpdate")
    public Ret update(@RequestBody VipPurchaseHistory vipPurchaseHistory) {
        vipPurchaseHistoryService.update(vipPurchaseHistory);
        sysLogService.addSysLog(getUsername(), vipPurchaseHistory.getId(), vipPurchaseHistory.getAccount(), "PC", SysLogEnum.UPDATE_VIP_PURCHASE_HISTORY_INFO);
        return Rets.success();
    }


    @PutMapping("examine")
    @BussinessLog(value = "审核vip未支付订单银行卡", key = "name")
    @RequiresPermissions(value = "vipPurchaseHistoryExamine")
    public Ret examine(@RequestBody VipPurchaseHistory vipPurchaseHistory) {
        return vipPurchaseHistoryService.examine(vipPurchaseHistory,isProxy(),getUcode());
    }



    @DeleteMapping
    @BussinessLog(value = "删除Vip购买记录", key = "id")
    @RequiresPermissions(value = "vipPurchaseHistoryDelete")
    public Ret remove(Long id) {
        if (id == null) {
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }
        vipPurchaseHistoryService.delete(id);
        sysLogService.addSysLog(getUsername(), id, "", "PC", SysLogEnum.DELETE_VIP_PURCHASE_HISTORY_INFO);
        return Rets.success();
    }

    /**
     * 导出
     *
     * @return
     */
    @GetMapping(value = "/vipPurchaseHistoryExportV2")
//	@RequiresPermissions(value = "vipPurchaseHistoryExportV2") TODO 权限
    public void exportV2(HttpServletResponse  response, @RequestParam(required = false) String account, @RequestParam(required = false) String previousViPType,
                           @RequestParam(required = false) String afterViPType, @RequestParam(required = false) String paymentMethod,
                           @RequestParam(required = false) String expireTimes, @RequestParam(required = false) String expireTimee,
                           @RequestParam(required = false) Integer whetherToPay, @RequestParam(required = false) String sourceInvitationCode,
                           @RequestParam(required = false) String gmt, @RequestParam(required = false) String idw,
                           @RequestParam(required = false) String depositAddress, @RequestParam(required = false) String isVip) {
        Page<VipPurchaseHistory> page = new PageFactory<VipPurchaseHistory>().defaultPage();

        if (isProxy()) {
            page.addFilter("sourceInvitationCode", getUcode());
        } else if (StringUtil.isNotEmpty(sourceInvitationCode)) {
            User user = userService.get(SearchFilter.build("account", sourceInvitationCode));
            if (user != null) {
                page.addFilter("sourceInvitationCode", SearchFilter.Operator.LIKE, user.getUcode());
            } else {
                throw new RuntimeException("未找到此代理账号");
            }
        }
        page.addFilter("idw", idw);
        page.addFilter("account", account);
        if (StringUtil.isNotEmpty(previousViPType)) {
            page.addFilter("previousViPType", SearchFilter.Operator.IN, previousViPType.split("_"));
        }
        if (StringUtil.isNotEmpty(afterViPType)) {
            page.addFilter("afterViPType", SearchFilter.Operator.IN, afterViPType.split("_"));
        }
        page.addFilter("paymentMethod", paymentMethod);
        page.addFilter("whetherToPay", whetherToPay);
        page.addFilter("depositAddress", depositAddress);
        if (StringUtil.isNotEmpty(isVip) && "0".equals(isVip)) {
            page.addFilter("afterViPType", SearchFilter.Operator.NE, "v1");
        }
        if (StringUtil.isNotEmpty(expireTimes) && StringUtil.isNotEmpty(expireTimee)) {
//            expireTimes = DateUtil.getTimeByZone(expireTimes);
//            expireTimee = DateUtil.getTimeByZone(expireTimee);
            page.addFilter("createTime", SearchFilter.Operator.GTE, DateUtil.parseTime(expireTimes));
            page.addFilter("createTime", SearchFilter.Operator.LT, DateUtil.parseTime(expireTimee));
        }
        page = vipPurchaseHistoryService.queryPage(page);
        List<Map<String, Object>> list = (List<Map<String, Object>>) new VipPurchaseHistoryWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
        if (ObjUtil.isEmpty(page) || CollUtil.isEmpty(list)){
            throw new RuntimeException("查询为空");
        }
        vipPurchaseHistoryService.exportV2(response,list);
    }

}