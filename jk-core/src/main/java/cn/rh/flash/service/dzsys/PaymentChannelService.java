package cn.rh.flash.service.dzsys;


import cn.rh.flash.bean.entity.dzsys.PaymentChannel;
import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.bean.vo.api.PaymentChannelVo;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.CacheDao;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.dao.dzsys.PaymentChannelRepository;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.service.system.impl.ConstantFactory;
import cn.rh.flash.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentChannelService extends BaseService<PaymentChannel,Long,PaymentChannelRepository>  {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private PaymentChannelRepository paymentChannelRepository;
    @Autowired
    private ConfigCache configCache;
    @Autowired
    private CacheDao cacheDao;

    public List<PaymentChannelVo> findAllVo() {
        String pay = configCache.get(ConfigKeyEnum.SYSTEM_PAY_CHANNEL).trim();

        return this.queryAll(SearchFilter.build("channelName",StringUtil.isEmpty(pay)?"BiPay":pay)).stream().map(v -> {
            PaymentChannelVo paymentChannelVo = new PaymentChannelVo();
            BeanUtils.copyProperties(v, paymentChannelVo);
            paymentChannelVo.setChannelName(v.getChannelName());
            paymentChannelVo.setChannelValue(ConstantFactory.me().getDictsByName("支付通道",v.getChannelName()));
            return paymentChannelVo;
        }).collect(Collectors.toList());
    }

    public PaymentChannelVo getPaymentChannelVo(@NotNull String channelName){
        PaymentChannelVo vo = cacheDao.hget(CacheDao.PAYMENT_CHANNEL, channelName, PaymentChannelVo.class);
        if (vo==null){
            PaymentChannel paymentChannel = this.get(SearchFilter.build("channelName", channelName));
            vo=new PaymentChannelVo();
            BeanUtils.copyProperties(paymentChannel, vo);
            cacheDao.hset(CacheDao.PAYMENT_CHANNEL,channelName,vo);
        }
        return  vo;
    }
}

