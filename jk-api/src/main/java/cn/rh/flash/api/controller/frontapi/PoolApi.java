package cn.rh.flash.api.controller.frontapi;

import cn.hutool.core.util.ObjUtil;
import cn.rh.flash.bean.dto.api.PoolResortMessagDto;
import cn.rh.flash.bean.entity.dzpool.Pool;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.coom.dz.ApiUserCoom;
import cn.rh.flash.service.dzpool.PoolContributeMessagService;
import cn.rh.flash.service.dzpool.PoolResortMessagService;
import cn.rh.flash.service.dzpool.PoolService;
import cn.rh.flash.service.dzpool.UserPoolService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/pool")
@Api(tags = "积金池")
public class PoolApi  extends ApiUserCoom {

    @Autowired
    private PoolService poolService;
    @Autowired
    private UserPoolService userPoolService;
    @Autowired
    private PoolContributeMessagService poolContributeMessagService;
    @Autowired
    private PoolResortMessagService poolResortMessagService;

    @GetMapping(value = "/myPool")
    @ApiOperation(value = "首页获取公积金池和消息通知", notes = "v1 版本")
    public Ret myPoolAndMessage() {
        return Rets.success(poolService.myPoolAndMessage());
    }

    @GetMapping(value = "/myPoolInformation")
    @ApiOperation(value = "获取自己的爱心等级和申请次数和捐献金额", notes = "v1 版本")
    public Ret myPoolInformation() {
        return Rets.success(userPoolService.myPoolInformation());
    }


    @GetMapping(value = "/contribute")
    @ApiOperation(value = "捐助", notes = "v1 版本")
    @ApiImplicitParams({@ApiImplicitParam(name ="amount" ,value = "捐献金额",dataType = "Double")})
    public Ret contribute(@RequestParam Double amount ) {
        // 获取最近的基金池
        Pool pool = poolService.myPool();
        return poolContributeMessagService.contribute(amount,pool.getIdw());
    }

    @GetMapping(value = "/getState")
    @ApiOperation(value = "是否可以求助(true:可以,false:不可以)", notes = "v1 版本")
    public Ret getState() {
        return Rets.success(userPoolService.getState());
    }


    @PostMapping(value = "/resort")
    @ApiOperation(value = "求助", notes = "v1 版本")
    public Ret resort(@Valid @RequestBody PoolResortMessagDto dto) {
        Boolean state = userPoolService.getState();
        if (!state){
            return Rets.failure(MessageTemplateEnum.NO_PERMISSION.getCode());
        }
        // 获取最近的基金池
        Pool pool = poolService.myPool();
        return poolResortMessagService.resort(dto,pool);
    }


}
