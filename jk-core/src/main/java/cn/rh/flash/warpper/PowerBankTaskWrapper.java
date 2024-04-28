package cn.rh.flash.warpper;

import cn.rh.flash.service.system.impl.ConstantFactory;

import java.util.List;
import java.util.Map;

public class PowerBankTaskWrapper extends BaseControllerWrapper {

    public PowerBankTaskWrapper(List<Map<String, Object>> maps) {
        super(maps);
    }

    @Override
    protected void warpTheMap(Map<String, Object> map) {
        map.put("vipType_str", ConstantFactory.me().getDictsByName("ViP类型",  map.get("vipType")+""));
    }
}
