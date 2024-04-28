package cn.rh.flash.service.dzuser;


import cn.rh.flash.bean.entity.dzuser.TotalBonusIncome;
import cn.rh.flash.dao.dzuser.TotalBonusIncomeRepository;
import cn.rh.flash.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TotalBonusIncomeService extends BaseService<TotalBonusIncome,Long,TotalBonusIncomeRepository>  {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private TotalBonusIncomeRepository totalBonusIncomeRepository;

    public Double branchTotal() {
        Map map =  totalBonusIncomeRepository.getMapBySql( TotalBonusIncomeServiceSql.sqlBranchTotal() );
        return map.get( "branchTotal" ) == null ? 0 : Double.valueOf( map.get( "branchTotal" )+"" );
    }


}

