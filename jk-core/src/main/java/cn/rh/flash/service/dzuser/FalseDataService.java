package cn.rh.flash.service.dzuser;


import cn.rh.flash.bean.entity.dzpower.TotalBonusPb;
import cn.rh.flash.bean.entity.dzuser.FalseData;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.dao.dzuser.FalseDataRepository;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.service.coom.dz.RecordInformation;
import cn.rh.flash.service.dzpower.TotalBonusPbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class FalseDataService extends BaseService<FalseData,Long, FalseDataRepository> {

    @Autowired
    private FalseDataRepository falseDataRepository;

    @Autowired
    private ConfigCache configCache;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private TotalBonusPbService totalBonusPbService;

    @Autowired
    private RecordInformation recordInformation;

    @Transactional(rollbackFor = Exception.class)
    public Ret delFalseData(Long id) {
        FalseData falseData = this.get(id);

        if ("1".equals(falseData.getIsDel())){
            return Rets.failure("当前相关造假数据已删除,请勿重复操作");
        }

        switch (falseData.getFalseType()){
            // 1.提现记录 2.交易记录(CDB返佣) 3.用户下级
            case "1":
                delWithFalse(falseData);
                break;
            case "2":
                delTranFalse(falseData);
                break;
            case "3":
                delUser(falseData);
                break;
            case "4":
                delRechargeFalse(falseData);
                break;
        }
        falseData.setIsDel("1");
        this.update(falseData);
        return Rets.success();
    }


    // 删除提现记录相关
    @Transactional
    private void delWithFalse(FalseData falseData) {
        falseDataRepository.execute(FalseDataServiceSql.delWithFalse(falseData.getIdw()));
    }
    // 删除交易记录(CDB返佣) 相关
    @Transactional
    private void delTranFalse(FalseData falseData) {
        String[] falseDate = falseData.getFalseDate().split(",");
        String textCode = configCache.get(ConfigKeyEnum.TEST_USER_CODE);

        Map<String,Double> moneyMap = new HashMap();
        Map<String,UserInfo> userMap = new HashMap();
        // 循环处理假数据  因为可能有多个用户假数据
        for (String date : falseDate) {
            String[] split = date.split("---");
            String account = split[0];
            String money = split[1];
            // 查询当前用户是否为测试用户
            UserInfo userInfo = userInfoService.get(SearchFilter.build("account", account));
            if (userInfo == null) {
                continue;
            }
            if (!userInfo.getSourceInvitationCode().equals(textCode)) {
                // 不为测试账户 直接跳过 进行下一次循环
                continue;
            }
            // 将相同的用户 金额累加
            if (moneyMap.get(account) == null) {
                moneyMap.put(account,Double.valueOf(money));
            }else {
                moneyMap.put(account,moneyMap.get(account)+Double.valueOf(money));
            }
            userMap.put(account,userInfo);
        }

        for (String key : moneyMap.keySet()) {
            // 先删除充电宝返佣总收入
            TotalBonusPb totalBonusPb = totalBonusPbService.get(SearchFilter.build("account", key));
            totalBonusPb.setTotalBonusIncome(totalBonusPb.getTotalBonusIncome()-moneyMap.get(key));
            totalBonusPbService.update(totalBonusPb);
            // 减用户钱包余额
            UserInfo userInfo = userMap.get(key);
            recordInformation.updateUserBalance2(userInfo.getSourceInvitationCode(), userInfo.getId(), userInfo.getAccount()
                    , moneyMap.get(key), 2, true);
        }
        // 删除充电宝返佣记录
        falseDataRepository.execute(FalseDataServiceSql.delRecordFalse(falseData.getIdw()));
        // 删除交易记录
        falseDataRepository.execute(FalseDataServiceSql.delTranFalse(falseData.getIdw()));

    }
    // 删除用户下级相关
    @Transactional
    private void delUser(FalseData falseData) {
        // 将状态修改为已删除
        falseDataRepository.execute(FalseDataServiceSql.delUser(falseData.getIdw()));
        // 删除vip返佣
        falseDataRepository.execute(FalseDataServiceSql.delVipRebate(falseData.getIdw()));
        // 删除cdb返佣
        falseDataRepository.execute(FalseDataServiceSql.delCdbRebate(falseData.getIdw()));

    }
    // 删除 充值记录  相关
    @Transactional
    private void delRechargeFalse(FalseData falseData) {
        String[] falseDate = falseData.getFalseDate().split(",");
        String textCode = configCache.get(ConfigKeyEnum.TEST_USER_CODE);
        // 删除充值记录
        falseDataRepository.execute(FalseDataServiceSql.delRechargeFalse(falseData.getIdw()));
        // 减用户钱包余额
        Map<String,Double> moneyMap = new HashMap();
        Map<String,UserInfo> userMap = new HashMap();
        // 循环处理假数据  因为可能有多个用户假数据
        for (String date : falseDate) {
            String[] split = date.split("---");
            String account = split[0];
            String money = split[1];
            // 查询当前用户是否为测试用户
            UserInfo userInfo = userInfoService.get(SearchFilter.build("account", account));
            if (userInfo == null) {
                continue;
            }
            if (!userInfo.getSourceInvitationCode().equals(textCode)) {
                // 不为测试账户 直接跳过 进行下一次循环
                continue;
            }
            // 将相同的用户 金额累加
            if (moneyMap.get(account) == null) {
                moneyMap.put(account,Double.valueOf(money));
            }else {
                moneyMap.put(account,moneyMap.get(account)+Double.valueOf(money));
            }
            userMap.put(account,userInfo);
        }
        for (String key : moneyMap.keySet()) {
            // 减用户钱包余额
            UserInfo userInfo = userMap.get(key);
            recordInformation.updateUserBalance2(userInfo.getSourceInvitationCode(), userInfo.getId(), userInfo.getAccount()
                    , moneyMap.get(key), 2, true);
        }
    }
}
