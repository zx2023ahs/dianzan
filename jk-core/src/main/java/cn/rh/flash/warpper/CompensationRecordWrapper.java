package cn.rh.flash.warpper;

import cn.rh.flash.service.system.impl.ConstantFactory;

import java.util.List;
import java.util.Map;

public class CompensationRecordWrapper extends BaseControllerWrapper{
    public CompensationRecordWrapper(List<Map<String, Object>> maps) {
        super(maps);
    }

    @Override
    protected void warpTheMap(Map<String, Object> map) {
        map.put("additionAndSubtraction_str", ConstantFactory.me().getDictsByName("加减",  map.get("additionAndSubtraction")+""));
    }
}
