package cn.rh.flash.warpper;

import cn.rh.flash.service.system.impl.ConstantFactory;

import java.util.List;
import java.util.Map;

public class AppvWrapper extends BaseControllerWrapper{
    public AppvWrapper(List<Map<String, Object>> maps) {
        super(maps);
    }

    @Override
    protected void warpTheMap(Map<String, Object> map) {
        map.put("appType_str", ConstantFactory.me().getDictsByName("软件类型",  map.get("appType")+""));
        map.put("dzstatus_str", ConstantFactory.me().getDictsByName("状态",  map.get("dzstatus")+""));
    }
}
