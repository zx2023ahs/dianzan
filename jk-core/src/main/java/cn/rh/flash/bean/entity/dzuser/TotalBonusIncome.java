package cn.rh.flash.bean.entity.dzuser;

import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity(name = "t_dzuser_totalbonus")
@Table(appliesTo = "t_dzuser_totalbonus", comment = "赠送彩金总收入")
@Data
@EntityListeners(AuditingEntityListener.class)
public class TotalBonusIncome extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;
    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '来源邀请码'")
    private String sourceInvitationCode;

    @Column(name = "dzversion", columnDefinition = "int COMMENT 'version'")
    private Integer dzversion;

    //用户id、用户账号、赠送彩金 、来源邀请码、version
    @Column(name = "uid", columnDefinition = "bigint COMMENT '用户id'")
    private Long uid;
    @Column(name = "account", columnDefinition = "VARCHAR(30) COMMENT '账号'")
    private String account;
    @Column(name = "total_bonus_income", columnDefinition = "decimal(30,6) COMMENT '赠送彩金总收入'")
    private Double totalBonusIncome;


}
