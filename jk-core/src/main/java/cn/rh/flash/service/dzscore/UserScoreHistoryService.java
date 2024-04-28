package cn.rh.flash.service.dzscore;

import cn.rh.flash.bean.entity.dzscore.UserScoreHistory;
import cn.rh.flash.dao.dzscore.UserScoreHistoryRepository;
import cn.rh.flash.service.BaseService;
import org.springframework.stereotype.Service;

@Service
public class UserScoreHistoryService extends BaseService<UserScoreHistory,Long, UserScoreHistoryRepository> {
}
