package cn.rh.flash.service.dzpool;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.rh.flash.bean.entity.dzpool.Pool;
import cn.rh.flash.bean.entity.dzpool.PoolContributeMessag;
import cn.rh.flash.bean.entity.dzpool.PoolResortMessag;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.exception.ApiException;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.api.PoolVo;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.dao.dzpool.PoolRepository;

import cn.rh.flash.service.BaseService;
import cn.rh.flash.service.coom.dz.ApiUserCoom;
import cn.rh.flash.service.coom.dz.RecordInformation;
import cn.rh.flash.utils.*;
import cn.rh.flash.utils.factory.Page;
import lombok.extern.log4j.Log4j2;
import org.nutz.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class PoolService extends BaseService<Pool,Long,PoolRepository>  {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private PoolRepository poolRepository;

    @Autowired
    private RecordInformation recordInformation;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ApiUserCoom apiUserCoom;
    @Autowired
    private UserPoolService userPoolService;

    @Autowired
    private PoolContributeMessagService poolContributeMessagService;

    @Autowired
    private PoolResortMessagService poolResortMessagService;


    @Transactional(rollbackFor = Exception.class)
    public void add(double money,String PoolIdw){
        UserInfo oneBySql = apiUserCoom.getOneBySql(apiUserCoom.getUserId());
        Pool pool = poolRepository.get("SELECT * FROM t_dzpool_pool WHERE idw = \"" + PoolIdw + "\"");
        double v = apiUserCoom.getUserBalance(apiUserCoom.getUserId()).doubleValue();
        // 交易记录 扣除金额
        Integer zc = 0;
        zc=recordInformation.transactionRecordMinus(oneBySql.getSourceInvitationCode(),  oneBySql.getId(), oneBySql.getAccount(),
                v, money, BigDecimalUtils.subtract(v, money),
                new IdWorker().nextId() + "", 21, "gjjc", "公积金捐献", "");
        if (ObjUtil.isEmpty(pool)){
            throw new ApiException(MessageTemplateEnum.FUND_EXIST);
        }
        if (zc==0){
            throw new ApiException(MessageTemplateEnum.TRANSACTION_FAILED);
        }
        // 添加用户关联爱心值表
        userPoolService.save(oneBySql,money);
        //添加用户捐助记录表
        poolContributeMessagService.save(oneBySql,money,pool);
        //基金池添加
        pool.setAmount(BigDecimalUtils.add(pool.getAmount(),money));
        update(pool);
    }

    @Transactional(rollbackFor = Exception.class)
    public  void subtract(double money,String PoolIdw,Long uid){
        UserInfo oneBySql = apiUserCoom.getOneBySql(uid);
        if (ObjUtil.isEmpty(oneBySql)){
            throw new ApplicationException(BizExceptionEnum.USER_EXIST);
        }
        Pool pool = poolRepository.get("SELECT * FROM t_dzpool_pool WHERE idw = \"" + PoolIdw + "\"");
        double v = apiUserCoom.getUserBalance(uid).doubleValue();
        //  交易记录 添加金额
        Integer zc = 0;
        zc = recordInformation.transactionRecordPlus(oneBySql.getSourceInvitationCode(),  oneBySql.getId(), oneBySql.getAccount(),
                v, money,  BigDecimalUtils.add(v, money),
                new IdWorker().nextId() + "", 22, "gjjc", "公积金补助", "");
        if (ObjUtil.isEmpty(pool)){
            throw new ApplicationException(BizExceptionEnum.POOL_EXIST);
        }
        if (zc==0){
            throw new ApplicationException(BizExceptionEnum.TRANSACTION_FAILED);
        }
        //基金池扣除
        pool.setAmount(BigDecimalUtils.subtract(pool.getAmount(),money));
        update(pool);
    }

    /**
     * 操作公积金池
     * @param money 金额
     * @param PoolIdw 唯一值
     * @param type add:+,subtract:-
     */
    public void controls(double money, String PoolIdw, String type, Long uid ){
        String key= "pool"+apiUserCoom.getUserId();
        boolean b = redisUtil.lock(key);
        if (b) {
            try {
                log.info("用户操作公积金池vip获取到锁,用户ID:{}",apiUserCoom.getUserId());
                switch (type){
                    case "add":
                        add(money, PoolIdw);
                        return;
                    case "subtract":
                        subtract(money, PoolIdw,uid);
                        return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                redisUtil.delete(key);
            }
        }
        log.error("用户操作公积金池没有获取到锁,用户ID:{},时间:{}",apiUserCoom.getUserId(), DateUtil.getTime());
        throw  new RuntimeException(MessageTemplateEnum.REQUEST_LIMIT.getName());
    }


    public Pool myPool() {
        Page<Pool> page = new Page<>(0,1);
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(Sort.Order.desc("createTime"));
        page.setSort(Sort.by(orders));
        page = queryPage(page);
        if (ObjUtil.isEmpty(page) || CollUtil.isEmpty(page.getRecords())){
            throw new ApiException(MessageTemplateEnum.FUND_EXIST);
        }
        return page.getRecords().get(0);
    }

    public Pool myPoolV2() {
        Page<Pool> page = new Page<>(0,1);
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(Sort.Order.desc("createTime"));
        page.setSort(Sort.by(orders));
        page = queryPage(page);
        if (ObjUtil.isEmpty(page) || CollUtil.isEmpty(page.getRecords())){
            throw new ApplicationException(BizExceptionEnum.POOL_EXIST);
        }
        return page.getRecords().get(0);
    }

    public Map<String,Object> myPoolAndMessage() {

        Map<String,Object> map=new HashMap<>();
        PoolVo poolVo=new PoolVo();
        BeanUtils.copyProperties(myPool(),poolVo);
        map.put("pool",poolVo);


        List<String> contributeStr = new ArrayList<>();
        Page<PoolContributeMessag> PoolContributeMessagPage = new Page<>(0,5);
        Sort PoolContributeMessagSort = Sort.by(Sort.Direction.DESC,"createTime");
        PoolContributeMessagPage.setSort(PoolContributeMessagSort);
        PoolContributeMessagPage = poolContributeMessagService.queryPage(PoolContributeMessagPage);
        if (ObjUtil.isNotEmpty(PoolContributeMessagPage) && CollUtil.isNotEmpty(PoolContributeMessagPage.getRecords())){
            for (PoolContributeMessag record : PoolContributeMessagPage.getRecords()) {
                contributeStr.add(StringUtil.addXing(record.getAccount()) +" 捐献"+new BigDecimal(record.getUserPoolAmount()).setScale(2,BigDecimal.ROUND_HALF_UP));
            }
        }
        map.put("contribute",contributeStr);


        List<String> resortStr = new ArrayList<>();
        Page<PoolResortMessag> PoolResortMessagPage = new Page<>(0,5);
        PoolResortMessagPage.addFilter("state",2);
        Sort PoolResortMessagPageSort = Sort.by(Sort.Direction.DESC,"createTime");
        PoolResortMessagPage.setSort(PoolResortMessagPageSort);
        PoolResortMessagPage = poolResortMessagService.queryPage(PoolResortMessagPage);
        if (ObjUtil.isNotEmpty(PoolResortMessagPage) && CollUtil.isNotEmpty(PoolResortMessagPage.getRecords())){
            for (PoolResortMessag record : PoolResortMessagPage.getRecords()) {
                resortStr.add(StringUtil.addXing(record.getAccount()) +"获得公积金池补助金 "+new BigDecimal(record.getReceivedAmount()).setScale(2,BigDecimal.ROUND_HALF_UP));
            }
        }
        map.put("resortStr",resortStr);
        return map;
    }
}

