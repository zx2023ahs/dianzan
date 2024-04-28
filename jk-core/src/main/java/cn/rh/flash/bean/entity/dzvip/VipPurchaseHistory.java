package cn.rh.flash.bean.entity.dzvip;

import cn.rh.flash.bean.entity.BaseEntity;
import cn.rh.flash.bean.entity.system.User;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity(name = "t_dzvip_vippurchase")
@Table(appliesTo = "t_dzvip_vippurchase", comment = "Vip购买记录")
@Data
@EntityListeners(AuditingEntityListener.class)
public class VipPurchaseHistory extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;
    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '来源邀请码'")
    private String sourceInvitationCode;

    // 用户id、用户账号、之前ViP类型、之后ViP类型、支付金额、支付方式、任务数量、创建时间、有效天数、来源邀请码
    @Column(name = "uid", columnDefinition = "bigint COMMENT '用户id'")
    private Long uid;
    @Column(name = "account", columnDefinition = "VARCHAR(30) COMMENT '账号'")
    private String account;

    @Column(name = "channel_name", columnDefinition = "VARCHAR(30) COMMENT '通道名称'")
    private String channelName;
    @Column(name = "channel_type", columnDefinition = "VARCHAR(30) COMMENT '通道类型'")
    private String channelType;

    @Column(name = "previous_vip_type", columnDefinition = "VARCHAR(10) COMMENT '之前ViP类型'")
    private String previousViPType;
    @Column(name = "after_vip_type", columnDefinition = "VARCHAR(10) COMMENT '之后ViP类型'")
    private String afterViPType;
    @Column(name = "payment_amount", columnDefinition = "decimal(30,6) COMMENT '支付金额'")
    private Double paymentAmount;
    @Column(name = "payment_method", columnDefinition = "int COMMENT '支付方式'")
    private Integer paymentMethod;
    @Column(name = "number_of_tasks", columnDefinition = "int COMMENT '充电宝数量'")
    private Integer numberOfTasks;
    @Column(name = "daily_income", columnDefinition = "decimal(30,6) COMMENT '每日收入'")
    private Double dailyIncome;
    @Column(name = "valid_date", columnDefinition = "int COMMENT '有效天数'")
    private Integer validDate;

    @Column(name = "whether_to_pay", columnDefinition = "int COMMENT '是否支付'")
    private Integer whetherToPay;  // 支付状态 1:未支付,2:已支付

    @Column(name = "deposit_address", columnDefinition = "VARCHAR(50) COMMENT '充值地址'")
    private String depositAddress;  //地址 = reg  表示  回调金额小于 发起金额 ,  做了充值流程  然后该单作废

    @JoinColumn(name = "source_invitation_code", referencedColumnName="ucode", insertable = false, updatable = false, foreignKey = @ForeignKey( name = "none", value = ConstraintMode.NO_CONSTRAINT))
    @ManyToOne
    private User user;  //顶级账号


}
