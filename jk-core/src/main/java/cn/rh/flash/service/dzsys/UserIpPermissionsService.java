package cn.rh.flash.service.dzsys;


import cn.rh.flash.bean.entity.dzsys.UserIpPermissions;
import cn.rh.flash.dao.dzsys.UserIpPermissionsRepository;
import cn.rh.flash.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserIpPermissionsService extends BaseService<UserIpPermissions,Long, UserIpPermissionsRepository>  {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private UserIpPermissionsRepository userIpPermissionsRepository;

}

