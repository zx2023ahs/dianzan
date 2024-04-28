package cn.rh.flash.api.controller.dz.dzvip;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzvip.VipRebateRecord;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.dzvip.VipRebateRecordService;
import cn.rh.flash.service.system.FileService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.StringUtil;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.VipRebateRecordWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/dzvip/viprebaterecord")
public class VipRebateRecordController extends BaseController {
	private  Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private VipRebateRecordService vipRebateRecordService;
	@Autowired
	private FileService fileService;
	@Autowired
	private SysLogService sysLogService;
	@GetMapping(value = "/list")
	@RequiresPermissions(value = "vipRebateRecord")
	public Ret list(@RequestParam(required = false) String account, @RequestParam(required = false) String idw ,
					@RequestParam(required = false) String expireTimes, @RequestParam(required = false) String expireTimee,
					@RequestParam(required = false) String sourceInvitationCode,@RequestParam(required = false) Integer relevels,
					@RequestParam(required = false) String gmt) {
		Page<VipRebateRecord> page = new PageFactory<VipRebateRecord>().defaultPage();
		if (isProxy()){
			page.addFilter("sourceInvitationCode",getUcode());
		}else {
			page.addFilter("sourceInvitationCode",sourceInvitationCode  );
		}
		page.addFilter("account",account );
		page.addFilter("idw",idw );
		page.addFilter("relevels",relevels );
		page.addFilter(SearchFilter.build("fidw", SearchFilter.Operator.ISNULL));


//		if( StringUtil.isNotEmpty( expireTimes ) && StringUtil.isNotEmpty( expireTimee ) ){
//			page.addFilter("createTime", SearchFilter.Operator.BETWEEN, Arrays.asList( DateUtil.parseTime( expireTimes ),DateUtil.parseTime( expireTimee ) ) );
//		}
		if( StringUtil.isNotEmpty( expireTimes ) && StringUtil.isNotEmpty( expireTimee ) ) {
			//gmt = StringUtil.isEmpty( gmt ) ? TimeZone.getDefault().getID() : gmt;
//			expireTimes = DateUtil.getTimeByZone(expireTimes);
//			expireTimee = DateUtil.getTimeByZone(expireTimee);
			page.addFilter( "createTime", SearchFilter.Operator.GTE, DateUtil.parseTime( expireTimes ) );
			page.addFilter( "createTime", SearchFilter.Operator.LT, DateUtil.parseTime( expireTimee ) );
		}
		page = vipRebateRecordService.queryPage(page);

		List list = (List) new VipRebateRecordWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
		page.setRecords(list);
		return Rets.success(page);
	}
	@PostMapping
	@BussinessLog(value = "新增团队开通vip返佣记录", key = "name")
	@RequiresPermissions(value = "vipRebateRecordAdd")
	public Ret add(@RequestBody VipRebateRecord vipRebateRecord){

		vipRebateRecord.setSourceInvitationCode( getUcode() );
		vipRebateRecord.setIdw( new IdWorker().nextId()+"" );

		vipRebateRecordService.insert(vipRebateRecord);
		sysLogService.addSysLog(getUsername(),vipRebateRecord.getId(),vipRebateRecord.getAccount(),"PC", SysLogEnum.ADD_VIP_REBATE_RECORD_INFO);
		return Rets.success();
	}
	@PutMapping
	@BussinessLog(value = "更新团队开通vip返佣记录", key = "name")
	@RequiresPermissions(value = "vipRebateRecordUpdate")
	public Ret update(@RequestBody @Valid VipRebateRecord vipRebateRecord){
		vipRebateRecordService.update(vipRebateRecord);
		sysLogService.addSysLog(getUsername(),vipRebateRecord.getId(),vipRebateRecord.getAccount(),"PC", SysLogEnum.UPDATE_VIP_REBATE_RECORD_INFO);
		return Rets.success();
	}
	@DeleteMapping
	@BussinessLog(value = "删除团队开通vip返佣记录", key = "id")
	@RequiresPermissions(value = "vipRebateRecordDelete")
	public Ret remove(Long id){
		if (id == null) {
			throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
		}
		vipRebateRecordService.delete(id);
		sysLogService.addSysLog(getUsername(),id,"","PC", SysLogEnum.DELETE_VIP_REBATE_RECORD_INFO);
		return Rets.success();
	}

	@GetMapping(value = "/getAmountTotal")
	public Ret getAmountTotal(@RequestParam(required = false) String account, @RequestParam(required = false) String idw ,
					@RequestParam(required = false) String expireTimes, @RequestParam(required = false) String expireTimee,
					@RequestParam(required = false) String sourceInvitationCode,@RequestParam(required = false) Integer relevels,
					@RequestParam(required = false) String gmt) {
		List<SearchFilter> filters = new ArrayList<>();
		if (isProxy()){
			filters.add(SearchFilter.build("source_invitation_code",getUcode()));
		}else {
			if (StringUtil.isNotEmpty(sourceInvitationCode)){
				filters.add(SearchFilter.build("source_invitation_code",sourceInvitationCode ));
			}
		}
		if (StringUtil.isNotEmpty(account)){
			filters.add(SearchFilter.build("account",account ));
		}
		if (StringUtil.isNotEmpty(idw)){
			filters.add(SearchFilter.build("idw",idw ));
		}
		if (relevels!=null){
			filters.add(SearchFilter.build("relevels",relevels ));
		}
		filters.add(SearchFilter.build("fidw", SearchFilter.Operator.ISNULL));
		if( StringUtil.isNotEmpty( expireTimes ) && StringUtil.isNotEmpty( expireTimee ) ) {
//			expireTimes = DateUtil.getTimeByZone(expireTimes);
//			expireTimee = DateUtil.getTimeByZone(expireTimee);
			filters.add(SearchFilter.build( "create_time", SearchFilter.Operator.GTE,  expireTimes ));
			filters.add(SearchFilter.build("create_time", SearchFilter.Operator.LT,  expireTimee ));


		}
		Double amountTotal =vipRebateRecordService.getMoneySum(filters);
		return Rets.success(amountTotal);
	}


	@GetMapping(value = "/viprebaterecordExportV2")
//	@RequiresPermissions(value = "viprebaterecordExportV2") TODO 权限
	public void exportV2(HttpServletResponse response, @RequestParam(required = false) String account, @RequestParam(required = false) String idw ,
						   @RequestParam(required = false) String expireTimes, @RequestParam(required = false) String expireTimee,
						   @RequestParam(required = false) String sourceInvitationCode, @RequestParam(required = false) String gmt){
		Page<VipRebateRecord> page = new PageFactory<VipRebateRecord>().defaultPage();
		if (isProxy()){
			page.addFilter("sourceInvitationCode",getUcode());
		}else {
			page.addFilter("sourceInvitationCode",sourceInvitationCode  );
		}
		page.addFilter("account",account );
		page.addFilter("idw",idw );
		if( StringUtil.isNotEmpty( expireTimes ) && StringUtil.isNotEmpty( expireTimee ) ) {
//			expireTimes = DateUtil.getTimeByZone(expireTimes);
//			expireTimee = DateUtil.getTimeByZone(expireTimee);
			page.addFilter( "createTime", SearchFilter.Operator.GTE, DateUtil.parseTime( expireTimes ) );
			page.addFilter( "createTime", SearchFilter.Operator.LT, DateUtil.parseTime( expireTimee ) );
		}
		page = vipRebateRecordService.queryPage(page);
		List<Map<String,Object>> list = (List<Map<String,Object>>) new VipRebateRecordWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
		if (ObjUtil.isEmpty(page) || CollUtil.isEmpty(list)){
			throw new RuntimeException("查询为空");
		}
		vipRebateRecordService.exportV2(response,list);
	}
}