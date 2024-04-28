package cn.rh.flash.api.controller.dz.dzsys;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzsys.PaymentChannel;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.api.PaymentChannelVo;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.CacheDao;
import cn.rh.flash.service.dzsys.PaymentChannelService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.PaymentChannelWrapper;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/dzsys/paymentchannel")
public class PaymentChannelController extends BaseController {
	private  Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private PaymentChannelService paymentChannelService;
	@Autowired
	private SysLogService sysLogService;

	@Autowired
	private CacheDao cacheDao;

	@GetMapping(value = "/list")
	@RequiresPermissions(value = "paymentChannel")
	public Ret list(@RequestParam(required = false) Long id) {
		Page<PaymentChannel> page = new PageFactory<PaymentChannel>().defaultPage();
		page.addFilter("id",id);
		page = paymentChannelService.queryPage(page);

		List list = (List) new PaymentChannelWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
//		page.getRecords().forEach(x->cacheDao.hset(CacheDao.PAYMENT_CHANNEL,x.getChannelName(),x));
		page.setRecords(list);
		return Rets.success(page);
	}
	@PostMapping
	@BussinessLog(value = "新增支付通道", key = "name")
	@RequiresPermissions(value = "paymentChannelAdd")
	public Ret add( @Valid @RequestBody PaymentChannel paymentChannel){

		paymentChannelService.insert(paymentChannel);
		if ("CNY".equals(paymentChannel.getCurrency())){
			PaymentChannelVo vo=new PaymentChannelVo();
			BeanUtils.copyProperties(paymentChannel, vo);
			cacheDao.hset(CacheDao.PAYMENT_CHANNEL,paymentChannel.getChannelName(),vo);
		}
		sysLogService.addSysLog(getUsername(),paymentChannel.getId(),"","PC", SysLogEnum.ADD_PAYMENT_CHANNEL_INFO);
		return Rets.success();
	}
	@PutMapping
	@BussinessLog(value = "更新支付通道", key = "name")
	@RequiresPermissions(value = "paymentChannelUpdate")
	public Ret update( @Valid @RequestBody PaymentChannel paymentChannel){
		paymentChannelService.update(paymentChannel);
		if ("CNY".equals(paymentChannel.getCurrency())){
			PaymentChannelVo vo=new PaymentChannelVo();
			BeanUtils.copyProperties(paymentChannel, vo);
			cacheDao.hset(CacheDao.PAYMENT_CHANNEL,paymentChannel.getChannelName(),vo);
		}
		sysLogService.addSysLog(getUsername(),paymentChannel.getId(),"","PC", SysLogEnum.UPDATE_PAYMENT_CHANNEL_INFO);
		return Rets.success();
	}
	@DeleteMapping
	@BussinessLog(value = "删除支付通道", key = "id")
	@RequiresPermissions(value = "paymentChannelDelete")
	public Ret remove(Long id){
		if (id == null) {
			throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
		}
		cacheDao.hdel(CacheDao.PAYMENT_CHANNEL,paymentChannelService.get(id).getChannelName());
		paymentChannelService.delete(id);
		sysLogService.addSysLog(getUsername(),id,"","PC", SysLogEnum.DELETE_PAYMENT_CHANNEL_INFO);
		return Rets.success();
	}

	//刷新缓存
	@PostMapping("/refresh")
	@RequiresPermissions(value = "paymentChannelUpdate")
	public Ret refresh(){

		cacheDao.del(CacheDao.PAYMENT_CHANNEL);
		List<PaymentChannel> list = paymentChannelService.queryAll(SearchFilter.build("currency","CNY"));
		list.forEach(x->{
			PaymentChannelVo vo=new PaymentChannelVo();
			BeanUtils.copyProperties(x, vo);
			cacheDao.hset(CacheDao.PAYMENT_CHANNEL,x.getChannelName(),vo);
		});
//		Page<PaymentChannel> page = new Page<PaymentChannel>(1, 50);
//		page.addFilter("currency","CNY");
//		page = paymentChannelService.queryPage(page);
//		page.getRecords().forEach(x->{
//			PaymentChannelVo vo=new PaymentChannelVo();
//			BeanUtils.copyProperties(x, vo);
//			cacheDao.hset(CacheDao.PAYMENT_CHANNEL,x.getChannelName(),vo);
//		});
		return Rets.success();
	}

}