package cn.rh.flash.warpper;

import cn.rh.flash.service.system.impl.ConstantFactory;

import java.util.Map;

public class VipMessageWrapper extends  BaseControllerWrapper{


    public VipMessageWrapper(Object obj) {
        super(obj);
    }

    @Override
    protected void warpTheMap(Map<String, Object> map) {
        map.put("vipType_str", ConstantFactory.me().getDictsByName("ViP类型",  map.get("vipType")+""));
        map.put("dzstatus_str", ConstantFactory.me().getDictsByName("状态",  map.get("dzstatus")+""));
        map.put("gear_str", ConstantFactory.me().getDictsByName("档次类型",  map.get("gearCode")+""));
    }
}
