package cn.rh.flash.service.dzprize;

import cn.rh.flash.bean.constant.factory.PageFactory;
import cn.rh.flash.bean.entity.dzprize.HuntingRecord;
import cn.rh.flash.bean.entity.dzprize.Prize;
import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.dao.dzprize.HuntingRecordRepository;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.factory.Page;
import cn.rh.flash.warpper.HuntingRecordWrapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;


@Log4j2
@Service
public class HuntingRecordService extends BaseService<HuntingRecord, Long, HuntingRecordRepository> {

    @Autowired
    private PrizeService prizeService;

    @Autowired
    private SysLogService sysLogService;

    @Lazy
    @Autowired
    private LuckyDrawService luckyDrawService;

    @Autowired
    private HuntingRecordRepository huntingRecordRepository;

    @Autowired
    private ConfigCache configCache;


    /**
     * @Description: 查询夺宝参与表用户并展示后五位账号
     * @Author: zx
     * @Date: 2023/10/25
     */
    public ArrayList<String> selectHuntingRecordByPrizeId(String prizeId) {
        ArrayList<String> strings = new ArrayList<>();

        Page<HuntingRecord> page = new PageFactory<HuntingRecord>().defaultPage();
        page.addFilter("huntIdw", prizeId);
        //page.setSort(Sort.by(Sort.Direction.DESC,"id"));
        page = this.queryPage(page);
        List<Map> huntingRecord = (List<Map>) new HuntingRecordWrapper(BeanUtil.objectsToMaps(page.getRecords())).warp();

        for (Map<String, Object> map : huntingRecord
        ) {
            String account = map.get("account").toString();
            strings.add("**" + account.substring((account.length() - 5)));
        }
        return strings;
    }



    /**
     * @Description: 分页查询夺宝参与表用户并展示后五位账号
     * @Author: zx
     * @Date: 2023/10/25
     */
    public List<HuntingRecord> queryHuntingRecordPageList(Page<HuntingRecord> page) {
        String sql = HuntingRecordServiceSql.findHuntingRecordPage(page);
        List<HuntingRecord> huntingRecordList = (List<HuntingRecord>) huntingRecordRepository.queryObjBySql(sql, HuntingRecord.class);
        for (int i = 0;i<huntingRecordList.size();i++){
            HuntingRecord huntingRecord = huntingRecordList.get(i);
            String account = huntingRecord.getAccount();
            huntingRecord.setAccount("**"+account.substring((account.length() - 5)));
        }
        return huntingRecordList;
    }

    /**
     * @Description: 批量添加账号并开奖
     * @Author: zx
     * @Date: 2023/10/25
     */
    @Transactional
    public Ret batchAdd(String prizeIdw, Integer addNum, String userName) {
        List<SearchFilter> filters = new ArrayList<>();
        String testCode = configCache.get(ConfigKeyEnum.TEST_USER_CODE).trim();
        filters.add(SearchFilter.build("idw", prizeIdw));
        Prize prize = prizeService.get(filters);
        //判断奖品是否存在
        if (prize==null){
            return Rets.failure(MessageTemplateEnum.LUCKY_DRAW_PRIZE_IS_NULL.getName(), MessageTemplateEnum.LUCKY_DRAW_PRIZE_IS_NULL);
        }

        //查询奖品信息并且若添加次数到达临界次数立即开奖
        //活动是否结束
        if (prize.getIsEnd().equals("1")) {
            return Rets.failure(MessageTemplateEnum.LUCKY_DRAW_IS_END.getCode(), MessageTemplateEnum.LUCKY_DRAW_IS_END);
        }

        //判定活动次数限制并添加完毕之后开奖
        //若添加数量到达上限，则立即开奖
        if (prize.getParticipateNumber()+addNum>=prize.getTotalNumber()){

            int num = prize.getTotalNumber() - prize.getParticipateNumber();
            //添加不超过上限的数量，并开奖
            Random random = new Random();
            for (int i = 0; i < num; i++) {
                HuntingRecord huntingRecord = new HuntingRecord();
                huntingRecord.setPrizeName(prize.getPrizeName());
                huntingRecord.setHuntIdw(prizeIdw);
                huntingRecord.setIsFabricate("1");
                huntingRecord.setSourceInvitationCode(testCode);
                Integer number = random.nextInt(90000) + 10000;
                huntingRecord.setAccount(number.toString());
                huntingRecord.setIdw(new IdWorker().nextId() + "");
                this.insert(huntingRecord);
                sysLogService.addSysLog(userName, huntingRecord.getId(), "", "PC", SysLogEnum.ADD_HUNTING_RECORD);
            }
            prize.setParticipateNumber(prize.getTotalNumber());
            prize.setIsEnd("1");
            prizeService.update(prize);
            luckyDrawService.OpenLuckyDraw(prizeIdw,prize.getPrizeType());
        }else {
            //正常添加

            Random random = new Random();
            for (int i = 0; i < addNum; i++) {
                HuntingRecord huntingRecord = new HuntingRecord();
                huntingRecord.setPrizeName(prize.getPrizeName());
                huntingRecord.setHuntIdw(prizeIdw);
                huntingRecord.setIsFabricate("1");
                huntingRecord.setSourceInvitationCode(testCode);
                Integer number = random.nextInt(90000) + 10000;
                huntingRecord.setAccount(number.toString());
                huntingRecord.setIdw(new IdWorker().nextId() + "");
                this.insert(huntingRecord);
                sysLogService.addSysLog(userName, huntingRecord.getId(), "", "PC", SysLogEnum.ADD_HUNTING_RECORD);
            }
            prize.setParticipateNumber(prize.getParticipateNumber()+addNum);
            prizeService.update(prize);
        }
        return Rets.success();
    }





}
