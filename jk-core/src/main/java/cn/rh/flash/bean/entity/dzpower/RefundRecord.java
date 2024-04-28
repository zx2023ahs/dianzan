package cn.rh.flash.bean.entity.dzpower;

import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity(name = "t_dzgoods_refundrecord")
@Table(appliesTo = "t_dzgoods_refundrecord", comment = "充电宝到期退款记录")
@Data
@EntityListeners(AuditingEntityListener.class)
public class RefundRecord extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;

    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '来源邀请码'")
    private String sourceInvitationCode;

    @Column(name = "vip_type", columnDefinition = "VARCHAR(32) COMMENT '退款时vip等级'")
    private String vipType;

    @Column(name = "total_money", columnDefinition = "decimal(30,6) COMMENT '累计购买vip金额'")
    private Double totalMoney;

    @Column(name = "cancel_refund", columnDefinition = "decimal(30,6) COMMENT '退款时充电宝到期退款比'")
    private Double cancelRefund;

    @Column(name = "money", columnDefinition = "decimal(30,6) COMMENT '实际退款金额'")
    private Double money;

    @Column(name = "uid", columnDefinition = "bigint COMMENT '用户id'")
    private Long uid;
    @Column(name = "account", columnDefinition = "VARCHAR(30) COMMENT '账号'")
    private String account;

}
