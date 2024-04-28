package cn.rh.flash.warpper;

import cn.rh.flash.service.system.impl.ConstantFactory;

import java.util.List;
import java.util.Map;

public class MultilingualLangWrapper extends BaseControllerWrapper{
    public MultilingualLangWrapper(List<Map<String, Object>> maps) {
        super(maps);
    }

    @Override
    protected void warpTheMap(Map<String, Object> map) {
        map.put("langCode_str", ConstantFactory.me().getDictsByName("语种",  map.get("langCode")+""));
    }
}
