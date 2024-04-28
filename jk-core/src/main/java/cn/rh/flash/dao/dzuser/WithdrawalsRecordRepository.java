package cn.rh.flash.dao.dzuser;


import cn.rh.flash.bean.entity.dzuser.WithdrawalsRecord;
import cn.rh.flash.dao.BaseRepository;


public interface WithdrawalsRecordRepository extends BaseRepository<WithdrawalsRecord,Long>{

    WithdrawalsRecord findByOrderNumber(String orderNumber);

}

