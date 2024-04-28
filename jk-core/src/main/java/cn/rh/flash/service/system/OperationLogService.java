package cn.rh.flash.service.system;

import cn.rh.flash.bean.entity.system.OperationLog;
import cn.rh.flash.dao.system.OperationLogRepository;
import cn.rh.flash.service.BaseService;
import org.springframework.stereotype.Service;


@Service
public class OperationLogService extends BaseService<OperationLog, Long, OperationLogRepository> {

}
