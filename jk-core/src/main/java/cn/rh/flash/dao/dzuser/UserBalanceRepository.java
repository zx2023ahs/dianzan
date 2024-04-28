package cn.rh.flash.dao.dzuser;


import cn.rh.flash.bean.entity.dzuser.UserBalance;
import cn.rh.flash.dao.BaseRepository;


public interface UserBalanceRepository extends BaseRepository<UserBalance,Long>{


   /* @Transactional//事务的注解
    @Modifying//增删改必须有这个注解
    @Query( value = "update work_order set last_reply =?1  where id = ?2", nativeQuery = true)
    int updateByDzversion(Integer dzversion);*/
}

