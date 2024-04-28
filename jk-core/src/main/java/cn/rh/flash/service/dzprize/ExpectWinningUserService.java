package cn.rh.flash.service.dzprize;

import cn.rh.flash.bean.entity.dzprize.ExpectWinningUser;
import cn.rh.flash.bean.entity.dzprize.HuntingRecord;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.dao.dzprize.ExpectWinningUserRepository;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.service.dzuser.UserInfoService;
import cn.rh.flash.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class ExpectWinningUserService extends BaseService<ExpectWinningUser,Long, ExpectWinningUserRepository> {

    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private HuntingRecordService huntingRecordService;


    public String addExpectWinningUser(ExpectWinningUser expectWinningUser) {
        //夺宝活动预期中奖可以使用虚拟账号
        if (expectWinningUser.getPrizeType().equals("8")){
            List<SearchFilter> filters = new ArrayList<>();
            filters.add(SearchFilter.build("account", expectWinningUser.getAccount()));
            filters.add(SearchFilter.build("huntIdw", expectWinningUser.getPrizeIdw()));
            HuntingRecord record = huntingRecordService.get(filters);
//            UserInfo userInfo = userInfoService.get(SearchFilter.build("account", expectWinningUser.getAccount()));
            if (record == null){
                return "夺宝活动预期中奖只能在夺宝参与人中选择！";
            }
            expectWinningUser.setIdw(new IdWorker().nextId()+"");
            expectWinningUser.setSourceInvitationCode(record.getSourceInvitationCode());
            expectWinningUser.setUid(1L);
            expectWinningUser.setIsPrize("no");
            this.insert(expectWinningUser);
            return "OK";
        }

        // 查询用户是否存在
        UserInfo userInfo = userInfoService.get(SearchFilter.build("account", expectWinningUser.getAccount()));
        if (userInfo == null){
            return "用户账号错误";
        }
        expectWinningUser.setIdw(new IdWorker().nextId()+"");
        expectWinningUser.setSourceInvitationCode(userInfo.getSourceInvitationCode());
        expectWinningUser.setUid(userInfo.getId());
        expectWinningUser.setIsPrize("no");
        this.insert(expectWinningUser);
        return "OK";
    }
}
