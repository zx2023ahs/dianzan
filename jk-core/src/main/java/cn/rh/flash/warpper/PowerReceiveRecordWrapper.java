package cn.rh.flash.warpper;

import cn.rh.flash.service.system.impl.ConstantFactory;
import cn.rh.flash.utils.DateUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class PowerReceiveRecordWrapper extends BaseControllerWrapper  {

    public PowerReceiveRecordWrapper(List<Map<String, Object>> maps) {
        super(maps);
    }

    @Override
    protected void warpTheMap(Map<String, Object> map) {
        map.put("vipType_str", ConstantFactory.me().getDictsByName("ViP类型",  map.get("vipType")+""));
        map.put("status_str", ConstantFactory.me().getDictsByName("领取状态",  map.get("status")+""));
        Object endTime = map.get("endTime");
        String flg = "";
        boolean after = DateUtil.parse(DateUtil.getTime(), "yyyy-MM-dd HH:mm:ss").after((Date) endTime);
        if (!after) {
            // 营业中
            flg = "营业中";
        } else {
            flg = "已结束";
        }
        map.put("flg",flg);
    }
}
