package cn.rh.flash.service.dzprize;


import cn.rh.flash.bean.dto.PrizeNumDto;
import cn.rh.flash.bean.entity.dzprize.PrizeNum;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.dao.dzprize.PrizeNumRepository;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.service.dzuser.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PrizeNumService extends BaseService<PrizeNum, Long, PrizeNumRepository> {

    @Autowired
    private UserInfoService userInfoService;

    public String upOrDownPoints(PrizeNumDto prizeNumDto) {
        // 查询用户是否存在
        UserInfo userInfo = userInfoService.get(SearchFilter.build("account", prizeNumDto.getAccount()));
        if (userInfo == null) {
            return "用户账号错误";
        }

        List<SearchFilter> filters = new ArrayList<>();
        filters.add(SearchFilter.build("account", prizeNumDto.getAccount()));
        filters.add(SearchFilter.build("prizeType", prizeNumDto.getPrizeType()));

        PrizeNum prizeNum = this.get(filters);
        if (prizeNumDto.getPrizeNum() == null) {
            prizeNumDto.setPrizeNum(0);
        }

        switch (prizeNumDto.getIsAdd()) {
            // 上分
            case "1":
                if (prizeNum == null) {
                    prizeNum = new PrizeNum();
                    prizeNum.setSourceInvitationCode(userInfo.getSourceInvitationCode());
                    prizeNum.setUid(userInfo.getId());
                    prizeNum.setPrizeType(prizeNumDto.getPrizeType());
                    prizeNum.setAccount(userInfo.getAccount());
                    prizeNum.setPrizeNum(prizeNumDto.getPrizeNum());
                } else {
                    prizeNum.setPrizeNum(prizeNum.getPrizeNum() == null ? 0 : prizeNum.getPrizeNum() + prizeNumDto.getPrizeNum());
                }
                break;
            // 下分
            case "2":
                if (prizeNum == null) {
                    return "当前用户无抽奖次数";
                } else {
                    if (prizeNumDto.getPrizeNum() > prizeNum.getPrizeNum()) {
                        return "剩余抽奖次数不足下分次数";
                    }
                    prizeNum.setPrizeNum(prizeNum.getPrizeNum() - prizeNumDto.getPrizeNum());
                }
                break;
        }

        this.update(prizeNum);
        return "OK";
    }
}
