package cn.rh.flash.cache;

import cn.rh.flash.bean.entity.dzvip.DzVipMessage;

public interface DzVipMessageCache extends Cache{


    DzVipMessage getByVipType(String vipType);
}
