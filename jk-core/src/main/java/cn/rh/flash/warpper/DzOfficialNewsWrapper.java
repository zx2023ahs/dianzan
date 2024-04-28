package cn.rh.flash.warpper;

import cn.rh.flash.service.system.impl.ConstantFactory;

import java.util.List;
import java.util.Map;

public class DzOfficialNewsWrapper extends BaseControllerWrapper{
    public DzOfficialNewsWrapper(List<Map<String, Object>> maps) {
        super(maps);
    }

    @Override
    protected void warpTheMap(Map<String, Object> map) {
        map.put("officialType_str", ConstantFactory.me().getDictsByName("公告类型",  map.get("officialType")+""));
        map.put("language_str", ConstantFactory.me().getDictsByName("语种",  map.get("language")+""));
    }
}
