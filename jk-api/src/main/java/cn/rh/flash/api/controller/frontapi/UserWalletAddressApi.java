package cn.rh.flash.api.controller.frontapi;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.rh.flash.api.interceptor.Requestxz.RequestLimit;
import cn.rh.flash.bean.dto.api.UserWalletAddressDto;
import cn.rh.flash.bean.entity.dzuser.UserBalance;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.entity.dzuser.UserWalletAddress;
import cn.rh.flash.bean.entity.dzvip.DzVipMessage;
import cn.rh.flash.bean.entity.system.Dict;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.exception.ApiException;
import cn.rh.flash.bean.vo.DictVo;
import cn.rh.flash.bean.vo.api.UserWalletAddressVo;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.service.coom.dz.ApiUserCoom;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.service.dzuser.UserBalanceService;
import cn.rh.flash.service.dzuser.UserWalletAddressService;
import cn.rh.flash.service.dzvip.DzVipMessageService;
import cn.rh.flash.service.system.impl.ConstantFactory;
import cn.rh.flash.utils.CoinAddressUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping("/api/user/wallet/address")
@Api(tags = "用户绑定钱包地址")
public class UserWalletAddressApi extends ApiUserCoom {
    @Autowired
    private UserWalletAddressService userWalletAddressService;
    @Autowired
    private DzVipMessageService dzVipMessageService;
    @Autowired
    private SysLogService sysLogService;
    @Autowired
    private UserBalanceService userBalanceService;

    @GetMapping(value = "/list")
    @ApiOperation( value = "查询自己的钱包地址" , notes = "v1 版本")
    public Ret list() {
        UserWalletAddress newUserWalletAddress = userWalletAddressService.get(SearchFilter.build("uid",getUserId()));
        if (ObjectUtil.isNotEmpty(newUserWalletAddress)) {
            UserWalletAddressVo userWalletAddressVo = new UserWalletAddressVo();
            BeanUtils.copyProperties(newUserWalletAddress, userWalletAddressVo);
            return Rets.success(userWalletAddressVo);
        }
        UserBalance old = userBalanceService.get(SearchFilter.build("uid", getUserId()));
        if (ObjectUtil.isNotEmpty(old) && StrUtil.isNotEmpty(old.getWalletAddress())){
            UserWalletAddressVo userWalletAddressVo = new UserWalletAddressVo();
            BeanUtils.copyProperties(old, userWalletAddressVo);
            return Rets.success(userWalletAddressVo);
        }
        return Rets.success();
    }

    @GetMapping(value = "/select")
    @ApiOperation( value = "查询常用的绑定通道" , notes = "v1 版本")
    public Ret select() {
        List<DictVo> dicVos=new ArrayList<>();
        List<Dict> dicts = ConstantFactory.me().getDicts("绑定通道");
        for (Dict dic : dicts) {
            DictVo dictVo= new DictVo();
            dictVo.setKey(dic.getNum());
            dictVo.setValue(dic.getName());
            dicVos.add(dictVo);
        }
        return Rets.success(dicVos);
    }

    @PostMapping
    @RequestLimit(count = 1, time = 1000)
    @ApiOperation( value = "设置自己的钱包地址" , notes = "v1 版本")
    public Ret add(@Valid @RequestBody UserWalletAddressDto dto){
        UserWalletAddress verify = verify(dto);
        try {
            userWalletAddressService.insert(verify);
            sysLogService.addSysLog(verify.getAccount(), verify.getId(), verify.getAccount(), "APP", SysLogEnum.SET_WALLET_ADDR_INFO);
        }catch (Exception e){
            return Rets.failure(MessageTemplateEnum.PARAM_NOT_EXIST.getCode(), MessageTemplateEnum.PARAM_NOT_EXIST);
        }
        return Rets.success();
    }
    


    UserWalletAddress verify(UserWalletAddressDto dto) {
        //钱包地址验证
        if ("BiPay".equals(dto.getPlatformName()) && !CoinAddressUtil.isTronAddress(dto.getWalletAddress())){
            throw new ApiException(MessageTemplateEnum.INVALID_ADDRESS);
        }
        if ("WalletPay".equals(dto.getPlatformName()) && !CoinAddressUtil.isTronAddress(dto.getWalletAddress())){
            throw new ApiException(MessageTemplateEnum.INVALID_ADDRESS);
        }
        //一个用户只能有一个地址
        if (CollUtil.isNotEmpty(userWalletAddressService.queryAll(SearchFilter.build("uid", getUserId())))){
            throw new ApiException(MessageTemplateEnum.EXISTS_ADDRESS);
        }
        //一个地址只能被绑定一次
        if (CollUtil.isNotEmpty(userWalletAddressService.queryAll(SearchFilter.build("walletAddress", dto.getWalletAddress())))){
            throw new ApiException(MessageTemplateEnum.EXISTS_ADDRESS);
        }
        //真是姓名
        UserInfo oneBySql = getOneBySql(getUserId());
        if (ObjectUtil.isEmpty(oneBySql) ||  !dto.getWalletName().equals(oneBySql.getRealName())){
            throw new ApiException(MessageTemplateEnum.REALNAME_EXIST);
        }
        UserWalletAddress userWalletAddressAdd = new UserWalletAddress();
        userWalletAddressAdd.setUid(getUserId());
        userWalletAddressAdd.setAccount(getAccount());
        userWalletAddressAdd.setSourceInvitationCode(getSourceInvitationCode());
        BeanUtils.copyProperties(dto,userWalletAddressAdd);
        return userWalletAddressAdd;
    }


}


















