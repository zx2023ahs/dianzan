package cn.rh.flash.api.controller.frontapi;


import cn.rh.flash.bean.entity.dzcredit.UserCredit;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.service.coom.dz.ApiUserCoom;
import cn.rh.flash.service.dzcredit.CreditConfigService;
import cn.rh.flash.service.dzcredit.UserCreditService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/api/credit")
@Api(tags = "信誉分")
@CrossOrigin
public class CreditApi extends ApiUserCoom {

    @Autowired
    private CreditConfigService creditConfigService;

    @Autowired
    private UserCreditService userCreditService;

    @ApiOperationSupport(author = "skj")
    @ApiOperation(value = "获取信誉分配置与当前用户信誉分", notes = "v1 版本")
    @PostMapping("/getCreditConfig")
    public Ret getCreditConfig() {
        // 信誉分配置
//        List<CreditConfig> creditConfigs = creditConfigService.queryAll(Lists.newArrayList(), Sort.by(Sort.Direction.ASC, "creditMax"));
//        List<Integer> creditMaxs = creditConfigs.stream().map(CreditConfig::getCreditMax).collect(Collectors.toList());
        // 及格线  跟最高值
        Integer[] creditMaxs = {70,140};

        UserCredit userCredit = userCreditService.get(SearchFilter.build("account", getAccount()));
        Map result = new HashMap<>();
        result.put("config", creditMaxs);
        result.put("credit", userCredit == null ? 0 : userCredit.getCredit());
        return Rets.success(result);
    }


}
