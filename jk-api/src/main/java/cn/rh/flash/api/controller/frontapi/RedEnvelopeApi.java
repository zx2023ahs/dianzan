package cn.rh.flash.api.controller.frontapi;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.rh.flash.api.interceptor.Requestxz.RequestLimit;
import cn.rh.flash.bean.entity.dzprize.*;
import cn.rh.flash.bean.entity.dzuser.TotalBonusIncome;
import cn.rh.flash.bean.entity.dzvip.DzRedEnvelopeVipMessage;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.vo.api.RedEnvelopeReceiveVo;
import cn.rh.flash.bean.vo.api.RedEnvelopeVo;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.service.coom.dz.ApiUserCoom;
import cn.rh.flash.service.coom.dz.RecordInformation;
import cn.rh.flash.service.dzprize.*;
import cn.rh.flash.service.dzuser.RedEnvelopeService;
import cn.rh.flash.service.dzuser.TotalBonusIncomeService;
import cn.rh.flash.service.dzvip.DzRedEnvelopeVipMessageService;
import cn.rh.flash.utils.BigDecimalUtils;
import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.RandomUtil;
import cn.rh.flash.utils.factory.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/red/envelope")
@Api(tags = "红包Api")
@CrossOrigin
public class RedEnvelopeApi extends ApiUserCoom {

    @Autowired
    private RedEnvelopeService redEnvelopeService;


    @GetMapping(value = "/getRedEnvelopeFlag")
    @ApiOperation(value = "查询红包活动是否开启在有效期内", notes = "v1 版本")
    public Ret getRedEnvelopeFlag() {
        return redEnvelopeService.getRedEnvelopeFlag();
    }

    @GetMapping(value = "/getRedEnvelopeRecord")
    @ApiOperation(value = "用户领取记录", notes = "v1 版本")
    public Ret getRedEnvelopeRecord() {
        //活动未开启
        Ret redEnvelopeFlag = getRedEnvelopeFlag();
        if (!(boolean) redEnvelopeFlag.getData()) {
            return Rets.failure(MessageTemplateEnum.lUCKEDRAM_NOT_STARTED.getCode(), MessageTemplateEnum.lUCKEDRAM_NOT_STARTED);
        }
        return Rets.success(redEnvelopeService.getRedEnvelopeRecord());
    }


    @GetMapping(value = "/getRedEnvelopeCount")
    @ApiOperation(value = "用户剩余红包次数", notes = "v1 版本")
    public Ret getRedEnvelopeCount() {
        ///活动未开启
        Ret redEnvelopeFlag = getRedEnvelopeFlag();
        if (!(boolean) redEnvelopeFlag.getData()) {
            return Rets.failure(MessageTemplateEnum.lUCKEDRAM_NOT_STARTED.getCode(), MessageTemplateEnum.lUCKEDRAM_NOT_STARTED);
        }
        return Rets.success(redEnvelopeService.getRedEnvelopeCount());
    }


    @ApiOperation(value = "用户领取红包", notes = "v1 版本")
    @GetMapping(value = "/receiveRedEnvelope")
    @RequestLimit(time = 5000, count = 1)
    @Transactional
    public Ret receiveRedEnvelope() {
        //已过期无法调用
        Ret redEnvelopeFlag = getRedEnvelopeFlag();
        if (!(boolean) redEnvelopeFlag.getData()) {
            return Rets.failure(MessageTemplateEnum.lUCKEDRAM_NOT_STARTED.getCode(), MessageTemplateEnum.lUCKEDRAM_NOT_STARTED);
        }
        //次数用尽无法领取
        Ret redEnvelopeCount = getRedEnvelopeCount();
        long count = (long) redEnvelopeCount.getData();
        if (ObjUtil.isEmpty(count) || count <= 0) {
            return Rets.failure(MessageTemplateEnum.COUNT_RUN_OUT.getCode(), MessageTemplateEnum.COUNT_RUN_OUT);
        }
        return redEnvelopeService.receiveRedEnvelope();
    }


}
