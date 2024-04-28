package cn.rh.flash.api.controller.frontapi;

import cn.rh.flash.bean.dto.api.OfficialNewDto;
import cn.rh.flash.bean.dto.api.VipMessageDetailDto;
import cn.rh.flash.bean.entity.system.FileInfo;
import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.bean.vo.api.*;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.cache.impl.MyRedissonLocker;
import cn.rh.flash.service.appv.AppvService;
import cn.rh.flash.service.coom.dz.ApiUserCoom;
import cn.rh.flash.service.dzsys.*;
import cn.rh.flash.service.dzvip.DzVipMessageService;
import cn.rh.flash.service.system.FileService;
import cn.rh.flash.service.system.impl.ConstantFactory;
import cn.rh.flash.utils.ContentType;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping("/api/content")
@Api(tags = "公共信息")
public class ContentApi extends ApiUserCoom {

    @Autowired
    private CountryCodeService countryCodeService;

    @Autowired
    private DzBannerService bannerService;

    @Autowired
    private DzOfficialNewsService officialNewsService;

    @Autowired
    private DzVipMessageService vipMessageService;

    @Autowired
    private FileService fileService;

    @Autowired
    private PaymentChannelService paymentChannelService;

    @Autowired
    private AppvService appvService;

    @Autowired
    private OnlineServeService onlineServeService;

    @Autowired
    private MyRedissonLocker myRedissonLocker;

    @Autowired
    private ConfigCache configCache;


    @ApiOperationSupport(author = "yc")
    @ApiOperation(value = "国家码列表", notes = "v1 版本")
    @GetMapping("/countryCode")
    public Ret<List<CountryCodeVo>> countryCode() {
        return Rets.success(countryCodeService.findAllVo());
    }

    @ApiOperationSupport(author = "yc")
    @ApiOperation(value = "轮播图列表", notes = "v1 版本")
    @GetMapping("/banner")
    public Ret<List<BannerVo>> banner() {
        return Rets.success(bannerService.findAllVo());
    }

    @ApiOperationSupport(author = "yc")
    @ApiOperation(value = "公告列表", notes = "v1 版本")
    @PostMapping("/officialNews")
    public Ret<List<OfficialNewsVo>> officialNews(@Valid @RequestBody OfficialNewDto officialNewDto) {
        return Rets.success(officialNewsService.findAllVo(officialNewDto));
    }


    @ApiOperationSupport(author = "jk")
    @ApiOperation(value = "系统参数", notes = "v1 版本")
    @PostMapping("/sysParm")
    public Ret<OfficialNewsVo> sysParm(@Valid @RequestBody OfficialNewDto officialNewDto) {
        return Rets.success(officialNewsService.findOneVo(officialNewDto));
    }

    @ApiOperationSupport(author = "yc")
    @ApiOperation(value = "会员信息列表", notes = "v1 版本")
    @GetMapping("/vipMessage")
    public Ret<List<VipMessageVo>> vipMessage() {
        return Rets.success(vipMessageService.findAllVo());
    }

    @ApiOperationSupport(author = "yc")
    @ApiOperation(value = "会员详情", notes = "v1 版本")
    @PostMapping("/vipMessageDetail")
    public Ret<VipMessageDetailVo> vipMessageDetail(@Valid @RequestBody VipMessageDetailDto vipMessageDetailDto) {
        return Rets.success(vipMessageService.getVipMessageDetail(vipMessageDetailDto));
    }

    @ApiOperationSupport(author = "jk")
    @ApiOperation(value = "任务类型", notes = "v1 版本")
    @GetMapping("/taskTypes")
    public Ret<List<ApiDictVo> > taskTypes() {
        return Rets.success(ConstantFactory.me().getDicts("收益类型").stream().map(v -> {
            ApiDictVo dictVo = new ApiDictVo();
            BeanUtils.copyProperties(v, dictVo);
            return dictVo;
        }).collect(Collectors.toList()));
    }

    @ApiOperationSupport(author = "jk")
    @ApiOperation(value = "ViP类型", notes = "v1 版本")
    @GetMapping("/vipTypes")
    public Ret<List<ApiDictVo> > vipTypes() {
        return Rets.success(ConstantFactory.me().getDicts("ViP类型").stream().map(v -> {
            ApiDictVo dictVo = new ApiDictVo();
            BeanUtils.copyProperties(v, dictVo);
            return dictVo;
        }).collect(Collectors.toList()));
    }


    @ApiOperationSupport(author = "yc")
    @ApiOperation(value = "支付渠道列表", notes = "v1 版本")
    @PostMapping("/payChannel")
    public Ret<List<PaymentChannelVo>> payChannel() {
        return Rets.success(paymentChannelService.findAllVo());
    }



    /**
     * 获取文件流
     */
    @GetMapping(value = "/getImg/{idFile}")
    public void getImgStream(HttpServletResponse response, @PathVariable("idFile") Long idFile) throws IOException {
        if (idFile == null) {
            return;
        }
        FileInfo fileInfo = fileService.get(idFile);
        FileInputStream fis = null;
        String suffix = "."+fileInfo.getRealFileName().split("\\.")[1];
        String contentType = ContentType.get(suffix);
        response.setContentType(contentType);
        try {
            OutputStream out = response.getOutputStream();
            File file = new File(fileInfo.getAblatePath());
            fis = new FileInputStream(file);
            byte[] b = new byte[fis.available()];
            fis.read(b);
            out.write(b);
            out.flush();
        } catch (Exception e) {
            throw e;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e2) {
                    log.error("close getImgStream error", e2);
                }
            }
        }
    }


    @ApiOperationSupport(author = "jk")
    @ApiOperation(value = "获取版本信息Ios", notes = "v1 版本")
    @PostMapping("/getVersionByIos")
    public Ret<AppvVo> getVersionByIos() {
        return Rets.success( appvService.getVersion("ios") );
    }

    @ApiOperationSupport(author = "jk")
    @ApiOperation(value = "获取版本信息Android", notes = "v1 版本")
    @PostMapping("/getVersionByAndroid")
    public Ret<AppvVo> getVersionByAndroid() {
        return Rets.success(appvService.getVersion("android"));
    }

    @ApiOperationSupport(author = "jk")
    @ApiOperation(value = "客服链接", notes = "v1 版本[ ty = t1  ]")
    @GetMapping("/getCustomerServiceLink")
    public Ret<String> getCustomerServiceLink( @RequestParam String ty) {
        return Rets.success( onlineServeService.getCustomerServiceLink(ty) );
    }

    @ApiOperationSupport(author = "jk")
    @ApiOperation(value = "客服链接集合", notes = "v2")
    @GetMapping("/getCustomerServiceLinkV2")
    public Ret<OnlineServeVo> getCustomerServiceLinkV2( ) {
        return Rets.success( onlineServeService.getCustomerServiceLinkV2() );
    }

    @ApiOperation(value = "获取短信间隔时间", notes = "v1 版本")
    @PostMapping("/messageTime")
    public Ret messageTime() {
        return Rets.success(configCache.get(ConfigKeyEnum.MESSAGE_CODE_TIME).trim());
    }

//    @GetMapping("/text")
//    @ApiOperation(value = "测试", notes = "v2")
//    public Ret text() {
//        String key = "text";
//        boolean b = redisUtil.lock(key);
//        if (b) {
//            try {
//                System.out.println("====拿到了!====");
//                return Rets.success();
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                redisUtil.delete(key);
//            }
//        }
//        return Rets.failure(MessageTemplateEnum.REQUEST_LIMIT.getCode(), MessageTemplateEnum.REQUEST_LIMIT);
//    }

}
