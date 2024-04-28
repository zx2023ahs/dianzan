package cn.rh.flash.service.dzuser;


import cn.rh.flash.bean.entity.dzuser.UserBalance;
import cn.rh.flash.bean.enumeration.ConfigKeyEnum;
import cn.rh.flash.bean.vo.dz.BalanceRanking;
import cn.rh.flash.bean.vo.dz.IpRanking;
import cn.rh.flash.cache.ConfigCache;
import cn.rh.flash.dao.dzuser.UserBalanceRepository;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.utils.factory.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserBalanceService extends BaseService<UserBalance,Long,UserBalanceRepository>  {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private UserBalanceRepository userBalanceRepository;

    @Autowired
    private ConfigCache configCache;


    /**
     * 余额排行
     * @param page
     * @return
     */
    public List<BalanceRanking> queryPageSqlBYBalance(Page<UserBalance> page,String ucode) {

        String testCode = configCache.get(ConfigKeyEnum.TEST_USER_CODE).trim();


//        String sql = UserBalanceServiceSql.sqlBalanceRanking(page.getLimit(),ucode,testCode);
        String sql = UserBalanceServiceSql.sqlBalanceRanking(1,page.getLimit(),ucode,testCode);



        List<BalanceRanking> balanceRankings = (List<BalanceRanking>) userBalanceRepository.queryObjBySql( sql, BalanceRanking.class );

//        Set<String> accounts = balanceRankings.stream().map(BalanceRanking::getAccount).collect(Collectors.toSet());
//        String s = "('-1'";
//        for (String account : accounts) {
//            s = s+",'"+account+"'";
//        }
//        s = s +")";
//        String numSql = UserBalanceServiceSql.getUserNum(s);
//
//        List<BalanceRanking> numRanks = (List<BalanceRanking>) userBalanceRepository.queryObjBySql( numSql, BalanceRanking.class );
//
//        Map<String, String> collect = numRanks.stream().collect(Collectors.toMap(BalanceRanking::getAccount, BalanceRanking::getNumberOfSubordinates));
//
//        for (BalanceRanking balanceRanking : balanceRankings) {
//            balanceRanking.setNumberOfSubordinates(collect.get(balanceRanking.getAccount()));
//        }
        return balanceRankings;
    }

    /**
     * ip 排行
     * @param page
     * @return
     */
    public List<IpRanking> queryPageSqlByIp(Page<UserBalance> page,String ucode) {
        String testCode = configCache.get(ConfigKeyEnum.TEST_USER_CODE).trim();

        String sql = UserBalanceServiceSql.sqlIpRanking( page.getLimit(),ucode,testCode );
        System.out.println( sql );
        return (List<IpRanking>) userBalanceRepository.queryObjBySql( sql, IpRanking.class );
    }

    /**
     * 修改用户余额
     * @param userBalance
     * @param dzversion
     */
    @Transactional(rollbackFor = Exception.class)
    public int findByDzversionAndIdForUpdateByUserBalance(double userBalance, Integer dzversion,Long id) {
        String sql = UserBalanceServiceSql.updateUserBalanceByIdAndDzversion(userBalance,id,dzversion );
        return userBalanceRepository.execute(sql);

    }

}

