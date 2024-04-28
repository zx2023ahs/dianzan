package cn.rh.flash.service.dzuser;


import cn.rh.flash.bean.entity.dzuser.FalseTotal;
import cn.rh.flash.dao.dzuser.FalseTotalRepository;
import cn.rh.flash.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FalseTotalService extends BaseService<FalseTotal,Long,FalseTotalRepository>  {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private FalseTotalRepository falseTotalRepository;

}

