package cn.rh.flash.bean.entity.dzpower;

import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity(name = "t_dzgoods_totalbonuspb")
@Table(appliesTo = "t_dzgoods_totalbonuspb", comment = "充电宝返佣总收入")
@Data
@EntityListeners(AuditingEntityListener.class)
public class TotalBonusPb extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;
    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '来源邀请码'")
    private String sourceInvitationCode;

    @Column(name = "dzversion", columnDefinition = "int COMMENT 'version'")
    private Integer dzversion;

    @Column(name = "uid", columnDefinition = "bigint COMMENT '用户id'")
    private Long uid;
    @Column(name = "account", columnDefinition = "VARCHAR(30) COMMENT '账号'")
    private String account;
    @Column(name = "total_bonus_income", columnDefinition = "decimal(30,6) COMMENT '充电宝返佣总收入'")
    private Double totalBonusIncome;


    @Column(name = "source_user_account", columnDefinition = "VARCHAR(30) COMMENT '来源用户账号'")
    private String sourceUserAccount;


}
