package cn.rh.flash.warpper;

import cn.rh.flash.service.system.impl.ConstantFactory;

import java.util.Map;

public class RefundRecordWrapper extends BaseControllerWrapper{


    public RefundRecordWrapper(Object obj) {
        super(obj);
    }
    @Override
    protected void warpTheMap(Map<String, Object> map) {
        map.put("vipType_str", ConstantFactory.me().getDictsByName("ViP类型",  map.get("vipType")+""));
    }
}
