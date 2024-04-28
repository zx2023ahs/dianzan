package cn.rh.flash.service.dzscore;

import cn.rh.flash.bean.entity.dzscore.UserScore;
import cn.rh.flash.dao.dzscore.UserScoreRepository;
import cn.rh.flash.service.BaseService;
import org.springframework.stereotype.Service;

@Service
public class UserScoreService extends BaseService<UserScore,Long, UserScoreRepository> {
}
