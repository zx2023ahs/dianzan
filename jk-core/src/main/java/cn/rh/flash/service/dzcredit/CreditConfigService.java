package cn.rh.flash.service.dzcredit;


import cn.rh.flash.bean.entity.dzcredit.CreditConfig;
import cn.rh.flash.dao.dzcredit.CreditConfigRepository;
import cn.rh.flash.service.BaseService;
import org.springframework.stereotype.Service;

@Service
public class CreditConfigService extends BaseService<CreditConfig,Long, CreditConfigRepository> {
}
