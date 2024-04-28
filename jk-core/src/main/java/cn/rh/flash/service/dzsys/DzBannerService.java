package cn.rh.flash.service.dzsys;


import cn.rh.flash.bean.entity.dzsys.DzBanner;
import cn.rh.flash.bean.vo.api.BannerVo;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.dao.dzsys.DzBannerRepository;
import cn.rh.flash.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DzBannerService extends BaseService<DzBanner,Long,DzBannerRepository>  {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private DzBannerRepository dzBannerRepository;

    public List<BannerVo> findAllVo() {
        return queryAll(SearchFilter.build("bannerType", SearchFilter.Operator.NE, "0")).stream().map(v->{
            BannerVo bannerVo = new BannerVo();
            BeanUtils.copyProperties(v,bannerVo);
            return bannerVo;
        }).collect(Collectors.toList());
    }
}

