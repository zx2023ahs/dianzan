package cn.rh.flash.bean.entity.dzprize;


import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Transient;

@Entity(name = "t_dzprize_expectwinninguser")
@Table(appliesTo = "t_dzprize_expectwinninguser", comment = "预期中奖用户表")
@Data
@EntityListeners(AuditingEntityListener.class)
public class ExpectWinningUser extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;

    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '来源邀请码'")
    private String sourceInvitationCode;

    @Column(name = "uid", columnDefinition = "bigint COMMENT '用户id'")
    private Long uid;

    @Column(name = "account", columnDefinition = "VARCHAR(32) COMMENT '账号'")
    private String account;

    @Column(name = "prize_idw", columnDefinition = "VARCHAR(32) COMMENT '预期中奖ID'")
    private String prizeIdw;

    @Column(name = "prize_type", columnDefinition = "VARCHAR(32) COMMENT '活动类型   1 转盘活动 2 投注活动 3 盲盒活动'")
    private String prizeType;

    @Column(name = "is_prize", columnDefinition = "VARCHAR(32) COMMENT '是否中奖 yes/no'")
    private String isPrize;

    @Transient
    private String prizeName; // 奖品名称
}
