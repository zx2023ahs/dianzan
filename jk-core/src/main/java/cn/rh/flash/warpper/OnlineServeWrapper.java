package cn.rh.flash.warpper;

import cn.rh.flash.service.system.impl.ConstantFactory;

import java.util.List;
import java.util.Map;

public class OnlineServeWrapper extends BaseControllerWrapper{

    public OnlineServeWrapper(List<Map<String, Object>> maps) {
        super(maps);
    }

    @Override
    protected void warpTheMap(Map<String, Object> map) {
        map.put("onlinesType_str", ConstantFactory.me().getDictsByName("在线客服类型",  map.get("onlinesType")+""));
        map.put("onlinesFlag_str",ConstantFactory.me().getDictsByName("状态",map.get("onlinesFlag")+""));
    }
}
