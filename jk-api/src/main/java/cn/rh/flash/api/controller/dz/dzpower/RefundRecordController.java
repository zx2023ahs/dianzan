package cn.rh.flash.api.controller.dz.dzpower;


import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.entity.dzpower.RefundRecord;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.dzpower.RefundRecordService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.RefundRecordWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dzgoods/refundrecord")
public class RefundRecordController extends BaseController {

    @Autowired
    private RefundRecordService refundRecordService;

    @GetMapping(value = "/list")
    @RequiresPermissions(value = "refundRecord")
    public Ret list(@RequestParam(required = false) String account) {
        Page<RefundRecord> page = new PageFactory<RefundRecord>().defaultPage();
        page.addFilter("account",account);

        if (isProxy()){
            page.addFilter("sourceInvitationCode",getUcode());
        }
        page = refundRecordService.queryPage(page);
        List list = (List) new RefundRecordWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
        page.setRecords(list);
        return Rets.success(page);
    }
}
