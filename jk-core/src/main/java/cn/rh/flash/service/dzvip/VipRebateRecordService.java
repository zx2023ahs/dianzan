package cn.rh.flash.service.dzvip;

import cn.hutool.core.util.ObjUtil;
import cn.rh.flash.bean.entity.dzvip.VipRebateRecord;
import cn.rh.flash.bean.vo.dzvip.VipRebateRecordVo;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.bean.vo.query.SqlSpecification;
import cn.rh.flash.dao.dzvip.VipRebateRecordRepository;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.EasyExcelUtil;
import cn.rh.flash.utils.SqlFileNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class VipRebateRecordService extends BaseService<VipRebateRecord,Long, VipRebateRecordRepository> {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private VipRebateRecordRepository VipRebateRecordRepository;

    public Double sun(List<SearchFilter> filters) {

        //处理where
        String where= "";
        for (SearchFilter filter : filters) {
            where+=filter.join+"  "+ SqlFileNameUtils.convert(filter.fieldName)+"  " + filter.operator+" "+"'"+filter.value+"'"+"\t\n";
        }
        where = where.replace("EQ","=");
        where = where.replace("null","");
        where = where.replace("GTE",">=");
        where = where.replace("LTE","<=");
        where = where.replace("ISNULL","is null");
        where = where.replace("''","");
        Map mapBySql = VipRebateRecordRepository.getMapBySql(VipRebateRecordServiceSql.getSum(where));
        Object sum = mapBySql.get("sum");
        if (ObjUtil.isEmpty(sum)){
            return 0.00;
        }
        return Double.valueOf(sum.toString());
    }


    public void exportV2(HttpServletResponse response, List<Map<String, Object>> list) {
        List<VipRebateRecordVo> voList=new ArrayList<>();
        for (Map<String, Object> stringObjectMap : list) {
            VipRebateRecordVo vo=new VipRebateRecordVo();
            BeanUtil.mapToBean(stringObjectMap, vo);
            if (ObjUtil.isNotEmpty(stringObjectMap.get("relevels"))){
                vo.setRelevelsName(stringObjectMap.get("relevels").toString()+"级下级");
            }
            vo.setVipTypeSourlyName(vo.getSourceUserAccount()+" "+vo.getOldVipType_str()+"=>"+vo.getNewVipType_str());
            voList.add(vo);
        }
        EasyExcelUtil.export(response,"开通vip返佣记录",voList,VipRebateRecordVo.class);
    }


    public Map getVipSum(String startTime,String endTime,String ucode,String testCode) {
        String sql = VipRebateRecordServiceSql.getVipSum(startTime,endTime,ucode,testCode);
        Map mapBySql = VipRebateRecordRepository.getMapBySql(sql);
       return mapBySql;

    }


    public Double getMoneySum(List<SearchFilter> filters) {
        String sql = SqlSpecification.toAddSql("SELECT SUM(money) as moneySum FROM t_dzvip_viprebaterecord", filters);
        System.out.println(sql);
        Map mapBySql = VipRebateRecordRepository.getMapBySql(sql);
        BigDecimal moneySum = (BigDecimal) mapBySql.get("moneySum");
        if (ObjUtil.isEmpty(moneySum)){
            return 0.00;
        }
        return moneySum.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }


}