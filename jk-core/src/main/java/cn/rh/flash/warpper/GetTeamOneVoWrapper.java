package cn.rh.flash.warpper;

import cn.rh.flash.service.system.impl.ConstantFactory;

import java.util.List;
import java.util.Map;

public class GetTeamOneVoWrapper extends BaseControllerWrapper {


    public GetTeamOneVoWrapper(List<Map<String, Object>> list) {
        super(list);
    }

    @Override
    protected void warpTheMap(Map<String, Object> map) {
        map.put("vipTypeStr", ConstantFactory.me().getDictsByName("ViP类型",  map.get("vipType")+""));
    }
}
