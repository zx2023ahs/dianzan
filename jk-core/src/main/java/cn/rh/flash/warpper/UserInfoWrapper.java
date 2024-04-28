package cn.rh.flash.warpper;

import cn.rh.flash.service.system.impl.ConstantFactory;

import java.util.List;
import java.util.Map;

public class UserInfoWrapper extends BaseControllerWrapper {


    public UserInfoWrapper(List<Map<String, Object>> list) {
        super(list);
    }
    @Override
    protected void warpTheMap(Map<String, Object> map) {
        map.put("userType_str", ConstantFactory.me().getDictsByName("用户类型",  map.get("userType")+""));
        map.put("dzstatus_str", ConstantFactory.me().getDictsByName("账号状态",  map.get("dzstatus")+""));
        map.put("vipType_str", ConstantFactory.me().getDictsByName("ViP类型",  map.get("vipType")+""));
    }
}
