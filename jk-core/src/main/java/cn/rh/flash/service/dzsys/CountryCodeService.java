package cn.rh.flash.service.dzsys;


import cn.rh.flash.bean.entity.dzsys.CountryCode;
import cn.rh.flash.bean.vo.api.CountryCodeVo;
import cn.rh.flash.cache.CacheDao;
import cn.rh.flash.cache.impl.EhcacheDao;
import cn.rh.flash.dao.dzsys.CountryCodeRepository;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.utils.StringUtil;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CountryCodeService extends BaseService<CountryCode,Long,CountryCodeRepository>  {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private CountryCodeRepository countryCodeRepository;

    @Autowired
    private EhcacheDao ehcacheDao;

    public List<CountryCodeVo> findAllVo() {
        List<CountryCodeVo> countryCodeVos = new ArrayList<>();

        String value = ehcacheDao.lget(CacheDao.COUNTRYCODE);

        if (StringUtil.isNotEmpty(value)){
            countryCodeVos = JSONObject.parseArray(value, CountryCodeVo.class);
        }else {
            countryCodeVos = countryCodeRepository.findAll(Sort.by(Sort.Order.asc("queueNumber"))).stream().map(v -> {
                CountryCodeVo countryCodeVo = new CountryCodeVo();
                BeanUtils.copyProperties(v, countryCodeVo);
                return countryCodeVo;
            }).collect(Collectors.toList());

            ehcacheDao.lset(CacheDao.COUNTRYCODE,countryCodeVos);
        }

        return countryCodeVos;

    }
}

