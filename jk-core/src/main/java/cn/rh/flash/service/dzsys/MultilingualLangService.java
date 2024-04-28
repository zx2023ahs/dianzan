package cn.rh.flash.service.dzsys;


import cn.rh.flash.bean.entity.dzsys.MultilingualLang;
import cn.rh.flash.dao.dzsys.MultilingualLangRepository;
import cn.rh.flash.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MultilingualLangService extends BaseService<MultilingualLang,Long,MultilingualLangRepository>  {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private MultilingualLangRepository multilingualLangRepository;

}

