package cn.rh.flash.service.dzuser;


import cn.hutool.core.util.ObjUtil;
import cn.rh.flash.bean.dto.FalseDataForm;
import cn.rh.flash.bean.entity.dzpower.RecordPb;
import cn.rh.flash.bean.entity.dzpower.TotalBonusPb;
import cn.rh.flash.bean.entity.dzuser.FalseData;
import cn.rh.flash.bean.entity.dzuser.TransactionRecord;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.bean.vo.dzuser.TransactionRecordVo;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.dao.dzpower.RecordPbRepository;
import cn.rh.flash.dao.dzuser.TransactionRecordRepository;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.service.coom.dz.ApiUserCoom;
import cn.rh.flash.service.coom.dz.RecordInformation;
import cn.rh.flash.service.dzpower.RecordPbService;
import cn.rh.flash.service.dzpower.RecordPbServiceSql;
import cn.rh.flash.service.dzpower.TotalBonusPbService;
import cn.rh.flash.service.dzsys.SysLogService;
import cn.rh.flash.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.rh.flash.security.JwtUtil.getUsername;

@Service
public class TransactionRecordService extends BaseService<TransactionRecord,Long,TransactionRecordRepository>  {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private TransactionRecordRepository transactionRecordRepository;

    @Autowired
    private ConfigCache configCache;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private SysLogService sysLogService;

    @Autowired
    private RecordInformation recordInformation;

    @Autowired
    private TotalBonusPbService totalBonusPbService;

    @Autowired
    private RecordPbService recordPbService;

    @Autowired
    private RecordPbRepository recordPbRepository;

    @Autowired
    private ApiUserCoom apiUserCoom;

    @Autowired
    private FalseDataService falseDataService;

    @Transactional(rollbackFor = Exception.class)
    public Ret addTranFalse(FalseDataForm falseDataForm, String userName) {

        String[] falseDate = falseDataForm.getFalseDate().replace("\n","").split(",");

        FalseData falseData = new FalseData(falseDataForm);

        String textCode = configCache.get(ConfigKeyEnum.TEST_USER_CODE);
        int count = falseDate.length;
        for (String date : falseDate) {
            String[] split = date.split("---");
            if (split.length!=3){
                // 数据格式不对
                count--;
                continue;
            }
            String account = split[0];
            String money = split[1];
            String dateTime = split[2];
            // 查询当前用户是否为测试用户
            UserInfo userInfo = userInfoService.get(SearchFilter.build("account", account));
            if (userInfo == null){
                count--;
                continue;
            }
            if (!userInfo.getSourceInvitationCode().equals(textCode)){
                // 不为测试账户 直接跳过 进行下一次循环
                count--;
                continue;
            }
            // 查询用户余额
            Double balance = apiUserCoom.getUserBalance(userInfo.getId()).doubleValue();
            RecordPb record = new RecordPb();
            record.setIdw(new IdWorker().nextId() + "");
            record.setSourceInvitationCode(userInfo.getSourceInvitationCode());
            record.setUid(userInfo.getId());
            record.setAccount(account);
            record.setMoney(Double.valueOf(money));
            record.setRelevels(0);
            record.setFormerCreditScore(balance);
            record.setPostCreditScore(BigDecimalUtils.add(balance, record.getMoney()));
            record.setSourceUserAccount(userInfo.getAccount());
            record.setRebateTime(DateUtil.parseTime(DateUtil.getTime()) );
            record.setFidw(falseData.getIdw());
            TotalBonusPb uid1 = totalBonusPbService.get(SearchFilter.build("uid", record.getUid()));

            if (uid1 != null) {
                uid1.setTotalBonusIncome(BigDecimalUtils.add(uid1.getTotalBonusIncome(), record.getMoney()));
            } else {
                uid1 = new TotalBonusPb();
                uid1.setIdw(new IdWorker().nextId() + "");
                uid1.setSourceInvitationCode(record.getSourceInvitationCode());
                uid1.setDzversion(0);
                uid1.setUid(record.getUid());
                uid1.setAccount(record.getAccount());
                uid1.setTotalBonusIncome(record.getMoney());
                uid1.setSourceUserAccount(userInfo.getAccount());
            }
            Integer tx = recordInformation.transactionRecordFalseDate(record.getSourceInvitationCode(), record.getUid(), record.getAccount(),
                    record.getFormerCreditScore(), record.getMoney(), record.getPostCreditScore(),
                    record.getIdw(), 5, "cdbz", "",falseData.getIdw(),dateTime);
            if (tx != 0) {
                uid1.setDzversion(tx);
            }
            if (uid1 != null) {
                totalBonusPbService.update(uid1);
            } else {
                totalBonusPbService.insert(uid1);
            }

            recordPbRepository.execute(RecordPbServiceSql.updateRecordPb(record,dateTime));
        }
        falseData.setRemark("添加交易造假数据:成功"+count+"条,失败"+(falseDate.length-count)+"条");
        if (count>0){
            falseDataService.insert(falseData);
        }
        sysLogService.addSysLog(userName,null,"PC", SysLogEnum.FALSE_DATE,
                getUsername()+"--在"+ DateUtil.getTime()+"--增加交易造假数据:"+falseDate.length+"条");

        return Rets.success("添加交易造假数据:成功"+count+"条,失败"+(falseDate.length-count)+"条");
    }

    public void exportV2(HttpServletResponse response, List<Map<String, Object>> list) {
        ArrayList<TransactionRecordVo> listVo = new ArrayList<>();
        for (Map<String, Object> stringObjectMap : list) {
            TransactionRecordVo vo=new TransactionRecordVo();
            BeanUtil.mapToBean(stringObjectMap,vo);
            if (ObjUtil.isNotEmpty(stringObjectMap.get("transactionType_str"))){
                vo.setTransactionTypeName(stringObjectMap.get("transactionType_str").toString());
            }
            vo.setMoneyName(stringObjectMap.get("additionAndSubtraction_str").toString()+stringObjectMap.get("money").toString());
            listVo.add(vo);
        }
        EasyExcelUtil.export(response,"交易记录",listVo,TransactionRecordVo.class);
    }
}

