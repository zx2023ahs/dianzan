package cn.rh.flash.service.dzprize;

import cn.rh.flash.bean.entity.dzprize.*;
import cn.rh.flash.bean.entity.dzscore.UserScore;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.dao.dzprize.LuckyDrawRepository;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.service.coom.dz.ApiUserCoom;
import cn.rh.flash.service.coom.dz.RecordInformation;
import cn.rh.flash.service.dzscore.UserScoreService;
import cn.rh.flash.service.dzuser.UserInfoService;
import cn.rh.flash.utils.BigDecimalUtils;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class LuckyDrawService extends BaseService<LuckyDraw,Long, LuckyDrawRepository> {

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
    private RedisUtil redisUtil;

    @Autowired
    private HuntingRecordService huntingRecordService;

    @Lazy
    @Autowired
    private WinningRecordService winningRecordService;

    @Autowired
    private UserScoreService userScoreService;
    /**
     * @Description: 夺宝抽奖
     * @Author: zx
     * @Date: 2023/10/25
     */
    @Transactional
    public Ret LuckyDrawLottery(String account,Long userId, String prizeIdw, String prizeType) {
        //查询夺宝额度是否已满
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("idw", prizeIdw));
        Prize prize = prizeService.get(filters);
        //需求人数小于等于参加人数
        if (prize.getIsEnd().equals("1") || prize.getTotalNumber()<=prize.getParticipateNumber()){
            return Rets.failure(MessageTemplateEnum.LUCKY_DRAW_IS_END.getCode(), MessageTemplateEnum.LUCKY_DRAW_IS_END);
        }

        //判断是否有夺宝资格
        List<SearchFilter> filters1 = new ArrayList<>();
        filters1.add(SearchFilter.build("account",account));
        filters1.add(SearchFilter.build("prizeType",prizeType));
        PrizeNum prizeNum = prizeNumService.get(filters1);
        if (prizeNum == null || prizeNum.getPrizeNum() <= 0) {
            return Rets.failure(MessageTemplateEnum.COUNT_RUN_OUT.getCode(), MessageTemplateEnum.COUNT_RUN_OUT);
        }

        //非开奖逻辑，正常参与
        if (prize.getTotalNumber()-prize.getParticipateNumber()>1){
            this.JoinLuckyDraw(userId,account,prizeIdw,prizeType,prizeNum,prize.getPrizeName());
            //修改奖品参与数
            prize.setParticipateNumber(prize.getParticipateNumber()+1);
            prizeService.update(prize);
            ArrayList<String> users = huntingRecordService.selectHuntingRecordByPrizeId(prizeIdw);
            //返回用户信息
            return Rets.success(users);
        }else if (prize.getTotalNumber()-prize.getParticipateNumber()==1){
            //最后一人开奖逻辑
            //正常参与记录
            this.JoinLuckyDraw(userId,account,prizeIdw,prizeType,prizeNum,prize.getPrizeName());
            //活动关闭
            prize.setParticipateNumber(prize.getParticipateNumber()+1);
            prize.setIsEnd("1");
            prizeService.update(prize);
            //开奖
            this.OpenLuckyDraw(prizeIdw,prizeType);
            return Rets.success();
        }

        return Rets.failure(MessageTemplateEnum.REQUEST_LIMIT.getCode(), MessageTemplateEnum.REQUEST_LIMIT);
    }


    /**
     * @Description: 夺宝抽奖参与
     * @Author: zx
     * @Date: 2023/10/25
     */
    @Transactional
    public void JoinLuckyDraw(Long userId,String account, String prizeIdw, String prizeType,PrizeNum prizeNum,String prizeName) {
        //新增夺宝参与表
        HuntingRecord huntingRecord = new HuntingRecord();
        huntingRecord.setAccount(account);
        huntingRecord.setSourceInvitationCode(prizeNum.getSourceInvitationCode());
        huntingRecord.setHuntIdw(prizeIdw);
        huntingRecord.setIdw(new IdWorker().nextId() + "");
        huntingRecord.setIsFabricate("0");
        huntingRecord.setPrizeName(prizeName);
        huntingRecordService.insert(huntingRecord);
        //减少用户抽奖次数
        prizeNum.setPrizeNum(prizeNum.getPrizeNum()-1);
        prizeNumService.update(prizeNum);
        //新增或修改积分
        List<SearchFilter> filters2 = new ArrayList<>();
        filters2.add(SearchFilter.build("account",account));
        filters2.add(SearchFilter.build("prizeType",prizeType));
        UserScore userScore = userScoreService.get(filters2);
        if (userScore==null){
            UserScore userScore1 = new UserScore();
            userScore1.setAccount(account);
            userScore1.setUserScore(1.0);
            userScore1.setIdw(new IdWorker().nextId() + "");
            userScore1.setUid(userId);
            userScore1.setPrizeType(prizeType);
            userScore1.setSourceInvitationCode(prizeNum.getSourceInvitationCode());
            userScoreService.insert(userScore1);
        }else {
            //积分加1
            userScore.setUserScore(userScore.getUserScore()+1);
            userScoreService.update(userScore);
        }
//        recordInformation.changeUserScore(1.0, userId, prizeNum.getSourceInvitationCode(), account, 8);
    }

    /**
     * @Description: 夺宝抽奖开奖
     * @Author: zx
     * @Date: 2023/10/25
     */
    @Transactional
    public void OpenLuckyDraw(String prizeIdw, String prizeType) {
        //查询是否有预设
        List<SearchFilter> filters1 = new ArrayList<>();
        filters1.add(SearchFilter.build("prizeType",prizeType));
        filters1.add(SearchFilter.build("prizeIdw",prizeIdw));
        ExpectWinningUser expectWinningUser = expectWinningUserService.get(filters1);
        //查询奖品信息
        List<SearchFilter> filters2 = new ArrayList<>();
        filters2.add(SearchFilter.build("idw",prizeIdw));
        Prize prize = prizeService.get(filters2);

        if (expectWinningUser!=null){
            WinningRecord winningRecord = new WinningRecord();
            winningRecord.setAccount(expectWinningUser.getAccount());
            winningRecord.setIdw(new IdWorker().nextId() + "");
            winningRecord.setPrizeType(expectWinningUser.getPrizeType());
            winningRecord.setPrizeIdw(expectWinningUser.getPrizeIdw());
            winningRecord.setPrizeName(prize.getPrizeName());
            winningRecord.setSourceInvitationCode(expectWinningUser.getSourceInvitationCode());
            //余额类型
            if (prize.getPrizeType().equals("1")){
                winningRecord.setAmount(prize.getAmount());
            }
            winningRecord.setHuntRecordIdw(expectWinningUser.getIdw());
            winningRecordService.insert(winningRecord);
            expectWinningUser.setIsPrize("yes");
            expectWinningUserService.update(expectWinningUser);
        }else{
            List<SearchFilter> filters = new ArrayList<>();
            filters.add(SearchFilter.build("huntIdw", prizeIdw));
            List<HuntingRecord> huntingRecords = huntingRecordService.queryAll(filters);
            //随机下标生成用户
            Random ran=new Random();
            int ranNum=ran.nextInt(huntingRecords.size());
            HuntingRecord huntingRecord = huntingRecords.get(ranNum);
            //生成获奖表数据
            WinningRecord winningRecord = new WinningRecord();
            winningRecord.setAccount(huntingRecord.getAccount());
            winningRecord.setIdw(new IdWorker().nextId() + "");
            winningRecord.setPrizeType(prizeType);
            winningRecord.setPrizeIdw(huntingRecord.getHuntIdw());
            winningRecord.setSourceInvitationCode(huntingRecord.getSourceInvitationCode());
            winningRecord.setPrizeName(prize.getPrizeName());
            //余额类型
            if (prize.getPrizeType().equals("1")){
                winningRecord.setAmount(prize.getAmount());
            }
            winningRecord.setHuntRecordIdw(huntingRecord.getIdw());
            winningRecordService.insert(winningRecord);

            //如果造假用户为真0    假1
            if (huntingRecord.getIsFabricate().equals("0")){
                // 奖品如果是金额 去增加用户余额
                if ("1".equals(prize.getTypes())) {
                    // 查询用户余额
                    List<SearchFilter> filters3 = new ArrayList<>();
                    filters3.add(SearchFilter.build("account", winningRecord.getAccount()));
                    filters3.add(SearchFilter.build("prizeType", winningRecord.getPrizeType()));
                    PrizeNum prizeNum1 = prizeNumService.get(filters3);

                    Double balance = apiUserCoom.getUserBalance(prizeNum1.getUid()).doubleValue();
                    recordInformation.transactionRecordPlus(prizeNum1.getSourceInvitationCode(), prizeNum1.getUid(), prizeNum1.getAccount(),
                            balance, prize.getAmount(), BigDecimalUtils.add(balance, prize.getAmount()),
                            prize.getIdw(), 15, "cj", "", "");
                }
            }
        }
    }




}
