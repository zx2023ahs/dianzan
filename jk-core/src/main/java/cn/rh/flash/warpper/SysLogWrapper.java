package cn.rh.flash.warpper;

import cn.rh.flash.bean.enumeration.SysLogEnum;

import java.util.List;
import java.util.Map;

public class SysLogWrapper extends  BaseControllerWrapper {

    public SysLogWrapper(List<Map<String, Object>> maps) {
        super(maps);
    }

    @Override
    protected void warpTheMap(Map<String, Object> map) {
        SysLogEnum operation = (SysLogEnum) map.get("operation");
        map.put("operation_str", operation.getMessage());
    }
}
