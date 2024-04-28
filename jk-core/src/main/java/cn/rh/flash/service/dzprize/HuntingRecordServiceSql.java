package cn.rh.flash.service.dzprize;

import cn.rh.flash.bean.entity.dzprize.HuntingRecord;
import cn.rh.flash.bean.vo.query.SqlSpecification;
import cn.rh.flash.utils.factory.Page;

public class HuntingRecordServiceSql {

    /**
     * 分页查询夺宝奖品参与信息
     * @param page
     * @return
     */
    public static String findHuntingRecordPage(Page<HuntingRecord> page) {
        String sqlHuntingRecord;
        sqlHuntingRecord = "SELECT * FROM t_dzprize_huntingrecord as a";
        sqlHuntingRecord = SqlSpecification.toAddSql(sqlHuntingRecord, page.getFilters());
        sqlHuntingRecord = SqlSpecification.toSqlLimit(page.getCurrent(), page.getSize(), sqlHuntingRecord, "a.id");
        return sqlHuntingRecord;
    }


}
