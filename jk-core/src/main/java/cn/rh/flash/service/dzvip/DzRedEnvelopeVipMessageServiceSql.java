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
public class DzRedEnvelopeVipMessageServiceSql{


    public static String getDzRedEnvelopeVipMessage(String vip){
        return "SELECT * FROM t_dzvip_red_envelope_message WHERE FIND_IN_SET( '"+vip+"', vip_type ) > 0";
    }
}