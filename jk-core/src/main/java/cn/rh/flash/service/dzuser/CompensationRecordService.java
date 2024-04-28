package cn.rh.flash.service.dzuser;


import cn.hutool.core.util.ObjectUtil;
import cn.rh.flash.bean.entity.dzuser.CompensationRecord;
import cn.rh.flash.bean.vo.dzser.CompensationRecordVo;
import cn.rh.flash.dao.dzuser.CompensationRecordRepository;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.service.system.impl.ConstantFactory;
import cn.rh.flash.utils.EasyExcelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CompensationRecordService extends BaseService<CompensationRecord,Long,CompensationRecordRepository>  {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private CompensationRecordRepository compensationRecordRepository;

    public Double branchTotal(String operator, String account, String expireTimes, String expireTimee, String sourceInvitationCode,String testCode) {
        Map map =  compensationRecordRepository.getMapBySql( CompensationRecordServiceSql.sqlBranchTotal(operator,account,expireTimes,expireTimee,sourceInvitationCode,testCode) );
        return map.get( "branchTotal" ) == null ? 0 : Double.valueOf( map.get( "branchTotal" )+"" );
    }



    public void exportV2(HttpServletResponse response, List<CompensationRecord> records) {
        Map<String, String> jiaJian = ConstantFactory.me().getDictsToMap("加减");
        List<CompensationRecordVo> voList=new ArrayList<>();
        for (CompensationRecord record : records) {
            CompensationRecordVo vo=new CompensationRecordVo();
            BeanUtils.copyProperties(record,vo);
            vo.setUserAccount(record.getUser().getAccount());
            vo.setMoneyAndAdditionAndSubtraction(jiaJian.get(record.getAdditionAndSubtraction().toString())+""+record.getMoney());
            voList.add(vo);
        }
        EasyExcelUtil.export(response,"补分记录",voList,CompensationRecordVo.class);
    }


    public BigDecimal getSum(String startTime, String endTime, String ucode,String testCode) {
        String sql = CompensationRecordServiceSql.getSum(startTime,endTime,ucode,testCode);
        Map mapBySql = compensationRecordRepository.getMapBySql(sql);
        if (ObjectUtil.isNotEmpty(mapBySql) && ObjectUtil.isNotEmpty(mapBySql.get("sum"))){
            return  new BigDecimal(mapBySql.get("sum").toString());
        }
        return new BigDecimal("0.00");
    }


}

