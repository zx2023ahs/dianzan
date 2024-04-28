package cn.rh.flash.service.dzuser;


import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.entity.dzuser.UserWalletAddress;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.vo.api.UserWalletAddressVo;
import cn.rh.flash.bean.vo.dzser.UserWalletVo;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.dao.dzuser.UserWalletAddressRepository;

import cn.rh.flash.service.BaseService;
import cn.rh.flash.utils.EasyExcelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserWalletAddressService extends BaseService<UserWalletAddress,Long,UserWalletAddressRepository>  {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private UserWalletAddressRepository userWalletAddressRepository;
    @Autowired
    private UserInfoService userInfoService;

    @Transactional(rollbackFor = Exception.class)
    public Ret addUserWalletAddress(UserWalletAddress userWalletAddress){

        UserInfo userInfo = userInfoService.get(SearchFilter.build("account", userWalletAddress.getAccount()));
        if (userInfo==null){
            return Rets.failure(MessageTemplateEnum.ACCOUNT_NOT_EXISTS.getCode(), MessageTemplateEnum.ACCOUNT_NOT_EXISTS);
        }
        userWalletAddress.setUid(userInfo.getId());
        userWalletAddress.setSourceInvitationCode(userInfo.getSourceInvitationCode());
        this.insert(userWalletAddress);
        return Rets.success();
    }


    @Transactional(rollbackFor = Exception.class)
    public Ret updateUserWalletAddress(UserWalletAddress userWalletAddress){
        ////todo
        this.update(userWalletAddress);
        return Rets.success();
    }

    public UserWalletAddress getByChannelAndUid(String channel,Long uid){
        List<SearchFilter> filterList=new ArrayList<>();
        filterList.add(SearchFilter.build("uid",uid));
        filterList.add(SearchFilter.build("platformName",channel));
        return this.get(filterList);
    }

    public void walletExport(HttpServletResponse response,List<UserWalletAddress> list){
        List<UserWalletVo> voList=new ArrayList<>();
        list.forEach(x->{
            UserWalletVo vo = new UserWalletVo();
            BeanUtils.copyProperties(x,vo);
            voList.add(vo);
        });
        EasyExcelUtil.export(response,"钱包地址数据",voList, UserWalletVo.class);
    }

}

