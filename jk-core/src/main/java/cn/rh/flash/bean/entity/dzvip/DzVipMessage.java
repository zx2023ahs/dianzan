package cn.rh.flash.bean.entity.dzvip;

import cn.rh.flash.bean.entity.BaseEntity;
import lombok.Data;
import org.hibernate.annotations.Table;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity(name = "t_dzvip_vipmessage")
@Table(appliesTo = "t_dzvip_vipmessage", comment = "Vip信息")
@Data
@EntityListeners(AuditingEntityListener.class)
public class DzVipMessage extends BaseEntity {

    @Column(name = "idw", columnDefinition = "VARCHAR(32) COMMENT '唯一值'")
    private String idw;
    @Column(name = "source_invitation_code", columnDefinition = "VARCHAR(20) COMMENT '来源邀请码'")
    private String sourceInvitationCode;

    // 名称、ViP类型、售价、有效天数、任务次数、提现手续费、最低提现、最高提现、状态、来源邀请码
    // L1充值返佣、L2充值返佣、L3充值返佣、
    // L1任务返佣、L2任务返佣L3任务返佣、
    // L1注册返佣、L2注册返佣、L3注册返佣、

    @NotBlank(message = "请输入产品型号")
    @Column(name = "name", columnDefinition = "VARCHAR(30) COMMENT '名称'")
    private String name;
    @Column(name = "nick", columnDefinition = "VARCHAR(30) COMMENT '别名/昵称'")
    private String nick;

    @NotBlank(message = "请选择ViP类型")
    @Column(name = "vip_type", columnDefinition = "VARCHAR(30) COMMENT 'ViP类型'")
    private String vipType;
    @Column(name = "vip_img", columnDefinition = "VARCHAR(500) COMMENT 'ViP图片'")
    private String vipImg;

    @Column(name = "vip_back_ground", columnDefinition = "VARCHAR(500) COMMENT 'ViP背景'")
    private String vipBackGround;

    @Column(name = "power_bank_img", columnDefinition = "VARCHAR(500) COMMENT '充电中设备图片'")
    private String powerBankImg;

    @NotNull(message = "请输入售价")
    @Column(name = "selling_price", columnDefinition = "decimal(30,6) COMMENT '售价'")
    private Double sellingPrice;
    @NotNull(message = "请输入每日收入")
    @Column(name = "daily_income", columnDefinition = "decimal(30,6) COMMENT '每日收入'")
    private Double dailyIncome;
    @NotNull(message = "请输入充电宝数量")
    @Column(name = "number_of_tasks", columnDefinition = "int COMMENT '充电宝数量'")
    private Integer numberOfTasks;

    @Column(name = "valid_date", columnDefinition = "int COMMENT '有效天数'")
    private Integer validDate;

    @Column(name = "operate_num", columnDefinition = "int COMMENT '最大运行次数'")
    private Integer operateNum;

    @Column(name = "gear_code", columnDefinition = "VARCHAR(30) COMMENT '档次code(字典)'") // e1,a2,b4,f5,c8,d12
    private String gearCode;

    @Column(name = "cancel_refund", columnDefinition = "decimal(30,6) COMMENT '充电宝到期退款比'")
    private Double cancelRefund;

    @NotNull(message = "请输入提现手续费")
    @Column(name = "withdrawal_fee", columnDefinition = "decimal(30,6) COMMENT '提现手续费'")
    private Double withdrawalFee;
    @NotNull(message = "请输入最低提现")
    @Column(name = "minimum_withdrawal", columnDefinition = "decimal(30,6) COMMENT '最低提现'")
    private Double minimumWithdrawal;
    @NotNull(message = "请输入最高提现")
    @Column(name = "maximum_withdrawal", columnDefinition = "decimal(30,6) COMMENT '最高提现'")
    private Double maximumWithdrawal;

    @Column(name = "lang_key", columnDefinition = "VARCHAR(50) COMMENT '会员详情多语言key'")
    private String langKey;

    @NotNull(message = "请输入限制提现次数")
    @Column(name = "limit_num", columnDefinition = "int COMMENT '限制提现次数'")
    private Integer limitNum;

    @NotNull(message = "请选择状态")
    @Column(name = "dzstatus", columnDefinition = "int COMMENT '状态'")
    private Integer dzstatus;     //  1 启用 2 禁用 3 关闭

//    @NotNull(message = "请输入L1充值返佣")
//    @Column(name = "l1_recharge_rebate", columnDefinition = "decimal(30,6) COMMENT 'L1充值返佣'")
//    private Double l1RechargeRebate;
//    @NotNull(message = "请输入L2充值返佣")
//    @Column(name = "l2_recharge_rebate", columnDefinition = "decimal(30,6) COMMENT 'L2充值返佣'")
//    private Double l2RechargeRebate;
//    @NotNull(message = "请输入L3充值返佣")
//    @Column(name = "l3_recharge_rebate", columnDefinition = "decimal(30,6) COMMENT 'L3充值返佣'")
//    private Double l3RechargeRebate;

//    @NotNull(message = "请输入L1充电宝收益返佣")
//    @Column(name = "l1_task_rebate", columnDefinition = "decimal(30,6) COMMENT 'L1充电宝收益返佣'")
//    private Double l1TaskRebate;
//    @NotNull(message = "请输入L2充电宝收益返佣")
//    @Column(name = "l2_task_rebate", columnDefinition = "decimal(30,6) COMMENT 'L2充电宝收益返佣'")
//    private Double l2TaskRebate;
//    @NotNull(message = "请输入L3充电宝收益返佣")
//    @Column(name = "l3_task_rebate", columnDefinition = "decimal(30,6) COMMENT 'L3充电宝收益返佣'")
//    private Double l3TaskRebate;

    @Column(name = "l1_openvip_rebate", columnDefinition = "decimal(30,6) COMMENT 'L1开通VIP返佣'")  //
    private Double l1OpenVipRebate;
    @Column(name = "l2_openvip_rebate", columnDefinition = "decimal(30,6) COMMENT 'L2开通VIP返佣'")
    private Double l2OpenVipRebate;
    @Column(name = "l3_openvip_rebate", columnDefinition = "decimal(30,6) COMMENT 'L3开通VIP返佣'")
    private Double l3OpenVipRebate;
    @Column(name = "l4_openvip_rebate", columnDefinition = "decimal(30,6) COMMENT 'L4开通VIP返佣'")  //
    private Double l4OpenVipRebate;
    @Column(name = "l5_openvip_rebate", columnDefinition = "decimal(30,6) COMMENT 'L5开通VIP返佣'")
    private Double l5OpenVipRebate;
    @Column(name = "l6_openvip_rebate", columnDefinition = "decimal(30,6) COMMENT 'L6开通VIP返佣'")
    private Double l6OpenVipRebate;
    @Column(name = "l7_openvip_rebate", columnDefinition = "decimal(30,6) COMMENT 'L7开通VIP返佣'")
    private Double l7OpenVipRebate;
    @Column(name = "l8_openvip_rebate", columnDefinition = "decimal(30,6) COMMENT 'L8开通VIP返佣'")
    private Double l8OpenVipRebate;
    @Column(name = "l9_openvip_rebate", columnDefinition = "decimal(30,6) COMMENT 'L9开通VIP返佣'")
    private Double l9OpenVipRebate;
    @Column(name = "l10_openvip_rebate", columnDefinition = "decimal(30,6) COMMENT 'L10开通VIP返佣'")
    private Double l10OpenVipRebate;

//    @Column(name = "l1_registration_rebate", columnDefinition = "decimal(30,6) COMMENT 'L1注册返佣'")
//    private Double l1RegistrationRebate;
//    @Column(name = "l2_registration_rebate", columnDefinition = "decimal(30,6) COMMENT 'L2注册返佣'")
//    private Double l2RegistrationRebate;
//    @Column(name = "l3_registration_rebate", columnDefinition = "decimal(30,6) COMMENT 'L3注册返佣'")
//    private Double l3RegistrationRebate;

//    @Column(name = "renewal_ratio", columnDefinition = "decimal(30,6) COMMENT '续费比例'")
//    private Double renewalRatio;

    @Column(name = "withdraw_methods", columnDefinition = "VARCHAR(50) COMMENT '可提现方式（多选）'")
    private String withdrawMethods;

}
