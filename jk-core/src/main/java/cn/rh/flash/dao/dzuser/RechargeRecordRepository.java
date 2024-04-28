package cn.rh.flash.dao.dzuser;


import cn.rh.flash.bean.entity.dzuser.RechargeRecord;
import cn.rh.flash.dao.BaseRepository;


public interface RechargeRecordRepository extends BaseRepository<RechargeRecord,Long>{

    RechargeRecord findByOrderNumber(String orderNumber);
    RechargeRecord findByWithdrawalAddress(String withdrawalAddress);
}

