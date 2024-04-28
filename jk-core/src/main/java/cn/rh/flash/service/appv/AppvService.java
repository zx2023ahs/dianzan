package cn.rh.flash.service.appv;


import cn.rh.flash.bean.entity.appv.Appv;
import cn.rh.flash.bean.vo.api.AppvVo;
import cn.rh.flash.bean.vo.query.DynamicSpecifications;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.dao.appv.AppvRepository;
import cn.rh.flash.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AppvService extends BaseService<Appv,Long,AppvRepository>  {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private AppvRepository appvRepository;


    public AppvVo getVersion(String appType) {
        List<SearchFilter> searlist =  new    ArrayList<>();
        searlist.add( SearchFilter.build( "appType", SearchFilter.Operator.EQ, appType ));
        searlist.add( SearchFilter.build( "dzstatus", SearchFilter.Operator.EQ, 1 ));
        Specification<Appv> appType1 = DynamicSpecifications.bySearchFilter( searlist , Appv.class );
        Optional<Appv> one = appvRepository.findOne( appType1 );
        AppvVo appvVo = new AppvVo();
        one.ifPresent(appv -> BeanUtils.copyProperties(appv, appvVo));
        return appvVo;
    }

    @Override
    public Appv update(Appv record) {
        return super.update(record);
    }

}

