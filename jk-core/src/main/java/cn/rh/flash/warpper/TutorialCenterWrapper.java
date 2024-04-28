package cn.rh.flash.warpper;

import cn.rh.flash.service.system.impl.ConstantFactory;

import java.util.List;
import java.util.Map;

public class TutorialCenterWrapper  extends BaseControllerWrapper{
    public TutorialCenterWrapper(List<Map<String, Object>> maps) {
        super(maps);
    }

    @Override
    protected void warpTheMap(Map<String, Object> map) {
        map.put("type_str", ConstantFactory.me().getDictsByName("教程类型",  map.get("type")+""));
        map.put("status_str", ConstantFactory.me().getDictsByName("状态",  map.get("status")+""));
    }
}

