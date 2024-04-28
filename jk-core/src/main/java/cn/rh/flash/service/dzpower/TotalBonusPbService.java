package cn.rh.flash.service.dzpower;


import cn.rh.flash.bean.entity.dzpower.TotalBonusPb;
import cn.rh.flash.dao.dzpower.TotalBonusPbRepository;
import cn.rh.flash.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TotalBonusPbService extends BaseService<TotalBonusPb,Long,TotalBonusPbRepository>  {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private TotalBonusPbRepository totalBonusPbRepository;

}

