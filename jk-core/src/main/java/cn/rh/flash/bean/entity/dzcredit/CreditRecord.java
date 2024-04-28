package cn.rh.flash.bean.entity.dzcredit;


import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity(name = "t_dzcredit_creditrecord")
@Table(appliesTo = "t_dzcredit_creditrecord", comment = "信誉分变动记录")
@Data
@EntityListeners(AuditingEntityListener.class)
public class CreditRecord extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;

    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '来源邀请码'")
    private String sourceInvitationCode;

    @Column(name = "uid", columnDefinition = "bigint COMMENT '用户id'")
    private Long uid;

    @Column(name = "account", columnDefinition = "VARCHAR(32) COMMENT '账号'")
    private String account;

    @Column(name = "from_account", columnDefinition = "VARCHAR(32) COMMENT '来源账号'")
    private String fromAccount;

    @Column(name = "befort_credit", columnDefinition = "int COMMENT '变更前信誉分'")
    private Integer befortCredit;

    @Column(name = "credit_change", columnDefinition = "int COMMENT '信誉分变更值'")
    private Integer creditChange;

    @Column(name = "after_credit", columnDefinition = "int COMMENT '变更后信誉分'")
    private Integer afterCredit;

    @Column(name = "charge_status", columnDefinition = "VARCHAR(10) COMMENT '变更类型'")
    private String chargeStatus; // 1.注册 2.手动上下分 3.升级vip 4.直属下级升级vip 5.3天未运行 6.5天未运行 7.直属下级注册

    @Column(name = "remark", columnDefinition = "VARCHAR(500) COMMENT '备注'")
    private String remark;

}
