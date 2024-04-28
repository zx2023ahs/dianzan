package cn.rh.flash.warpper;

import cn.rh.flash.bean.vo.node.IsMenu;

import java.util.List;
import java.util.Map;

/**
 * 菜单列表的包装类
 */
public class MenuWrapper extends BaseControllerWrapper {

    public MenuWrapper(List<Map<String, Object>> list) {
        super(list);
    }

    @Override
    public void warpTheMap(Map<String, Object> map) {
        map.put("isMenuName", IsMenu.valueOf((Integer) map.get("ismenu")));
    }

}
