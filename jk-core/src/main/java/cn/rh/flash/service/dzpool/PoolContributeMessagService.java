package cn.rh.flash.service.dzpool;


import cn.hutool.core.lang.Assert;
import cn.rh.flash.bean.entity.dzpool.Pool;
import cn.rh.flash.bean.entity.dzpool.PoolContributeMessag;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.exception.ApiException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.dao.dzpool.PoolContributeMessagRepository;

import cn.rh.flash.service.BaseService;
import cn.rh.flash.service.coom.dz.ApiUserCoom;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.IdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class PoolContributeMessagService extends BaseService<PoolContributeMessag,Long,PoolContributeMessagRepository>  {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private PoolContributeMessagRepository poolContributeMessagRepository;
    @Autowired
    private ApiUserCoom apiUserCoom;
    @Autowired
    @Lazy
    private PoolService poolService;

    public Ret contribute(Double amount, String poolIdw) {

        if (amount <= 0){
            throw new ApiException(MessageTemplateEnum.THE_AMOUT_CANNOT_BE_0);    ////////// todo  不能为0
        }
        if (amount > apiUserCoom.getUserBalance(apiUserCoom.getUserId()).doubleValue()){
            throw new ApiException(MessageTemplateEnum.INSUFFICIENT_BALANCE);    ////////// todo  余额不足
        }
        poolService.controls(amount,poolIdw,"add",null);
        return Rets.success();
    }


    public void save(UserInfo oneBySql, double money, Pool pool) {
        PoolContributeMessag poolContributeMessag=new PoolContributeMessag();
        poolContributeMessag.setIdw(new IdWorker().nextId() + "");
        poolContributeMessag.setPoolIdw(pool.getIdw());
        poolContributeMessag.setSourceInvitationCode(oneBySql.getSourceInvitationCode());
        poolContributeMessag.setAccount(oneBySql.getAccount());
        poolContributeMessag.setUid(oneBySql.getId());
        poolContributeMessag.setUserPoolAmount(money);
        insert(poolContributeMessag);
    }


}

