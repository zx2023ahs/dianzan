package cn.rh.flash.service.dzpool;


import cn.rh.flash.bean.entity.dzpool.PoolParameter;
import cn.rh.flash.dao.dzpool.PoolParameterRepository;
import cn.rh.flash.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PoolParameterService extends BaseService<PoolParameter,Long, PoolParameterRepository>  {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private PoolParameterRepository poolParameterRepository;

}

