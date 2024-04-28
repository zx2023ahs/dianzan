package cn.rh.flash.warpper;

import cn.rh.flash.service.system.impl.ConstantFactory;

import java.util.List;
import java.util.Map;

public class CreditRecordWrapper extends BaseControllerWrapper{

    public CreditRecordWrapper(List<Map<String, Object>> maps) {
        super(maps);
    }

    @Override
    protected void warpTheMap(Map<String, Object> map) {
        map.put("chargeStatus_str", ConstantFactory.me().getDictsByName("变更类型",  map.get("chargeStatus")+""));
    }
}
