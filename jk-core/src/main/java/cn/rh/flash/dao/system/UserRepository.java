package cn.rh.flash.dao.system;


import cn.rh.flash.bean.entity.system.User;
import cn.rh.flash.dao.BaseRepository;
import org.springframework.data.jpa.repository.Query;


public interface UserRepository extends BaseRepository<User, Long> {

    User findByAccount(String account);

    User findByAccountAndStatusNot(String account, Integer status);

    @Query(value = "select * from t_sys_user a where a.id = 1 for update", nativeQuery = true)
    User findForUpdate();

    User findByPhone(String phone);




}
