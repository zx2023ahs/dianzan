package cn.rh.flash.service.dzuser;


import cn.rh.flash.bean.entity.dzuser.TotalWithdrawalAmount;
import cn.rh.flash.dao.dzuser.TotalWithdrawalAmountRepository;
import cn.rh.flash.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TotalWithdrawalAmountService extends BaseService<TotalWithdrawalAmount,Long,TotalWithdrawalAmountRepository>  {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private TotalWithdrawalAmountRepository totalWithdrawalAmountRepository;

}

