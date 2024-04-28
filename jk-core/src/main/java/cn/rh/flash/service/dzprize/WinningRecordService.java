package cn.rh.flash.service.dzprize;

import cn.hutool.core.util.ObjUtil;
import cn.rh.flash.bean.entity.dzprize.*;
import cn.rh.flash.bean.entity.dzuser.TransactionRecord;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.entity.system.User;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.vo.dzprize.WinningRecordVo;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.dao.dzprize.WinningRecordRepository;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.service.coom.dz.ApiUserCoom;
import cn.rh.flash.service.coom.dz.RecordInformation;
import cn.rh.flash.service.dzuser.TransactionRecordService;
import cn.rh.flash.service.dzuser.UserInfoService;
import cn.rh.flash.utils.*;
import cn.rh.flash.utils.factory.Page;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Service
public class WinningRecordService extends BaseService<WinningRecord, Long, WinningRecordRepository> {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private ExpectWinningUserService expectWinningUserService;

    @Autowired
    private PrizeService prizeService;

    @Autowired
    private RecordInformation recordInformation;

    @Autowired
    private ApiUserCoom apiUserCoom;

    @Autowired
    private PrizeNumService prizeNumService;

    @Autowired
    private TransactionRecordService transactionRecordService;

    @Autowired
    private LuckyDrawService luckyDrawService;

    /**
     * @Description: 用户抽奖
     * @Author: Skj(老子真TM帅)
     * @Date: 2023/5/12
     */
    @Transactional
    public Ret startPrize(String account, List<String> prizeIdws, String prizeType) {
        log.info("----------用户抽奖 start----------");

        // 查询活动是否开启
        List<SearchFilter> ldFilters = new ArrayList<>();
        ldFilters.add(SearchFilter.build("status", "1"));
        ldFilters.add(SearchFilter.build("prizeType", prizeType));
        LuckyDraw luckyDraw = luckyDrawService.get(ldFilters);
        if (luckyDraw == null) {
            return Rets.failure(MessageTemplateEnum.lUCKEDRAM_NOT_STARTED.getCode(), MessageTemplateEnum.lUCKEDRAM_NOT_STARTED);
        }
        boolean b = DateUtil.betWeen(DateUtil.parseTime(DateUtil.getTime()), luckyDraw.getStartTime(), luckyDraw.getEndTime());

        if (!b) {
            return Rets.failure(MessageTemplateEnum.lUCKEDRAM_NOT_STARTED.getCode(), MessageTemplateEnum.lUCKEDRAM_NOT_STARTED);
        }

        // 查询当前用户是否有抽奖次数
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("account", account));
        filters.add(SearchFilter.build("prizeType", prizeType));
        PrizeNum prizeNum = prizeNumService.get(filters);
        if (prizeNum == null || prizeNum.getPrizeNum() <= 0) {
            log.info("----------抽奖次数已用尽,账号:{}----------", account);
            return Rets.failure(MessageTemplateEnum.COUNT_RUN_OUT.getCode(), MessageTemplateEnum.COUNT_RUN_OUT);
        }
        // 查询所有奖品 (只查询前端显示的奖品)
        List<SearchFilter> priFilter = new ArrayList<>();
        priFilter.add(SearchFilter.build("prizeType", prizeType));
        if (!CollectionUtils.isEmpty(prizeIdws)) {
            priFilter.add(SearchFilter.build("idw", SearchFilter.Operator.IN, prizeIdws));
        }
        List<Prize> prizeList = prizeService.queryAll(priFilter);

        // 查询当前用户有没有预设奖品
        List<SearchFilter> expectFilter = new ArrayList<>();
        expectFilter.add(SearchFilter.build("uid", prizeNum.getUid()));
        expectFilter.add(SearchFilter.build("account", account));
        expectFilter.add(SearchFilter.build("prizeType", prizeType));
        if (!CollectionUtils.isEmpty(prizeIdws)) {
            expectFilter.add(SearchFilter.build("prizeIdw", SearchFilter.Operator.IN, prizeIdws));
        }
        expectFilter.add(SearchFilter.build("isPrize", "no"));
        List<ExpectWinningUser> expectWinningUsers = expectWinningUserService.queryAll(expectFilter);

        Map<String, Prize> prizeMap = prizeList.stream().collect(Collectors.toMap(Prize::getIdw, Function.identity()));
        // 用户中的奖品
        Prize prize = null;
        if (expectWinningUsers.size() == 0) {
            // 当前用户没有预设奖品 根据奖品概率计算中奖奖品
            Map<String, String> map = prizeList.stream().collect(Collectors.toMap(Prize::getIdw, Prize::getWinningChance));
            String idw = MyToolBiz.weightRandom(map);
            prize = prizeMap.get(idw);
        } else {
            // 有预设奖品
            ExpectWinningUser expectWinningUser = expectWinningUsers.get(0);
            expectWinningUser.setIsPrize("yes"); // 中将状态改为已中奖
            expectWinningUserService.update(expectWinningUser);
            prize = prizeMap.get(expectWinningUser.getPrizeIdw());
        }
        // 扣一次抽奖次数
        prizeNum.setPrizeNum(prizeNum.getPrizeNum() - 1);
        prizeNumService.update(prizeNum);
        // 生成中奖记录
        WinningRecord winningRecord = new WinningRecord();
        winningRecord.setIdw(new IdWorker().nextId() + "");
        winningRecord.setSourceInvitationCode(prizeNum.getSourceInvitationCode());
        winningRecord.setUid(prizeNum.getUid());
        winningRecord.setAccount(account);
        winningRecord.setPrizeType(prizeType);
        if ("1".equals(prize.getTypes())) {
            winningRecord.setAmount(prize.getAmount());
        }
        winningRecord.setSurplusNumber(prizeNum.getPrizeNum());
        winningRecord.setPrizeIdw(prize.getIdw());
        winningRecord.setPrizeName(prize.getPrizeName());
        this.insert(winningRecord);

        // 奖品如果是金额 去增加用户余额
        if ("1".equals(prize.getTypes())) {
            // 查询用户余额
            Double balance = apiUserCoom.getUserBalance(prizeNum.getUid()).doubleValue();
            recordInformation.transactionRecordPlus(prizeNum.getSourceInvitationCode(), prizeNum.getUid(), prizeNum.getAccount(),
                    balance, prize.getAmount(), BigDecimalUtils.add(balance, prize.getAmount()),
                    prize.getIdw(), 12, "cj", "", "");
        }
        log.info("----------用户抽奖 end----------");
        return Rets.success(prize.getIdw());
    }

    /**
     * @Description: 用户中奖几率
     * @Author: Skj(老子真TM帅)
     * @Date: 2023/5/12
     */
//    public Ret findWinningRecordByUser(Page page, Long userId) {
    public Ret findWinningRecordByUser(Page page, String account) {
        Page<WinningRecord> pageFilters = page;
//        pageFilters.addFilter(SearchFilter.build("uid", userId));
        pageFilters.addFilter(SearchFilter.build("account", account));
        page = this.queryPage(pageFilters);

        List<WinningRecord> winningRecords = page.getRecords();
//        Set<String> collect = winningRecords.stream().map(WinningRecord::getPrizeIdw).collect(Collectors.toSet());
//        List<Prize> prizes = prizeService.queryAll(SearchFilter.build("idw", SearchFilter.Operator.IN, collect));
//        Map<String, String> prizeMap = prizes.stream().collect(Collectors.toMap(Prize::getIdw, Prize::getPrizeName));
//        for (WinningRecord winningRecord : winningRecords) {
//            winningRecord.setPrizeName(prizeMap.get(winningRecord.getPrizeIdw()));
//        }
        return Rets.success(winningRecords);
    }

    @Transactional(rollbackFor = Exception.class)
    public Ret startPrize2(Long uid, Double money, String password, String prizeType) {


        // 查询活动是否开启
        List<SearchFilter> ldFilters = new ArrayList<>();
        ldFilters.add(SearchFilter.build("status", "1"));
        ldFilters.add(SearchFilter.build("prizeType", prizeType));
        LuckyDraw luckyDraw = luckyDrawService.get(ldFilters);
        if (luckyDraw == null) {
            return Rets.failure(MessageTemplateEnum.lUCKEDRAM_NOT_STARTED.getCode(), MessageTemplateEnum.lUCKEDRAM_NOT_STARTED);
        }
        boolean b = DateUtil.betWeen(DateUtil.parseTime(DateUtil.getTime()), luckyDraw.getStartTime(), luckyDraw.getEndTime());

        if (!b) {
            return Rets.failure(MessageTemplateEnum.lUCKEDRAM_NOT_STARTED.getCode(), MessageTemplateEnum.lUCKEDRAM_NOT_STARTED);
        }

        // 验证用户交易密码
        UserInfo userInfo = userInfoService.get(uid);
        if (!userInfo.getPaymentPassword().equals(MD5.md5(password, ""))) {
            return Rets.failure(MessageTemplateEnum.PAY_PASSWORD_ERROR.getCode(), MessageTemplateEnum.PAY_PASSWORD_ERROR);
        }
        // 查询用户余额
        Double balance = apiUserCoom.getUserBalance(uid).doubleValue();

        if (balance < money) {
            // 余额不足
            return Rets.failure(MessageTemplateEnum.INSUFFICIENT_BALANCE_ZERO.getCode(), MessageTemplateEnum.INSUFFICIENT_BALANCE_ZERO);
        }

        // 奖品列表
        List<Prize> prizeList = prizeService.queryAll(SearchFilter.build("prizeType", prizeType));

        // 查询当前用户有没有预设奖品
        List<SearchFilter> expectFilter = new ArrayList<>();
        expectFilter.add(SearchFilter.build("uid", uid));
        expectFilter.add(SearchFilter.build("account", userInfo.getAccount()));
        expectFilter.add(SearchFilter.build("prizeType", prizeType));
        expectFilter.add(SearchFilter.build("isPrize", "no"));
        List<ExpectWinningUser> expectWinningUsers = expectWinningUserService.queryAll(expectFilter);


        Map<String, Prize> prizeMap = prizeList.stream().collect(Collectors.toMap(Prize::getIdw, Function.identity()));

        // 用户中的奖品
        Prize prize = null;
        if (expectWinningUsers.size() == 0) {
            // 当前用户没有预设奖品 根据奖品概率计算中奖奖品
            Map<String, String> map = prizeList.stream().collect(Collectors.toMap(Prize::getIdw, Prize::getWinningChance));
            String idw = MyToolBiz.weightRandom(map);
            prize = prizeMap.get(idw);
        } else {
            // 有预设奖品
            ExpectWinningUser expectWinningUser = expectWinningUsers.get(0);
            expectWinningUser.setIsPrize("yes"); // 中将状态改为已中奖
            expectWinningUserService.update(expectWinningUser);
            prize = prizeMap.get(expectWinningUser.getPrizeIdw());
        }
        // 用户中奖的金额
        Double prizeMoney = prize.getAmount() * money;

        // 生成中奖记录
        WinningRecord winningRecord = new WinningRecord();
        winningRecord.setIdw(new IdWorker().nextId() + "");
        winningRecord.setSourceInvitationCode(userInfo.getSourceInvitationCode());
        winningRecord.setUid(uid);
        winningRecord.setAmount(prizeMoney);
        winningRecord.setPrizeType(prizeType);
        winningRecord.setAccount(userInfo.getAccount());
        winningRecord.setPrizeIdw(prize.getIdw());
        winningRecord.setPrizeName(prize.getPrizeName());
        this.insert(winningRecord);


        // 生成交易记录
        // 购买VIP 创建交易记录-余额

        TransactionRecord transactionObj = new TransactionRecord();
        transactionObj.setIdw(new IdWorker().nextId() + "");
        transactionObj.setSourceInvitationCode(userInfo.getSourceInvitationCode());
        transactionObj.setUid(uid);
        transactionObj.setAccount(userInfo.getAccount());
        transactionObj.setOrderNumber(MakeOrderNum.makeOrderNum("cj"));
        transactionObj.setTransactionNumber(prize.getIdw());
        transactionObj.setMoney(money);
        transactionObj.setPreviousBalance(balance);
        transactionObj.setAfterBalance(BigDecimalUtils.subtract(balance, money));
        transactionObj.setTransactionType("13");  //  1:通道一充值,2:通道一提现,3:平台赠送,4:平台扣款,5:任务收益,6:转入共享余额,7:转出共享余额
        transactionObj.setAdditionAndSubtraction(2);
        transactionObj.setRemark("");
        transactionRecordService.insert(transactionObj);

        // 创建交易记录+余额
        TransactionRecord transactionAdd = new TransactionRecord();
        transactionAdd.setIdw(new IdWorker().nextId() + "");
        transactionAdd.setSourceInvitationCode(userInfo.getSourceInvitationCode());
        transactionAdd.setUid(uid);
        transactionAdd.setAccount(userInfo.getAccount());
        transactionAdd.setOrderNumber(MakeOrderNum.makeOrderNum("cj"));
        transactionAdd.setTransactionNumber(winningRecord.getIdw());
        transactionAdd.setMoney(prizeMoney);
        transactionAdd.setPreviousBalance(transactionObj.getAfterBalance());
        transactionAdd.setAfterBalance(BigDecimalUtils.add(transactionAdd.getPreviousBalance(), prizeMoney));
        transactionAdd.setTransactionType("12");  //  1:通道一充值,2:通道一提现,3:平台赠送,4:平台扣款,5:任务收益,6:转入共享余额,7:转出共享余额
        transactionAdd.setAdditionAndSubtraction(1);
        transactionAdd.setRemark("");
        transactionRecordService.insert(transactionAdd);

        Double subtract = prizeMoney - money;

        if (subtract > 0) { // 用户中奖的金额 大于用户抽奖的金额
            recordInformation.updateUserBalance2(userInfo.getSourceInvitationCode()
                    , uid, userInfo.getAccount(), subtract, 1, false);

        } else if (subtract < 0) {
            recordInformation.updateUserBalance2(userInfo.getSourceInvitationCode()
                    , uid, userInfo.getAccount(), subtract * -1, 2, false);
        }

        log.info("----------用户抽奖 end----------");
//        int indexOf = prizeList.indexOf(prize);
        return Rets.success(prize.getIdw());
    }

    public void export(List<Map<String,Object>> list, HttpServletResponse response) {
        List<WinningRecordVo> voList=new ArrayList<>();
        for (Map<String, Object> stringObjectMap : list) {
            WinningRecordVo vo=new WinningRecordVo();
            BeanUtil.mapToBean(stringObjectMap, vo);
            User user = BeanUtil.objToBean(stringObjectMap.get("user"), User.class);
            if (ObjUtil.isNotEmpty(stringObjectMap.get("user")) && ObjUtil.isNotEmpty(user)){
                vo.setUserAccount(user.getAccount());
            }

            voList.add(vo);
        }
        EasyExcelUtil.export(response,"中奖记录",voList,WinningRecordVo.class);



    }

}
