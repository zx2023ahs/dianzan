package cn.rh.flash.warpper;

import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.service.system.impl.ConstantFactory;

import java.util.List;
import java.util.Map;

public class WithdrawalsRecordWrapper extends  BaseControllerWrapper{
    public WithdrawalsRecordWrapper(List<Map<String, Object>> maps) {
        super(maps);
    }

    @Override
    protected void warpTheMap(Map<String, Object> map) {
        UserInfo userInfo = (UserInfo) map.get("userInfo");
        map.put("rechargeStatus_str", ConstantFactory.me().getDictsByName("审核状态",  map.get("rechargeStatus")+""));
        map.put("channelName_str", ConstantFactory.me().getDictsByName("支付通道",  map.get("channelName")+""));
        map.put("vipType_str", ConstantFactory.me().getDictsByName("ViP类型",   userInfo!=null? userInfo.getVipType():""));

    }
}
