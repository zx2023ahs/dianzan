package cn.rh.flash.dao.dzsys;


import cn.rh.flash.bean.entity.dzsys.CountryCode;
import cn.rh.flash.dao.BaseRepository;

import java.util.List;


public interface CountryCodeRepository extends BaseRepository<CountryCode,Long>{

    List<CountryCode> findByCountryCode(String countryCode);
}

