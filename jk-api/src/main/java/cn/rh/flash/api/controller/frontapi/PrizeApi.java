package cn.rh.flash.api.controller.frontapi;


import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.dto.PrizeDto;
import cn.rh.flash.bean.dto.StartPrizeDto;
import cn.rh.flash.bean.entity.dzprize.*;
import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.vo.api.MonopolyUserAndNumVo;
import cn.rh.flash.bean.vo.dz.PrizeVo;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.service.coom.dz.ApiUserCoom;
import cn.rh.flash.service.dzprize.*;
import cn.rh.flash.utils.*;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.WinningRecordWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@RestController
@RequestMapping("/api/prize")
@Api(tags = "抽奖接口")
public class PrizeApi extends ApiUserCoom {

    @Autowired
    private PrizeService prizeService;

    @Autowired
    private WinningRecordService winningRecordService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private PrizeNumService prizeNumService;

    @Autowired
    private ConfigCache configCache;

    @Autowired
    private LuckyDrawService luckyDrawService;

    @Autowired
    private HuntingRecordService huntingRecordService;

    @Autowired
    private MonopolyRecordService monopolyRecordService;

    @Autowired
    private MonopolyUserService monopolyUserService;



    /**
     * @Description: 获取抽奖转盘信息
     * @Param:
     * @return:
     * @Author: Skj
     */
    @ApiOperationSupport(author = "skj")
    @ApiOperation(value = "获取抽奖转盘信息", notes = "v1 版本")
    @PostMapping("/getLuckyDraw")
    public Ret getLuckyDraw(@RequestBody PrizeDto prizeDto) {
        List<LuckyDraw> list = luckyDrawService.queryAll(SearchFilter.build("prizeType", prizeDto.getPrizeType()));
        LuckyDraw luckyDraw = list.size() == 0 ? new LuckyDraw() : list.get(0);
        boolean b = DateUtil.betWeen(DateUtil.parseTime(DateUtil.getTime()), luckyDraw.getStartTime(), luckyDraw.getEndTime());
        luckyDraw.setExpire(b);
        return Rets.success(luckyDraw);
    }

    @ApiOperationSupport(author = "skj")
    @ApiOperation(value = "获取抽奖转盘开关", notes = "v1 版本")
    @GetMapping("/getLuckyDrawType")
    public Ret getLuckyDrawType() {
        List<SearchFilter> filters = new ArrayList<>();
        List<LuckyDraw> list = luckyDrawService.queryAll(filters, Sort.by(Sort.Order.asc("prizeType")));
        for (LuckyDraw luckyDraw : list) {
            boolean b = DateUtil.betWeen(DateUtil.parseTime(DateUtil.getTime()), luckyDraw.getStartTime(), luckyDraw.getEndTime());
            luckyDraw.setExpire(b);
        }
        return Rets.success(list);
    }


    /**
     * @Description: 抽奖列表查询
     * @Param:
     * @return:
     * @Author: Skj
     */
    @ApiOperationSupport(author = "skj")
    @ApiOperation(value = "获取奖品列表", notes = "v1 版本")
    @PostMapping("/getPrizeList")
    public Ret getPrizeList(@RequestBody PrizeDto prizeDto) {
        List<Prize> list = prizeService.queryAll(SearchFilter.build("prizeType", prizeDto.getPrizeType()));
        List<PrizeVo> prizeVos = list.stream().map(v -> {
            PrizeVo prizeVo = new PrizeVo();
            BeanUtils.copyProperties(v, prizeVo);
            return prizeVo;
        }).collect(Collectors.toList());
        Collections.shuffle(prizeVos);
        return Rets.success(prizeVos);
    }

    @ApiOperationSupport(author = "yc")
    @ApiOperation(value = "获取奖品列表展示", notes = "v1 版本")
    @GetMapping("/getPrizeShowList")
    public Ret getPrizeShowList() {
        LuckyDraw prizeType = luckyDrawService.get(SearchFilter.build("prizeType", "3"));
        return Rets.success(prizeType!=null?prizeType.getRemark():"");
    }

//    @ApiOperationSupport(author = "skj")
//    @ApiOperation( value = "获取转盘名称" , notes = "v1 版本")
//    @PostMapping("/getPrizeName")
//    public Ret getPrizeName(){
//        String prizeName = configCache.get(ConfigKeyEnum.DZ_PRIZE_NAME).trim();
//        return Rets.success(StringUtil.isEmpty(prizeName)?"抽奖大转盘":prizeName);
//    }

    @ApiOperationSupport(author = "skj")
    @ApiOperation(value = "获取剩余抽奖次数", notes = "v1 版本")
    @PostMapping("/getPrizeNum")
    public Ret getPrizeNum(@RequestBody PrizeDto prizeDto) {
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("account", getAccount()));
        filters.add(SearchFilter.build("prizeType", prizeDto.getPrizeType()));
        PrizeNum prizeNum = prizeNumService.get(filters);
        return Rets.success(prizeNum == null ? 0 : prizeNum.getPrizeNum());
    }

    /**
     * @Description: 抽奖转盘抽奖
     * @Param:
     * @return:
     * @Author: Skj
     */
    @ApiOperationSupport(author = "skj")
    @ApiOperation(value = "抽奖转盘抽奖", notes = "v1 版本")
    @PostMapping("/startPrize")
    public Ret startPrize(@RequestBody Map param) { // param:{ 'list':[前端显示的奖品idw],prizeType:'' }

        // 加锁防止重复调用
        String key = "start_prize_" + getAccount();
        boolean b = redisUtil.lock(key);
        if (b) {
            try {
                log.info("用户抽奖获取到锁,用户ID:{}", getAccount());
                // 前端显示的奖品
                List<String> prizeIdws = (List<String>) param.get("list");
                String prizeType = (String) param.get("prizeType");
                return winningRecordService.startPrize(getAccount(), prizeIdws, prizeType);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                redisUtil.delete(key);
            }
        }
        log.error("用户抽奖没有获取到锁,用户账号:{},时间:{}", getAccount(), DateUtil.getTime());
        return Rets.failure(MessageTemplateEnum.REQUEST_LIMIT.getCode(), MessageTemplateEnum.REQUEST_LIMIT);
    }


    /**
     * @Description: 抽奖转盘抽奖
     * @Param:
     * @return:
     * @Author: Skj
     */
    @ApiOperationSupport(author = "skj")
    @ApiOperation(value = "投注抽奖(共享单车专用)", notes = "v1 版本")
    @PostMapping("/startPrize2")
    public Ret startPrize2(@RequestBody StartPrizeDto dto) {

        // 加锁防止重复调用
        String key = "start_prize2_" + getAccount();
        boolean b = redisUtil.lock(key);
        if (b) {
            try {
                log.info("投注抽奖获取到锁,用户ID:{}", getAccount());
                return winningRecordService.startPrize2(getUserId(), dto.getMoney(), dto.getPassword(), dto.getPrizeType());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                redisUtil.delete(key);
            }
        }
        log.error("投注抽奖没有获取到锁,用户账号:{},时间:{}", getAccount(), DateUtil.getTime());
        return Rets.failure(MessageTemplateEnum.REQUEST_LIMIT.getCode(), MessageTemplateEnum.REQUEST_LIMIT);
    }

    /**
     * @Description: 用户中奖记录
     * @Param:
     * @return:
     * @Author: Skj
     */
    @ApiOperationSupport(author = "skj")
    @ApiOperation(value = "用户中奖记录", notes = "v1 版本")
    @PostMapping("/findWinningRecordByUser")
    public Ret findWinningRecordByUser(@RequestBody Page page) {
        return winningRecordService.findWinningRecordByUser(page, getAccount());
//        return winningRecordService.findWinningRecordByUser(page, getUserId());
    }


    /**
     * @Description: 夺宝列表查询
     * @Param:
     * @return:
     * @Author: zx
     */
    @ApiOperationSupport(author = "zx")
    @ApiOperation(value = "夺宝列表查询", notes = "v1 版本")
    @PostMapping("/getLuckyDrawPrizeList")
    public Ret getLuckyDrawPrizeList(@RequestBody Map map) {
        Page<Prize> page = new PageFactory<Prize>().defaultPage();
        page.setLimit(Integer.parseInt(map.get("limit").toString()));
        page.setCurrent(Integer.parseInt(map.get("page").toString()));
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("prizeType","8"));
        long count = prizeService.count(filters);
        page.setTotal((int)count);
        List<Prize> prizes = prizeService.queryPrizePageList(page);
        page.setRecords(prizes);
        return Rets.success(page);
    }


    /**
     * @Description: 夺宝参与用户查询
     * @Param:
     * @return:
     * @Author: zx
     */
    @ApiOperationSupport(author = "zx")
    @ApiOperation(value = "夺宝参与用户查询", notes = "v1 版本")
    @PostMapping("/getLuckyDrawPrizeUserList")
    public Ret getLuckyDrawPrizeUserList(@RequestBody Map map) {
        Page<HuntingRecord> page = new PageFactory<HuntingRecord>().defaultPage();
        page.setLimit(Integer.parseInt(map.get("limit").toString()));
        page.setCurrent(Integer.parseInt(map.get("page").toString()));
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("huntIdw",map.get("prizeIdw")));
        long count = huntingRecordService.count(filters);
        page.setTotal((int)count);
        page.addFilter("hunt_idw",map.get("prizeIdw"));
        List<HuntingRecord> prizes = huntingRecordService.queryHuntingRecordPageList(page);
        page.setRecords(prizes);
        return Rets.success(page);
    }


    /**
     * @Description:参与夺宝
     * @Param:
     * @return:
     * @Author: zx
     */
    @ApiOperationSupport(author = "zx")
    @ApiOperation(value = "参与夺宝", notes = "v1 版本")
    @PostMapping("/participateLuckDraw")
    public Ret participateLuckDraw(@RequestBody Map param) { // param:{ 'list':[前端显示的奖品idw],prizeType:'' }
        // 加锁防止重复调用
        String prizeIdw = param.get("prizeIdw").toString();
        String key = "participateLuckDraw_"+prizeIdw;
        boolean b = redisUtil.lock(key);
        if (b) {
            try {

                log.info("用户抽奖获取到锁,用户ID:{}", getAccount());
                Ret ret = luckyDrawService.LuckyDrawLottery(getAccount(), getUserId(), param.get("prizeIdw").toString(), param.get("prizeType").toString());
                return ret;

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                redisUtil.delete(key);
            }
        }
        log.error("用户抽奖没有获取到锁,用户账号:{},时间:{}", getAccount(), DateUtil.getTime());
        return Rets.failure(MessageTemplateEnum.REQUEST_LIMIT.getCode(), MessageTemplateEnum.REQUEST_LIMIT);
    }

    @ApiOperationSupport(author = "zx")
    @ApiOperation(value = "获取夺宝剩余抽奖次数", notes = "v1 版本")
    @PostMapping("/getLuckyDrawPrizeNum")
    public Ret getLuckyDrawPrizeNum(@RequestBody PrizeDto prizeDto) {
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("account", getAccount()));
        filters.add(SearchFilter.build("prizeType", prizeDto.getPrizeType()));
        PrizeNum prizeNum = prizeNumService.get(filters);
        return Rets.success(prizeNum == null ? 0 : prizeNum.getPrizeNum());
    }

    @ApiOperationSupport(author = "zx")
    @ApiOperation(value = "获取夺宝获奖用户", notes = "v1 版本")
    @PostMapping("/getWinner")
    public Ret getWinner(@RequestBody PrizeDto prizeDto) {
        Page<WinningRecord> page = new PageFactory<WinningRecord>().defaultPage();
        page.addFilter("prizeType", prizeDto.getPrizeType());
        page = winningRecordService.queryPage(page);
        List<HashMap> list = (List<HashMap>) new WinningRecordWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();
        ArrayList<WinningRecord> winningRecords = new ArrayList<>();

        for (int i=0;i<list.size();i++){
            HashMap hashMap = list.get(i);
            String account = hashMap.get("account").toString();

            WinningRecord winningRecord = new WinningRecord();
            winningRecord.setAccount("*****"+account.substring((account.length() - 5)));
            winningRecord.setPrizeName(hashMap.get("prizeName").toString());
            winningRecords.add(winningRecord);
        }

        page.setRecords(winningRecords);
        return Rets.success(page);
    }


    /**
     * @Description: 大富翁奖品列表查询
     * @Param:
     * @return:
     * @Author: zx
     */
    @ApiOperationSupport(author = "zx")
    @ApiOperation(value = "大富翁奖品列表查询", notes = "v2 版本")
    @GetMapping("/getMonopolyPrizeList")
    public Ret getMonopolyPrizeList() {
        return Rets.success(prizeService.getMonopolyPrizeList());
    }

    /**
     * @Description: 大富翁当前用户状态查询
     * @Param:
     * @return:
     * @Author: zx
     */
    @ApiOperationSupport(author = "zx")
    @ApiOperation(value = "大富翁用户信息查询", notes = "v2 版本")
    @GetMapping("/getMonopolyUserInformation")
    public Ret getMonopolyUserInformation() {
        MonopolyUserAndNumVo monopolyUserAndNumVo = monopolyUserService.selectOrCreateMonopolyUser(getUserId(), getAccount(), getSourceInvitationCode());
        Map map=new HashMap<>();
        map.put("num",monopolyUserAndNumVo.getNum());
        map.put("position",monopolyUserAndNumVo.getPosition());
        return Rets.success(map);
    }

    /**
     * @Description: 大富翁活动参与
     * @Param:
     * @return:
     * @Author: zx
     */
    @ApiOperationSupport(author = "zx")
    @ApiOperation(value = "大富翁活动参与", notes = "v2 版本")
    @PostMapping("/joinMonopoly")
    public Ret joinMonopoly(@RequestBody Map map) {
        String key = "joinMonopoly_"+getAccount();
        boolean b = redisUtil.lock(key);
        if (b) {
            try {
                log.info("用户抽奖获取到锁,用户ID:{}", getAccount());
                Ret ret = monopolyUserService.joinMonopoly(getUserId(), getAccount(), getSourceInvitationCode());
                return ret;

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                redisUtil.delete(key);
            }
        }
        log.error("用户抽奖没有获取到锁,用户账号:{},时间:{}", getAccount(), DateUtil.getTime());
        return Rets.failure(MessageTemplateEnum.REQUEST_LIMIT.getCode(), MessageTemplateEnum.REQUEST_LIMIT);
    }

    /**
     * @Description: 查询单个奖品信息
     * @Param:
     * @return:
     * @Author: zx
     */
    @ApiOperationSupport(author = "zx")
    @ApiOperation(value = "查询单个奖品信息", notes = "v2 版本")
    @PostMapping("/getPrizeById")
    public Ret getPrizeById(@RequestParam Long id) {
        Prize prize = prizeService.get(id);
        prize.setUrl(ImageUtil.getImage(prize.getUrl()));
        return Rets.success(prize);
    }



    @ApiOperationSupport(author = "fuyu")
    @ApiOperation(value = "随机生成15条中奖记录")
    @GetMapping("/randomRecord")
    public Ret randomRecord() {
        int i = Integer.parseInt(configCache.get(ConfigKeyEnum.RANDOWRECORDSTATE).trim());
        return (i == 0) ? Rets.success(RandomRecordUtil.randomRecordStr()) : Rets.success();
     }

    @ApiOperationSupport(author = "fuyu")
    @ApiOperation(value = "随机生成中奖记录开关")
    @GetMapping("/randomRecordState")
    public Ret randomRecordState() {
        return Rets.success(Integer.parseInt(configCache.get(ConfigKeyEnum.RANDOWRECORDSTATE).trim()));
    }

}
