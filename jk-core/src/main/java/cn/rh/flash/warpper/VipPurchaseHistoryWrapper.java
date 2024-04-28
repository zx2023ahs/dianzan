package cn.rh.flash.warpper;

import cn.rh.flash.service.system.impl.ConstantFactory;
import cn.rh.flash.utils.DateUtil;

import java.util.List;
import java.util.Map;

public class VipPurchaseHistoryWrapper  extends BaseControllerWrapper{
    public VipPurchaseHistoryWrapper(List<Map<String, Object>> maps) {
        super(maps);
    }

    @Override
    protected void warpTheMap(Map<String, Object> map) {

        map.put("previousViPType_str", ConstantFactory.me().getDictsByName("ViP类型",  map.get("previousViPType")+""));
        map.put("afterViPType_str", ConstantFactory.me().getDictsByName("ViP类型",  map.get("afterViPType")+""));

        map.put("paymentMethod_str", ConstantFactory.me().getDictsByName("支付方式",  map.get("paymentMethod")+""));
        map.put("whetherToPay_str", ConstantFactory.me().getDictsByName("支付状态",  map.get("whetherToPay")+""));
        map.put("channelName_str", ConstantFactory.me().getDictsByName("支付通道",  map.get("channelName")+""));

        // 到期时间
        String afterDayDate = "";
        Object modifyTime = map.get("modifyTime");
        Object validDate = map.get("validDate");
        if( modifyTime != null &&  validDate !=null ){
            afterDayDate = DateUtil.getAfterDayDate(DateUtil.parseTime(modifyTime + ""), validDate + "");
        }
        map.put("expireDate", afterDayDate );

    }
}
