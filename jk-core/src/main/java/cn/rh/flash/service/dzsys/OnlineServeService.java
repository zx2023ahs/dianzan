package cn.rh.flash.service.dzsys;


import cn.rh.flash.bean.entity.dzsys.OnlineServe;
import cn.rh.flash.bean.vo.api.OnlineServeVo;
import cn.rh.flash.bean.vo.query.DynamicSpecifications;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.dao.dzsys.OnlineServeRepository;
import cn.rh.flash.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OnlineServeService extends BaseService<OnlineServe,Long,OnlineServeRepository>  {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private OnlineServeRepository onlineServeRepository;

    public String getCustomerServiceLink( String onlinesType ) {
        Optional<OnlineServe> OnlineServe = onlineServeRepository.findOne(
                DynamicSpecifications.bySearchFilter(SearchFilter.build("onlinesType", onlinesType), OnlineServe.class)
        );
        if (OnlineServe.isPresent()) {
            OnlineServe onlineServe = OnlineServe.get();
            return onlineServe.getCustomerServiceLink();
        }else{
            return "";
        }
    }

    /**
     * 返回 客服信息
     * @return
     */
    public List<OnlineServeVo> getCustomerServiceLinkV2( ) {

        return onlineServeRepository.findAll(
                DynamicSpecifications.bySearchFilter( SearchFilter.build( "onlinesFlag", 1 ), OnlineServe.class )
        ).stream().map(v -> {
            OnlineServeVo dictVo = new OnlineServeVo();
            BeanUtils.copyProperties(v, dictVo);
            return dictVo;
        }).collect( Collectors.toList() );


    }

}

