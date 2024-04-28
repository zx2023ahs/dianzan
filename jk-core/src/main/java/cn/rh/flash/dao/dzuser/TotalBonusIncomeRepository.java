package cn.rh.flash.dao.dzuser;


import cn.rh.flash.bean.entity.dzuser.TotalBonusIncome;
import cn.rh.flash.dao.BaseRepository;


public interface TotalBonusIncomeRepository extends BaseRepository<TotalBonusIncome,Long>{

    TotalBonusIncome findByUid(Long uid);
}

