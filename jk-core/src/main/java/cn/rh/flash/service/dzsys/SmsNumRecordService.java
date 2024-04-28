package cn.rh.flash.service.dzsys;


import cn.rh.flash.bean.entity.dzsys.SmsNumRecord;
import cn.rh.flash.dao.dzsys.SmsNumRecordRepository;
import cn.rh.flash.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsNumRecordService extends BaseService<SmsNumRecord,Long,SmsNumRecordRepository>  {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private SmsNumRecordRepository smsNumRecordRepository;

}

