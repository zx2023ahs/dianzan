package cn.rh.flash.service.dzpool;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.rh.flash.bean.entity.dzpool.PoolParameter;
import cn.rh.flash.bean.entity.dzpool.UserPool;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.bean.vo.api.MyPoolInformationVo;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.dao.dzpool.UserPoolRepository;

import cn.rh.flash.service.BaseService;
import cn.rh.flash.service.coom.dz.ApiUserCoom;
import cn.rh.flash.utils.BigDecimalUtils;
import cn.rh.flash.utils.IdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserPoolService extends BaseService<UserPool,Long,UserPoolRepository>  {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private UserPoolRepository userPoolRepository;
    @Autowired
    private PoolParameterService poolParameterService;
    @Autowired
    private ApiUserCoom apiUserCoom;
    @Autowired
    private ConfigCache configCache;

    public MyPoolInformationVo myPoolInformation() {
        //返回结果
        MyPoolInformationVo vo=new MyPoolInformationVo();
        //爱心等级
        List<PoolParameter> poolParameters = poolParameterService.queryAll();
        //用户管理爱心值
        UserPool userPool=null;
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("uid", apiUserCoom.getUserId()));
        List<UserPool> userPools = queryAll(filters);
        if (CollUtil.isNotEmpty(userPools) &&  userPools.size()==1){
            userPool=userPools.get(0);
        }
        // 用户跟等级都无 返回no
        if (CollUtil.isEmpty(poolParameters) &&  ObjUtil.isEmpty(userPool)){
            vo.setPoolLevel("no");
            vo.setUserAmount(0.00);
            vo.setPoolNumber(0);
            return vo;
        }
        //用户不存在 显示最小等级
        if (CollUtil.isNotEmpty(poolParameters) &&  ObjUtil.isEmpty(userPool)){
            //如果Level为no 赋值为最小值的等级
            PoolParameter min = poolParameters.stream().min(Comparator.comparingDouble(PoolParameter::getMinimum)).orElse(null);
            if (ObjUtil.isNotEmpty(min)){
                vo.setPoolLevel(min.getName());
                vo.setUserAmount(0.00);
                vo.setPoolNumber(0);
                return vo;
            }
        }
        //用户存在 取对应等级
        if (ObjUtil.isNotEmpty(userPool)){
            // 用户关联的捐献金额
            if (CollUtil.isNotEmpty(poolParameters)){
                for (PoolParameter poolParameter : poolParameters) {
                    if (userPool.getUserPoolAmount() >= poolParameter.getMinimum() && userPool.getUserPoolAmount()<= poolParameter.getMaximum()){
                        vo.setPoolLevel(poolParameter.getName());
                        break;
                    }
                }
            }

            //如果超出最后最大值算最后的等级
            PoolParameter max = poolParameters.stream().max(Comparator.comparingDouble(PoolParameter::getMaximum)).orElse(null);
            if (userPool.getUserPoolAmount() > max.getMaximum() && ObjUtil.isNotEmpty(max)){
                vo.setPoolLevel(max.getName());
                return vo;
            }
            vo.setUserAmount(userPool.getUserPoolAmount());
            vo.setPoolNumber(userPool.getResortNumber() != null ? userPool.getResortNumber() : 0);
            return vo;
        }
        return vo;
    }


    public void save(UserInfo oneBySql, double money) {
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("uid", oneBySql.getId()));
        List<UserPool> userPools = queryAll(filters);
        if (CollUtil.isEmpty(userPools)){
            UserPool userPool=new UserPool();
            userPool.setIdw(new IdWorker().nextId() + "");
            userPool.setSourceInvitationCode(oneBySql.getSourceInvitationCode());
            userPool.setUid(oneBySql.getId());
            userPool.setAccount(oneBySql.getAccount());
            userPool.setUserPoolAmount(BigDecimalUtils.add(0,money));
            insert(userPool);
        }else {
            UserPool userPool=userPools.get(0);
            userPool.setUserPoolAmount(BigDecimalUtils.add(userPool.getUserPoolAmount(),money));
            update(userPool);
        }
    }

    public Boolean getState() {
        MyPoolInformationVo vo = myPoolInformation();
        int number = vo.getPoolNumber();
        if (ObjUtil.isEmpty(number) || number<=0){
            return false;
        }
        int resortNumber =  Integer.parseInt(configCache.get(ConfigKeyEnum.RESORT_NUMBER).trim());
        if (number>=resortNumber){
            return true;
        }
        return false;
    }
}

