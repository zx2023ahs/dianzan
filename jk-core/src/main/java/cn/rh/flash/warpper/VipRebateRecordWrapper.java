package cn.rh.flash.warpper;

import cn.rh.flash.service.system.impl.ConstantFactory;

import java.util.List;
import java.util.Map;

public class VipRebateRecordWrapper extends BaseControllerWrapper{
    public VipRebateRecordWrapper(List<Map<String, Object>> maps) {
        super(maps);
    }

    @Override
    protected void warpTheMap(Map<String, Object> map) {

        map.put("oldVipType_str", ConstantFactory.me().getDictsByName("ViP类型",  map.get("oldVipType")+"" ) );
        map.put("newVipType_str", ConstantFactory.me().getDictsByName("ViP类型",  map.get("newVipType")+"" ) );

    }
}
