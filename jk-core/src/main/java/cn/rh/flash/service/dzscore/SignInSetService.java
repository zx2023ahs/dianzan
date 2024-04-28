package cn.rh.flash.service.dzscore;

import cn.rh.flash.bean.entity.dzscore.SignIn;
import cn.rh.flash.bean.entity.dzscore.SignInSet;
import cn.rh.flash.bean.entity.dzscore.UserScore;
import cn.rh.flash.bean.vo.dz.SignInSetSubVo;
import cn.rh.flash.bean.vo.dz.SignInSetVo;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.dao.dzscore.SignInRepository;
import cn.rh.flash.dao.dzscore.SignInSetRepository;
import cn.rh.flash.service.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SignInSetService extends BaseService<SignInSet, Long, SignInSetRepository> {

    @Autowired
    private SignInRepository signInRepository;

    @Autowired
    private UserScoreService userScoreService;

    public Ret<SignInSetVo> signRule(String account) {
        SignInSetVo signInSetVo = new SignInSetVo();

        SignInSet one = this.get(SearchFilter.build("dayIndex", 1));
        SignInSet seven = this.get(SearchFilter.build("dayIndex", 7));
        List<SignInSetSubVo> list = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            SignInSetSubVo vo = new SignInSetSubVo();
            vo.setDay(i + 1);
            if (i == 6) {
                vo.setReward(String.valueOf(seven.getRewardAmount().intValue()));
            } else {
                vo.setReward(String.valueOf(one.getRewardAmount().intValue()));
            }
            list.add(vo);
        }

        if (StringUtils.isNotBlank(account)) {
            String sql = String.format("select * from t_dzscore_signin t where t.account = '%s' and (DATE(t.sign_time) = DATE(NOW()) or DATE(t.sign_time) = DATE(DATE_SUB(NOW(),INTERVAL 1 day))) order by sign_time desc limit 1", account);
            SignIn signIn = signInRepository.getNull(sql);
            if (signIn != null) {
                Integer signDays = signIn.getSignDays();
                for (SignInSetSubVo signInSetSubVo : list) {
                    if (signInSetSubVo.getDay() <= signDays) {
                        signInSetSubVo.setIsSign(1);
                    }
                }
            }

            UserScore userScore = userScoreService.get(SearchFilter.build("account", account));
            if (userScore != null) {
                signInSetVo.setScore(userScore.getUserScore().intValue());
            }

        }

        signInSetVo.setList(list);

        return Rets.success(signInSetVo);
    }
}
