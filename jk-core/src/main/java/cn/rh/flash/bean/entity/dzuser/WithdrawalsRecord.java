package cn.rh.flash.bean.entity.dzuser;

import cn.rh.flash.bean.entity.BaseEntity;
import cn.rh.flash.bean.entity.system.User;
import cn.rh.flash.config.chinesePattern.ChinesePattern;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity(name = "t_dzuser_withdrawals")
@Table(appliesTo = "t_dzuser_withdrawals", comment = "提现记录")
@Data
@EntityListeners(AuditingEntityListener.class)
public class WithdrawalsRecord extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;
    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '来源邀请码'")
    private String sourceInvitationCode;

    // 用户id、用户账号、订单编号、金额、通道名称、提现状态、前余额、后余额、备注、交易编号、来源邀请码
    @Column(name = "uid", columnDefinition = "bigint COMMENT '用户id'")
    private Long uid;
    @Column(name = "account", columnDefinition = "VARCHAR(30) COMMENT '账号'")
    private String account;
    @Column(name = "order_number", columnDefinition = "VARCHAR(30) COMMENT '订单编号'")
    private String orderNumber;
    @Column(name = "transaction_number", columnDefinition = "VARCHAR(30) COMMENT '交易编号'")
    private String transactionNumber;
    @Column(name = "money", columnDefinition = "decimal(30,6) COMMENT '金额'")
    private Double money;
    @Column(name = "channel_name", columnDefinition = "VARCHAR(10) COMMENT '支付通道'")
    private String channelName;
    @Column(name = "channel_type", columnDefinition = "VARCHAR(50) COMMENT '通道类型'")
    private String channelType;
    @Column(name = "recharge_status", columnDefinition = "VARCHAR(10) COMMENT '审核状态'")
    private String rechargeStatus;  //  //  审核状态 ok:已审核,no:审核中,er:已拒绝,suc:已成功,exit:已退款,sysok:订单通过不出款    回调判断在使用请勿随意修改
    @Column(name = "previous_balance", columnDefinition = "decimal(30,6) COMMENT '前余额'")
    private Double previousBalance;
    @Column(name = "after_balance", columnDefinition = "decimal(30,6) COMMENT '后余额'")
    private Double afterBalance;
    @Column(name = "remark", columnDefinition = "VARCHAR(500) COMMENT '备注'")
    @ChinesePattern(regexp = "[^\\u4E00-\\u9FA5\\uF900-\\uFA2D]+",message = "禁止输入中文",groups = {ChinesePattern.OnUpdate.class})
    private String remark;
    @Column(name = "handling_fee", columnDefinition = "decimal(30,6) COMMENT '提现手续费'")
    private Double handlingFee;
    @Column(name = "amount_received", columnDefinition = "decimal(30,6) COMMENT '到账金额'")
    private Double amountReceived;
    @Column(name = "withdrawal_address", columnDefinition = "VARCHAR(50) COMMENT '提现地址'")
    private String withdrawalAddress;

    @Column(name = "operator", columnDefinition = "VARCHAR(20) COMMENT '操作员'")
    private String operator;

    @Column(name = "up_withdrawal_address", columnDefinition = "VARCHAR(50) COMMENT '上次提现地址'")
    private String upWithdrawalAddress;

    @Column(name = "fidw", columnDefinition = "VARCHAR(32) COMMENT '造假数据唯一值'")
    private String fidw;

    @JoinColumn(name = "source_invitation_code", referencedColumnName="ucode", insertable = false, updatable = false, foreignKey = @ForeignKey( name = "none", value = ConstraintMode.NO_CONSTRAINT))
    @ManyToOne
    private User user;  //顶级账号

    @JoinColumn(name = "uid", referencedColumnName="id", insertable = false, updatable = false, foreignKey = @ForeignKey( name = "none", value = ConstraintMode.NO_CONSTRAINT))
    @ManyToOne
    private UserInfo userInfo;  // 用户自己


    @Transient
    private long withNum;
}
