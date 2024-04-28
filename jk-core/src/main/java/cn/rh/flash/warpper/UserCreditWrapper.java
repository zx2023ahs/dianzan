package cn.rh.flash.warpper;

import cn.rh.flash.service.system.impl.ConstantFactory;

import java.util.List;
import java.util.Map;

public class UserCreditWrapper extends BaseControllerWrapper{

    public UserCreditWrapper(List<Map<String, Object>> maps) {
        super(maps);
    }

    @Override
    protected void warpTheMap(Map<String, Object> map) {
        map.put("status_str", ConstantFactory.me().getDictsByName("信誉分状态",  map.get("status")+""));
        map.put("vipType_str", ConstantFactory.me().getDictsByName("ViP类型",  map.get("vipType")+""));

    }
}
