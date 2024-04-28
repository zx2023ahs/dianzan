package cn.rh.flash.service.dzpool;


import cn.rh.flash.bean.dto.api.PoolResortMessagDto;
import cn.rh.flash.bean.entity.dzpool.Pool;
import cn.rh.flash.bean.entity.dzpool.PoolResortMessag;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.dao.dzpool.PoolResortMessagRepository;

import cn.rh.flash.service.BaseService;
import cn.rh.flash.service.coom.dz.ApiUserCoom;
import cn.rh.flash.utils.IdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class PoolResortMessagService extends BaseService<PoolResortMessag,Long,PoolResortMessagRepository>  {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private PoolResortMessagRepository poolResortMessagRepository;
    @Autowired
    private ApiUserCoom apiUserCoom;
    @Autowired
    @Lazy
    private PoolService poolService;


    public Ret resort(PoolResortMessagDto dto, Pool pool) {
        UserInfo oneBySql = apiUserCoom.getOneBySql(apiUserCoom.getUserId());
        PoolResortMessag poolResortMessag=new PoolResortMessag();
        poolResortMessag.setIdw(new IdWorker().nextId() + "");
        poolResortMessag.setState(0);
        poolResortMessag.setUserPoolAmount(dto.getUserPoolAmount());
        poolResortMessag.setContent(dto.getContent());
        poolResortMessag.setUid(oneBySql.getId());
        poolResortMessag.setPoolIdw(pool.getIdw());
        poolResortMessag.setSourceInvitationCode(oneBySql.getSourceInvitationCode());
        poolResortMessag.setAccount(oneBySql.getAccount());
        insert(poolResortMessag);
        return Rets.success();
    }

    public Ret examine(cn.rh.flash.bean.dto.PoolResortMessagDto dto) {
        Pool pool = poolService.myPoolV2();
        PoolResortMessag poolResortMessag = get(dto.getId());
        Integer state = poolResortMessag.getState();
        if (state==1 || state==2 ){
            return Rets.failure("已经审核过!");
        }
        if (dto.getState() == 1){
            poolResortMessag.setState(dto.getState());
            poolResortMessag.setReceivedAmount(0.00);
        }
        if (dto.getState() == 2){
            if (pool.getAmount()<poolResortMessag.getUserPoolAmount()){
                return Rets.failure("基金池余额不足!");
            }
            poolResortMessag.setState(dto.getState());
            poolService.controls(poolResortMessag.getUserPoolAmount(),pool.getIdw(),"subtract",poolResortMessag.getUid());
            poolResortMessag.setReceivedAmount(poolResortMessag.getUserPoolAmount());
        }
        update(poolResortMessag);
        return Rets.success();
    }

}

