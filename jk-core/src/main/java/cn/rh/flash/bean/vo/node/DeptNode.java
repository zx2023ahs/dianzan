package cn.rh.flash.bean.vo.node;

import cn.rh.flash.bean.entity.system.Dept;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * DeptNode
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeptNode extends Dept {

    private List<DeptNode> children = null;

    public List<DeptNode> getChildren() {
        return children;
    }

    public void setChildren(List<DeptNode> children) {
        this.children = children;
    }

    public String getLabel() {
        return getSimplename();
    }
}
