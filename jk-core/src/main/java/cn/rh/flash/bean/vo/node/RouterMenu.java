package cn.rh.flash.bean.vo.node;

import cn.rh.flash.utils.Lists;
import lombok.Data;

import java.util.List;


@Data
public class RouterMenu {
    private Long id;
    private Long parentId;
    private String path;
    private String component;
    private String name;
    private Integer num;
    private Boolean hidden = false;
    private MenuMeta meta;
    private List<RouterMenu> children = Lists.newArrayList();

}
