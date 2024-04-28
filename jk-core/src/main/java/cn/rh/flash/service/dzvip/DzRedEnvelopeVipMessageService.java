package cn.rh.flash.service.dzvip;


import cn.rh.flash.bean.entity.dzvip.DzRedEnvelopeVipMessage;
import cn.rh.flash.dao.dzvip.DzRedEnvelopeVipMessageRepository;

import cn.rh.flash.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DzRedEnvelopeVipMessageService extends BaseService<DzRedEnvelopeVipMessage,Long,DzRedEnvelopeVipMessageRepository> {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private DzRedEnvelopeVipMessageRepository dzRedEnvelopeVipMessageRepository;

    public List<DzRedEnvelopeVipMessage> getDzRedEnvelopeVipMessage(String vip) {
        List<DzRedEnvelopeVipMessage> objects = (List<DzRedEnvelopeVipMessage>) dzRedEnvelopeVipMessageRepository.query(DzRedEnvelopeVipMessageServiceSql.getDzRedEnvelopeVipMessage(vip));
        System.out.println(objects);
        return objects;
    }


}