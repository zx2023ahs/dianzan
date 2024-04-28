package cn.rh.flash.service.dzscore;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.rh.flash.bean.entity.dzscore.SignIn;
import cn.rh.flash.bean.entity.dzscore.SignInSet;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.enumeration.MessageTemplateEnum;
import cn.rh.flash.bean.vo.front.Ret;
import cn.rh.flash.bean.vo.front.Rets;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.dao.dzscore.SignInRepository;
import cn.rh.flash.dao.dzscore.SignInSetRepository;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.service.coom.dz.RecordInformation;
import cn.rh.flash.service.dzuser.UserInfoService;
import cn.rh.flash.utils.IdWorker;
import cn.rh.flash.utils.factory.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class SignInService extends BaseService<SignIn, Long, SignInRepository> {

    @Autowired
    private SignInRepository signInRepository;

    @Autowired
    private SignInSetRepository signInSetRepository;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private RecordInformation recordInformation;

    @Transactional(rollbackFor = Exception.class)
    public Ret signToday(String account) {

        String sql = String.format("select * from t_dzscore_signin t where t.account = '%s' order by sign_time desc limit 1", account);

        UserInfo userInfo = userInfoService.get(SearchFilter.build("account", account));

        SignIn signIn = signInRepository.getNull(sql);
        if (signIn != null && signIn.getSignDays() < 7) {
            //最后一次签到的日期
            String dateLast = DateUtil.format(signIn.getSignTime(), "yyyy-MM-dd");
            Date date = new Date();
            String dateToday = DateUtil.format(date, "yyyy-MM-dd");
            if (dateToday.equals(dateLast)) {
                return Rets.failure(MessageTemplateEnum.SIGN_REPEAT.getCode(), MessageTemplateEnum.SIGN_REPEAT);
            }
            if (signIn.getSignTime().getTime() > date.getTime()) {
                return Rets.failure(MessageTemplateEnum.SIGN_REPEAT.getCode(), MessageTemplateEnum.SIGN_REPEAT);
            }
            if (signIn.getSignTime().getTime() < date.getTime()) {
                SignIn sign = new SignIn();
                sign.setIdw(new IdWorker().nextId() + "");
                sign.setSourceInvitationCode(userInfo.getSourceInvitationCode());
                sign.setUid(userInfo.getId());
                sign.setAccount(userInfo.getAccount());
                sign.setSignTime(new Date());
                sign.setLastSignTime(signIn.getSignTime());
                sign.setSignDays(1);

                //判断上一次和本次是否连续
                DateTime offsetDay = DateUtil.offsetDay(sign.getLastSignTime(), 1);
                String dateLast1 = DateUtil.format(offsetDay, "yyyy-MM-dd");
                String signDate = DateUtil.format(sign.getSignTime(), "yyyy-MM-dd");
                if (dateLast1.equals(signDate)) {
                    sign.setSignDays(signIn.getSignDays() + 1);
                }

                String setSql = String.format("select * from t_dzscore_signinset t where t.day_index <= %s order by day_index desc limit 1", sign.getSignDays());
                SignInSet dayIndex = signInSetRepository.getNull(setSql);
                if (dayIndex != null) {
                    sign.setRewardAmount(dayIndex.getRewardAmount());
                    recordInformation.changeUserScore(sign.getRewardAmount(), userInfo.getId(), userInfo.getSourceInvitationCode(), userInfo.getAccount(), 1);
                }
                this.insert(sign);
            }
        } else {
            SignIn sign = new SignIn();
            sign.setIdw(new IdWorker().nextId() + "");
            sign.setSourceInvitationCode(userInfo.getSourceInvitationCode());
            sign.setUid(userInfo.getId());
            sign.setAccount(userInfo.getAccount());
            sign.setSignTime(new Date());
            sign.setLastSignTime(signIn != null ? signIn.getSignTime() : null);
            sign.setSignDays(1);
            String setSql = String.format("select * from t_dzscore_signinset t where t.day_index <= %s order by day_index desc limit 1", sign.getSignDays());
            SignInSet dayIndex = signInSetRepository.getNull(setSql);
            if (dayIndex != null) {
                sign.setRewardAmount(dayIndex.getRewardAmount());
                recordInformation.changeUserScore(sign.getRewardAmount(), userInfo.getId(), userInfo.getSourceInvitationCode(), userInfo.getAccount(), 1);
            }
            this.insert(sign);
        }
        return Rets.success();
    }

    public Ret findSignInByUser(Page page, Long userId) {
        Page<SignIn> pageFilters = page;
        pageFilters.addFilter(SearchFilter.build("uid", userId));
        page = this.queryPage(pageFilters);
        List<SignIn> signIns = page.getRecords();
        return Rets.success(signIns);
    }
}
