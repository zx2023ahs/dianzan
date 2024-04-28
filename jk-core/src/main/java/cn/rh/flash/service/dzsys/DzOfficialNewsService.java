package cn.rh.flash.service.dzsys;


import cn.rh.flash.bean.dto.api.OfficialNewDto;
import cn.rh.flash.bean.entity.dzsys.DzOfficialNews;
import cn.rh.flash.bean.vo.api.OfficialNewsVo;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.impl.EhcacheDao;
import cn.rh.flash.dao.dzsys.DzOfficialNewsRepository;
import cn.rh.flash.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DzOfficialNewsService extends BaseService<DzOfficialNews,Long, DzOfficialNewsRepository>  {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private DzOfficialNewsRepository dzOfficialNewsRepository;

    @Autowired
    private EhcacheDao ehcacheDao;

    public List<OfficialNewsVo> findAllVo(OfficialNewDto officialNewDto) {

        List<SearchFilter> sealist = new ArrayList<>();
        sealist.add( SearchFilter.build("officialType", SearchFilter.Operator.EQ, officialNewDto.getOfficialType()) );
        sealist.add( SearchFilter.build("language", SearchFilter.Operator.EQ, officialNewDto.getLanguage() ) );

        List<DzOfficialNews> officialNewsList =  queryAll( sealist );

        // 对应语言没有  就默认提示 英文的
        if (officialNewsList.size()  == 0 ) {

            sealist = new ArrayList<>();
            sealist.add( SearchFilter.build("officialType", SearchFilter.Operator.EQ, officialNewDto.getOfficialType()) );
            sealist.add( SearchFilter.build("language", SearchFilter.Operator.EQ, "ZH_EN" ) );
            officialNewsList =  queryAll( sealist );
        }

        return officialNewsList.stream().map( v->{
            OfficialNewsVo vo = new OfficialNewsVo();
            BeanUtils.copyProperties(v,vo);
            return vo;
        }).collect(Collectors.toList());
    }

    public OfficialNewsVo findOneVo(OfficialNewDto officialNewDto) {
        List<SearchFilter> sealist = new ArrayList<>();
        sealist.add( SearchFilter.build("officialType", SearchFilter.Operator.EQ, officialNewDto.getOfficialType()) );
        sealist.add( SearchFilter.build("language", SearchFilter.Operator.EQ, officialNewDto.getLanguage() ) );
        DzOfficialNews v = get( sealist );
        if (v == null) {
            sealist = new ArrayList<>();
            sealist.add( SearchFilter.build("officialType", SearchFilter.Operator.EQ, officialNewDto.getOfficialType()) );
            sealist.add( SearchFilter.build("language", SearchFilter.Operator.EQ, "ZH_EN" ) );
            v = get( sealist );
            if (v == null) {
                v = new DzOfficialNews();
            }
        }
        OfficialNewsVo vo = new OfficialNewsVo();
        BeanUtils.copyProperties(v,vo);
        return vo;
    }
}

