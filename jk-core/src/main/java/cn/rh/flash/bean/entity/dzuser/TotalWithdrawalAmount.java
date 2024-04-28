package cn.rh.flash.bean.entity.dzuser;

import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity(name = "t_dzuser_totalwithdrawal")
@Table(appliesTo = "t_dzuser_totalwithdrawal", comment = "提现总金额")
@Data
@EntityListeners(AuditingEntityListener.class)
public class TotalWithdrawalAmount extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;
    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '来源邀请码'")
    private String sourceInvitationCode;

    @Column(name = "dzversion", columnDefinition = "int COMMENT 'version'")
    private Integer dzversion;

    // 用户id、用户账号、提现总金额                 、来源邀请码、version
    @Column(name = "uid", columnDefinition = "bigint COMMENT '用户id'")
    private Long uid;
    @Column(name = "account", columnDefinition = "VARCHAR(30) COMMENT '账号'")
    private String account;
    @Column(name = "total_withdrawal_amount", columnDefinition = "decimal(30,6) COMMENT '提现总金额'")
    private Double totalWithdrawalAmount;


}
