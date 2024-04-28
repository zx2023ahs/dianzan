package cn.rh.flash.bean.entity.dzscore;


import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import java.util.Date;

@Entity(name = "t_dzscore_signin")
@Table(appliesTo = "t_dzscore_signin", comment = "用户签到")
@Data
@EntityListeners(AuditingEntityListener.class)
public class SignIn extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;

    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '来源邀请码'")
    private String sourceInvitationCode;

    @Column(name = "uid", columnDefinition = "bigint COMMENT '用户id'")
    private Long uid;

    @Column(name = "account", columnDefinition = "VARCHAR(32) COMMENT '账号'")
    private String account;

    @Column(name = "sign_time", columnDefinition = "DATETIME COMMENT '签到时间'")
    private Date signTime;

    @Column(name = "last_sign_time", columnDefinition = "DATETIME COMMENT '上次签到时间'")
    private Date lastSignTime;

    @Column(name = "sign_days", columnDefinition = "int COMMENT '签到天数'")
    private Integer signDays;

    @Column(name = "reward_amount", columnDefinition = "decimal(30,6) COMMENT '奖励积分'")
    private Double rewardAmount;

}
