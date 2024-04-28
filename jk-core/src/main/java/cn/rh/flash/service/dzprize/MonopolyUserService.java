package cn.rh.flash.service.dzprize;

import cn.rh.flash.bean.dto.api.MonopolyRecordDto;
import cn.rh.flash.bean.entity.dzprize.*;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.vo.api.MonopolyUserAndNumVo;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.dao.dzprize.MonopolyUserRepository;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.service.coom.dz.ApiUserCoom;
import cn.rh.flash.service.coom.dz.RecordInformation;
import cn.rh.flash.utils.BigDecimalUtils;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.RedisUtil;
import cn.rh.flash.utils.StringUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;


@Log4j2
@Service
public class MonopolyUserService extends BaseService<MonopolyUser, Long, MonopolyUserRepository> {

    @Autowired
    private MonopolyUserRepository monopolyUserRepository;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private PrizeService prizeService;

    @Autowired
    private MonopolyRecordService monopolyRecordService;

    @Autowired
    private WinningRecordService winningRecordService;

    @Autowired
    private ApiUserCoom apiUserCoom;

    @Autowired
    private PrizeNumService prizeNumService;

    @Autowired
    private RecordInformation recordInformation;

    /**
     * 查询当前大富翁的用户信息，若没有，则创建
     * @param uid
     * @param account
     * @param sourceInvitationCode
     * @return
     */
    public MonopolyUserAndNumVo selectOrCreateMonopolyUser(Long uid,String account,String sourceInvitationCode){
        //查询用户大富翁数据
        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("uid",uid));
        MonopolyUser monopolyUser = this.get(filters);

        if(monopolyUser==null){
            //创建新用户并返回用户信息
            MonopolyUser monopolyUser1 = new MonopolyUser();
            monopolyUser1.setIdw(new IdWorker().nextId() + "");
            monopolyUser1.setPosition(0);
            monopolyUser1.setSourceInvitationCode(sourceInvitationCode);
            monopolyUser1.setActivityType("10");
            monopolyUser1.setUid(uid);
            monopolyUser1.setAccount(account);
            this.insert(monopolyUser1);
        }
        Map map = monopolyUserRepository.queryMonopolyUserAndNum(uid);
        if (map.size()==0){
            MonopolyUserAndNumVo monopolyUserAndNumVo = new MonopolyUserAndNumVo();
            monopolyUserAndNumVo.setPosition(0);
            monopolyUserAndNumVo.setNum(0);
            return monopolyUserAndNumVo;
        }
        MonopolyUserAndNumVo monopolyUserAndNumVo = new MonopolyUserAndNumVo();
        monopolyUserAndNumVo.setId(Long.parseLong(map.get("id").toString()));
        monopolyUserAndNumVo.setUid(Long.parseLong(map.get("uid").toString()));
        monopolyUserAndNumVo.setAccount(map.get("account").toString());
        monopolyUserAndNumVo.setActivityType(map.get("activityType").toString());
        monopolyUserAndNumVo.setIdw(map.get("idw").toString());
        monopolyUserAndNumVo.setPosition(Integer.parseInt(map.get("position").toString()));
        monopolyUserAndNumVo.setSourceInvitationCode(map.get("sourceInvitationCode").toString());
        monopolyUserAndNumVo.setNum(Integer.parseInt(map.get("num").toString()));
        monopolyUserAndNumVo.setPrizeNumId(Long.parseLong(map.get("prizeNumId").toString()));
        return monopolyUserAndNumVo;
    }


    /**
     * 参与大富翁游戏
     * @return
     */
    @Transactional
    public Ret joinMonopoly(Long uid,String account,String sourceInvitationCode){
        //查询是否有抽奖次数
        MonopolyUserAndNumVo monopolyUserAndNumVo = this.selectOrCreateMonopolyUser(uid,account,sourceInvitationCode);
        if (monopolyUserAndNumVo==null||monopolyUserAndNumVo.getNum()==0){
            return Rets.failure(MessageTemplateEnum.COUNT_RUN_OUT.getCode(), MessageTemplateEnum.COUNT_RUN_OUT);
        }

        //查询奖品列表
        List<Prize> prizes = prizeService.getMonopolyPrizeList();

        //随机数一次1-6
        Random ran=new Random();
        Integer ranNum=ran.nextInt(6)+1;

        //获取用户当前定位，并计算所获奖品
        Integer position = monopolyUserAndNumVo.getPosition();
        Integer num = position + ranNum;
        Integer size = prizes.size();
        if(num>=size){
            num=num-size;
        }

        Prize prize = prizes.get(num);
        //修改大富翁用户信息
        MonopolyUser monopolyUser = this.get(monopolyUserAndNumVo.getId());
        monopolyUser.setPosition(num);
        //修改用户参与次数信息
        PrizeNum prizeNum = prizeNumService.get(monopolyUserAndNumVo.getPrizeNumId());
        prizeNum.setPrizeNum(monopolyUserAndNumVo.getNum() - 1);
        prizeNumService.update(prizeNum);

        //生成用户操作记录
        MonopolyRecord monopolyRecord = this.createMonopolyRecord(prize,ranNum,monopolyUserAndNumVo,position,num,"7");
        //发放奖励
        ArrayList<MonopolyRecord> monopolyRecords1 = this.grantPrize(uid, prizes, num, monopolyRecord, monopolyUserAndNumVo, 1);
        ArrayList<MonopolyRecord> monopolyRecords = new ArrayList<>();
        monopolyRecords.add(monopolyRecord);
        monopolyRecords.addAll(monopolyRecords1);

//        if (monopolyRecord1!=null){
//            monopolyRecords.add(monopolyRecord1);
//        }
        ArrayList<MonopolyRecordDto> monopolyRecordDtos = new ArrayList<>();
        for (int i = 0; i < monopolyRecords.size(); i++) {
            MonopolyRecord monopolyRecord1 = monopolyRecords.get(i);
            MonopolyRecordDto monopolyRecordDto = new MonopolyRecordDto();
            monopolyRecordDto.setDicePoints(monopolyRecord1.getDicePoints());
            monopolyRecordDto.setLastPosition(monopolyRecord1.getLastPosition());
            monopolyRecordDto.setPrizeIdw(monopolyRecord1.getPrizeIdw());
            monopolyRecordDto.setNowPosition(monopolyRecord1.getNowPosition());
            monopolyRecordDto.setSurplusNumber(monopolyRecord1.getSurplusNumber());
            monopolyRecordDto.setPrizeId(monopolyRecord1.getPrizeId());
            monopolyRecordDto.setPrizeName(monopolyRecord1.getPrizeName());
            monopolyRecordDto.setPrizeType(monopolyRecord1.getPrizeType());
            monopolyRecordDto.setPrizePicUrl(monopolyRecord1.getPrizePicUrl());
            monopolyRecordDto.setLastPrizeType(monopolyRecord1.getLastPrizeType());
            monopolyRecordDtos.add(monopolyRecordDto);
        }

        return Rets.success(monopolyRecordDtos);
    }

    /**
     * 发奖
     * @param uid
     * @param prizes 1:余额,2:实物,3:信誉分增加,4:信誉分减少,5:抽奖次数增加,6:抽奖次数减少,7:前进步数,8:后退步数,0:无操作
     * @param index
     * @param monopolyRecord
     * @return -1 发放失败
     */
    @Transactional
    public ArrayList<MonopolyRecord> grantPrize(Long uid,List<Prize> prizes,Integer index,MonopolyRecord monopolyRecord,MonopolyUserAndNumVo monopolyUserAndNumVo,Integer outIndex) {
        ArrayList<MonopolyRecord> monopolyRecords = new ArrayList<>();
        //递归结束规则
        if (outIndex>3){
            return monopolyRecords;
        }
        WinningRecord winningRecord = new WinningRecord();
        Prize prize = prizes.get(index);
        UserInfo oneBySql = apiUserCoom.getOneBySql(uid);
        switch (prize.getTypes()){
            //余额
            case "1" :
                //增加中奖记录
                winningRecord.setAccount(monopolyRecord.getAccount());
                winningRecord.setIdw(new IdWorker().nextId() + "");
                winningRecord.setPrizeType(prize.getPrizeType());
                winningRecord.setUid(uid);
                winningRecord.setPrizeIdw(prize.getIdw());
                winningRecord.setSourceInvitationCode(monopolyRecord.getSourceInvitationCode());
                winningRecord.setPrizeName(prize.getPrizeName());
                winningRecord.setAmount(prize.getAmount());
                winningRecord.setHuntRecordIdw(monopolyRecord.getIdw());
                winningRecord.setSurplusNumber(monopolyRecord.getSurplusNumber());
                winningRecordService.insert(winningRecord);

                //发放余额
                Double balance = apiUserCoom.getUserBalance(monopolyRecord.getUid()).doubleValue();
                recordInformation.transactionRecordPlus(monopolyRecord.getSourceInvitationCode(), monopolyRecord.getUid(), monopolyRecord.getAccount(),
                        balance, prize.getAmount(), BigDecimalUtils.add(balance, prize.getAmount()),
                        prize.getIdw(), 12, "cj", "", "");
                return monopolyRecords;
            //实物
            case "2":
                //增加中奖记录
                winningRecord.setAccount(monopolyRecord.getAccount());
                winningRecord.setIdw(new IdWorker().nextId() + "");
                winningRecord.setPrizeType(prize.getPrizeType());
                winningRecord.setPrizeIdw(prize.getIdw());
                winningRecord.setUid(uid);
                winningRecord.setSourceInvitationCode(monopolyRecord.getSourceInvitationCode());
                winningRecord.setPrizeName(prize.getPrizeName());
                winningRecord.setAmount(0.0);
                winningRecord.setHuntRecordIdw(monopolyRecord.getIdw());
                winningRecord.setSurplusNumber(monopolyRecord.getSurplusNumber());
                winningRecordService.insert(winningRecord);
                return monopolyRecords;
            //实物
            case "3":
                //增加中奖记录
                winningRecord.setAccount(monopolyRecord.getAccount());
                winningRecord.setIdw(new IdWorker().nextId() + "");
                winningRecord.setPrizeType(prize.getPrizeType());
                winningRecord.setPrizeIdw(prize.getIdw());
                winningRecord.setUid(uid);
                winningRecord.setSourceInvitationCode(monopolyRecord.getSourceInvitationCode());
                winningRecord.setPrizeName(prize.getPrizeName());
                winningRecord.setAmount(0.0);
                winningRecord.setHuntRecordIdw(monopolyRecord.getIdw());
                winningRecord.setSurplusNumber(monopolyRecord.getSurplusNumber());
                winningRecordService.insert(winningRecord);
                //信誉分增加
                recordInformation.changeCredit(oneBySql.getSourceInvitationCode(), oneBySql.getId(), oneBySql.getAccount(), "1", "8", "1", prize.getTotalNumber(), "", oneBySql.getAccount(), oneBySql.getVipType());
                return monopolyRecords;
            //实物
            case "4":
                //增加中奖记录
                winningRecord.setAccount(monopolyRecord.getAccount());
                winningRecord.setIdw(new IdWorker().nextId() + "");
                winningRecord.setPrizeType(prize.getPrizeType());
                winningRecord.setPrizeIdw(prize.getIdw());
                winningRecord.setSourceInvitationCode(monopolyRecord.getSourceInvitationCode());
                winningRecord.setPrizeName(prize.getPrizeName());
                winningRecord.setAmount(0.0);
                winningRecord.setUid(uid);
                winningRecord.setHuntRecordIdw(monopolyRecord.getIdw());
                winningRecord.setSurplusNumber(monopolyRecord.getSurplusNumber());
                winningRecordService.insert(winningRecord);
                //信誉分减少
                recordInformation.changeCredit(oneBySql.getSourceInvitationCode(), oneBySql.getId(), oneBySql.getAccount(), "1", "8", "2", prize.getTotalNumber(), "", oneBySql.getAccount(), oneBySql.getVipType());
                return monopolyRecords;
            //前进
            case "7":
                //前进后的奖品
                Integer num = index + prize.getTotalNumber();
                Integer size = prizes.size();
                if(num>=size){
                    num=num-size;
                }
                //修改大富翁用户信息
                MonopolyUser monopolyUser = this.get(monopolyUserAndNumVo.getId());
                monopolyUser.setPosition(num);
                //创建操作记录
                MonopolyRecord monopolyRecord1 = this.createMonopolyRecord(prizes.get(num),prize.getTotalNumber(),monopolyUserAndNumVo,index,num,"7");
                //二次开奖
                outIndex+=1;
                ArrayList<MonopolyRecord> monopolyRecords1 = this.grantPrize(uid, prizes, num, monopolyRecord1, monopolyUserAndNumVo, outIndex);
                monopolyRecords.add(monopolyRecord1);
                monopolyRecords.addAll(monopolyRecords1);

                return monopolyRecords;
            case "8":
                //后退后的奖品
                Integer num1 = index - prize.getTotalNumber();
                Integer size1 = prizes.size();
                if(num1<0){
                    num1=size1+num1;
                }
                //修改大富翁用户信息
                MonopolyUser monopolyUser1 = this.get(monopolyUserAndNumVo.getId());
                monopolyUser1.setPosition(num1);
                //创建操作记录
                MonopolyRecord monopolyRecord2 = this.createMonopolyRecord(prizes.get(num1),prize.getTotalNumber(),monopolyUserAndNumVo,index,num1,"8");
                //二次开奖
                outIndex+=1;
                ArrayList<MonopolyRecord> monopolyRecords2 = this.grantPrize(uid, prizes, num1, monopolyRecord2, monopolyUserAndNumVo, outIndex);
                monopolyRecords.add(monopolyRecord2);
                monopolyRecords.addAll(monopolyRecords2);

                return monopolyRecords;
            default:
                break;
        }
        return monopolyRecords;
    }


    /**
     * 生成操作记录
     * @param prize
     * @param monopolyUserAndNumVo
     * @param lastPosition
     * @param dicePoints
     * @param nowPosition
     * @return
     */
    @Transactional
    public MonopolyRecord createMonopolyRecord(Prize prize,Integer dicePoints,MonopolyUserAndNumVo monopolyUserAndNumVo,Integer lastPosition,Integer nowPosition,String type) {
        //生成用户操作记录
        MonopolyRecord monopolyRecord = new MonopolyRecord();
        monopolyRecord.setAccount(monopolyUserAndNumVo.getAccount());
        monopolyRecord.setIdw(new IdWorker().nextId() + "");
        monopolyRecord.setUid(monopolyUserAndNumVo.getUid());
        monopolyRecord.setPrizeName(prize.getPrizeName());
        monopolyRecord.setPrizePicUrl(prize.getUrl());
        monopolyRecord.setPrizeId(prize.getId());
        monopolyRecord.setPrizeIdw(prize.getIdw());
        monopolyRecord.setIdw(prize.getIdw());
        monopolyRecord.setPrizeType(prize.getTypes());
        monopolyRecord.setDicePoints(dicePoints);
        monopolyRecord.setSourceInvitationCode(monopolyUserAndNumVo.getSourceInvitationCode());
        monopolyRecord.setSurplusNumber(monopolyUserAndNumVo.getNum()-1);
        monopolyRecord.setNowPosition(nowPosition);
        monopolyRecord.setLastPosition(lastPosition);
        monopolyRecord.setLastPrizeType(type);
        monopolyRecordService.insert(monopolyRecord);
        return monopolyRecord;
    }

    /**
     * 大富翁活动重置
     * @return
     */
    @Transactional
    public void resettingUserState() {
        monopolyUserRepository.resettingUserState();
    }



}
