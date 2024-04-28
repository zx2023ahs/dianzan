package cn.rh.flash.bean.entity.dzscore;

import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity(name = "t_dzscore_userscorehistory")
@Table(appliesTo = "t_dzscore_userscorehistory", comment = "用户积分记录")
@Data
@EntityListeners(AuditingEntityListener.class)
public class UserScoreHistory extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;

    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '来源邀请码'")
    private String sourceInvitationCode;

    @Column(name = "uid", columnDefinition = "bigint COMMENT '用户id'")
    private Long uid;

    @Column(name = "account", columnDefinition = "VARCHAR(30) COMMENT '账号'")
    private String account;

    @Column(name = "user_score", columnDefinition = "decimal(30,6) COMMENT '用户积分'")
    private Double userScore;

    @Column(name = "type", columnDefinition = "int COMMENT '类型 1签到 2邀请 3赠送  8夺宝'")
    private Integer type;

}
