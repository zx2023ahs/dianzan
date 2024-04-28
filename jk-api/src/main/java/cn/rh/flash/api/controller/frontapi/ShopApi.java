package cn.rh.flash.api.controller.frontapi;

import cn.rh.flash.api.interceptor.Requestxz.RequestLimit;
import cn.rh.flash.bean.dto.api.BuyVipDto;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.vo.api.RechargeOrderVo;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.service.coom.dz.ApiUserCoom;
import cn.rh.flash.service.coom.dz.ShopService;
import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.RedisUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Log4j2
@RestController
@RequestMapping("/api/shop")
@Api(tags = "商品购买接口")
public class ShopApi extends ApiUserCoom {

    @Autowired
    private ShopService shopService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ContentApi contentApi;

    @ApiOperationSupport(author = "yc")
    @ApiOperation( value = "购买VIP" , notes = "v1 版本")
    @PostMapping("/buyVip")
    @RequestLimit(count = 1,time = 2000)
    public Ret<RechargeOrderVo> buyVip(@Valid @RequestBody BuyVipDto buyVipDto) {

        // 枷锁防止重复调用
        String key = "pay_vip_"+contentApi.getUserId();
        boolean b = redisUtil.lock(key);
        if (b) {
            try {
                log.info("用户购买vip获取到锁,用户ID:{}",contentApi.getUserId());
                return shopService.buyVip(buyVipDto);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                redisUtil.delete(key);
            }
        }
        log.error("用户购买vip没有获取到锁,用户ID:{},时间:{}",contentApi.getUserId(), DateUtil.getTime());
        return Rets.failure(MessageTemplateEnum.REQUEST_LIMIT.getCode(), MessageTemplateEnum.REQUEST_LIMIT);
    }


    @ApiOperationSupport(author = "yc")
    @ApiOperation( value = "购买VIP权限" , notes = "v1 版本")
    @GetMapping("/isBuyVip")
    public Ret<RechargeOrderVo> isBuyVip( @RequestParam String vipIdw ) {
        return shopService.isBuyVip(vipIdw);
    }




}
