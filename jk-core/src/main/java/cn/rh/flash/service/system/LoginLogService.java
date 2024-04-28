package cn.rh.flash.service.system;


import cn.rh.flash.bean.entity.system.LoginLog;
import cn.rh.flash.dao.system.LoginLogRepository;
import cn.rh.flash.service.BaseService;
import org.springframework.stereotype.Service;


@Service
public class LoginLogService extends BaseService<LoginLog, Long, LoginLogRepository> {

}
