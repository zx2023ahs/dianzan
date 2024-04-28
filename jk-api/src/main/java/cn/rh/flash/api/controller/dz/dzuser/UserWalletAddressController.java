package cn.rh.flash.api.controller.dz.dzuser;

import cn.rh.flash.api.controller.BaseController;
import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.core.BussinessLog;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.entity.dzuser.UserWalletAddress;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.dzuser.UserWalletAddressService;
import cn.rh.flash.utils.StringUtil;
import cn.rh.flash.utils.factory.Page;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping("/dzuser/walletaddress")
public class UserWalletAddressController extends BaseController {

	@Autowired
	private UserWalletAddressService userWalletAddressService;
	@Autowired
	private SysLogService sysLogService;

	@GetMapping(value = "/list")
	@RequiresPermissions(value = "userWalletAddress")
	public Ret list(@RequestParam(required = false) Long uid,
					@RequestParam(required = false) String account,
					@RequestParam(required = false) String platformName,
					@RequestParam(required = false) String walletAddress,
					@RequestParam(required = false) String walletName,
					@RequestParam(required = false) String channelType
	) {
		Page<UserWalletAddress> page = new PageFactory<UserWalletAddress>().defaultPage();
		if (isProxy()) {
			page.addFilter("sourceInvitationCode", getUcode());
		}
		page.addFilter("uid",uid);
		page.addFilter("account",account);
		page.addFilter("channelType",channelType);
		page.addFilter("walletName",SearchFilter.Operator.LIKE,walletName);
		if (StringUtil.isNotEmpty(platformName)){
			page.addFilter("platformName",SearchFilter.Operator.IN,platformName.split(","));
		}
		page.addFilter("walletAddress", walletAddress);
		page.setSort(Sort.by(Sort.Order.desc("modifyTime")));
		page = userWalletAddressService.queryPage(page);
		return Rets.success(page);
	}

	@PostMapping
	@BussinessLog(value = "新增用户钱包地址", key = "name")
	@RequiresPermissions(value = "userWalletAddressAdd")
	public Ret add(@RequestBody UserWalletAddress userWalletAddress){

		if (userWalletAddress==null||userWalletAddress.getAccount()==null){
			throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
		}

		userWalletAddress.setId(null);
		userWalletAddress.setModifyBy(getIdUser());
		userWalletAddressService.addUserWalletAddress(userWalletAddress);
		sysLogService.addSysLog(getUsername(), userWalletAddress.getUid(), userWalletAddress.getAccount(), "PC", SysLogEnum.UPDATE_WALLET_ADDR_INFO);
		return Rets.success();
	}

	@PutMapping
	@BussinessLog(value = "更新用户钱包地址", key = "name")
	@RequiresPermissions(value = "userWalletAddressUpdate")
	public Ret update(@RequestBody UserWalletAddress userWalletAddress){
		userWalletAddressService.updateUserWalletAddress(userWalletAddress);
		sysLogService.addSysLog(getUsername(), userWalletAddress.getUid(), userWalletAddress.getAccount(), "PC", SysLogEnum.UPDATE_WALLET_ADDR_INFO);
		return Rets.success();
	}


	@DeleteMapping
	@BussinessLog(value = "删除用户钱包地址", key = "id")
	@RequiresPermissions(value = "userWalletAddressDelete")
	public Ret remove(Long id) {
		if (id == null) {
			throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
		}
		UserWalletAddress userWalletAddress = userWalletAddressService.get(id);
		sysLogService.addSysLog(getUsername(), userWalletAddress.getUid(), userWalletAddress.getAccount(), "PC", SysLogEnum.DELETE_WALLET_ADDR_INFO);
		userWalletAddressService.delete(id);
		return Rets.success();
	}

	@GetMapping("/walletExport")
	@RequiresPermissions(value = "walletExport")
	public void walletExport(HttpServletResponse response,
							 @RequestParam(required = false) Long uid, @RequestParam(required = false) String account,
							 @RequestParam(required = false) String platformName, @RequestParam(required = false) String walletAddress,
							 @RequestParam(required = false) String walletName, @RequestParam(required = false) String channelType){
		Page<UserWalletAddress> page = new PageFactory<UserWalletAddress>().defaultPage();
		if (isProxy()) {
			page.addFilter("sourceInvitationCode", getUcode());
		}
		page.addFilter("uid",uid);
		page.addFilter("account",account);
		page.addFilter("channelType",channelType);
		page.addFilter("walletName",SearchFilter.Operator.LIKE,walletName);
		if (StringUtil.isNotEmpty(platformName)){
			page.addFilter("platformName",SearchFilter.Operator.IN,platformName.split(","));
		}
		page.addFilter("walletAddress", walletAddress);
		page.setSort(Sort.by(Sort.Order.desc("modifyTime")));
		page = userWalletAddressService.queryPage(page);
		userWalletAddressService.walletExport(response,page.getRecords());
	}

}