package cn.rh.flash.bean.entity.dzscore;

import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity(name = "t_dzscore_userscore")
@Table(appliesTo = "t_dzscore_userscore", comment = "用户积分总")
@Data
@EntityListeners(AuditingEntityListener.class)
public class UserScore extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;

    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '来源邀请码'")
    private String sourceInvitationCode;

    @Column(name = "uid", columnDefinition = "bigint COMMENT '用户id'")
    private Long uid;

    @Column(name = "account", columnDefinition = "VARCHAR(30) COMMENT '账号'")
    @NotBlank
    private String account;

    @Column(name = "user_score", columnDefinition = "decimal(30,6) COMMENT '用户积分'")
    @NotNull
    private Double userScore;

    @Column(name = "prize_type", columnDefinition = "VARCHAR(32) COMMENT '活动类型  1、签到 8、夺宝活动'")//不同活动有不同的积分，不通用
    private String prizeType;

}
