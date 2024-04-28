package cn.rh.flash.service.dzscore;

import cn.rh.flash.bean.dto.api.PrizeExchangeDto;
import cn.rh.flash.bean.entity.dzscore.ScorePrize;
import cn.rh.flash.bean.entity.dzscore.ScorePrizeRecord;
import cn.rh.flash.bean.entity.dzscore.UserScore;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.dao.dzscore.ScorePrizeRepository;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.service.coom.dz.ApiUserCoom;
import cn.rh.flash.service.coom.dz.RecordInformation;
import cn.rh.flash.service.dzuser.UserInfoService;
import cn.rh.flash.utils.BigDecimalUtils;
import cn.rh.flash.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScorePrizeService extends BaseService<ScorePrize, Long, ScorePrizeRepository> {

    @Autowired
    private ScorePrizeRepository scorePrizeRepository;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private UserScoreService userScoreService;

    @Autowired
    private RecordInformation recordInformation;

    @Autowired
    private ScorePrizeRecordService scorePrizeRecordService;

    @Autowired
    private ApiUserCoom apiUserCoom;

    @Transactional(rollbackFor = Exception.class)
    public Ret prizeExchange(PrizeExchangeDto dto, String account) {

        UserInfo userInfo = userInfoService.get(SearchFilter.build("account", account));
        Integer vipNum = Integer.parseInt(userInfo.getVipType().replace("v", ""));

        ScorePrize scorePrize = this.get(SearchFilter.build("idw", dto.getIdw()));

        List<SearchFilter> filter = new ArrayList<>();
        filter.add(SearchFilter.build("account", account));
        //夺宝积分不通用
        if (scorePrize.getPrizeType().equals("8")){
            filter.add(SearchFilter.build("prizeType", 8));
        }else if (scorePrize.getMaxVip() > vipNum) {
            return Rets.failure(MessageTemplateEnum.PRIZE_MAX_VIP.getCode(), MessageTemplateEnum.PRIZE_MAX_VIP);
        }else {
            filter.add(SearchFilter.build("prizeType", 1));
        }

        UserScore userScore = userScoreService.get(filter);
//        UserScore userScore = userScoreService.get(SearchFilter.build("account", account));
        if (userScore == null) {
            return Rets.failure(MessageTemplateEnum.SCORE_NOT_ENOUGH.getCode(), MessageTemplateEnum.SCORE_NOT_ENOUGH);
        }

        if (userScore.getUserScore() < scorePrize.getScore()) {
            return Rets.failure(MessageTemplateEnum.SCORE_NOT_ENOUGH.getCode(), MessageTemplateEnum.SCORE_NOT_ENOUGH);
        }

        recordInformation.changeUserScore(scorePrize.getScore() * -1, userInfo.getId(),
                userInfo.getSourceInvitationCode(), userInfo.getAccount(), userScore.getPrizeType().equals("8")?8:4);

        ScorePrizeRecord scorePrizeRecord = new ScorePrizeRecord();
        scorePrizeRecord.setIdw(new IdWorker().nextId() + "");
        scorePrizeRecord.setSourceInvitationCode(userInfo.getSourceInvitationCode());
        scorePrizeRecord.setUid(userInfo.getId());
        scorePrizeRecord.setAccount(userScore.getAccount());
        scorePrizeRecord.setPrizeIdw(scorePrize.getIdw());
        scorePrizeRecord.setAmount(scorePrize.getAmount());
        scorePrizeRecord.setScore(scorePrize.getScore());
        scorePrizeRecord.setUrl(scorePrize.getUrl());
        scorePrizeRecord.setTypes(scorePrize.getTypes());
        scorePrizeRecord.setPrizeName(scorePrize.getPrizeName());
        scorePrizeRecord.setSurplusScore(userScore.getUserScore());
        scorePrizeRecord.setPrizeType(userScore.getPrizeType());
        scorePrizeRecordService.insert(scorePrizeRecord);

        //余额
        if ("1".equals(scorePrize.getTypes())) {

            // 查询用户余额
            Double balance = apiUserCoom.getUserBalance(userInfo.getId()).doubleValue();
            recordInformation.transactionRecordPlus(userInfo.getSourceInvitationCode(), userInfo.getId(), userInfo.getAccount(),
                    balance, scorePrize.getAmount(), BigDecimalUtils.add(balance, scorePrize.getAmount()),
                    scorePrizeRecord.getIdw(), 14, "jf", "", "");
        }

        return Rets.success();
    }

}
