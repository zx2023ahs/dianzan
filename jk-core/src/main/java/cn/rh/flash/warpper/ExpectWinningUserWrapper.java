package cn.rh.flash.warpper;

import cn.rh.flash.service.system.impl.ConstantFactory;

import java.util.List;
import java.util.Map;

public class ExpectWinningUserWrapper extends BaseControllerWrapper{

    public ExpectWinningUserWrapper(List<Map<String, Object>> maps) {
        super(maps);
    }

    @Override
    protected void warpTheMap(Map<String, Object> map) {
        map.put("isPrize_str", ConstantFactory.me().getDictsByName("是否中奖",  map.get("isPrize")+""));
        map.put("prizeType_str", ConstantFactory.me().getDictsByName("活动类型",  map.get("prizeType")+""));
    }
}
