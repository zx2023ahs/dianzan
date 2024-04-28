package cn.rh.flash.service.dzvip;


import cn.rh.flash.bean.entity.dzvip.TeamVIPActivationTotalRevenue;
import cn.rh.flash.bean.vo.dz.NumberOfVipLevels;
import cn.rh.flash.bean.vo.dz.TeamStatistics;
import cn.rh.flash.bean.vo.dz.UserInformation;
import cn.rh.flash.dao.dzvip.TeamVIPActivationTotalRevenueRepository;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.service.system.impl.ConstantFactory;
import cn.rh.flash.utils.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TeamVIPActivationTotalRevenueService extends BaseService<TeamVIPActivationTotalRevenue, Long, TeamVIPActivationTotalRevenueRepository> {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private TeamVIPActivationTotalRevenueRepository teamVIPActivationTotalRevenueRepository;

    // 今日新增用户 / 昨日新增  / vip总数

    /**
     * 团队统计数据
     *
     * @return
     */
    public TeamStatistics teamStatisticsOne(String phone) {

        String sql = String.format(" select %s %s %s %s %s from t_dzuser_user %s",
                // 返回值
                "registration_time as create_time,",
                "sum( case when DATEDIFF( registration_time,NOW())=0  then 1 else 0 end) as addedToday,", //今日新增
                "sum( case when DATEDIFF( registration_time,NOW())=-1 then 1 else 0 end) as addedYesterday,", //昨日新增
                "sum( case when 1=1 then 1 else 0 end) as total,", //总数 总新增
                "sum( case when vip_type !='v0' and vip_type !='v1'  then 1 else 0 end) as totalNumberOfVips", //vip总数
                /// 条件
                "where pinvitation_code like ( select CONCAt('%[',invitation_code,']%') from  t_dzuser_user where  account = '" + phone + "') " +
                        " and levels > ( select levels from  t_dzuser_user where account = '" + phone + "' )"
        );
        Map mapBySql = teamVIPActivationTotalRevenueRepository.getMapBySql(sql);
        return BeanUtil.mapToBean(mapBySql, new TeamStatistics());
    }

    // 今日首充() / 昨日首充()

    /**
     * 团队统计数据
     *
     * @return
     */
    public TeamStatistics teamStatisticsTwo(String phone) {
        String sql = String.format(" select %s %s %s from ( %s ) as dat",
                // 返回值
                "dat.create_time as create_time,",
                "sum( case when  dat.num > 0  then dat.num else 0 end) as firstChargeToday,", //今日首充
                "sum( case when  dat.num2 > 0  then dat.num2 else 0 end) as firstChargeYesterday", //昨日首充
                /// 虚拟表
                "  select " +
                    "   max(create_time) as create_time, " +
                    "   sum( case when DATEDIFF( create_time,NOW())=0    then 1 else 0 end) as num, " +
                    "   sum( case when DATEDIFF( create_time,NOW())=-1   then 1 else 0 end) as num2 " +
                    "from t_dzvip_vippurchase where (previous_vip_type = 'v0' or previous_vip_type = 'v1')" +
                        " AND after_vip_type !='v1' and whether_to_pay = '2' and " +
                    "   EXISTS (" +
                    "       SELECT id FROM t_dzuser_user WHERE " +
                    "           id = t_dzvip_vippurchase.uid and " +
                    "           pinvitation_code like (SELECT CONCAt('%[',invitation_code,']%') FROM t_dzuser_user WHERE account = '" + phone + "' ) and " +
                    "           levels > (SELECT levels FROM t_dzuser_user WHERE account = '"+phone+"' ) " +
                    "   ) "


        );
        Map mapBySql = teamVIPActivationTotalRevenueRepository.getMapBySql(sql);
        return BeanUtil.mapToBean(mapBySql, new TeamStatistics());
    }


    public TeamStatistics teamStatisticsFive(String phone) {
        String sql = String.format(" select %s %s from t_dzvip_vippurchase where %s",
                // 返回值
                "sum( case when DATEDIFF( create_time,NOW())=0  then 1 else 0 end) as jjToday,", //今日总晋级
                "sum( case when DATEDIFF( create_time,NOW())=-1   then 1 else 0 end) as jjYesterday", //昨日总晋级
                /// 虚拟表
                            " previous_vip_type != 'v0' and previous_vip_type != 'v1' and whether_to_pay = '2' and EXISTS (" +
                            "       SELECT id FROM t_dzuser_user WHERE " +
                            "           id = t_dzvip_vippurchase.uid and " +
                            "           pinvitation_code like (SELECT CONCAt('%[',invitation_code,']%') FROM t_dzuser_user WHERE account = '" + phone + "' ) and " +
                            "           levels > (SELECT levels FROM t_dzuser_user WHERE account = '"+phone+"' ) " +
                            "   ) "
        );

        Map mapBySql = teamVIPActivationTotalRevenueRepository.getMapBySql(sql);
        return BeanUtil.mapToBean(mapBySql, new TeamStatistics());
    }

    public TeamStatistics teamStatisticsSex(String phone) {
        String sql = String.format(" select %s %s from t_dzvip_vippurchase where %s",
                // 返回值
                "sum( case when DATEDIFF( create_time,NOW())=0  then 1 else 0 end) as addedVipToday,", //今日新增  vip
                "sum( case when DATEDIFF( create_time,NOW())=-1   then 1 else 0 end) as addedVipYesterday", //昨日新增  vip
                /// 虚拟表
                " (previous_vip_type = 'v0' or previous_vip_type = 'v1') and after_vip_type != 'v1' and  whether_to_pay = '2' and EXISTS (" +
                        "       SELECT id FROM t_dzuser_user WHERE " +
                        "           id = t_dzvip_vippurchase.uid and " +
                        "           pinvitation_code like (SELECT CONCAt('%[',invitation_code,']%') FROM t_dzuser_user WHERE account = '" + phone + "' ) and " +
                        "           levels > (SELECT levels FROM t_dzuser_user WHERE account = '"+phone+"' ) " +
                        "   ) "
        );

        Map mapBySql = teamVIPActivationTotalRevenueRepository.getMapBySql(sql);
        return BeanUtil.mapToBean(mapBySql, new TeamStatistics());
    }

    // 今日充值总金额 / 昨日充值总金额 / 总充值 / 今日充值次数/ 昨日充值次数/ 总次数 /

    /**
     * 团队统计数据
     *
     * @return
     */
    public TeamStatistics teamStatisticsThree(String phone) {

        String sql = String.format(" select %s %s %s %s %s %s %s from t_dzuser_rechargehistory %s )",
                // 返回值
                "modify_time,",
                "sum( case when DATEDIFF( modify_time,NOW())=0    then money else 0 end) as totalRechargeAmountToday,",  //今日充值总金额
                "sum( case when DATEDIFF( modify_time,NOW())=-1    then money else 0 end) as totalRechargeAmountYesterday,",  //昨日充值总金额
                "sum( money) AS totalRecharge, ",  //总充值
                "sum( case when DATEDIFF( modify_time,NOW())=0  then 1 else 0 end) as totalRechargeNumToday,",  //今日充值总次数
                "sum( case when DATEDIFF( modify_time,NOW())=-1  then 1 else 0 end) as totalRechargeNumYesterday,",  //昨日充值总次数
                "count(1) AS totalNum ", //总次数
                /// 条件 统计 已到账的金额
                " where recharge_status = '3' " +
                " and EXISTS (SELECT id FROM t_dzuser_user WHERE id = t_dzuser_rechargehistory.uid " +
                " and pinvitation_code like (SELECT CONCAt('%[',invitation_code,']%') FROM t_dzuser_user WHERE account = '" + phone + "' ) " +
                " and levels > (SELECT levels FROM t_dzuser_user WHERE account = '"+phone+"' )  "
        );
        Map mapBySql = teamVIPActivationTotalRevenueRepository.getMapBySql(sql);

        String sql1 = String.format(" select %s %s from  ( %s ) as dat ",
                // 返回值
                "sum( case when DATEDIFF( dat.modify_time,NOW())=0  then 1 else 0 end) as firstTotalNumWithToday,",  //今日首充次数
                "sum( case when DATEDIFF( dat.modify_time,NOW())=-1  then 1 else 0 end) as firstTheTotalNumWithYesterday ",  //昨日首充次数
                /// 虚拟表
                " select " +
                "min(modify_time) as modify_time " +
                /// 条件 统计 已到账
                "from t_dzuser_rechargehistory where recharge_status = '3' GROUP BY uid  " +
                        " and EXISTS (SELECT id FROM t_dzuser_user WHERE id = t_dzuser_rechargehistory.uid " +
                        " and pinvitation_code like (SELECT CONCAt('%[',invitation_code,']%') FROM t_dzuser_user WHERE account = '" + phone + "' ) " +
                        " and levels > (SELECT levels FROM t_dzuser_user WHERE account = '"+phone+"' ) )  "
        );
        Map mapBySql1 = teamVIPActivationTotalRevenueRepository.getMapBySql(sql1);

        mapBySql.putAll(mapBySql1);

        return BeanUtil.mapToBean(mapBySql, new TeamStatistics());
    }


    //  今日提现总金额 / 昨日提现总金额 / 总提现 /

    /**
     * 团队统计数据
     *
     * @return
     */
    public TeamStatistics teamStatisticsFour(String phone) {

        String sql = String.format(" select %s %s %s %s from  t_dzuser_withdrawals %s )",
                // 返回值
                "modify_time,",
                "sum( case when DATEDIFF( modify_time,NOW())=0  then money else 0 end) as totalAmountWithdrawnToday,",  //今日提现总金额
                "sum( case when DATEDIFF( modify_time,NOW())=-1  then money else 0 end) as theTotalAmountWithdrawnYesterday,",  //昨日提现总金额
                "sum( money) AS totalWithdrawal", //总提现

                /// 条件
                " where (recharge_status = 'suc' or recharge_status = 'sysok') " +
                " and EXISTS (SELECT id FROM t_dzuser_user WHERE id = t_dzuser_withdrawals.uid " +
                " and pinvitation_code like (SELECT CONCAt('%[',invitation_code,']%') FROM t_dzuser_user WHERE account = '" + phone + "' ) " +
                " and levels > (SELECT levels FROM t_dzuser_user WHERE account = '"+phone+"' )  "
        );
        Map mapBySql = teamVIPActivationTotalRevenueRepository.getMapBySql(sql);
        return BeanUtil.mapToBean(mapBySql, new TeamStatistics());
    }


    public UserInformation getUserInformation(String phone) {
        String sql = String.format(" select %s %s %s %s %s %s %s %s  from   t_dzuser_user as a where account = '" + phone + "'",
                "account,", //账号
                "registration_time as registrationTime,", //注册时间
                "vip_type as vipLevel,", //VIP级别
                "(  select  IFNULL(create_time,null)  from  t_sys_login_log  where logname='API登录日志' and userid = a.id  ORDER BY id DESC limit 0,1  ) as lastLoginTime,", //最后登录时间
                "(  select  IFNULL(review_time,null)  from  t_dztask_taskorder  where  uid = a.id  ORDER BY id DESC limit 0,1  ) as lastMissionTime,", //最后任务时间
                "(  SELECT IFNULL(user_balance,0)   from  t_dzuser_balance  where  uid = a.id  ORDER BY id DESC limit 0,1  ) as totalBalance,", //总余额
                "(  SELECT sum(money)   from  t_dzuser_rechargehistory  where recharge_status = '3' and uid = a.id  ORDER BY id DESC limit 0,1  ) as totalRecharge,", //充值已完成总额
                "(  SELECT sum(money) from t_dzuser_withdrawals where (recharge_status = 'suc' or recharge_status = 'sysok') and  uid = a.id  ORDER BY id DESC limit 0,1  ) as totalWithdrawal"  //提现一万哼总额
        );
        Map mapBySql = teamVIPActivationTotalRevenueRepository.getMapBySql(sql);

        UserInformation userInformation = BeanUtil.mapToBean(mapBySql, new UserInformation());
        userInformation.setVipLevel(ConstantFactory.me().getDictsByName("ViP类型", userInformation.getVipLevel()));
        return userInformation;
    }

    /**
     * vip 等级 对用用户数量
     *
     * @param phone
     * @return
     */
    public NumberOfVipLevels numberOfVipLevels(String phone) {
        String sql = String.format("select %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s from  t_dzuser_user %s ",
                "sum( case when vip_type='v1' then 1 else 0 end) as v1num,",
                "sum( case when vip_type='v2' then 1 else 0 end) as v2num,",
                "sum( case when vip_type='v3' then 1 else 0 end) as v3num,",
                "sum( case when vip_type='v4' then 1 else 0 end) as v4num,",
                "sum( case when vip_type='v5' then 1 else 0 end) as v5num,",
                "sum( case when vip_type='v6' then 1 else 0 end) as v6num,",
                "sum( case when vip_type='v7' then 1 else 0 end) as v7num,",
                "sum( case when vip_type='v8' then 1 else 0 end) as v8num,",
                "sum( case when vip_type='v9' then 1 else 0 end) as v9num,",
                "sum( case when vip_type='v10' then 1 else 0 end) as v10num,",
                "sum( case when vip_type='v11' then 1 else 0 end) as v11num,",
                "sum( case when vip_type='v12' then 1 else 0 end) as v12num,",
                "sum( case when vip_type='v13' then 1 else 0 end) as v13num,",
                "sum( case when vip_type='v14' then 1 else 0 end) as v14num,",
                "sum( case when vip_type='v15' then 1 else 0 end) as v15num,",
                "sum( case when vip_type='v16' then 1 else 0 end) as v16num,",
                "sum( case when vip_type='v17' then 1 else 0 end) as v17num,",
                "sum( case when vip_type='v18' then 1 else 0 end) as v18num,",
                "sum( case when vip_type='v19' then 1 else 0 end) as v19num,",
                "sum( case when vip_type='v20' then 1 else 0 end) as v20num,",
                "sum( case when vip_type='v21' then 1 else 0 end) as v21num,",
                "sum( case when vip_type='v22' then 1 else 0 end) as v22num,",
                "sum( case when vip_type='v23' then 1 else 0 end) as v23num,",
                "sum( case when vip_type='v24' then 1 else 0 end) as v24num,",
                "sum( case when vip_type='v25' then 1 else 0 end) as v25num,",
                "sum( case when vip_type='v26' then 1 else 0 end) as v26num,",
                "sum( case when vip_type='v27' then 1 else 0 end) as v27num,",
                "sum( case when vip_type='v28' then 1 else 0 end) as v28num,",
                "sum( case when vip_type='v29' then 1 else 0 end) as v29num,",
                "sum( case when vip_type='v30' then 1 else 0 end) as v30num",

                "where pinvitation_code like ( " +
                        "select CONCAt('%[',invitation_code,']%') from  t_dzuser_user where  account = '" + phone + "'" +
                ") " +
                "and levels > ( select levels from  t_dzuser_user where  account = '" + phone + "' ) ");
        Map mapBySql = teamVIPActivationTotalRevenueRepository.getMapBySql(sql);
        NumberOfVipLevels numberOfVipLevels = BeanUtil.mapToBean(mapBySql, new NumberOfVipLevels());
        return BeanUtil.mapToBean(mapBySql, new NumberOfVipLevels());
    }


}

