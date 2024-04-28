package cn.rh.flash.warpper;

import cn.rh.flash.service.system.impl.ConstantFactory;

import java.util.List;
import java.util.Map;

public class SmsMessageWrapper extends BaseControllerWrapper{
    public SmsMessageWrapper(List<Map<String, Object>> maps) {
        super(maps);
    }

    @Override
    protected void warpTheMap(Map<String, Object> map) {
        map.put("platformName_str", ConstantFactory.me().getDictsByName("短信平台",  map.get("platformName")+""));
        map.put("dzstatus_str", ConstantFactory.me().getDictsByName("状态",  map.get("dzstatus")+""));
    }
}
