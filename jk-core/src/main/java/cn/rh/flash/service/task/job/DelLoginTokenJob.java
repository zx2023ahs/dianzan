package cn.rh.flash.service.task.job;

import cn.rh.flash.bean.constant.cache.CacheApiKey;
import cn.rh.flash.cache.impl.EhcacheDao;
import cn.rh.flash.service.task.jobUtil.JobExecuter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Log4j2
@Component
public class DelLoginTokenJob extends JobExecuter {

    @Autowired
    private EhcacheDao ehcacheDao;


    @Override
    @Transactional
    public void execute(Map<String, Object> dataMap) throws Exception {

        delLoginToken();
    }

    private void delLoginToken() {
        ehcacheDao.hdelByPrex(CacheApiKey.LOGIN_CONSTANT);
    }
}
