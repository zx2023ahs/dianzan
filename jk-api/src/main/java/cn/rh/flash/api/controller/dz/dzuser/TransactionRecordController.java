package cn.rh.flash.api.controller.dz.dzuser;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzuser.TransactionRecord;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.config.chinesePattern.ChinesePattern;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.dzuser.TransactionRecordService;
import cn.rh.flash.service.system.FileService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.StringUtil;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.TransactionRecordWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/dzuser/transaction")
public class TransactionRecordController extends BaseController {
	private  Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private TransactionRecordService transactionRecordService;
	@Autowired
	private SysLogService sysLogService;
	@Autowired
	private FileService fileService;

	@GetMapping(value = "/list")
	@RequiresPermissions(value = {"transactionRecord", "transactionRecord1", "transactionRecord2"}, logical = Logical.OR)
	public Ret list(@RequestParam(required = false) String transactionType ,@RequestParam(required = false) String orderNumber,
					@RequestParam(required = false) String account,@RequestParam(required = false) String expireTimes,
					@RequestParam(required = false) String expireTimee,@RequestParam(required = false) String gmt
	) {
		Page<TransactionRecord> page = new PageFactory<TransactionRecord>().defaultPage();
		if (isProxy()) {
			page.addFilter("sourceInvitationCode", getUcode());
		}
		// 22-12-30 去掉页面充电宝查询
		if (StringUtils.isEmpty(transactionType) || "5".equals(transactionType)){
			page.addFilter("transactionType",SearchFilter.Operator.NE,"5");
		}else {
			page.addFilter("transactionType",transactionType);
		}

		page.addFilter("orderNumber",orderNumber);
		page.addFilter("account",account);



//		if( StringUtil.isNotEmpty( expireTimes ) && StringUtil.isNotEmpty( expireTimee ) ){
//			page.addFilter("createTime", SearchFilter.Operator.BETWEEN, Arrays.asList( DateUtil.parseTime( expireTimes ),DateUtil.parseTime( expireTimee ) ) );
//		}

		if( StringUtil.isNotEmpty( expireTimes ) && StringUtil.isNotEmpty( expireTimee ) ) {
			//gmt = StringUtil.isEmpty( gmt ) ? TimeZone.getDefault().getID() : gmt;
			expireTimes = DateUtil.getTimeByZone(expireTimes);
			expireTimee = DateUtil.getTimeByZone(expireTimee);

			page.addFilter( "createTime", SearchFilter.Operator.GTE,  DateUtil.parseTime(expireTimes ) );
			page.addFilter( "createTime", SearchFilter.Operator.LT,  DateUtil.parseTime(expireTimee ) );
		}

		page = transactionRecordService.queryPage(page);

		List list = (List) new TransactionRecordWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
		page.setRecords(list);
		return Rets.success(page);
	}
	@PostMapping
	@BussinessLog(value = "新增交易记录", key = "name")
	@RequiresPermissions(value = "transactionRecordAdd")
	public Ret add(@RequestBody TransactionRecord transactionRecord){

		transactionRecord.setSourceInvitationCode( getUcode() );
		transactionRecord.setIdw( new IdWorker().nextId()+"" );

		transactionRecordService.insert(transactionRecord);
		sysLogService.addSysLog(getUsername(),transactionRecord.getId(),transactionRecord.getAccount(),"PC", SysLogEnum.ADD_TRANSACTION_RECORD_INFO);
		return Rets.success();
	}
	@PutMapping
	@BussinessLog(value = "更新交易记录", key = "name")
	@RequiresPermissions(value = "transactionRecordUpdate")
	public Ret update(@RequestBody @Validated(ChinesePattern.OnUpdate.class)TransactionRecord transactionRecord){
		transactionRecordService.update(transactionRecord);
		sysLogService.addSysLog(getUsername(),transactionRecord.getId(),transactionRecord.getAccount(),"PC", SysLogEnum.UPDATE_TRANSACTION_RECORD_INFO);
		return Rets.success();
	}
	@DeleteMapping
	@BussinessLog(value = "删除交易记录", key = "id")
	@RequiresPermissions(value = "transactionRecordDelete")
	public Ret remove(Long id){
		if (id == null) {
			throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
		}
		transactionRecordService.delete(id);
		sysLogService.addSysLog(getUsername(),id,"","PC", SysLogEnum.DELETE_TRANSACTION_RECORD_INFO);
		return Rets.success();
	}

	@GetMapping(value = "/transactionExportV2")
	@RequiresPermissions(value = "transactionExport")
	public void exportV2(HttpServletResponse response,
						 @RequestParam(required = false) String transactionType , @RequestParam(required = false) String orderNumber,
						 @RequestParam(required = false) String account, @RequestParam(required = false) String expireTimes,
						 @RequestParam(required = false) String expireTimee, @RequestParam(required = false) String gmt) {
		Page<TransactionRecord> page = new PageFactory<TransactionRecord>().defaultPage();
		if (isProxy()) {
			page.addFilter("sourceInvitationCode", getUcode());
		}
		if (StringUtils.isEmpty(transactionType) || "5".equals(transactionType)){
			page.addFilter("transactionType",SearchFilter.Operator.NE,"5");
		}else {
			page.addFilter("transactionType",transactionType);
		}
		page.addFilter("orderNumber",orderNumber);
		page.addFilter("account",account);


		if( StringUtil.isNotEmpty( expireTimes ) && StringUtil.isNotEmpty( expireTimee ) ) {
			expireTimes = DateUtil.getTimeByZone(expireTimes);
			expireTimee = DateUtil.getTimeByZone(expireTimee);
			page.addFilter( "createTime", SearchFilter.Operator.GTE,  DateUtil.parseTime(expireTimes ) );
			page.addFilter( "createTime", SearchFilter.Operator.LT,  DateUtil.parseTime(expireTimee ) );
		}
		page = transactionRecordService.queryPage(page);
		List<Map<String,Object>> list = (List<Map<String,Object>>) new TransactionRecordWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
		if (ObjUtil.isEmpty(page) || CollUtil.isEmpty(list)){
			throw new RuntimeException("查询为空");
		}
		transactionRecordService.exportV2(response,list);
	}



}