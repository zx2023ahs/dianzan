package cn.rh.flash.service.dzpower;


import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.rh.flash.bean.entity.dzpower.RecordPb;
import cn.rh.flash.bean.vo.dzpower.RecordPbVo;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.dao.dzpower.RecordPbRepository;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.utils.EasyExcelUtil;
import cn.rh.flash.utils.SqlFileNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecordPbService extends BaseService<RecordPb, Long, RecordPbRepository> {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private RecordPbRepository recordPbRepository;

    public Double querySum(List<SearchFilter> filters) {
        //处理where
        String where = "";
        for (SearchFilter filter : filters) {
            where += filter.join + "  " + SqlFileNameUtils.convert(filter.fieldName) + "  " + filter.operator + " " + "'" + filter.value + "'" + "\t\n";
        }
        where = where.replace("EQ", "=");
        where = where.replace("null", "");
        where = where.replace("GTE", ">=");
        where = where.replace("LTE", "<=");
        where = where.replace("LT", "<");
        where = where.replace("ISNULL", "is null");
        where = where.replace("''", "");
        Map mapBySql = recordPbRepository.getMapBySql(RecordPbServiceSql.getSum(where));
        Object sum = mapBySql.get("sum");
        if (ObjUtil.isEmpty(sum)) {
            return 0.00;
        }
        return Double.valueOf(sum.toString());
    }

    public void exportV2(HttpServletResponse response, List<RecordPb> records) {
        List<RecordPbVo> voList = new ArrayList<>();
        for (RecordPb record : records) {
            RecordPbVo vo = new RecordPbVo();
            BeanUtils.copyProperties(record, vo);
            if (ObjUtil.isNotEmpty(record.getRelevels()) && record.getRelevels() == 0) {
                vo.setRelevelsName("自身收益");
            }
            if (ObjUtil.isNotEmpty(record.getRelevels()) && record.getRelevels() != 0) {
                vo.setRelevelsName(record.getRelevels() + "级返佣");
            }
            voList.add(vo);
        }
        EasyExcelUtil.export(response,"充电宝返佣记录",voList,RecordPbVo.class);
    }


    public int getCount(String startTime, String endTime, String ucode,String testCode) {
        String sql = RecordPbServiceSql.getCount(startTime,endTime,ucode,testCode);
        Map mapBySql = recordPbRepository.getMapBySql(sql);
        if (ObjectUtil.isNotEmpty(mapBySql) && ObjectUtil.isNotEmpty(mapBySql.get("count"))){
            return  Integer.parseInt(mapBySql.get("count").toString());
        }
        return 0;
    }


    public Map<String, BigDecimal> getReLevelsMap(String startTime, String endTime, String ucode,String testCode) {
        HashMap<String, BigDecimal> stringBigDecimalHashMap = new HashMap<>();
        stringBigDecimalHashMap.put("sum_relevels_1",new BigDecimal("0.00"));
        stringBigDecimalHashMap.put("sum_relevels_2",new BigDecimal("0.00"));
        stringBigDecimalHashMap.put("sum_relevels_3",new BigDecimal("0.00"));
        stringBigDecimalHashMap.put("sum",new BigDecimal("0.00"));
        String sql = RecordPbServiceSql.getVipSum(startTime,endTime, ucode, testCode);
        Map mapBySql = recordPbRepository.getMapBySql(sql);
        if (ObjUtil.isNotEmpty(mapBySql)){
            if (ObjUtil.isNotEmpty(mapBySql.get("sum_relevels_1"))){
                stringBigDecimalHashMap.put("sum_relevels_1",new BigDecimal(mapBySql.get("sum_relevels_1").toString()));
            }
            if (ObjUtil.isNotEmpty(mapBySql.get("sum_relevels_2"))){
                stringBigDecimalHashMap.put("sum_relevels_2",new BigDecimal(mapBySql.get("sum_relevels_2").toString()));
            }
            if (ObjUtil.isNotEmpty(mapBySql.get("sum_relevels_3"))){
                stringBigDecimalHashMap.put("sum_relevels_3",new BigDecimal(mapBySql.get("sum_relevels_3").toString()));
            }
            if (ObjUtil.isNotEmpty(mapBySql.get("sum"))){
                stringBigDecimalHashMap.put("sum",new BigDecimal(mapBySql.get("sum").toString()));
            }
        }
        return stringBigDecimalHashMap;

    }
}

