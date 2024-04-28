package cn.rh.flash.api.controller.frontapi;

import cn.rh.flash.api.interceptor.Requestxz.RequestLimit;
import cn.rh.flash.bean.dto.api.DrawIncomeDto;
import cn.rh.flash.bean.dto.api.PowerBankTaskDto;
import cn.rh.flash.bean.dto.api.PowerBankVoDto;
import cn.rh.flash.bean.dto.api.ReceiveRecordDto;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.vo.api.PowerBankTaskVo;
import cn.rh.flash.bean.vo.api.PowerBankVo;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.cache.impl.MyRedissonLocker;
import cn.rh.flash.service.dzpower.PowerBankService;
import cn.rh.flash.service.dzpower.PowerBankTaskService;
import cn.rh.flash.service.dzpower.PowerReceiveRecordService;
import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.RedisUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/pb")
@Api(tags = "充电宝")
public class PowerBankApi {

    // 充电宝服務
    @Autowired
    private PowerBankService powerBankService;
    @Autowired
    private PowerBankTaskService powerBankTaskService;
    @Autowired
    private ContentApi contentApi;
    @Autowired
    private PowerReceiveRecordService receiveRecordService;

    @Autowired
    private MyRedissonLocker myRedissonLocker;

    @Autowired
    private RedisUtil redisUtil;

    @ApiOperationSupport(author = "jk")
    @ApiOperation(value = "获取充电宝", notes = "v1 版本")
    @PostMapping("/getPowerBank")
    public Ret<List<PowerBankVo>> getPowerBank() {
        List<PowerBankVo> listForPowerBankVo = powerBankService.getListForPowerBankVo(1);
        return Rets.success(listForPowerBankVo);
    }

    @ApiOperationSupport(author = "jk")
    @ApiOperation(value = "获取充电宝返佣任务", notes = "v1 版本")
    @PostMapping("/getPowerBankRebateRecord")
    public Ret<List<PowerBankVo>> getPowerBankRebateRecord(@Valid @RequestBody PowerBankVoDto powerBankVoDto) {
        return Rets.success(powerBankService.getPowerBankRebateRecord(powerBankVoDto, contentApi.getUserId()));
    }





    @ApiOperationSupport(author = "skj")
    @ApiOperation(value = "获取正在进行中充电宝任务", notes = "v2 版本")
    @PostMapping("/getPowerBankTaskV2")
    public Ret<List<PowerBankTaskVo>> getPowerBankTaskV2(@Valid @RequestBody PowerBankVoDto powerBankVoDto) {
        return Rets.success(powerBankTaskService.getPowerBankTaskV2(powerBankVoDto, contentApi.getUserId()));
    }

    @ApiOperationSupport(author = "skj")
    @ApiOperation(value = "点击开始运营充电宝", notes = "v2 版本")
    @PostMapping("/startPowerBankTask")
    @RequestLimit(count = 1,time = 60000)
    public Ret startPowerBankTask(@RequestBody PowerBankTaskDto powerBankTaskDto) {

        // 枷锁防止重复调用
        String key = "start_power_bank_"+contentApi.getUserId();
        boolean b = redisUtil.lock(key);
        if (b) {
            try {
                log.info("运营充电宝获取到锁,用户ID:{},用户账号:{}",contentApi.getUserId(),contentApi.getAccount());
                return powerBankTaskService.startPowerBankTask(powerBankTaskDto.getIdw(), contentApi.getUserId());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                redisUtil.delete(key);
            }
        }
        log.error("运营充电宝没有获取到锁,用户ID:{},用户账号:{},时间:{}",contentApi.getUserId(),contentApi.getAccount(), DateUtil.getTime());
        return Rets.failure(MessageTemplateEnum.REQUEST_LIMIT.getCode(), MessageTemplateEnum.REQUEST_LIMIT);
    }

    @ApiOperationSupport(author = "skj")
    @ApiOperation(value = "查看领取收益列表", notes = "v2 版本")
    @PostMapping("/getReceiveRecord")
    public Ret getReceiveRecord(@RequestBody ReceiveRecordDto receiveRecordDto) {
        return receiveRecordService.getReceiveRecord(receiveRecordDto, contentApi.getUserId());
    }

    @ApiOperationSupport(author = "skj")
    @ApiOperation(value = "手动领取收益", notes = "v2 版本")
    @PostMapping("/drawIncome")
    public Ret drawIncome(@RequestBody DrawIncomeDto drawIncomeDto) {

        // 加锁防止重复调用
        String key = "draw_income_"+contentApi.getUserId();
        boolean b = redisUtil.lock(key);
        if (b) {
            try {
                log.info("手动领取收益获取到锁,用户ID:{}",contentApi.getUserId());
                return receiveRecordService.drawIncome(drawIncomeDto.getIdws(), contentApi.getUserId());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                redisUtil.delete(key);
            }
        }
        log.error("手动领取收益没有获取到锁,用户ID:{},时间:{}",contentApi.getUserId(), DateUtil.getTime());
        return Rets.failure(MessageTemplateEnum.REQUEST_LIMIT.getCode(), MessageTemplateEnum.REQUEST_LIMIT);
    }

}
