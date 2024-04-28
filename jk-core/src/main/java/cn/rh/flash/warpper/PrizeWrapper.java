package cn.rh.flash.warpper;

import cn.rh.flash.service.system.impl.ConstantFactory;

import java.util.List;
import java.util.Map;

public class PrizeWrapper extends BaseControllerWrapper{

    public PrizeWrapper(List<Map<String, Object>> maps) {
        super(maps);
    }

    @Override
    protected void warpTheMap(Map<String, Object> map) {
        map.put("types_str", ConstantFactory.me().getDictsByName("奖品类型",  map.get("types")+""));
        map.put("prizeType_str", ConstantFactory.me().getDictsByName("活动类型",  map.get("prizeType")+""));
    }
}
