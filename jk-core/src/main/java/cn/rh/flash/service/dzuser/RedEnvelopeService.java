package cn.rh.flash.service.dzuser;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
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
import cn.rh.flash.service.dzvip.DzRedEnvelopeVipMessageService;
import cn.rh.flash.utils.BigDecimalUtils;
import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.RandomUtil;
import cn.rh.flash.utils.factory.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RedEnvelopeService {
    @Autowired
    private ApiUserCoom apiUserCoom;
    @Autowired
    private LuckyDrawService luckyDrawService;
    @Autowired
    private DzRedEnvelopeVipMessageService dzRedEnvelopeVipMessageService;
    @Autowired
    private WinningRecordService winningRecordService;
    @Autowired
    private RecordInformation recordInformation;
    @Autowired
    private ExpectWinningUserService expectWinningUserService;
    @Autowired
    private PrizeService prizeService;
    @Autowired
    private TotalBonusIncomeService totalBonusIncomeService;
    @Autowired
    private PrizeNumService prizeNumService;

    /**
     * 获取vip参数信息
     *
     * @return
     */
    public DzRedEnvelopeVipMessage getDzRedEnvelopeVipMessage() {
        String vip = apiUserCoom.getVipType();
        List<DzRedEnvelopeVipMessage> dzRedEnvelopeVipMessages = dzRedEnvelopeVipMessageService.getDzRedEnvelopeVipMessage(vip);
        if (CollUtil.isEmpty(dzRedEnvelopeVipMessages)) {
            return null;
        }
        return dzRedEnvelopeVipMessages.get(0);
    }

    /**
     * 获取预期中奖
     */
    public ExpectWinningUser getexpectWinningUser() {
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("uid", apiUserCoom.getUserId()));
        filters.add(SearchFilter.build("prizeType", "11"));
        filters.add(SearchFilter.build("isPrize", "no"));
        List<ExpectWinningUser> expectWinningUsers = expectWinningUserService.queryAll(filters);
        if (CollUtil.isEmpty(expectWinningUsers)) {
            return null;
        }
        return expectWinningUsers.get(0);
    }

    /**
     * 获取用户抽奖次数
     */
    public PrizeNum getPrizeNum() {
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("uid", apiUserCoom.getUserId()));
        filters.add(SearchFilter.build("prizeType", "11"));
        return prizeNumService.get(filters);
    }

    /**
     * 获取奖品金额
     */
    public Double getAmount(String idw) {
        Prize prize = prizeService.get(SearchFilter.build("idw", idw));
        if (ObjUtil.isNotEmpty(prize) && ObjUtil.isNotEmpty(prize.getAmount())) {
            return prize.getAmount();
        }
        return null;
    }

    /**
     * 领取记录条数
     */
    public long getWinningRecordCount() {
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("uid", apiUserCoom.getUserId()));
        filters.add(SearchFilter.build("prizeType", "11"));
        filters.add(SearchFilter.build("createTime", SearchFilter.Operator.LIKE, DateUtil.format(new Date(), "yyyy-MM-dd")));
        return winningRecordService.count(filters);
    }


    /**
     * 领取红包
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Ret receiveRedEnvelope() {
        // 次数
        long count = getRedEnvelopeCount();

        double money = 0.00;
        //无预期中奖 拿最大值最小值之间的随机值
        money = RandomUtil.randomByMaxAndMin(getDzRedEnvelopeVipMessage().getRedEnvelopeMoneyMin(), getDzRedEnvelopeVipMessage().getRedEnvelopeMoneyMax());

        //有预期中奖并且有对应奖品金额
        if (ObjUtil.isNotEmpty(getexpectWinningUser()) && ObjUtil.isNotEmpty(getexpectWinningUser().getPrizeIdw()) && ObjUtil.isNotEmpty(getAmount(getexpectWinningUser().getPrizeIdw()))) {
            money = getAmount(getexpectWinningUser().getPrizeIdw());
            //更改预期中奖状态
            ExpectWinningUser expectWinningUser = getexpectWinningUser();
            expectWinningUser.setIsPrize("yes");
            expectWinningUserService.update(expectWinningUser);
        }

        //添加余额
        Integer zc = 0;
        zc = recordInformation.transactionRecordPlus(apiUserCoom.getSourceInvitationCode(), apiUserCoom.getUserId(), apiUserCoom.getAccount(),
                apiUserCoom.getUserBalance(apiUserCoom.getUserId()).doubleValue(), money,
                BigDecimalUtils.add(apiUserCoom.getUserBalance(apiUserCoom.getUserId()).doubleValue(), money), new IdWorker().nextId() + "", 30, "hbhd", "", "");
        if (zc == 0) {
            return Rets.failure(MessageTemplateEnum.TRANSACTION_FAILED.getCode(), MessageTemplateEnum.TRANSACTION_FAILED);
        }
        //添加赠送彩总额记录
        addToTalBonusIncome(money);
        //vip次数 - 记录
        long l = getDzRedEnvelopeVipMessage().getRedEnvelopeTotal() - getWinningRecordCount();
        // 小于0 说明vip参数已经用完 然后使用 用户抽奖次数
        if (l <= 0 && (ObjectUtil.isNotEmpty(getPrizeNum()) && ObjectUtil.isNotEmpty(getPrizeNum().getPrizeNum()) && getPrizeNum().getPrizeNum() > 0)) {
            PrizeNum prizeNum = new PrizeNum();
            prizeNum = getPrizeNum();
            prizeNum.setPrizeNum(prizeNum.getPrizeNum() - 1);
            prizeNumService.update(prizeNum);
        }

        //添加中奖记录
        WinningRecord winningRecord = new WinningRecord();
        winningRecord.setIdw(new IdWorker().nextId() + "");
        winningRecord.setSourceInvitationCode(apiUserCoom.getSourceInvitationCode());
        winningRecord.setUid(apiUserCoom.getUserId());
        winningRecord.setAccount(apiUserCoom.getAccount());
        winningRecord.setAmount(money);
        winningRecord.setPrizeType("11");
        winningRecord.setPrizeName(money + "");
        winningRecord.setSurplusNumber(new Long(count).intValue() - 1);
        winningRecordService.insert(winningRecord);

        RedEnvelopeReceiveVo vo = new RedEnvelopeReceiveVo();
        vo.setCount(new Long(count).intValue() - 1);
        vo.setMoney(money);
        return Rets.success(vo);

    }

    /**
     * 添加自己的赠送彩总额记录
     *
     * @param toTal
     */
    @Transactional(rollbackFor = Exception.class)
    public void addToTalBonusIncome(double toTal) {
        TotalBonusIncome TotalBonusIncome = totalBonusIncomeService.get(SearchFilter.build("uid", apiUserCoom.getUserId()));
        if (ObjUtil.isEmpty(TotalBonusIncome)) {
            TotalBonusIncome = new TotalBonusIncome();
            TotalBonusIncome.setAccount(apiUserCoom.getAccount());
            TotalBonusIncome.setIdw(new IdWorker().nextId() + "");
            TotalBonusIncome.setUid(apiUserCoom.getUserId());
            TotalBonusIncome.setSourceInvitationCode(apiUserCoom.getSourceInvitationCode());
            TotalBonusIncome.setTotalBonusIncome(toTal);
            TotalBonusIncome.setDzversion(0);
            totalBonusIncomeService.insert(TotalBonusIncome);
        } else {
            Double add = BigDecimalUtils.add(TotalBonusIncome.getTotalBonusIncome(), toTal);
            TotalBonusIncome.setTotalBonusIncome(add);
            totalBonusIncomeService.update(TotalBonusIncome);
        }

    }

    /**
     * 获取剩余次数
     */
    public long getRedEnvelopeCount() {
        long count = 0L;
        //  当前vip次数 -领取记录条数
        if (ObjUtil.isNotEmpty(getDzRedEnvelopeVipMessage()) || ObjUtil.isNotEmpty(getDzRedEnvelopeVipMessage().getRedEnvelopeTotal())) {
            count = getDzRedEnvelopeVipMessage().getRedEnvelopeTotal() - getWinningRecordCount();
        }
        // 因为用户抽奖次数也添加记录会存在超出 超出=0
        if (count < 0) {
            count = 0;
        }
        // + 抽奖用户抽奖次数
        if (ObjectUtil.isNotEmpty(getPrizeNum()) && ObjectUtil.isNotEmpty(getPrizeNum().getPrizeNum()) && getPrizeNum().getPrizeNum() > 0) {
            count = count + getPrizeNum().getPrizeNum();
        }
        return count;
    }

    /**
     * 查询领取记录
     *
     * @return
     */
    public List<RedEnvelopeVo> getRedEnvelopeRecord() {
        List<RedEnvelopeVo> vos = new ArrayList<>();
        Page<WinningRecord> page = new Page<>(1, 3);
        page.addFilter("uid", apiUserCoom.getUserId());
        page.addFilter("prizeType", "11");
        page.setSort(Sort.by(Sort.Order.desc("createTime")));
        Page<WinningRecord> winningRecordPage = winningRecordService.queryPage(page);
        if (ObjUtil.isNotEmpty(winningRecordPage) && CollUtil.isNotEmpty(winningRecordPage.getRecords())) {
            for (WinningRecord record : winningRecordPage.getRecords()) {
                RedEnvelopeVo redEnvelopeVo = new RedEnvelopeVo();
                redEnvelopeVo.setCollectionTime(record.getCreateTime());
                redEnvelopeVo.setCollectionMoney(record.getAmount());
                vos.add(redEnvelopeVo);
            }
        }
        return vos;
    }

    /**
     * 查询红包活动是否开启在有效期内
     *
     * @return
     */
    public Ret getRedEnvelopeFlag() {
        //查询活动是否存在
        LuckyDraw luckyDraw = luckyDrawService.get(SearchFilter.build("prizeType", "11"));
        if (ObjUtil.isEmpty(luckyDraw)) {
            return Rets.success(false);
        }
        //查询是否开启
        if (!"1".equals(luckyDraw.getStatus())) {
            return Rets.success(false);
        }
        //查询是否在时间范围内
        if (!DateUtil.betWeen(new Date(), luckyDraw.getStartTime(), luckyDraw.getEndTime())) {
            return Rets.success(false);
        }
        return Rets.success(true);
    }
}
