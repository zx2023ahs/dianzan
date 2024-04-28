package cn.rh.flash.service.dzprize;

import cn.rh.flash.bean.entity.dzprize.Prize;
import cn.rh.flash.bean.vo.query.SqlSpecification;
import cn.rh.flash.utils.factory.Page;

public class PrizeServiceSql {

    /**
     * 分页查询夺宝奖品信息
     *
     * @param page
     * @return
     */
    public static String findPrizeInfoPage(Page<Prize> page) {
        String sqlPrize;
        sqlPrize = "SELECT a.idw as idw,a.prize_name as prizeName,a.prize_nice as prizeNice ,a.url as url,a.prize_type as prizeType,a.amount as amount\n" +
                ",a.total_number as totalNumber,a.participate_number as participateNumber,a.is_end as isEnd,b.account as account \n" +
                "FROM t_dzprize_prize as a LEFT JOIN t_dzprize_winningrecord as b on a.idw=b.prize_idw where a.prize_type=\"8\"";
        sqlPrize = SqlSpecification.toSqlLimit(page.getCurrent(), page.getSize(), sqlPrize, "a.id");
        return sqlPrize;
    }

    public static String getRandomRecord() {
        String sqlPrize;
        sqlPrize = " SELECT  prize_name,  prize_type  FROM  t_dzprize_prize as a   WHERE  a.id >= ( SELECT floor( RAND() * ( SELECT MAX( b.id ) FROM `t_dzprize_prize`  as b) ) )  ORDER BY  a.id  LIMIT 10 ";
        return sqlPrize;
    }

}
