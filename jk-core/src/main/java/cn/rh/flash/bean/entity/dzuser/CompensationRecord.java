package cn.rh.flash.bean.entity.dzuser;

import cn.rh.flash.bean.entity.BaseEntity;
import cn.rh.flash.bean.entity.system.User;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity(name = "t_dzuser_compensation")
@Table(appliesTo = "t_dzuser_compensation", comment = "补分记录")
@Data
@EntityListeners(AuditingEntityListener.class)
public class CompensationRecord extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;
    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '来源邀请码'")
    private String sourceInvitationCode;

    // 用户id、用户账号、操作员、金额、备注、前余额、后余额、创建时间、补分类型、来源邀请码
    @Column(name = "uid", columnDefinition = "bigint COMMENT '用户id'")
    private Long uid;
    @Column(name = "account", columnDefinition = "VARCHAR(30) COMMENT '账号'")
    private String account;
    @Column(name = "addition_and_subtraction", columnDefinition = "int COMMENT '加减'")  // 补分类型
    private Integer additionAndSubtraction;
    @Column(name = "operator", columnDefinition = "VARCHAR(20) COMMENT '操作员'")
    private String operator;
    @Column(name = "money", columnDefinition = "decimal(30,6) COMMENT '金额'")
    private Double money;
    @Column(name = "former_credit_score", columnDefinition = "decimal(30,6) COMMENT '前余额'")
    private Double formerCreditScore;
    @Column(name = "post_credit_score", columnDefinition = "decimal(30,6) COMMENT '后余额'")
    private Double postCreditScore;

    @JoinColumn(name = "source_invitation_code", referencedColumnName="ucode", insertable = false, updatable = false, foreignKey = @ForeignKey( name = "none", value = ConstraintMode.NO_CONSTRAINT))
    @ManyToOne
    private User user;  //顶级账号

}
