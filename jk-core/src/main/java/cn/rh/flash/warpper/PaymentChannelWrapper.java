package cn.rh.flash.warpper;

import cn.rh.flash.service.system.impl.ConstantFactory;

import java.util.List;
import java.util.Map;

public class PaymentChannelWrapper extends BaseControllerWrapper{
    public PaymentChannelWrapper(List<Map<String, Object>> maps) {
        super(maps);
    }

    @Override
    protected void warpTheMap(Map<String, Object> map) {
        map.put("channelName_str", ConstantFactory.me().getDictsByName("支付通道",  map.get("channelName")+""));

    }
}
