package cn.rh.flash.warpper;

import cn.rh.flash.service.system.impl.ConstantFactory;

import java.util.List;
import java.util.Map;

public class LuckyDrawWrapper extends BaseControllerWrapper{

    public LuckyDrawWrapper(List<Map<String, Object>> maps) {
        super(maps);
    }

    @Override
    protected void warpTheMap(Map<String, Object> map) {
        map.put("status_str", ConstantFactory.me().getDictsByName("活动状态",  map.get("status")+""));
        map.put("prizeType_str", ConstantFactory.me().getDictsByName("活动类型",  map.get("prizeType")+""));
    }
}
