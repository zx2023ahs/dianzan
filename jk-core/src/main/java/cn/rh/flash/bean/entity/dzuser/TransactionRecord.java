package cn.rh.flash.bean.entity.dzuser;

import cn.rh.flash.bean.entity.BaseEntity;
import cn.rh.flash.config.chinesePattern.ChinesePattern;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity(name = "t_dzuser_transaction")
@Table(appliesTo = "t_dzuser_transaction", comment = "交易记录")
@Data
@EntityListeners(AuditingEntityListener.class)
public class TransactionRecord extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;
    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '来源邀请码'") // 1111
    private String sourceInvitationCode;

    // 用户id、用户账号、订单编号、金额、描述、前余额、后余额、交易编号、交易类型、来源邀请码
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
    @Column(name = "previous_balance", columnDefinition = "decimal(30,6) COMMENT '前余额'")
    private Double previousBalance;
    @Column(name = "after_balance", columnDefinition = "decimal(30,6) COMMENT '后余额'")
    private Double afterBalance;

    @Column(name = "transaction_type", columnDefinition = "VARCHAR(10) COMMENT '交易类型'")
    private String transactionType; // 1:充值,2:提现,3:平台赠送,4:平台扣款,5:充电宝返佣,8:vip开通返佣,9:团队任务收益,10:购买vip,11:注册 12:中奖奖品 13:抽奖消费 14 积分兑换 20:充电宝到期退款
    @Column(name = "addition_and_subtraction", columnDefinition = "int COMMENT '加减'")
    private Integer additionAndSubtraction;

    @Column(name = "fidw", columnDefinition = "VARCHAR(32) COMMENT '造假数据唯一值'")
    private String fidw;

    @Column(name = "remark", columnDefinition = "VARCHAR(500) COMMENT '备注'")
    @ChinesePattern(regexp = "[^\\u4E00-\\u9FA5\\uF900-\\uFA2D]+",message = "禁止输入中文",groups = {ChinesePattern.OnUpdate.class})
    private String remark;


}
