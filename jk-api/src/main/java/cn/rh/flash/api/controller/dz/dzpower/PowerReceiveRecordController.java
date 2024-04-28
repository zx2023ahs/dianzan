package cn.rh.flash.api.controller.dz.dzpower;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.entity.dzpower.PowerReceiveRecord;
import cn.rh.flash.bean.entity.system.User;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.service.dzpower.PowerReceiveRecordService;
import cn.rh.flash.service.dzuser.UserInfoService;
import cn.rh.flash.service.system.FileService;
import cn.rh.flash.service.system.UserService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.StringUtil;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.PowerReceiveRecordWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/dzgoods/powerreceiverecord")
public class PowerReceiveRecordController extends BaseController {

    @Autowired
    private PowerReceiveRecordService powerReceiveRecordService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;

    @GetMapping(value = "/list")
    @RequiresPermissions(value = "powerReceiveRecord")
    public Ret list(@RequestParam(required = false) String account,@RequestParam(required = false) String taskidw,
                    @RequestParam(required = false) Integer status,@RequestParam(required = false) String vipType,
                    @RequestParam(required = false) String sourceInvitationCode,
                    @RequestParam(required = false) String startTimeStart, @RequestParam(required = false) String startTimeEnd) {
        Page<PowerReceiveRecord> page = new PageFactory<PowerReceiveRecord>().defaultPage();
        if (isProxy()){
            page.addFilter("sourceInvitationCode",getUcode());
        }else if( StringUtil.isNotEmpty( sourceInvitationCode ) ){
//            UserInfo userInfo = userInfoService.get( SearchFilter.build( "account", sourceInvitationCode ) );
//            if (userInfo != null) {
//                page.addFilter("sourceInvitationCode", SearchFilter.Operator.LIKE,  userInfo.getSourceInvitationCode() );
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
        page.addFilter("account",account);
        page.addFilter("taskidw",taskidw);
        page.addFilter("status",status);
        page.addFilter("vipType",vipType);

        if( StringUtil.isNotEmpty( startTimeStart ) && StringUtil.isNotEmpty( startTimeEnd ) ) {
            //gmt = StringUtil.isEmpty( gmt ) ? TimeZone.getDefault().getID() : gmt;
//            startTimeStart = DateUtil.getTimeByZone(startTimeStart);
//            startTimeEnd = DateUtil.getTimeByZone(startTimeEnd);
            page.addFilter( "startTime", SearchFilter.Operator.GTE, DateUtil.parseTime( startTimeStart ) );
            page.addFilter( "startTime", SearchFilter.Operator.LT, DateUtil.parseTime( startTimeEnd ) );
        }

        page = powerReceiveRecordService.queryPage(page);
        List list = (List) new PowerReceiveRecordWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
        page.setRecords(list);
        return Rets.success(page);
    }


    @GetMapping(value = "/exportV2")
  //  @RequiresPermissions(value = "exportV2")
    public void exportV2(HttpServletResponse response,
                        @RequestParam(required = false) String account, @RequestParam(required = false) String taskidw,
                        @RequestParam(required = false) Integer status, @RequestParam(required = false) String vipType,
                        @RequestParam(required = false) String sourceInvitationCode) {
        Page<PowerReceiveRecord> page = new PageFactory<PowerReceiveRecord>().defaultPage();
        if (isProxy()){
            page.addFilter("sourceInvitationCode",getUcode());
        }else if( StringUtil.isNotEmpty( sourceInvitationCode ) ){
            User user = userService.get(SearchFilter.build("account", sourceInvitationCode));
            if (user != null) {
                page.addFilter("sourceInvitationCode", SearchFilter.Operator.LIKE, user.getUcode());
            } else {
                throw  new RuntimeException("未找到此代理账号");
            }
        }
        page.addFilter("account",account);
        page.addFilter("taskidw",taskidw);
        page.addFilter("status",status);
        page.addFilter("vipType",vipType);
        page = powerReceiveRecordService.queryPage(page);
        if (CollUtil.isEmpty(page.getRecords()) || ObjectUtil.isEmpty(page)){
            throw new RuntimeException("查询为空");
        }
        powerReceiveRecordService.exportV2(response,page.getRecords());
    }
}
