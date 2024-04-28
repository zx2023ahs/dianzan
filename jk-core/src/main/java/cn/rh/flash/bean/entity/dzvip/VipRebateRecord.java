package cn.rh.flash.bean.entity.dzvip;

import cn.rh.flash.bean.entity.BaseEntity;
import cn.rh.flash.bean.entity.dzuser.UserInfo;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;



@Entity(name = "t_dzvip_viprebaterecord")
@Table(appliesTo = "t_dzvip_viprebaterecord", comment = "团队开通vip返佣记录")  ///
@Data
@EntityListeners(AuditingEntityListener.class)
public class VipRebateRecord extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'") // 管理购买记录
    private String idw;
    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '来源邀请码'")
    private String sourceInvitationCode;

    //用户id、用户账号、共享余额、周期产品编号、买入时间、到期时间、收益百分比、来源邀请码
    @Column(name = "uid", columnDefinition = "bigint COMMENT '用户id'")
    private Long uid;
    @Column(name = "account", columnDefinition = "VARCHAR(30) COMMENT '账号'")
    private String account;
    @Column(name = "money", columnDefinition = "decimal(30,6) COMMENT '金额'")
    private Double money;

    @Column(name = "previousAmount", columnDefinition = "decimal(30,6) COMMENT '前金额'")
    private Double previousAmount;
    @Column(name = "amount_after", columnDefinition = "decimal(30,6) COMMENT '后金额'")
    private Double amountAfter;

    @Column(name = "source_user_account", columnDefinition = "VARCHAR(30) COMMENT '来源用户账号'")
    private String sourceUserAccount;

    @Column(name = "old_vip_type", columnDefinition = "VARCHAR(30) COMMENT '原来ViP类型'")
    private String oldVipType;
    @Column(name = "new_vip_type", columnDefinition = "VARCHAR(30) COMMENT '购买ViP类型'")
    private String newVipType;


    @Column(name = "relevels", columnDefinition = "int COMMENT '相对层级'")
    private Integer relevels;

    @Column(name = "fidw", columnDefinition = "VARCHAR(32) COMMENT '造假数据唯一值'")
    private String fidw;

    @JoinColumn(name = "source_user_account", referencedColumnName="account", insertable = false, updatable = false, foreignKey = @ForeignKey( name = "none", value = ConstraintMode.NO_CONSTRAINT))
    @ManyToOne
    private UserInfo userInfo;  // 来源用户信息


}
