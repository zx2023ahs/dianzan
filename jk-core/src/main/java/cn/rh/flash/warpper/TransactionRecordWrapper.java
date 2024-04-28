package cn.rh.flash.warpper;

import cn.rh.flash.service.system.impl.ConstantFactory;

import java.util.List;
import java.util.Map;

public class TransactionRecordWrapper extends  BaseControllerWrapper{
    public TransactionRecordWrapper(List<Map<String, Object>> maps) {
        super(maps);
    }

    @Override
    protected void warpTheMap(Map<String, Object> map) {
        map.put("transactionType_str", ConstantFactory.me().getDictsByName("交易类型2",  map.get("transactionType")+""));
        map.put("additionAndSubtraction_str", ConstantFactory.me().getDictsByName("加减",  map.get("additionAndSubtraction")+""));
    }
}
