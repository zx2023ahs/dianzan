package cn.rh.flash.service.dzuser;


import cn.rh.flash.bean.entity.dzuser.TotalRechargeAmount;
import cn.rh.flash.dao.dzuser.TotalRechargeAmountRepository;
import cn.rh.flash.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TotalRechargeAmountService extends BaseService<TotalRechargeAmount,Long,TotalRechargeAmountRepository>  {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private TotalRechargeAmountRepository totalRechargeAmountRepository;

}

