package cn.rh.flash.service.dzuser;


import cn.hutool.core.util.ObjUtil;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.vo.query.SqlSpecification;
import cn.rh.flash.utils.StringUtil;
import cn.rh.flash.utils.factory.Page;

import java.util.List;
import java.util.Map;

/**
 *
 * @author jreak
 */
public class UserInfoServiceSql{

    /**
     * 根据条件查询ids
     * @param testCode
     * @param ucode
     * @return
     */
    public  static  String getIdSql(String testCode,String ucode){
        if (!"admin".equals(ucode)){
            return  " SELECT t_dzuser_user.* FROM t_dzuser_user "+
                    " WHERE user_type = '1'  "+
                    " and fidw is null  "+
                    " and source_invitation_code != '"+testCode+"'  "+
                    " and source_invitation_code = '"+ucode+"'    " +
                    "  GROUP BY  id " ;
        }else {
            return  " SELECT t_dzuser_user.* FROM t_dzuser_user "+
                    " WHERE user_type = '1'  "+
                    " and fidw is null  "+
                    " and source_invitation_code != '"+testCode+"'  "+
                    "  GROUP BY  id " ;
        }
    }

    /**
     * 用户统计数据
     * @return
     */
    public static String sqlMap(String testCode) {
        return String.format(
                " select %s,%s,%s,%s from t_dzuser_user where 1 = 1 and user_type = '1' and source_invitation_code !='"+testCode+"'",
                "sum( case when DATEDIFF( registration_time,NOW())=0  then 1 else 0 end) as addedToday",
                "sum( case when DATEDIFF( registration_time,NOW())=-1 then 1 else 0 end) as addedYesterday",
                "sum( case when dzstatus = 2   then 1 else 0 end) as freezeAccount",
                "sum( case when dzstatus = 1  then 1 else 0 end) as normalAccount"
        );
    }

    /**
     * 今日VIP返佣
     * @param userId
     * @return
     */
    public static String sqlVipReward( Long userId ) {
        return String.format(
                "select %s from t_dzvip_viprebaterecord where uid = %s and DATEDIFF(create_time,NOW())=0",
                "IFNULL(sum(money),0)",
                userId
        );
    }


    /**
     * 今日直冲收入
     * @param userId
     * @return
     */
    public static String sqlBonusReward( Long userId ) {
        return String.format("select %s from t_dzuser_compensation where uid = %s and DATEDIFF(create_time,NOW())=0",
                "IFNULL(sum(if(addition_and_subtraction = 1,money,money*-1)),0)",
                userId
        );
    }

    /**
     * 今日充电宝返佣
     * @param userId
     * @return
     */
    public static String sqlTotalbonuspb( Long userId ) {
        return String.format("select %s from t_dzgoods_recordpb where uid = %s and DATEDIFF(create_time,NOW())=0",
                "IFNULL(sum(money),0)",
                userId
        );
    }

    /**
     * 今日收入查询结果  当日利润 = 当日收益+下级晋级返佣+下级收益返佣
     * @param userId
     * @return
     */
/*    public static String sqlTodayRevenueVo( Long userId ) {
        //下级晋级返佣
        String vipRewardSql = sqlVipReward(userId);
        //直冲
        String bonusRewardSql = sqlBonusReward(userId);
        return String.format(
                "select %s, %s from dual",
                String.format(" ( %s ) as vipReward ",vipRewardSql),
                String.format(" ( %s ) as bonusReward ",bonusRewardSql)
        );
    }*/

    /**
     * 团队充值收入
     * @param getSourceInvitationCode
     * @param getInvitationCode
     * @return
     */
    /*public static String sqlTeamRechargeRevenue(String getSourceInvitationCode, String getInvitationCode ){
        return String.format(
                "select %s from t_dzuser_totalrecharge where source_invitation_code = '%s' and  uid in ( select id from  t_dzuser_user where superior_invitation_code = '%s' ) ",
                "sum(total_recharge_amount) as teamReport",
                getSourceInvitationCode,
                getInvitationCode
        );
    }*/




    /**
     * 获取余额相关数据  统计数据
     * @param one
     * @return
     */
    public static String sqlBalanceInformation(UserInfo one) {

        /*
            直冲总金额 totalBonusIncome、
            充电宝返佣金额 totalBonuspb、
            下级充电宝返佣金额 dwTotalBonuspb、
            购买vip返佣金额 dwPayVip、
            当日自己收益 vipToDay brToDay cdbToDay、
            下级注册奖励 reg、
            总充值金额 rechargeAmount、
            总提现金额 withdrawalAmount、
            购买vip支出 payVip

         */

        return String.format(
                " select %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s from t_dzuser_user as u where u.id="+one.getId()+" ",
                " IFNULL( ( select total_bonus_income from t_dzuser_totalbonus where uid ="+one.getId()+"  ),0) as totalBonusIncome", //直冲总金额
                " IFNULL(( select total_bonus_income from t_dzgoods_totalbonuspb where uid ="+one.getId()+"  ),0) as totalBonuspb",  //充电宝返佣金额
//                " IFNULL(( select sum(total_bonus_income) from t_dzgoods_totalbonuspb where uid in ("+getDw3( one.getInvitationCode(),one.getLevels() )+")  ),0) as dwTotalBonuspb", // 下3级充电宝返佣金额
                " IFNULL(( select sum(money) from t_dzgoods_recordpb where uid = '"+one.getId()+"' and source_user_account in ("+getAccount3( one.getInvitationCode(),one.getLevels() )+")  ),0) as dwTotalBonuspb", // 下3级充电宝返佣金额
//                " IFNULL(( select sum(team_vip_opening_total_rebate) from t_dzvip_teamvip where uid ="+one.getId()+" ),0) as dwPayVip", // 下级购买vip返佣金额
                " IFNULL(( select sum(money) from t_dzvip_viprebaterecord where uid = '"+one.getId()+"' and source_user_account in ("+getAccount3( one.getInvitationCode(),one.getLevels() )+") ),0) as dwPayVip", // 下3级购买vip返佣金额
//                " IFNULL(( select sum(team_vip_opening_total_rebate) from t_dzvip_teamvip where uid in ("+getDw3( one.getInvitationCode(),one.getLevels() )+")  ),0) as dwPayVip", // 下3级购买vip返佣金额
                " IFNULL(( select sum(payment_amount) from t_dzvip_vippurchase where whether_to_pay=2 and uid ="+one.getId()+" and payment_method =1 ),0) as payVip", // 购买vip支出
                " IFNULL(( select total_recharge_amount from t_dzuser_totalrecharge where uid ="+one.getId()+"  ),0) as rechargeAmount", //总充值金额
                " IFNULL(( select total_withdrawal_amount from t_dzuser_totalwithdrawal where uid ="+one.getId()+"  ),0) as withdrawalAmount", //总提现金额
                " IFNULL(( select sum(money) from t_dzuser_transaction where  uid ="+one.getId()+" and  transaction_type=11 and addition_and_subtraction=1 AND DATEDIFF( create_time, NOW())= 0 ),0) as reg",  // 当天下级注册奖励
                " IFNULL(( select sum(money) from t_dzuser_transaction where  uid ="+one.getId()+" and  transaction_type=11 and addition_and_subtraction=1  ),0) as allReg",  // 下级注册奖励
                " IFNULL(("+sqlVipFriends( one.getInvitationCode(),one.getLevels() )+"),0) as teams",  // 团队人数
                " IFNULL(("+sqlVipReward( one.getId() )+"),0) as vipToDay",  // 当日自己收益.vip
                " IFNULL(("+sqlBonusReward( one.getId() )+"),0) as brToDay", // 当日自己收益.直冲扣
                " IFNULL(("+sqlTotalbonuspb( one.getId() )+"),0) as cdbToDay", // 当日自己收益.充电宝
                " IFNULL(( select user_balance as userBalance from t_dzuser_balance where uid ="+one.getId()+"),0) as userBalance"// 用户可用余额
//                " IFNULL(( select sum(if(addition_and_subtraction = 1,money,money*-1)) from t_dzuser_transaction " +
//                        " where transaction_type NOT IN ('1','2') and uid ="+one.getId()+"),0) as transactionMoney"// 除充值提现金额
        );
    }
    /**
     * 获取余额相关数据  统计数据
     * @param one
     * @return
     */
    public static String sqlBalanceInformationV2(UserInfo one) {

        /*
            直冲总金额 totalBonusIncome、
            充电宝返佣金额 totalBonuspb、
            下级充电宝返佣金额 dwTotalBonuspb、
            购买vip返佣金额 dwPayVip、
            当日自己收益 vipToDay brToDay cdbToDay、
            下级注册奖励 reg、
            总充值金额 rechargeAmount、
            总提现金额 withdrawalAmount、
            购买vip支出 payVip
            中奖金额
         */

        return String.format(
                " select %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s from t_dzuser_user as u where u.id="+one.getId()+" ",
                " IFNULL( ( select total_bonus_income from t_dzuser_totalbonus where uid ="+one.getId()+"  ),0) as totalBonusIncome", //直冲总金额
                " IFNULL(( select total_bonus_income from t_dzgoods_totalbonuspb where uid ="+one.getId()+"  ),0) as totalBonuspb",  //充电宝返佣金额
                " IFNULL(( select sum(money) from t_dzgoods_recordpb where uid = '"+one.getId()+"' and source_user_account in ("+getAccount3( one.getInvitationCode(),one.getLevels() )+")  ),0) as dwTotalBonuspb", // 下3级充电宝返佣金额
                " IFNULL(( select sum(money) from t_dzvip_viprebaterecord where uid = '"+one.getId()+"' and source_user_account in ("+getAccount3( one.getInvitationCode(),one.getLevels() )+") ),0) as dwPayVip", // 下3级购买vip返佣金额
                " IFNULL(( select sum(payment_amount) from t_dzvip_vippurchase where whether_to_pay=2 and uid ="+one.getId()+" and payment_method =1 ),0) as payVip", // 购买vip支出
                " IFNULL(( select total_recharge_amount from t_dzuser_totalrecharge where uid ="+one.getId()+"  ),0) as rechargeAmount", //总充值金额
                " IFNULL(( select total_withdrawal_amount from t_dzuser_totalwithdrawal where uid ="+one.getId()+"  ),0) as withdrawalAmount", //总提现金额
                " IFNULL(( select sum(money) from t_dzuser_transaction where  uid ="+one.getId()+" and  transaction_type=11 and addition_and_subtraction=1 AND DATEDIFF( create_time, NOW())= 0 ),0) as reg",  // 当天下级注册奖励
                " IFNULL(( select sum(money) from t_dzuser_transaction where  uid ="+one.getId()+" and  transaction_type=11 and addition_and_subtraction=1  ),0) as allReg",  // 下级注册奖励
                " ("+sqlVipFriendsV2( one.getInvitationCode(),one.getLevels() )+") as teams",  // 团队人数
                " IFNULL(("+sqlVipReward( one.getId() )+"),0) as vipToDay",  // 当日自己收益.vip
                " IFNULL(("+sqlBonusReward( one.getId() )+"),0) as brToDay", // 当日自己收益.直冲扣
                " IFNULL(("+sqlTotalbonuspb( one.getId() )+"),0) as cdbToDay", // 当日自己收益.充电宝
                " IFNULL(( select user_balance as userBalance from t_dzuser_balance where uid ="+one.getId()+"),0) as userBalance",// 用户可用余额
                " IFNULL(( select sum(amount) as winningAmount from t_dzprize_winningrecord where uid ="+one.getId()+"),0) as winningAmount"// 中奖金额
        );
    }

    /* 下三级用户id集合 */
    private static String getDw3(String invitationCode,Integer levels) {
        return " select id from t_dzuser_user where pinvitation_code like '%["+ invitationCode +"]%' and '"+levels+"'<levels and levels<='"+(levels+3)+"' ";
    }

    /* 下三级用户account集合 */
    private static String getAccount3(String invitationCode,Integer levels) {
        return " select account from t_dzuser_user where vip_type!='v0' and vip_type!='v1' and pinvitation_code like '%["+ invitationCode +"]%' and '"+levels+"'<levels and levels<='"+(levels+3)+"' ";
    }


    /**
     * 团队会员人数  往下  3级
     */
    public static String sqlVipFriends( String invitationCode,Integer levels ){
        return String.format(
                "select %s from t_dzuser_user where dzstatus !='3' and "+levels+"<levels and levels<="+( levels+3 )+"  and pinvitation_code like %s  ",
                "count(1) as vipFriends",
                "'%["+invitationCode +"]%'"
        );
    }
    /**
     * 团队会员人数  往下  3级 去除 V0 V1
     */
    public static String sqlVipFriendsV2( String invitationCode,Integer levels ){
        return String.format(
                "select %s from t_dzuser_user where dzstatus !='3' and "+levels+"<levels and levels<="+( levels+3 )+" and vip_type!='v0' and vip_type!='v1' and pinvitation_code like %s  ",
                "count(1) as vipFriends",
                "'%["+invitationCode +"]%'"
        );
    }



    private static String userInfosql = String.format( "select %s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s from t_dzuser_user rep",
                "registration_time as registrationTime",
                        "register_ip as registerIp",
                        "vip_expire_date as vipExpireDate" ,
                        "vip_type as vipType" ,
                        "pinvitation_code as pinvitationCode" ,
                        "levels as levels" ,
                        "user_type as userType",
                        "authenticator_password as authenticatorPassword" ,
                        "payment_password as paymentPassword",
                        "password",
                        "dzstatus" ,
                        "account" ,
                        "head_portrait_key as headPortraitKey" ,
                        "name" ,
                        "invitation_code as invitationCode" ,
                        "superior_invitation_code as superiorInvitationCode",
                        "country_code_number as countryCodeNumber" ,
                        "source_invitation_code as sourceInvitationCode" ,
                        "idw",
                        "id",
                        "create_time as createTime",
                        "limit_buy_cdb as limitBuyCdb",
                        "limit_drawing as limitDrawing",
                        "limit_profit as limitProfit",
                        "limit_code as limitCode",
                        "real_name as realName",
                        "last_ip as lastIp",
                        "register_ip_city as registerIpCity",
                        "last_ip_city as lastIpCity",
                        "last_time as lastTime",
                        "remark as remark"
    );
    /**
     * 获取用户信息
     * @param uid
     * @return
     */
    public static String sqlUserInfo( Long uid ){
        return userInfosql.replace( "rep","where id ="+uid );
    }

    public static String sqlUserInfo( String idw ){
        return userInfosql.replace( "rep","where ids ="+idw );
    }

    public static String sqlUserInfoByInvitationCode( String invitation_code ){
        return userInfosql.replace( "rep","where invitation_code ='"+invitation_code+"'" );
    }

    public static String sqlUserInfoByCodeAndAccount(String countryCode, String account) {
        return userInfosql.replace( "rep","where country_code_number ='"+countryCode+"' and account='"+account+"' " );
    }


    /**
     *
     * @param id
     * @param accounts
     * @return
     */
    public static String sqlTeamProfit(Long id, List<String> accounts){

        // 团队开通vip 总收益
        String vipSql = String.format(
                "select IFNULL(sum(money),0) as money from t_dzvip_viprebaterecord where uid = %s and source_user_account in ( %s )",
                id,dealCode(accounts)
        );
        String cdbSql = String.format(
                "select IFNULL(sum(money),0) as money from t_dzgoods_recordpb where uid = %s and source_user_account in ( %s )",
                id,dealCode(accounts)
        );
        return String.format(
                "select %s,%s from dual",
                String.format(" ( %s ) as vmoney ",vipSql ),
                String.format(" ( %s ) as tmoney ",cdbSql )
        );
    }
    /**
     * accounts  给  id  带来的 收益
     * @param id
     * @param accounts
     * @return
     */
    public static String sqlTeamProfit2(Long id, String accounts){

        // 团队开通vip 总收益
        String vipSql = String.format("select IFNULL(sum(money),0) as money from t_dzvip_viprebaterecord where uid = %s and source_user_account = '%s'",
                id, accounts
        );
        // 冲充电宝 收益
        String cdbSql = String.format("select IFNULL(sum(total_bonus_income),0) as money from t_dzgoods_totalbonuspb where uid = %s and source_user_account = '%s'",
                id, accounts
        );
        return String.format(
                "select %s,%s from dual",
                String.format(" ( %s ) as vmoney ",vipSql ),
                String.format(" ( %s ) as tmoney ",cdbSql )
        );
    }


    /**
     *
     * @param code
     * @return
     */
    public static String dealCode(List<String> code){
        StringBuilder sb = new StringBuilder();
        code.forEach(v-> sb.append("'"+v+"',"));
        sb.deleteCharAt(sb.lastIndexOf(","));
        return sb.toString();
    }





    /**
     * 下级用户集合
     * @param invitationCode
     * @return
     */
    public static String sqlUserInfoSubVo(String invitationCode,Integer levels) {

        return String.format(
                "select id,invitation_code as invitationCode,account, ( select l3_task_rebate from t_dzvip_vipmessage  where vip_type = a1.vip_type  ) as fee from t_dzuser_user as a1 where invitation_code in %s ",
                "( select invitation_code from t_dzuser_user where vip_type!='v0' and vip_type!='v1' and pinvitation_code like '%[" + invitationCode + "]%' and levels = "+levels+"  )"
        );
    }

    /**
     * invitationCode 的 直推 用户信息  【 今日新增人数，团队人数，头像,注册是时间,vip类型 】
     * @param invitationCode
     * @return
     */
    public static String sqlGetTeamOneVo(String invitationCode, Integer levels) {
        return String.format(
                "select %s, %s, %s, invitation_code as invitationCode, head_portrait_key as headPortraitKey,account, vip_type as vipType,registration_time as registrationTime , id, levels from t_dzuser_user as a1 where dzstatus !='3' and superior_invitation_code = '%s' and levels > '%s'  ",
                "( select count(a2.id) from t_dzuser_user as a2 where DATEDIFF( registration_time,NOW() )=0 and superior_invitation_code = a1.invitation_code  and levels > '"+levels+"'  ) as dayPeopleNumber",
                "( select count(a2.id) from t_dzuser_user as a2 where superior_invitation_code = a1.invitation_code and levels > '"+levels+"' ) as teamaSize",
                "( select vip_img from t_dzvip_vipmessage  where vip_type = a1.vip_type  ) as vipImg ",

                invitationCode,levels
        );
    }

    /**
     * invitationCode 模糊搜索用户账号，通过账号查询数据
     * @param invitationCode
     * @return
     */
    public static String sqlGetTeamVo(String invitationCode, Integer levels,String accountFragment) {
        String changeString=null;
        if (accountFragment!=null&&accountFragment!=""){
            changeString=" and a1.account like '%"+accountFragment+"%' ";
        }else{
            changeString= " ";
        }

        return String.format(
                "select %s, %s, %s, invitation_code as invitationCode, head_portrait_key as headPortraitKey,account, vip_type as vipType,registration_time as registrationTime , id, levels from t_dzuser_user as a1 where dzstatus !='3' and superior_invitation_code = '%s' and levels > '%s' and a1.vip_type != 'v1' and a1.vip_type != 'v0'  %s ",
                "( select count(a2.id) from t_dzuser_user as a2 where DATEDIFF( registration_time,NOW() )=0 and superior_invitation_code = a1.invitation_code  and levels > '"+levels+"'  ) as dayPeopleNumber",
                "( select count(a2.id) from t_dzuser_user as a2 where superior_invitation_code = a1.invitation_code and levels > "+levels+" and a2.vip_type != 'v1' and a2.vip_type != 'v0' ) as teamaSize",
                "( select vip_img from t_dzvip_vipmessage  where vip_type = a1.vip_type  ) as vipImg ",

                invitationCode,levels,changeString
        );
    }

    /**
     * 最新查询团队信息数据接口
     * @param invitationCode
     * @param selfLevels
     * @param accountFragment
     * @param levels
     * @return
     */
    public static String sqlGetTeam(String invitationCode, Integer selfLevels,String accountFragment,Integer levels) {
        String changeString=null;
        if (accountFragment!=null&&accountFragment!=""){
            changeString=" and a1.account like '%"+accountFragment+"%' ";
        }else{
            changeString= " ";
        }
        int level1 = levels + selfLevels;
        int level2 = levels + selfLevels+1;

        String sql="SELECT (SELECT count(a2.id) FROM t_dzuser_user AS a2 WHERE DATEDIFF(registration_time,NOW())=0 AND superior_invitation_code=a1.invitation_code AND levels="+level1+") AS dayPeopleNumber,(\n" +
                "SELECT count(a2.id) FROM t_dzuser_user AS a2 WHERE a2.superior_invitation_code=a1.invitation_code AND levels="+level2+" AND a2.vip_type !='v1' AND a2.vip_type !='v0') AS teamaSize,(\n" +
                "SELECT vip_img FROM t_dzvip_vipmessage WHERE vip_type=a1.vip_type) AS vipImg,invitation_code AS invitationCode,head_portrait_key AS headPortraitKey,account,vip_type AS vipType,registration_time AS registrationTime,id,levels FROM t_dzuser_user AS a1 WHERE dzstatus !='3' AND pinvitation_code LIKE '%"+invitationCode+"%' AND levels="+level1+" AND a1.vip_type !='v1' AND a1.vip_type !='v0'  "+changeString;
        return sql;
    }

    /**
     * invitationCode 的 直推 用户信息  【 头像，账号，vip类型,注册是时间 】
     * @param invitationCode
     * @return
     */
    public static String sqlGetTeamTwoVo(String invitationCode) {
        return  String.format(
                "select  %s, head_portrait_key as headPortraitKey,account, vip_type as vipType, registration_time as registrationTime, id from t_dzuser_user as a1 where dzstatus !='3' and superior_invitation_code = '%s'",
                "( select vip_img from t_dzvip_vipmessage  where vip_type = a1.vip_type  ) as vipImg ",
                invitationCode
        );

    }

    /**
     * invitationCode 的 直推 用户信息  【 头像，账号，vip类型,注册是时间 】
     * @param invitationCode
     * @return
     */
    public static String sqlGetTeamThreeVo(String invitationCode,String msg) {
        String changeString=null;
        if (msg!=null&&msg!=""){
            changeString=" and a1.account like '%"+msg+"%' ";
        }else{
            changeString= " ";
        }
        return  String.format(
                "select  %s, head_portrait_key as headPortraitKey,account, vip_type as vipType, registration_time as registrationTime, id from t_dzuser_user as a1 where dzstatus !='3' and superior_invitation_code = '%s' and a1.vip_type != 'v1' and a1.vip_type != 'v0' %s ",
                "( select vip_img from t_dzvip_vipmessage  where vip_type = a1.vip_type  ) as vipImg ",
                invitationCode,changeString
        );

    }


    /**
     * 充电宝返佣
     * 获取 自己的  上级 上上级   上上上级  信息【l1.id,l1.account,l1.fee,  l2.id,l2.account,l2.fee  ,l3.id,l3.account,l3.fee】
     * @param superiorInvitationCode
     * @return
     */
    public static String findUpUpUpCdb( String superiorInvitationCode) {

        /**
         * uid  account  fee
         */

//        String l1 = String.format(
//                "select source_invitation_code, id ,account,vip_type, ( select l1_task_rebate from t_dzvip_vipmessage  where vip_type = a1.vip_type ) as fee from t_dzuser_user as a1 where limit_profit =2  and invitation_code = '%s' ",
//                superiorInvitationCode
//        );

        String l1 = String.format(
                "select source_invitation_code, id ,account,vip_type from t_dzuser_user as a1 where limit_profit =2  and invitation_code = '%s' ",
                superiorInvitationCode
        );

        // 获取上级 的 上级  邀请码
        String sqlk = "( select superior_invitation_code from t_dzuser_user where invitation_code = '"+superiorInvitationCode+"')";


//        String l2 = String.format(
//                "select source_invitation_code, id ,account,vip_type, ( select l2_task_rebate from t_dzvip_vipmessage  where vip_type = a1.vip_type  ) as fee from t_dzuser_user as a1 where limit_profit =2  and invitation_code = %s ",
//                sqlk
//        );

        String l2 = String.format(
                "select source_invitation_code, id ,account,vip_type from t_dzuser_user as a1 where limit_profit =2  and invitation_code = %s ",
                sqlk
        );

//        String l3 = String.format(
//                "select source_invitation_code, id ,account,vip_type, ( select l3_task_rebate from t_dzvip_vipmessage  where vip_type = a1.vip_type  ) as fee from t_dzuser_user as a1 where  limit_profit =2  and invitation_code = %s ",
//                "( select superior_invitation_code from t_dzuser_user where invitation_code = "+sqlk+" )"
//        );

        String l3 = String.format(
                "select source_invitation_code, id ,account,vip_type from t_dzuser_user as a1 where  limit_profit =2  and invitation_code = %s ",
                "( select superior_invitation_code from t_dzuser_user where invitation_code = "+sqlk+" )"
        );

        return String.format(
                " select " +
                        "l1.id as l1id,l1.account as l1account,l1.vip_type as l1vipType, " +
                        "l2.id as l2id,l2.account as l2account,l2.vip_type as l2vipType," +
                        "l3.id as l3id,l3.account as l3account,l3.vip_type as l3vipType " +
                        " from  (%s) as l1 " +
                        " left join (%s) as l2 on l1.source_invitation_code = l2.source_invitation_code " +
                        " left join (%s) as l3 on l1.source_invitation_code = l3.source_invitation_code",
                l1,l2,l3
        );

    }

    /**
     * 注册返佣
     * 获取 自己的  上级 上上级   上上上级  信息【l1.id,l1.account,l1.fee,  l2.id,l2.account,l2.fee  ,l3.id,l3.account,l3.fee】
     * @param uid
     * @return
     */
    public static String findUpUpUpReg(Long uid, String invitationCode, Integer levels ) {

        /**
         * uid  account  fee
         */

        String l1 = String.format(
                "select source_invitation_code, id ,account, ( select l1_registration_rebate from t_dzvip_vipmessage  where vip_type = a1.vip_type  ) as fee from t_dzuser_user as a1 where id = '%s'",
                uid
        );
        String l2 = String.format(
                "select source_invitation_code, id ,account, ( select l2_registration_rebate from t_dzvip_vipmessage  where vip_type = a1.vip_type  ) as fee from t_dzuser_user as a1 where id = '%s'",
                "(select superior_invitation_code from t_dzuser_user where id = "+ uid +")"
        );
        String l3 = String.format(
                "select source_invitation_code, id ,account, ( select l3_registration_rebate from t_dzvip_vipmessage  where vip_type = a1.vip_type  ) as fee from t_dzuser_user as a1 where invitation_code = '%s'",
                "( select count(id) from t_dzuser_user where pinvitation_code like '%[" + invitationCode + "]%' and levels = "+(levels-2)+"  )"
        );

        String l4 = String.format(
                "select source_invitation_code, id ,account, ( select l3_task_rebate from t_dzvip_vipmessage  where vip_type = a1.vip_type  ) as fee from t_dzuser_user as a1 where invitation_code = %s ",
                "( select invitation_code from t_dzuser_user where pinvitation_code like '%[" + invitationCode + "]%' and levels = "+(levels-3)+"  )"
        );


        return String.format(
                " select  %s  from  (%s) as l1 left join (%s) as l2 on l1.source_invitation_code = l2.source_invitation_code left join (%s) as l3 on l1.source_invitation_code = l3.source_invitation_code left join (%s) as l4 on l1.source_invitation_code = l4.source_invitation_code ",
                " l1.id as l1id,l1.account as l1account,l1.fee as l1fee,  l2.id as l2id,l2.account as l2account,l2.fee as l2fee  ,l3.id as l3id,l3.account as l3account,l3.fee as l3fee ,l4.id as l4id,l4.account as l4account,l4.fee as l4fee",
                l1,l2,l3,l4
        );

    }

    /**
     * 修改邀请码
     * @param invitationCode
     * @return
     */
//    public static String updateInvitation(String invitationCode,String invitationCodeNew) {
//
//        return " UPDATE t_dzuser_user SET pinvitation_code= replace(pinvitation_code,'"+invitationCode+"','"+invitationCodeNew+"') WHERE  pinvitation_code like '%"+invitationCode+"%' ";
//    }

    /**
     * PC段列表查询
     * @param page
     * @return
     */
    public static String findUserInfoPage(Page<UserInfo> page,String sourceInvitationCode) {
        String sqlUser;
        if (StringUtil.isNotEmpty(sourceInvitationCode)){
            String supUser = " select * from t_dzuser_user where source_invitation_code = ( select ucode from t_sys_user where account = '"+sourceInvitationCode+"') ";
            sqlUser = " select user.* from ("+supUser+") as user " +
                    " left join t_dzuser_balance as ub1 on ub1.uid = user.id  ";
        }else {
            sqlUser = " select user.* from t_dzuser_user as user " +
                    " left join t_dzuser_balance as ub1 on ub1.uid = user.id  ";
        }

        sqlUser = SqlSpecification.toAddSql(sqlUser, page.getFilters());
        sqlUser = SqlSpecification.toSqlLimit(page.getCurrent(), page.getSize(), sqlUser, "user.id");
        String sqlLeft = getUserInfoSql(sqlUser);
        return sqlLeft;
    }

    public static String findUserInfoPage(Page<UserInfo> page,String sourceInvitationCode,String orderSql) {
        String sqlUser;
        if (StringUtil.isNotEmpty(sourceInvitationCode)){
            String supUser = " select * from t_dzuser_user where source_invitation_code = ( select ucode from t_sys_user where account = '"+sourceInvitationCode+"') ";
            sqlUser = " select user.* from ("+supUser+") as user " +
                    " left join t_dzuser_balance as ub1 on ub1.uid = user.id  ";
        }else {
            sqlUser = " select user.* from t_dzuser_user as user " +
                    " left join t_dzuser_balance as ub1 on ub1.uid = user.id  ";
        }

        sqlUser = SqlSpecification.toAddSql(sqlUser, page.getFilters());
        sqlUser = SqlSpecification.toSqlLimit(page.getCurrent(), page.getSize(), sqlUser, "user.id");
        String sqlLeft = getUserInfoSql(sqlUser,orderSql);
        return sqlLeft;
    }

    /**
     * 查询导出信息，兼具完整和最新的查询条件
     * @param page
     * @return
     */
    public static String findUserInfoExportPage(Page<UserInfo> page,String sourceInvitationCode,String orderSql) {
        String sqlUser;
        if (StringUtil.isNotEmpty(sourceInvitationCode)){
            String supUser = " select * from t_dzuser_user where source_invitation_code = ( select ucode from t_sys_user where account = '"+sourceInvitationCode+"') ";
            sqlUser = " select user.* from ("+supUser+") as user " +
                    " left join t_dzuser_balance as ub1 on ub1.uid = user.id  ";
        }else {
            sqlUser = " select user.* from t_dzuser_user as user " +
                    " left join t_dzuser_balance as ub1 on ub1.uid = user.id  ";
        }

        sqlUser = SqlSpecification.newToAddSql(sqlUser, page.getFilters());
        sqlUser = SqlSpecification.toSqlLimit(page.getCurrent(), page.getSize(), sqlUser, "user.id");
        String sqlLeft = getUserInfoSql(sqlUser,orderSql);
        return sqlLeft;
    }

    /**
     * 查询部分用户信息，提高查询效率
     * @param page
     * @return
     */
    public static String findPartUserInfoPage(Page<UserInfo> page,String sourceInvitationCode,String orderSql) {
        String sqlUser;
        if (StringUtil.isNotEmpty(sourceInvitationCode)){
            String supUser = " select * from t_dzuser_user where source_invitation_code = ( select ucode from t_sys_user where account = '"+sourceInvitationCode+"') ";
            sqlUser = " select user.* ,  ub1.user_balance as userBalanceLeft , ub1.wallet_address as walletAddress from ("+supUser+") as user " +
                    " left join t_dzuser_balance as ub1 on ub1.uid = user.id  ";
        }else {
            sqlUser = " select user.* , ub1.user_balance as userBalanceLeft , ub1.wallet_address as walletAddress from t_dzuser_user as user " +
                    " left join t_dzuser_balance as ub1 on ub1.uid = user.id  ";
        }
        sqlUser = SqlSpecification.newToAddSql(sqlUser, page.getFilters());
        sqlUser = SqlSpecification.toSqlLimit(page.getCurrent(), page.getSize(), sqlUser, "user.id");
        String sqlLeft = getPartUserInfoSql(sqlUser,orderSql);
        return sqlLeft;
    }
    /**
     * 查询部分用户信息，提高查询效率
     * @param page
     * @return
     */
    public static String findPartUserInfoPage(Page<UserInfo> page, Map<String,Object> where,boolean subordinate,boolean money) {
        String  leftSql = "(SELECT count(1) FROM t_dzuser_user WHERE  fidw  IS NULL and pinvitation_code  like CONCAT('%[',user.pinvitation_code,']%' and pinvitation_code!=CONCAT('[',user.pinvitation_code,'],'))";
        if (ObjUtil.isNotEmpty(where.get("vipTypes"))){
                leftSql = "(SELECT count(1) FROM t_dzuser_user WHERE  fidw  IS NULL and vip_type in("+where.get("vipTypes").toString()+") and pinvitation_code  like CONCAT('%[',user.pinvitation_code,']%') and pinvitation_code!=CONCAT('[',user.pinvitation_code,'],') ) ";
        }
        String sqlUser;
         sqlUser = " select user.* , " +
                "ub1.user_balance as userBalanceLeft , " +
                "ub1.wallet_address as walletAddress " +
                "from t_dzuser_user as user " +
                " left join t_dzuser_balance as ub1 on ub1.uid = user.id  ";
        if (subordinate){
            sqlUser = " select user.* , " +
                    leftSql +"as numberOfSubordinates,"+
                    "ub1.user_balance as userBalanceLeft , " +
                    "ub1.wallet_address as walletAddress " +
                    "from t_dzuser_user as user " +
                    " left join t_dzuser_balance as ub1 on ub1.uid = user.id  ";
        }
        if (money){
            sqlUser = " select user.* , " +
                    " IFNULL( w.wiMoney, 0 )- IFNULL( r.reMoney, 0 ) as profitMoney, "+
                    "ub1.user_balance as userBalanceLeft , " +
                    "ub1.wallet_address as walletAddress " +
                    "from t_dzuser_user as user " +
                    "left join t_dzuser_balance as ub1 on ub1.uid = user.id "
                    +" left join ( SELECT uid, IFNULL( sum( money ), 0 ) AS reMoney FROM t_dzuser_rechargehistory WHERE recharge_status = '3' GROUP BY uid ) r ON user.id = r.uid "
                    +" left join ( SELECT uid, IFNULL( sum( money ), 0 ) AS wiMoney FROM t_dzuser_withdrawals WHERE recharge_status = 'suc' GROUP BY uid ) w ON user.id = w.uid ";
        }
        sqlUser = SqlSpecification.newToAddSql(sqlUser, page.getFilters());
        sqlUser = SqlSpecification.toSqlLimit(page.getCurrent(), page.getSize(), sqlUser, "user.id");
        String partUserInfoSql = getPartUserInfoSql(sqlUser,subordinate,money);
        System.out.println("=================="+partUserInfoSql+"==================");
        return partUserInfoSql;
    }


    /*
      查询部分用户信息
      zx
       */
    private static String getPartUserInfoSql(String sqlUser,boolean subordinate,boolean money) {
        if (subordinate){
            return "\n" +
                    " SELECT u.walletAddress AS walletAddress,u.userBalanceLeft AS userBalanceLeft,u.register_ip_city AS registerIpCity," +
                    "u.last_ip_city AS lastIpCity,u.real_name AS realName,u.remark AS remark,u.id AS id,u.create_time AS createTime,u.create_by AS createBy," +
                    "u.modify_time AS modifyTime,u.modify_by AS modifyBy,u.limit_buy_cdb AS limitBuyCdb,u.limit_drawing AS limitDrawing," +
                    "u.limit_profit AS limitProfit,u.limit_code AS limitCode,u.idw AS idw,u.source_invitation_code AS sourceInvitationCode," +
                    "u.country_code_number AS countryCodeNumber,u.superior_invitation_code AS superiorInvitationCode,u.invitation_code AS invitationCode," +
                    "u.NAME AS NAME,u.head_portrait_key AS headPortraitKey,u.pinvitation_code AS pinvitationCode,u.levels AS levels,u.account AS account,u.password AS password," +
                    "u.payment_password AS paymentPassword,u.authenticator_password AS authenticatorPassword,u.user_type AS userType,u.vip_type AS vipType,u.vip_expire_date AS vipExpireDate," +
                    "u.register_ip AS registerIp,u.last_ip AS lastIp,u.last_time AS lastTime,u.registration_time AS registrationTime,u.dzstatus AS dzstatus,su.account AS superAccount, "+
                    "u.numberOfSubordinates"+
                    " from ("+sqlUser+") as u " +
                    " LEFT JOIN t_sys_user AS su ON su.ucode = u.source_invitation_code ";
        }
        if (money){
            return "\n" +
                    " SELECT u.walletAddress AS walletAddress,u.userBalanceLeft AS userBalanceLeft,u.register_ip_city AS registerIpCity," +
                    "u.last_ip_city AS lastIpCity,u.remark AS remark,u.id AS id,u.create_time AS createTime,u.create_by AS createBy," +
                    "u.modify_time AS modifyTime,u.modify_by AS modifyBy,u.limit_buy_cdb AS limitBuyCdb,u.limit_drawing AS limitDrawing," +
                    "u.limit_profit AS limitProfit,u.limit_code AS limitCode,u.idw AS idw,u.source_invitation_code AS sourceInvitationCode," +
                    "u.country_code_number AS countryCodeNumber,u.superior_invitation_code AS superiorInvitationCode,u.invitation_code AS invitationCode," +
                    "u.NAME AS NAME,u.head_portrait_key AS headPortraitKey,u.pinvitation_code AS pinvitationCode,u.levels AS levels,u.account AS account,u.password AS password," +
                    "u.payment_password AS paymentPassword,u.authenticator_password AS authenticatorPassword,u.user_type AS userType,u.vip_type AS vipType,u.vip_expire_date AS vipExpireDate," +
                    "u.register_ip AS registerIp,u.last_ip AS lastIp,u.last_time AS lastTime,u.registration_time AS registrationTime,u.dzstatus AS dzstatus,su.account AS superAccount, "+
                    "  u.profitMoney as profitAmount "+
                    " from ("+sqlUser+") as u " +
                    " LEFT JOIN t_sys_user AS su ON su.ucode = u.source_invitation_code ";
        }

        return "\n" +
                " SELECT u.walletAddress AS walletAddress,u.userBalanceLeft AS userBalanceLeft,u.register_ip_city AS registerIpCity," +
                "u.last_ip_city AS lastIpCity,u.remark AS remark,u.id AS id,u.create_time AS createTime,u.create_by AS createBy," +
                "u.modify_time AS modifyTime,u.modify_by AS modifyBy,u.limit_buy_cdb AS limitBuyCdb,u.limit_drawing AS limitDrawing," +
                "u.limit_profit AS limitProfit,u.limit_code AS limitCode,u.idw AS idw,u.source_invitation_code AS sourceInvitationCode," +
                "u.country_code_number AS countryCodeNumber,u.superior_invitation_code AS superiorInvitationCode,u.invitation_code AS invitationCode," +
                "u.NAME AS NAME,u.head_portrait_key AS headPortraitKey,u.pinvitation_code AS pinvitationCode,u.levels AS levels,u.account AS account,u.password AS password," +
                "u.payment_password AS paymentPassword,u.authenticator_password AS authenticatorPassword,u.user_type AS userType,u.vip_type AS vipType,u.vip_expire_date AS vipExpireDate," +
                "u.register_ip AS registerIp,u.last_ip AS lastIp,u.last_time AS lastTime,u.registration_time AS registrationTime,u.dzstatus AS dzstatus,su.account AS superAccount, "+
                " from ("+sqlUser+") as u " +
                " LEFT JOIN t_sys_user AS su ON su.ucode = u.source_invitation_code ";
    }




    public static String findUserByUserId(Long userId) {
        String sqlUser = " select * from t_dzuser_user where id = '"+userId+"' ";
        String sqlLeft = getUserInfoSql(sqlUser);
        return sqlLeft;
    }

    public static String findUserByUserIdBill(Long userId) {
        return " select u.id,u.source_invitation_code as sourceInvitationCode ,u.account,u.vip_type as vipType from t_dzuser_user u where id = '"+userId+"' ";
    }

    private static String getUserInfoSql(String sqlUser) {

        return " select " +
                    " u.register_ip_city as registerIpCity,u.last_ip_city as lastIpCity,u.real_name AS realName,u.remark as remark,u.id AS id,u.create_time AS createTime,u.create_by AS createBy,u.modify_time AS modifyTime,u.modify_by AS modifyBy, " +
                    " u.limit_buy_cdb as limitBuyCdb,u.limit_drawing as limitDrawing,u.limit_profit as limitProfit, " +
                    " u.idw AS idw,u.source_invitation_code AS sourceInvitationCode,u.country_code_number AS countryCodeNumber, " +
                    " u.superior_invitation_code AS superiorInvitationCode,u.invitation_code AS invitationCode, " +
                    " u.name AS name,u.head_portrait_key AS headPortraitKey,u.pinvitation_code AS pinvitationCode, " +
                    " u.levels AS levels,u.account AS account,u.password AS password,u.payment_password AS paymentPassword, " +
                    " u.authenticator_password AS authenticatorPassword,u.user_type AS userType,u.vip_type AS vipType, " +
                    " u.vip_expire_date AS vipExpireDate,u.register_ip AS registerIp,u.last_ip AS lastIp,u.last_time AS lastTime, " +
                    " u.registration_time AS registrationTime,u.dzstatus AS dzstatus, " +
                    " IFNULL(ub.user_balance,0) as userBalanceLeft, " +
                    " IFNULL(ut.total_recharge_amount,0) as totalRechargeAmountLeft, " +
                    " IFNULL(uwnok.num,0) as remakeNumOK, " +
                    " IFNULL(uwok.money,0) as moneyOK, IFNULL(uwno.money,0) as moneyNO, " +
                    " IFNULL(uv.total_money,0) as teamVIPOpeningTotalRebate," +
                    " IFNULL(utb.total_bonus_income,0) as totalBonusIncomeLeft, " +
                    " IFNULL(up.total_bonus_income,0) as sourceUserAccount, " +
                    " ub.wallet_address as walletAddress, " +
                    " su.account as superAccount, " +

                    " vipobj.vip_img as vipImg, " +

                    " IFNULL(utv.team_vip_opening_total_rebate,0) as teamVIPOpeningTotal " +
                    " from ("+sqlUser+") as u " +
                    // 关联用户余额
                    " left join t_dzuser_balance as ub on ub.uid = u.id  " +
                    // 关联充值金额
                    " left join t_dzuser_totalrecharge as ut on ut.uid = u.id " +
                    // 关联已提现次数
                    " left join " +
                    " (select uid,count(1) as num from t_dzuser_withdrawals where (recharge_status = 'suc' or recharge_status = 'sysok') GROUP BY uid) as uwnok " +
                    " on uwnok.uid = u.id " +

                     // 关联已提现金额
                    " left join " +
                    " (select uid,sum(money) as money from t_dzuser_withdrawals where (recharge_status = 'suc' or recharge_status = 'sysok') GROUP BY uid) as uwok " +
                    " on uwok.uid = u.id " +
                    // 关联提现中金额
                    " left join " +
                    " (select uid,sum(money) as money from t_dzuser_withdrawals where recharge_status = 'no' GROUP BY uid) as uwno  " +
                    " on uwno.uid = u.id " +
                    // 关联VIP开通金额
                    " left join " +
//                    " (select uid,sum(payment_amount) as payment_amount from t_dzvip_vippurchase where whether_to_pay = 2 GROUP BY uid ) as uv" +
                    " t_dzvip_byviptotalmoney as uv" +
                    " on uv.uid = u.id " +
                    // 关联赠送彩总额
                    " left join t_dzuser_totalbonus as utb on utb.uid = u.id " +
                    // 关联充电宝返佣金额
                    " left join t_dzgoods_totalbonuspb as up on up.uid = u.id " +
                    // 关联用户
                    " left join t_sys_user as su on su.ucode = u.source_invitation_code " +
                    // 关联团队开通VIP总返佣
                    " left join t_dzvip_teamvip as utv on utv.uid = u.id "+
                    // 关联 vip表數據
                    " left join t_dzvip_vipmessage as vipobj on vipobj.vip_type = u.vip_type  " +
                    // 排序
                    " order by u.id desc ";
    }

    private static String getUserInfoSql(String sqlUser,String orderSql) {

        return " select " +
                " u.register_ip_city as registerIpCity,u.last_ip_city as lastIpCity,u.real_name AS realName,u.remark as remark,u.id AS id,u.create_time AS createTime,u.create_by AS createBy,u.modify_time AS modifyTime,u.modify_by AS modifyBy, " +
                " u.limit_buy_cdb as limitBuyCdb,u.limit_drawing as limitDrawing,u.limit_profit as limitProfit,u.limit_code as limitCode, " +
                " u.idw AS idw,u.source_invitation_code AS sourceInvitationCode,u.country_code_number AS countryCodeNumber, " +
                " u.superior_invitation_code AS superiorInvitationCode,u.invitation_code AS invitationCode, " +
                " u.name AS name,u.head_portrait_key AS headPortraitKey,u.pinvitation_code AS pinvitationCode, " +
                " u.levels AS levels,u.account AS account,u.password AS password,u.payment_password AS paymentPassword, " +
                " u.authenticator_password AS authenticatorPassword,u.user_type AS userType,u.vip_type AS vipType, " +
                " u.vip_expire_date AS vipExpireDate,u.register_ip AS registerIp,u.last_ip AS lastIp,u.last_time AS lastTime, " +
                " u.registration_time AS registrationTime,u.dzstatus AS dzstatus, " +
                " IFNULL(ub.user_balance,0) as userBalanceLeft, " +
                " IFNULL(ut.total_recharge_amount,0) as totalRechargeAmountLeft, " +
                " IFNULL(uwnok.num,0) as remakeNumOK, " +
                " IFNULL(uwok.money,0) as moneyOK, IFNULL(uwno.money,0) as moneyNO, " +
                " IFNULL(uv.total_money,0) as teamVIPOpeningTotalRebate," +
                " IFNULL(utb.total_bonus_income,0) as totalBonusIncomeLeft, " +
                " IFNULL(up.total_bonus_income,0) as sourceUserAccount, " +
                " ub.wallet_address as walletAddress, " +
                " su.account as superAccount, " +

                " vipobj.vip_img as vipImg, " +

                " IFNULL(utv.team_vip_opening_total_rebate,0) as teamVIPOpeningTotal " +
                " from ("+sqlUser+") as u " +
                // 关联用户余额
                " left join t_dzuser_balance as ub on ub.uid = u.id  " +
                // 关联充值金额
                " left join t_dzuser_totalrecharge as ut on ut.uid = u.id " +
                // 关联已提现次数
                " left join " +
                " (select uid,count(1) as num from t_dzuser_withdrawals where (recharge_status = 'suc' or recharge_status = 'sysok') GROUP BY uid) as uwnok " +
                " on uwnok.uid = u.id " +

                // 关联已提现金额
                " left join " +
                " (select uid,sum(money) as money from t_dzuser_withdrawals where (recharge_status = 'suc' or recharge_status = 'sysok') GROUP BY uid) as uwok " +
                " on uwok.uid = u.id " +
                // 关联提现中金额
                " left join " +
                " (select uid,sum(money) as money from t_dzuser_withdrawals where recharge_status = 'no' GROUP BY uid) as uwno  " +
                " on uwno.uid = u.id " +
                // 关联VIP开通金额
                " left join " +
//                    " (select uid,sum(payment_amount) as payment_amount from t_dzvip_vippurchase where whether_to_pay = 2 GROUP BY uid ) as uv" +
                " t_dzvip_byviptotalmoney as uv" +
                " on uv.uid = u.id " +
                // 关联赠送彩总额
                " left join t_dzuser_totalbonus as utb on utb.uid = u.id " +
                // 关联充电宝返佣金额
                " left join t_dzgoods_totalbonuspb as up on up.uid = u.id " +
                // 关联用户
                " left join t_sys_user as su on su.ucode = u.source_invitation_code " +
                // 关联团队开通VIP总返佣
                " left join t_dzvip_teamvip as utv on utv.uid = u.id "+
                // 关联 vip表數據
                " left join t_dzvip_vipmessage as vipobj on vipobj.vip_type = u.vip_type  " +
                // 排序
                orderSql;
    }

    /*
    查询部分用户信息
    zx
     */

    private static String getPartUserInfoSql(String sqlUser,String orderSql) {

        return "\n" +
                " SELECT u.walletAddress AS walletAddress,u.userBalanceLeft AS userBalanceLeft,u.register_ip_city AS registerIpCity,u.last_ip_city AS lastIpCity,u.real_name AS realName,u.remark AS remark,u.id AS id,u.create_time AS createTime,u.create_by AS createBy,u.modify_time AS modifyTime,u.modify_by AS modifyBy,u.limit_buy_cdb AS limitBuyCdb,u.limit_drawing AS limitDrawing,u.limit_profit AS limitProfit,u.limit_code AS limitCode,u.idw AS idw,u.source_invitation_code AS sourceInvitationCode,u.country_code_number AS countryCodeNumber,u.superior_invitation_code AS superiorInvitationCode,u.invitation_code AS invitationCode,u.NAME AS NAME,u.head_portrait_key AS headPortraitKey,u.pinvitation_code AS pinvitationCode,u.levels AS levels,u.account AS account,u.password AS password,u.payment_password AS paymentPassword,u.authenticator_password AS authenticatorPassword,u.user_type AS userType,u.vip_type AS vipType,u.vip_expire_date AS vipExpireDate,u.register_ip AS registerIp,u.last_ip AS lastIp,u.last_time AS lastTime,u.registration_time AS registrationTime,u.dzstatus AS dzstatus,su.account AS superAccount "+
                " from ("+sqlUser+") as u " +
                " LEFT JOIN t_sys_user AS su ON su.ucode = u.source_invitation_code "+
                // 排序
                orderSql;
    }


    public static String findCount(Page<UserInfo> page,String sourceInvitationCode) {
//        String sqlUser = " select count(user.id) as count from t_dzuser_user as user " +
//                " left join t_dzuser_balance as ub1 on ub1.uid = user.id  ";
        String sqlUser;
        if (StringUtil.isNotEmpty(sourceInvitationCode)){
            String supUser = " select * from t_dzuser_user where source_invitation_code = ( select ucode from t_sys_user where account = '"+sourceInvitationCode+"') ";
            sqlUser = " select count(user.id) as count from ("+supUser+") as user " +
                    " left join t_dzuser_balance as ub1 on ub1.uid = user.id  ";
        }else {
            sqlUser = " select count(user.id) as count from t_dzuser_user as user " +
                    " left join t_dzuser_balance as ub1 on ub1.uid = user.id  ";
        }

        sqlUser = SqlSpecification.toAddSql(sqlUser, page.getFilters());
        return sqlUser;
    }

//zx
    public static String newFindCount(Page<UserInfo> page,String sourceInvitationCode) {
//        String sqlUser = " select count(user.id) as count from t_dzuser_user as user " +
//                " left join t_dzuser_balance as ub1 on ub1.uid = user.id  ";
        String sqlUser;
        if (StringUtil.isNotEmpty(sourceInvitationCode)){
            String supUser = " select * from t_dzuser_user where source_invitation_code = ( select ucode from t_sys_user where account = '"+sourceInvitationCode+"') ";
            sqlUser = " select count(user.id) as count from ("+supUser+") as user " +
                    " left join t_dzuser_balance as ub1 on ub1.uid = user.id  ";
        }else {
            sqlUser = " select count(user.id) as count from t_dzuser_user as user " +
                    " left join t_dzuser_balance as ub1 on ub1.uid = user.id  ";
        }

        sqlUser = SqlSpecification.newToAddSql(sqlUser, page.getFilters());
        return sqlUser;
    }


    public static String newFindCount(Page<UserInfo> page) {
        String sqlUser;
            sqlUser = " select count(user.id) as count" +
                    " from t_dzuser_user as user " +
                    " left join t_dzuser_balance as ub1 on ub1.uid = user.id  ";
        sqlUser = SqlSpecification.newToAddSql(sqlUser, page.getFilters());
        return sqlUser;
    }


    /*
    查询单个数据
     */
    public static String findUserInfoByAccount(Long id) {

        return "select \n" +
                ""+id+" as uid, \n" +
                "IFNULL((SELECT t_dzuser_totalrecharge.total_recharge_amount FROM t_dzuser_totalrecharge WHERE t_dzuser_totalrecharge.uid="+id+"),0) AS totalRechargeAmountLeft,\n" +
                "IFNULL(a.num,0) as remakeNumOK,  \n" +
                "IFNULL(a.money,0) as moneyOK,\n" +
                "IFNULL(( SELECT sum(money) FROM t_dzuser_withdrawals WHERE t_dzuser_withdrawals.uid="+id+" and t_dzuser_withdrawals.recharge_status = 'no'),0) AS moneyNO,\n" +
                "IFNULL(( SELECT t_dzvip_byviptotalmoney.total_money FROM t_dzvip_byviptotalmoney WHERE t_dzvip_byviptotalmoney.uid="+id+"),0) AS teamVIPOpeningTotalRebate,\n" +
                "IFNULL(( SELECT t_dzuser_totalbonus.total_bonus_income FROM t_dzuser_totalbonus WHERE t_dzuser_totalbonus.uid="+id+"),0) AS totalBonusIncomeLeft,\n" +
                "IFNULL(( SELECT t_dzgoods_totalbonuspb.total_bonus_income FROM t_dzgoods_totalbonuspb WHERE t_dzgoods_totalbonuspb.uid="+id+"),0) AS sourceUserAccount,\n" +
                "IFNULL(( SELECT t_dzvip_teamvip.team_vip_opening_total_rebate FROM t_dzvip_teamvip WHERE t_dzvip_teamvip.uid="+id+"),0) AS teamVIPOpeningTotal\n" +
                "from\n" +
                " (select count(1) as num,sum(money) as money from t_dzuser_withdrawals WHERE (t_dzuser_withdrawals.recharge_status = 'suc' or t_dzuser_withdrawals.recharge_status = 'sysok') \n" +
                "\tand t_dzuser_withdrawals.uid="+id+") as a";
        // 排序
    }


}

