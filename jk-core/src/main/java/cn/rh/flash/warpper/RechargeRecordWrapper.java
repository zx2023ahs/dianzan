package cn.rh.flash.warpper;

import cn.rh.flash.service.system.impl.ConstantFactory;

import java.util.List;
import java.util.Map;

public class RechargeRecordWrapper extends BaseControllerWrapper{
    public RechargeRecordWrapper(List<Map<String, Object>> maps) {
        super(maps);
    }

    @Override
    protected void warpTheMap(Map<String, Object> map) {
        map.put("rechargeStatus_str", ConstantFactory.me().getDictsByName("充值状态",  map.get("rechargeStatus")+""));
        map.put("channelName_str", ConstantFactory.me().getDictsByName("支付通道",  map.get("channelName")+""));
        map.put("firstCharge_str", ConstantFactory.me().getDictsByName("是否首充",  map.get("firstCharge")+""));
    }
}
