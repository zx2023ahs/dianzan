package cn.rh.flash.warpper;

import cn.rh.flash.service.system.impl.ConstantFactory;

import java.util.List;
import java.util.Map;

public class FalseDataWrapper extends BaseControllerWrapper {
    public FalseDataWrapper(List<Map<String, Object>> maps) {
        super(maps);
    }

    @Override
    protected void warpTheMap(Map<String, Object> map) {
        map.put("falseType_str", ConstantFactory.me().getDictsByName("造假方式",  map.get("falseType")+""));
        map.put("isDel_str", ConstantFactory.me().getDictsByName("是否",  map.get("isDel")+""));
    }
}
