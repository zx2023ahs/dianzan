package cn.rh.flash.dao.dzuser;


import cn.rh.flash.bean.entity.dzuser.UserInfo;
import cn.rh.flash.dao.BaseRepository;


public interface UserInfoRepository extends BaseRepository<UserInfo,Long>{

    //UserInfo findByInvitationCode(String invitationCode);

    //UserInfo findByCountryCodeNumberAndAccount(String countryCode, String account);
}

