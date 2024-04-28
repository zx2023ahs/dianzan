package cn.rh.flash.api.controller.frontapi;

import cn.rh.flash.bean.dto.api.PrizeExchangeDto;
import cn.rh.flash.bean.dto.api.ScoreDto;
import cn.rh.flash.bean.entity.dzscore.ScorePrize;
import cn.rh.flash.bean.entity.dzscore.UserScore;
import cn.rh.flash.bean.entity.system.Dict;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.vo.dz.ScorePrizeVo;
import cn.rh.flash.bean.vo.dz.SignInSetVo;
import cn.rh.flash.bean.vo.dz.UserScoreVo;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.impl.MyRedissonLocker;
import cn.rh.flash.service.coom.dz.ApiUserCoom;
import cn.rh.flash.service.dzscore.*;
import cn.rh.flash.service.system.impl.ConstantFactory;
import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.StringUtil;
import cn.rh.flash.utils.factory.Page;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping("/api/score")
@Api(tags = "积分签到")
@CrossOrigin
public class ScoreApi extends ApiUserCoom {

    @Autowired
    private SignInService signInService;

    @Autowired
    private SignInSetService signInSetService;

    @Autowired
    private ScorePrizeService scorePrizeService;

    @Autowired
    private MyRedissonLocker myRedissonLocker;

    @Autowired
    private ScorePrizeRecordService scorePrizeRecordService;

    @Autowired
    private ContentApi contentApi;

    @Autowired
    private UserScoreService userScoreService;

    @ApiOperationSupport(author = "yc")
    @ApiOperation(value = "签到", notes = "v1 版本")
    @PostMapping("/signToday")
    public Ret signToday() {
        // 加锁防止重复调用
        String key = "sign_" + getAccount();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.lock(lock);

        if (b) {
            try {
                return signInService.signToday(getAccount());
            } catch (Exception e) {
                e.printStackTrace();
                return Rets.failure(e.getMessage());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        log.error("用户积分没有获取到锁,用户ID:{},时间:{}", contentApi.getAccount(), DateUtil.getTime());
        return Rets.failure(MessageTemplateEnum.REQUEST_LIMIT.getCode(), MessageTemplateEnum.REQUEST_LIMIT);

    }

    @ApiOperationSupport(author = "yc")
    @ApiOperation(value = "签到规则", notes = "v1 版本")
    @GetMapping("/signRule")
    public Ret<SignInSetVo> signRule() {
        return signInSetService.signRule(getAccount());
    }

    @ApiOperationSupport(author = "yc")
    @ApiOperation(value = "兑换奖品列表", notes = "v1 版本")
    @GetMapping("/prizeList")
    public Ret<List<ScorePrizeVo>> prizeList(@RequestParam String prizeType) {
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("prizeType", prizeType));
        List<ScorePrize> list = scorePrizeService.queryAll(filters, Sort.by(Sort.Order.asc("sort")));

        List<Dict> dictList = ConstantFactory.me().getDicts("ViP类型");
        Map<String, String> vipMap = dictList.stream().collect(Collectors.toMap(Dict::getNum, Dict::getName));

        List<ScorePrizeVo> prizeVos = list.stream().map(v -> {
            ScorePrizeVo scorePrizeVo = new ScorePrizeVo();
            scorePrizeVo.setScore(String.valueOf(v.getScore().intValue()));
            BeanUtils.copyProperties(v, scorePrizeVo);
            scorePrizeVo.setVipType(vipMap.get("v"+v.getMaxVip()));
            return scorePrizeVo;
        }).collect(Collectors.toList());
//        Collections.shuffle(prizeVos);
        return Rets.success(prizeVos);
    }

    @ApiOperationSupport(author = "yc")
    @ApiOperation(value = "兑换奖品", notes = "v1 版本")
    @PostMapping("/prizeExchange")
    public Ret prizeExchange(@Valid @RequestBody PrizeExchangeDto dto) {
        // 加锁防止重复调用
        String key = "sign_" + getAccount();
        RLock lock = myRedissonLocker.lock(key);
        boolean b = myRedissonLocker.lock(lock);
        if (b) {
            try {
                return scorePrizeService.prizeExchange(dto, getAccount());
            } catch (Exception e) {
                e.printStackTrace();
                return Rets.failure(e.getMessage());
            } finally {
                myRedissonLocker.unlock(lock);
            }
        }
        log.error("用户积分没有获取到锁,用户ID:{},时间:{}", contentApi.getAccount(), DateUtil.getTime());
        return Rets.failure(MessageTemplateEnum.REQUEST_LIMIT.getCode(), MessageTemplateEnum.REQUEST_LIMIT);
    }

    @ApiOperationSupport(author = "skj")
    @ApiOperation(value = "用户兑换记录", notes = "v1 版本")
    @PostMapping("/findScorePrizeRecordByUser")
    public Ret findScorePrizeRecordByUser(@RequestBody ScoreDto page) {
        return scorePrizeRecordService.findScorePrizeRecordByUser(page, getUserId());
    }

    @ApiOperationSupport(author = "skj")
    @ApiOperation(value = "用户签到记录", notes = "v1 版本")
    @PostMapping("/findSignInByUser")
    public Ret findSignInByUser(@RequestBody Page page) {
        return signInService.findSignInByUser(page, getUserId());
    }


    @ApiOperationSupport(author = "yd")
    @ApiOperation(value = "用户积分", notes = "v1 版本")
    @GetMapping("/getUserScore")
    public Ret getUserScore(@RequestParam String prizeType) {
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("prizeType", prizeType));
        filters.add(SearchFilter.build("account", getAccount()));
        UserScore userScore = userScoreService.get(filters);
        UserScoreVo vo=new UserScoreVo();
        if(StringUtil.isNotNullOrEmpty(userScore)){
            vo.setUserScore(userScore.getUserScore());
            vo.setPrizeType(userScore.getPrizeType());
        }else {
            vo.setPrizeType("8");
            vo.setUserScore(0.00);
        }
        return Rets.success(vo);
    }
}
