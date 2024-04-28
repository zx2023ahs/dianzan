package cn.rh.flash.warpper;

import cn.rh.flash.bean.entity.system.Dict;
import cn.rh.flash.service.system.impl.ConstantFactory;
import cn.rh.flash.utils.ToolUtil;

import java.util.List;
import java.util.Map;

/**
 * 字典列表的包装
 */
public class DictWrapper extends BaseControllerWrapper {

    public DictWrapper(Object list) {
        super(list);
    }

    @Override
    public void warpTheMap(Map<String, Object> map) {
        StringBuffer detail = new StringBuffer();
        Long id = (Long) map.get("id");
        List<Dict> dicts = ConstantFactory.me().findInDict(id);
        if (dicts != null) {
            for (Dict dict : dicts) {
                detail.append(dict.getNum() + ":" + dict.getName() + ",");
            }
            map.put("detail", ToolUtil.removeSuffix(detail.toString(), ","));
        }
    }

}
