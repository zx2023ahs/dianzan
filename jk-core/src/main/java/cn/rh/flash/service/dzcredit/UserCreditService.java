package cn.rh.flash.service.dzcredit;


import cn.hutool.core.util.ObjUtil;
import cn.rh.flash.bean.dto.UserCreditDto;
import cn.rh.flash.bean.entity.dzcredit.UserCredit;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.bean.vo.dzcredit.UserCreditVo;
import cn.rh.flash.bean.vo.query.SearchFilter;
import cn.rh.flash.dao.dzcredit.UserCreditRepository;
import cn.rh.flash.service.BaseService;
import cn.rh.flash.service.coom.dz.RecordInformation;
import cn.rh.flash.service.dzuser.UserInfoService;
import cn.rh.flash.utils.BeanUtil;
import cn.rh.flash.utils.EasyExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserCreditService extends BaseService<UserCredit, Long, UserCreditRepository> {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private RecordInformation recordInformation;

    // 上下信誉分
    public String upOrDownCredit(UserCreditDto userCreditDto, String userName) {
        // 查询用户是否存在
        UserInfo userInfo = userInfoService.get(SearchFilter.build("account", userCreditDto.getAccount()));
        if (userInfo == null) {
            return "用户账号错误";
        }
        recordInformation.changeCredit(userInfo.getSourceInvitationCode(), userInfo.getId(), userInfo.getAccount(), "1", "2", userCreditDto.getIsAdd(), userCreditDto.getCredit(), userName, "admin", userInfo.getVipType());
        return "OK";
    }


    public void exportXlsV2(HttpServletResponse response, List<Map<String, Object>> list) {
        List<UserCreditVo> voList = new ArrayList<>();
        for (Map<String, Object> stringObjectMap : list) {
            UserCreditVo vo = new UserCreditVo();
            BeanUtil.mapToBean(stringObjectMap, vo);
            if (ObjUtil.isNotEmpty(stringObjectMap.get("status_str"))) {
                vo.setStatusName(stringObjectMap.get("status_str").toString());
            }
            if (ObjUtil.isNotEmpty(stringObjectMap.get("vipType_str"))) {
                vo.setVipTypeName(stringObjectMap.get("vipType_str").toString());
            }
            voList.add(vo);
        }
        EasyExcelUtil.export(response, "用户信誉分", voList, UserCreditVo.class);
    }


}
