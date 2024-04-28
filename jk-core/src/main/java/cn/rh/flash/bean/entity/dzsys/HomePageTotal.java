package cn.rh.flash.bean.entity.dzsys;


import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

@Entity(name = "t_dzsys_homepagetotal")
@Table(appliesTo = "t_dzsys_homepagetotal", comment = "首页统计")
@Data
@EntityListeners(AuditingEntityListener.class)
public class HomePageTotal extends BaseEntity {

    @Column(name = "day", columnDefinition = "VARCHAR(50) COMMENT '日期'")
    private String day;

    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '来源邀请码'")
    private String sourceInvitationCode;

    @Column(name = "registration_num", columnDefinition = "VARCHAR(50) COMMENT '注册人数'")
    private String registrationNum;

    @Column(name = "vip_num", columnDefinition = "VARCHAR(50) COMMENT 'VIP新增人数'")
    private String vipNum;

    @Column(name = "vip_first_num", columnDefinition = "VARCHAR(50) COMMENT '首充人数'")
    private String vipFirstNum;

    @Column(name = "c_num", columnDefinition = "VARCHAR(50) COMMENT '充值数量'")
    private String cNum;

    @Column(name = "c_money", columnDefinition = "VARCHAR(50) COMMENT '充值金额'")
    private String cMoney;

    @Column(name = "t_num", columnDefinition = "VARCHAR(50) COMMENT '提现数量'")
    private String tNum;

    @Column(name = "t_money", columnDefinition = "VARCHAR(50) COMMENT '提现金额'")
    private String tMoney;

    @Column(name = "money", columnDefinition = "VARCHAR(50) COMMENT '平台盈利'")
    private String money;

    @Column(name = "l1Vip", columnDefinition = "VARCHAR(50) COMMENT 'L1VIP返佣'")
    private String l1Vip;

    @Column(name = "l2Vip", columnDefinition = "VARCHAR(50) COMMENT 'L2VIP返佣'")
    private String l2Vip;

    @Column(name = "l3Vip", columnDefinition = "VARCHAR(50) COMMENT 'L3VIP返佣'")
    private String l3Vip;

    @Column(name = "l1Pb", columnDefinition = "VARCHAR(50) COMMENT 'L1任务返佣'")
    private String l1Pb;

    @Column(name = "l2Pb", columnDefinition = "VARCHAR(50) COMMENT 'L2任务返佣'")
    private String l2Pb;

    @Column(name = "l3Pb", columnDefinition = "VARCHAR(50) COMMENT 'L3任务返佣'")
    private String l3Pb;

    @Column(name = "total_pb", columnDefinition = "VARCHAR(50) COMMENT '发放总佣金'")
    private String totalPb;

    @Column(name = "dc_money", columnDefinition = "VARCHAR(50) COMMENT '注册奖励'")
    private String dcMoney;

    @Column(name = "pb_num", columnDefinition = "VARCHAR(50) COMMENT '完成任务数量'")
    private String pbNum;

    @Column(name = "tystart_pb", columnDefinition = "VARCHAR(50) COMMENT '体验会员启用设备'")
    private String tystartPb;

    @Column(name = "start_pb", columnDefinition = "VARCHAR(50) COMMENT '总启用设备'")
    private String startPb;
}
