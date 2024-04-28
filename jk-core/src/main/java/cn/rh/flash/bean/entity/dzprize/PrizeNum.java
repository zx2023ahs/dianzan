package cn.rh.flash.bean.entity.dzprize;


import cn.rh.flash.bean.entity.BaseEntity;
import cn.rh.flash.bean.entity.system.User;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity(name = "t_dzprize_prizenum")
@Table(appliesTo = "t_dzprize_prizenum", comment = "抽奖次数表")
@Data
@EntityListeners(AuditingEntityListener.class)
public class PrizeNum extends BaseEntity {

    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '来源邀请码'")
    private String sourceInvitationCode;

    @Column(name = "uid", columnDefinition = "bigint COMMENT '用户id'")
    private Long uid;

    @Column(name = "account", columnDefinition = "VARCHAR(32) COMMENT '账号'")
    private String account;

    @Column(name = "prize_type", columnDefinition = "VARCHAR(32) COMMENT '活动类型   1 转盘活动 2 投注活动 3 盲盒活动'")
    private String prizeType;

    @Column(name = "prize_num", columnDefinition = "int COMMENT '抽奖次数'")
    private Integer prizeNum;


    @JoinColumn(name = "source_invitation_code", referencedColumnName="ucode", insertable = false, updatable = false, foreignKey = @ForeignKey( name = "none", value = ConstraintMode.NO_CONSTRAINT))
    @ManyToOne
    private User user;  //顶级账号

}
