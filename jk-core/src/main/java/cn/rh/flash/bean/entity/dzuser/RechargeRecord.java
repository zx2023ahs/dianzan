package cn.rh.flash.bean.entity.dzuser;

import cn.rh.flash.bean.entity.BaseEntity;
import cn.rh.flash.bean.entity.system.User;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity(name = "t_dzuser_rechargehistory")
@Table(appliesTo = "t_dzuser_rechargehistory", comment = "充值记录")
@Data
@EntityListeners(AuditingEntityListener.class)
public class RechargeRecord extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;
    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '来源邀请码'")
    private String sourceInvitationCode;

    // 用户id、用户账号、订单编号、金额、通道名称、充值状态、前余额、后余额、来源邀请码
    @Column(name = "uid", columnDefinition = "bigint COMMENT '用户id'")
    private Long uid;
    @Column(name = "account", columnDefinition = "VARCHAR(30) COMMENT '账号'")
    private String account;
    @Column(name = "order_number", columnDefinition = "VARCHAR(30) COMMENT '订单编号'")
    private String orderNumber;
    @Column(name = "money", columnDefinition = "decimal(30,6) COMMENT '金额'")
    private Double money;
    @Column(name = "channel_name", columnDefinition = "VARCHAR(30) COMMENT '通道名称'")
    private String channelName;
    @Column(name = "recharge_status", columnDefinition = "VARCHAR(10) COMMENT '充值状态'")
    private String rechargeStatus;  // 充值状态 1:进行中,2:待回掉,3:已完成,4:已拒绝
    @Column(name = "previous_balance", columnDefinition = "decimal(30,6) COMMENT '前余额'")
    private Double previousBalance;
    @Column(name = "after_balance", columnDefinition = "decimal(30,6) COMMENT '后余额'")
    private Double afterBalance;
    @Column(name = "withdrawal_address", columnDefinition = "VARCHAR(50) COMMENT '充值地址'")
    private String withdrawalAddress;
    @Column(name = "channel_type", columnDefinition = "VARCHAR(50) COMMENT '通道类型'")
    private String channelType;

    @Column(name = "fidw", columnDefinition = "VARCHAR(32) COMMENT '造假数据唯一值'")
    private String fidw;

    @JoinColumn(name = "uid", referencedColumnName="id", insertable = false, updatable = false, foreignKey = @ForeignKey( name = "none", value = ConstraintMode.NO_CONSTRAINT))
    @ManyToOne
    private UserInfo userInfo;  // 用户自己

    @JoinColumn(name = "source_invitation_code", referencedColumnName="ucode", insertable = false, updatable = false, foreignKey = @ForeignKey( name = "none", value = ConstraintMode.NO_CONSTRAINT))
    @ManyToOne
    private User user;  //顶级账号

    @Column(name = "first_charge", columnDefinition = "VARCHAR(32) COMMENT '是否首充'") // 1 首充 2 复充
    private String firstCharge;
}
