package cn.rh.flash.service.dzuser;

import cn.rh.flash.bean.entity.dzuser.UserBalanceLockLog;
import cn.rh.flash.dao.dzuser.UserBalanceLockLogRepository;
import cn.rh.flash.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserBalanceLockLogService  extends BaseService<UserBalanceLockLog,Long, UserBalanceLockLogRepository> {

    @Autowired
    private  UserBalanceLockLogRepository userBalanceLockLogRepository;
}
