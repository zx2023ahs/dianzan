package cn.rh.flash.service.dzsys;

import cn.rh.flash.bean.entity.dzsys.TutorialCenter;
import cn.rh.flash.dao.dzsys.TutorialCenterRepository;
import cn.rh.flash.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TutorialCenterService extends BaseService<TutorialCenter,Long, TutorialCenterRepository> {

    @Autowired
    private TutorialCenterRepository tutorialCenterRepository;
}
