package cn.rh.flash.service.dzvip;


import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.entity.dzvip.ByVipTotalMoney;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.dao.dzvip.ByVipTotalMoneyRepository;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.utils.IdWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ByVipTotalMoneyService extends BaseService<ByVipTotalMoney,Long, ByVipTotalMoneyRepository> {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ByVipTotalMoneyRepository byVipTotalMoneyRepository;

    @Transactional(rollbackFor = Exception.class)
    public void addByVipTotalMoney(UserInfo userInfo, Double money) {

        ByVipTotalMoney byVipTotalMoney = this.get(SearchFilter.build("uid", userInfo.getId()));
        if (byVipTotalMoney == null){
            byVipTotalMoney = new ByVipTotalMoney();
            byVipTotalMoney.setIdw(new IdWorker().nextId() + "");
            byVipTotalMoney.setSourceInvitationCode(userInfo.getSourceInvitationCode());
            byVipTotalMoney.setTotalMoney(money);
            byVipTotalMoney.setUid(userInfo.getId());
            byVipTotalMoney.setAccount(userInfo.getAccount());
            this.insert(byVipTotalMoney);
        }else {
            byVipTotalMoney.setTotalMoney(byVipTotalMoney.getTotalMoney()+money);
            this.update(byVipTotalMoney);
        }
    }
}
