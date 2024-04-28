package cn.rh.flash.service.system;

import cn.rh.flash.BaseApplicationStartTest;
import org.springframework.beans.factory.annotation.Autowired;


public class DeptServiceTest extends BaseApplicationStartTest {


    @Autowired
    private DeptService deptService;

//    @Test
//    public void tree() {
//        List<ZTreeNode> list = deptService.tree();
//        for (ZTreeNode treeNode : list) {
//            System.out.println(JsonUtil.toJson(treeNode));
//        }
//    }

}
