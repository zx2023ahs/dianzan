package cn.rh.flash.dao.dzprize;

import cn.rh.flash.bean.entity.dzprize.MonopolyUser;
import cn.rh.flash.dao.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public interface MonopolyUserRepository extends BaseRepository<MonopolyUser,Long> {
    /**
     * 查询大富翁用户数据和抽奖次数
     * @return
     */
    @Query(value = "SELECT u.id as id ,u.uid as uid ,u.account as account ,u.activity_type as activityType ,u.idw as idw ,u.position as position,u.source_invitation_code as sourceInvitationCode,n.prize_num as num ,n.id as prizeNumId from t_dzprize_monopoly_user as u left JOIN t_dzprize_prizenum as n on u.uid=n.uid where n.prize_type='10' and u.uid=?1", nativeQuery = true)
    Map queryMonopolyUserAndNum(Long uid);

    /**
     * 重置大富翁用户数据
     * @return
     */
    @Transactional
    @Modifying()
    @Query(value = " UPDATE t_dzprize_monopoly_user SET position = 0", nativeQuery = true)
    void resettingUserState();
}
