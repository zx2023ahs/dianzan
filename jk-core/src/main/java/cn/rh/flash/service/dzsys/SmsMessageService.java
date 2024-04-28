package cn.rh.flash.service.dzsys;


import cn.rh.flash.bean.entity.dzsys.SmsMessage;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.dao.dzsys.SmsMessageRepository;
import cn.rh.flash.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SmsMessageService extends BaseService<SmsMessage, Long, SmsMessageRepository> {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private SmsMessageRepository smsMessageRepository;

    public boolean isSendPhoneCode() {
//        SmsMessage smsMessage = this.get(SearchFilter.build("platformName", "su"));
        List<SmsMessage> smsMessages = this.queryAll(SearchFilter.build("dzstatus", 1));
        if (smsMessages.size()==0) {
            return false;
        }
        return true;
    }
}

