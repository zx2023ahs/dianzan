package cn.rh.flash.service.dzsys;


import cn.rh.flash.bean.entity.dzsys.SysLog;
import cn.rh.flash.bean.enumeration.SysLogEnum;
import cn.rh.flash.dao.dzsys.SysLogRepository;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.utils.DateUtil;
import cn.rh.flash.utils.IdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class SysLogService extends BaseService<SysLog,Long, SysLogRepository>  {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private SysLogRepository sysLogRepository;

    @Transactional(rollbackFor = Exception.class)
    public void addSysLog( String operator, Long objId,String account, String operatorSystem, SysLogEnum operation){
        Date date = DateUtil.parseTime( DateUtil.getTime() );
        SysLog sysLog = new SysLog();
        sysLog.setIdw(new IdWorker().nextId()+"" );
        sysLog.setOperator(operator);
        sysLog.setObjId(objId);
        sysLog.setOperatorSystem(operatorSystem);
        sysLog.setOperation(operation);
        sysLog.setOperationTime(date);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = sdf.format(date);

        sysLog.setRemark(operator+"--在"+format+"--"+operation.getMessage()+", 操作账号:"+account);
        this.insert(sysLog);
    }





    @Transactional(rollbackFor = Exception.class)
    public void addSysLog( String operator, Long objId, String operatorSystem, SysLogEnum operation,String Remark){
        Date date = DateUtil.parseTime( DateUtil.getTime() );
        SysLog sysLog = new SysLog();
        sysLog.setIdw(new IdWorker().nextId()+"" );
        sysLog.setOperator(operator);
        sysLog.setObjId(objId);
        sysLog.setOperatorSystem(operatorSystem);
        sysLog.setOperation(operation);
        sysLog.setOperationTime(date);

        sysLog.setRemark(Remark);
        this.insert(sysLog);
    }

}

