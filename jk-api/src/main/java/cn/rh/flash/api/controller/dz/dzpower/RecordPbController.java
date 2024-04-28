package cn.rh.flash.api.controller.dz.dzpower;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzpower.RecordPb;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.service.dzpower.RecordPbService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.system.FileService;
import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.StringUtil;
import cn.rh.flash.utils.factory.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/dzgoods/recordpb")
public class RecordPbController extends BaseController {
	private  Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private RecordPbService recordPbService;
	@Autowired
	private FileService fileService;
	@Autowired
	private SysLogService sysLogService;
	@GetMapping(value = "/list")
	@RequiresPermissions(value = "recordPb")
	public Ret list(@RequestParam(required = false) String account,@RequestParam(required = false) String sourceInvitationCode,
					@RequestParam(required = false) String expireTimes, @RequestParam(required = false) String expireTimee,
					@RequestParam(required = false) Integer relevels,@RequestParam(required = false) String gmt) {
		Page<RecordPb> page = new PageFactory<RecordPb>().defaultPage();
		page.addFilter("account",account);

		if (isProxy()){
			page.addFilter("sourceInvitationCode",getUcode());
		}else {
			page.addFilter("sourceInvitationCode",sourceInvitationCode  );
		}
		page.addFilter("relevels",relevels );
		page.addFilter(SearchFilter.build("fidw", SearchFilter.Operator.ISNULL));


//		if( StringUtil.isNotEmpty( expireTimes ) && StringUtil.isNotEmpty( expireTimee ) ){
//			page.addFilter("createTime", SearchFilter.Operator.BETWEEN, Arrays.asList( DateUtil.parseTime( expireTimes ), DateUtil.parseTime( expireTimee ) ) );
//		}
		if( StringUtil.isNotEmpty( expireTimes ) && StringUtil.isNotEmpty( expireTimee ) ) {
			//gmt = StringUtil.isEmpty( gmt ) ? TimeZone.getDefault().getID() : gmt;
//			expireTimes = DateUtil.getTimeByZone(expireTimes);
//			expireTimee = DateUtil.getTimeByZone(expireTimee);
			page.addFilter( "createTime", SearchFilter.Operator.GTE, DateUtil.parseTime( expireTimes ) );
			page.addFilter( "createTime", SearchFilter.Operator.LT, DateUtil.parseTime( expireTimee ) );
		}
		page = recordPbService.queryPage(page);
		return Rets.success(page);
	}
	@PostMapping
	@BussinessLog(value = "新增充电宝返佣记录", key = "name")
	@RequiresPermissions(value = "recordPbAdd")
	public Ret add( @Valid  @RequestBody RecordPb recordPb){
		recordPbService.insert(recordPb);
		sysLogService.addSysLog(getUsername(),recordPb.getId(),recordPb.getAccount(),"PC", SysLogEnum.ADD_RECORD_PB_INFO);
		return Rets.success();
	}
	@PutMapping
	@BussinessLog(value = "更新充电宝返佣记录", key = "name")
	@RequiresPermissions(value = "recordPbUpdate")
	public Ret update( @Valid @RequestBody RecordPb recordPb){
		recordPbService.update(recordPb);
		sysLogService.addSysLog(getUsername(),recordPb.getId(),recordPb.getAccount(),"PC", SysLogEnum.UPDATE_RECORD_PB_INFO);
		return Rets.success();
	}
	@DeleteMapping
	@BussinessLog(value = "删除充电宝返佣记录", key = "id")
	@RequiresPermissions(value = "recordPbDelete")
	public Ret remove(Long id){
		if (id == null) {
			throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
		}
		recordPbService.delete(id);
		sysLogService.addSysLog(getUsername(),id,"","PC", SysLogEnum.DELETE_RECORD_PB_INFO);
		return Rets.success();
	}

	@GetMapping(value = "/getAmountTotal")
	public Ret getAmountTotal(@RequestParam(required = false) String account,@RequestParam(required = false) String sourceInvitationCode,
					@RequestParam(required = false) String expireTimes, @RequestParam(required = false) String expireTimee,
					@RequestParam(required = false) Integer relevels,@RequestParam(required = false) String gmt) {

		List<SearchFilter> filters = new ArrayList<>();
		if (StringUtil.isNotEmpty(account)){
			filters.add(SearchFilter.build("account",account));
		}
		if (isProxy()){
			filters.add(SearchFilter.build("sourceInvitationCode",getUcode()));
		}else {
			if (StringUtil.isNotEmpty(sourceInvitationCode)){
				filters.add(SearchFilter.build("sourceInvitationCode",sourceInvitationCode ));
			}
		}
		if (relevels!=null){
			filters.add(SearchFilter.build("relevels",relevels));
		}
		filters.add(SearchFilter.build("fidw", SearchFilter.Operator.ISNULL));

		if( StringUtil.isNotEmpty( expireTimes ) && StringUtil.isNotEmpty( expireTimee ) ) {
			//gmt = StringUtil.isEmpty( gmt ) ? TimeZone.getDefault().getID() : gmt;
//			expireTimes = DateUtil.getTimeByZone(expireTimes);
//			expireTimee = DateUtil.getTimeByZone(expireTimee);

			filters.add(SearchFilter.build("createTime", SearchFilter.Operator.GTE,  expireTimes  ));
			filters.add(SearchFilter.build("createTime", SearchFilter.Operator.LT,  expireTimee ));

		}

		//Double amountTotal =recordPbs.stream().mapToDouble(RecordPb::getMoney).sum();
		return Rets.success(recordPbService.querySum(filters));
	}

	@GetMapping(value = "/recordpbExportV2")
//	@RequiresPermissions(value = "recordpbExport") TODO 权限
	public void exportV2(HttpServletResponse response, @RequestParam(required = false) String account, @RequestParam(required = false) String sourceInvitationCode,
						 @RequestParam(required = false) String expireTimes, @RequestParam(required = false) String expireTimee, @RequestParam(required = false) String gmt){
		Page<RecordPb> page = new PageFactory<RecordPb>().defaultPage();
		if (isProxy()){
			page.addFilter("sourceInvitationCode",getUcode());
		}else {
			page.addFilter("sourceInvitationCode",sourceInvitationCode  );
		}
		page.addFilter("account",account);
		if( StringUtil.isNotEmpty( expireTimes ) && StringUtil.isNotEmpty( expireTimee ) ) {
//			expireTimes = DateUtil.getTimeByZone(expireTimes);
//			expireTimee = DateUtil.getTimeByZone(expireTimee);
			page.addFilter( "createTime", SearchFilter.Operator.GTE, DateUtil.parseTime( expireTimes ) );
			page.addFilter( "createTime", SearchFilter.Operator.LT, DateUtil.parseTime( expireTimee ) );
		}
		page = recordPbService.queryPage(page);
		if (ObjectUtil.isEmpty(page)|| CollUtil.isEmpty(page.getRecords())){
			throw new RuntimeException("查询为空");
		}
		recordPbService.exportV2(response,page.getRecords());
	}


}