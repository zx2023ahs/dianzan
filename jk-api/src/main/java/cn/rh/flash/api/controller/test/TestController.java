package cn.rh.flash.api.controller.test;

import cn.rh.flash.bean.entity.dzprize.WinningRecord;
import cn.rh.flash.bean.enumeration.BizExceptionEnum;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.exception.ApplicationException;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.dzprize.WinningRecordService;
import cn.rh.flash.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * 测试工具
 * 启动api服务后，通过swagger地址：http://localhost:8082/swagger-ui.html 来做一些测试
 * @Date 2021/6/2 15:23
 * @Version 1.0
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private WinningRecordService winningRecordService;

    public static void main(String[] args) {
        System.out.println(  MessageTemplateEnum.TOKEN_EXPIRED );
    }

    @GetMapping("/test")
    public void test(@RequestParam("key") String key){
        if("lock".equals(key)){
            System.out.println(1);
        }

        if("ex".equals(key)){
            throw new ApplicationException(BizExceptionEnum.REQUEST_NULL);
        }

        if(key.startsWith("trans")){
            if(key.contains("yes")){
                System.out.println(2);
            }
            if(key.contains("no")){
                System.out.println(3);
            }

        }
    }

    @GetMapping("/test2")
    public Ret test2(){
       return Rets.failure( MessageTemplateEnum.TOKEN_EXPIRED.getCode(), MessageTemplateEnum.TOKEN_EXPIRED );
    }


    @GetMapping("/test3")
    public Ret test3(){
        ArrayList<WinningRecord> winningRecords = new ArrayList<>();
        for (int i=0;i<1000;i++){
            WinningRecord winningRecord = new WinningRecord();
            winningRecord.setIdw(new IdWorker().nextId() + "");
            winningRecord.setSourceInvitationCode("test");
            winningRecord.setUid(1L);
            winningRecord.setAmount(0.0);
            winningRecord.setPrizeType("8");
            winningRecord.setAccount("001");
            winningRecord.setPrizeIdw("TEST");
            winningRecord.setPrizeName("TEST");
            winningRecords.add(winningRecord);
        }

        winningRecordService.saveAll(winningRecords);
        Ret ret = new Ret();
        ret.setMsg("成功");
        return ret;
    }
}
